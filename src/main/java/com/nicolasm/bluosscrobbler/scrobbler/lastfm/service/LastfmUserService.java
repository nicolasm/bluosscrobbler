package com.nicolasm.bluosscrobbler.scrobbler.lastfm.service;

import com.nicolasm.bluosscrobbler.scrobbler.lastfm.config.LastfmConfig;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmEndpoint;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUserParameters;
import com.nicolasm.service.bluosscrobbler.bluos.model.StatusType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LastfmUserService {
    private final LastfmConfig config;
    private final RestTemplate restTemplate = new RestTemplate();

    public void updateNowPlaying(StatusType status) {
        try {
            LastfmEndpoint endpoint = LastfmEndpoint.track_updateNowPlaying;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            Map<String, String> urlVariables = endpoint.buildUrlVariables(config, LastfmUserParameters.builder()
                    .artist(status.getArtist())
                    .album(status.getAlbum())
                    .track(status.getName())
                    .duration(status.getTotlen())
                    .build());

            postQuery(urlVariables, headers);
        } catch (Exception e) {
            log.error("An error occurred when trying to update now playing.", e);
        }
    }

    public void scrobble(StatusType status) {
        try {
            LastfmEndpoint endpoint = LastfmEndpoint.track_scrobble;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            Map<String, String> urlVariables = endpoint.buildUrlVariables(config, LastfmUserParameters.builder()
                    .artist(status.getArtist())
                    .album(status.getAlbum())
                    .track(status.getName())
                    .duration(status.getTotlen())
                    .timestamp(String.valueOf(OffsetDateTime.now(ZoneId.of("Europe/Paris")).toEpochSecond()))
                    .build());

            postQuery(urlVariables, headers);
        } catch (Exception e) {
            log.error("An error occurred when trying to update now playing.", e);
        }
    }

    private void postQuery(Map<String, String> urlVariables, HttpHeaders headers) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        urlVariables.forEach(map::add);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(config.getApiUrl(), request, String.class);
        log.info("{}", response.getBody());
    }
}
