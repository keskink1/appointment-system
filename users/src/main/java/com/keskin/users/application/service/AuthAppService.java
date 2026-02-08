package com.keskin.users.application.service;

import com.keskin.common.dto.UserDto;
import com.keskin.common.dto.event.UserCreatedEvent;
import com.keskin.common.dto.request.CreateUserRequestDto;
import com.keskin.common.dto.request.LoginRequestDto;
import com.keskin.common.dto.response.AuthResponseDto;
import com.keskin.common.exception.AuthenticationException;
import com.keskin.common.exception.ResourceAlreadyExistsException;
import com.keskin.users.application.mapper.UserMapper;
import com.keskin.users.domain.model.User;
import com.keskin.users.domain.repository.UserRepository;
import com.keskin.users.infrastructure.persistence.message.UserEventPublisher;
import com.keskin.users.infrastructure.security.JwtTokenProvider;
import com.keskin.users.infrastructure.security.UserContextHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service for handling authentication flows: Registration, Login, Token Refresh, and Logout.
 * Uses JWT for access tokens and Redis-backed Refresh Tokens for session management.
 */
@Service
@RequiredArgsConstructor
public class AuthAppService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserEventPublisher userEventPublisher;


    private String generateAccessToken(User user) {
        return jwtTokenProvider.generateToken(
                user.getUuid(),
                user.getEmail().value(),
                user.getRole().name()
        );
    }

    private String generateRefreshToken(User user) {
        return jwtTokenProvider.generateRefreshToken(
                user.getUuid(),
                user.getEmail().value()
        );
    }


    /**
     * Orchestrates user registration: persists the domain entity, triggers cross-service
     * data synchronization via RabbitMQ, and manages security sessions.
     * <p>
     * This method performs the following operations:
     * 1. Checks for email uniqueness.
     * 2. Persists the new User to the database.
     * 3. Publishes a {@link UserCreatedEvent} to notify other microservices (e.g., Appointment Service)
     * for data replication/shadowing.
     * 4. Invalidates the 'users' cache to ensure data consistency in paginated lists.
     * 5. Generates JWT access and refresh tokens.
     * </p>
     *
     * @param request The user registration details.
     * @return {@link AuthResponseDto} containing user details and security tokens.
     * @throws ResourceAlreadyExistsException if the email is already registered and active.
     */
    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public AuthResponseDto registerUser(CreateUserRequestDto request) {
        if (userRepository.existsByEmailAndDeletedFalse(request.email())) {
            throw new ResourceAlreadyExistsException("User", "Email", request.email());
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        String actorEmail = UserContextHelper.getCurrentUserEmail();
        User user = User.createUser(
                actorEmail,
                request.name(),
                request.age(),
                request.email(),
                encodedPassword
        );
        userRepository.saveUser(user);

        UserCreatedEvent event = new UserCreatedEvent(
                user.getUuid(),
                user.getName().value(),
                user.getEmail().value(),
                System.currentTimeMillis()
        );

        userEventPublisher.publishUserCreated(event);

        String accessToken = generateAccessToken(user);

        String refreshTokenStr = generateRefreshToken(user);

        refreshTokenService.saveRefreshToken(user, refreshTokenStr);

        UserDto userDto = userMapper.toDto(user);
        return new AuthResponseDto(userDto, accessToken, refreshTokenStr);
    }


    @Transactional
    public AuthResponseDto loginUser(LoginRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.email()).orElseThrow(() ->
                new AuthenticationException(""));

        if (!passwordEncoder.matches(requestDto.password(), user.getPassword().value())) {
            throw new AuthenticationException("");
        }

        String accessToken = generateAccessToken(user);

        String refreshTokenStr = generateRefreshToken(user);

        refreshTokenService.saveRefreshToken(user, refreshTokenStr);


        UserDto userDto = userMapper.toDto(user);
        return new AuthResponseDto(userDto, accessToken, refreshTokenStr);
    }

    public String refreshMyAccessToken(String refreshToken) {
        return refreshTokenService.refreshAccessToken(refreshToken);
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.deleteToken(refreshToken);
    }
}
