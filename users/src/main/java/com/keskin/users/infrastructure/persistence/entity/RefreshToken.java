package com.keskin.users.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "RefreshToken", timeToLive = 604800) // 7 days
public class RefreshToken implements Serializable {

    @Id
    @Column(name = "token")
    private String token;

    @Indexed
    @Column(name = "user_uuid")
    private UUID userUuid;

    @Column(name = "user_email")
    private String userEmail;
}