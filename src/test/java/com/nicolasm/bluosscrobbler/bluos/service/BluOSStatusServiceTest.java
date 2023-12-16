package com.nicolasm.bluosscrobbler.bluos.service;

import com.google.common.collect.ImmutableList;
import com.nicolasm.bluosscrobbler.bluos.config.BluOSConfig;
import com.nicolasm.bluosscrobbler.bluos.model.BluOSStatus;
import com.nicolasm.service.bluosscrobbler.bluos.model.BluOSPlayingState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;

import java.io.IOException;

import static com.nicolasm.bluosscrobbler.testutils.TestUtils.readResourceToString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        BluOSStatusService.class,
        BluOSStatusServiceTest.ServiceTestConfiguration.class
})
class BluOSStatusServiceTest {
    private static final String PLAYING_RESPONSE = "bluos/service/polling-response-playing.xml";
    private static final String PAUSED_RESPONSE = "bluos/service/polling-response-paused.xml";
    private static final String STOPPED_RESPONSE = "bluos/service/polling-response-stopped.xml";
    private static final String LONG_POLLING_RESPONSE = "bluos/service/long-polling-response-playing.xml";

    @Autowired
    private BluOSStatusService service;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(service);
    }

    @Test
    void getPlayingStatus() throws IOException {
        mockServer.expect(requestTo("http://host:18700/Status"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(readResourceToString(PLAYING_RESPONSE), MediaType.TEXT_XML));

        BluOSStatus status = service.getStatus();
        assertThat(status).isNotNull();
        assertThat(status.getEtag()).isNotBlank();
        assertThat(status.isPlaying()).isTrue();
        assertThat(status.isPaused()).isFalse();
        assertThat(status.isStopped()).isFalse();
        assertThat(status.shouldBeScrobbled()).isFalse();
        assertThat(status.isNewTrackPlay("old-etag")).isTrue();
        assertThat(status.getPlayedLength()).isZero();
    }

    @Test
    void getPausedStatus() throws IOException {
        mockServer.expect(requestTo("http://host:18700/Status"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(readResourceToString(PAUSED_RESPONSE), MediaType.TEXT_XML));

        BluOSStatus status = service.getStatus();
        assertThat(status).isNotNull();
        assertThat(status.getEtag()).isNotBlank();
        assertThat(status.isPlaying()).isFalse();
        assertThat(status.isPaused()).isTrue();
        assertThat(status.isStopped()).isFalse();
        assertThat(status.shouldBeScrobbled()).isTrue();
        assertThat(status.isNewTrackPlay("old-etag")).isFalse();
        assertThat(status.getPlayedLength()).isEqualTo(298L);
    }

    @Test
    void getStoppedStatus() throws IOException {
        mockServer.expect(requestTo("http://host:18700/Status"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(readResourceToString(STOPPED_RESPONSE), MediaType.TEXT_XML));

        BluOSStatus status = service.getStatus();
        assertThat(status).isNotNull();
        assertThat(status.getEtag()).isNotBlank();
        assertThat(status.isPlaying()).isFalse();
        assertThat(status.isPaused()).isFalse();
        assertThat(status.isStopped()).isTrue();
        assertThat(status.shouldBeScrobbled()).isFalse();
        assertThat(status.isNewTrackPlay("old-etag")).isFalse();
        assertThat(status.getPlayedLength()).isZero();
    }

    @Test
    void testBluOSNotAvailable() {
        mockServer.expect(requestTo("http://host:18700/Status"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        assertThat(service.getStatus()).isNull();
    }

    @Test
    void testLongPolling() throws IOException {
        String etag = "720a0cf5c7e777862735f19bc2300a25";
        mockServer.expect(requestTo(String.format("http://host:18700/Status?timeout=100&etag=%s", etag)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(readResourceToString(LONG_POLLING_RESPONSE), MediaType.TEXT_XML));

        BluOSStatus status = service.getStatus(100, etag);
        assertThat(status).isNotNull();
        assertThat(status.getEtag()).isNotBlank();
        assertThat(status.isPlaying()).isTrue();
        assertThat(status.isPaused()).isFalse();
        assertThat(status.isStopped()).isFalse();
        assertThat(status.shouldBeScrobbled()).isFalse();
        assertThat(status.isNewTrackPlay(etag)).isFalse();
        assertThat(status.getPlayedLength()).isEqualTo(236L);
    }

    @TestConfiguration
    static class ServiceTestConfiguration {
        @Bean
        BluOSConfig config() {
            return BluOSConfig.builder()
                    .host("host")
                    .port(18700)
                    .excludedServices(ImmutableList.of("Spotify"))
                    .build();
        }

    }
}
