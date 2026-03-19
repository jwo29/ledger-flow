package com.january.ledgerflow.account.controller;

import com.january.ledgerflow.account.dto.AccountCreateRequestDTO;
import com.january.ledgerflow.account.service.AccountService;
import com.january.ledgerflow.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ApiResponse<Long> createAccount(@RequestBody AccountCreateRequestDTO accountCreateRequestDTO) {
        Long accountId = accountService.createAccount(accountCreateRequestDTO);
        return ApiResponse.success(accountId);
    }

}
