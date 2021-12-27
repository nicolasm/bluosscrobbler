package com.nicolasm.bluosscrobbler.scrobbler;

public interface ScrobblerCallback {
    void notifyNowPlaying();

    void scrobbleTrack();
}
