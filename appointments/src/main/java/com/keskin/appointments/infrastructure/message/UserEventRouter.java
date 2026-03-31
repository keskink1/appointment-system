package com.keskin.appointments.infrastructure.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keskin.appointments.infrastructure.message.config.AppointmentRabbitMQConfig;
import com.keskin.common.dto.event.UserCreatedEvent;
import com.keskin.common.dto.event.UserDeletedEvent;
import com.keskin.common.dto.event.UserUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventRouter {

    private final ObjectMapper objectMapper;
    private final UserEventHandler userEventHandler;

    @RabbitListener(queues = AppointmentRabbitMQConfig.USER_QUEUE)
    public void routeUserEvent(Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();

        try {
            log.info("Received message with routing key: {}", routingKey);

            switch (routingKey) {
                case "user.created" -> {
                    UserCreatedEvent event = objectMapper.readValue(
                            message.getBody(),
                            UserCreatedEvent.class
                    );
                    userEventHandler.handleUserCreated(event);
                }
                case "user.updated" -> {
                    UserUpdatedEvent event = objectMapper.readValue(
                            message.getBody(),
                            UserUpdatedEvent.class
                    );
                    userEventHandler.handleUserUpdated(event);
                }
                case "user.deleted" -> {
                    UserDeletedEvent event = objectMapper.readValue(
                            message.getBody(),
                            UserDeletedEvent.class
                    );
                    userEventHandler.handleUserDeleted(event);
                }
                default -> log.warn("Unknown routing key: {}", routingKey);
            }

        } catch (Exception e) {
            log.error("Failed to process message with routing key {}: {}",
                    routingKey, e.getMessage(), e);
            throw new RuntimeException("Message processing failed", e);
        }
    }
}