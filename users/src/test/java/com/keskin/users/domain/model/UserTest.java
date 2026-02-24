package com.keskin.users.domain.model;

import com.keskin.users.application.dto.UpdateUserRequestDto;
import com.keskin.users.application.mapper.UserMapper;
import com.keskin.users.application.service.UserAppService;
import com.keskin.users.domain.repository.UserRepository;
import com.keskin.users.infrastructure.persistence.message.UserEventPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserAppService userAppService;

    @Mock
    private UserEventPublisher userEventPublisher;

    @Test
    @DisplayName("Should successfully update user details and set audit fields")
    void shouldUpdateUserDetails() {
        User user = User.createUser(
                "SYSTEM",
                "John",
                33,
                "john@gmail.com",
                "123456"
        );

        UUID testId = UUID.randomUUID();

        when(userRepository.findById(testId)).thenReturn(Optional.of(user));

        UpdateUserRequestDto requestDto = new UpdateUserRequestDto(
                "Jane",
                44,
                null
        );

        userAppService.updateUserById(testId, requestDto);

        assertEquals("Jane", user.getName().value());
        assertEquals(44, user.getAge().value());
        assertEquals("john@gmail.com", user.getEmail().value());

        assertNotNull(user.getUpdatedAt());

        verify(userRepository, times(1)).saveUser(any());

        verify(userEventPublisher, times(1)).publishUserUpdated(any());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when updating a deleted user")
    void shouldThrowExceptionWhenUpdatingDeletedUser() {
        // ARRANGE
        User user = User.createUser("SYSTEM", "Michael", 30, "mike@gmail.com", "12345");
        user.deleteUser("SYSTEM");

        UUID testId = UUID.randomUUID();
        UpdateUserRequestDto requestDto = new UpdateUserRequestDto("Gabriella", null, null);

        when(userRepository.findById(testId)).thenReturn(Optional.of(user));

        //ACT & ASSERT
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            userAppService.updateUserById(testId, requestDto);
        });

        verify(userRepository, never()).saveUser(any());
        verify(userEventPublisher, never()).publishUserUpdated(any());
    }
}