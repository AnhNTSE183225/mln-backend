package com.mln.service;

import com.mln.dto.LeaderboardEntryDto;
import com.mln.dto.SubmitScoreRequest;
import com.mln.entity.GameScore;
import com.mln.repository.GameScoreRepository;
import com.mln.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameScoreRepository gameScoreRepository;
    private final UserRepository userRepository; // To get displayName

    @Transactional
    public void submitScore(SubmitScoreRequest request) {
        userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + request.getUserId() + " not found."));

        LocalDate today = LocalDate.now();

        Optional<GameScore> existingScoreOpt = gameScoreRepository.findByUserIdAndGameTypeAndDate(
                request.getUserId(), request.getGameType(), today);

        if (existingScoreOpt.isPresent()) {
            // Update score only if the new one is higher
            GameScore existingScore = existingScoreOpt.get();
            if (request.getScore() > existingScore.getScore()) {
                existingScore.setScore(request.getScore());
                gameScoreRepository.save(existingScore);
            }
        } else {
            // Create a new score entry for the day
            GameScore newScore = new GameScore();
            newScore.setUserId(request.getUserId());
            newScore.setGameType(request.getGameType());
            newScore.setScore(request.getScore());
            newScore.setDate(today);
            gameScoreRepository.save(newScore);
        }
    }

    public List<LeaderboardEntryDto> getLeaderboard(String gameType) {
        LocalDate today = LocalDate.now();
        List<GameScore> topScores = gameScoreRepository.findTop10ByGameTypeAndDateOrderByScoreDesc(gameType, today);

        AtomicInteger rank = new AtomicInteger(1);

        return topScores.stream()
                .map(score -> new LeaderboardEntryDto(
                        score.getUser() != null ? score.getUser().getDisplayName() : "Unknown User",
                        score.getScore(),
                        score.getDate(),
                        rank.getAndIncrement()
                ))
                .collect(Collectors.toList());
    }
}
