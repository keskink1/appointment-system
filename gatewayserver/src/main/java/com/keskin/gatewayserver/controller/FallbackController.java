package com.keskin.gatewayserver.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
@Slf4j
public class FallbackController {

    @GetMapping("/appointment")
    public Mono<String> appointmentFallback() {
        log.error("Appointment service fallback triggered! Service might be down or slow.");
        return Mono.just("Appointment service is down right now, please try again later.");
    }

    @GetMapping("/user")
    public Mono<String> userFallback() {
        log.error("User service fallback triggered!");
        return Mono.just("User service is down right now, please try again later.");
    }
}