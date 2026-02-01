package com.keskin.users.application.service;

import com.keskin.users.application.dto.CreateUserRequestDto;
import com.keskin.users.application.mapper.UserMapper;
import com.keskin.users.common.exception.ResourceAlreadyExistsException;
import com.keskin.users.domain.model.User;
import com.keskin.users.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAppServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserAppService userAppService;

    @Test
    @DisplayName("Should register user successfully when email is unique")
    void shouldRegisterUserSuccessfully() {
        // Arrange
        CreateUserRequestDto request = new CreateUserRequestDto(
                "Mert", 25, "mert@keskin.com", "securePass123"
        );
        when(userRepository.existsByEmailAndDeletedFalse(anyString())).thenReturn(false);

        userAppService.registerUser(request);

        verify(userRepository, times(1)).saveUser(any(User.class));
    }

    @Test
    @DisplayName("Should throw ResourceAlreadyExistsException when email is taken")
    void shouldThrowExceptionWhenEmailIsTaken() {
        CreateUserRequestDto request = new CreateUserRequestDto(
                "Mert", 25, "duplicate@mail.com", "pass"
        );
        when(userRepository.existsByEmailAndDeletedFalse("duplicate@mail.com")).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> {
            userAppService.registerUser(request);
        });
    }
}