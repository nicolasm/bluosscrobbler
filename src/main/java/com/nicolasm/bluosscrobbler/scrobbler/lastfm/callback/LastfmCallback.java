package com.nicolasm.bluosscrobbler.scrobbler.lastfm.callback;

import com.nicolasm.bluosscrobbler.scrobbler.ScrobblerCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LastfmCallback implements ScrobblerCallback {
    @Override
    public void notifyNowPlaying() {
        log.info("Notify now playing to Last.fm");
    }

    @Override
    public void scrobbleTrack() {
        log.info("Scrobble track to Last.fm");
    }
}
