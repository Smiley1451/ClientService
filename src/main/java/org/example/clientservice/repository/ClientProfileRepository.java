package org.example.clientservice.repository;

import org.example.clientservice.model.ClientProfile;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Repository
public interface ClientProfileRepository extends R2dbcRepository<ClientProfile, UUID> {
    Mono<ClientProfile> findByUserId(String userId);
    Flux<ClientProfile> findByAverageRatingGreaterThanEqual(Double minRating);


    @Query("SELECT * FROM client_profiles WHERE :skill = ANY(skills)")
    Flux<ClientProfile> findBySkill(String skill);


    @Query("INSERT INTO client_profiles " +
            "(id, user_id, name, email, source, created_at, " +
            "profile_completion_percent, recommendation_flag, total_reviews, " +
            "average_rating, job_success_rate, ai_generated_summary) " +
            "VALUES " +
            "(:#{#p.id}, :#{#p.userId}, :#{#p.name}, :#{#p.email}, :#{#p.source}, :#{#p.createdAt}, " +
            ":#{#p.profileCompletionPercent}, :#{#p.recommendationFlag}, :#{#p.totalReviews}, " +
            ":#{#p.averageRating}, :#{#p.jobSuccessRate}, :#{#p.aiGeneratedSummary}) " +
            "RETURNING *")
    Mono<ClientProfile> createProfile(@Param("p") ClientProfile profile);

    @Query("SELECT * FROM client_profile WHERE recommendation_flag = true")
    Flux<ClientProfile> findAllActiveWorkers();


}