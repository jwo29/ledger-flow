package com.january.ledgerflow.transaction.domain;

import com.january.ledgerflow.transaction.vo.TransactionStatus;
import com.january.ledgerflow.transaction.vo.TransactionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @Column(nullable = false)
    private String transactionType;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column
    private Long fromAccountId;

    @Column
    private Long toAccountId;

    @Column(nullable = false)
    private String status;

    private LocalDateTime createdAt;

    public Transaction (TransactionType transactionType, BigDecimal amount, Long fromAccountId, Long toAccountId) {
        this.transactionType = transactionType.name();
        this.amount = amount;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.status = TransactionStatus.SUCCESS.name();
        this.createdAt = LocalDateTime.now();
    }

    public void success() {
        this.status = TransactionStatus.SUCCESS.name();
    }

    public void fail() {
        this.status = TransactionStatus.FAILED.name();
    }
}
