package org.example.clientservice.service;

import org.example.clientservice.dto.ClientProfileDto;
import org.example.clientservice.config.UserCreatedEvent;
import org.example.clientservice.model.ClientProfile;
import org.example.clientservice.repository.ClientProfileRepository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientProfileService {

    private final ClientProfileRepository profileRepository;

    public Mono<ClientProfile> initializeNewClientProfile(UserCreatedEvent event) {
        log.info("Initializing new profile for userId: {}", event.getUserId());

        ClientProfile newProfile = ClientProfile.builder()
                .userId(event.getUserId())
                .name(event.getUserName())
                .profileCompletionPercent(0)
                .recommendationFlag(false)

                // --- ADDED NEW FIELDS ---
                .email(event.getEmail())
                .createdAt(event.getCreatedAt())
                .source(event.getSource())
                // --- END OF NEW FIELDS ---

                .build();

        newProfile.setNew(true); // Set the isNew flag

        return profileRepository.save(newProfile)
                .doOnError(e -> log.error("Failed to initialize profile for userId: {}", event.getUserId(), e));
    }

    /**
     * Creates a new profile for testing.
     * (We also add the new fields here, making them optional)
     */
    public Mono<ClientProfileDto> createClientProfile(String userId, Mono<ClientProfileDto> profileDtoMono) {
        return profileDtoMono
                .flatMap(dto -> {
                    ClientProfile newProfile = ClientProfile.builder()
                            .userId(userId)
                            .name(dto.name())
                            .phone(dto.phone())
                            .skills(dto.skills())
                            .latitude(dto.latitude())
                            .longitude(dto.longitude())
                            .recommendationFlag(dto.recommendationFlag())
                            .profileCompletionPercent(calculateCompletion(dto))

                            // --- ADDED NEW FIELDS (read from DTO) ---
                            .email(dto.email())
                            .createdAt(dto.createdAt())
                            .source("postman-test") // Source is internal for this
                            // --- END OF NEW FIELDS ---

                            .build();

                    newProfile.setNew(true);

                    return profileRepository.save(newProfile);
                })
                .map(this::entityToDto);
    }

    /**
     * Retrieves a client's profile by their user ID.
     */
    public Mono<ClientProfileDto> getClientProfile(String userId) {
        return profileRepository.findById(userId)
                .map(this::entityToDto)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found")));
    }

    /**
     * Updates an existing client's profile.
     */
    public Mono<ClientProfileDto> updateClientProfile(String userId, Mono<ClientProfileDto> profileDtoMono) {
        return profileRepository.findById(userId)
                .flatMap(existingProfile -> profileDtoMono.flatMap(dto -> {

                    existingProfile.setName(dto.name());
                    existingProfile.setPhone(dto.phone());
                    existingProfile.setSkills(dto.skills());
                    existingProfile.setLatitude(dto.latitude());
                    existingProfile.setLongitude(dto.longitude());
                    existingProfile.setRecommendationFlag(dto.recommendationFlag());
                    existingProfile.setProfileCompletionPercent(calculateCompletion(dto));

                    // We also update the email if it's part of the DTO
                    existingProfile.setEmail(dto.email());

                    return profileRepository.save(existingProfile);
                }))
                .map(this::entityToDto)
                .doOnSuccess(dto -> log.info("Profile updated for userId: {}", dto.userId()))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found")));
    }

    // --- Helper Methods ---

    private ClientProfileDto entityToDto(ClientProfile profile) {
        return new ClientProfileDto(
                profile.getUserId(),
                profile.getName(),
                profile.getPhone(),
                profile.getSkills(),
                profile.getLatitude(),
                profile.getLongitude(),
                profile.getProfileCompletionPercent(),
                profile.getRecommendationFlag(),

                // --- ADDED NEW FIELDS ---
                profile.getEmail(),
                profile.getCreatedAt()
                // --- END OF NEW FIELDS ---
        );
    }

    // (calculateCompletion method is unchanged)
    private int calculateCompletion(ClientProfileDto dto) {
        int complete = 0;
        int totalFields = 4;

        if (dto.name() != null && !dto.name().isBlank()) complete++;
        if (dto.phone() != null && !dto.phone().isBlank()) complete++;
        if (dto.skills() != null && !dto.skills().isEmpty()) complete++;
        if (dto.latitude() != null && dto.longitude() != null) complete++;

        return (complete * 100) / totalFields;
    }
}