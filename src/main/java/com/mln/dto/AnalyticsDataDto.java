package com.mln.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDataDto {
    private List<DailyVisitorDto> dailyVisitors;
    private List<PageVisitDto> pageVisits;
    private List<HourlyTrafficDto> hourlyTraffic;
    private MetricsDto metrics;
    private AdditionalStatsDto additionalStats;
}

