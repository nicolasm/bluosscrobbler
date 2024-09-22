package com.nicolasm.bluosscrobbler.bluos.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.time.ZoneId;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class TrackPlay {
    private static final ZoneId ZONE_ID = ZoneId.of("Europe/Paris");

    private String artist;
    private String album;
    private String track;
    private String duration;
    private long timestamp;
    private String md5Checksum;

    public static TrackPlay fromStatus(BluOSStatus status) {
        return TrackPlay.builder()
                .artist(status.getArtist())
                .album(status.getAlbum())
                .track(status.getName())
                .duration(status.getTotlen())
                .timestamp(OffsetDateTime.now(ZONE_ID).toEpochSecond() - Long.parseLong(status.getSecs()))
                .md5Checksum(status.md5Checksum())
                .build();
    }
}
