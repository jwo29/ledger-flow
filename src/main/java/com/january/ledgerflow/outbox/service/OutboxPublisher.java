package com.january.ledgerflow.outbox.service;

import com.january.ledgerflow.messaging.config.RabbitMQConfig;
import com.january.ledgerflow.outbox.domain.OutboxEvent;
import com.january.ledgerflow.outbox.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final RabbitTemplate rabbitTemplate;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void publish() {

        List<OutboxEvent> events = outboxEventRepository.findTop100ByStatus("PENDING");

        for (OutboxEvent event : events) {
            try {
                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.EXCHANGE_NAME,
                        RabbitMQConfig.QUEUE_NAME,
                        event.getPayload()
                );

                event.markSent();

            } catch (Exception e) {
                event.markFailed();
            }
        }
    }
}
