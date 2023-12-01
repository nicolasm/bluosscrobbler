package com.nicolasm.bluosscrobbler.bluos.poller;

import com.nicolasm.bluosscrobbler.bluos.model.BluOSStatus;
import com.nicolasm.bluosscrobbler.bluos.model.TrackPlay;
import com.nicolasm.bluosscrobbler.bluos.service.BluOSStatusService;
import com.nicolasm.bluosscrobbler.bluos.service.TrackPlayService;
import com.nicolasm.service.bluosscrobbler.bluos.model.BluOSPlayingState;
import com.nicolasm.service.bluosscrobbler.bluos.model.BluOSRawStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static com.nicolasm.service.bluosscrobbler.bluos.model.BluOSPlayingState.PAUSE;
import static com.nicolasm.service.bluosscrobbler.bluos.model.BluOSPlayingState.PLAY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        BluOSStatusPoller.class,
        BluOSStatusService.class,
        TrackPlayService.class
})
class BluOSStatusPollerTest {
    @Autowired
    private BluOSStatusPoller poller;
    @MockBean
    private BluOSStatusService statusService;
    @MockBean
    private TrackPlayService playService;

    @BeforeEach
    void setUp() {
        Mockito.reset(statusService);
    }

    @Test
    void poll() {
        poller.poll();

        verify(statusService).getStatus();
    }

    @Test
    void testPolling() {
        mockStatusPoll(PLAY, "0");
        mockStatusLongPoll(PLAY, "etag", "new-etag");
        mockStatusLongPoll(PLAY, "new-etag", null);

        when(playService.isMarkedAsToBeScrobbled("etag")).thenReturn(true);

        poller.poll();

        verify(statusService).getStatus();
        verify(playService, times(2)).updateNowPlaying(any(TrackPlay.class));
        verify(playService, never()).markAsToBeScrobbled();
        verify(statusService).getStatus(100L, "etag");
        verify(playService).scrobble();
        verify(statusService).getStatus(90L, "new-etag");
    }

    @Test
    void testHalfPlayed() {
        mockStatusPoll(PLAY, "225");

        poller.poll();
        verify(statusService).getStatus();
        verify(playService).updateNowPlaying(any(TrackPlay.class));
        verify(playService).markAsToBeScrobbled();
    }

    @Test
    void testPausePlay() {
        mockStatusPoll(PAUSE, "10");
        mockStatusLongPoll(PLAY, "etag", "new-etag");
        mockStatusLongPoll(PLAY, "new-etag", null);

        poller.poll();
        verify(statusService).getStatus();
        verify(playService).updateNowPlaying(any(TrackPlay.class));
    }

    private void mockStatusPoll(BluOSPlayingState state, String secs) {
        BluOSRawStatus rawStatus = new BluOSRawStatus();
        rawStatus.setState(state);
        rawStatus.setSecs(secs);
        rawStatus.setTotlen("450");
        rawStatus.setEtag("etag");
        when(statusService.getStatus()).thenReturn(BluOSStatus.builder()
                .status(rawStatus)
                .serviceEnabled(true)
                .build());
    }

    private void mockStatusLongPoll(BluOSPlayingState state, String etagIn, String etagOut) {
        BluOSRawStatus rawStatus = new BluOSRawStatus();
        rawStatus.setState(state);
        rawStatus.setSecs("0");
        rawStatus.setTotlen("180");
        rawStatus.setEtag(etagOut);
        when(statusService.getStatus(anyLong(), ArgumentMatchers.eq(etagIn)))
                .thenReturn(Optional.ofNullable(etagOut)
                        .map(e -> BluOSStatus.builder()
                                .status(rawStatus)
                                .serviceEnabled(true).build()).orElse(null));
    }
}
