package com.dajeom.controller;

import com.dajeom.dto.SajuAnalysisRequest;
import com.dajeom.dto.SajuAnalysisResponse;
import com.dajeom.dto.SajuCinematicResponse;
import com.dajeom.dto.SajuMonthForecastResponse;
import com.dajeom.dto.SajuProfileRequest;
import com.dajeom.dto.SajuProfileResponse;
import com.dajeom.dto.SajuYearForecastResponse;
import com.dajeom.service.SajuAnalysisService;
import com.dajeom.service.SajuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/saju")
public class SajuController {

    private final SajuService sajuService;
    private final SajuAnalysisService sajuAnalysisService;

    /**
     * 사주 프로필을 등록한다.
     */
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

    /**
     * 저장된 사주 프로필을 조회한다.
     */
    @GetMapping("/profiles/{profileId}")
    public SajuProfileResponse getProfile(
            @PathVariable
            Long profileId
    ) {
        return sajuService.getProfile(
                profileId
        );
    }

    /**
     * 사주 종합 분석을 생성한다.
     *
     * 생성되는 기본 계산:
     *
     * 연주·월주·일주·시주
     * 오행과 음양
     * 십성과 지장간
     * 십이운성
     * 납음과 공망
     * 합·충·형·파·해
     * 신살
     * 신강·신약 보조 판정
     * 용신·희신·기신 보조 판정
     * 대운과 세운
     *
     * 생성되는 심층 해설:
     *
     * 타고난 종합운
     * 올해 종합운
     * 앞으로의 종합운
     * 만나기 쉬운 사람
     * 연애 스타일
     * 잘 맞는 사람과 궁합
     * 과거운
     * 부모와의 인연
     * 형제자매 운
     * 금전운
     * 재물운
     * 관재수
     * 현재운
     * 미래운
     */
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

    /**
     * 저장된 사주 종합 분석 결과를 조회한다.
     */
    @GetMapping("/analyses/{analysisId}")
    public SajuAnalysisResponse getAnalysis(
            @PathVariable
            Long analysisId
    ) {
        return sajuAnalysisService.getAnalysis(
                analysisId
        );
    }

    /**
     * 영상형 사주 장면 데이터를 조회한다.
     *
     * 실제 MP4 파일이 아니라 프론트엔드에서
     * 영상처럼 재생할 수 있는 장면 목록을 반환한다.
     *
     * 긴 심층 해설 전체를 읽지 않고,
     * 각 카테고리의 핵심 내용을 영상 분량에 맞춰 요약한다.
     */
    @GetMapping("/analyses/{analysisId}/cinematic")
    public SajuCinematicResponse getCinematic(
            @PathVariable
            Long analysisId
    ) {
        return sajuAnalysisService.getCinematic(
                analysisId
        );
    }

    /**
     * 기술적인 연도별 운세 데이터를 조회한다.
     *
     * 기본 화면과 영상형 사주에서는 직접 사용하지 않는다.
     * 별도의 상세 운세 기능에서 사용할 수 있도록 유지한다.
     */
    @GetMapping("/analyses/{analysisId}/forecast/years")
    public List<SajuYearForecastResponse> getYearForecasts(
            @PathVariable
            Long analysisId
    ) {
        return sajuAnalysisService.getYearForecasts(
                analysisId
        );
    }

    /**
     * 특정 연도의 기술적인 운세 데이터를 조회한다.
     */
    @GetMapping("/analyses/{analysisId}/forecast/years/{year}")
    public SajuYearForecastResponse getYearForecast(
            @PathVariable
            Long analysisId,

            @PathVariable
            int year
    ) {
        return sajuAnalysisService.getYearForecast(
                analysisId,
                year
        );
    }

    /**
     * 특정 연도와 월의 기술적인 월운 데이터를 조회한다.
     *
     * 기본 종합해설과 영상형 사주에서는 사용하지 않는다.
     */
    @GetMapping(
            "/analyses/{analysisId}/forecast/years/{year}/months/{month}"
    )
    public SajuMonthForecastResponse getMonthForecast(
            @PathVariable
            Long analysisId,

            @PathVariable
            int year,

            @PathVariable
            int month
    ) {
        return sajuAnalysisService.getMonthForecast(
                analysisId,
                year,
                month
        );
    }
}