package com.nicolasm.bluosscrobbler.scrobbler.lastfm.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class LastfmRequestAuthUrlResponse {
    private final String url;
    private final String token;
}
