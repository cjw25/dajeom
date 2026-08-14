package com.dajeom.exception;

import com.dajeom.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * API 공통 예외 처리.
 *
 * =================================================
 *
 * 원칙
 *
 * 1.
 * 사용자에게는 필요한 오류 정보만 반환한다.
 *
 * 2.
 * 예상하지 못한 서버 오류의 stack trace를
 * API 응답에 노출하지 않는다.
 *
 * 3.
 * 서버 내부 오류는 서버 로그에 전체 stack trace를 남긴다.
 *
 * 4.
 * favicon / Chrome DevTools well-known 요청 같은
 * 존재하지 않는 정적 리소스는 404로 처리한다.
 *
 * 이런 요청을 500 서버 오류로 취급하지 않는다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(
                    GlobalExceptionHandler.class
            );

    /**
     * 존재하지 않는 정적 리소스.
     *
     * 예:
     *
     * /favicon.ico
     * /.well-known/appspecific/com.chrome.devtools.json
     *
     * 애플리케이션 분석 실패가 아니므로
     * ERROR stack trace를 기록하지 않는다.
     */
    @ExceptionHandler(
            NoResourceFoundException.class
    )
    public ResponseEntity<ApiErrorResponse>
    handleNoResourceFound(
            NoResourceFoundException exception,
            HttpServletRequest request
    ) {

        log.debug(
                "정적 리소스를 찾을 수 없습니다. method={}, uri={}",
                request.getMethod(),
                request.getRequestURI()
        );

        return createResponse(
                HttpStatus.NOT_FOUND,
                "요청한 리소스를 찾을 수 없습니다.",
                request,
                List.of()
        );
    }

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

        log.warn(
                "API 요청값 검증 실패. method={}, uri={}, details={}",
                request.getMethod(),
                request.getRequestURI(),
                details
        );

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

        log.warn(
                "API 요청 JSON 해석 실패. method={}, uri={}, message={}",
                request.getMethod(),
                request.getRequestURI(),
                exception.getMessage()
        );

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

        log.warn(
                "출생시간 필요 오류. method={}, uri={}, message={}",
                request.getMethod(),
                request.getRequestURI(),
                exception.getMessage()
        );

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

        log.warn(
                "음력 날짜 검증 실패. method={}, uri={}, message={}",
                request.getMethod(),
                request.getRequestURI(),
                exception.getMessage()
        );

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

        log.warn(
                "Domain validation 실패. method={}, uri={}, message={}",
                request.getMethod(),
                request.getRequestURI(),
                exception.getMessage()
        );

        return createResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request,
                List.of()
        );
    }

    /**
     * 예상하지 못한 서버 오류.
     *
     * exception 객체를 마지막 인자로 전달하여
     * 실제 stack trace를 서버 로그에 남긴다.
     */
    @ExceptionHandler(
            Exception.class
    )
    public ResponseEntity<ApiErrorResponse>
    handleUnexpected(
            Exception exception,
            HttpServletRequest request
    ) {

        log.error(
                "사주 분석 처리 중 예상하지 못한 서버 오류. method={}, uri={}",
                request.getMethod(),
                request.getRequestURI(),
                exception
        );

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