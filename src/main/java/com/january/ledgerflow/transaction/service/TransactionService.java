package com.january.ledgerflow.transaction.service;

import com.january.ledgerflow.account.domain.Account;
import com.january.ledgerflow.transaction.dto.DepositRequestDTO;
import com.january.ledgerflow.transaction.dto.TransferRequestDTO;
import com.january.ledgerflow.transaction.dto.WithdrawRequestDTO;
import com.january.ledgerflow.account.repository.AccountRepository;
import com.january.ledgerflow.transaction.domain.AccountLedger;
import com.january.ledgerflow.transaction.domain.Transaction;
import com.january.ledgerflow.transaction.repository.LedgerRepository;
import com.january.ledgerflow.transaction.repository.TransactionRepository;
import com.january.ledgerflow.transaction.vo.EntryType;
import com.january.ledgerflow.transaction.vo.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final LedgerRepository ledgerRepository;

    @Transactional
    public void deposit(DepositRequestDTO depositRequestDTO) {
        Account account = accountRepository.findByIdForUpdate(depositRequestDTO.getAccountId())
                .orElseThrow(() -> new IllegalStateException("Account not found"));

        account.deposit(depositRequestDTO.getAmount());

        Transaction transaction = transactionRepository.save(
                new Transaction(
                        TransactionType.DEPOSIT,
                        depositRequestDTO.getAmount(),
                        null,
                        depositRequestDTO.getAccountId()));

        ledgerRepository.save(
                new AccountLedger(
                        depositRequestDTO.getAccountId(),
                        transaction.getTransactionId(),
                        EntryType.CREDIT,
                        depositRequestDTO.getAmount(),
                        account.getBalance())
        );
    }

    @Transactional
    public void withdraw(WithdrawRequestDTO withdrawRequestDTO) {
        Account account = accountRepository.findByIdForUpdate(withdrawRequestDTO.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        account.withdraw(withdrawRequestDTO.getAmount());

        Transaction transaction = transactionRepository.save(
                new Transaction(TransactionType.WITHDRAW, withdrawRequestDTO.getAmount(), account.getAccountId(), null)
        );

        ledgerRepository.save(
                new AccountLedger(account.getAccountId(), transaction.getTransactionId(), EntryType.DEBIT, withdrawRequestDTO.getAmount(), account.getBalance())
        );
    }

    @Transactional
    public void transfer(TransferRequestDTO transferRequestDTO) {
        Long fromAccountId = transferRequestDTO.getFromAccountId();
        Long toAccountId = transferRequestDTO.getToAccountId();
        BigDecimal amount = transferRequestDTO.getAmount();

        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalStateException("같은 계좌 이체 불가");
        }

        // 1. 정렬 (데드락 방지 핵심)
        Long firstAccountId = Math.min(fromAccountId, toAccountId);
        Long secondAccountId = Math.max(fromAccountId, toAccountId);

        Account firstAccount = accountRepository.findByIdForUpdate(firstAccountId)
                .orElseThrow(() -> new IllegalStateException("Account not found"));

        Account secondAccount = accountRepository.findByIdForUpdate(secondAccountId)
                .orElseThrow(() -> new IllegalStateException("Account not found"));

        // 2. 실제 계좌 매핑
        Account fromAccount = firstAccount.getAccountId().equals(fromAccountId) ? firstAccount : secondAccount;
        Account toAccount = firstAccount.getAccountId().equals(toAccountId) ? firstAccount : secondAccount;

        // 3. 출금 (잔액 검증 포함)
        fromAccount.withdraw(amount);

        // 4. 입금
        toAccount.deposit(amount);

        // 5. 거래 생성
        Transaction transaction = transactionRepository.save(
                new Transaction(TransactionType.TRANSFER, amount, fromAccountId, secondAccountId)
        );

        // 6. 원장(Ledger) 기록 (2건)
        ledgerRepository.save(
                new AccountLedger(fromAccountId, transaction.getTransactionId(), EntryType.DEBIT, amount, fromAccount.getBalance())
        );

        ledgerRepository.save(
                new AccountLedger(toAccountId, transaction.getTransactionId(), EntryType.CREDIT, amount, toAccount.getBalance())
        );

    }
}
