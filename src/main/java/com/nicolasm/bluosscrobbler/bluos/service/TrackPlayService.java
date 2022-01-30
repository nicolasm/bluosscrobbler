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
import java.util.Optional;

import static com.nicolasm.bluosscrobbler.bluos.model.ScrobbleStatus.DISABLED;
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

        TrackPlayEntity playing = repository.findPlaying();
        if (playing == null) {
            playing = TrackPlayEntity.builder().playStatus(PLAYING).build();
        }
        playing.setArtist(play.getArtist());
        playing.setAlbum(play.getAlbum());
        playing.setTrack(play.getTrack());
        playing.setDuration(play.getDuration());
        playing.setEtag(play.getEtag());
        playing.setTimestamp(play.getTimestamp());
        OffsetDateTime now = OffsetDateTime.now();
        playing.setCreatedAt(now);
        playing.setUpdatedAt(now);
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

    public void updateNowPlaying() {
        callbacks.stream()
                .filter(ScrobblingCallback::isEnabled)
                .forEach(callback -> {
                    TrackPlayEntity playing = repository.findPlaying();
                    callback.updateNowPlaying(ScrobblerTrackPlay.builder()
                            .artist(playing.getArtist())
                            .album(playing.getAlbum())
                            .track(playing.getTrack())
                            .duration(playing.getDuration())
                            .timestamp(playing.getTimestamp())
                            .build());
                });
    }

    @Transactional
    public void markAsToBeScrobbled() {
        Optional.ofNullable(repository.findPlaying()).ifPresent(p -> {
            log.info("Mark current track '{} - {} - {}' as to be scrobbled.",
                    p.getArtist(), p.getAlbum(), p.getTrack());

            OffsetDateTime now = OffsetDateTime.now();
            TrackPlayEntity played = TrackPlayEntity.builder()
                    .artist(p.getArtist())
                    .album(p.getAlbum())
                    .track(p.getTrack())
                    .duration(p.getDuration())
                    .timestamp(p.getTimestamp())
                    .playStatus(PLAYED)
                    .etag(p.getEtag())
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            callbacks.forEach(callback ->
                    played.setScrobblerStatus(callback.getType(),
                            callback.isEnabled() ? TO_BE_SCROBBLED : DISABLED));
            repository.save(played);
        });
    }

    public boolean isMarkedAsToBeScrobbled(String etag) {
        return repository.findPlayedByEtag(etag) != null;
    }

    @Transactional
    public void scrobble() {
        callbacks.stream()
                .filter(ScrobblingCallback::isEnabled)
                .forEach(callback -> {
                    if (callback.getType() == Scrobbler.LASTFM) {
                        repository.findLastfmToBeScrobbled().forEach(play -> {
                            callback.scrobble(ScrobblerTrackPlay.builder()
                                    .artist(play.getArtist())
                                    .album(play.getAlbum())
                                    .track(play.getTrack())
                                    .duration(play.getDuration())
                                    .timestamp(play.getTimestamp())
                                    .build());
                            play.setLastfmScrobbleStatus(SCROBBLED);
                            repository.save(play);
                        });
                    }
                });
    }

    @Transactional
    public void deletePlaying() {
        TrackPlayEntity playing = repository.findPlaying();
        Optional.ofNullable(playing).ifPresent(repository::delete);
    }
}