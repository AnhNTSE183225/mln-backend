package com.mln.repository;

import com.mln.entity.HourlyTraffic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HourlyTrafficRepository extends JpaRepository<HourlyTraffic, Long> {
    Optional<HourlyTraffic> findByHour(String hour);
    
    @Query("SELECT h FROM HourlyTraffic h ORDER BY h.hour ASC")
    List<HourlyTraffic> findAllOrderByHourAsc();
}

