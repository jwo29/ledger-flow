package com.january.ledgerflow.messaging.consumer;

import com.january.ledgerflow.messaging.config.RabbitMQConfig;
import com.january.ledgerflow.messaging.dto.TransactionEventDTO;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@Log4j2
@RequiredArgsConstructor
public class TransactionEventListener {

    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void consume(Message message, Channel channel) throws Exception{

        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {


            // 1. 메시징 파싱
            TransactionEventDTO transactionEventDTO = objectMapper.readValue(
                    message.getBody(),
                    TransactionEventDTO.class);

            log.info("Received transaction event from RabbitMQ");
            log.info("message: {}", transactionEventDTO);

            // 2. 비즈니스 로직
            process(transactionEventDTO);

            // 3. 성공 → ACK
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("메시지 처리 실패: {}", message, e);
            // 4. 실패 → DLQ 이동
            channel.basicNack(deliveryTag, false, false);
        }
    }

    private void process(TransactionEventDTO transactionEventDTO) {
        // 메일 발송, 외부 API 호출 등
    }
}
