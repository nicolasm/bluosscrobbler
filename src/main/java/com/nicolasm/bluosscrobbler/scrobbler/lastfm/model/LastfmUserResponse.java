package com.nicolasm.bluosscrobbler.scrobbler.lastfm.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class LastfmUserResponse implements Serializable {
    private String message;
    private int error;
}
