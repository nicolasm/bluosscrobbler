package com.nicolasm.bluosscrobbler.scrobbler.lastfm.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LastfmUserParameters {
    private String token;
    private String artist;
    private String track;
    private String album;
    private String duration;
    private String timestamp;
}
