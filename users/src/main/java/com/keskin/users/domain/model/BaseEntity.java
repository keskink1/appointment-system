package com.keskin.users.domain.model;


import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public abstract class BaseEntity {

    private final UUID uuid;
    private final LocalDateTime createdAt;
    private final String createdBy;

    private LocalDateTime updatedAt;
    private String updatedBy;
    private LocalDateTime deletedAt;
    private String deletedBy;
    private boolean deleted = false;

    protected BaseEntity(UUID uuid, LocalDateTime createdAt, String createdBy){
        this.uuid = (uuid != null) ? uuid : UUID.randomUUID();
        this.createdAt = (createdAt != null) ? createdAt : LocalDateTime.now();        this.createdBy = createdBy;
    }

    protected BaseEntity(UUID uuid, LocalDateTime createdAt, String createdBy, boolean deleted, LocalDateTime deletedAt, String deletedBy) {
        this.uuid = uuid;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.deleted = deleted;
        this.deletedAt = deletedAt;
        this.deletedBy = deletedBy;
    }

    protected void markAsDeleted(String deletedBy) {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }

    protected void updateAudit(String updatedBy) {
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }
}