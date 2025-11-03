package com.mln.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "analytics_metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsMetrics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String key; // Single row identifier
    
    @Column(nullable = false)
    private String avgTimeOnSite = "0:00";
    
    @Column(nullable = false)
    private String bounceRate = "0%";
    
    @Column(nullable = false)
    private String visitorsGrowth = "+0%";
    
    @Column(nullable = false)
    private String pageViewsGrowth = "+0%";
    
    @Column(nullable = false)
    private String timeGrowth = "+0:00";
    
    @Column(nullable = false)
    private String bounceRateChange = "0%";
}

