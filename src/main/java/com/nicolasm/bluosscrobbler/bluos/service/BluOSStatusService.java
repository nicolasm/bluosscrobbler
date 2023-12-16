package com.nicolasm.bluosscrobbler.bluos.service;

import com.nicolasm.bluosscrobbler.bluos.config.BluOSConfig;
import com.nicolasm.bluosscrobbler.bluos.model.BluOSEndpoint;
import com.nicolasm.bluosscrobbler.bluos.model.BluOSStatus;
import com.nicolasm.service.bluosscrobbler.bluos.model.BluOSRawStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.support.RestGatewaySupport;

import static com.nicolasm.service.bluosscrobbler.bluos.model.BluOSPlayingState.STOP;

@Service
@RequiredArgsConstructor
@Slf4j
public class BluOSStatusService extends RestGatewaySupport {
    private final BluOSConfig config;

    public BluOSStatus getStatus() {
        try {
            log.info("Normal polling");
            BluOSEndpoint endpoint = BluOSEndpoint.STATUS_POLLING;
            ResponseEntity<BluOSRawStatus> responseEntity =
                    getRestTemplate().getForEntity(endpoint.getEndpoint(config), BluOSRawStatus.class);
            BluOSRawStatus raw = responseEntity.getBody();
            return new BluOSStatus(raw, config.isServiceEnabled(raw));
        } catch (Exception e) {
            log.error("An error occurred when polling BluOS status.", e);
            return null;
        }
    }

    public BluOSStatus getStatus(long timeout, String etagIn) {
        try {
            log.info("Long polling with timeout {} and etag {}", timeout, etagIn);
            BluOSEndpoint endpoint = BluOSEndpoint.STATUS_LONG_POLLING;
            ResponseEntity<BluOSRawStatus> responseEntity =
                    getRestTemplate().getForEntity(endpoint.getEndpoint(config), BluOSRawStatus.class,
                            endpoint.buildUrlVariables(config, timeout, etagIn));
            BluOSRawStatus raw = responseEntity.getBody();
            return new BluOSStatus(raw, config.isServiceEnabled(raw));
        } catch (Exception e) {
            log.error("An error occurred when long polling BluOS status with timeout {} and etag {}.", timeout, etagIn, e);
            return null;
        }
    }

    private BluOSStatus stopped() {
        BluOSRawStatus raw = new BluOSRawStatus();
        raw.setState(STOP);
        return new BluOSStatus(raw, config.isServiceEnabled(raw));
    }
}
