package com.senchenko.weather.repository;

import com.weather.senchenko.City;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
public class CityRepository {
    private static final Map<String, City> citys = new HashMap<>();

    @PostConstruct
    public void initData(){
        City minsk = new City();
        minsk.setName("Minsk");
        minsk.setTemperature(new Random().nextInt(30));

        citys.put(minsk.getName(), minsk);

        City vitebsk = new City();
        vitebsk.setName("Vitebsk");
        vitebsk.setTemperature(new Random().nextInt(35));

        citys.put(vitebsk.getName(), vitebsk);
    }

    public City findCity(String name) {
        Assert.notNull(name, "The country's name must not be null");
        return citys.get(name);
    }
}
