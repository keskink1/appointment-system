package com.keskin.users.infrastructure.persistence.mapper;

import com.keskin.users.domain.model.User;
import com.keskin.users.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {
    public UserEntity toEntity(User domain) {
        if (domain == null) return null;

        return new UserEntity(
                domain.getUuid(),
                domain.getName().value(),
                domain.getEmail().value(),
                domain.getPassword().value(),
                domain.getAge().value(),
                domain.getRole(),
                domain.isActive(),
                domain.getCreatedAt(),
                domain.getCreatedBy(),
                domain.getUpdatedAt(),
                domain.getUpdatedBy(),
                domain.isDeleted(),
                domain.getDeletedAt(),
                domain.getDeletedBy()
        );
    }

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;

        return new User(
                entity.getUuid(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.isDeleted(),
                entity.getDeletedAt(),
                entity.getDeletedBy(),
                entity.getName(),
                entity.getAge(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getRole(),
                entity.isActive()
        );
    }
}
