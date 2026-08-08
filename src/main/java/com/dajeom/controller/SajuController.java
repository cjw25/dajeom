package com.dajeom.controller;

import com.dajeom.dto.SajuAnalysisRequest;
import com.dajeom.dto.SajuAnalysisResponse;
import com.dajeom.service.SajuAnalysisService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사주 분석 API Controller.
 *
 * Controller 역할:
 *
 * - Request 수신
 * - Validation
 * - Service 호출
 * - Response 반환
 *
 * 명리 계산 로직은
 * Controller에 작성하지 않는다.
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
     * 사주 전체 분석.
     *
     * POST
     * /api/saju/calculate
     */
    @PostMapping("/calculate")
    public ResponseEntity<SajuAnalysisResponse>
    calculate(
            @Valid
            @RequestBody
            SajuAnalysisRequest request
    ) {

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