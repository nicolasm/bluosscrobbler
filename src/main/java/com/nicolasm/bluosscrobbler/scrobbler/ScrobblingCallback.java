package com.nicolasm.bluosscrobbler.scrobbler;

import com.nicolasm.bluosscrobbler.scrobbler.model.Scrobbler;
import com.nicolasm.bluosscrobbler.scrobbler.model.ScrobblerTrackPlay;

public interface ScrobblingCallback {
    boolean isEnabled();

    Scrobbler getType();

    void updateNowPlaying(ScrobblerTrackPlay play);

    void scrobble(ScrobblerTrackPlay play);
}
