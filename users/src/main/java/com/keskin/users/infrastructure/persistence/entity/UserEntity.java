package com.keskin.users.infrastructure.persistence.entity;

import com.keskin.common.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * User entity representing the persistent storage of user data in the "app_users" table.
 * * <p>Performance Optimization - Indexing Strategy:</p>
 * <ul>
 * <li><b>idx_user_name:</b> Applied to the 'name' column to speed up search and sorting operations by name.</li>
 * <li><b>idx_user_email_active:</b> A composite index on 'email' and 'active'. This optimizes queries
 * that filter users by both their email and account status (e.g., during login or status checks).</li>
 * <li><b>Note on 'email' index:</b> A separate index for the 'email' column is not manually defined here
 * because the {@code unique = true} constraint on the field automatically triggers the creation
 * of a unique index in most relational databases.</li>
 * </ul>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "app_users",
        indexes = {
                @Index(name = "idx_user_name", columnList = "name"),
                @Index(name = "idx_user_email_active", columnList = "email, active")
        }
)
public class UserEntity {

    @Id
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID uuid;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "age")
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "active", nullable = false)
    private boolean active;

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