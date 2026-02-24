package com.keskin.appointments.infrastructure.persistence.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String USER_EXCHANGE = "user.exchange";
    public static final String USER_QUEUE = "appointment.user.queue";


    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(USER_EXCHANGE);
    }

    @Bean
    public Queue userQueue() {
        return new Queue(USER_QUEUE, true);
    }


    @Bean
    public Binding userCreatedBinding(Queue userQueue, TopicExchange userExchange) {
        return BindingBuilder
                .bind(userQueue)
                .to(userExchange)
                .with("user.created"); // Routing Key
    }

    @Bean
    public Binding userUpdatedBinding(Queue userQueue, TopicExchange userExchange) {
        return BindingBuilder
                .bind(userQueue)
                .to(userExchange)
                .with("user.updated"); // Routing Key
    }

    @Bean
    public Binding userDeletedBinding(Queue userQueue, TopicExchange userExchange){
        return BindingBuilder
                .bind(userQueue)
                .to(userExchange)
                .with("user.deleted");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}