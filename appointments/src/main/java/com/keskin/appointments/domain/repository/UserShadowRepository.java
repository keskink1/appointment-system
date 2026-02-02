package com.keskin.appointments.domain.repository;

import com.keskin.appointments.domain.valueobject.UserShadow;

import java.util.Optional;
import java.util.UUID;

public interface UserShadowRepository {
    Optional<UserShadow> findById(UUID id);
    void save(UserShadow userShadow);

}
