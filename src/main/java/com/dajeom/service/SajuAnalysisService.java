package com.dajeom.service;

import com.dajeom.dto.SajuAnalysisRequest;
import com.dajeom.dto.SajuAnalysisResponse;
import com.dajeom.dto.SajuAnalysisResponse.InterpretationResponse;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SajuAnalysisService {

    private static final int MIN_FORECAST_YEAR = 1900;
    private static final int MAX_FORECAST_YEAR = 2100;

    private static final String MODERN_CATEGORY = "현대사주";
    private static final String TRADITIONAL_CATEGORY = "전통사주";

    private final BirthProfileRepository birthProfileRepository;
    private final SajuAnalysisRepository sajuAnalysisRepository;

    private final SajuCalculatorService sajuCalculatorService;
    private final SajuEasyExplanationService sajuEasyExplanationService;
    private final SajuForecastService sajuForecastService;
    private final SajuCinematicService sajuCinematicService;

    private final PlatformTransactionManager transactionManager;

    /**
     * 새로운 사주 분석을 생성한다.
     *
     * 분석 데이터 생성과 실제 계산을 분리한다.
     * 계산 과정에서 오류가 발생하면 FAILED 상태를
     * 별도 트랜잭션으로 저장한다.
     */
    public SajuAnalysisResponse createAnalysis(
            SajuAnalysisRequest request
    ) {
        validateAnalysisRequest(
                request
        );

        Long analysisId =
                createCalculatingAnalysis(
                        request.profileId()
                );

        try {
            return completeAnalysis(
                    analysisId
            );

        } catch (IllegalArgumentException exception) {
            markAnalysisFailed(
                    analysisId
            );

            throw exception;

        } catch (Exception exception) {
            markAnalysisFailed(
                    analysisId
            );

            throw new IllegalStateException(
                    "사주 만세력 계산 중 오류가 발생했습니다.",
                    exception
            );
        }
    }

    /**
     * CALCULATING 상태의 분석 정보를 먼저 저장한다.
     */
    private Long createCalculatingAnalysis(
            Long profileId
    ) {
        TransactionTemplate transactionTemplate =
                createTransactionTemplate(
                        TransactionDefinition.PROPAGATION_REQUIRES_NEW,
                        false
                );

        Long analysisId =
                transactionTemplate.execute(
                        status -> {
                            BirthProfile birthProfile =
                                    birthProfileRepository.findById(
                                            profileId
                                    ).orElseThrow(
                                            () ->
                                                    new IllegalArgumentException(
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

                            return savedAnalysis.getId();
                        }
                );

        if (
                analysisId == null
        ) {
            throw new IllegalStateException(
                    "사주 분석 정보를 생성하지 못했습니다."
            );
        }

        return analysisId;
    }

    /**
     * 만세력 계산과 심층 해설 생성을 처리한다.
     */
    private SajuAnalysisResponse completeAnalysis(
            Long analysisId
    ) {
        TransactionTemplate transactionTemplate =
                createTransactionTemplate(
                        TransactionDefinition.PROPAGATION_REQUIRES_NEW,
                        false
                );

        SajuAnalysisResponse response =
                transactionTemplate.execute(
                        status -> {
                            SajuAnalysis analysis =
                                    sajuAnalysisRepository.findById(
                                            analysisId
                                    ).orElseThrow(
                                            () ->
                                                    new IllegalArgumentException(
                                                            "사주 분석 정보를 찾을 수 없습니다."
                                                    )
                                    );

                            analysis.updateProgress(
                                    20
                            );

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

                            analysis.updateProgress(
                                    70
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

                            analysis.updateProgress(
                                    90
                            );

                            analysis.complete();

                            sajuAnalysisRepository.saveAndFlush(
                                    analysis
                            );

                            return enrichedResponse;
                        }
                );

        if (
                response == null
        ) {
            throw new IllegalStateException(
                    "사주 분석 결과를 생성하지 못했습니다."
            );
        }

        return response;
    }

    /**
     * 저장된 전체 사주 분석 결과를 조회한다.
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
     * 현대 종합사주 해설만 조회한다.
     */
    @Transactional(readOnly = true)
    public List<InterpretationResponse> getModernReport(
            Long analysisId
    ) {
        SajuAnalysisResponse response =
                calculateEnrichedAnalysis(
                        getCompletedAnalysis(
                                analysisId
                        )
                );

        return filterInterpretationsByCategory(
                response,
                MODERN_CATEGORY
        );
    }

    /**
     * 전통사주 심층 해설만 조회한다.
     */
    @Transactional(readOnly = true)
    public List<InterpretationResponse> getTraditionalReport(
            Long analysisId
    ) {
        SajuAnalysisResponse response =
                calculateEnrichedAnalysis(
                        getCompletedAnalysis(
                                analysisId
                        )
                );

        return filterInterpretationsByCategory(
                response,
                TRADITIONAL_CATEGORY
        );
    }

    /**
     * 영상형 사주 장면 데이터를 조회한다.
     *
     * 특정 월과 여러 연도의 예측 목록은
     * 영상에서 사용하지 않으므로 빈 목록을 전달한다.
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
                analysisResponse,
                List.of()
        );
    }

    /**
     * 현재 연도부터 앞으로의 연도별 흐름을 조회한다.
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
     * 특정 연도의 종합 흐름을 조회한다.
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
     * 특정 연도와 월의 내부 계산 결과를 조회한다.
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
     * 카테고리에 해당하는 해설만 반환한다.
     */
    private List<InterpretationResponse> filterInterpretationsByCategory(
            SajuAnalysisResponse response,
            String category
    ) {
        if (
                response == null
                        || response.interpretations() == null
                        || response.interpretations().isEmpty()
        ) {
            return List.of();
        }

        return response.interpretations()
                .stream()
                .filter(
                        Objects::nonNull
                )
                .filter(
                        interpretation ->
                                Objects.equals(
                                        category,
                                        interpretation.category()
                                )
                )
                .toList();
    }

    /**
     * 만세력 계산 결과와 심층 해설을 결합한다.
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
                    "사주 심층 해설을 생성하지 못했습니다."
            );
        }

        return enrichedResponse;
    }

    /**
     * 완료된 분석 정보를 조회한다.
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
                        () ->
                                new IllegalArgumentException(
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
     * 계산 실패 상태를 별도 트랜잭션으로 저장한다.
     */
    private void markAnalysisFailed(
            Long analysisId
    ) {
        if (
                analysisId == null
        ) {
            return;
        }

        try {
            TransactionTemplate transactionTemplate =
                    createTransactionTemplate(
                            TransactionDefinition.PROPAGATION_REQUIRES_NEW,
                            false
                    );

            transactionTemplate.executeWithoutResult(
                    status ->
                            sajuAnalysisRepository.findById(
                                    analysisId
                            ).ifPresent(
                                    analysis -> {
                                        analysis.fail();

                                        sajuAnalysisRepository.saveAndFlush(
                                                analysis
                                        );
                                    }
                            )
            );

        } catch (Exception ignored) {
            /*
             * 원래 계산 오류가 실패 상태 저장 오류로
             * 가려지지 않도록 한다.
             */
        }
    }

    /**
     * TransactionTemplate을 생성한다.
     */
    private TransactionTemplate createTransactionTemplate(
            int propagationBehavior,
            boolean readOnly
    ) {
        TransactionTemplate transactionTemplate =
                new TransactionTemplate(
                        transactionManager
                );

        transactionTemplate.setPropagationBehavior(
                propagationBehavior
        );

        transactionTemplate.setReadOnly(
                readOnly
        );

        return transactionTemplate;
    }

    /**
     * 사주 분석 요청을 검증한다.
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
     * 조회 가능한 연도를 검증한다.
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
     * 조회 가능한 월을 검증한다.
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