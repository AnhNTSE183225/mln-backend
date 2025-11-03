package com.mln.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentStatisticsDto {
    private Double averageRating;
    private Long totalComments;
    private Long uniqueUsers;
    private Long satisfiedPercentage; // Percentage of 4-5 star ratings
}

