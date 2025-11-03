package com.mln.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricsDto {
    private String avgTimeOnSite;
    private String bounceRate;
    private GrowthMetricsDto growthMetrics;
}

