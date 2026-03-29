package com.keskin.appointments.application.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UpdateAppointmentDto(
        @Future @NotNull LocalDateTime time
) {
}
