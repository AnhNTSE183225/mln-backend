package com.mln.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitScoreRequest {
    @NotNull
    private UUID userId;

    @NotBlank
    private String gameType;

    @NotNull
    private Integer score;
}
