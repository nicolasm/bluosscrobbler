package com.nicolasm.bluosscrobbler.bluos.service;

import com.nicolasm.bluosscrobbler.bluos.model.TrackPlay;
import com.nicolasm.bluosscrobbler.bluos.persistence.entity.TrackPlayEntity;
import com.nicolasm.bluosscrobbler.bluos.persistence.repository.TrackPlayRepository;
import com.nicolasm.bluosscrobbler.scrobbler.ScrobblingCallback;
import com.nicolasm.bluosscrobbler.scrobbler.model.Scrobbler;
import com.nicolasm.bluosscrobbler.scrobbler.model.ScrobblerTrackPlay;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

import static com.nicolasm.bluosscrobbler.bluos.model.ScrobbleStatus.SCROBBLED;
import static com.nicolasm.bluosscrobbler.bluos.model.ScrobbleStatus.TO_BE_SCROBBLED;
import static com.nicolasm.bluosscrobbler.bluos.model.TrackPlayStatus.PLAYED;
import static com.nicolasm.bluosscrobbler.bluos.model.TrackPlayStatus.PLAYING;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackPlayService {
    private final List<ScrobblingCallback> callbacks;
    private final TrackPlayRepository repository;

    @Transactional
    public void updateNowPlaying(TrackPlay play) {
        log.info("New playing track is '{} - {} - {}'", play.getArtist(), play.getAlbum(), play.getTrack());

        TrackPlayEntity playing = repository.findLastfmPlaying();
        if (playing == null) {
            playing = TrackPlayEntity.builder().playStatus(PLAYING).build();
        } else if (!playing.getMd5Checksum().equals(play.getMd5Checksum())
                && playing.getLastfmScrobbleStatus() == SCROBBLED) {
            playing.setLastfmScrobbleStatus(null);
            playing.setTimestamp(play.getTimestamp());
        }
        if (playing.getTimestamp() == 0L) {
            playing.setTimestamp(play.getTimestamp());
        }

        playing.setArtist(play.getArtist());
        playing.setAlbum(play.getAlbum());
        playing.setTrack(play.getTrack());
        playing.setDuration(play.getDuration());
        playing.setMd5Checksum(play.getMd5Checksum());
        playing.setCreatedAt(OffsetDateTime.now());
        playing.setUpdatedAt(OffsetDateTime.now());
        repository.save(playing);

        callbacks.stream()
                .filter(ScrobblingCallback::isEnabled)
                .forEach(callback -> callback.updateNowPlaying(ScrobblerTrackPlay.builder()
                        .artist(play.getArtist())
                        .album(play.getAlbum())
                        .track(play.getTrack())
                        .duration(play.getDuration())
                        .timestamp(play.getTimestamp())
                        .build()));
    }

    @Transactional
    public void markAsPlayed(String md5Checksum) {
        TrackPlayEntity play = repository.findLastfmByMd5Checksum(md5Checksum);
        if (play != null) {
            play.setPlayStatus(PLAYED);
            repository.save(play);
        }
    }

    @Transactional
    public void scrobble(TrackPlay play) {
        TrackPlayEntity entity = repository.findLastfmByMd5Checksum(play.getMd5Checksum());

            callbacks.stream()
                    .filter(ScrobblingCallback::isEnabled)
                    .forEach(callback -> {
                        if (callback.getType() == Scrobbler.LASTFM) {
                            if (entity.getLastfmScrobbleStatus() == null) {
                                scrobbleCurrent(play, callback, entity);
                            }

                            repository.deleteScrobbled();
                            scrobbleToBeScrobbled(callback);
                        }
                    });

    }

    private void scrobbleCurrent(TrackPlay play, ScrobblingCallback callback, TrackPlayEntity entity) {
        if (callback.scrobble(ScrobblerTrackPlay.builder()
                .artist(play.getArtist())
                .album(play.getAlbum())
                .track(play.getTrack())
                .duration(play.getDuration())
                .timestamp(play.getTimestamp())
                .build())) {
            entity.setLastfmScrobbleStatus(SCROBBLED);
        } else {
            entity.setLastfmScrobbleStatus(TO_BE_SCROBBLED);
        }
        repository.save(entity);
    }

    private void scrobbleToBeScrobbled(ScrobblingCallback callback) {
        repository.findLastfmToBeScrobbled().forEach(p -> {
            if (callback.scrobble(ScrobblerTrackPlay.builder()
                    .artist(p.getArtist())
                    .album(p.getAlbum())
                    .track(p.getTrack())
                    .duration(p.getDuration())
                    .timestamp(p.getTimestamp())
                    .build())) {
                p.setLastfmScrobbleStatus(SCROBBLED);
            }
            repository.save(p);
        });
    }
}
