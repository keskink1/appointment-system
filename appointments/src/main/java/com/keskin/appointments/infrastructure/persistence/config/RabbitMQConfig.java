package com.keskin.appointments.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // User Service'teki ile aynı olmalı
    public static final String USER_EXCHANGE = "user.exchange";

    // Sadece bu servise özel kuyruk ismi
    public static final String USER_QUEUE = "appointment.user.queue";

    /**
     * Postane (Exchange) tanımı.
     * User Service zaten oluşturdu ama burada da tanımlamak güvenlidir (Durable).
     */
    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(USER_EXCHANGE);
    }

    /**
     * Kendi posta kutumuz (Queue).
     * Mesajlar burada birikecek.
     */
    @Bean
    public Queue userQueue() {
        return new Queue(USER_QUEUE, true); // durable: true (RabbitMQ kapanırsa kuyruk silinmez)
    }

    /**
     * Köprü (Binding).
     * "user.exchange" postanesine gelen "user.created" etiketli mektupları
     * "appointment.user.queue" kutusuna yönlendir.
     */
    @Bean
    public Binding userCreatedBinding(Queue userQueue, TopicExchange userExchange) {
        return BindingBuilder
                .bind(userQueue)
                .to(userExchange)
                .with("user.created"); // Routing Key
    }

    /**
     * JSON okuyabilmek için Converter.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}