package org.example.clientservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

/**
 * Represents the user creation event consumed from Kafka.
 * This class structure matches your new requirement.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEvent {
    private String userId;
    private String userName;
    private String email;
    private String role;
    private Instant createdAt;
    private String source;
}