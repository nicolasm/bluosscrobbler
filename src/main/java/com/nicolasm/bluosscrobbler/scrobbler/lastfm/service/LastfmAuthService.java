package com.nicolasm.bluosscrobbler.scrobbler.lastfm.service;

import com.nicolasm.bluosscrobbler.scrobbler.lastfm.config.LastfmConfig;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LastfmAuthService {
    private final LastfmConfig config;
    private final RestTemplate restTemplate = new RestTemplate();

    public LastfmAuthTokenResponse getAuthToken() {
        try {
            LastfmEndpoint endpoint = LastfmEndpoint.auth_getToken;

            ResponseEntity<LastfmAuthTokenResponse> response = restTemplate.getForEntity(endpoint.getEndpoint(config),
                    LastfmAuthTokenResponse.class, endpoint.buildUrlVariables(config, null));
            return response.getBody();
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public LastfmRequestAuthUrlResponse requestAuth() {
        try {
            LastfmAuthTokenResponse tokenResponse = getAuthToken();
            LastfmEndpoint endpoint = LastfmEndpoint.request_auth;

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint.getEndpoint(config));
            Map<String, String> urlVariables = endpoint.buildUrlVariables(config,
                    LastfmUserParameters.builder().token(tokenResponse.getToken()).build());

            URI uri = builder.buildAndExpand(urlVariables).toUri();
            return new LastfmRequestAuthUrlResponse(uri.toString(), tokenResponse.getToken());
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public LastfmAuthGetSessionResponse getAuthSession(String tokenValue) {
        try {
            LastfmEndpoint endpoint = LastfmEndpoint.auth_getSession;
            ResponseEntity<LastfmAuthGetSessionResponse> response =
                    restTemplate.getForEntity(endpoint.getEndpoint(config),
                            LastfmAuthGetSessionResponse.class, endpoint.buildUrlVariables(config,
                                    LastfmUserParameters.builder().token(tokenValue).build()));
            return response.getBody();
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
