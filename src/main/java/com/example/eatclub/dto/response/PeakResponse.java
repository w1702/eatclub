package com.example.eatclub.dto.response;

import lombok.Builder;
import lombok.Data;

// Represents the response for the /peakTime endpoint
@Data
@Builder
public class PeakResponse {
    private String peakTimeStart;
    private String peakTimeEnd;
}
