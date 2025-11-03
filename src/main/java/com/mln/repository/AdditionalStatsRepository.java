package com.mln.repository;

import com.mln.entity.AdditionalStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdditionalStatsRepository extends JpaRepository<AdditionalStats, Long> {
    Optional<AdditionalStats> findByCompositeKey(String compositeKey);
    
    List<AdditionalStats> findByCategory(String category);
}

