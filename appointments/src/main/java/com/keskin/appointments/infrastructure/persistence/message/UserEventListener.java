package com.keskin.appointments.infrastructure.persistence.message;


import com.keskin.appointments.domain.repository.UserShadowRepository;
import com.keskin.appointments.domain.valueobject.UserShadow;
import com.keskin.appointments.infrastructure.persistence.config.RabbitMQConfig;
import com.keskin.appointments.infrastructure.persistence.entity.UserShadowEntity;
import com.keskin.appointments.infrastructure.persistence.mapper.UserShadowPersistenceMapper;
import com.keskin.common.dto.event.UserCreatedEvent;
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
    private final UserShadowPersistenceMapper userShadowPersistenceMapper;

    private LocalDateTime convertTimestamp(long timestamp){
        if (timestamp <= 0) return LocalDateTime.now();
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.USER_QUEUE)
    public void handleUserCreated(UserCreatedEvent event) {
            log.info("Received UserCreatedEvent for userId: {} with name: {}", event.userId(), event.name());

            UserShadowEntity entity = new UserShadowEntity(
                    event.userId(),
                    event.name(),
                    event.email(),
                    true
            );

            entity.setSyncAt(convertTimestamp(event.occurredAt()));

            UserShadow domain = userShadowPersistenceMapper.toDomain(entity);
            userShadowRepository.save(domain);

            log.info("Successfully replicated user with the id of {} to shadow table.", event.userId());
    }

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.USER_QUEUE)
    public void handleUserUpdated (UserUpdatedEvent event) {
        log.info("Received UserUpdatedEvent for userId: {} with name: {}", event.userId(), event.name());

        UserShadowEntity entity = new UserShadowEntity(
                event.userId(),
                event.name(),
                event.email(),
                true
        );

        entity.setSyncAt(convertTimestamp(event.occurredAt()));

        UserShadow domain = userShadowPersistenceMapper.toDomain(entity);
        userShadowRepository.save(domain);

        log.info("Successfully updated user with the id of {} to shadow table.", event.userId());

    }
}

