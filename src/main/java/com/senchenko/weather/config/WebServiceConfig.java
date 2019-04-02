package com.senchenko.weather.config;

import com.senchenko.weather.controller.CityEndpoint;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.annotation.*;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.json.JsonToObjectTransformer;
import org.springframework.integration.transformer.ObjectToMapTransformer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {
    @Bean
    public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean(servlet, "/ws/*");
    }

    @Bean(name = "city")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema citySchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("CityPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace("http://weather.com/senchenko");
        wsdl11Definition.setSchema(citySchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema citySchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/weather.xsd"));
    }

    @MessagingGateway
    public interface Cafe {

        @Gateway(requestChannel = "weather.input")
        void placeOrder(CityEndpoint endpoint);

    }

//    @Bean
//    public MessageChannel soapInChannel(){
//        return MessageChannels.queue().get();
//    }
//
//    @Bean
//    public MessageChannel soapOutChannel(){
//        return MessageChannels.queue().get();
//    }
//
//    @Bean
//    public IntegrationFlow soapIntegrationFlows(){
//        return IntegrationFlows
//                .from("in")
//                .transform(JsonToObjectTransformer::getComponentType)
//                .transform(objectToMapTransformer())
//                .get();
//    }
//
//    @Bean
//    @Transformer()
//    JsonToObjectTransformer jsonToObjectTransformer() {
//        return new JsonToObjectTransformer();
//    }
//
//    @Bean
//    @Transformer()
//    ObjectToMapTransformer objectToMapTransformer() {
//        return new ObjectToMapTransformer();
//    }

    @Bean
    public DirectChannel inputChannel() {
        return new DirectChannel();
    }

}
