package com.keskin.appointments.infrastructure.persistence.message;


import com.keskin.appointments.domain.repository.UserShadowRepository;
import com.keskin.appointments.domain.valueobject.UserShadow;
import com.keskin.appointments.infrastructure.persistence.config.RabbitMQConfig;
import com.keskin.appointments.infrastructure.persistence.entity.UserShadowEntity;
import com.keskin.appointments.infrastructure.persistence.mapper.UserShadowPersistenceMapper;
import com.keskin.common.dto.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventListener {

    private final UserShadowRepository userShadowRepository;
    private final UserShadowPersistenceMapper userShadowPersistenceMapper;

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.USER_QUEUE)
    public void handleUserCreated(UserCreatedEvent event) {
            log.info("Received UserCreatedEvent for userId: {} with name: {}", event.userId(), event.name());

            UserShadowEntity entity = new UserShadowEntity(
                    event.userId(),
                    event.name(),
                    event.email()
            );

            //cast long timestamp to local date time
            entity.setSyncAt(Instant.ofEpochMilli(event.occurredAt())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime());

            UserShadow domain = userShadowPersistenceMapper.toDomain(entity);
            userShadowRepository.save(domain);

            log.info("Successfully replicated user {} to shadow table.", event.userId());
    }

}

