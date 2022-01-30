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

        String etag = status.getEtag();
        do {
            logPollStatus(status);
            if (shouldBeScrobbled(status, etag)) {
                playService.markAsToBeScrobbled();
            }

            BluOSStatus previousStatus = status;
            status = service.getStatus(status.computePollingTimeout(), status.getEtag());
            if (status != null) {
                if (shouldScrobble(status, etag)) {
                    playService.scrobble();
                }

                etag = handlePlayingState(previousStatus, status, etag);
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
    }

    private boolean shouldBeScrobbled(BluOSStatus status, String etag) {
        return status.isSameTrackPlay(etag)
                && !playService.isMarkedAsToBeScrobbled(etag)
                && status.isHalfPlayed()
                && status.isServiceEnabled();
    }

    private boolean shouldScrobble(BluOSStatus status, String etag) {
        return status.hasStatusChanged(etag)
                && status.isServiceEnabled();
    }

    private String handlePlayingState(BluOSStatus previous, BluOSStatus current, String etag) {
        if (current.isNewTrackPlay(etag)) {
            updateNowPlaying(current);
            return current.getEtag();
        }

        if (current.isPlaying()
                && previous.isPaused()) {
            log.info("BluOS playing on.");
            updateNowPlayingAfterPause(current);
        } else if (current.isPaused()) {
            log.info("BluOS playing paused.");
        } else if (current.isStopped()) {
            log.info("BluOS playing stopped.");
            playService.deletePlaying();
        }
        return etag;
    }

    private void updateNowPlaying(BluOSStatus status) {
        if (status.isServiceEnabled()) {
            playService.updateNowPlaying(TrackPlay.fromStatus(status));
        }
    }

    private void updateNowPlayingAfterPause(BluOSStatus status) {
        if (status.isServiceEnabled()) {
            playService.updateNowPlaying();
        }
    }
}
