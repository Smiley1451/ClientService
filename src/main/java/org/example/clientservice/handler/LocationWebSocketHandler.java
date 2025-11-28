package org.example.clientservice.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clientservice.dto.LocationUpdateDto;
import org.example.clientservice.service.ClientProfileService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocationWebSocketHandler implements WebSocketHandler {

    private final ClientProfileService clientProfileService;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(WebSocketSession session) {

        String userId = getUserIdFromSession(session);

        if (userId == null) {
            return session.close();
        }

        log.info("Live location tracking started for userId: {}", userId);

        return session.receive()
                .map(msg -> msg.getPayloadAsText())
                .flatMap(json -> {
                    try {
                        return Mono.just(objectMapper.readValue(json, LocationUpdateDto.class));
                    } catch (Exception e) {
                        log.error("Invalid location format", e);
                        return Mono.empty();
                    }
                })

                .sample(Duration.ofSeconds(3))
                .flatMap(dto -> {
                    log.debug("Updating location for {}: {}, {}", userId, dto.latitude(), dto.longitude());
                    return clientProfileService.updateLocation(userId, dto.latitude(), dto.longitude());
                })
                .then();
    }

    private String getUserIdFromSession(WebSocketSession session) {
        try {

            String query = session.getHandshakeInfo().getUri().getQuery();
            if (query != null && query.contains("userId=")) {
                return query.split("userId=")[1].split("&")[0];
            }
        } catch (Exception e) {
            log.error("Failed to extract userId from WS session");
        }
        return null;
    }
}