package com.dajeom.controller;

import com.dajeom.dto.SajuAnalysisRequest;
import com.dajeom.dto.SajuAnalysisResponse;
import com.dajeom.dto.SajuProfileRequest;
import com.dajeom.dto.SajuProfileResponse;
import com.dajeom.service.SajuAnalysisService;
import com.dajeom.service.SajuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/saju")
public class SajuController {

    private final SajuService sajuService;
    private final SajuAnalysisService sajuAnalysisService;

    @PostMapping("/profiles")
    @ResponseStatus(HttpStatus.CREATED)
    public SajuProfileResponse createProfile(
            @Valid
            @RequestBody
            SajuProfileRequest request
    ) {
        return sajuService.createProfile(
                request
        );
    }

    @GetMapping("/profiles/{profileId}")
    public SajuProfileResponse getProfile(
            @PathVariable
            Long profileId
    ) {
        return sajuService.getProfile(
                profileId
        );
    }

    @PostMapping("/analyses")
    @ResponseStatus(HttpStatus.CREATED)
    public SajuAnalysisResponse createAnalysis(
            @Valid
            @RequestBody
            SajuAnalysisRequest request
    ) {
        return sajuAnalysisService.createAnalysis(
                request
        );
    }

    @GetMapping("/analyses/{analysisId}")
    public SajuAnalysisResponse getAnalysis(
            @PathVariable
            Long analysisId
    ) {
        return sajuAnalysisService.getAnalysis(
                analysisId
        );
    }
}