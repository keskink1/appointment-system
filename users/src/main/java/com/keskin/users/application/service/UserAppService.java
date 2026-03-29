package com.keskin.users.application.service;

import com.keskin.common.dto.event.UserDeletedEvent;
import com.keskin.common.dto.event.UserUpdatedEvent;
import com.keskin.common.dto.response.PaginatedResponseDto;
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


    private String getActorAudit(){
        return UserContextHelper.getCurrentUserEmail();
    }

    private void checkOwnerShip(UUID uuid){
        String currentUserId = UserContextHelper.getCurrentUserId();
        if (currentUserId == null){
            throw new ForbiddenException("You are not logged in.");
        }

        UUID actorId = UUID.fromString(currentUserId);
        if (!uuid.equals(actorId) && !UserContextHelper.isAdmin()) {
            throw new jakarta.ws.rs.ForbiddenException("You can only change your own profile.");
        }
    }

    @Transactional(readOnly = true)
    public UserDto getUserDtoById(UUID uuid) {
        checkOwnerShip(uuid);
        User user = findById(uuid);
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public UserDto getUserDtoByEmail(String email) {
        User user = findByEmail(email);
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDto<UserDto> findAll(int page, int size) {

        var userPage = userRepository.findAllUsers(page, size);

        List<UserDto> dtos = userPage.data().stream()
                .map(userMapper::toDto)
                .toList();

        return new PaginatedResponseDto<>(
                dtos,
                userPage.totalElements(),
                userPage.totalPages(),
                userPage.currentPage()
        );
    }

    /**
     * @throws ResourceNotFoundException if user is not found.
     */
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


    /**
     * Updates an existing user's details and triggers data synchronization across the system.
     * <p>
     * This method ensures high consistency by:
     * 1. Performing an email uniqueness check only if the email address is being modified.
     * 2. Updating the User domain entity using the provided request data and audit information.
     * 3. Publishing a {@link UserUpdatedEvent} via RabbitMQ to synchronize the changes
     * with shadow tables in other microservices (e.g., Appointment Service).
     * </p>
     *
     * @param uuid The unique identifier of the user to be updated.
     * @param request The DTO containing the updated user information (name, age, email).
     * @return {@link UserDto} representing the updated state of the user.
     * @throws ResourceAlreadyExistsException if the new email is already registered by another user.
     * @throws ResourceNotFoundException if no user exists with the given UUID.
     */
    @Transactional
    public UserDto updateUserById(UUID uuid, UpdateUserRequestDto request) {
        checkOwnerShip(uuid);

        User user = findById(uuid);

        if (!user.getEmail().value().equals(request.email())) {
            if (userRepository.existsByEmailAndDeletedFalse(request.email())) {
                throw new ResourceAlreadyExistsException("User", "Email", request.email());
            }
        }

        String actorEmail = getActorAudit();
        user.updateUser(request.name(), request.age(), request.email(), actorEmail);

        userRepository.saveUser(user);
    // fix me. if database commit fails message still will be in rabbitmq
        UserUpdatedEvent event = new UserUpdatedEvent(
                user.getUuid(),
                user.getName().value(),
                user.getEmail().value(),
                System.currentTimeMillis()
        );
        userEventPublisher.publishUserUpdated(event);


        return userMapper.toDto(user);
    }

    /**
     * Performs a soft delete by marking the user as deleted.
     */
    @Transactional
    public void deleteUserById(UUID uuid){
        checkOwnerShip(uuid);
        User user = findById(uuid);

        String actorEmail = getActorAudit();
        user.deleteUser(actorEmail);
        userRepository.saveUser(user);

        UserDeletedEvent event =  new UserDeletedEvent(
                user.getUuid(),
                System.currentTimeMillis()
        );

        userEventPublisher.publishUserDeleted(event);
    }

    @Transactional
    public UserDto promoteToAdmin(UUID uuid){
        User user = findById(uuid);
        String actorEmail = getActorAudit();
        user.promoteToAdmin(actorEmail);
        userRepository.saveUser(user);
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto changeRoleToEmployee(UUID uuid){
        User user = findById(uuid);
        String actorEmail = getActorAudit();
        user.changeRoleToEmployee(actorEmail);
        userRepository.saveUser(user);
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto activateUser(UUID uuid){
        User user = findById(uuid);
        String actorEmail = getActorAudit();
        user.activate(actorEmail);
        userRepository.saveUser(user);
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto deactivateUser(UUID uuid){
        User user = findById(uuid);
        String actorEmail = getActorAudit();
        user.deactivate(actorEmail);
        userRepository.saveUser(user);
        return userMapper.toDto(user);
    }
}
