package com.nicolasm.bluosscrobbler.scrobbler.lastfm.model;

import lombok.Data;

@Data
public class LastfmAuthGetSessionResponse {
    private Session session;

    @Data
    private static final class Session {
        private String name;
        private String key;
        private boolean subscriber;
    }
}
