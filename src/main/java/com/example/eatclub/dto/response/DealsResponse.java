package com.example.eatclub.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

// Represents the response for the /deals endpoint
@Data
@Builder
public class DealsResponse {
    private List<RestaurantDeal> restaurantDeals;
}
