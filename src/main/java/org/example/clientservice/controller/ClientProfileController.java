package org.example.clientservice.controller;

import org.example.clientservice.dto.ClientProfileDto;
import org.example.clientservice.service.ClientProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientProfileController {

    private final ClientProfileService clientProfileService;

    @GetMapping("/hello")
    public Mono<String> sayHello() {
        return Mono.just("Hello! The Client Service is running on port 8081.");
    }

    /**
     * POST /{userId} - Creates or overwrites a profile
     * Use this in Postman to create a user for testing.
     */
    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ClientProfileDto> createClientProfile(
            @PathVariable String userId,
            @Valid @RequestBody Mono<ClientProfileDto> profileDtoMono) {

        return clientProfileService.createClientProfile(userId, profileDtoMono);
    }

    @GetMapping("/{userId}")
    public Mono<ResponseEntity<ClientProfileDto>> getClientProfile(
            @PathVariable String userId,
            @AuthenticationPrincipal Jwt jwt) { // jwt will be null

        // --- THIS BLOCK MUST BE COMMENTED OUT FOR TESTING ---
        /*
        if (jwt == null || !jwt.getSubject().equals(userId)) {
             return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
        }
        */

        return clientProfileService.getClientProfile(userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}")
    public Mono<ResponseEntity<ClientProfileDto>> updateClientProfile(
            @PathVariable String userId,
            @Valid @RequestBody Mono<ClientProfileDto> profileDtoMono,
            @AuthenticationPrincipal Jwt jwt) { // jwt will be null

        // --- THIS BLOCK MUST BE COMMENTED OUT FOR TESTING ---
        /*
        if (jwt == null || !jwt.getSubject().equals(userId)) {
             return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
        }
        */

        return clientProfileService.updateClientProfile(userId, profileDtoMono)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}