package com.nicolasm.bluosscrobbler.scrobbler.lastfm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.config.LastfmConfig;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.exception.LastfmException;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmAuthGetSessionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.DigestUtils;

import java.io.IOException;

import static com.nicolasm.bluosscrobbler.testutils.TestUtils.readResourceToString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        LastfmAuthService.class,
        ObjectMapper.class,
        LastfmAuthServiceTest.ServiceTestConfiguration.class
})
class LastfmAuthServiceTest {
    private static final String CALLBACK_URL = "http://localhost:18123/v1/bluossrobbler/lastfm/auth/session";
    private static final String AUTH_URL = "https://www.last.fm/api/auth";
    private static final String API_URL = "https://ws.audioscrobbler.com/2.0";
    private static final String TOKEN = "token";
    private static final String API_KEY = "api-key";
    private static final String SHARED_SECRET = "shared-secret";
    private static final String AUTH_GET_SESSION_RESPONSE = "lastfm/service/auth/auth-getSession-response.json";
    private static final String AUTH_GET_SESSION_ERROR_RESPONSE = "lastfm/service/auth/auth-getSession-error-response.json";

    @Autowired
    private LastfmAuthService service;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(service);
    }

    @Test
    void requestAuth() {
        ResponseEntity<Object> entity = service.requestAuth(CALLBACK_URL);
        assertThat(entity.getHeaders().getLocation()).isNotNull();
        assertThat(entity.getHeaders().getLocation())
                .hasToString(String.format("%s?api_key=%s&cb=%s", AUTH_URL, API_KEY, CALLBACK_URL));
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.SEE_OTHER);
    }

    @Test
    void getAuthSession() throws IOException {
        String method = "auth.getSession";
        String apiSig = DigestUtils.md5DigestAsHex((
                "api_key" + API_KEY
                        + "method" + method
                        + "token" + TOKEN
                        + SHARED_SECRET).getBytes());
        String expectedUrl =
                API_URL
                        + "?api_key=" + API_KEY
                        + "&api_sig=" + apiSig
                        + "&format=json"
                        + "&method=" + method
                        + "&token=" + TOKEN;
        String jsonResponse = readResourceToString(AUTH_GET_SESSION_RESPONSE);
        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        LastfmAuthGetSessionResponse response = service.getAuthSession(TOKEN);
        assertThat(response).isNotNull();
        assertThat(response.getSession()).isNotNull();
        assertThat(response.getSession().getKey()).isNotNull();
        assertThat(response.getSession().getKey()).isEqualTo("session-key");
    }

    @Test
    void errorOnGetAuthSession() throws IOException {
        mockServer.expect(method(HttpMethod.GET))
                .andRespond(withUnauthorizedRequest()
                        .body(readResourceToString(AUTH_GET_SESSION_ERROR_RESPONSE))
                        .contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> service.getAuthSession(TOKEN))
                .isInstanceOf(LastfmException.class)
                .hasMessageContaining("Unauthorized Token - This token has not been issued");
    }

    @TestConfiguration
    static class ServiceTestConfiguration {
        @Bean
        LastfmConfig config() {
            return LastfmConfig.builder()
                    .enabled(true)
                    .apiUrl(API_URL)
                    .authUrl(AUTH_URL)
                    .apiKey(API_KEY)
                    .sharedSecret(SHARED_SECRET)
                    .build();
        }

    }

}