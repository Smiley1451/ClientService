package org.example.clientservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clientservice.model.ClientProfile;
import org.example.clientservice.model.ReviewRating;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroqService {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    @Value("${groq.api.model}")
    private String model;

    /**
     * Generates a new monthly summary using previous context + new reviews.
     */
    public Mono<String> generateMonthlyProfileUpdate(ClientProfile profile, List<ReviewRating> newReviews) {

        String context = buildIncrementalContext(profile, newReviews);

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content",
                                "You are an AI managing worker profiles. " +
                                        "Update the worker's summary and metrics based on their PREVIOUS summary and NEW reviews. " +
                                        "Generate a JSON response with: " +
                                        "aiGeneratedSummary (string), " +
                                        "experienceLevel (Beginner/Intermediate/Expert), " +
                                        "topReviewKeywords (list of strings, max 5), " +
                                        "recommendedWagePerHour (number), " +
                                        "profileStrengthScore (0-100)."),
                        Map.of("role", "user", "content", context)
                ),
                "response_format", Map.of("type", "json_object")
        );

        return webClientBuilder.build()
                .post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::extractContent)
                .doOnError(e -> log.error("Error calling Groq API", e));
    }

    private String buildIncrementalContext(ClientProfile profile, List<ReviewRating> reviews) {
        StringBuilder sb = new StringBuilder();

        sb.append("--- WORKER PROFILE ---\n");
        sb.append("Name: ").append(profile.getName()).append("\n");
        sb.append("Skills: ").append(profile.getSkills()).append("\n");
        sb.append("Previous Summary: ").append(profile.getAiGeneratedSummary() != null ? profile.getAiGeneratedSummary() : "No previous summary.").append("\n");
        sb.append("Current Success Rate: ").append(profile.getJobSuccessRate()).append("\n\n");

        sb.append("--- REVIEWS FROM LAST MONTH ---\n");
        if (reviews.isEmpty()) {
            sb.append("No new reviews this month.\n");
        } else {
            for (ReviewRating r : reviews) {
                sb.append(String.format("- [Rating: %d/5] %s\n", r.getRating(), r.getReviewText()));
            }
        }

        sb.append("\nINSTRUCTIONS: Analyze the new reviews in context of the previous summary. " +
                "If no new reviews, keep the summary stable but mention consistency. " +
                "Update the recommended wage based on performance trend.");

        return sb.toString();
    }

    private String extractContent(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            log.error("Failed to parse Groq response", e);
            return "{}"; // Return empty JSON on failure
        }
    }
}