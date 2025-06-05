package com.example.eatclub.service;

import com.example.eatclub.dto.RestaurantWrapper;
import com.example.eatclub.dto.response.PeakResponse;
import com.example.eatclub.dto.response.RestaurantDeal;
import com.example.eatclub.dto.response.DealsResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class RestaurantService {
    public static String URL = "https://eccdn.com.au/misc/challengedata.json";

    @Autowired
    private final RestTemplate restTemplate;

    public DealsResponse getDeals(String timeOfDay){
        RestaurantWrapper restaurants = getRestaurants();
        return filterDealsByTimeOfDay(restaurants, timeOfDay);
    }

    public PeakResponse getPeak(){
        RestaurantWrapper restaurants = getRestaurants();
        return PeakResponse.builder().build();
    }

    private RestaurantWrapper getRestaurants(){
        return restTemplate.getForObject(URL, RestaurantWrapper.class);
    }

    // Assumption 1
    // Take into account the restaurant open and close times when calculating deal times
    // e.g. when a restaurant is opened 6:00pm to 9:00pm, but it has a deal from 3:00pm to 9:00pm, then the deal is considered valid from 6:00pm to 9:00pm

    // Assumption 2
    // If a deal with no open-close or start-end time, it is considered active as long as the restaurant is opened at that time
    // e.g a restaurant is opened 3:00pm to 9:00pm but it's deal has no start-end or open-close date then the deal is considered valid from 3:00pm to 9:00pm

    // Assumption 3
    // If a deal ends at a certain time, it is not inclusive of that time
    // e.g. If a deal ends at 9:00pm but the timeOfDay supplied is 9:00pm, it is considered invalid

    // Assumption 4
    // If a deal has a qtyLeft it means it is still active

    // Assumption 5
    // Lightning deals with no open-close or start-end times are valid throughout the restaurant opening hours
    private DealsResponse filterDealsByTimeOfDay(RestaurantWrapper wrapper, String timeOfDay) {
        DealsResponse.DealsResponseBuilder result = DealsResponse.builder();
        if (wrapper == null || wrapper.getRestaurants() == null) {
            return result.build();
        }
        int timeOfDayMinutes = convertToMinutes(timeOfDay);
        List<RestaurantDeal> restaurantDeals = wrapper.getRestaurants().stream()
                .filter(r -> r.getDeals() != null && r.getOpen() != null && r.getClose() != null)
                .flatMap(r -> {
                    int restaurantOpen = convertToMinutes(r.getOpen());
                    int restaurantClose = convertToMinutes(r.getClose());

                    if (!isTimeInRange(timeOfDayMinutes, restaurantOpen, restaurantClose)) {
                        return Stream.empty();
                    }

                    return r.getDeals().stream()
                            .filter(deal -> {
                                String startStr = deal.getStart() != null ? deal.getStart() : deal.getOpen();
                                String endStr = deal.getEnd() != null ? deal.getEnd() : deal.getClose();

                                // Assumption 2: use restaurant hours when no hours on deal
                                int dealStart = startStr != null ? convertToMinutes(startStr) : restaurantOpen;
                                int dealEnd = endStr != null ? convertToMinutes(endStr) : restaurantClose;

                                // Assumption 1: Restaurant hours are the source of truth over deal hours
                                int effectiveStart = Math.max(dealStart, restaurantOpen);
                                int effectiveEnd = Math.min(dealEnd, restaurantClose);

                                return isTimeInRange(timeOfDayMinutes, effectiveStart, effectiveEnd);
                            })
                            .map(deal -> RestaurantDeal.builder()
                                    .restaurantObjectId(r.getObjectId())
                                    .restaurantName(r.getName())
                                    .restaurantAddress1(r.getAddress1())
                                    .restaurantSuburb(r.getSuburb())
                                    .restaurantOpen(r.getOpen())
                                    .restaurantClose(r.getClose())
                                    .dealObjectId(deal.getObjectId())
                                    .discount(deal.getDiscount())
                                    .dineIn(deal.getDineIn())
                                    .lightning(deal.getLightning())
                                    .qtyLeft(deal.getQtyLeft())
                                    .build()
                            );
                })
                .collect(Collectors.toList());

        return result.restaurantDeals(restaurantDeals).build();
    }

    // Calculate the number of minutes past midnight
    private int convertToMinutes(String timeStr) {
        timeStr = timeStr.trim().toLowerCase();
        boolean isPM = timeStr.endsWith("pm");
        timeStr = timeStr.replaceAll("[ap]m", "").trim();
        String[] parts = timeStr.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        if (hour == 12){
            hour = 0;
        }
        if (isPM){
            hour += 12;
        }
        return hour * 60 + minute;
    }

    private boolean isTimeInRange(int target, int start, int end) {
        return target >= start && target < end;
    }
}
