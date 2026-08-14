package com.dajeom.controller;

import com.dajeom.dto.SajuAnalysisRequest;
import com.dajeom.dto.SajuAnalysisResponse;
import com.dajeom.service.SajuAnalysisService;
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
                Objects.requireNonNull(
                        sajuAnalysisService,
                        "SajuAnalysisService가 필요합니다."
                );
    }

    @PostMapping("/calculate")
    public ResponseEntity<SajuAnalysisResponse> calculate(
            @RequestBody SajuAnalysisRequest request
    ) {

        Objects.requireNonNull(
                request,
                "사주 분석 요청은 필수입니다."
        );

        SajuAnalysisResponse response =
                sajuAnalysisService
                        .analyze(
                                request
                        );

        return ResponseEntity.ok(
                response
        );
    }
}