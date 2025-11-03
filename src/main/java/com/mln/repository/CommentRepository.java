package com.mln.repository;

import com.mln.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Page<Comment> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    Page<Comment> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    
    Optional<Comment> findByIdAndUserId(UUID id, UUID userId);
    
    @Query("SELECT AVG(c.rating) FROM Comment c")
    Double getAverageRating();
    
    @Query("SELECT COUNT(c) FROM Comment c")
    Long getTotalComments();
    
    @Query("SELECT COUNT(DISTINCT c.userId) FROM Comment c")
    Long getUniqueUsersCount();
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.rating >= 4")
    Long getSatisfiedCount();
}

