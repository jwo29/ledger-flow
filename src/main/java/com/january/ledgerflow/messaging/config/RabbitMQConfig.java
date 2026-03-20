package com.january.ledgerflow.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE_NAME = "transaction.exchange";
    public static final String QUEUE_NAME = "transaction.queue";
    public static final String ROUTING_KEY = "transaction.created";

    // Exchange
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    // Queue
    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME);
    }

    // Binding
    @Bean
    public Binding binding(DirectExchange directExchange, Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(directExchange)
                .with(ROUTING_KEY);
    }

    // MessageConverter (JSON)
    // * Spring Boot는 기본적으로 RabbitTemplate을 자동으로 생성함.
    // 커스텀 RabbitTemplate을 정의하는 이유 : 직렬화 전략을 바꾸기 위해 override.
    // - 기본 → SimpleMessageConverter (byte[])
    // - 커스텀 → JSON Converter
    // **MessageConverter만 Bean으로 등록해두면 Spring Boot가 자동으로 RabbitTemplate에 적용함.**
    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

}
