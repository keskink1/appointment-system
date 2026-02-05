package com.keskin.users.application.service;

import com.keskin.common.dto.UserDto;
import com.keskin.common.dto.request.CreateUserRequestDto;
import com.keskin.common.dto.request.LoginRequestDto;
import com.keskin.common.dto.response.AuthResponseDto;
import com.keskin.common.exception.AuthenticationException;
import com.keskin.common.exception.ResourceAlreadyExistsException;
import com.keskin.common.exception.ResourceNotFoundException;
import com.keskin.users.application.mapper.UserMapper;
import com.keskin.users.domain.model.User;
import com.keskin.users.domain.repository.UserRepository;
import com.keskin.users.infrastructure.persistence.entity.RefreshToken;
import com.keskin.users.infrastructure.persistence.repository.RefreshTokenRepository;
import com.keskin.users.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthAppService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


    private String trimString(String value){
        return value.trim();
    }

    @Transactional
    public AuthResponseDto registerUser(CreateUserRequestDto request) {
        String email = trimString(request.email());
        if (userRepository.existsByEmailAndDeletedFalse(email)) {
            throw new ResourceAlreadyExistsException("User", "Email", request.email());
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        User user = User.createUser(
                request.name(),
                request.age(),
                request.email(),
                encodedPassword
        );
        userRepository.saveUser(user);

        String accessToken = jwtTokenProvider.generateToken(
                user.getUuid(),
                user.getEmail().value(),
                user.getRole().name()
        );

        String refreshTokenStr = jwtTokenProvider.generateRefreshToken(
                user.getUuid(),
                user.getEmail().value()
        );

        RefreshToken redisToken = RefreshToken.builder()
                .token(refreshTokenStr)
                .userUuid(user.getUuid())
                .userEmail(user.getEmail().value())
                .build();
        refreshTokenRepository.save(redisToken);

        UserDto userDto = userMapper.toDto(user);
        return new AuthResponseDto(userDto, accessToken,refreshTokenStr);
    }

    @Transactional
    public AuthResponseDto loginUser(LoginRequestDto requestDto){
        String email = trimString(requestDto.email());
        User user = userRepository.findByEmail(email).orElseThrow(() ->
            new AuthenticationException(""));

        if(!passwordEncoder.matches(requestDto.password(), user.getPassword().value())){
            throw new AuthenticationException("");
        }

        String accessToken = jwtTokenProvider.generateToken(
                user.getUuid(),
                user.getEmail().value(),
                user.getRole().name()
        );

        String refreshTokenStr = jwtTokenProvider.generateRefreshToken(
                user.getUuid(),
                user.getEmail().value()
        );

        RefreshToken redisToken = RefreshToken.builder()
                .token(refreshTokenStr)
                .userUuid(user.getUuid())
                .userEmail(user.getEmail().value())
                .build();
        refreshTokenRepository.save(redisToken);

        UserDto userDto = userMapper.toDto(user);
        return new AuthResponseDto(userDto, accessToken, refreshTokenStr);
    }

    public String refreshMyAccessToken(String refreshToken) {
        return refreshTokenRepository.findById(refreshToken)
                .map(token -> jwtTokenProvider.generateToken(token.getUserUuid(), token.getUserEmail(), "USER"))
                .orElseThrow(() -> new AuthenticationException("Session expired"));
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.deleteById(refreshToken);
    }
}
