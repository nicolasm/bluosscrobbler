package com.nicolasm.bluosscrobbler.bluos.poller;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTask {
    private final BluOSStatusPoller poller;

    @Getter(AccessLevel.PACKAGE)
    private boolean enabled = true;

    @Scheduled(fixedDelay = 30000L)
    public void pollStatus() {
        if (enabled) {
            try {
                disableScheduledTask();
                poller.poll();
            } catch (Exception e) {
                log.error("An unexpected error occurred.", e);
            } finally {
                enableScheduledTask();
            }
        }
    }

    private void enableScheduledTask() {
        log.info("Start polling...");
        enabled = true;
    }

    private void disableScheduledTask() {
        log.info("Stop polling...");
        enabled = false;
    }
}
