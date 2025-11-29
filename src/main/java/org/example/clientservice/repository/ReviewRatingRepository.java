package org.example.clientservice.repository;

import org.example.clientservice.model.ClientProfile;
import org.example.clientservice.model.ReviewRating;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface ReviewRatingRepository extends R2dbcRepository<ReviewRating, UUID> {

    Flux<ReviewRating> findByWorkerId(String workerId);




    @Query("SELECT * FROM review_ratings WHERE worker_id = :workerId AND created_at >= :startDate AND created_at < :endDate")
    Flux<ReviewRating> findByWorkerIdAndCreatedAtBetween(String workerId, Instant startDate, Instant endDate);

    @Query("SELECT AVG(rating) as avg_rating, COUNT(*) as total_reviews FROM review_ratings WHERE worker_id = :workerId")
    Mono<WorkerStats> getWorkerStats(String workerId);
}


