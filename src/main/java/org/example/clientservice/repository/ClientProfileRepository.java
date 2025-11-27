package org.example.clientservice.repository;



import org.example.clientservice.model.ClientProfile;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientProfileRepository extends R2dbcRepository<ClientProfile, String> {
    // Provides reactive methods like findById(String userId), save(ClientProfile profile)
}
