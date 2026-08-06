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
     *
     * 이름, 성별, 생년월일, 출생시간,
     * 양력·음력, 윤달 여부 등의 기본정보를 저장한다.
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
     * 프로필을 기준으로 다음 내용을 계산한다.
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
     * 성격·연애·재물·직업·건강·시험·창업 풀이
     *
     * 미래 연도·월별 흐름과 영상 장면은
     * 별도의 조회 API에서 생성한다.
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
     * 영상형 사주 결과를 조회한다.
     *
     * 실제 동영상 파일이 아니라 프론트엔드에서
     * 영상처럼 재생할 수 있는 장면 데이터를 반환한다.
     *
     * 반환 내용:
     *
     * 장면 순서
     * 장면 유형
     * 화면 제목
     * 내레이션
     * 자막
     * 배경 키
     * 캐릭터 표정 키
     * 전환 효과
     * 장면 재생시간
     * 강조 내용
     * 명리학적 근거
     * 연도·월 영향 기간
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
     * 현재 연도부터 앞으로의 연도별 미래 흐름을 조회한다.
     *
     * 기본적으로 현재 연도를 포함해
     * 앞으로 10년의 흐름을 반환한다.
     *
     * 예:
     *
     * GET /api/saju/analyses/10/forecast/years
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
     * 특정 연도의 종합 흐름을 조회한다.
     *
     * 연간 핵심 주제와 함께
     * 1월부터 12월까지의 월별 흐름을 반환한다.
     *
     * 예:
     *
     * GET /api/saju/analyses/10/forecast/years/2027
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
     * 특정 연도와 월의 상세 흐름을 조회한다.
     *
     * 일반 달력상의 월뿐만 아니라
     * 절기 기준 실제 월운 시작 시각과 종료 시각을 함께 반환한다.
     *
     * 반환 내용:
     *
     * 월운 간지
     * 절기 기준 적용 기간
     * 전조월·집중월·잔여월 구분
     * 월운 핵심 주제
     * 나타날 수 있는 작용
     * 좋은 활용 방법
     * 조심해야 할 행동
     * 분야별 작용 점수
     * 해석 근거
     * 근거 중첩 신뢰도
     *
     * 예:
     *
     * GET /api/saju/analyses/10/forecast/years/2027/months/5
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