package com.mln.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsDto {
    private Integer newUsers;
    private Integer returningUsers;
    private String returnRate;
}

