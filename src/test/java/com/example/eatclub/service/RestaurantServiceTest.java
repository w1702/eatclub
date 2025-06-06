package com.example.eatclub.service;

import com.example.eatclub.dto.RestaurantWrapper;
import com.example.eatclub.dto.response.DealsResponse;
import com.example.eatclub.dto.response.PeakResponse;
import com.example.eatclub.dto.response.RestaurantDeal;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {
    @Mock
    private RestTemplate restTemplate;

    @Test
    @DisplayName("When searching deals by timeOfDay then return filtered deals")
    void getDealsTest(){
        when(restTemplate.getForObject(anyString(), eq(RestaurantWrapper.class))).thenReturn(mockRestaurantData());
        DealsResponse result = new RestaurantService(restTemplate).getDeals("11:00am");
        List<RestaurantDeal> deals = result.getRestaurantDeals();
        assertEquals(2, deals.size());
        assertEquals("178CC02C-69F5-5D90-FF67-84B135B19103", deals.get(0).getRestaurantObjectId());
        assertEquals("OzzyThai Cafe Bar ", deals.get(0).getRestaurantName());
        assertEquals("34 Saint Kilda Road", deals.get(0).getRestaurantAddress1());
        assertEquals("Saint Kilda", deals.get(0).getRestaurantSuburb());
        assertEquals("8:00am", deals.get(0).getRestaurantOpen());
        assertEquals("3:00pm", deals.get(0).getRestaurantClose());
        assertEquals("B5913CD0-0550-40C7-AFC3-7D46D26B01BF", deals.get(0).getDealObjectId());
        assertEquals("30", deals.get(0).getDiscount());
        assertEquals("false", deals.get(0).getDineIn());
        assertEquals("false", deals.get(0).getLightning());
        assertEquals("8", deals.get(0).getQtyLeft());

        assertEquals("178CC02C-69F5-5D90-FF67-84B135B19103", deals.get(1).getRestaurantObjectId());
        assertEquals("OzzyThai Cafe Bar ", deals.get(1).getRestaurantName());
        assertEquals("34 Saint Kilda Road", deals.get(1).getRestaurantAddress1());
        assertEquals("Saint Kilda", deals.get(1).getRestaurantSuburb());
        assertEquals("8:00am", deals.get(1).getRestaurantOpen());
        assertEquals("3:00pm", deals.get(1).getRestaurantClose());
        assertEquals("B5713CD0-1361-40C7-AFC3-7D46D26B00BF", deals.get(1).getDealObjectId());
        assertEquals("25", deals.get(1).getDiscount());
        assertEquals("false", deals.get(1).getDineIn());
        assertEquals("false", deals.get(1).getLightning());
        assertEquals("7", deals.get(1).getQtyLeft());
    }

    @Test
    @DisplayName("When searching deals and chained request returns empty then return response with empty deals")
    void getDealsChainedRequestNoResultsTest(){
        RestaurantWrapper chainedResponse = RestaurantWrapper.builder().restaurants(new ArrayList<>()).build();
        when(restTemplate.getForObject(anyString(), eq(RestaurantWrapper.class))).thenReturn(chainedResponse);
        DealsResponse result = new RestaurantService(restTemplate).getDeals("11:00am");
        List<RestaurantDeal> deals = result.getRestaurantDeals();
        assertEquals(0, deals.size());
    }

    @Test
    @DisplayName("When searching peak times then return peak window with most deals")
    void getPeakTest(){
        when(restTemplate.getForObject(anyString(), eq(RestaurantWrapper.class))).thenReturn(mockRestaurantData());
        PeakResponse result = new RestaurantService(restTemplate).getPeak();
        assertEquals("6:00pm", result.getPeakTimeStart());
        assertEquals("9:00pm", result.getPeakTimeEnd());
    }

    @Test
    @DisplayName("When searching peak times and chained request returns empty then return response with empty peak times")
    void getPeakChainedRequestNoResultsTest(){
        RestaurantWrapper chainedResponse = RestaurantWrapper.builder().restaurants(new ArrayList<>()).build();
        when(restTemplate.getForObject(anyString(), eq(RestaurantWrapper.class))).thenReturn(chainedResponse);
        PeakResponse result = new RestaurantService(restTemplate).getPeak();
        assertEquals("", result.getPeakTimeStart());
        assertEquals("", result.getPeakTimeEnd());
    }

    private RestaurantWrapper mockRestaurantData() {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getResourceAsStream("/data.json");
        RestaurantWrapper restaurantWrapper = null;
        try {
            restaurantWrapper = mapper.readValue(is, RestaurantWrapper.class);
        } catch (IOException e) {
            fail("Failed to read sample data");
        }
        return restaurantWrapper;
    }
}