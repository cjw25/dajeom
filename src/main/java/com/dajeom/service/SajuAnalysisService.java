package com.dajeom.service;

import com.dajeom.dto.SajuAnalysisRequest;
import com.dajeom.dto.SajuAnalysisResponse;
import com.dajeom.dto.SajuCinematicResponse;
import com.dajeom.dto.SajuMonthForecastResponse;
import com.dajeom.dto.SajuYearForecastResponse;
import com.dajeom.entity.BirthProfile;
import com.dajeom.entity.SajuAnalysis;
import com.dajeom.entity.SajuAnalysisStatus;
import com.dajeom.repository.BirthProfileRepository;
import com.dajeom.repository.SajuAnalysisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SajuAnalysisService {

    /*
     * SajuCalculatorService의 절기 계산 지원 범위와 동일하게 맞춘다.
     */
    private static final int MIN_FORECAST_YEAR = 1900;
    private static final int MAX_FORECAST_YEAR = 2100;

    private final BirthProfileRepository birthProfileRepository;
    private final SajuAnalysisRepository sajuAnalysisRepository;

    /*
     * 만세력과 명리학 계산을 담당한다.
     *
     * 연주·월주·일주·시주
     * 오행·음양
     * 십성·십이운성
     * 합·충·형·파·해
     * 신살
     * 대운·세운·월운 계산
     */
    private final SajuCalculatorService sajuCalculatorService;

    /*
     * 계산된 사주 결과를
     * 일반인이 이해하기 쉬운 문장으로 바꾼다.
     */
    private final SajuEasyExplanationService sajuEasyExplanationService;

    /*
     * 원국·대운·세운·월운을 비교해
     * 연도별 및 월별 미래 흐름을 생성한다.
     */
    private final SajuForecastService sajuForecastService;

    /*
     * 종합 사주 결과와 미래 운세를
     * 영상형 장면 데이터로 변환한다.
     */
    private final SajuCinematicService sajuCinematicService;

    /**
     * 새로운 사주 종합 분석을 생성한다.
     *
     * 처리 순서
     *
     * 1. 프로필 조회
     * 2. 분석 엔티티 생성
     * 3. 만세력 계산 검증
     * 4. 쉬운 설명 생성 검증
     * 5. 분석 완료 처리
     * 6. 완료 상태가 반영된 최종 응답 반환
     */
    @Transactional
    public SajuAnalysisResponse createAnalysis(
            SajuAnalysisRequest request
    ) {
        validateAnalysisRequest(
                request
        );

        BirthProfile birthProfile =
                birthProfileRepository.findById(
                        request.profileId()
                ).orElseThrow(
                        () -> new IllegalArgumentException(
                                "사주 기본정보를 찾을 수 없습니다."
                        )
                );

        SajuAnalysis analysis =
                SajuAnalysis.createCalculating(
                        birthProfile
                );

        SajuAnalysis savedAnalysis =
                sajuAnalysisRepository.saveAndFlush(
                        analysis
                );

        try {
            /*
             * 분석 엔티티와 프로필 확인 완료
             */
            savedAnalysis.updateProgress(
                    20
            );

            /*
             * 만세력 계산이 정상적으로 가능한지 먼저 확인한다.
             */
            SajuAnalysisResponse calculatedResponse =
                    sajuCalculatorService.calculate(
                            savedAnalysis
                    );

            savedAnalysis.updateProgress(
                    70
            );

            /*
             * 쉬운 풀이 생성 과정까지 정상적으로 처리되는지 확인한다.
             */
            sajuEasyExplanationService.enrich(
                    calculatedResponse
            );

            savedAnalysis.updateProgress(
                    90
            );

            /*
             * 모든 계산과 설명 생성이 끝난 뒤 완료 상태로 변경한다.
             */
            savedAnalysis.complete();

            sajuAnalysisRepository.flush();

            /*
             * COMPLETED 상태와 completedAt이 포함된
             * 최종 계산 결과를 다시 생성한다.
             */
            return calculateEnrichedAnalysis(
                    savedAnalysis
            );

        } catch (IllegalArgumentException exception) {
            markAnalysisFailed(
                    savedAnalysis
            );

            throw exception;

        } catch (Exception exception) {
            markAnalysisFailed(
                    savedAnalysis
            );

            throw new IllegalStateException(
                    "사주 만세력 계산 중 오류가 발생했습니다.",
                    exception
            );
        }
    }

    /**
     * 저장된 종합 사주 분석 결과를 조회한다.
     */
    @Transactional(readOnly = true)
    public SajuAnalysisResponse getAnalysis(
            Long analysisId
    ) {
        SajuAnalysis analysis =
                getCompletedAnalysis(
                        analysisId
                );

        return calculateEnrichedAnalysis(
                analysis
        );
    }

    /**
     * 영상형 사주 결과를 조회한다.
     *
     * 다음 내용이 장면 단위로 반환된다.
     *
     * 오프닝
     * 만세력
     * 기본 기운
     * 겉 성격
     * 속마음
     * 장점
     * 단점과 보완점
     * 재물운
     * 직업과 사업운
     * 사람복
     * 연애운
     * 현재 대운
     * 미래 연도 흐름
     * 월별 영향 기간
     * 조심해야 할 기간
     * 기회 활용 기간
     * 현실적인 조언
     * 해석 근거
     */
    @Transactional(readOnly = true)
    public SajuCinematicResponse getCinematic(
            Long analysisId
    ) {
        SajuAnalysis analysis =
                getCompletedAnalysis(
                        analysisId
                );

        SajuAnalysisResponse analysisResponse =
                calculateEnrichedAnalysis(
                        analysis
                );

        List<SajuYearForecastResponse> yearForecasts =
                sajuForecastService.getYearForecasts(
                        analysis,
                        analysisResponse
                );

        return sajuCinematicService.createCinematic(
                analysis,
                analysisResponse,
                yearForecasts
        );
    }

    /**
     * 현재 연도부터 앞으로의 연도별 흐름을 조회한다.
     *
     * 기본 조회 기간은 SajuForecastService에서 정한다.
     */
    @Transactional(readOnly = true)
    public List<SajuYearForecastResponse> getYearForecasts(
            Long analysisId
    ) {
        SajuAnalysis analysis =
                getCompletedAnalysis(
                        analysisId
                );

        SajuAnalysisResponse analysisResponse =
                calculateEnrichedAnalysis(
                        analysis
                );

        return sajuForecastService.getYearForecasts(
                analysis,
                analysisResponse
        );
    }

    /**
     * 특정 연도의 종합 흐름과
     * 1월부터 12월까지의 월별 운세를 조회한다.
     */
    @Transactional(readOnly = true)
    public SajuYearForecastResponse getYearForecast(
            Long analysisId,
            int year
    ) {
        validateYear(
                year
        );

        SajuAnalysis analysis =
                getCompletedAnalysis(
                        analysisId
                );

        SajuAnalysisResponse analysisResponse =
                calculateEnrichedAnalysis(
                        analysis
                );

        return sajuForecastService.getYearForecast(
                analysis,
                analysisResponse,
                year
        );
    }

    /**
     * 특정 연도와 월의 상세 흐름을 조회한다.
     *
     * 일반 달력의 매월 1일부터 말일까지가 아니라
     * 절기 기준 적용 시작 시각과 종료 시각을 함께 반환한다.
     */
    @Transactional(readOnly = true)
    public SajuMonthForecastResponse getMonthForecast(
            Long analysisId,
            int year,
            int month
    ) {
        validateYear(
                year
        );

        validateMonth(
                month
        );

        SajuAnalysis analysis =
                getCompletedAnalysis(
                        analysisId
                );

        SajuAnalysisResponse analysisResponse =
                calculateEnrichedAnalysis(
                        analysis
                );

        return sajuForecastService.getMonthForecast(
                analysis,
                analysisResponse,
                year,
                month
        );
    }

    /**
     * 사주 분석 요청값을 검증한다.
     */
    private void validateAnalysisRequest(
            SajuAnalysisRequest request
    ) {
        if (
                request == null
        ) {
            throw new IllegalArgumentException(
                    "사주 분석 요청값이 없습니다."
            );
        }

        if (
                request.profileId() == null
        ) {
            throw new IllegalArgumentException(
                    "사주 프로필 ID는 필수입니다."
            );
        }

        if (
                request.profileId() <= 0
        ) {
            throw new IllegalArgumentException(
                    "사주 프로필 ID가 올바르지 않습니다."
            );
        }
    }

    /**
     * 분석 결과가 존재하며
     * 완료된 상태인지 공통으로 확인한다.
     */
    private SajuAnalysis getCompletedAnalysis(
            Long analysisId
    ) {
        validateAnalysisId(
                analysisId
        );

        SajuAnalysis analysis =
                sajuAnalysisRepository.findById(
                        analysisId
                ).orElseThrow(
                        () -> new IllegalArgumentException(
                                "사주 분석 결과를 찾을 수 없습니다."
                        )
                );

        if (
                analysis.getStatus()
                        == SajuAnalysisStatus.CALCULATING
        ) {
            throw new IllegalStateException(
                    "사주 분석이 아직 진행 중입니다."
            );
        }

        if (
                analysis.getStatus()
                        == SajuAnalysisStatus.FAILED
        ) {
            throw new IllegalStateException(
                    "사주 분석 처리에 실패한 결과입니다."
            );
        }

        if (
                analysis.getStatus()
                        != SajuAnalysisStatus.COMPLETED
        ) {
            throw new IllegalStateException(
                    "사주 분석이 아직 완료되지 않았습니다."
            );
        }

        if (
                analysis.getBirthProfile() == null
        ) {
            throw new IllegalStateException(
                    "사주 분석에 연결된 기본정보가 없습니다."
            );
        }

        return analysis;
    }

    /**
     * 분석 ID를 검증한다.
     */
    private void validateAnalysisId(
            Long analysisId
    ) {
        if (
                analysisId == null
        ) {
            throw new IllegalArgumentException(
                    "사주 분석 ID는 필수입니다."
            );
        }

        if (
                analysisId <= 0
        ) {
            throw new IllegalArgumentException(
                    "사주 분석 ID가 올바르지 않습니다."
            );
        }
    }

    /**
     * 만세력 계산 결과에
     * 쉬운 설명을 결합한다.
     */
    private SajuAnalysisResponse calculateEnrichedAnalysis(
            SajuAnalysis analysis
    ) {
        if (
                analysis == null
        ) {
            throw new IllegalArgumentException(
                    "사주 분석 정보가 없습니다."
            );
        }

        SajuAnalysisResponse calculatedResponse =
                sajuCalculatorService.calculate(
                        analysis
                );

        if (
                calculatedResponse == null
        ) {
            throw new IllegalStateException(
                    "사주 계산 결과를 생성하지 못했습니다."
            );
        }

        SajuAnalysisResponse enrichedResponse =
                sajuEasyExplanationService.enrich(
                        calculatedResponse
                );

        if (
                enrichedResponse == null
        ) {
            throw new IllegalStateException(
                    "사주 쉬운 풀이 결과를 생성하지 못했습니다."
            );
        }

        return enrichedResponse;
    }

    /**
     * 분석 처리 실패 상태로 변경한다.
     */
    private void markAnalysisFailed(
            SajuAnalysis analysis
    ) {
        if (
                analysis == null
        ) {
            return;
        }

        try {
            analysis.fail();

            sajuAnalysisRepository.flush();

        } catch (Exception ignored) {
            /*
             * 원래 발생한 계산 오류가
             * 실패 상태 저장 오류로 가려지지 않도록 한다.
             */
        }
    }

    /**
     * 조회 가능한 미래 운세 연도 범위를 검증한다.
     */
    private void validateYear(
            int year
    ) {
        if (
                year < MIN_FORECAST_YEAR
                        || year > MAX_FORECAST_YEAR
        ) {
            throw new IllegalArgumentException(
                    "조회 연도는 "
                            + MIN_FORECAST_YEAR
                            + "년부터 "
                            + MAX_FORECAST_YEAR
                            + "년 사이여야 합니다."
            );
        }
    }

    /**
     * 월 입력값을 검증한다.
     */
    private void validateMonth(
            int month
    ) {
        if (
                month < 1
                        || month > 12
        ) {
            throw new IllegalArgumentException(
                    "조회 월은 1월부터 12월 사이여야 합니다."
            );
        }
    }
}