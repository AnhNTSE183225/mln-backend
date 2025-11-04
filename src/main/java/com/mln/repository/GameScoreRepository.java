package com.mln.repository;

import com.mln.entity.GameScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameScoreRepository extends JpaRepository<GameScore, UUID> {
    Optional<GameScore> findByUserIdAndGameTypeAndDate(UUID userId, String gameType, LocalDate date);

    List<GameScore> findTop10ByGameTypeAndDateOrderByScoreDesc(String gameType, LocalDate date);
}
