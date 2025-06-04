package com.example.eatclub.service;

import com.example.eatclub.response.DealsResponse;
import com.example.eatclub.response.PeakResponse;
import com.example.eatclub.dto.Restaurant;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@AllArgsConstructor
public class RestaurantService {
    public static String URL = "https://eccdn.com.au/misc/challengedata.json";

    @Autowired
    private final RestTemplate restTemplate;

    public DealsResponse getDeals(String timeOfDay){
        List<Restaurant> restaurants = getRestaurants();
        return new DealsResponse();
    }

    public PeakResponse getPeak(){
        List<Restaurant> restaurants = getRestaurants();
        return new PeakResponse();
    }

    private List<Restaurant> getRestaurants(){
        ResponseEntity<List<Restaurant>> response = restTemplate.exchange(URL, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });
        return response.getBody();
    }
}
