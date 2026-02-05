package com.keskin.users.infrastructure.persistence.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@ConfigurationProperties(prefix = "jwt")
@Validated
public record JwtConfig(
        @NotBlank String secret,
        @Min(900) long accessTokenExpiration,
        @Min(86400) long refreshTokenExpiration
) {}