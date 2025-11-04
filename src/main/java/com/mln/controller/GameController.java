package com.mln.controller;

import com.mln.dto.LeaderboardEntryDto;
import com.mln.dto.SubmitScoreRequest;
import com.mln.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("/score")
    public ResponseEntity<Void> submitScore(@Valid @RequestBody SubmitScoreRequest request) {
        gameService.submitScore(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/leaderboard/{gameType}")
    public ResponseEntity<List<LeaderboardEntryDto>> getLeaderboard(@PathVariable String gameType) {
        return ResponseEntity.ok(gameService.getLeaderboard(gameType));
    }
}
