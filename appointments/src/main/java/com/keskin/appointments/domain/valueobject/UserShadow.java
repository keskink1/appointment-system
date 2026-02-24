package com.keskin.appointments.domain.valueobject;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class UserShadow {
    private final UUID userId;
    private String name;
    private String email;
    private boolean userActive;
    private LocalDateTime syncAt;

    public UserShadow(UUID userId, String name, String email, boolean userActive, LocalDateTime syncAt) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.userActive = userActive;
        this.syncAt = syncAt;
    }

    public void updateDetails(String name, String email, boolean userActive, LocalDateTime syncTime) {
        this.name = name;
        this.email = email;
        this.userActive = userActive;
        this.syncAt = syncTime;
    }

    public void deactivate(LocalDateTime syncTime) {
        this.userActive = false;
        this.syncAt = syncTime;
    }
}