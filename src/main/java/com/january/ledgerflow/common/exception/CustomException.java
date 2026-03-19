package com.january.ledgerflow.common.exception;

import lombok.Getter;

/* ==== CustomException이 RuntimeException을 상속받는 이유 ====
 * 결론:
 * - Checked Exception → 비즈니스 로직에 부적합
 * Unchecked(RuntimeException) → 표준
 */
@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public  CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
