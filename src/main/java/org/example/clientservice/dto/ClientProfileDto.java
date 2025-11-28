package org.example.clientservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

public record ClientProfileDto(
        String userId,

        @Size(min = 2, max = 100)
        String name,

        @Size(max = 20)
        String phone,

        String email, // User info

        List<String> skills,

        @Min(-90) @Max(90)
        Double latitude,

        @Min(-180) @Max(180)
        Double longitude,



        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        String aiGeneratedSummary,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Double averageRating,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Integer totalReviews,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Double jobSuccessRate,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        String experienceLevel,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Double recommendedWagePerHour,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Integer profileStrengthScore,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        List<String> topReviewKeywords,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Instant lastAiUpdate,


        String profilePictureUrl,

        Integer profileCompletionPercent,
        Boolean recommendationFlag,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Instant createdAt
) {}