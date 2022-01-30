package com.nicolasm.bluosscrobbler.scrobbler.lastfm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.config.LastfmConfig;
import com.nicolasm.bluosscrobbler.scrobbler.model.ScrobblerTrackPlay;
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
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.util.DigestUtils;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        LastfmUserService.class,
        ObjectMapper.class,
        LastfmUserServiceTest.ServiceTestConfiguration.class
})
class LastfmUserServiceTest {
    private static final String API_URL = "https://ws.audioscrobbler.com/2.0";
    private static final String API_KEY = "api-key";
    private static final String SHARED_SECRET = "shared-secret";
    private static final String SESSION_KEY = "session-key";

    @Autowired
    private LastfmUserService service;

    private MockRestServiceServer mockServer;

    private ScrobblerTrackPlay play;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(service);
        play = ScrobblerTrackPlay.builder()
                .artist("Aranis")
                .album("II")
                .track("Mythra")
                .duration("304")
                .timestamp(OffsetDateTime.now().toEpochSecond())
                .build();
    }

    @Test
    void updateNowPlaying() {
        String method = "track.updateNowPlaying";
        String apiSig = DigestUtils.md5DigestAsHex(
                ("album" + play.getAlbum()
                        + "api_key" + API_KEY
                        + "artist" + play.getArtist()
                        + "duration" + play.getDuration()
                        + "method" + method
                        + "sk" + SESSION_KEY
                        + "track" + play.getTrack()
                        + SHARED_SECRET).getBytes());

        Map<String, String> map = new HashMap<>();
        map.put("album", play.getAlbum());
        map.put("artist", play.getArtist());
        map.put("track", play.getTrack());
        map.put("duration", play.getDuration());
        map.put("method", method);
        map.put("format", "json");
        map.put("api_key", API_KEY);
        map.put("api_sig", apiSig);
        map.put("sk", SESSION_KEY);

        mockServer.expect(requestTo(API_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(MockRestRequestMatchers.content().formDataContains(map))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        service.updateNowPlaying(play);
    }

    @Test
    void addPlayedTrack() {
        String method = "track.scrobble";
        String apiSig = DigestUtils.md5DigestAsHex(
                ("album" + play.getAlbum()
                        + "api_key" + API_KEY
                        + "artist" + play.getArtist()
                        + "duration" + play.getDuration()
                        + "method" + method
                        + "sk" + SESSION_KEY
                        + "timestamp" + play.getTimestamp()
                        + "track" + play.getTrack()
                        + SHARED_SECRET).getBytes());

        Map<String, String> map = new HashMap<>();
        map.put("album", play.getAlbum());
        map.put("artist", play.getArtist());
        map.put("track", play.getTrack());
        map.put("duration", play.getDuration());
        map.put("method", method);
        map.put("format", "json");
        map.put("api_key", API_KEY);
        map.put("api_sig", apiSig);
        map.put("sk", SESSION_KEY);
        map.put("timestamp", String.valueOf(play.getTimestamp()));

        mockServer.expect(requestTo(API_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(MockRestRequestMatchers.content().formDataContains(map))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        service.addPlayedTrack(play);

    }

    @TestConfiguration
    static class ServiceTestConfiguration {
        @Bean
        LastfmConfig config() {
            return LastfmConfig.builder()
                    .enabled(true)
                    .apiUrl(API_URL)
                    .apiKey(API_KEY)
                    .sessionKey(SESSION_KEY)
                    .sharedSecret(SHARED_SECRET)
                    .build();
        }

    }
}