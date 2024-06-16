package com.gayuh.auto_deploy.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(value = ResponseStatusException.class)
    public ResponseEntity<Object> responseStatus(ResponseStatusException exception) {
        log.warn("Error in ResponseStatusException");

        return ResponseEntity
                .status(exception.getStatusCode())
                .body(Map.of("error", Map.of("messsage", exception.getReason())));
    }
}

