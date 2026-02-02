package com.keskin.appointments.infrastructure.persistence.repository;

import com.keskin.appointments.domain.model.AppointmentStatus;
import com.keskin.appointments.infrastructure.persistence.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AppointmentJpaRepository extends JpaRepository<AppointmentEntity, UUID> {

    @Query("SELECT a FROM AppointmentEntity a WHERE a.userId= :id AND a.appointmentStatus!= :status")
    List<AppointmentEntity> findByUserIdAndAppointmentStatusNotCanceled(
            @Param("id")UUID id,
            @Param("status") AppointmentStatus status
    );
}