package org.example.clientservice.controller;

import org.example.clientservice.dto.ClientProfileDto;
import org.example.clientservice.service.ClientProfileService;
import org.example.clientservice.service.ImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientProfileController {

    private final ClientProfileService clientProfileService;
    private final ImageService imageService;



    @GetMapping("/{userId}")
    public Mono<ResponseEntity<ClientProfileDto>> getClientProfile(@PathVariable String userId) {
        return clientProfileService.getClientProfile(userId)
                .map(ResponseEntity::ok);
    }


    @PutMapping("/{userId}")
    public Mono<ResponseEntity<ClientProfileDto>> updateClientProfile(
            @PathVariable String userId,
            @Valid @RequestBody Mono<ClientProfileDto> profileDtoMono) {

        return clientProfileService.updateClientProfile(userId, profileDtoMono)
                .map(ResponseEntity::ok);
    }


    @PostMapping("/{userId}/refresh-ai")
    public Mono<ResponseEntity<ClientProfileDto>> triggerAiRefresh(@PathVariable String userId) {
        return clientProfileService.performMonthlyAiAnalysis(userId)
                .map(ResponseEntity::ok);
    }

    @PostMapping(value = "/{userId}/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<ClientProfileDto>> uploadProfilePicture(
            @PathVariable String userId,
            @RequestPart("file") Mono<FilePart> filePartMono) {

        return filePartMono
                .flatMap(imageService::uploadImage)
                .flatMap(url -> clientProfileService.updateProfilePicture(userId, url))
                .map(ResponseEntity::ok);
    }


    @GetMapping
    public Mono<ResponseEntity<Flux<ClientProfileDto>>> getAllProfiles(
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) Double minRating) {

        return Mono.just(ResponseEntity.ok(
                clientProfileService.searchProfiles(skill, minRating)
        ));
    }
}