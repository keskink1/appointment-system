package com.keskin.appointments.domain.model;

import com.keskin.appointments.domain.valueobject.AppointmentTime;
import com.keskin.appointments.domain.valueobject.UserShadow;
import com.keskin.common.model.BaseEntity;
import jakarta.validation.ValidationException;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
public class Appointment extends BaseEntity {

    private AppointmentTime appointmentTime;

    private UserShadow user;

    private AppointmentStatus appointmentStatus;


    public Appointment(UUID uuid, LocalDateTime createdAt, String createdBy, boolean deleted, LocalDateTime deletedAt, String deletedBy, LocalDateTime timeValue, UUID userId, String userName, String userEmail, boolean userActive, AppointmentStatus appointmentStatus) {
        super(uuid, createdAt, createdBy, deleted, deletedAt, deletedBy);
        this.appointmentTime = new AppointmentTime(timeValue);
        this.user = new UserShadow(userId, userName, userEmail, userActive, LocalDateTime.now());
        this.appointmentStatus = appointmentStatus;
    }


    public static Appointment createAppointment(LocalDateTime appointmentTime, UUID userId, String userName, String userEmail, String createdBy) {

        if (appointmentTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Appointment time cannot be in the past");
        }

        return new Appointment(
                UUID.randomUUID(),
                LocalDateTime.now(),
                createdBy,
                false,
                null,
                null,
                appointmentTime,
                userId,
                userName,
                userEmail,
                true,
                AppointmentStatus.PENDING
        );
    }

    public void rescheduleAppointment(LocalDateTime appointmentTime, String actor){
        if (appointmentStatus.equals(AppointmentStatus.PENDING) || appointmentStatus.equals(AppointmentStatus.APPROVED)) {
            this.appointmentTime = new AppointmentTime(appointmentTime);
            this.appointmentStatus = AppointmentStatus.PENDING;
            this.updateAudit(actor);
        }

        if (appointmentTime.isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("Appointment time cannot be in the past.");
        }
    }

    public void approveAppointment(String actor){
        if (appointmentStatus.equals(AppointmentStatus.PENDING)){
            this.appointmentStatus = AppointmentStatus.APPROVED;
            this.updateAudit(actor);
        }
    }

    public void completeAppointment(String actor){
        if (appointmentStatus.equals(AppointmentStatus.APPROVED)){
            this.appointmentStatus = AppointmentStatus.COMPLETED;
            this.updateAudit(actor);
        }
    }

    public void cancelAppointment(String actor){
        if (appointmentStatus.equals(AppointmentStatus.COMPLETED)){
            throw new ValidationException("Completed appointment can't be canceled");
        }else if(appointmentStatus.equals(AppointmentStatus.CANCELED)){
            throw new ValidationException("Appointment already canceled");
        }else {
            this.appointmentStatus = AppointmentStatus.CANCELED;
            this.updateAudit(actor);
        }
    }

}
