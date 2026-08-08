package com.dajeom.exception;

import com.dajeom.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * API 공통 예외 처리.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @Valid 검증 실패.
     */
    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<ApiErrorResponse>
    handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {

        List<String> details =
                exception
                        .getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(
                                fieldError ->
                                        fieldError.getField()
                                                + ": "
                                                + fieldError
                                                .getDefaultMessage()
                        )
                        .toList();

        return createResponse(
                HttpStatus.BAD_REQUEST,
                "요청값을 확인해 주세요.",
                request,
                details
        );
    }

    /**
     * JSON 형식이나 enum,
     * LocalTime 등이 잘못된 경우.
     */
    @ExceptionHandler(
            HttpMessageNotReadableException.class
    )
    public ResponseEntity<ApiErrorResponse>
    handleUnreadableMessage(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {

        return createResponse(
                HttpStatus.BAD_REQUEST,
                "요청 데이터 형식이 올바르지 않습니다.",
                request,
                List.of(
                        "생년월일은 yyyy-MM-dd, "
                                + "시간은 HH:mm 형식을 사용하고 "
                                + "enum 값도 확인해 주세요."
                )
        );
    }

    /**
     * 절입 경계일인데
     * 출생시간을 몰라 정확한
     * 년주/월주를 확정할 수 없는 경우.
     */
    @ExceptionHandler(
            BirthTimeRequiredException.class
    )
    public ResponseEntity<ApiErrorResponse>
    handleBirthTimeRequired(
            BirthTimeRequiredException exception,
            HttpServletRequest request
    ) {

        return createResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request,
                List.of()
        );
    }

    /**
     * 잘못된 음력 날짜 또는 윤달.
     */
    @ExceptionHandler(
            InvalidLunarDateException.class
    )
    public ResponseEntity<ApiErrorResponse>
    handleInvalidLunarDate(
            InvalidLunarDateException exception,
            HttpServletRequest request
    ) {

        return createResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request,
                List.of(
                        "음력 날짜와 평달/윤달 여부를 확인해 주세요."
                )
        );
    }

    /**
     * Domain validation 실패.
     */
    @ExceptionHandler(
            IllegalArgumentException.class
    )
    public ResponseEntity<ApiErrorResponse>
    handleIllegalArgument(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {

        return createResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request,
                List.of()
        );
    }

    /**
     * 예상하지 못한 서버 오류.
     */
    @ExceptionHandler(
            Exception.class
    )
    public ResponseEntity<ApiErrorResponse>
    handleUnexpected(
            Exception exception,
            HttpServletRequest request
    ) {

        return createResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "사주 분석 중 서버 오류가 발생했습니다.",
                request,
                List.of()
        );
    }

    private ResponseEntity<ApiErrorResponse>
    createResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            List<String> details
    ) {

        ApiErrorResponse response =
                new ApiErrorResponse(
                        ZonedDateTime.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        message,
                        request.getRequestURI(),
                        details
                );

        return ResponseEntity
                .status(
                        status
                )
                .body(
                        response
                );
    }
}