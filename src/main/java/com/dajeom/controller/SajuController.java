package com.dajeom.controller;

import com.dajeom.dto.SajuAnalysisRequest;
import com.dajeom.dto.TraditionalSajuAnalysisResponse;
import com.dajeom.service.SajuAnalysisService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 새로운 정통 종합사주 계산 API Controller다.
 *
 * /api/saju/calculate
 * 요청을 받아 새로운 명리 계산 엔진을 실행한다.
 */
@RestController
@RequestMapping("/api/saju")
public class SajuController {

    private final SajuAnalysisService
            sajuAnalysisService;

    public SajuController(
            SajuAnalysisService sajuAnalysisService
    ) {
        this.sajuAnalysisService =
                sajuAnalysisService;
    }

    /**
     * 정통 종합사주 전체 분석
     *
     * 원국
     * 오행
     * 십성
     * 신강·신약
     * 구조
     * 용신
     * 대운
     * 세운
     * 운 상호작용
     * 13개 분야 해석 신호
     * 최종 종합 보고서
     */
    @PostMapping("/calculate")
    public ResponseEntity<TraditionalSajuAnalysisResponse> calculate(
            @Valid
            @RequestBody
            SajuAnalysisRequest request
    ) {

        TraditionalSajuAnalysisResponse response =
                sajuAnalysisService.analyze(
                        request
                );

        return ResponseEntity.ok(
                response
        );
    }
}