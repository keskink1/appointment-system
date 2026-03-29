package com.keskin.appointments.application.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateAppointmentRequestDto (
       @Future @NotNull LocalDateTime time
){
}
