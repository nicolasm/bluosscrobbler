package com.nicolasm.bluosscrobbler.bluos.model;

import lombok.RequiredArgsConstructor;

import static com.nicolasm.bluosscrobbler.bluos.model.PollingUrlParameters.etag;
import static com.nicolasm.bluosscrobbler.bluos.model.PollingUrlParameters.timeout;

@RequiredArgsConstructor
public enum BluOSEndpoint {
    STATUS_POLLING("Status"),
    STATUS_LONG_POLLING(String.format("Status?%s={%s}&%s={%s}", timeout, timeout, etag, etag));

    private final String endpoint;

    public String getEndpoint(String baseEndpoint) {
        return String.format("%s/%s", baseEndpoint, endpoint);
    }
}
