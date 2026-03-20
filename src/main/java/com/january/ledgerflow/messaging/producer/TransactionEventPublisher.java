package com.january.ledgerflow.messaging.producer;

import com.january.ledgerflow.messaging.config.RabbitMQConfig;
import com.january.ledgerflow.messaging.dto.TransactionEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(TransactionEventDTO transactionEventDTO) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                transactionEventDTO
        );
    }
}
