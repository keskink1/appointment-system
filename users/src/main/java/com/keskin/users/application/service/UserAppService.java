package com.keskin.users.application.service;

import com.keskin.users.application.dto.CreateUserRequestDto;
import com.keskin.users.application.dto.UpdateUserRequestDto;
import com.keskin.users.application.dto.UserDto;
import com.keskin.users.application.mapper.UserMapper;
import com.keskin.common.exception.ResourceAlreadyExistsException;
import com.keskin.common.exception.ResourceNotFoundException;
import com.keskin.users.domain.model.User;
import com.keskin.users.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserAppService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserAppService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
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
    public UserDto registerUser(CreateUserRequestDto request) {
        if (userRepository.existsByEmailAndDeletedFalse(request.email())) {
            throw new ResourceAlreadyExistsException("User", "Email", request.email());
        }

        User user = User.createUser(
                request.name(),
                request.age(),
                request.email(),
                request.password()
                );
        userRepository.saveUser(user);

        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto updateUserById(UUID uuid, UpdateUserRequestDto request) {
        User user = findById(uuid);

        if (!user.getEmail().value().equals(request.email())) {
            if (userRepository.existsByEmailAndDeletedFalse(request.email())) {
                throw new ResourceAlreadyExistsException("User", "Email", request.email());
            }
        }

        user.updateUser(request.name(), request.age(), request.email(), "SYSTEM"); // update when JWT

        userRepository.saveUser(user);

        return userMapper.toDto(user);
    }

    @Transactional
    public void deleteUserById(UUID uuid){
        User user = findById(uuid);

        user.deleteUser("SYSTEM"); //change when jwt
        userRepository.saveUser(user);
    }

    @Transactional
    public UserDto promoteToAdmin(UUID uuid){
        User user = findById(uuid);
        user.promoteToAdmin();
        userRepository.saveUser(user);
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto changeRoleToEmployee(UUID uuid){
        User user = findById(uuid);
        user.changeRoleToEmployee();
        userRepository.saveUser(user);
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto activateUser(UUID uuid){
        User user = findById(uuid);
        user.activate();
        userRepository.saveUser(user);
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto deactivateUser(UUID uuid){
        User user = findById(uuid);
        user.deactivate();
        userRepository.saveUser(user);
        return userMapper.toDto(user);
    }
}
