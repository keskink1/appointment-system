package com.keskin.appointments.infrastructure.persistence.repository;

import com.keskin.appointments.domain.valueobject.UserShadow;
import com.keskin.appointments.infrastructure.persistence.entity.UserShadowEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserShadowJpaRepository extends JpaRepository<UserShadowEntity, UUID> {
    Optional<UserShadowEntity> findById(UUID id);
    void save(UserShadow userShadow);
}