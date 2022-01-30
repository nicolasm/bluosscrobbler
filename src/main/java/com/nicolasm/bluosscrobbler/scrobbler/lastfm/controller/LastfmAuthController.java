package com.nicolasm.bluosscrobbler.scrobbler.lastfm.controller;

import com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmAuthGetSessionResponse;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.service.LastfmAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/v1/bluossrobbler/lastfm/auth")
public class LastfmAuthController {
    private static final String CALLBACK_URL = "http://localhost:%s/v1/bluossrobbler/lastfm/auth/session";

    private final LastfmAuthService service;
    @Value("${server.port}")
    private int port;

    @GetMapping("/request")
    public ResponseEntity<Object> requestAuth() {
        return service.requestAuth(String.format(CALLBACK_URL, port));
    }

    @GetMapping("/session")
    public ResponseEntity<LastfmAuthGetSessionResponse> getAuthSession(@RequestParam("token") String token) {
        return ResponseEntity.ok(service.getAuthSession(token));
    }
}
