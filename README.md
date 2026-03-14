# LedgerFlow

LedgerFlow는 금융 거래 시스템의 핵심 개념인 **원장 기반(Double Entry)
거래 처리 구조**를 구현한 백엔드 프로젝트입니다.

이 프로젝트는 **이체(Transfer), 거래 원장(Ledger), 정산(Settlement)**
기능을 중심으로 금융 시스템의 핵심 구조를 설계하고 구현하는 것을 목표로
합니다.

UI 없이 **API 기반 금융 거래 시스템**에 집중하여 다음과 같은 문제를
해결합니다.

-   금융 거래 데이터 정합성
-   동시성 문제
-   멱등성(Idempotency)
-   이벤트 기반 정산 처리

------------------------------------------------------------------------

# 핵심 기능

## 1. Account System

계좌 생성 및 잔액 관리

-   계좌 생성
-   계좌 조회
-   잔액 조회

잔액은 컬럼 값이 아닌 **Ledger 기반 계산**으로 관리됩니다.

------------------------------------------------------------------------

## 2. Transfer System

계좌 간 이체 처리

-   계좌 간 송금
-   잔액 검증
-   거래 기록 생성

모든 거래는 **Atomic Transaction**으로 처리됩니다.

------------------------------------------------------------------------

## 3. Ledger System (Double Entry)

금융 시스템의 핵심 원장 구조를 구현합니다.

하나의 거래는 반드시 **2개의 Ledger Entry**를 생성합니다.

예시

    Account A -10000 (DEBIT)
    Account B +10000 (CREDIT)

이를 통해 다음을 보장합니다.

-   데이터 정합성
-   거래 추적 가능성
-   감사 로그(Audit)

------------------------------------------------------------------------

## 4. Idempotent Transfer API

금융 API에서 필수적인 **멱등성 처리**를 지원합니다.

네트워크 재시도 상황에서도 중복 이체가 발생하지 않도록 설계했습니다.

예시

    POST /transfers
    Idempotency-Key: abc123

동일 요청은 한 번만 처리됩니다.

------------------------------------------------------------------------

## 5. Event Driven Settlement

거래 이후 정산 처리는 이벤트 기반으로 수행됩니다.

흐름

    Transaction 생성
    ↓
    Ledger Entry 기록
    ↓
    Event 발행
    ↓
    Settlement Worker 처리

이 구조를 통해 다음을 달성합니다.

-   시스템 확장성
-   서비스 간 결합도 감소
-   비동기 처리

------------------------------------------------------------------------

# 시스템 아키텍처

    Client
    ↓
    Transfer API
    ↓
    Transaction Service
    ↓
    Ledger Service
    ↓
    Database

    ↓
    
    Outbox Event
    ↓
    Message Queue
    ↓
    Settlement Worker

------------------------------------------------------------------------

# 기술 스택

## Backend

-   Java 17
-   Spring Boot
-   Spring Transaction
-   Spring Validation

## Database

-   PostgreSQL

## Messaging

-   RabbitMQ

## Build Tool

-   Gradle

------------------------------------------------------------------------

# 데이터 모델

주요 테이블

- accounts
- transactions
- ledger_entries
- transfer_requests
- settlements
- outbox_events
- idempotency_keys

------------------------------------------------------------------------

# 핵심 거래 흐름

이체 요청

    POST /transfers

처리 과정

1.  요청 검증
2.  Transaction 생성
3.  잔액 확인
4.  Ledger Entry 생성
5.  이벤트 발행
6.  정산 처리

------------------------------------------------------------------------

# 프로젝트 목표

이 프로젝트는 다음 금융 시스템 개념을 학습하고 구현하는 것을 목표로
합니다.

-   Double Entry Ledger
-   Transaction Atomicity
-   Idempotent API
-   Event Driven Architecture
-   Settlement Processing

------------------------------------------------------------------------

# 프로젝트 구조

    ledgerflow 
    ├─ account
    ├─ transaction 
    ├─ ledger 
    ├─ transfer 
    ├─ settlement
    ├─ event 
    └─ common

------------------------------------------------------------------------

# 실행 방법

    ./gradlew bootRun

------------------------------------------------------------------------

# 향후 개선 계획

-   Optimistic Lock 기반 동시성 제어
-   Outbox Pattern 적용
-   대량 거래 처리 성능 테스트
-   배치 기반 정산 시스템 확장
