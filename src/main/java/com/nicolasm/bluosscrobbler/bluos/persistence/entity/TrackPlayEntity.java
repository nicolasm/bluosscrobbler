package com.nicolasm.bluosscrobbler.bluos.persistence.entity;

import com.nicolasm.bluosscrobbler.bluos.model.ScrobbleStatus;
import com.nicolasm.bluosscrobbler.bluos.model.TrackPlayStatus;
import com.nicolasm.bluosscrobbler.scrobbler.model.Scrobbler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.OffsetDateTime;

import static com.nicolasm.bluosscrobbler.scrobbler.model.Scrobbler.LASTFM;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "track_play")
public class TrackPlayEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "artist")
    private String artist;
    @Column(name = "album")
    private String album;
    @Column(name = "track")
    private String track;
    @Column(name = "duration")
    private String duration;
    @Column(name = "timestamp")
    private long timestamp;

    @Column(name = "play_status")
    @Enumerated(EnumType.STRING)
    private TrackPlayStatus playStatus;

    @Column(name = "lastfm_scrobble_status")
    @Enumerated(EnumType.STRING)
    private ScrobbleStatus lastfmScrobbleStatus;

    @Column(name = "etag")
    private String etag;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    public void setScrobblerStatus(Scrobbler scrobbler, ScrobbleStatus status) {
        if (scrobbler == LASTFM) {
            this.setLastfmScrobbleStatus(status);
        }
    }
}
