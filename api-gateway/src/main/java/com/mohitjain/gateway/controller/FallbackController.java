package com.mohitjain.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/user-service")
    public Mono<Map<String, Object>> userServiceFallback(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        return Mono.just(Map.of(
                "status", 503,
                "error", "Service Unavailable",
                "message", "User service is temporarily unavailable. Please try again later.",
                "timestamp", Instant.now().toString()
        ));
    }

    @GetMapping("/notification-service")
    public Mono<Map<String, Object>> notificationServiceFallback(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        return Mono.just(Map.of(
                "status", 503,
                "error", "Service Unavailable",
                "message", "Notification service is temporarily unavailable. Please try again later.",
                "timestamp", Instant.now().toString()
        ));
    }
}
