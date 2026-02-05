package com.keskin.gatewayserver.config;

import com.keskin.gatewayserver.filter.AuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class GatewayConfig {

    private final AuthenticationFilter authFilter;

    public GatewayConfig(AuthenticationFilter authFilter) {
        this.authFilter = authFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 1. AUTH (LOGIN & REGISTER) -> /auth/**
                .route("auth-route", r -> r.path("/auth/**")
                        .filters(f -> f.rewritePath("/auth/(?<segment>.*)", "/api/v1/auth/${segment}"))
                        .uri("lb://user-service"))

                // 2. USER SERVICE -> /users/**
                .route("user-service", r -> r.path("/users/**")
                        .filters(f -> f
                                .filter(authFilter.apply(new AuthenticationFilter.Config()))
                                .rewritePath("/users/(?<segment>.*)", "/api/v1/users/${segment}")
                                .addRequestHeader("X-Response-Header", "Gateway-Authenticated")
                        )
                        .uri("lb://user-service"))

                // 3. APPOINTMENT SERVICE -> /appointments/**
                .route("appointment-service", r -> r.path("/appointments/**")
                        .filters(f -> f
                                .filter(authFilter.apply(new AuthenticationFilter.Config()))
                                .rewritePath("/appointments/(?<segment>.*)", "/api/v1/appointment/${segment}")
                                .addRequestHeader("X-Gateway-Time", LocalDateTime.now().toString())
                                .circuitBreaker(config -> config
                                        .setName("appointmentCB")
                                        .setFallbackUri("forward:/fallback/appointment"))
                        )
                        .uri("lb://appointment-service"))
                .build();
    }
}