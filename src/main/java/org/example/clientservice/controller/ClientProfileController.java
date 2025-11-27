package org.example.clientservice.controller;

import org.example.clientservice.dto.ClientProfileDto;
import org.example.clientservice.service.ClientProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientProfileController {

    private final ClientProfileService clientProfileService;

    // --- PUBLIC/USER ENDPOINTS ---

    @GetMapping("/{userId}")
    public Mono<ResponseEntity<ClientProfileDto>> getClientProfile(@PathVariable String userId) {
        return clientProfileService.getClientProfile(userId)
                .map(ResponseEntity::ok);
    }

    /**
     * Update Profile (User Editable Fields Only)
     * Name, Phone, Skills, Location
     */
    @PutMapping("/{userId}")
    public Mono<ResponseEntity<ClientProfileDto>> updateClientProfile(
            @PathVariable String userId,
            @Valid @RequestBody Mono<ClientProfileDto> profileDtoMono) {

        return clientProfileService.updateClientProfile(userId, profileDtoMono)
                .map(ResponseEntity::ok);
    }

    // --- SYSTEM/ADMIN ENDPOINTS ---

    /**
     * Manually Trigger AI Refresh (For Demo/Testing)
     * Normally this runs via Scheduler on the 1st of the month.
     */
    @PostMapping("/{userId}/refresh-ai")
    public Mono<ResponseEntity<ClientProfileDto>> triggerAiRefresh(@PathVariable String userId) {
        return clientProfileService.performMonthlyAiAnalysis(userId)
                .map(ResponseEntity::ok);
    }
}