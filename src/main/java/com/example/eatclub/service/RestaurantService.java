package com.example.eatclub.service;

import com.example.eatclub.dto.RestaurantWrapper;
import com.example.eatclub.dto.response.PeakResponse;
import com.example.eatclub.dto.response.RestaurantDeal;
import com.example.eatclub.dto.response.DealsResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.eatclub.util.TimeUtils.*;

@Service
@AllArgsConstructor
public class RestaurantService {
    private static final String URL = "https://eccdn.com.au/misc/challengedata.json";

    @Autowired
    private final RestTemplate restTemplate;

    public DealsResponse getDeals(String timeOfDay){
        RestaurantWrapper restaurants = getRestaurants();
        return filterDealsByTimeOfDay(restaurants, timeOfDay);
    }

    public PeakResponse getPeak(){
        RestaurantWrapper restaurants = getRestaurants();
        return filterDealsByPeakTime(restaurants);
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
    // The lightning boolean value is not taken into account, not sure how it would affect the formula in a real use case
    // Deals with no open-close or start-end times are valid throughout the restaurant opening hours
    private DealsResponse filterDealsByTimeOfDay(RestaurantWrapper restaurantWrapper, String timeOfDay) {
        DealsResponse.DealsResponseBuilder result = DealsResponse.builder();
        if (restaurantWrapper == null || CollectionUtils.isEmpty(restaurantWrapper.getRestaurants()) ) {
            return result.restaurantDeals(new ArrayList<>()).build();
        }
        int timeOfDayMinutes = convertToMinutes(timeOfDay);
        List<RestaurantDeal> restaurantDeals = restaurantWrapper.getRestaurants().stream()
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

    // Assumption 1
    // When calculating the peak times we are referring to a window of time
    // It is unspecified in the spec, I assume up to 3 hrs as that is where the bulk of the deals sit

    // Assumption 2
    // We are counting the qtyLeft on the deals rather than the deal array size

    // Assumption 3
    // The lightning value is not taken into account, not sure how it would affect the formula in a real use case
    private PeakResponse filterDealsByPeakTime(RestaurantWrapper restaurantWrapper) {
        PeakResponse.PeakResponseBuilder result = PeakResponse.builder();
        if (restaurantWrapper == null || CollectionUtils.isEmpty(restaurantWrapper.getRestaurants())) {
            return result.peakTimeStart("").peakTimeEnd("").build();
        }

        // 1440 minutes in a day
        // we COULD bump up to larger search intervals for more efficiency at the cost of granularity
        int dayMinutes = 1440;
        int[] dealCounts = new int[dayMinutes];

        restaurantWrapper.getRestaurants().stream()
                .filter(r -> r.getDeals() != null && r.getOpen() != null && r.getClose() != null)
                .forEach(r -> {
                    int restaurantOpen = convertToMinutes(r.getOpen());
                    int restaurantClose = convertToMinutes(r.getClose());

                    r.getDeals().forEach(deal -> {
                        String startStr = deal.getStart() != null ? deal.getStart() : deal.getOpen();
                        String endStr = deal.getEnd() != null ? deal.getEnd() : deal.getClose();

                        int dealStart = startStr != null ? convertToMinutes(startStr) : restaurantOpen;
                        int dealEnd = endStr != null ? convertToMinutes(endStr) : restaurantClose;

                        int effectiveStart = Math.max(dealStart, restaurantOpen);
                        int effectiveEnd = Math.min(dealEnd, restaurantClose);

                        if (effectiveEnd <= effectiveStart){
                            return;
                        }

                        int qty = 0;
                        try {
                            qty = Integer.parseInt(deal.getQtyLeft());
                        } catch (NumberFormatException e) {
                            return;
                        }

                        for (int i = effectiveStart; i < effectiveEnd; i++) {
                            dealCounts[i] += qty;
                        }
                    });
                });

        int peakWindowStart = 0;
        int peakWindowEnd = 0;
        // max deal quantity ever recorded in a window
        int maxDealQty = 0;
        // first check 60, then 120, then 180min windows
        for (int windowSize = 60; windowSize <= 180; windowSize += 60) {
            // move the window through the day
            for (int i = 0; i <= dayMinutes - windowSize; i++) {
                int sum = 0;
                for (int j = 0; j < windowSize; j++) {
                    sum += dealCounts[i + j];
                }
                if (sum > maxDealQty) {
                    maxDealQty = sum;
                    peakWindowStart = i;
                    peakWindowEnd = i + windowSize;
                }
            }
        }

        return result
                .peakTimeStart(formatMinutesToTime(peakWindowStart))
                .peakTimeEnd(formatMinutesToTime(peakWindowEnd))
                .build();
    }
}
