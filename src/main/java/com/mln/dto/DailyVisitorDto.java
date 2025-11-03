package com.mln.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyVisitorDto {
    private String date;
    private Integer visitors;
    private Integer pageViews;
}

