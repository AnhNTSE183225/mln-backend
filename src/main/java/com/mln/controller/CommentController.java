package com.mln.controller;

import com.mln.dto.*;
import com.mln.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentService commentService;
    
    @GetMapping
    public ResponseEntity<PagedResponse<CommentDto>> getAllComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(commentService.getAllComments(page, size));
    }
    
    @GetMapping("/my-comments")
    public ResponseEntity<PagedResponse<CommentDto>> getMyComments(
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(commentService.getUserComments(userId, page, size));
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<CommentStatisticsDto> getStatistics() {
        return ResponseEntity.ok(commentService.getStatistics());
    }
    
    @PostMapping
    public ResponseEntity<CommentDto> createComment(
            @RequestParam UUID userId,
            @Valid @RequestBody CreateCommentRequest request) {
        return ResponseEntity.ok(commentService.createComment(userId, request));
    }
    
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable UUID commentId,
            @RequestParam UUID userId,
            @Valid @RequestBody UpdateCommentRequest request) {
        return ResponseEntity.ok(commentService.updateComment(commentId, userId, request));
    }
    
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable UUID commentId,
            @RequestParam UUID userId) {
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok().build();
    }
}

