package com.dajeom.exception;

import com.dajeom.dto.ApiErrorCode;
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
import java.util.Objects;

/**
 * API 공통 예외 처리.
 *
 * =================================================
 * 122 공식 오류 계약
 * =================================================
 *
 * 프론트는 오류 message를 파싱하지 않는다.
 *
 * 다음 errorCode를 공식 분기값으로 사용한다.
 *
 * RESOURCE_NOT_FOUND
 * REQUEST_VALIDATION_FAILED
 * REQUEST_BODY_INVALID
 * INVALID_REQUEST
 * BIRTH_TIME_REQUIRED
 * INVALID_LUNAR_DATE
 * DETERMINISTIC_CONTRACT_VIOLATION
 * INTERNAL_SERVER_ERROR
 *
 * =================================================
 * 보안 / 개인정보 경계
 * =================================================
 *
 * 400 계열 오류 로그에는:
 *
 * - 생년월일 원문
 * - 질문 원문
 * - 관계 Context 원문
 * - malformed JSON 원문
 * - exception.getMessage()
 *
 * 를 기록하지 않는다.
 *
 * 예상하지 못한 500 오류는
 * 서버 진단을 위해 stack trace를 남기되
 * API 응답에는 내부 예외 내용을 노출하지 않는다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(
                    GlobalExceptionHandler.class
            );

    /**
     * 존재하지 않는 정적/API 리소스.
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
                "정적/API 리소스를 찾을 수 없습니다. method={}, uri={}",
                request.getMethod(),
                request.getRequestURI()
        );

        return createResponse(
                HttpStatus.NOT_FOUND,
                ApiErrorCode.RESOURCE_NOT_FOUND,
                "요청한 리소스를 찾을 수 없습니다.",
                request,
                List.of()
        );
    }

    /**
     * @Valid Bean Validation 실패.
     *
     * rejectedValue는 절대 API details나
     * 서버 로그에 넣지 않는다.
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
                                fieldError -> {

                                    String message =
                                            fieldError
                                                    .getDefaultMessage();

                                    if (
                                            message == null
                                                    ||
                                                    message.isBlank()
                                    ) {

                                        return fieldError.getField()
                                                + ": 요청값이 올바르지 않습니다.";
                                    }

                                    return fieldError.getField()
                                            + ": "
                                            + message.trim();
                                }
                        )
                        .toList();

        log.warn(
                "API 요청값 검증 실패. method={}, uri={}, fieldErrorCount={}",
                request.getMethod(),
                request.getRequestURI(),
                details.size()
        );

        return createResponse(
                HttpStatus.BAD_REQUEST,
                ApiErrorCode.REQUEST_VALIDATION_FAILED,
                "요청값을 확인해 주세요.",
                request,
                details
        );
    }

    /**
     * JSON 형식,
     * enum,
     * LocalDate,
     * LocalTime,
     * Record 생성 단계 등의 오류.
     *
     * Jackson exception message에는
     * 사용자가 보낸 원문 일부가 포함될 수 있으므로
     * 로그에 그대로 출력하지 않는다.
     */
    @ExceptionHandler(
            HttpMessageNotReadableException.class
    )
    public ResponseEntity<ApiErrorResponse>
    handleUnreadableMessage(

            HttpMessageNotReadableException exception,

            HttpServletRequest request

    ) {

        boolean domainInputViolation =
                hasCause(
                        exception,
                        IllegalArgumentException.class
                );

        ApiErrorCode errorCode =
                domainInputViolation
                        ? ApiErrorCode.INVALID_REQUEST
                        : ApiErrorCode.REQUEST_BODY_INVALID;

        String message =
                domainInputViolation
                        ? "요청값 조합을 확인해 주세요."
                        : "요청 데이터 형식이 올바르지 않습니다.";

        List<String> details =
                domainInputViolation
                        ? List.of()
                        : List.of(
                        "날짜, 시간, enum 값을 확인해 주세요."
                );

        log.warn(
                "API 요청 Body 해석 실패. method={}, uri={}, errorCode={}, exception={}",
                request.getMethod(),
                request.getRequestURI(),
                errorCode,
                exception
                        .getClass()
                        .getSimpleName()
        );

        return createResponse(
                HttpStatus.BAD_REQUEST,
                errorCode,
                message,
                request,
                details
        );
    }

    /**
     * 절입 경계일 등에서
     * 정확한 년주/월주를 확정하기 위해
     * 출생시간이 필요한 경우.
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
                "출생시간 필요 오류. method={}, uri={}",
                request.getMethod(),
                request.getRequestURI()
        );

        return createResponse(
                HttpStatus.BAD_REQUEST,
                ApiErrorCode.BIRTH_TIME_REQUIRED,
                "정확한 사주 계산을 위해 출생시간이 필요합니다.",
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
                "음력 날짜 검증 실패. method={}, uri={}",
                request.getMethod(),
                request.getRequestURI()
        );

        return createResponse(
                HttpStatus.BAD_REQUEST,
                ApiErrorCode.INVALID_LUNAR_DATE,
                "음력 날짜 또는 윤달 정보가 올바르지 않습니다.",
                request,
                List.of(
                        "음력 날짜와 평달/윤달 여부를 확인해 주세요."
                )
        );
    }

    /**
     * Domain 입력값 또는
     * 입력값 조합 검증 실패.
     *
     * 예:
     *
     * - SOLAR + leapMonth=true
     * - birthTimeUnknown=true + birthTime 존재
     * - birthTimeUnknown=false + birthTime 없음
     * - 미래 생년월일
     * - luckReferenceDate 없이 luckReferenceTime만 존재
     *
     * 내부 exception message를 사용자에게
     * 그대로 전달하지 않는다.
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
                "Domain 요청값 검증 실패. method={}, uri={}, exception={}",
                request.getMethod(),
                request.getRequestURI(),
                exception
                        .getClass()
                        .getSimpleName()
        );

        return createResponse(
                HttpStatus.BAD_REQUEST,
                ApiErrorCode.INVALID_REQUEST,
                "요청값 조합을 확인해 주세요.",
                request,
                List.of()
        );
    }

    /**
     * =================================================
     * Deterministic Contract Violation
     * =================================================
     *
     * IllegalStateException은 일반 사용자 입력 오류로
     * 400 처리하지 않는다.
     *
     * 이미 검증된 Java 분석 파이프라인 내부에서
     * Identity / State / Provenance 계약이 깨졌다는 뜻이므로
     * 명시적인 500 오류로 분리한다.
     *
     * stack trace는 서버에만 남긴다.
     */
    @ExceptionHandler(
            IllegalStateException.class
    )
    public ResponseEntity<ApiErrorResponse>
    handleIllegalState(

            IllegalStateException exception,

            HttpServletRequest request

    ) {

        log.error(
                "Deterministic 내부 계약 위반. method={}, uri={}",
                request.getMethod(),
                request.getRequestURI(),
                exception
        );

        return createResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ApiErrorCode.DETERMINISTIC_CONTRACT_VIOLATION,
                "사주 분석 내부 검증 중 오류가 발생했습니다.",
                request,
                List.of()
        );
    }

    /**
     * 그 밖의 예상하지 못한 서버 오류.
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
                ApiErrorCode.INTERNAL_SERVER_ERROR,
                "사주 분석 중 서버 오류가 발생했습니다.",
                request,
                List.of()
        );
    }

    /**
     * Throwable Cause Chain 안에
     * 특정 Exception Type이 존재하는지 확인한다.
     *
     * Jackson이 Record Compact Constructor의
     * IllegalArgumentException을
     * HttpMessageNotReadableException으로 감싸는 경우를
     * INPUT FORMAT 오류와 구분하기 위한 용도다.
     */
    private boolean hasCause(

            Throwable throwable,

            Class<? extends Throwable> targetType

    ) {

        Objects.requireNonNull(
                throwable,
                "Cause 검사 대상 Throwable은 필수입니다."
        );

        Objects.requireNonNull(
                targetType,
                "Cause 검사 대상 Exception Type은 필수입니다."
        );

        Throwable current =
                throwable.getCause();

        int depth =
                0;

        while (
                current != null
                        &&
                        depth < 20
        ) {

            if (
                    targetType.isInstance(
                            current
                    )
            ) {

                return true;
            }

            current =
                    current.getCause();

            depth++;
        }

        return false;
    }

    private ResponseEntity<ApiErrorResponse>
    createResponse(

            HttpStatus status,

            ApiErrorCode errorCode,

            String message,

            HttpServletRequest request,

            List<String> details

    ) {

        Objects.requireNonNull(
                status,
                "API Error HTTP Status는 필수입니다."
        );

        Objects.requireNonNull(
                errorCode,
                "API Error Code는 필수입니다."
        );

        Objects.requireNonNull(
                request,
                "API Error HttpServletRequest는 필수입니다."
        );

        ApiErrorResponse response =
                new ApiErrorResponse(
                        ZonedDateTime.now(),

                        status.value(),

                        status.getReasonPhrase(),

                        errorCode,

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