package com.january.ledgerflow.messaging.consumer;

import com.january.ledgerflow.messaging.config.RabbitMQConfig;
import com.january.ledgerflow.messaging.domain.ProcessedEvent;
import com.january.ledgerflow.messaging.dto.TransactionEventDTO;
import com.january.ledgerflow.messaging.repository.ProcessedEventRepository;
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

    private final ProcessedEventRepository processedEventRepository;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void consume(Message message, Channel channel) throws Exception{

        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {

            // 1. 메시징 파싱
            TransactionEventDTO transactionEventDTO = objectMapper.readValue(
                    message.getBody(),
                    TransactionEventDTO.class);

            String idempotencyKey = transactionEventDTO.getTransactionId().toString();

            log.info("Received transaction event from RabbitMQ");
            log.info("message: {}", transactionEventDTO);

            // 2. 멱등성 확인
            if (processedEventRepository.existsById(idempotencyKey)) {
                log.info("중복 메시지 → ACK 후 스킵");
                channel.basicAck(deliveryTag, false); // 멱등성 체크 후 return할 때도 ACK 필수. ACK 보내지 않으면 중복 메시지가 계속 들어옴.
                return;
            }

            // 3. 비즈니스 로직
            process(transactionEventDTO);

            // 4. 처리 완료 기록(DB, MQ 정합성을 위해 ACK보다 앞서야 한다)
            processedEventRepository.save(
                    new ProcessedEvent(idempotencyKey)
            );

            // 5. 성공 → ACK
            channel.basicAck(deliveryTag, false);


        } catch (Exception e) {
            log.error("메시지 처리 실패: {}", message, e);

            // 6. 실패 → DLQ 이동
            channel.basicNack(deliveryTag, false, false);
        }
    }

    private void process(TransactionEventDTO transactionEventDTO) {
        // 메일 발송, 외부 API 호출 등
        // todo 정산
    }
}
