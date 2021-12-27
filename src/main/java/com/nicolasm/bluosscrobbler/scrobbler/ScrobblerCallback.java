package com.nicolasm.bluosscrobbler.scrobbler;

public interface ScrobblerCallback {
    boolean isEnabled();

    void notifyNowPlaying();

    void scrobbleTrack();
}
