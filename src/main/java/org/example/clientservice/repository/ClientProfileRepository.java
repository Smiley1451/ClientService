package org.example.clientservice.repository;

import org.example.clientservice.model.ClientProfile;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
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
}