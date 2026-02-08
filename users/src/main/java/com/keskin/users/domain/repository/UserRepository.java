package com.keskin.users.domain.repository;

import com.keskin.common.dto.response.PaginatedResponseDto;
import com.keskin.users.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain Port for User persistence operations.
 * This interface defines the contract that infrastructure adapters must implement.
 */
public interface UserRepository {

    PaginatedResponseDto<User> findAllUsers(int page, int size);


    Optional<User> findById(UUID id);


    Optional<User> findByEmail(String email);

    void saveUser(User user);

    /**
     * Business validation to check if email is already taken and if user is active.
     */
    boolean existsByEmailAndDeletedFalse(String email);
}
