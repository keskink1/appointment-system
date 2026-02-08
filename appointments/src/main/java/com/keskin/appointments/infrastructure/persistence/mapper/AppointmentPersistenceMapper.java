package com.keskin.appointments.infrastructure.persistence.mapper;

import com.keskin.appointments.domain.model.Appointment;
import com.keskin.appointments.infrastructure.persistence.entity.AppointmentEntity;
import org.springframework.stereotype.Component;

@Component
public class AppointmentPersistenceMapper {

    public AppointmentEntity toEntity(Appointment domain){
        if (domain == null) return null;

        return new AppointmentEntity(
                domain.getUuid(),
                domain.getAppointmentTime().time(),
                domain.getUser().id(),
                domain.getUser().name(),
                domain.getUser().email(),
                domain.getAppointmentStatus(),
                domain.getCreatedAt(),
                domain.getCreatedBy(),
                domain.getUpdatedAt(),
                domain.getUpdatedBy(),
                domain.isDeleted(),
                domain.getDeletedAt(),
                domain.getDeletedBy()
        );
    }

    public Appointment toDomain(AppointmentEntity entity){
        if (entity == null) return null;

        return new Appointment(
                entity.getUuid(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.isDeleted(),
                entity.getDeletedAt(),
                entity.getDeletedBy(),
                entity.getAppointmentTime(),
                entity.getUserId(),
                entity.getUserName(),
                entity.getUserEmail(),
                entity.getAppointmentStatus()
        );
    }
}
