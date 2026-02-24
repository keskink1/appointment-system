package com.keskin.appointments.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "app_user_shadow")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserShadowEntity {

    public UserShadowEntity(UUID userId, String userName, String userEmail,boolean userActive) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userActive = userActive;
    }

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "sync_at")
    private LocalDateTime syncAt = LocalDateTime.now();

    @Column(name = "is_user_active")
    private boolean userActive;
}
