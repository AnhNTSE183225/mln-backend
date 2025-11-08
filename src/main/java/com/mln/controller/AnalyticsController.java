package com.mln.controller;

import com.mln.dto.*;
import com.mln.service.AnalyticsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    @GetMapping
    public ResponseEntity<AnalyticsDataDto> getAllAnalytics() {
        return ResponseEntity.ok(analyticsService.getAllAnalytics());
    }
    
    @GetMapping("/daily-visitors")
    public ResponseEntity<java.util.List<DailyVisitorDto>> getDailyVisitors() {
        return ResponseEntity.ok(analyticsService.getDailyVisitors());
    }
    
    @GetMapping("/page-visits")
    public ResponseEntity<java.util.List<PageVisitDto>> getPageVisits() {
        return ResponseEntity.ok(analyticsService.getPageVisits());
    }
    
    @PostMapping("/track-visit")
    public ResponseEntity<Map<String, Object>> trackVisit(
            @RequestParam String page,
            HttpServletRequest request) {
        String clientIp = getClientIp(request);
        return ResponseEntity.ok(analyticsService.trackVisit(page, clientIp));
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip != null ? ip : "unknown";
    }
}

