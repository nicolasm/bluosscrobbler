package com.nicolasm.bluosscrobbler.bluos.poller;

import com.nicolasm.bluosscrobbler.bluos.service.BluOSStatusService;
import com.nicolasm.bluosscrobbler.scrobbler.ScrobblerCallback;
import com.nicolasm.service.bluosscrobbler.bluos.model.StatusType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BluOSStatusPoller {
    private final BluOSStatusService service;
    private final List<ScrobblerCallback> scrobblerCallbacks;

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
        StatusType status = service.getStatus();
        if (status != null) {
            log.info("Current played track: {} - {} - {}", status.getArtist(), status.getAlbum(), status.getName());
            if (status.getState().equals("play")) {
                scrobblerCallbacks.forEach(ScrobblerCallback::notifyNowPlaying);
            }

            String etag = status.getEtag();
            do {
                log.info("Poll status...");
                status = service.getStatus(status.getEtag());

                if ((status != null)
                        && !StringUtils.equals(etag, status.getEtag())
                        && (status.getState().equals("play"))) {
                    log.info("New played track: {} - {} - {}", status.getArtist(), status.getAlbum(), status.getName());
                    etag = status.getEtag();

                    scrobblerCallbacks.forEach(ScrobblerCallback::notifyNowPlaying);
                }
            } while (status != null);
        }
    }
}
