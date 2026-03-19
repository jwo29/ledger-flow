package com.january.ledgerflow.common.exception;

import com.january.ledgerflow.common.response.ApiResponse;
import com.january.ledgerflow.common.response.ErrorResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/* ==== @RestControllerAdvice의 의미 : 모든 컨트롤러의 예외를 중앙에서 처리하는 인터셉터
 * 정의: @ControllerAdvice + @ResonseBody
 * 역할:
 * - 전역 예외 처리
 * - 모든 @RestController에 적용
 * - 반환값 → **자동 JSON 변환**
 * 동작 흐름:
 * Controller → Exception 발생 → @RestControllerAdvice가 가로챔 → JSON 응답 반환
 * 없으면?
 * - 각 Controller마다 try-catch 필요 → **코드 중복 폭발**
 */
@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ApiResponse<?> handleCustomException(CustomException e) {

        log.error("Unexpected error", e);

        return ApiResponse.fail(
                ErrorResponse.of(e.getErrorCode())
        );
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleException(Exception e) {

        log.error("Unexpected error", e);

        return ApiResponse.fail(
                ErrorResponse.of(ErrorCode.INTERNAL_ERROR)
        );
    }
}
