package com.january.ledgerflow.account.service;

import com.january.ledgerflow.account.domain.Account;
import com.january.ledgerflow.account.dto.AccountCreateRequestDTO;
import com.january.ledgerflow.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Long createAccount(@RequestBody AccountCreateRequestDTO accountCreateRequestDTO) {
        accountRepository.findByAccountNumber(accountCreateRequestDTO.getAccountNumber())
                .ifPresent(account -> {
                    throw new IllegalStateException("Account already exists");
                });

        Account account = new Account(
                accountCreateRequestDTO.getAccountNumber(),
                accountCreateRequestDTO.getUserId()
        );

        Account savedAccount = accountRepository.save(account);

        return savedAccount.getAccountId();
    }
}
