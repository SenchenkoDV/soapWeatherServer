package com.senchenko.soap.controller;


import com.senchenko.soap.repository.CityRepository;
import com.weather.senchenko.GetCityRequest;
import com.weather.senchenko.GetCityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class CityEndpoint {

    @Value("@{namespace.uri}")
    private static final String NAMESPACE_URI = "http://weather.com/senchenko";

    private CityRepository cityRepository;

    @Autowired
    public CityEndpoint(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getCityRequest")
    @ResponsePayload
    public GetCityResponse getCityResponse(@RequestPayload GetCityRequest request) {
        GetCityResponse response = new GetCityResponse();
        response.setCity(cityRepository.findCity(request.getName()));
        return response;
    }
}
