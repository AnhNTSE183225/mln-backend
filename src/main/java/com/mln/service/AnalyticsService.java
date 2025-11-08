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
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM");
    
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
        
        return new AnalyticsDataDto(dailyVisitors, pageVisits);
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
        
        // Get current date
        String currentDate = now.format(DATE_FORMATTER);
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

