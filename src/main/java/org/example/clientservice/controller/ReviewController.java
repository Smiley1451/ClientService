package org.example.clientservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clientservice.dto.ReviewDto;
import org.example.clientservice.model.ReviewRating;
import org.example.clientservice.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;


    @PostMapping
    public Mono<ResponseEntity<ReviewRating>> addReview(@Valid @RequestBody ReviewDto reviewDto) {
        return reviewService.addReview(reviewDto)
                .map(ResponseEntity::ok);
    }


    @GetMapping("/worker/{workerId}")
    public Mono<ResponseEntity<Flux<ReviewRating>>> getWorkerReviews(@PathVariable String workerId) {
        return Mono.just(ResponseEntity.ok(reviewService.getReviewsForWorker(workerId)));
    }
}