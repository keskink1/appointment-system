package com.keskin.users.infrastructure.persistence.repository.impl;

import com.keskin.users.domain.model.User;
import com.keskin.users.domain.repository.UserRepository;
import com.keskin.users.infrastructure.persistence.entity.UserEntity;
import com.keskin.users.infrastructure.persistence.mapper.UserPersistenceMapper;
import com.keskin.users.infrastructure.persistence.repository.JpaUserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class SqlUserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaUserRepository;
    private final UserPersistenceMapper userMapper;

    public SqlUserRepositoryImpl(JpaUserRepository entityRepository, UserPersistenceMapper userMapper) {
        this.jpaUserRepository = entityRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Optional<User> findById(UUID id) {
       return jpaUserRepository.findById(id)
               .map(userEntity -> userMapper.toDomain(userEntity));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmailAndDeletedFalse(email)
                .map(user -> userMapper.toDomain(user));
    }

    @Override
    public void saveUser(User user) {
        UserEntity entity = userMapper.toEntity(user);
        jpaUserRepository.save(entity);
    }

    @Override
    public boolean existsByEmailAndDeletedFalse(String email) {
        return jpaUserRepository.existsByEmailAndDeletedFalse(email);
    }
}
