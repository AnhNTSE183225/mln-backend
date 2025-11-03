package com.mln.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalStatsDto {
    private DeviceStatsDto devices;
    private UserStatsDto users;
    private SourceStatsDto sources;
}

