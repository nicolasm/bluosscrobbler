package com.nicolasm.bluosscrobbler.scrobbler.lastfm.exception;

import org.springframework.http.HttpStatus;

public class LastfmException extends RuntimeException {
    private static final long serialVersionUID = -3784428070819900002L;

    private final String message;
    private final Object body;
    private final HttpStatus status;

    public LastfmException(String message, HttpStatus status) {
        this.message = message;
        this.body = null;
        this.status = status;
    }

    public LastfmException(String message, Object body, HttpStatus status) {
        this.message = message;
        this.body = body;
        this.status = status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Object getBody() {
        return body;
    }

    public HttpStatus getStatus() {
        return status;
    }
}