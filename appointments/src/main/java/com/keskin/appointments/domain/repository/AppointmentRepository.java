package com.keskin.appointments.domain.repository;

import com.keskin.appointments.domain.model.Appointment;

import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepository {
    Optional<Appointment> findById(UUID id);

    void save(Appointment appointment);

}
