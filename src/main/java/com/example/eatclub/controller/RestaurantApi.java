package com.example.eatclub.controller;

import com.example.eatclub.dto.response.DealsResponse;
import com.example.eatclub.dto.response.PeakResponse;
import com.example.eatclub.service.RestaurantService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restaurant")
@AllArgsConstructor
public class RestaurantApi {

    @Autowired
    private final RestaurantService restaurantService;

    @GetMapping("/deals")
    public DealsResponse getRestaurantDeals(@RequestParam String timeOfDay) {
        return restaurantService.getDeals(timeOfDay);
    }

    @GetMapping("/peak")
    public PeakResponse getPeakTimeWindow() {
        return restaurantService.getPeak();
    }
}
