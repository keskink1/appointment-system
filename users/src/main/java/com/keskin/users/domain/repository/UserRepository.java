package com.keskin.users.domain.repository;

import com.keskin.users.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain Port for User persistence operations.
 * This interface defines the contract that infrastructure adapters must implement.
 */
public interface UserRepository {

    /**
     * @return A list of all active users. Implementation should handle pagination.
     */
    List<User> findAllUsers();

    /**
     * @param id The unique identifier of the user.
     * @return Optional user domain object.
     */
    Optional<User> findById(UUID id);

    /**
     * @param email The unique email to search for.
     * @return Optional user if found and not soft-deleted.
     */
    Optional<User> findByEmail(String email);

    /**
     * Persists the domain model to the chosen data store.
     * @param user The domain object to be saved.
     */
    void saveUser(User user);

    /**
     * Business validation to check if email is already taken.
     */
    boolean existsByEmailAndDeletedFalse(String email);
}
