package com.mln.config;

import com.mln.entity.*;
import com.mln.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInitializer {
    
    private final PageVisitRepository pageVisitRepository;
    private final AnalyticsMetricsRepository metricsRepository;
    private final AdditionalStatsRepository additionalStatsRepository;
    
    @PostConstruct
    @Transactional
    public void initializeDefaultData() {
        try {
            // Initialize page visits with zero counts
            initializePageVisits();
            
            // Initialize metrics
            initializeMetrics();
            
            // Initialize additional stats
            initializeAdditionalStats();
        } catch (Exception e) {
            // Log error but don't fail startup if data already exists
            System.err.println("Data initialization warning: " + e.getMessage());
        }
    }
    
    private void initializePageVisits() {
        String[] pages = {"Trang Chủ", "Trắc Nghiệm", "Nhận Xét", "Thống Kê"};
        for (String page : pages) {
            if (!pageVisitRepository.findByPage(page).isPresent()) {
                PageVisit pageVisit = new PageVisit();
                pageVisit.setPage(page);
                pageVisit.setVisits(0);
                pageVisitRepository.save(pageVisit);
            }
        }
    }
    
    private void initializeMetrics() {
        if (!metricsRepository.findByKey("default").isPresent()) {
            AnalyticsMetrics metrics = new AnalyticsMetrics();
            metrics.setKey("default");
            metrics.setAvgTimeOnSite("0:00");
            metrics.setBounceRate("0%");
            metrics.setVisitorsGrowth("+0%");
            metrics.setPageViewsGrowth("+0%");
            metrics.setTimeGrowth("+0:00");
            metrics.setBounceRateChange("0%");
            metricsRepository.save(metrics);
        }
    }
    
    private void initializeAdditionalStats() {
        // Initialize device stats
        String[] deviceKeys = {"mobile", "desktop", "tablet"};
        for (String key : deviceKeys) {
            String compositeKey = "devices_" + key;
            try {
                if (!additionalStatsRepository.findByCompositeKey(compositeKey).isPresent()) {
                    AdditionalStats stat = new AdditionalStats();
                    stat.setCategory("devices");
                    stat.setKeyName(key);
                    stat.setValue("0%");
                    stat.setCompositeKey(compositeKey);
                    additionalStatsRepository.save(stat);
                }
            } catch (Exception e) {
                // Skip if already exists or constraint violation
                System.err.println("Skipping device stat initialization for " + key + ": " + e.getMessage());
            }
        }
        
        // Initialize user stats
        String[] userKeys = {"newUsers", "returningUsers", "returnRate"};
        String[] userValues = {"0", "0", "0%"};
        for (int i = 0; i < userKeys.length; i++) {
            String compositeKey = "users_" + userKeys[i];
            try {
                if (!additionalStatsRepository.findByCompositeKey(compositeKey).isPresent()) {
                    AdditionalStats stat = new AdditionalStats();
                    stat.setCategory("users");
                    stat.setKeyName(userKeys[i]);
                    stat.setValue(userValues[i]);
                    stat.setCompositeKey(compositeKey);
                    additionalStatsRepository.save(stat);
                }
            } catch (Exception e) {
                // Skip if already exists or constraint violation
                System.err.println("Skipping user stat initialization for " + userKeys[i] + ": " + e.getMessage());
            }
        }
        
        // Initialize source stats
        String[] sourceKeys = {"search", "direct", "social"};
        for (String key : sourceKeys) {
            String compositeKey = "sources_" + key;
            try {
                if (!additionalStatsRepository.findByCompositeKey(compositeKey).isPresent()) {
                    AdditionalStats stat = new AdditionalStats();
                    stat.setCategory("sources");
                    stat.setKeyName(key);
                    stat.setValue("0%");
                    stat.setCompositeKey(compositeKey);
                    additionalStatsRepository.save(stat);
                }
            } catch (Exception e) {
                // Skip if already exists or constraint violation
                System.err.println("Skipping source stat initialization for " + key + ": " + e.getMessage());
            }
        }
    }
}

