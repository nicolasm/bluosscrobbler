package com.nicolasm.bluosscrobbler.scrobbler.lastfm.callback;

import com.nicolasm.bluosscrobbler.scrobbler.ScrobblerCallback;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.config.LastfmConfig;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.service.LastfmUserService;
import com.nicolasm.service.bluosscrobbler.bluos.model.StatusType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LastfmCallback implements ScrobblerCallback {
    private final LastfmConfig config;
    private final LastfmUserService service;

    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public void notifyNowPlaying(StatusType status) {
        log.info("Notify now playing to Last.fm");
        service.updateNowPlaying(status);
    }

    @Override
    public void scrobbleTrack() {
        log.info("Scrobble track to Last.fm");
    }
}
