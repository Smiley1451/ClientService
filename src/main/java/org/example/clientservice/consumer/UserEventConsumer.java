package org.example.clientservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper; // IMPORT THIS
import org.example.clientservice.config.UserCreatedEvent;
import org.example.clientservice.service.ClientProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord; // IMPORT THIS
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment; // IMPORT THIS
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final ClientProfileService clientProfileService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "user.created", groupId = "client-service-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void handleUserCreatedEvent(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            String message = record.value();
            log.info("Raw message received: {}", message);


            UserCreatedEvent event = objectMapper.readValue(message, UserCreatedEvent.class);

            clientProfileService.initializeNewClientProfile(event)
                    .subscribe(
                            profile -> {
                                log.info("Successfully initialized profile for userId: {}", profile.getUserId());
                                ack.acknowledge();
                            },
                            error -> {
                                log.error("Error processing UserCreatedEvent: {}", error.getMessage());

                            }
                    );

        } catch (Exception e) {
            log.error("Failed to process message: {}", e.getMessage());

        }
    }
}