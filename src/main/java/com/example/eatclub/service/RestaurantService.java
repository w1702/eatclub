package com.example.eatclub.service;

import com.example.eatclub.dto.response.DealsResponse;
import com.example.eatclub.dto.response.PeakResponse;
import com.example.eatclub.dto.RestaurantWrapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class RestaurantService {
    public static String URL = "https://eccdn.com.au/misc/challengedata.json";

    @Autowired
    private final RestTemplate restTemplate;

    public DealsResponse getDeals(String timeOfDay){
        RestaurantWrapper restaurants = getRestaurants();
        return new DealsResponse();
    }

    public PeakResponse getPeak(){
        RestaurantWrapper restaurants = getRestaurants();
        return new PeakResponse();
    }

    private RestaurantWrapper getRestaurants(){
        return restTemplate.getForObject(URL, RestaurantWrapper.class);
    }
}
