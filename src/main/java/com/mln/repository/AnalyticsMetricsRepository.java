package com.mln.repository;

import com.mln.entity.AnalyticsMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnalyticsMetricsRepository extends JpaRepository<AnalyticsMetrics, Long> {
    Optional<AnalyticsMetrics> findByKey(String key);
}

