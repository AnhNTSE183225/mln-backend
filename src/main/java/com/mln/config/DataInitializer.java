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
    
    @PostConstruct
    @Transactional
    public void initializeDefaultData() {
        try {
            // Initialize page visits with zero counts
            initializePageVisits();
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
}

