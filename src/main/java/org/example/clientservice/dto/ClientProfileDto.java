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

        List<String> skills,

        @Min(-90) @Max(90)
        Double latitude,

        @Min(-180) @Max(180)
        Double longitude,

        Integer profileCompletionPercent,
        Boolean recommendationFlag,

        // --- ADDED NEW FIELDS ---
        String email,

        // This makes 'createdAt' read-only in the API response
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Instant createdAt
        // --- END OF NEW FIELDS ---
) {}