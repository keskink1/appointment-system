package com.keskin.users.application;

import com.keskin.common.dto.event.UserUpdatedEvent;
import com.keskin.common.util.UserContextHelper;
import com.keskin.users.application.dto.UpdateUserRequestDto;
import com.keskin.users.application.mapper.UserMapper;
import com.keskin.users.application.service.UserAppService;
import com.keskin.users.domain.model.User;
import com.keskin.users.domain.repository.UserRepository;
import com.keskin.users.infrastructure.persistence.message.UserEventPublisher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserAppServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserEventPublisher userEventPublisher;

    @InjectMocks
    private UserAppService userAppService;

    private MockedStatic<UserContextHelper> userContextHelperMock;

    @BeforeEach
    void setUp() {
        userContextHelperMock = mockStatic(UserContextHelper.class);
    }

    @AfterEach
    void closeStaticClasses() {
        userContextHelperMock.close();
    }

    @Test
    @DisplayName("User info should be updated and event should be published")
    void shouldUpdateUserByIdSuccessfully() {
        UUID testUuid = UUID.randomUUID();
        User mockUser = User.createUser("system", "John", 25, "john@old.com", "pass");
        UpdateUserRequestDto request = new UpdateUserRequestDto("Jane", 30, "jane@new.com");

        userContextHelperMock.when(() -> UserContextHelper.getCurrentUserEmail()).thenReturn("admin@test.com");

        when(userRepository.findById(testUuid)).thenReturn(Optional.of(mockUser));
        when(userRepository.existsByEmailAndDeletedFalse("jane@new.com")).thenReturn(false);

        userAppService.updateUserById(testUuid, request);

        assertEquals("Jane", mockUser.getName().value());
        assertEquals("jane@new.com", mockUser.getEmail().value());

        verify(userRepository, times(1)).saveUser(mockUser);

        verify(userEventPublisher, times(1)).publishUserUpdated(any(UserUpdatedEvent.class));
    }
}
