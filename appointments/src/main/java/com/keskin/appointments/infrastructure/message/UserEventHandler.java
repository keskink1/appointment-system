package com.keskin.appointments.infrastructure.message;

import com.keskin.appointments.domain.repository.UserShadowRepository;
import com.keskin.appointments.domain.valueobject.UserShadow;
import com.keskin.common.dto.event.UserCreatedEvent;
import com.keskin.common.dto.event.UserDeletedEvent;
import com.keskin.common.dto.event.UserUpdatedEvent;
import com.keskin.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventHandler {

    private final UserShadowRepository userShadowRepository;

    @Transactional
    public void handleUserCreated(UserCreatedEvent event) {
        log.info("Processing UserCreatedEvent for ID: {}", event.userId());

        if (userShadowRepository.findById(event.userId()).isPresent()) {
            log.warn("User {} already exists in shadow table, skipping", event.userId());
            return;
        }

        UserShadow domain = new UserShadow(
                event.userId(),
                event.name(),
                event.email(),
                true,
                convertTimestamp(event.occurredAt())
        );

        userShadowRepository.save(domain);
        log.info("User {} replicated to shadow table at {}", event.userId(), domain.getSyncAt());
    }

    @Transactional
    public void handleUserUpdated(UserUpdatedEvent event) {
        log.info("Processing UserUpdatedEvent for ID: {}", event.userId());

        UserShadow user = userShadowRepository.findById(event.userId())
                .orElseThrow(() -> {
                    log.error("User {} not found in shadow table for update!", event.userId());
                    return new ResourceNotFoundException("User", "ID", event.userId());
                });

        user.updateDetails(
                event.name(),
                event.email(),
                true,
                convertTimestamp(event.occurredAt())
        );

        userShadowRepository.save(user);
        log.info("User {} updated in shadow table.", event.userId());
    }

    @Transactional
    public void handleUserDeleted(UserDeletedEvent event) {
        log.info("Processing UserDeletedEvent for ID: {}", event.userId());

        UserShadow user = userShadowRepository.findById(event.userId())
                .orElseThrow(() -> {
                    log.error("User {} not found in shadow table for deletion!", event.userId());
                    return new ResourceNotFoundException("User", "ID", event.userId());
                });

        user.deactivate(convertTimestamp(event.occurredAt()));
        userShadowRepository.save(user);
        log.info("User {} deactivated in shadow table.", event.userId());
    }

    private LocalDateTime convertTimestamp(long timestamp) {
        if (timestamp <= 0) return LocalDateTime.now();
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
