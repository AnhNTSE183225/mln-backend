package com.mln.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrowthMetricsDto {
    private String visitorsGrowth;
    private String pageViewsGrowth;
    private String timeGrowth;
    private String bounceRateChange;
}

