package com.january.ledgerflow.transaction.service;

import com.january.ledgerflow.account.domain.Account;
import com.january.ledgerflow.common.exception.CustomException;
import com.january.ledgerflow.common.exception.ErrorCode;
import com.january.ledgerflow.messaging.dto.TransactionEventDTO;
import com.january.ledgerflow.messaging.producer.TransactionEventPublisher;
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
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final LedgerRepository ledgerRepository;

    private final TransactionEventPublisher transactionEventPublisher;

    @Transactional
    public void deposit(DepositRequestDTO depositRequestDTO) {
        Account account = accountRepository.findByIdForUpdate(depositRequestDTO.getAccountId())
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        account.deposit(depositRequestDTO.getAmount());

        Transaction transaction = new Transaction(
                TransactionType.DEPOSIT,
                depositRequestDTO.getAmount(),
                null,
                depositRequestDTO.getAccountId());

        try {
            transactionRepository.save(transaction);

            ledgerRepository.save(
                    new AccountLedger(
                            depositRequestDTO.getAccountId(),
                            transaction.getTransactionId(),
                            EntryType.CREDIT,
                            depositRequestDTO.getAmount(),
                            account.getBalance())
            );

            transaction.success();

            /************ 이벤트 발행 ************/
            TransactionEventDTO eventDTO = new TransactionEventDTO(
                    transaction.getTransactionId(),
                    transaction.getAmount(),
                    "CREATED",
                    LocalDateTime.now()
            );

            transactionEventPublisher.publish(eventDTO);

        } catch (Exception e) {
            transaction.fail();
            throw e;
        }

    }

    @Transactional
    public void withdraw(WithdrawRequestDTO withdrawRequestDTO) {
        Account account = accountRepository.findByIdForUpdate(withdrawRequestDTO.getAccountId())
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        account.withdraw(withdrawRequestDTO.getAmount());

        Transaction transaction = new Transaction(TransactionType.WITHDRAW, withdrawRequestDTO.getAmount(), account.getAccountId(), null);

        try {
            transactionRepository.save(transaction);

            ledgerRepository.save(
                    new AccountLedger(account.getAccountId(), transaction.getTransactionId(), EntryType.DEBIT, withdrawRequestDTO.getAmount(), account.getBalance())
            );
            transaction.success();

            /************ 이벤트 발행 ************/
            TransactionEventDTO eventDTO = new TransactionEventDTO(
                    transaction.getTransactionId(),
                    transaction.getAmount(),
                    "CREATED",
                    LocalDateTime.now()
            );

            transactionEventPublisher.publish(eventDTO);
        } catch (Exception e) {
            transaction.fail();
            throw e;
        }
    }

    @Transactional
    public void transfer(TransferRequestDTO transferRequestDTO) {
        Long fromAccountId = transferRequestDTO.getFromAccountId();
        Long toAccountId = transferRequestDTO.getToAccountId();
        BigDecimal amount = transferRequestDTO.getAmount();

        if (fromAccountId.equals(toAccountId)) {
            throw new CustomException(ErrorCode.INVALID_TRANSFER);
        }

        // 1. 정렬 (데드락 방지 핵심)
        Long firstAccountId = Math.min(fromAccountId, toAccountId);
        Long secondAccountId = Math.max(fromAccountId, toAccountId);

        Account firstAccount = accountRepository.findByIdForUpdate(firstAccountId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        Account secondAccount = accountRepository.findByIdForUpdate(secondAccountId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 2. 실제 계좌 매핑
        Account fromAccount = firstAccount.getAccountId().equals(fromAccountId) ? firstAccount : secondAccount;
        Account toAccount = firstAccount.getAccountId().equals(toAccountId) ? firstAccount : secondAccount;

        // 3. 출금 (잔액 검증 포함)
        fromAccount.withdraw(amount);

        // 4. 입금
        toAccount.deposit(amount);

        // 5. 거래 생성
        Transaction transaction = new Transaction(TransactionType.TRANSFER, amount, fromAccountId, secondAccountId);

        try {
            transactionRepository.save(transaction);

            // 6. 원장(Ledger) 기록 (2건)
            ledgerRepository.save(
                    new AccountLedger(fromAccountId, transaction.getTransactionId(), EntryType.DEBIT, amount, fromAccount.getBalance())
            );

            ledgerRepository.save(
                    new AccountLedger(toAccountId, transaction.getTransactionId(), EntryType.CREDIT, amount, toAccount.getBalance())
            );

            transaction.success();

            /************ 이벤트 발행 ************/ // 비즈니스 이벤트는 Service 책임이므로, Service에서 publish 한다.
            TransactionEventDTO eventDTO = new TransactionEventDTO(
                    transaction.getTransactionId(),
                    transaction.getAmount(),
                    "CREATED",
                    LocalDateTime.now()
            );

            transactionEventPublisher.publish(eventDTO);
        } catch (Exception e) {
            transaction.fail();
            throw e;
        }

    }

}
