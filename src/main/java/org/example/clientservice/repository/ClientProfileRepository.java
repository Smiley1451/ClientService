package org.example.clientservice.repository;

import org.example.clientservice.model.ClientProfile;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Repository
public interface ClientProfileRepository extends R2dbcRepository<ClientProfile, UUID> {
    Mono<ClientProfile> findByUserId(String userId);
}