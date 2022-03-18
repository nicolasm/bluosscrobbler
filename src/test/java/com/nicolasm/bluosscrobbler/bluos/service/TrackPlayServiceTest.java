package com.nicolasm.bluosscrobbler.bluos.service;

import com.google.common.collect.ImmutableList;
import com.nicolasm.bluosscrobbler.bluos.model.TrackPlay;
import com.nicolasm.bluosscrobbler.bluos.persistence.entity.TrackPlayEntity;
import com.nicolasm.bluosscrobbler.bluos.persistence.repository.TrackPlayRepository;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.callback.LastfmCallback;
import com.nicolasm.bluosscrobbler.scrobbler.model.Scrobbler;
import com.nicolasm.bluosscrobbler.scrobbler.model.ScrobblerTrackPlay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        TrackPlayService.class,
        LastfmCallback.class
})
class TrackPlayServiceTest {
    @Autowired
    private TrackPlayService service;
    @MockBean
    private LastfmCallback callback;
    @MockBean
    private TrackPlayRepository repository;

    @BeforeEach
    void setUp() {
        when(callback.getType()).thenReturn(Scrobbler.LASTFM);
        when(callback.isEnabled()).thenReturn(true);
    }

    @Test
    void updateNowPlaying() {
        TrackPlay play = TrackPlay.builder().build();
        service.updateNowPlaying(play);

        verify(repository).findPlaying();
        verify(repository).save(any(TrackPlayEntity.class));
        verify(callback).isEnabled();
        verify(callback).updateNowPlaying(any(ScrobblerTrackPlay.class));
    }

    @Test
    void markAsToBeScrobbled() {
        String etag = "etag";
        TrackPlayEntity entity = new TrackPlayEntity();
        when(repository.findPlaying()).thenReturn(entity);
        when(repository.findPlayedByEtag(etag)).thenReturn(entity);

        service.markAsToBeScrobbled();

        verify(repository).findPlaying();
        verify(repository).save(any(TrackPlayEntity.class));
        assertThat(service.isMarkedAsToBeScrobbled(etag)).isTrue();
    }

    @Test
    void scrobble() {
        when(repository.findLastfmToBeScrobbled())
                .thenReturn(ImmutableList.of(new TrackPlayEntity(), new TrackPlayEntity()));

        service.scrobble();

        verify(repository).deleteScrobbled();
        verify(callback).isEnabled();
        verify(repository, times(2)).save(any(TrackPlayEntity.class));
        verify(callback, times(2)).scrobble(any(ScrobblerTrackPlay.class));
    }
}