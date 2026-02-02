package com.keskin.appointments.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.keskin.appointments.domain.model.AppointmentStatus;
import com.keskin.appointments.domain.valueobject.AppointmentTime;
import com.keskin.appointments.domain.valueobject.UserShadow;

import java.util.UUID;

public record AppointmentDto(
        @JsonIgnore
        UUID id,
        AppointmentTime appointmentTime,
        UserShadow user,
        AppointmentStatus appointmentStatus
) {
}
