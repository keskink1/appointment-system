package com.keskin.appointments.infrastructure.persistence.message;


import com.keskin.appointments.domain.repository.UserShadowRepository;
import com.keskin.appointments.domain.valueobject.UserShadow;
import com.keskin.appointments.infrastructure.persistence.config.RabbitMQConfig;
import com.keskin.common.dto.event.UserCreatedEvent;
import com.keskin.common.dto.event.UserDeletedEvent;
import com.keskin.common.dto.event.UserUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventListener {

    private final UserShadowRepository userShadowRepository;

    private LocalDateTime convertTimestamp(long timestamp) {
        if (timestamp <= 0) return LocalDateTime.now();
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.USER_QUEUE)
    public void handleUserCreated(UserCreatedEvent event) {
        log.info("Processing UserCreatedEvent for ID: {}", event.userId());

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
    @RabbitListener(queues = RabbitMQConfig.USER_QUEUE)
    public void handleUserUpdated(UserUpdatedEvent event) {
        log.info("Processing UserUpdatedEvent for ID: {}", event.userId());

        userShadowRepository.findById(event.userId()).ifPresent(user -> {
            user.updateDetails(
                    event.name(),
                    event.email(),
                    true,
                    convertTimestamp(event.occurredAt())
            );
            userShadowRepository.save(user);
            log.info("User {} updated in shadow table.", event.userId());
        });
    }

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.USER_QUEUE)
    public void handleUserDeleted(UserDeletedEvent event) {
        log.info("Processing UserDeletedEvent for ID: {}", event.userId());

        userShadowRepository.findById(event.userId()).ifPresent(user -> {
            user.deactivate(convertTimestamp(event.occurredAt()));
            userShadowRepository.save(user);
            log.info("User {} deactivated in shadow table.", event.userId());
        });
    }
}