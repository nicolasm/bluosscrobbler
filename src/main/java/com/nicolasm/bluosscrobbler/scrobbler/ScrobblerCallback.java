package com.nicolasm.bluosscrobbler.scrobbler;

import com.nicolasm.service.bluosscrobbler.bluos.model.StatusType;

public interface ScrobblerCallback {
    boolean isEnabled();

    void notifyNowPlaying(StatusType status);

    void scrobbleTrack();
}
