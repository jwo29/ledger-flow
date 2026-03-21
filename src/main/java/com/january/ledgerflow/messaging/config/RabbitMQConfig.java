package com.january.ledgerflow.messaging.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE_NAME = "transaction.exchange";
    public static final String QUEUE_NAME = "transaction.queue";
    public static final String ROUTING_KEY = "transaction.created";

    public static final String DLX = "transaction.dlx"; // Dead Letter Exchange
    public static final String DLQ = "transaction.dlq"; // Dead Letter Queue

    // 메인 Exchange
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    // DLX (Dead Letter Exchange)
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX);
    }

    // 메인 Queue
    @Bean
    public Queue queue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY)
                .build();
    }

    // DLQ (Dead Letter Queue)
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ).build();
    }

    // 메인 Binding
    @Bean
    public Binding binding() {
        return BindingBuilder
                .bind(queue())
                .to(directExchange())
                .with(ROUTING_KEY);
    }

    // DLQ Binding
    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder
                .bind(deadLetterQueue())
                .to(deadLetterExchange())
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
