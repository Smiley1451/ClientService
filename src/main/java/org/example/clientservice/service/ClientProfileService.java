package org.example.clientservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.clientservice.config.UserCreatedEvent;
import org.example.clientservice.dto.ClientProfileDto;
import org.example.clientservice.model.ClientProfile;
import org.example.clientservice.model.ReviewRating;
import org.example.clientservice.repository.ClientProfileRepository;
import org.example.clientservice.repository.ReviewRatingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientProfileService {

    private final ClientProfileRepository profileRepository;
    private final ReviewRatingRepository reviewRepository;
    private final GroqService groqService;
    private final ObjectMapper objectMapper;



    public Mono<ClientProfile> initializeNewClientProfile(UserCreatedEvent event) {
        log.info("Initializing new profile for userId: {}", event.getUserId());

        ClientProfile newProfile = ClientProfile.builder()
                .id(UUID.fromString(event.getUserId()))
                .userId(event.getUserName())
                .name(null)
                .email(event.getEmail())
                .source(event.getSource())
                .createdAt(event.getCreatedAt())
                .profileCompletionPercent(0)
                .recommendationFlag(false)
                .totalReviews(0)
                .averageRating(0.0)
                .jobSuccessRate(100.0)
                .aiGeneratedSummary("New worker profile initialized.")
                .build();


        return profileRepository.createProfile(newProfile)
                .doOnError(e -> log.error("Failed to initialize profile for userId: {}", event.getUserId(), e));
    }


    public Mono<ClientProfileDto> updateClientProfile(String userId, Mono<ClientProfileDto> profileDtoMono) {
        return profileRepository.findByUserId(userId)
                .flatMap(existingProfile -> profileDtoMono.flatMap(dto -> {

                    if(dto.name() != null) existingProfile.setName(dto.name());
                    if(dto.phone() != null) existingProfile.setPhone(dto.phone());
                    if(dto.skills() != null) existingProfile.setSkills(dto.skills());
                    if(dto.latitude() != null) existingProfile.setLatitude(dto.latitude());
                    if(dto.longitude() != null) existingProfile.setLongitude(dto.longitude());

                    existingProfile.setProfileCompletionPercent(calculateCompletion(existingProfile));

                    return profileRepository.save(existingProfile);
                }))
                .map(this::entityToDto)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found")));
    }


    public Mono<ClientProfileDto> performMonthlyAiAnalysis(String userId) {
        Instant now = Instant.now();
        Instant startOfLastMonth = now.minus(30, ChronoUnit.DAYS); // Approximate for demo

        return profileRepository.findByUserId(userId)
                .flatMap(profile ->
                        reviewRepository.findByWorkerIdAndCreatedAtBetween(userId, startOfLastMonth, now)
                                .collectList()
                                .flatMap(reviews -> {
                                    return groqService.generateMonthlyProfileUpdate(profile, reviews)
                                            .flatMap(jsonResponse -> updateProfileWithAiData(profile, jsonResponse));
                                })
                )
                .map(this::entityToDto);
    }

    public Mono<Void> updateLocation(String userId, Double lat, Double lon) {
        return profileRepository.findByUserId(userId)
                .flatMap(profile -> {
                    profile.setLatitude(lat);
                    profile.setLongitude(lon);
                    return profileRepository.save(profile);
                })
                .then();
    }

    private Mono<ClientProfile> updateProfileWithAiData(ClientProfile profile, String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);

            if (root.has("aiGeneratedSummary"))
                profile.setAiGeneratedSummary(root.get("aiGeneratedSummary").asText());
            if (root.has("experienceLevel"))
                profile.setExperienceLevel(root.get("experienceLevel").asText());
            if (root.has("recommendedWagePerHour"))
                profile.setRecommendedWagePerHour(root.get("recommendedWagePerHour").asDouble());
            if (root.has("profileStrengthScore"))
                profile.setProfileStrengthScore(root.get("profileStrengthScore").asInt());

            if (root.has("topReviewKeywords") && root.get("topReviewKeywords").isArray()) {
                List<String> keywords = new ArrayList<>();
                root.get("topReviewKeywords").forEach(k -> keywords.add(k.asText()));
                profile.setTopReviewKeywords(keywords);
            }

            profile.setLastAiUpdate(Instant.now());

            return profileRepository.save(profile);

        } catch (Exception e) {
            log.error("Error parsing AI response", e);
            return Mono.just(profile);
        }
    }

    public Mono<ClientProfileDto> getClientProfile(String userId) {
        return profileRepository.findByUserId(userId)
                .map(this::entityToDto)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found")));
    }


    public Flux<ClientProfileDto> searchProfiles(String skill, Double minRating) {
        Flux<ClientProfile> profiles;

        if (skill != null && !skill.isBlank()) {
            profiles = profileRepository.findBySkill(skill);
        } else if (minRating != null) {
            profiles = profileRepository.findByAverageRatingGreaterThanEqual(minRating);
        } else {
            profiles = profileRepository.findAll();
        }

        return profiles.map(this::entityToDto);
    }


    public Mono<ClientProfileDto> updateProfilePicture(String userId, String imageUrl) {
        return profileRepository.findByUserId(userId)
                .flatMap(profile -> {
                    profile.setProfilePictureUrl(imageUrl);
                    return profileRepository.save(profile);
                })
                .map(this::entityToDto);
    }


    private ClientProfileDto entityToDto(ClientProfile profile) {
        return new ClientProfileDto(
                profile.getUserId(),
                profile.getName(),
                profile.getPhone(),
                profile.getEmail(),
                profile.getSkills(),
                profile.getLatitude(),
                profile.getLongitude(),
                profile.getAiGeneratedSummary(),
                profile.getAverageRating(),
                profile.getTotalReviews(),
                profile.getJobSuccessRate(),
                profile.getExperienceLevel(),
                profile.getRecommendedWagePerHour(),
                profile.getProfileStrengthScore(),
                profile.getTopReviewKeywords(),
                profile.getLastAiUpdate(),
                profile.getProfilePictureUrl(),
                profile.getProfileCompletionPercent(),
                profile.getRecommendationFlag(),
                profile.getCreatedAt()
        );
    }

    private int calculateCompletion(ClientProfile p) {
        int complete = 0;
        if (p.getName() != null && !p.getName().isBlank()) complete++;
        if (p.getPhone() != null && !p.getPhone().isBlank()) complete++;
        if (p.getSkills() != null && !p.getSkills().isEmpty()) complete++;
        if (p.getLatitude() != null && p.getLongitude() != null) complete++;
        return (complete * 100) / 4;
    }
}