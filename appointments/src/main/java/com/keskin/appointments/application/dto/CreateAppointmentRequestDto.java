package com.keskin.appointments.application.dto;

import java.time.LocalDateTime;

public record CreateAppointmentRequestDto (
        LocalDateTime time
){
}
