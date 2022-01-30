package com.nicolasm.bluosscrobbler.scrobbler.lastfm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.config.LastfmConfig;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.exception.LastfmException;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmAuthGetSessionResponse;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmEndpoint;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUserParameters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.support.RestGatewaySupport;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LastfmAuthService extends RestGatewaySupport {
    private final LastfmConfig config;
    private final ObjectMapper mapper;

    public ResponseEntity<Object> requestAuth(String callbackUrl) {
        LastfmEndpoint endpoint = LastfmEndpoint.request_auth;

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint.getEndpoint(config));
        Map<String, String> urlVariables = endpoint.buildUrlVariables(config,
                LastfmUserParameters.builder().callbackUrl(callbackUrl).build());

        URI uri = builder.buildAndExpand(urlVariables).toUri();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(uri);
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    }

    public LastfmAuthGetSessionResponse getAuthSession(String tokenValue) {
        try {
            LastfmEndpoint endpoint = LastfmEndpoint.auth_getSession;
            ResponseEntity<LastfmAuthGetSessionResponse> response =
                    getRestTemplate().getForEntity(endpoint.getEndpoint(config),
                            LastfmAuthGetSessionResponse.class, endpoint.buildUrlVariables(config,
                                    LastfmUserParameters.builder().token(tokenValue).build()));
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return handleError(e);
        }
    }

    private LastfmAuthGetSessionResponse handleError(HttpClientErrorException e) {
        try {
            LastfmAuthGetSessionResponse errorResponse =
                    mapper.readValue(e.getResponseBodyAsString(), LastfmAuthGetSessionResponse.class);
            log.error("An error occurred when calling Last.fm API: message {}, error {}",
                    errorResponse.getMessage(), errorResponse.getError());
            throw new LastfmException(errorResponse.getMessage(), errorResponse, e.getStatusCode());
        } catch (JsonProcessingException jpe) {
            log.error("Could not deserialize Last.fm error: {}", e.getResponseBodyAsString());
            throw new LastfmException("Unknown", e.getStatusCode());
        }
    }
}
