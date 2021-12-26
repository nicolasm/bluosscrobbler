package com.nicolasm.bluosscrobbler.bluos.controller;

import com.nicolasm.bluosscrobbler.bluos.service.BluOSStatusService;
import com.nicolasm.service.bluosscrobbler.bluos.model.StatusType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/v1/bluossrobbler/bluos/")
public class BluOSStatusController {
    private final BluOSStatusService service;

    @GetMapping("/status")
    public ResponseEntity<StatusType> getStatus() {
        return ResponseEntity.ok(service.getStatus());
    }
}
