package com.january.ledgerflow.messaging.dto;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@ToString
public class TransactionEventDTO {

    private Long transactionId;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;

    public TransactionEventDTO(Long transactionId, BigDecimal amount, String status, LocalDateTime createdAt) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }
}
