package com.keskin.appointments.infrastructure.persistence.repository.impl;

import com.keskin.appointments.domain.repository.UserShadowRepository;
import com.keskin.appointments.domain.valueobject.UserShadow;
import com.keskin.appointments.infrastructure.persistence.mapper.UserShadowPersistenceMapper;
import com.keskin.appointments.infrastructure.persistence.repository.UserShadowJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SqlUserShadowRepositoryImpl implements UserShadowRepository {

    private final UserShadowJpaRepository jpaRepository;
    private final UserShadowPersistenceMapper mapper;

    @Override
    public Optional<UserShadow> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public void save(UserShadow userShadow) {
        jpaRepository.save(mapper.toEntity(userShadow));
    }
}