package com.nicolasm.bluosscrobbler.scrobbler.lastfm.controller;

import com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmAuthGetSessionResponse;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmAuthTokenResponse;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmRequestAuthUrlResponse;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.service.LastfmAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/v1/bluossrobbler/lastfm/auth")
public class LastfmAuthController {
    private final LastfmAuthService service;

    @GetMapping("/token")
    public ResponseEntity<LastfmAuthTokenResponse> getAuthToken() {
        return ResponseEntity.ok(service.getAuthToken());
    }

    @GetMapping("/request")
    public ResponseEntity<LastfmRequestAuthUrlResponse> requestAuth() {
        return ResponseEntity.ok(service.requestAuth());
    }

    @GetMapping("/session")
    public ResponseEntity<LastfmAuthGetSessionResponse> getAuthSession(@RequestParam String token) {
        return ResponseEntity.ok(service.getAuthSession(token));
    }
}
