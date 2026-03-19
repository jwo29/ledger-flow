package com.january.ledgerflow.account.repository;

import com.january.ledgerflow.account.domain.Account;
import jakarta.persistence.LockModeType;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    /*
    비관적 락(Pessimistic lock)을 사용한 동시성 제어. 동시에 접근 → DB가 락 → 순차 처리
    장점
    - 구현 단순
    - 금융 시스템에 안전
    - 트랜잭션 정합성 보장
    단점
    - 성능 낮음(추후 version 컬럼을 사용하여 낙관적 락(optimistic lock) 방식으로 전환 가능)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.accountId = :accountId")
    Optional<Account> findByIdForUpdate(@Param("accountId") Long accountId);
}
