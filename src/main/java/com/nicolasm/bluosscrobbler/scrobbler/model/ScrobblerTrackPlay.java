package com.nicolasm.bluosscrobbler.scrobbler.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ScrobblerTrackPlay {
    private String artist;
    private String album;
    private String track;
    private String duration;
    private long timestamp;
}
