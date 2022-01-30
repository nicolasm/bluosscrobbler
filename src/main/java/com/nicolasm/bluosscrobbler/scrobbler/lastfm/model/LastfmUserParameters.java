package com.nicolasm.bluosscrobbler.scrobbler.lastfm.model;

import com.nicolasm.bluosscrobbler.scrobbler.model.ScrobblerTrackPlay;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LastfmUserParameters {
    private String callbackUrl;
    private String token;
    private String artist;
    private String track;
    private String album;
    private String duration;
    private String timestamp;

    public static LastfmUserParameters fromPlay(ScrobblerTrackPlay play) {
        return LastfmUserParameters.builder()
                .artist(play.getArtist())
                .album(play.getAlbum())
                .track(play.getTrack())
                .duration(play.getDuration())
                .timestamp(String.valueOf(play.getTimestamp()))
                .build();
    }
}
