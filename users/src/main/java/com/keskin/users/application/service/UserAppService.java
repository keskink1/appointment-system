package com.keskin.users.application.service;

import com.keskin.common.dto.response.PaginatedResponseDto;
import com.keskin.users.application.dto.UpdateUserRequestDto;
import com.keskin.common.dto.UserDto;
import com.keskin.users.application.mapper.UserMapper;
import com.keskin.common.exception.ResourceAlreadyExistsException;
import com.keskin.common.exception.ResourceNotFoundException;
import com.keskin.users.domain.model.User;
import com.keskin.users.domain.repository.UserRepository;
import com.keskin.users.infrastructure.security.UserContextHelper;
import org.springframework.cache.annotation.Cacheable;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserAppService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    private String getActorAudit(){
        return UserContextHelper.getCurrentUserEmail();
    }

    @Cacheable(value = "users", key = "#page + '-' + #size")
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

    @Transactional(readOnly = true)
    public UserDto getUserDtoById(UUID uuid) {
        User user = findById(uuid);
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public UserDto getUserDtoByEmail(String email) {
        User user = findByEmail(email);
        return userMapper.toDto(user);
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
     * Updates user and checks for email uniqueness if email is changed.
     * @throws ResourceAlreadyExistsException if new email is already taken.
     */
    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public UserDto updateUserById(UUID uuid, UpdateUserRequestDto request) {
        User user = findById(uuid);

        if (!user.getEmail().value().equals(request.email())) {
            if (userRepository.existsByEmailAndDeletedFalse(request.email())) {
                throw new ResourceAlreadyExistsException("User", "Email", request.email());
            }
        }

        String actorEmail = getActorAudit();
        user.updateUser(request.name(), request.age(), request.email(), actorEmail);

        userRepository.saveUser(user);

        return userMapper.toDto(user);
    }

    /**
     * Performs a soft delete by marking the user as deleted.
     */
    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public void deleteUserById(UUID uuid){
        User user = findById(uuid);

        String actorEmail = getActorAudit();
        user.deleteUser(actorEmail);
        userRepository.saveUser(user);
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public UserDto promoteToAdmin(UUID uuid){
        User user = findById(uuid);
        String actorEmail = getActorAudit();
        user.promoteToAdmin(actorEmail);
        userRepository.saveUser(user);
        return userMapper.toDto(user);
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public UserDto changeRoleToEmployee(UUID uuid){
        User user = findById(uuid);
        String actorEmail = getActorAudit();
        user.changeRoleToEmployee(actorEmail);
        userRepository.saveUser(user);
        return userMapper.toDto(user);
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public UserDto activateUser(UUID uuid){
        User user = findById(uuid);
        String actorEmail = getActorAudit();
        user.activate(actorEmail);
        userRepository.saveUser(user);
        return userMapper.toDto(user);
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public UserDto deactivateUser(UUID uuid){
        User user = findById(uuid);
        String actorEmail = getActorAudit();
        user.deactivate(actorEmail);
        userRepository.saveUser(user);
        return userMapper.toDto(user);
    }
}
