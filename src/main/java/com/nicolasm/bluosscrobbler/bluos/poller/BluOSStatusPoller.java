package com.nicolasm.bluosscrobbler.bluos.poller;

import com.nicolasm.bluosscrobbler.bluos.model.BluOSStatus;
import com.nicolasm.bluosscrobbler.bluos.model.TrackPlay;
import com.nicolasm.bluosscrobbler.bluos.service.BluOSStatusService;
import com.nicolasm.bluosscrobbler.bluos.service.TrackPlayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BluOSStatusPoller {
    private final BluOSStatusService service;
    private final TrackPlayService playService;

    public void poll() {
        BluOSStatus status = service.getStatus();
        if (status == null) {
            return;
        }

        if (status.isPlaying()) {
            updateNowPlaying(status);
        }

        do {
            logPollStatus(status);

            BluOSStatus previous = status;
            status = service.getStatus(status.computePollingTimeout(), status.getEtag());
            if (status != null) {
                handlePlayingState(previous, status);

                if (status.shouldBeScrobbled()) {
                    playService.scrobble(TrackPlay.fromStatus(status));
                }

                if (status.isNewTrackPlay(previous)) {
                    playService.markAsPlayed(previous.md5Checksum());
                    updateNowPlaying(status);
                }

            }
        } while (status != null);
    }

    private void logPollStatus(BluOSStatus status) {
        log.info("Poll status '{} - {} - {} - {} {}/{}' seconds...",
                status.getArtist(),
                status.getAlbum(),
                status.getName(),
                status.getState(),
                status.getSecs(),
                status.getTotlen());
        log.info("md5 checksum {}", status.md5Checksum());
    }

    private void handlePlayingState(BluOSStatus previous, BluOSStatus current) {
        if (current.isPlaying()
                && previous.isPaused()) {
            log.info("BluOS playing on.");
            updateNowPlaying(current);
        } else if (current.isPaused()) {
            log.info("BluOS playing paused.");
        } else if (current.isStopped()) {
            log.info("BluOS playing stopped.");
        }
    }

    private void updateNowPlaying(BluOSStatus status) {
        if (status.isServiceEnabled()) {
            playService.updateNowPlaying(TrackPlay.fromStatus(status));
        }
    }
}
