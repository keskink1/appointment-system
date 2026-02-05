package com.keskin.gatewayserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@ConfigurationProperties(prefix = "jwt")
@Validated
public record JwtConfig(
        @NotNull
        @Size(min = 32, message = "Secret key must be at least 32 letters!")
        String secret,
        long expiration
) {}