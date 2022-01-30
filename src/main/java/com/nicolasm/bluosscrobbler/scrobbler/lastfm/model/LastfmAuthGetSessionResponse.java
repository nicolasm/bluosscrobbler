package com.nicolasm.bluosscrobbler.scrobbler.lastfm.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class LastfmAuthGetSessionResponse implements Serializable {
    private Session session;
    private String message;
    private int error;

    @Data
    public static final class Session implements Serializable {
        private String name;
        private String key;
        private boolean subscriber;
    }
}
