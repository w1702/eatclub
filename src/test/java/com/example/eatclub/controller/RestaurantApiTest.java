package com.example.eatclub.controller;

import com.example.eatclub.dto.response.DealsResponse;
import com.example.eatclub.dto.response.PeakResponse;
import com.example.eatclub.service.RestaurantService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantApiTest {
    @Mock
    private RestaurantService restaurantService;

    @Test
    @DisplayName("When /deals request is received then expect RestaurantService to be called")
    void getRestaurantDealsTest(){
        when(restaurantService.getDeals(anyString())).thenReturn(DealsResponse.builder().build());
        new RestaurantApi(restaurantService).getRestaurantDeals("3:00pm");
        verify(restaurantService, times(1)).getDeals(anyString());
    }

    @Test
    @DisplayName("When /peakTime request is received then expect RestaurantService to be called")
    void getPeakTimeTest(){
        when(restaurantService.getPeak()).thenReturn(PeakResponse.builder().build());
        new RestaurantApi(restaurantService).getPeakTime();
        verify(restaurantService, times(1)).getPeak();
    }
}