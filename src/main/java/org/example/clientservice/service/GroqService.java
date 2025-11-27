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

    public Mono<String> generateProfileAnalysis(ClientProfile profile, List<ReviewRating> reviews) {
        // Construct the prompt context
        String context = buildContext(profile, reviews);

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", "You are an AI assistant analyzing worker profiles for a gig platform. Generate a JSON response with: summary, experienceLevel, topKeywords (list), and recommendedWage."),
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

    private String buildContext(ClientProfile profile, List<ReviewRating> reviews) {
        StringBuilder sb = new StringBuilder();
        sb.append("Worker Name: ").append(profile.getName()).append("\n");
        sb.append("Skills: ").append(profile.getSkills()).append("\n");
        sb.append("Current Success Rate: ").append(profile.getJobSuccessRate()).append("\n");
        sb.append("Recent Reviews:\n");
        for (ReviewRating r : reviews) {
            sb.append("- Rating: ").append(r.getRating()).append("/5, Text: ").append(r.getReviewText()).append("\n");
        }
        return sb.toString();
    }

    private String extractContent(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            log.error("Failed to parse Groq response", e);
            return "{}";
        }
    }
}