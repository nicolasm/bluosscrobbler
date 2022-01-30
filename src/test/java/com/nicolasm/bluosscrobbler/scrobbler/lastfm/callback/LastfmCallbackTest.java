package com.nicolasm.bluosscrobbler.scrobbler.lastfm.callback;

import com.nicolasm.bluosscrobbler.scrobbler.lastfm.config.LastfmConfig;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.service.LastfmUserService;
import com.nicolasm.bluosscrobbler.scrobbler.model.ScrobblerTrackPlay;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        LastfmCallback.class,
        LastfmUserService.class,
        LastfmCallbackTest.CallbackTestConfiguration.class
})
class LastfmCallbackTest {
    @Autowired
    private LastfmCallback callback;
    @MockBean
    private LastfmUserService service;

    @Test
    void isEnabled() {
        assertThat(callback.isEnabled()).isTrue();
    }

    @Test
    void updateNowPlaying() {
        callback.updateNowPlaying(new ScrobblerTrackPlay());
        verify(service).updateNowPlaying(any(ScrobblerTrackPlay.class));
    }

    @Test
    void scrobble() {
        callback.scrobble(new ScrobblerTrackPlay());
        verify(service).addPlayedTrack(any(ScrobblerTrackPlay.class));
    }

    @TestConfiguration
    static class CallbackTestConfiguration {
        @Bean
        LastfmConfig config() {
            return LastfmConfig.builder()
                    .enabled(true)
                    .build();
        }

    }

}