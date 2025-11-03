package com.mln.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private UUID id;
    private UUID userId;
    private String username;
    private String displayName;
    private String content;
    private Integer rating;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

