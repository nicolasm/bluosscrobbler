package com.nicolasm.bluosscrobbler.bluos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nicolasm.service.bluosscrobbler.bluos.model.BluOSRawStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.apache.commons.codec.digest.DigestUtils;

import static com.nicolasm.service.bluosscrobbler.bluos.model.BluOSPlayingState.PAUSE;
import static com.nicolasm.service.bluosscrobbler.bluos.model.BluOSPlayingState.PLAY;
import static com.nicolasm.service.bluosscrobbler.bluos.model.BluOSPlayingState.STOP;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class BluOSStatus {
    private static final int FOUR_MINUTES_IN_SECONDS = 4 * 60;
    private static final int DEFAULT_TIMEOUT = 100;

    @Delegate
    @JsonIgnore
    private BluOSRawStatus status;
    private boolean serviceEnabled;

    public String md5Checksum() {
        String buffer = status.getPid()
                + status.getSong()
                + status.getArtist()
                + status.getAlbum()
                + status.getName();
        return DigestUtils.md5Hex(buffer.getBytes());
    }

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

    public boolean isNewTrackPlay(BluOSStatus previous) {
        return !this.md5Checksum().equals(previous.md5Checksum());
    }

    public boolean isPaused() {
        return status.getState() == PAUSE;
    }

    public boolean isPlaying() {
        return status.getState() == PLAY;
    }

    public boolean isStopped() {
        return status.getState() == STOP;
    }

    public long computePollingTimeout() {
        return isPlaying() && (getPlayedLength() < getScrobbleThreshold())
                ? Math.min(getScrobbleThreshold() - getPlayedLength(), DEFAULT_TIMEOUT)
                : DEFAULT_TIMEOUT;
    }

    private long getScrobbleThreshold() {
        return Math.min(getHalfLength(), FOUR_MINUTES_IN_SECONDS);
    }
}
