package com.keskin.users.infrastructure.security;

import com.keskin.users.infrastructure.persistence.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;

    public JwtTokenProvider(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.secretKey = Keys.hmacShaKeyFor(jwtConfig.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UUID userId, String email, String role) {
        return buildToken(
                userId,
                email,
                role,
                jwtConfig.accessTokenExpiration());
    }

    public String generateRefreshToken(UUID userId, String email) {
        return buildToken(
                userId,
                email,
                "REFRESH_TOKEN",
                jwtConfig.refreshTokenExpiration());
    }

    private String buildToken(UUID userId, String email, String role, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000);

        var builder = Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey);

        if (role != null) {
            builder.claim("role", role);
        }

        return builder.compact();
    }

    public UUID getUserIdFromToken(String token) {
        String subject = validateAndGetClaims(token).getSubject();
        return UUID.fromString(subject);
    }

    public String getEmailFromToken(String token) {
        return validateAndGetClaims(token).get("email", String.class);
    }

    private Claims validateAndGetClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}