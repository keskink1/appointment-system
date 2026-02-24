package com.keskin.appointments.domain.model;

import com.keskin.appointments.domain.valueobject.AppointmentTime;
import com.keskin.appointments.domain.valueobject.UserShadow;
import com.keskin.common.exception.ResourceAlreadyExistsException;
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


    public static Appointment createAppointment(LocalDateTime time,String createdBy, UUID userId, String userName, String userEmail) {
        return new Appointment(
                UUID.randomUUID(),
                LocalDateTime.now(),
                createdBy,
                false,
                null,
                null,
                time,
                userId,
                userName,
                userEmail,
                true,
                AppointmentStatus.PENDING
        );
    }

    public void syncUserShadow(String name, String email, boolean isActive, LocalDateTime syncTime) {
        this.user = new UserShadow(
                this.user.getUserId(),
                name,
                email,
                isActive,
                syncTime
        );
    }

    public void rescheduleAppointment(LocalDateTime time, String actor){
        if (appointmentStatus.equals(AppointmentStatus.PENDING) || appointmentStatus.equals(AppointmentStatus.APPROVED)) {
            this.appointmentTime = new AppointmentTime(time);
            this.appointmentStatus = AppointmentStatus.PENDING;
            this.updateAudit(actor);
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

    public void checkConflict(LocalDateTime otherTime) {
        if (this.appointmentStatus != AppointmentStatus.CANCELED &&
                this.appointmentTime.time().equals(otherTime)) {
            throw new ResourceAlreadyExistsException("Appointment", "time", otherTime.toString());
        }
    }

}
