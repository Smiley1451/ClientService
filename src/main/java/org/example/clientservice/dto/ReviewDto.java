package org.example.clientservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ReviewDto(
        @NotBlank String workerId,
        @NotBlank String reviewerId,
        @NotNull @Min(1) @Max(5) Integer rating,
        String reviewText,
        @Min(0) @Max(100) Integer punctualityScore,
        @Min(0) @Max(100) Integer qualityScore,
        @Min(0) @Max(100) Integer behaviourScore,
        String jobId
) {}