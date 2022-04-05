package com.nicolasm.bluosscrobbler.bluos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nicolasm.service.bluosscrobbler.bluos.model.BluOSRawStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class BluOSStatus {
    private static final int FOUR_MINUTES_IN_SECONDS = 4 * 60;

    @Delegate
    @JsonIgnore
    private BluOSRawStatus status;
    private boolean serviceEnabled;

    public long getTotalLength() {
        return Long.parseLong(status.getTotlen());
    }

    public boolean shouldBeScrobbled() {
        return hasBeenHalfPlayed()
                || hasBeenPlayedForFourMinutes();
    }

    private boolean hasBeenPlayedForFourMinutes() {
        return getPlayedLength() >= FOUR_MINUTES_IN_SECONDS;
    }

    private boolean hasBeenHalfPlayed() {
        return getPlayedLength() >= getHalfLength();
    }

    public long getHalfLength() {
        return getTotalLength() / 2;
    }

    public long getPlayedLength() {
        return Long.parseLong(status.getSecs());
    }

    public long getRemainingLength() {
        return getTotalLength() + getPlayedLength();
    }

    public boolean hasStatusChanged(String previousEtag) {
        return isNewTrackPlay(previousEtag)
                || isStopped();
    }

    public boolean isNewTrackPlay(String previousEtag) {
        return !StringUtils.equals(previousEtag, status.getEtag())
                && isPlaying();
    }

    public boolean isSameTrackPlay(String previousEtag) {
        return StringUtils.equals(previousEtag, status.getEtag())
                && isPlaying();
    }

    public boolean isPaused() {
        return status.getState().equals("pause");
    }

    public boolean isPlaying() {
        return status.getState().equals("play");
    }

    public boolean isStopped() {
        return status.getState().equals("stop");
    }

    public long computePollingTimeout() {
        return isPlaying() && (getPlayedLength() < getHalfLength())
                ? Math.min(getHalfLength() - getPlayedLength(), 100)
                : 100;
    }
}
