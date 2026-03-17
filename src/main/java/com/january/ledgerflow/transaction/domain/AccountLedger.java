package com.january.ledgerflow.transaction.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 원장(Ledger)은 절대 UPDATE 금지.
 * 즉, account_ledgers 테이블은 append only 이며,
 * 금융 시스템 핵심 원칙이다
 */
@Entity
@Table(name = "account_ledgers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ledgerId;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false)
    private Long transactionId;

    @Column(nullable = false)
    private String entryType;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    private LocalDateTime createdAt;

    public AccountLedger(Long accountId, Long transactionId, String entryType, BigDecimal amount, BigDecimal balanceAfter) {
        this.accountId = accountId;
        this.transactionId = transactionId;
        this.entryType = entryType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.createdAt = LocalDateTime.now();
    }
}
