package com.senchenko.soap.repository;

import com.weather.senchenko.City;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
public class CityRepository {
    private static final Map<String, City> cites = new HashMap<>();

    @PostConstruct
    public void initData(){
        City minsk = new City();
        minsk.setName("Minsk");
        minsk.setTemperature(new Random().nextInt(30));

        cites.put(minsk.getName(), minsk);

        City vitebsk = new City();
        vitebsk.setName("Vitebsk");
        vitebsk.setTemperature(new Random().nextInt(35));

        cites.put(vitebsk.getName(), vitebsk);
    }

    public City findCity(String name) {
        Assert.notNull(name, "The city's name must not be null");
        return cites.get(name);
    }
}
