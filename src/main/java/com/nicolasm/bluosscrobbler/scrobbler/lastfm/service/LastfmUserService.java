package com.nicolasm.bluosscrobbler.scrobbler.lastfm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.config.LastfmConfig;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmEndpoint;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUserParameters;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUserResponse;
import com.nicolasm.bluosscrobbler.scrobbler.model.ScrobblerTrackPlay;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.support.RestGatewaySupport;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LastfmUserService extends RestGatewaySupport {
    private final LastfmConfig config;
    private final ObjectMapper mapper;

    public void updateNowPlaying(ScrobblerTrackPlay play) {
        try {
            LastfmEndpoint endpoint = LastfmEndpoint.track_updateNowPlaying;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            Map<String, String> urlVariables = endpoint.buildUrlVariables(config, LastfmUserParameters.fromPlay(play));

            postQuery(urlVariables, headers);
        } catch (HttpClientErrorException e) {
            handleError(e);
        }
    }

    public boolean addPlayedTrack(ScrobblerTrackPlay play) {
        try {
            LastfmEndpoint endpoint = LastfmEndpoint.track_scrobble;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            Map<String, String> urlVariables = endpoint.buildUrlVariables(config, LastfmUserParameters.fromPlay(play));

            postQuery(urlVariables, headers);
            return true;
        } catch (HttpClientErrorException e) {
            handleError(e);
            return false;
        }
    }

    private void handleError(HttpClientErrorException e) {
        try {
            LastfmUserResponse response = mapper.readValue(e.getResponseBodyAsString(), LastfmUserResponse.class);
            log.error("An response occurred when calling Last.fm API: message {}, response {}", response.getMessage(), response.getError());
        } catch (JsonProcessingException ex) {
            log.error("Could not deserialize Last.fm error: {}", e.getResponseBodyAsString());
        }
    }

    private void postQuery(Map<String, String> urlVariables, HttpHeaders headers) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        urlVariables.forEach(map::add);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = getRestTemplate().postForEntity(config.getApiUrl(), request, String.class);
        log.trace("Last.fm call response: {}", response.getBody());
    }
}
