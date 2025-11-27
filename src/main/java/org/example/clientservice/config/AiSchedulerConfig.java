package org.example.clientservice.config;

import org.example.clientservice.repository.ClientProfileRepository;
import org.example.clientservice.service.ClientProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class AiSchedulerConfig {

    private final ClientProfileService clientProfileService;
    private final ClientProfileRepository clientProfileRepository;

    @Scheduled(cron = "0 0 0 1 * ?")
    public void runMonthlyAiUpdates() {
        log.info("Starting monthly AI profile refresh...");

        clientProfileRepository.findAll()
                .flatMap(profile -> {
                    log.info("Updating profile for: {}", profile.getUserId());
                    return clientProfileService.performMonthlyAiAnalysis(profile.getUserId());
                })
                .subscribe(
                        success -> log.debug("Updated profile"),
                        error -> log.error("Error updating profile in scheduler", error),
                        () -> log.info("Monthly AI refresh completed.")
                );
    }
}