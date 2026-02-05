package com.keskin.users.infrastructure.persistence.repository;

import com.keskin.users.infrastructure.persistence.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByToken(String token);
}