package com.nicolasm.bluosscrobbler.bluos.service;

import com.nicolasm.bluosscrobbler.bluos.config.BluOSConfig;
import com.nicolasm.bluosscrobbler.bluos.model.BluOSEndpoint;
import com.nicolasm.service.bluosscrobbler.bluos.model.StatusType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class BluOSStatusService {
    private final BluOSConfig config;
    private final RestTemplate restTemplate = new RestTemplate();

    public StatusType getStatus() {
        try {
            BluOSEndpoint endpoint = BluOSEndpoint.STATUS_POLLING;
            ResponseEntity<StatusType> responseEntity =
                    restTemplate.getForEntity(endpoint.getEndpoint(config), StatusType.class);
            return responseEntity.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public StatusType getStatus(String etagIn) {
        try {
            BluOSEndpoint endpoint = BluOSEndpoint.STATUS_LONG_POLLING;
            ResponseEntity<StatusType> responseEntity =
                    restTemplate.getForEntity(endpoint.getEndpoint(config), StatusType.class,
                            endpoint.buildUrlVariables(config, etagIn));
            return responseEntity.getBody();
        } catch (Exception e) {
            return null;
        }
    }
}
