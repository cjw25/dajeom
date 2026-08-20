package com.dajeom.controller;

import com.dajeom.dto.SajuAnalysisRequest;
import com.dajeom.dto.SajuAnalysisResponse;
import com.dajeom.service.SajuAnalysisService;
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
 * 프로필 Entity 저장이나
 * 별도 Analysis Entity 생성 없이
 * 현재 사용자가 입력한 사주정보를
 * deterministic 분석 Service에 직접 전달한다.
 *
 * =================================================
 * Runtime Diagnostic
 * =================================================
 *
 * 현재 분석 요청이:
 *
 * 1. Controller까지 실제로 진입했는지
 * 2. SajuAnalysisService.analyze() 호출에 들어갔는지
 * 3. Service에서 정상 반환됐는지
 *
 * 를 확인하기 위한 최소 진단 로그를 남긴다.
 *
 * 실제 생년월일, 이름, 질문 내용 등
 * 사용자 입력 원문은 로그에 출력하지 않는다.
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
            @RequestBody SajuAnalysisRequest request
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
                    (
                            System.nanoTime()
                                    -
                                    controllerStartedAt
                    )
                            /
                            1_000_000L;

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
                    (
                            System.nanoTime()
                                    -
                                    controllerStartedAt
                    )
                            /
                            1_000_000L;

            log.error(
                    "[SAJU-DIAG] service-call-failed elapsedMs={} exception={} message={}",
                    elapsedMillis,
                    exception
                            .getClass()
                            .getName(),
                    exception
                            .getMessage()
            );

            throw exception;
        }
    }
}