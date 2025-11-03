package com.mln.service;

import com.mln.dto.*;
import com.mln.entity.*;
import com.mln.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    
    private final DailyVisitorRepository dailyVisitorRepository;
    private final PageVisitRepository pageVisitRepository;
    private final HourlyTrafficRepository hourlyTrafficRepository;
    private final AnalyticsMetricsRepository metricsRepository;
    private final AdditionalStatsRepository additionalStatsRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:00");
    
    // Rate limiting cache
    private final Map<String, Map<String, LocalDateTime>> rateLimitCache = new HashMap<>();
    private static final int RATE_LIMIT_SECONDS = 30;
    
    public AnalyticsDataDto getAllAnalytics() {
        List<DailyVisitorDto> dailyVisitors = dailyVisitorRepository.findAllOrderByDateDesc()
                .stream()
                .map(dv -> new DailyVisitorDto(dv.getDate(), dv.getVisitors(), dv.getPageViews()))
                .collect(Collectors.toList());
        
        List<PageVisitDto> pageVisits = pageVisitRepository.findAll()
                .stream()
                .map(pv -> new PageVisitDto(pv.getPage(), pv.getVisits()))
                .collect(Collectors.toList());
        
        List<HourlyTrafficDto> hourlyTraffic = hourlyTrafficRepository.findAllOrderByHourAsc()
                .stream()
                .map(ht -> new HourlyTrafficDto(ht.getHour(), ht.getUsers()))
                .collect(Collectors.toList());
        
        MetricsDto metrics = getMetrics();
        AdditionalStatsDto additionalStats = getAdditionalStats();
        
        return new AnalyticsDataDto(dailyVisitors, pageVisits, hourlyTraffic, metrics, additionalStats);
    }
    
    public List<DailyVisitorDto> getDailyVisitors() {
        return dailyVisitorRepository.findAllOrderByDateDesc()
                .stream()
                .map(dv -> new DailyVisitorDto(dv.getDate(), dv.getVisitors(), dv.getPageViews()))
                .collect(Collectors.toList());
    }
    
    public List<PageVisitDto> getPageVisits() {
        return pageVisitRepository.findAll()
                .stream()
                .map(pv -> new PageVisitDto(pv.getPage(), pv.getVisits()))
                .collect(Collectors.toList());
    }
    
    public List<HourlyTrafficDto> getHourlyTraffic() {
        return hourlyTrafficRepository.findAllOrderByHourAsc()
                .stream()
                .map(ht -> new HourlyTrafficDto(ht.getHour(), ht.getUsers()))
                .collect(Collectors.toList());
    }
    
    public MetricsDto getMetrics() {
        AnalyticsMetrics metrics = metricsRepository.findByKey("default")
                .orElseGet(() -> {
                    AnalyticsMetrics defaultMetrics = new AnalyticsMetrics();
                    defaultMetrics.setKey("default");
                    defaultMetrics.setAvgTimeOnSite("0:00");
                    defaultMetrics.setBounceRate("0%");
                    defaultMetrics.setVisitorsGrowth("+0%");
                    defaultMetrics.setPageViewsGrowth("+0%");
                    defaultMetrics.setTimeGrowth("+0:00");
                    defaultMetrics.setBounceRateChange("0%");
                    return metricsRepository.save(defaultMetrics);
                });
        
        GrowthMetricsDto growthMetrics = new GrowthMetricsDto(
                metrics.getVisitorsGrowth(),
                metrics.getPageViewsGrowth(),
                metrics.getTimeGrowth(),
                metrics.getBounceRateChange()
        );
        
        return new MetricsDto(metrics.getAvgTimeOnSite(), metrics.getBounceRate(), growthMetrics);
    }
    
    public AdditionalStatsDto getAdditionalStats() {
        Map<String, DeviceStatsDto> devices = new HashMap<>();
        Map<String, UserStatsDto> users = new HashMap<>();
        Map<String, SourceStatsDto> sources = new HashMap<>();
        
        List<AdditionalStats> stats = additionalStatsRepository.findAll();
        for (AdditionalStats stat : stats) {
            switch (stat.getCategory()) {
                case "devices":
                    if (devices.isEmpty()) {
                        devices.put("mobile", new DeviceStatsDto("0%", "0%", "0%"));
                    }
                    break;
                case "users":
                    if (users.isEmpty()) {
                        users.put("default", new UserStatsDto(0, 0, "0%"));
                    }
                    break;
                case "sources":
                    if (sources.isEmpty()) {
                        sources.put("default", new SourceStatsDto("0%", "0%", "0%"));
                    }
                    break;
            }
        }
        
        // Build from database or use defaults
        DeviceStatsDto deviceStats = buildDeviceStats();
        UserStatsDto userStats = buildUserStats();
        SourceStatsDto sourceStats = buildSourceStats();
        
        return new AdditionalStatsDto(deviceStats, userStats, sourceStats);
    }
    
    private DeviceStatsDto buildDeviceStats() {
        List<AdditionalStats> stats = additionalStatsRepository.findByCategory("devices");
        Map<String, String> deviceMap = new HashMap<>();
        deviceMap.put("mobile", "0%");
        deviceMap.put("desktop", "0%");
        deviceMap.put("tablet", "0%");
        
        for (AdditionalStats stat : stats) {
            deviceMap.put(stat.getKeyName(), stat.getValue());
        }
        
        return new DeviceStatsDto(deviceMap.get("mobile"), deviceMap.get("desktop"), deviceMap.get("tablet"));
    }
    
    private UserStatsDto buildUserStats() {
        List<AdditionalStats> stats = additionalStatsRepository.findByCategory("users");
        Map<String, String> userMap = new HashMap<>();
        userMap.put("newUsers", "0");
        userMap.put("returningUsers", "0");
        userMap.put("returnRate", "0%");
        
        for (AdditionalStats stat : stats) {
            userMap.put(stat.getKeyName(), stat.getValue());
        }
        
        return new UserStatsDto(
                Integer.parseInt(userMap.getOrDefault("newUsers", "0")),
                Integer.parseInt(userMap.getOrDefault("returningUsers", "0")),
                userMap.getOrDefault("returnRate", "0%")
        );
    }
    
    private SourceStatsDto buildSourceStats() {
        List<AdditionalStats> stats = additionalStatsRepository.findByCategory("sources");
        Map<String, String> sourceMap = new HashMap<>();
        sourceMap.put("search", "0%");
        sourceMap.put("direct", "0%");
        sourceMap.put("social", "0%");
        
        for (AdditionalStats stat : stats) {
            sourceMap.put(stat.getKeyName(), stat.getValue());
        }
        
        return new SourceStatsDto(sourceMap.get("search"), sourceMap.get("direct"), sourceMap.get("social"));
    }
    
    @Transactional
    public Map<String, Object> trackVisit(String page, String clientIp) {
        LocalDateTime now = LocalDateTime.now();
        
        // Rate limiting check
        if (isRateLimited(clientIp, page, now)) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("page", page);
            response.put("skipped", "rate_limited");
            return response;
        }
        
        // Update rate limit cache
        rateLimitCache.computeIfAbsent(clientIp, k -> new HashMap<>()).put(page, now);
        cleanupRateLimitCache(now);
        
        // Get current date and hour
        String currentDate = now.format(DATE_FORMATTER);
        String currentHour = now.format(HOUR_FORMATTER);
        String dateKey = currentDate;
        
        // Update page visits
        PageVisit pageVisit = pageVisitRepository.findByPage(page)
                .orElseGet(() -> {
                    PageVisit newPageVisit = new PageVisit();
                    newPageVisit.setPage(page);
                    newPageVisit.setVisits(0);
                    return pageVisitRepository.save(newPageVisit);
                });
        pageVisit.setVisits(pageVisit.getVisits() + 1);
        pageVisitRepository.save(pageVisit);
        
        // Update daily visitors
        DailyVisitor dailyVisitor = dailyVisitorRepository.findByDateKey(dateKey)
                .orElseGet(() -> {
                    DailyVisitor newDailyVisitor = new DailyVisitor();
                    newDailyVisitor.setDate(currentDate);
                    newDailyVisitor.setDateKey(dateKey);
                    newDailyVisitor.setVisitors(0);
                    newDailyVisitor.setPageViews(0);
                    return dailyVisitorRepository.save(newDailyVisitor);
                });
        dailyVisitor.setVisitors(dailyVisitor.getVisitors() + 1);
        dailyVisitor.setPageViews(dailyVisitor.getPageViews() + 1);
        dailyVisitorRepository.save(dailyVisitor);
        
        // Keep only last 30 days
        List<DailyVisitor> allDailyVisitors = dailyVisitorRepository.findAllOrderByDateDesc();
        if (allDailyVisitors.size() > 30) {
            List<DailyVisitor> toDelete = allDailyVisitors.subList(30, allDailyVisitors.size());
            dailyVisitorRepository.deleteAll(toDelete);
        }
        
        // Update hourly traffic
        HourlyTraffic hourlyTraffic = hourlyTrafficRepository.findByHour(currentHour)
                .orElseGet(() -> {
                    HourlyTraffic newHourlyTraffic = new HourlyTraffic();
                    newHourlyTraffic.setHour(currentHour);
                    newHourlyTraffic.setUsers(0);
                    return hourlyTrafficRepository.save(newHourlyTraffic);
                });
        hourlyTraffic.setUsers(hourlyTraffic.getUsers() + 1);
        hourlyTrafficRepository.save(hourlyTraffic);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("page", page);
        return response;
    }
    
    private boolean isRateLimited(String clientIp, String page, LocalDateTime now) {
        Map<String, LocalDateTime> ipCache = rateLimitCache.get(clientIp);
        if (ipCache != null) {
            LocalDateTime lastTracked = ipCache.get(page);
            if (lastTracked != null) {
                long secondsSince = java.time.Duration.between(lastTracked, now).getSeconds();
                return secondsSince < RATE_LIMIT_SECONDS;
            }
        }
        return false;
    }
    
    private void cleanupRateLimitCache(LocalDateTime now) {
        if (rateLimitCache.size() > 1000) {
            LocalDateTime cutoffTime = now.minusHours(1);
            rateLimitCache.entrySet().removeIf(entry -> {
                entry.getValue().entrySet().removeIf(pageEntry -> pageEntry.getValue().isBefore(cutoffTime));
                return entry.getValue().isEmpty();
            });
        }
    }
}

