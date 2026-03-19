package com.january.ledgerflow.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS("U002", "이미 등록된 사용자입니다."),
    ACCOUNT_NOT_FOUND("A001", "계좌를 찾을 수 없습니다."),
    ACCOUNT_ALREADY_EXISTS("A002", "이미 등록된 계좌입니다."),
    INSUFFICIENT_BALANCE("A003", "잔액이 부족합니다."),
    INVALID_TRANSFER("A004", "잘못된 이체 요청입니다."),
    INTERNAL_ERROR("S001", "서버 오류가 발생했습니다.");

    private final String code;
    private final String message;
}
