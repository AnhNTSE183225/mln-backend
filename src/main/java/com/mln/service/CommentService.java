package com.mln.service;

import com.mln.dto.*;
import com.mln.entity.Comment;
import com.mln.entity.User;
import com.mln.repository.CommentRepository;
import com.mln.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    
    public PagedResponse<CommentDto> getAllComments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> commentPage = commentRepository.findAllByOrderByCreatedAtDesc(pageable);
        
        List<CommentDto> commentDtos = commentPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return new PagedResponse<>(
                commentDtos,
                commentPage.getNumber(),
                commentPage.getSize(),
                commentPage.getTotalElements(),
                commentPage.getTotalPages(),
                commentPage.isFirst(),
                commentPage.isLast()
        );
    }
    
    public PagedResponse<CommentDto> getUserComments(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> commentPage = commentRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        
        List<CommentDto> commentDtos = commentPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return new PagedResponse<>(
                commentDtos,
                commentPage.getNumber(),
                commentPage.getSize(),
                commentPage.getTotalElements(),
                commentPage.getTotalPages(),
                commentPage.isFirst(),
                commentPage.isLast()
        );
    }
    
    @Transactional
    public CommentDto createComment(UUID userId, CreateCommentRequest request) {
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        comment.setRating(request.getRating());
        comment.setCategory(request.getCategory());
        
        comment = commentRepository.save(comment);
        return convertToDto(comment);
    }
    
    @Transactional
    public CommentDto updateComment(UUID commentId, UUID userId, UpdateCommentRequest request) {
        Comment comment = commentRepository.findByIdAndUserId(commentId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found or you don't have permission to edit it"));
        
        comment.setContent(request.getContent());
        comment.setRating(request.getRating());
        comment.setCategory(request.getCategory());
        
        comment = commentRepository.save(comment);
        return convertToDto(comment);
    }
    
    @Transactional
    public void deleteComment(UUID commentId, UUID userId) {
        Comment comment = commentRepository.findByIdAndUserId(commentId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found or you don't have permission to delete it"));
        
        commentRepository.delete(comment);
    }
    
    public CommentStatisticsDto getStatistics() {
        Double avgRating = commentRepository.getAverageRating();
        Long totalComments = commentRepository.getTotalComments();
        Long uniqueUsers = commentRepository.getUniqueUsersCount();
        Long satisfiedCount = commentRepository.getSatisfiedCount();
        
        // Calculate satisfied percentage (4-5 star ratings)
        long satisfiedPercentage = totalComments != null && totalComments > 0 
                ? Math.round((satisfiedCount != null ? satisfiedCount : 0L) * 100.0 / totalComments)
                : 0L;
        
        return new CommentStatisticsDto(
                avgRating != null ? avgRating : 0.0,
                totalComments != null ? totalComments : 0L,
                uniqueUsers != null ? uniqueUsers : 0L,
                satisfiedPercentage
        );
    }
    
    private CommentDto convertToDto(Comment comment) {
        User user = userRepository.findById(comment.getUserId())
                .orElse(null);
        
        return new CommentDto(
                comment.getId(),
                comment.getUserId(),
                user != null ? user.getUsername() : "Unknown",
                user != null ? user.getDisplayName() : "Unknown",
                comment.getContent(),
                comment.getRating(),
                comment.getCategory(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}

