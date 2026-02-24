package com.keskin.users.infrastructure.persistence.message;

import com.keskin.common.dto.event.UserCreatedEvent;
import com.keskin.common.dto.event.UserDeletedEvent;
import com.keskin.common.dto.event.UserUpdatedEvent;
import com.keskin.users.infrastructure.persistence.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishUserCreated(UserCreatedEvent event){
        log.info("Publishing user created event for the id of {} ", event.userId());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.USER_EXCHANGE,
                "user.created",
                event
        );
    }

    public void publishUserUpdated(UserUpdatedEvent event){
        log.info("Publishing user updated event for the id of {} ", event.userId());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.USER_EXCHANGE,
                "user.updated",
                event
        );
    }

    public void publishUserDeleted(UserDeletedEvent event){
        log.info("Publishing user deleted event for the id of {} ", event.userId());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.USER_EXCHANGE,
                "user.deleted",
                event
        );
    }
}
