package com.nicolasm.bluosscrobbler.scrobbler.lastfm.callback;

import com.nicolasm.bluosscrobbler.scrobbler.ScrobblingCallback;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.config.LastfmConfig;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.service.LastfmUserService;
import com.nicolasm.bluosscrobbler.scrobbler.model.Scrobbler;
import com.nicolasm.bluosscrobbler.scrobbler.model.ScrobblerTrackPlay;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.nicolasm.bluosscrobbler.scrobbler.model.Scrobbler.LASTFM;

@Component
@RequiredArgsConstructor
@Slf4j
public class LastfmCallback implements ScrobblingCallback {
    private final LastfmConfig config;
    private final LastfmUserService service;

    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public Scrobbler getType() {
        return LASTFM;
    }

    @Override
    public void updateNowPlaying(ScrobblerTrackPlay play) {
        log.info("Update now playing to Last.fm");
        service.updateNowPlaying(play);
    }

    @Override
    public void scrobble(ScrobblerTrackPlay play) {
        log.info("Scrobble track to Last.fm");
        service.addPlayedTrack(play);
    }
}
