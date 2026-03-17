package com.january.ledgerflow.account.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class AccountCreateRequestDTO {

    private String accountNumber;
    private Long userId;

    public AccountCreateRequestDTO(String accountNumber, Long userId) {
        this.accountNumber = accountNumber;
        this.userId = userId;
    }
}
