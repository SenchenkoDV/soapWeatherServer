package com.senchenko.integration.config;

import com.senchenko.integration.service.SoapEnvelopService;
import com.weather.senchenko.GetCityRequest;
import org.json.XML;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.http.dsl.Http;

import java.util.HashMap;
import java.util.Map;

@IntegrationComponentScan
@Configuration
public class IntegrationConfig {
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
