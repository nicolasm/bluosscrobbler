package com.nicolasm.bluosscrobbler.bluos.model;

import com.google.common.collect.ImmutableMap;
import com.nicolasm.bluosscrobbler.bluos.config.BluOSConfig;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static com.nicolasm.bluosscrobbler.bluos.model.PollingUrlParameters.etag;
import static com.nicolasm.bluosscrobbler.bluos.model.PollingUrlParameters.timeout;

@RequiredArgsConstructor
public enum BluOSEndpoint {
    STATUS_POLLING("Status") {
        @Override
        public Map<String, Object> buildUrlVariables(BluOSConfig config, String etag) {
            return ImmutableMap.of();
        }
    },
    STATUS_LONG_POLLING(String.format("Status?%s={%s}&%s={%s}", timeout, timeout, etag, etag)) {
        @Override
        public Map<String, Object> buildUrlVariables(BluOSConfig config, String etagIn) {
            return ImmutableMap.of(timeout.toString(), config.getTimeout(),
                                   etag.toString(), etagIn);
        }
    };

    private final String endpoint;

    public String getEndpoint(BluOSConfig config) {
        return String.format("%s/%s", config.getBaseEndpoint(), endpoint);
    }

    public abstract Map<String, Object> buildUrlVariables(BluOSConfig config, String etag);
}
