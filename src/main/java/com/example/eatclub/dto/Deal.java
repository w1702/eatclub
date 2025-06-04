package com.example.eatclub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Deal {
    private String objectId;
    private String discount;
    private String dineIn;
    private String lightning;
    private String open;
    private String close;
    private String qtyLeft;
}
