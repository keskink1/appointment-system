package com.keskin.appointments.infrastructure.persistence.mapper;

import com.keskin.appointments.domain.model.Appointment;
import com.keskin.appointments.infrastructure.persistence.entity.AppointmentEntity;
import com.keskin.appointments.infrastructure.persistence.entity.UserShadowEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentPersistenceMapper {

    private final UserShadowPersistenceMapper userShadowMapper;

    public AppointmentEntity toEntity(Appointment domain) {
        if (domain == null) return null;

        return new AppointmentEntity(
                domain.getUuid(),
                domain.getAppointmentTime().time(),
                domain.getAppointmentStatus(),
                userShadowMapper.toEntity(domain.getUser()),
                domain.getCreatedAt(),
                domain.getCreatedBy(),
                domain.getUpdatedAt(),
                domain.getUpdatedBy(),
                domain.isDeleted(),
                domain.getDeletedAt(),
                domain.getDeletedBy()
        );
    }

    public Appointment toDomain(AppointmentEntity entity) {
        if (entity == null) return null;

        UserShadowEntity userEntity = entity.getUser();

        return new Appointment(
                entity.getUuid(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.isDeleted(),
                entity.getDeletedAt(),
                entity.getDeletedBy(),
                entity.getAppointmentTime(),
                userEntity.getUserId(),
                userEntity.getUserName(),
                userEntity.getUserEmail(),
                userEntity.isUserActive(),
                entity.getAppointmentStatus()
        );
    }
}