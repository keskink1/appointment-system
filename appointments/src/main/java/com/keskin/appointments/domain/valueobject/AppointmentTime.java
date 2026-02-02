package com.keskin.appointments.domain.valueobject;

import com.keskin.common.exception.InvalidValidationException;

import java.time.LocalDateTime;

public record AppointmentTime (
        LocalDateTime time
){

    public AppointmentTime {
        if (time == null || time.isBefore(LocalDateTime.now())){
            throw new InvalidValidationException("Invalid appointment time");
        }
    }
}
