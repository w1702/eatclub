package com.example.eatclub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {
    private String objectId;
    private String name;
    private String address1;
    private String suburb;
    private List<String> cuisines;
    private String imageLink;
    private String open;
    private String close;
    private List<Deal> deals;
}
