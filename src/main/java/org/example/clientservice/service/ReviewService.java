package org.example.clientservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clientservice.dto.ReviewDto;
import org.example.clientservice.model.ClientProfile;
import org.example.clientservice.model.ReviewRating;
import org.example.clientservice.repository.ClientProfileRepository;
import org.example.clientservice.repository.ReviewRatingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRatingRepository reviewRepository;
    private final ClientProfileRepository profileRepository;

    @Transactional
    public Mono<ReviewRating> addReview(ReviewDto dto) {
        ReviewRating review = ReviewRating.builder()
                .workerId(dto.workerId())
                .reviewerId(dto.reviewerId())
                .rating(dto.rating())
                .reviewText(dto.reviewText())
                .punctualityScore(dto.punctualityScore())
                .qualityScore(dto.qualityScore())
                .behaviourScore(dto.behaviourScore())
                .jobId(dto.jobId())
                .createdAt(Instant.now())
                .build();

        return reviewRepository.save(review)
                .flatMap(savedReview -> updateWorkerStats(savedReview.getWorkerId())
                        .thenReturn(savedReview));
    }

    public Flux<ReviewRating> getReviewsForWorker(String workerId) {
        return reviewRepository.findByWorkerId(workerId);
    }


    private Mono<Void> updateWorkerStats(String workerId) {
        return reviewRepository.getWorkerStats(workerId)
                .flatMap(stats -> profileRepository.findByUserId(workerId)
                        .flatMap(profile -> {
                            double avg = stats.getAvgRating() != null ? stats.getAvgRating() : 0.0;
                            int count = stats.getTotalReviews() != null ? stats.getTotalReviews() : 0;

                            profile.setAverageRating(avg);
                            profile.setTotalReviews(count);
                            return profileRepository.save(profile);
                        }))
                .then();
    }
}