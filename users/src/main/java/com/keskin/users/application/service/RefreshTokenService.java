package com.keskin.users.application.service;

import com.keskin.common.exception.AuthenticationException;
import com.keskin.users.domain.model.User;
import com.keskin.users.infrastructure.persistence.entity.RefreshToken;
import com.keskin.users.infrastructure.persistence.repository.RefreshTokenRepository;
import com.keskin.users.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public void saveRefreshToken(User user, String token){
        RefreshToken redisToken = RefreshToken.builder()
                .token(token)
                .userUuid(user.getUuid())
                .userEmail(user.getEmail().value())
                .userRole(user.getRole().name())
                .build();
        refreshTokenRepository.save(redisToken);
    }

    /**
     * Generates a new Access Token using a valid Refresh Token from Redis.
     *
     * @param refreshToken The token stored in the client and Redis.
     * @return New JWT Access Token.
     * @throws AuthenticationException if the refresh token is missing or expired.
     */
    public String refreshAccessToken(String refreshToken) {
        return refreshTokenRepository.findById(refreshToken)
                .map(token -> jwtTokenProvider.generateToken(
                                token.getUserUuid(),
                                token.getUserEmail(),
                                token.getUserRole()
                        )
                )
                .orElseThrow(() -> new AuthenticationException("Session expired"));
    }

    public void deleteToken(String refreshToken) {
        refreshTokenRepository.deleteById(refreshToken);
    }
}
