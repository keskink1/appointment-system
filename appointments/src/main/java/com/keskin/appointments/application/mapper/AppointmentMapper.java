package com.keskin.appointments.application.mapper;

import com.keskin.appointments.application.dto.AppointmentDto;
import com.keskin.appointments.domain.model.Appointment;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {
    public AppointmentDto toDto(Appointment appointment){
        if (appointment == null) return null;

        return new AppointmentDto(
                appointment.getUuid(),
                appointment.getAppointmentTime(),
                appointment.getUser(),
                appointment.getAppointmentStatus()
        );
    }
}
