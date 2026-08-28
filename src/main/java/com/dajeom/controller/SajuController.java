package com.dajeom.controller;

import com.dajeom.dto.SajuAnalysisRequest;
import com.dajeom.dto.SajuAnalysisResponse;
import com.dajeom.service.SajuAnalysisService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * 사주 전체 분석 API Controller.
 *
 * =================================================
 * 역할
 * =================================================
 *
 * Controller에서는 명리 계산을 수행하지 않는다.
 *
 * Request
 *      ↓
 * Bean Validation
 *      ↓
 * SajuAnalysisService
 *      ↓
 * SajuAnalysisResponse
 *
 * 구조만 담당한다.
 *
 * =================================================
 * 현재 공식 분석 진입점
 * =================================================
 *
 * POST /api/saju/calculate
 *
 * =================================================
 * 122 API 입력 경계
 * =================================================
 *
 * @Valid
 *
 * 를 Controller 진입점에 명시하여
 * SajuAnalysisRequest에 선언된:
 *
 * - @NotNull
 * - @NotBlank
 * - @Pattern
 * - @Size
 *
 * 검증을 실제 API 요청에서 활성화한다.
 *
 * =================================================
 * Runtime Diagnostic
 * =================================================
 *
 * 실제 생년월일,
 * 질문 내용,
 * 관계 Context 등
 * 사용자 입력 원문은 로그에 출력하지 않는다.
 *
 * 실패 로그에서도 exception message를
 * 그대로 기록하지 않는다.
 *
 * 상세 오류 분류와 안전 응답은
 * GlobalExceptionHandler가 담당한다.
 */
@RestController
@RequestMapping("/api/saju")
public class SajuController {

    private static final Logger log =
            LoggerFactory.getLogger(
                    SajuController.class
            );

    private final SajuAnalysisService
            sajuAnalysisService;

    public SajuController(
            SajuAnalysisService sajuAnalysisService
    ) {

        this.sajuAnalysisService =
                Objects.requireNonNull(
                        sajuAnalysisService,
                        "SajuAnalysisService가 필요합니다."
                );
    }

    @PostMapping("/calculate")
    public ResponseEntity<SajuAnalysisResponse> calculate(

            @Valid
            @RequestBody
            SajuAnalysisRequest request

    ) {

        long controllerStartedAt =
                System.nanoTime();

        log.info(
                "[SAJU-DIAG] controller-enter thread={}",
                Thread.currentThread().getName()
        );

        Objects.requireNonNull(
                request,
                "사주 분석 요청은 필수입니다."
        );

        log.info(
                "[SAJU-DIAG] service-call-start thread={}",
                Thread.currentThread().getName()
        );

        try {

            SajuAnalysisResponse response =
                    sajuAnalysisService
                            .analyze(
                                    request
                            );

            long elapsedMillis =
                    elapsedMillis(
                            controllerStartedAt
                    );

            log.info(
                    "[SAJU-DIAG] service-call-complete elapsedMs={} thread={}",
                    elapsedMillis,
                    Thread.currentThread().getName()
            );

            ResponseEntity<SajuAnalysisResponse> result =
                    ResponseEntity.ok(
                            response
                    );

            log.info(
                    "[SAJU-DIAG] controller-return elapsedMs={} thread={}",
                    elapsedMillis,
                    Thread.currentThread().getName()
            );

            return result;

        } catch (
                RuntimeException exception
        ) {

            long elapsedMillis =
                    elapsedMillis(
                            controllerStartedAt
                    );

            /*
             * 여기서는 사용자 입력 원문이 포함될 가능성이 있는
             * exception.getMessage()를 기록하지 않는다.
             *
             * 예상 가능한 400과 실제 500의 최종 구분은
             * GlobalExceptionHandler가 수행한다.
             */
            log.warn(
                    "[SAJU-DIAG] service-call-failed elapsedMs={} exception={} thread={}",
                    elapsedMillis,
                    exception
                            .getClass()
                            .getName(),
                    Thread.currentThread().getName()
            );

            throw exception;
        }
    }

    private long elapsedMillis(
            long startedAt
    ) {

        return (
                System.nanoTime()
                        -
                        startedAt
        )
                /
                1_000_000L;
    }
}