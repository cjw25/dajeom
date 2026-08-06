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

    private static final int MIN_FORECAST_YEAR = 1900;
    private static final int MAX_FORECAST_YEAR = 2100;

    private final BirthProfileRepository birthProfileRepository;
    private final SajuAnalysisRepository sajuAnalysisRepository;

    private final SajuCalculatorService sajuCalculatorService;
    private final SajuEasyExplanationService sajuEasyExplanationService;
    private final SajuForecastService sajuForecastService;
    private final SajuCinematicService sajuCinematicService;

    /**
     * 새로운 종합 사주 분석을 생성한다.
     *
     * 처리 순서:
     *
     * 1. 사주 프로필 조회
     * 2. 계산 중 상태의 분석 엔티티 생성
     * 3. 만세력과 명리 데이터 계산
     * 4. 현대 종합사주와 전통 심층사주 생성
     * 5. 분석 완료 처리
     * 6. 완료 상태가 적용된 최종 결과 반환
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
            savedAnalysis.updateProgress(
                    20
            );

            SajuAnalysisResponse calculatedResponse =
                    sajuCalculatorService.calculate(
                            savedAnalysis
                    );

            if (
                    calculatedResponse == null
            ) {
                throw new IllegalStateException(
                        "사주 계산 결과를 생성하지 못했습니다."
                );
            }

            savedAnalysis.updateProgress(
                    65
            );

            SajuAnalysisResponse enrichedResponse =
                    sajuEasyExplanationService.enrich(
                            calculatedResponse
                    );

            if (
                    enrichedResponse == null
            ) {
                throw new IllegalStateException(
                        "사주 심층 해설을 생성하지 못했습니다."
                );
            }

            savedAnalysis.updateProgress(
                    90
            );

            savedAnalysis.complete();

            sajuAnalysisRepository.flush();

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
                    "사주 만세력과 심층 해설 생성 중 오류가 발생했습니다.",
                    exception
            );
        }
    }

    /**
     * 저장된 종합 사주 결과를 조회한다.
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
     * 영상형 사주 장면 데이터를 조회한다.
     *
     * 영상에서는 별도의 연도별 Forecast API를 호출하지 않는다.
     * 종합 분석에 포함된 올해 운과 현재 대운,
     * 전통 심층풀이를 영상용 분량으로 변환한다.
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

        return sajuCinematicService.createCinematic(
                analysis,
                analysisResponse
        );
    }

    /**
     * 기술적인 연도별 미래 흐름을 조회한다.
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
     * 특정 연도의 기술적인 종합 흐름을 조회한다.
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
     * 특정 연도와 월의 기술적인 월운 데이터를 조회한다.
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
                    "사주 심층 해설을 생성하지 못했습니다."
            );
        }

        return enrichedResponse;
    }

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
             * 원래 계산 오류가 실패 상태 기록 오류로
             * 가려지지 않도록 별도 예외를 던지지 않는다.
             */
        }
    }

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