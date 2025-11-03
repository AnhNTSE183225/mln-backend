package com.mln.repository;

import com.mln.entity.DailyVisitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DailyVisitorRepository extends JpaRepository<DailyVisitor, Long> {
    Optional<DailyVisitor> findByDateKey(String dateKey);
    
    @Query("SELECT d FROM DailyVisitor d ORDER BY d.dateKey DESC")
    List<DailyVisitor> findAllOrderByDateDesc();
}

