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
import com.keskin.users.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthAppService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthAppService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

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

        String token = jwtTokenProvider.generateToken(
                user.getUuid(),
                user.getEmail().value(),
                user.getRole().name()
        );

        UserDto userDto = userMapper.toDto(user);
        return new AuthResponseDto(userDto, token);
    }

    @Transactional
    public AuthResponseDto loginUser(LoginRequestDto requestDto){
        String email = trimString(requestDto.email());
        User user = userRepository.findByEmail(email).orElseThrow(() ->
            new AuthenticationException(""));

        if(!passwordEncoder.matches(requestDto.password(), user.getPassword().value())){
            throw new AuthenticationException("");
        }

        String token = jwtTokenProvider.generateToken(
                user.getUuid(),
                user.getEmail().value(),
                user.getRole().name()
        );

        UserDto userDto = userMapper.toDto(user);
        return new AuthResponseDto(userDto, token);
    }
}
