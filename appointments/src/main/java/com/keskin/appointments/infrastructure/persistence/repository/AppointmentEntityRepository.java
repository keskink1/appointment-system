package com.keskin.appointments.infrastructure.persistence.repository;

import com.keskin.appointments.infrastructure.persistence.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AppointmentEntityRepository extends JpaRepository<AppointmentEntity, UUID> {
}