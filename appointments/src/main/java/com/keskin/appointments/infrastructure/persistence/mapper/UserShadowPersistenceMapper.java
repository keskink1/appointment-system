package com.keskin.appointments.infrastructure.persistence.mapper;

import com.keskin.appointments.domain.valueobject.UserShadow;
import com.keskin.appointments.infrastructure.persistence.entity.UserShadowEntity;
import org.springframework.stereotype.Component;

@Component
public class UserShadowPersistenceMapper {

    public UserShadow toDomain(UserShadowEntity entity) {
        if (entity == null) return null;

        return new UserShadow(
                entity.getUserId(),
                entity.getUserName(),
                entity.getUserEmail()
        );
    }

    public UserShadowEntity toEntity(UserShadow domain) {
        if (domain == null) return null;

        return new UserShadowEntity(
                domain.id(),
                domain.name(),
                domain.email()
        );
    }
}