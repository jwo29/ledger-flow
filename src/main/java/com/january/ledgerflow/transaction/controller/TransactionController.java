package com.january.ledgerflow.transaction.controller;

import com.january.ledgerflow.common.response.ApiResponse;
import com.january.ledgerflow.transaction.dto.DepositRequestDTO;
import com.january.ledgerflow.transaction.dto.TransferRequestDTO;
import com.january.ledgerflow.transaction.dto.WithdrawRequestDTO;
import com.january.ledgerflow.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/{id}/deposit")
    public ApiResponse<Void> deposit(@RequestBody DepositRequestDTO depositRequestDTO, @PathVariable Long id) {
        transactionService.deposit(depositRequestDTO);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/withdraw")
    public ApiResponse<Void> withdraw(@RequestBody WithdrawRequestDTO withdrawRequestDTO, @PathVariable Long id) {
        transactionService.withdraw(withdrawRequestDTO);
        return ApiResponse.success(null);
    }

    @PostMapping("/transfer")
    public ApiResponse<Void> transfer(@RequestBody TransferRequestDTO transferRequestDTO) {
        /**
         * 계좌 이체(TRANSFER)
         * - A → B
         * - 하나의 트랜잭션으로 처리
         * - 동시성 안전
         * - 데드락 방지(계좌 A, B가 서로에게 이체하려는 경우) by 계좌 ID 정렬(항상 작은 ID 먼저 락)
         */
        transactionService.transfer(transferRequestDTO);
        return ApiResponse.success(null);
    }

}
