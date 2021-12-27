package com.nicolasm.bluosscrobbler.bluos.model;

import com.google.common.collect.ImmutableMap;
import com.nicolasm.bluosscrobbler.bluos.config.BluOSConfig;
import lombok.RequiredArgsConstructor;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

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
            Map<PollingUrlParameters, Object> variables = new EnumMap<>(PollingUrlParameters.class);
            variables.put(timeout, config.getTimeout());
            variables.put(etag, etagIn);
            Map<String, Object> urlVariables = variables.entrySet().stream()
                    .collect(Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue));
            return ImmutableMap.copyOf(urlVariables);
        }
    };

    private final String endpoint;

    public String getEndpoint(BluOSConfig config) {
        return String.format("%s/%s", config.getBaseEndpoint(), endpoint);
    }

    public abstract Map<String, Object> buildUrlVariables(BluOSConfig config, String etag);
}
