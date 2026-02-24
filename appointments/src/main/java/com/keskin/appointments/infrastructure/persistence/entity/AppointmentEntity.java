package com.keskin.appointments.infrastructure.persistence.entity;

import com.keskin.appointments.domain.model.AppointmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentEntity {
    @Id
    @Column(name = "appointment_id", updatable = false, nullable = false)
    private UUID uuid;

    @Column(name = "appointment_time")
    private LocalDateTime appointmentTime;

    @Column(name = "appointment_status")
    @Enumerated(EnumType.STRING)
    private AppointmentStatus appointmentStatus;


    // -- USER --
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserShadowEntity user;

    // -- BASE ENTITY --
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private String deletedBy;

}
