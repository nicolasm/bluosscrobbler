package com.nicolasm.bluosscrobbler.bluos.service;

import com.google.common.collect.ImmutableMap;
import com.nicolasm.bluosscrobbler.bluos.config.BluOSConfig;
import com.nicolasm.service.bluosscrobbler.bluos.model.StatusType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.nicolasm.bluosscrobbler.bluos.model.PollingUrlParameters.etag;
import static com.nicolasm.bluosscrobbler.bluos.model.PollingUrlParameters.timeout;

@Service
@RequiredArgsConstructor
@Slf4j
public class BluOSStatusService {
    private final BluOSConfig config;
    private final RestTemplate restTemplate = new RestTemplate();

    private boolean enabled = true;

    @Scheduled(fixedDelay = 30000L)
    public void pollStatus() {
        if (enabled) {
            log.info("Start polling...");
            enabled = false;

            poll();

            enabled = true;
        }
    }

    private void poll() {
        StatusType status = getStatus();
        if (status != null) {
            log.info("Current played track: {} - {} - {}", status.getArtist(), status.getAlbum(), status.getName());
            String etag = status.getEtag();
            do {
                log.info("Poll status...");
                status = getStatus(status.getEtag());

                if ((status != null)
                        && !StringUtils.equals(etag, status.getEtag())
                        && (status.getState().equals("play"))) {
                    log.info("New played track: {} - {} - {}", status.getArtist(), status.getAlbum(), status.getName());
                    etag = status.getEtag();
                }
            } while (status != null);
        }
    }

    public StatusType getStatus() {
        try {
            ResponseEntity<StatusType> responseEntity =
                    restTemplate.getForEntity(config.getStatusPollingEndpoint(), StatusType.class);
            return responseEntity.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public StatusType getStatus(String etagIn) {
        try {
            ResponseEntity<StatusType> responseEntity =
                    restTemplate.getForEntity(config.getStatusLongPollingEndpoint(), StatusType.class,
                            ImmutableMap.of(timeout, config.getTimeout(),
                                    etag, etagIn));
            return responseEntity.getBody();
        } catch (Exception e) {
            return null;
        }
    }
}
