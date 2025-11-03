package com.mln.repository;

import com.mln.entity.PageVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PageVisitRepository extends JpaRepository<PageVisit, Long> {
    Optional<PageVisit> findByPage(String page);
}

