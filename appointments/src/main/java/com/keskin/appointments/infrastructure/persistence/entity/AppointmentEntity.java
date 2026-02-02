package com.keskin.appointments.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class AppointmentEntity {
    @Id
    private UUID uuid;
}
