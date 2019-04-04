package com.senchenko.weather.controller;

import com.senchenko.weather.repository.CityRepository;
import com.weather.senchenko.GetCityRequest;
import com.weather.senchenko.GetCityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;


@IntegrationComponentScan
@Endpoint
public class CityEndpoint {
    private static final String NAMESPACE_URI = "http://weather.com/senchenko";

    private CityRepository cityRepository;

    @Autowired
    public CityEndpoint(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getCityRequest")
    @ResponsePayload
    public GetCityResponse getCityResponse(@RequestPayload GetCityRequest request){
        GetCityResponse response = new GetCityResponse();
        response.setCity(cityRepository.findCity(request.getName()));
        return response;
    }
}
