package com.nicolasm.bluosscrobbler.scrobbler.exception;

import com.nicolasm.bluosscrobbler.scrobbler.lastfm.exception.LastfmException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(LastfmException.class)
    public ResponseEntity<Object> handleLastfmException(LastfmException e, WebRequest request) {
        return handleExceptionInternal(e, e.getBody(), new HttpHeaders(), e.getStatus(), request);
    }
}
