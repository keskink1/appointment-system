package com.keskin.appointments.application.dto;

import java.time.LocalDateTime;

public record UpdateAppointmentDto(
        LocalDateTime time
) {
}
