package com.blackjack.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
@Tag(name = "Health Check", description = "Endpoint for verifying the API status")
public class HealthController {

    @Operation(summary = "Verify system status")
    @GetMapping
    public Mono<ResponseEntity<Map<String, Object>>> checkHealth() {
        Map<String, Object> status = new HashMap<>();

        status.put("status", "UP");
        status.put("timestamp", LocalDateTime.now());
        status.put("service", "Blackjack API");
        status.put("version", "1.0.0");

        return Mono.just(ResponseEntity.ok(status));
    }
}