package com.senchenko.weather.config;

import com.senchenko.weather.service.SoapEnvelopService;
import com.weather.senchenko.GetCityRequest;
import org.json.XML;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.http.dsl.Http;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import java.util.HashMap;
import java.util.Map;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

    @Bean
    public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean(servlet, "/temperature/*");
    }

    @Bean(name = "city")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema citySchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("CityPort");
        wsdl11Definition.setLocationUri("/temperature");
        wsdl11Definition.setTargetNamespace("http://weather.com/temperature");
        wsdl11Definition.setSchema(citySchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema citySchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/weather.xsd"));
    }

    @Bean
    public DirectChannel inputChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow httpWeatherProxy() {
        return IntegrationFlows
                .from(Http.inboundGateway("/integration/temperature")
                        .requestPayloadType(Map.class)
                )
                .transform(t -> new HashMap((Map) t).get("name"))
                .transform(t -> String.format(new SoapEnvelopService().createEnvelop(t.toString(), GetCityRequest.class)))
                .enrichHeaders(h -> h.header("Content-Type", "text/xml; charset=utf-8"))
                .handle(Http.outboundGateway("http://localhost:8080/temperature")
                        .expectedResponseType(String.class))
                .transform(t -> XML.toJSONObject(t.toString()).getJSONObject("SOAP-ENV:Envelope").getJSONObject("SOAP-ENV:Body").toString())
                .enrichHeaders(h -> h.header("Content-Type", "application/json; charset=utf-8"))
                .get();
    }
}
