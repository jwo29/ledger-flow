package com.january.ledgerflow.account.service;

import com.january.ledgerflow.account.domain.Account;
import com.january.ledgerflow.account.dto.AccountCreateRequestDTO;
import com.january.ledgerflow.account.repository.AccountRepository;
import com.january.ledgerflow.common.exception.CustomException;
import com.january.ledgerflow.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public Long createAccount(@RequestBody AccountCreateRequestDTO accountCreateRequestDTO) {
        accountRepository.findByAccountNumber(accountCreateRequestDTO.getAccountNumber())
                .ifPresent(account -> {
                    throw new CustomException(ErrorCode.ACCOUNT_ALREADY_EXISTS);
                });

        Account account = new Account(
                accountCreateRequestDTO.getAccountNumber(),
                accountCreateRequestDTO.getUserId()
        );

        Account savedAccount = accountRepository.save(account);

        return savedAccount.getAccountId();
    }

}
