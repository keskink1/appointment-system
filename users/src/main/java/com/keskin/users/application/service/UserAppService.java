package com.keskin.users.application.service;

import com.keskin.common.dto.event.UserDeletedEvent;
import com.keskin.common.dto.event.UserUpdatedEvent;
import com.keskin.common.dto.response.PaginatedResponseDto;
import com.keskin.common.exception.AuthenticationException;
import com.keskin.common.exception.ForbiddenException;
import com.keskin.common.util.UserContextHelper;
import com.keskin.users.application.dto.UpdateUserRequestDto;
import com.keskin.common.dto.UserDto;
import com.keskin.users.application.mapper.UserMapper;
import com.keskin.common.exception.ResourceAlreadyExistsException;
import com.keskin.common.exception.ResourceNotFoundException;
import com.keskin.users.domain.model.User;
import com.keskin.users.domain.repository.UserRepository;
import com.keskin.users.infrastructure.persistence.message.UserEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAppService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserEventPublisher userEventPublisher;

    private String getActorAudit() {
        return UserContextHelper.getCurrentUserEmail();
    }

    private void checkOwnership(UUID uuid) {
        String currentUserId = UserContextHelper.getCurrentUserId();
        if (currentUserId == null) {
            throw new AuthenticationException("You are not logged in."); // 401
        }

        UUID actorId = UUID.fromString(currentUserId);
        if (!uuid.equals(actorId) && !UserContextHelper.isAdmin()) {
            throw new ForbiddenException("You can only change your own profile."); // 403
        }
    }

    private void requireAdmin() {
        if (!UserContextHelper.isAdmin()) {
            throw new ForbiddenException("Only admins can perform this action.");
        }
    }

    @Transactional(readOnly = true)
    public UserDto getUserDtoById(UUID uuid) {
        checkOwnership(uuid);
        return userMapper.toDto(findById(uuid));
    }

    @Transactional(readOnly = true)
    public UserDto getUserDtoByEmail(String email) {
        requireAdmin();
        return userMapper.toDto(findByEmail(email));
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDto<UserDto> findAll(int page, int size) {
        var userPage = userRepository.findAllUsers(page, size);

        List<UserDto> dtos = userPage.data().stream()
                .map(userMapper::toDto)
                .toList();

        return new PaginatedResponseDto<>(dtos, userPage.totalElements(), userPage.totalPages(), userPage.currentPage());
    }

    @Transactional(readOnly = true)
    public User findById(UUID uuid) {
        return userRepository.findById(uuid).orElseThrow(() ->
                new ResourceNotFoundException("User", "ID", uuid));
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("User", "Email", email));
    }

    @Transactional
    public UserDto updateUserById(UUID uuid, UpdateUserRequestDto request) {
        checkOwnership(uuid);

        User user = findById(uuid);

        if (!user.getEmail().value().equals(request.email())) {
            if (userRepository.existsByEmailAndDeletedFalse(request.email())) {
                throw new ResourceAlreadyExistsException("User", "Email", request.email());
            }
        }

        user.updateUser(request.name(), request.age(), request.email(), getActorAudit());
        userRepository.saveUser(user);

        // fix me: if database commit fails message still will be in rabbitmq
        userEventPublisher.publishUserUpdated(new UserUpdatedEvent(
                user.getUuid(),
                user.getName().value(),
                user.getEmail().value(),
                System.currentTimeMillis()
        ));

        return userMapper.toDto(user);
    }

    @Transactional
    public void deleteUserById(UUID uuid) {
        checkOwnership(uuid);
        User user = findById(uuid);
        user.deleteUser(getActorAudit());
        userRepository.saveUser(user);

        userEventPublisher.publishUserDeleted(new UserDeletedEvent(
                user.getUuid(),
                System.currentTimeMillis()
        ));
    }

    @Transactional
    public UserDto promoteToAdmin(UUID uuid) {
        requireAdmin();
        User user = findById(uuid);
        user.promoteToAdmin(getActorAudit());
        userRepository.saveUser(user);
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto changeRoleToEmployee(UUID uuid) {
        requireAdmin();
        User user = findById(uuid);
        user.changeRoleToEmployee(getActorAudit());
        userRepository.saveUser(user);
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto activateUser(UUID uuid) {
        requireAdmin();
        User user = findById(uuid);
        user.activate(getActorAudit());
        userRepository.saveUser(user);
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto deactivateUser(UUID uuid) {
        requireAdmin();
        User user = findById(uuid);
        user.deactivate(getActorAudit());
        userRepository.saveUser(user);
        return userMapper.toDto(user);
    }
}