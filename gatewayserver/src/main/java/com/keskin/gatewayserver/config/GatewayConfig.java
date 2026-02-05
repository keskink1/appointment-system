package com.keskin.gatewayserver.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // USER SERVICE (Hem /auth hem /users için)
                .route("user-service", r -> r.path("/api/v1/users/**", "/api/v1/auth/**")
                        .filters(f -> f
                                        .addRequestHeader("X-Response-Header", "Gateway-Authenticated")
                        )
                        .uri("lb://user-service"))

                // APPOINTMENT SERVICE
                .route("appointment-service", r -> r.path("/api/v1/appointment/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway-Time", LocalDateTime.now().toString())
                                .circuitBreaker(config -> config
                                        .setName("appointmentCB")
                                        .setFallbackUri("forward:/fallback/appointment"))
                        )
                        .uri("lb://appointment-service"))
                .build();
    }
}