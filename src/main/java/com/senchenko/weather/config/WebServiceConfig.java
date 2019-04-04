package com.senchenko.weather.config;

import com.weather.senchenko.GetCityRequest;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Conventions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.json.JsonToObjectTransformer;
import org.springframework.integration.json.ObjectToJsonTransformer;
import org.springframework.integration.transformer.MapToObjectTransformer;
import org.springframework.integration.transformer.ObjectToMapTransformer;
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

    private static final String TEST_ENVOLOPE = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "                                     xmlns:gs=\"http://weather.com/senchenko\">\n" +
            "<soapenv:Header/>\n" +
            "<soapenv:Body>\n" +
            "    <gs:getCityRequest>\n" +
            "        <gs:name>%s</gs:name>\n" +
            "    </gs:getCityRequest>\n" +
            "</soapenv:Body>\n" +
            "</soapenv:Envelope>";

    private static final String TEST_TESPONSE = "{\"test\":\"tt\"}";

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

//    @MessagingGateway
//    public interface Cafe {
//
//        @Gateway(requestChannel = "weather.input")
//        void placeOrder(CityEndpoint endpoint);
//
//    }

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

    @Bean
    public IntegrationFlow httpInboundGatewayFlow() {
        return IntegrationFlows.from(Http.inboundGateway("/hello/{country}")
                .requestMapping(r -> r
                        .methods(HttpMethod.GET)
                        .params("msg"))
                .headerExpression("country", "#pathVariables.country")
                .payloadExpression("#requestParams.msg[0]"))
                .handle((payload, headers) ->
                        "de".equals(headers.get("country"))
                                ? "Hallo " + payload
                                : "Hello " + payload)
                .get();
    }

//    @Bean
//    public IntegrationFlow upperCaseFlow() {
//        return IntegrationFlows
//                .from(
//                        Http.inboundGateway("/conversions/upperCase")
//                                .requestMapping(r -> r.methods(HttpMethod.POST).consumes("application/json"))
//                                .headerExpression("content-type", "text/xtml")
//                                .requestPayloadType(GetCityRequest.class)
//                )
//                .handle(Http.outboundGateway("http://localhost/ws/")
//                )
////                .handle(payload -> System.out.println(payload))
//                .get();
//    }

//    @Bean
//    public IntegrationFlow sendMailFlow() {
//        return IntegrationFlows.from("sendMailChannel")
//                .handle(Wss4jSecurityInterceptor
//                        Wsdl.outboundAdapter("localhost")
//                                .port(smtpPort)
//                                .credentials("user", "pw")
//                                .protocol("smtp")
//                                .javaMailProperties(p -> p.put("mail.debug", "true")),
//                        e -> e.id("sendMailEndpoint"))
//                .get();
//    }


    @Bean
    public IntegrationFlow httpProxyFlow() {
        return IntegrationFlows
                .from(Http.inboundGateway("/service")
                        .requestPayloadType(Map.class)
                )
   //             .transform(new JsonToObjectTransformer(Map.class))
                .transform(t -> new HashMap((Map) t).get("name"))
             //   .transform(t -> XML.toString(new JSONObject(t)))
                //.transform(t -> String.format(TEST_ENVOLOPE, new JSONObject(t.).get("payload")))
               // .handle(t -> System.out.println(t.getPayload()))
//                .transform(JsonToObjectTransformer::getComponentType)
//                .transform(ObjectToMapTransformer::getComponentType)
//                .channel("inputChannel")
//                .handle(t -> System.out.println(t.getPayload()))

                .transform(t -> String.format(TEST_ENVOLOPE, t))
                .enrichHeaders(h -> h.header("Content-Type", "text/xml; charset=utf-8"))
                .handle(Http.outboundGateway("http://localhost:8080/ws")
                        .expectedResponseType(String.class))
                .enrichHeaders(h -> h.header("Content-Type", "application/json; charset=utf-8"))
        //        .enrich(t -> t.requestPayload(g -> new JSONObject(g.getPayload())))
                .get();
    }

    @Bean
    public DirectChannel directChannel() {
        return new DirectChannel();
    }



//    @Bean
//    public IntegrationFlow httpProxyFlow(MessageChannel directChannel) {
//        return IntegrationFlows
//                .from(Http.inboundGateway("/service").replyChannel(directChannel).requestChannel(directChannel))
//    //            .enrichHeaders(h -> h.header("Content-Type", "application/xml; charset=utf-8"))
//                .handle(Http.outboundGateway("http:/localhost/ws").expectedResponseType(String.class).get())
//      //          .handle(t -> System.out.println(t))
//                .channel(directChannel)
//                .get();
//    }

}
