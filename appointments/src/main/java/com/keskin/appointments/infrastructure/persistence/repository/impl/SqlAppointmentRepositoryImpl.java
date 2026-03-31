package com.keskin.appointments.infrastructure.persistence.repository.impl;

import com.keskin.appointments.domain.model.Appointment;
import com.keskin.appointments.domain.model.AppointmentStatus;
import com.keskin.appointments.domain.repository.AppointmentRepository;
import com.keskin.appointments.infrastructure.persistence.entity.AppointmentEntity;
import com.keskin.appointments.infrastructure.persistence.mapper.AppointmentPersistenceMapper;
import com.keskin.appointments.infrastructure.persistence.repository.AppointmentJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SqlAppointmentRepositoryImpl implements AppointmentRepository {

    private final AppointmentJpaRepository appointmentRepository;
    private final AppointmentPersistenceMapper appointmentMapper;

    public SqlAppointmentRepositoryImpl(AppointmentJpaRepository appointmentRepository, AppointmentPersistenceMapper appointmentMapper) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
    }

    @Override
    public Optional<Appointment> findById(UUID id) {
        return appointmentRepository.findById(id)
                .map(appointmentMapper::toDomain);
    }

    @Override
    public Appointment save(Appointment appointment) {
        var entity = appointmentMapper.toEntity(appointment);
        AppointmentEntity createdEntity = appointmentRepository.save(entity);
        return appointmentMapper.toDomain(createdEntity);
    }

    @Override
    public List<Appointment> findByUserIdAndNotCanceled(UUID id) {
        return appointmentRepository.findByUserIdAndAppointmentStatusNotCanceled(id, AppointmentStatus.CANCELED)
                .stream()
                .map(appointmentMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByTimeAndUserId(LocalDateTime time, UUID userId) {
        return appointmentRepository.existsByAppointmentTimeAndUser_UserId(time,userId);
    }
}
