package com.example.eatclub.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Deal {
    private String objectId;
    private String discount;
    private String dineIn;
    private String lightning;
    // Looks like some restaurant deals use open/close and others use start/end
    private String open;
    private String close;
    private String start;
    private String end;
    private String qtyLeft;
}
