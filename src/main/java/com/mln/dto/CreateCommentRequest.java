package com.mln.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentRequest {
    @NotBlank(message = "Content is required")
    @Size(max = 2000, message = "Content must be at most 2000 characters")
    private String content;
    
    @NotNull(message = "Rating is required")
    private Integer rating; // 1-5
    
    @NotBlank(message = "Category is required")
    private String category;
}

