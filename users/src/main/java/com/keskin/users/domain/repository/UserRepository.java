package com.keskin.users.domain.repository;

import com.keskin.users.domain.model.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    void saveUser(User user);

    boolean existsByEmailAndDeletedFalse(String email);

}
