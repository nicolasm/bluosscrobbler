package com.nicolasm.bluosscrobbler.scrobbler.lastfm.service;

import com.nicolasm.bluosscrobbler.scrobbler.lastfm.config.LastfmConfig;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmAuthEndpoint;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmAuthGetSessionResponse;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmAuthTokenResponse;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmRequestAuthUrlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LastfmAuthService {
    private final LastfmConfig config;
    private final RestTemplate restTemplate = new RestTemplate();

    public LastfmAuthTokenResponse getAuthToken() {
        LastfmAuthEndpoint endpoint = LastfmAuthEndpoint.auth_getToken;
        ResponseEntity<LastfmAuthTokenResponse> response =
                restTemplate.getForEntity(endpoint.getEndpoint(config),
                        LastfmAuthTokenResponse.class, endpoint.buildUrlVariables(config));
        return response.getBody();
    }

    public LastfmRequestAuthUrlResponse requestAuth() {
        LastfmAuthTokenResponse tokenResponse = getAuthToken();
        LastfmAuthEndpoint endpoint = LastfmAuthEndpoint.request_auth;

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint.getEndpoint(config));
        Map<String, Object> urlVariables = endpoint.buildUrlVariables(config, tokenResponse.getToken());

        URI uri = builder.buildAndExpand(urlVariables).toUri();
        return new LastfmRequestAuthUrlResponse(uri.toString(), tokenResponse.getToken());
    }

    public LastfmAuthGetSessionResponse getAuthSession(String tokenValue) {
        LastfmAuthEndpoint endpoint = LastfmAuthEndpoint.auth_getSession;
        ResponseEntity<LastfmAuthGetSessionResponse> response =
                restTemplate.getForEntity(endpoint.getEndpoint(config),
                        LastfmAuthGetSessionResponse.class, endpoint.buildUrlVariables(config, tokenValue));
        return response.getBody();
    }
}
