package com.keskin.appointments.domain.repository;

import com.keskin.appointments.domain.model.Appointment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepository {
    Optional<Appointment> findById(UUID id);

    Appointment save(Appointment appointment);

    //show active appointments of user
    List<Appointment> findByUserIdAndNotCanceled(UUID id);

}
