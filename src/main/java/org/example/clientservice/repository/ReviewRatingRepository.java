package org.example.clientservice.repository;

import org.example.clientservice.model.ReviewRating;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import java.util.UUID;

@Repository
public interface ReviewRatingRepository extends R2dbcRepository<ReviewRating, UUID> {
    Flux<ReviewRating> findByWorkerId(String workerId);
}