package com.dajeom.analyzer;

import com.dajeom.engine.AnnualFunctionalBaselinePipelineEngine;
import com.dajeom.engine.LifeDomainAnalysisEngine;
import com.dajeom.engine.LuckIntegratedAnalysisEngine;
import com.dajeom.engine.MonthlyFunctionalActivationPipelineEngine;
import com.dajeom.engine.ShinsalAnalysisEngine;
import com.dajeom.model.AnnualFunctionalBaselineAnalysis;
import com.dajeom.model.AnnualLuckAnalysis;
import com.dajeom.model.ConsultationTimelineRangeAnalysis;
import com.dajeom.model.DecadeLuckAnalysis;
import com.dajeom.model.LifeDomainAnalysis;
import com.dajeom.model.LifeDomainEvidenceResolution;
import com.dajeom.model.LuckIntegratedAnalysis;
import com.dajeom.model.LuckReferenceContext;
import com.dajeom.model.MonthlyFunctionalActivationAnalysis;
import com.dajeom.model.MonthlyLuckAnalysis;
import com.dajeom.model.NatalChartAdvancedAnalysis;
import com.dajeom.model.PalaceAnalysis;
import com.dajeom.model.ShinsalAnalysis;
import com.dajeom.model.TenGodCompositeAnalysis;
import com.dajeom.resolver.LifeDomainAnnualFunctionalSourceResolver;
import com.dajeom.resolver.LifeDomainDecadeContextSourceResolver;
import com.dajeom.resolver.LifeDomainEvidenceResolver;
import com.dajeom.resolver.LifeDomainMonthlyFunctionalSourceResolver;
import com.dajeom.resolver.LifeDomainNatalEvidenceSourceResolver;
import com.dajeom.resolver.LifeDomainNatalEvidenceSourceResolverFactory;
import com.dajeom.resolver.LuckIntegratedLayerAnalysisResolver;
import com.dajeom.resolver.LuckIntegratedLayerAnalysisResolverFactory;
import com.dajeom.resolver.ShinsalNatalPointSourceResolver;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 정밀화 21차-2.
 *
 * PAST_LUCK / FUTURE_LUCK용
 * deterministic Timeline Range Analyzer.
 *
 * =================================================
 *
 * 이 클래스는 새로운 명리 공식을 만들지 않는다.
 *
 * 각 기준 시점마다 기존:
 *
 * 16차 LuckIntegratedAnalysis
 *
 * ->
 *
 * 17차 ShinsalAnalysis
 *
 * ->
 *
 * Annual Functional Baseline
 *
 * ->
 *
 * Monthly Functional Activation
 *
 * ->
 *
 * 18차 LifeDomainAnalysis
 *
 * 를 그대로 실행한다.
 *
 * =================================================
 *
 * Annual Functional 원칙:
 *
 * - 현재 기준 시점의 16차 LuckIntegratedAnalysis가
 *   실제 보유한 AnnualLuckAnalysis를 사용한다.
 *
 * - 별도의 targetYear AnnualLuckAnalysis를 만들지 않는다.
 *
 * - AnnualLuckAnalysis 객체 Identity를 기준으로
 *   AnnualFunctionalBaselineAnalysis를 연결한다.
 *
 * - 같은 16차 Timeline 안에서 동일 AnnualLuckAnalysis가
 *   여러 월 Segment에 재사용되면 Pipeline 내부에서
 *   한 번만 계산한다.
 *
 * - Annual Functional Evidence는 기존 ANNUAL_CONTEXT를
 *   대체하지 않는다.
 *
 * - ANNUAL_FUNCTIONAL_CONTEXT는
 *   별도 derived temporal provenance로 추가한다.
 *
 * =================================================
 *
 * Monthly Functional 원칙:
 *
 * - 현재 기준 시점의 16차 LuckIntegratedAnalysis가
 *   실제 보유한 MonthlyLuckAnalysis를 사용한다.
 *
 * - MonthlyLuckAnalysis를 새로 만들지 않는다.
 *
 * - 위에서 생성한 공식 Annual Functional Baseline을
 *   그대로 입력으로 사용한다.
 *
 * - MonthlyLuckAnalysis 객체 Identity를 기준으로
 *   MonthlyFunctionalActivationAnalysis를 연결한다.
 *
 * - 동일 MonthlyLuckAnalysis가 Timeline Segment에서
 *   재사용되더라도 Pipeline 내부에서 한 번만 계산한다.
 *
 * - MONTHLY_FUNCTIONAL_CONTEXT는
 *   기존 MONTHLY_CONTEXT를 대체하지 않는다.
 *
 * - 월운 접촉을 Annual Functional 활성으로
 *   승격하지 않는다.
 *
 * =================================================
 *
 * 기본 Range:
 *
 * 과거 5년
 * 현재연도 제외
 *
 * 미래 5년
 * 현재연도 제외
 *
 * =================================================
 *
 * 각 명리연도는 12개월의 대표 내부 시점을 사용한다.
 *
 * 2월 ~ 12월 + 다음해 1월의 15일.
 *
 * 이유:
 *
 * 명리연도는 입춘 기준이며
 * 월운은 절입 기준으로 움직이므로
 * Gregorian 1월~12월만 단순 사용하지 않는다.
 *
 * 각 날짜에서도 실제 운 선택은
 * 16차 Candidate/Timeline Engine이 다시 수행한다.
 *
 * =================================================
 *
 * Timeline Range 병렬화 원칙:
 *
 * - 서로 다른 명리연도만 병렬 실행한다.
 *
 * - 하나의 명리연도 내부 12개 기준시점은
 *   기존 순서를 그대로 유지한다.
 *
 * - 최대 3개 연도만 동시에 실행한다.
 *
 * - targetYear 숫자로 Annual / Monthly Functional 결과를
 *   공유하거나 캐시하지 않는다.
 *
 * - 각 연도는 자신만의:
 *
 *   LuckIntegratedLayerAnalysisResolver
 *
 *   LifeDomainNatalEvidenceSourceResolver
 *
 *   를 사용한다.
 *
 * - 각 기준시점에서 생성되는 객체 Identity는
 *   기존과 동일하게 독립적으로 보존한다.
 */
@Component
public class ConsultationTimelineRangeAnalyzer {

    public static final int PAST_YEAR_COUNT =
            5;

    public static final int FUTURE_YEAR_COUNT =
            5;

    private static final int REPRESENTATIVE_DAY =
            15;

    private static final int
            MAX_PARALLEL_YEAR_ANALYSES =
            3;


    /*
     * =================================================
     * 16차
     * =================================================
     */

    private final LuckIntegratedAnalysisEngine
            luckIntegratedAnalysisEngine;

    private final LuckIntegratedLayerAnalysisResolverFactory
            layerAnalysisResolverFactory;


    /*
     * =================================================
     * 17차
     * =================================================
     */

    private final ShinsalAnalysisEngine
            shinsalAnalysisEngine;

    private final ShinsalNatalPointSourceResolver
            shinsalNatalPointSourceResolver;


    /*
     * =================================================
     * Annual Functional Baseline
     * =================================================
     */

    private final AnnualFunctionalBaselinePipelineEngine
            annualFunctionalBaselinePipelineEngine;


    /*
     * =================================================
     * Monthly Functional Activation
     * =================================================
     */

    private final MonthlyFunctionalActivationPipelineEngine
            monthlyFunctionalActivationPipelineEngine;


    /*
     * =================================================
     * 18차
     * =================================================
     */

    private final LifeDomainEvidenceResolver
            lifeDomainEvidenceResolver;

    private final LifeDomainAnalysisEngine
            lifeDomainAnalysisEngine;

    private final LifeDomainNatalEvidenceSourceResolverFactory
            natalEvidenceSourceResolverFactory;

    private final LifeDomainDecadeContextSourceResolver
            decadeContextSourceResolver;


    public ConsultationTimelineRangeAnalyzer(

            LuckIntegratedAnalysisEngine
                    luckIntegratedAnalysisEngine,

            LuckIntegratedLayerAnalysisResolverFactory
                    layerAnalysisResolverFactory,

            ShinsalAnalysisEngine
                    shinsalAnalysisEngine,

            ShinsalNatalPointSourceResolver
                    shinsalNatalPointSourceResolver,

            AnnualFunctionalBaselinePipelineEngine
                    annualFunctionalBaselinePipelineEngine,

            MonthlyFunctionalActivationPipelineEngine
                    monthlyFunctionalActivationPipelineEngine,

            LifeDomainEvidenceResolver
                    lifeDomainEvidenceResolver,

            LifeDomainAnalysisEngine
                    lifeDomainAnalysisEngine,

            LifeDomainNatalEvidenceSourceResolverFactory
                    natalEvidenceSourceResolverFactory,

            LifeDomainDecadeContextSourceResolver
                    decadeContextSourceResolver

    ) {

        this.luckIntegratedAnalysisEngine =
                Objects.requireNonNull(
                        luckIntegratedAnalysisEngine,
                        "LuckIntegratedAnalysisEngine이 필요합니다."
                );

        this.layerAnalysisResolverFactory =
                Objects.requireNonNull(
                        layerAnalysisResolverFactory,
                        "LuckIntegratedLayerAnalysisResolverFactory가 필요합니다."
                );

        this.shinsalAnalysisEngine =
                Objects.requireNonNull(
                        shinsalAnalysisEngine,
                        "ShinsalAnalysisEngine이 필요합니다."
                );

        this.shinsalNatalPointSourceResolver =
                Objects.requireNonNull(
                        shinsalNatalPointSourceResolver,
                        "ShinsalNatalPointSourceResolver가 필요합니다."
                );

        this.annualFunctionalBaselinePipelineEngine =
                Objects.requireNonNull(
                        annualFunctionalBaselinePipelineEngine,
                        "AnnualFunctionalBaselinePipelineEngine이 필요합니다."
                );

        this.monthlyFunctionalActivationPipelineEngine =
                Objects.requireNonNull(
                        monthlyFunctionalActivationPipelineEngine,
                        "MonthlyFunctionalActivationPipelineEngine이 필요합니다."
                );

        this.lifeDomainEvidenceResolver =
                Objects.requireNonNull(
                        lifeDomainEvidenceResolver,
                        "LifeDomainEvidenceResolver가 필요합니다."
                );

        this.lifeDomainAnalysisEngine =
                Objects.requireNonNull(
                        lifeDomainAnalysisEngine,
                        "LifeDomainAnalysisEngine이 필요합니다."
                );

        this.natalEvidenceSourceResolverFactory =
                Objects.requireNonNull(
                        natalEvidenceSourceResolverFactory,
                        "LifeDomainNatalEvidenceSourceResolverFactory가 필요합니다."
                );

        this.decadeContextSourceResolver =
                Objects.requireNonNull(
                        decadeContextSourceResolver,
                        "LifeDomainDecadeContextSourceResolver가 필요합니다."
                );
    }


    public ConsultationTimelineRangeAnalysis analyze(

            LocalDate currentReferenceDate,

            ZoneId zoneId,

            NatalChartAdvancedAnalysis advancedAnalysis,

            TenGodCompositeAnalysis tenGodCompositeAnalysis,

            PalaceAnalysis palaceAnalysis,

            DecadeLuckAnalysis decadeLuckAnalysis

    ) {

        Objects.requireNonNull(
                currentReferenceDate,
                "Timeline Range 기준날짜가 필요합니다."
        );

        Objects.requireNonNull(
                zoneId,
                "Timeline Range ZoneId가 필요합니다."
        );

        Objects.requireNonNull(
                advancedAnalysis,
                "Timeline Range에는 NatalChartAdvancedAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodCompositeAnalysis,
                "Timeline Range에는 TenGodCompositeAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                palaceAnalysis,
                "Timeline Range에는 PalaceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                decadeLuckAnalysis,
                "Timeline Range에는 DecadeLuckAnalysis가 필요합니다."
        );


        int currentYear =
                currentReferenceDate
                        .getYear();


        /*
         * =================================================
         * Year Task 구성
         * =================================================
         */

        List<YearTask>
                yearTasks =
                new ArrayList<>(
                        PAST_YEAR_COUNT
                                + FUTURE_YEAR_COUNT
                );


        /*
         * =================================================
         * Past Task
         * =================================================
         */

        for (
                int year =
                currentYear
                        - PAST_YEAR_COUNT;

                year
                        < currentYear;

                year++
        ) {

            yearTasks.add(
                    createYearTask(
                            year,
                            true,
                            zoneId,
                            advancedAnalysis,
                            tenGodCompositeAnalysis,
                            palaceAnalysis,
                            decadeLuckAnalysis
                    )
            );
        }


        /*
         * =================================================
         * Future Task
         * =================================================
         */

        for (
                int year =
                currentYear + 1;

                year
                        <= currentYear
                        + FUTURE_YEAR_COUNT;

                year++
        ) {

            yearTasks.add(
                    createYearTask(
                            year,
                            false,
                            zoneId,
                            advancedAnalysis,
                            tenGodCompositeAnalysis,
                            palaceAnalysis,
                            decadeLuckAnalysis
                    )
            );
        }


        if (
                yearTasks.isEmpty()
        ) {

            return new ConsultationTimelineRangeAnalysis(
                    currentReferenceDate,
                    List.of(),
                    List.of()
            );
        }


        int workerCount =
                Math.min(
                        MAX_PARALLEL_YEAR_ANALYSES,
                        yearTasks.size()
                );


        ExecutorService executorService =
                Executors.newFixedThreadPool(
                        workerCount
                );


        List<
                Future<YearAnalysisResult>
                > futures =
                new ArrayList<>(
                        yearTasks.size()
                );


        try {

            for (
                    YearTask yearTask
                    : yearTasks
            ) {

                futures.add(
                        executorService.submit(
                                () ->
                                        analyzeYearTask(
                                                yearTask
                                        )
                        )
                );
            }


            List<LifeDomainAnalysis>
                    past =
                    new ArrayList<>(
                            PAST_YEAR_COUNT
                                    * 12
                    );


            List<LifeDomainAnalysis>
                    future =
                    new ArrayList<>(
                            FUTURE_YEAR_COUNT
                                    * 12
                    );


            for (
                    Future<YearAnalysisResult>
                            futureResult
                    : futures
            ) {

                YearAnalysisResult result =
                        futureResult.get();


                if (
                        result.past()
                ) {

                    past.addAll(
                            result.analyses()
                    );

                }
                else {

                    future.addAll(
                            result.analyses()
                    );
                }
            }


            return new ConsultationTimelineRangeAnalysis(
                    currentReferenceDate,
                    List.copyOf(
                            past
                    ),
                    List.copyOf(
                            future
                    )
            );

        }
        catch (
                InterruptedException exception
        ) {

            cancelFutures(
                    futures
            );

            Thread
                    .currentThread()
                    .interrupt();

            throw new IllegalStateException(
                    "Timeline Range 연도 병렬 분석 대기 중 Thread가 중단되었습니다.",
                    exception
            );

        }
        catch (
                ExecutionException exception
        ) {

            cancelFutures(
                    futures
            );

            throw propagateExecutionFailure(
                    exception
            );

        }
        finally {

            executorService
                    .shutdownNow();
        }
    }


    private YearTask createYearTask(

            int targetYear,

            boolean past,

            ZoneId zoneId,

            NatalChartAdvancedAnalysis advancedAnalysis,

            TenGodCompositeAnalysis tenGodCompositeAnalysis,

            PalaceAnalysis palaceAnalysis,

            DecadeLuckAnalysis decadeLuckAnalysis

    ) {

        LuckIntegratedLayerAnalysisResolver
                layerResolver =
                Objects.requireNonNull(
                        layerAnalysisResolverFactory
                                .create(
                                        advancedAnalysis,
                                        tenGodCompositeAnalysis,
                                        palaceAnalysis
                                ),
                        "Timeline Range 연도별 LuckIntegratedLayerAnalysisResolver가 null일 수 없습니다."
                );


        LifeDomainNatalEvidenceSourceResolver
                natalEvidenceResolver =
                Objects.requireNonNull(
                        natalEvidenceSourceResolverFactory
                                .create(
                                        advancedAnalysis,
                                        tenGodCompositeAnalysis,
                                        palaceAnalysis
                                ),
                        "Timeline Range 연도별 LifeDomainNatalEvidenceSourceResolver가 null일 수 없습니다."
                );


        return new YearTask(
                targetYear,
                past,
                zoneId,
                advancedAnalysis,
                tenGodCompositeAnalysis,
                palaceAnalysis,
                decadeLuckAnalysis,
                layerResolver,
                natalEvidenceResolver
        );
    }


    private YearAnalysisResult analyzeYearTask(
            YearTask yearTask
    ) {

        Objects.requireNonNull(
                yearTask,
                "Timeline Range YearTask가 필요합니다."
        );


        List<LifeDomainAnalysis>
                analyses =
                analyzeSajuYear(
                        yearTask.targetYear(),
                        yearTask.zoneId(),
                        yearTask.advancedAnalysis(),
                        yearTask.tenGodCompositeAnalysis(),
                        yearTask.palaceAnalysis(),
                        yearTask.decadeLuckAnalysis(),
                        yearTask.layerResolver(),
                        yearTask.natalEvidenceResolver()
                );


        return new YearAnalysisResult(
                yearTask.targetYear(),
                yearTask.past(),
                analyses
        );
    }


    private List<LifeDomainAnalysis> analyzeSajuYear(

            int targetYear,

            ZoneId zoneId,

            NatalChartAdvancedAnalysis advancedAnalysis,

            TenGodCompositeAnalysis tenGodCompositeAnalysis,

            PalaceAnalysis palaceAnalysis,

            DecadeLuckAnalysis decadeLuckAnalysis,

            LuckIntegratedLayerAnalysisResolver
                    layerResolver,

            LifeDomainNatalEvidenceSourceResolver
                    natalEvidenceResolver

    ) {

        List<LifeDomainAnalysis> result =
                new ArrayList<>(
                        12
                );


        for (
                int month = 2;
                month <= 12;
                month++
        ) {

            result.add(
                    analyzeReferenceDate(
                            LocalDate.of(
                                    targetYear,
                                    month,
                                    REPRESENTATIVE_DAY
                            ),
                            zoneId,
                            advancedAnalysis,
                            tenGodCompositeAnalysis,
                            palaceAnalysis,
                            decadeLuckAnalysis,
                            layerResolver,
                            natalEvidenceResolver
                    )
            );
        }


        result.add(
                analyzeReferenceDate(
                        LocalDate.of(
                                targetYear + 1,
                                1,
                                REPRESENTATIVE_DAY
                        ),
                        zoneId,
                        advancedAnalysis,
                        tenGodCompositeAnalysis,
                        palaceAnalysis,
                        decadeLuckAnalysis,
                        layerResolver,
                        natalEvidenceResolver
                )
        );


        return List.copyOf(
                result
        );
    }


    private LifeDomainAnalysis analyzeReferenceDate(

            LocalDate referenceDate,

            ZoneId zoneId,

            NatalChartAdvancedAnalysis advancedAnalysis,

            TenGodCompositeAnalysis tenGodCompositeAnalysis,

            PalaceAnalysis palaceAnalysis,

            DecadeLuckAnalysis decadeLuckAnalysis,

            LuckIntegratedLayerAnalysisResolver
                    layerResolver,

            LifeDomainNatalEvidenceSourceResolver
                    natalEvidenceResolver

    ) {

        Objects.requireNonNull(
                referenceDate,
                "Timeline Range 기준날짜가 필요합니다."
        );

        Objects.requireNonNull(
                zoneId,
                "Timeline Range 기준날짜 분석에는 ZoneId가 필요합니다."
        );

        Objects.requireNonNull(
                advancedAnalysis,
                "Timeline Range Annual Functional에는 NatalChartAdvancedAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodCompositeAnalysis,
                "Timeline Range Annual Functional에는 TenGodCompositeAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                palaceAnalysis,
                "Timeline Range 기준날짜 분석에는 PalaceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                decadeLuckAnalysis,
                "Timeline Range 기준날짜 분석에는 DecadeLuckAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                layerResolver,
                "Timeline Range 기준날짜 분석에는 LuckIntegratedLayerAnalysisResolver가 필요합니다."
        );

        Objects.requireNonNull(
                natalEvidenceResolver,
                "Timeline Range 기준날짜 분석에는 LifeDomainNatalEvidenceSourceResolver가 필요합니다."
        );


        /*
         * =================================================
         * 16차
         * =================================================
         */

        LuckReferenceContext referenceContext =
                LuckReferenceContext
                        .dateOnly(
                                referenceDate,
                                zoneId
                        );


        LuckIntegratedAnalysis
                integratedAnalysis =
                Objects.requireNonNull(
                        luckIntegratedAnalysisEngine
                                .analyze(
                                        palaceAnalysis,
                                        decadeLuckAnalysis,
                                        referenceContext,
                                        layerResolver
                                ),
                        "Timeline Range 16차 분석이 null을 반환했습니다."
                );


        if (
                integratedAnalysis
                        .natalAnalysis()
                        != palaceAnalysis
        ) {

            throw new IllegalStateException(
                    "Timeline Range 16차 분석이 현재 PalaceAnalysis를 보존하지 않았습니다."
            );
        }


        if (
                integratedAnalysis
                        .decadeLuckAnalysis()
                        != decadeLuckAnalysis
        ) {

            throw new IllegalStateException(
                    "Timeline Range 16차 분석이 현재 DecadeLuckAnalysis를 보존하지 않았습니다."
            );
        }


        if (
                integratedAnalysis
                        .referenceContext()
                        != referenceContext
        ) {

            throw new IllegalStateException(
                    "Timeline Range 16차 분석이 현재 LuckReferenceContext Identity를 보존하지 않았습니다."
            );
        }


        /*
         * =================================================
         * 17차
         * =================================================
         */

        ShinsalAnalysis
                shinsalAnalysis =
                Objects.requireNonNull(
                        shinsalAnalysisEngine
                                .analyze(
                                        integratedAnalysis,
                                        shinsalNatalPointSourceResolver
                                ),
                        "Timeline Range 17차 분석이 null을 반환했습니다."
                );


        if (
                shinsalAnalysis
                        .luckIntegratedAnalysis()
                        != integratedAnalysis
        ) {

            throw new IllegalStateException(
                    "Timeline Range 17차 분석이 현재 LuckIntegratedAnalysis를 보존하지 않았습니다."
            );
        }


        /*
         * =================================================
         * Annual Functional Baseline
         * =================================================
         */

        Map<
                AnnualLuckAnalysis,
                AnnualFunctionalBaselineAnalysis
                > annualFunctionalBaselines =
                annualFunctionalBaselinePipelineEngine
                        .analyze(
                                advancedAnalysis,
                                tenGodCompositeAnalysis,
                                integratedAnalysis
                        );


        /*
         * =================================================
         * Monthly Functional Activation
         * =================================================
         *
         * 방금 계산한 공식 Annual Functional Baseline을
         * 그대로 사용한다.
         *
         * 별도 Annual 재계산 없음.
         */

        Map<
                MonthlyLuckAnalysis,
                MonthlyFunctionalActivationAnalysis
                > monthlyFunctionalActivations =
                monthlyFunctionalActivationPipelineEngine
                        .analyze(
                                integratedAnalysis,
                                annualFunctionalBaselines
                        );


        /*
         * =================================================
         * Annual Functional Source Resolver
         * =================================================
         */

        LifeDomainAnnualFunctionalSourceResolver
                annualFunctionalSourceResolver =
                segmentAnalysis -> {

                    AnnualLuckAnalysis
                            segmentAnnualLuckAnalysis =
                            segmentAnalysis
                                    .annualLuckAnalysis();


                    AnnualFunctionalBaselineAnalysis baseline =
                            annualFunctionalBaselines
                                    .get(
                                            segmentAnnualLuckAnalysis
                                    );


                    if (
                            baseline == null
                    ) {

                        throw new IllegalStateException(
                                "Timeline Range 18차 Annual Functional Baseline이 "
                                        + "현재 Segment의 AnnualLuckAnalysis에 존재하지 않습니다. "
                                        + "referenceDate="
                                        + referenceDate
                                        + ", targetYear="
                                        + segmentAnnualLuckAnalysis.targetYear()
                        );
                    }


                    if (
                            baseline
                                    .annualLuckAnalysis()
                                    != segmentAnnualLuckAnalysis
                    ) {

                        throw new IllegalStateException(
                                "Timeline Range Annual Functional Baseline이 "
                                        + "현재 Segment AnnualLuckAnalysis Identity를 보존하지 않았습니다. "
                                        + "referenceDate="
                                        + referenceDate
                                        + ", targetYear="
                                        + segmentAnnualLuckAnalysis.targetYear()
                        );
                    }


                    return Optional.of(
                            baseline
                    );
                };


        /*
         * =================================================
         * Monthly Functional Source Resolver
         * =================================================
         *
         * 현재 Segment가 실제 보유한
         * MonthlyLuckAnalysis Identity로만 조회한다.
         */

        LifeDomainMonthlyFunctionalSourceResolver
                monthlyFunctionalSourceResolver =
                segmentAnalysis -> {

                    MonthlyLuckAnalysis
                            segmentMonthlyLuckAnalysis =
                            segmentAnalysis
                                    .monthlyLuckAnalysis();


                    MonthlyFunctionalActivationAnalysis activation =
                            monthlyFunctionalActivations
                                    .get(
                                            segmentMonthlyLuckAnalysis
                                    );


                    if (
                            activation == null
                    ) {

                        throw new IllegalStateException(
                                "Timeline Range 18차 Monthly Functional Activation이 "
                                        + "현재 Segment의 MonthlyLuckAnalysis에 존재하지 않습니다. "
                                        + "referenceDate="
                                        + referenceDate
                                        + ", sajuYear="
                                        + segmentMonthlyLuckAnalysis.sajuYear()
                                        + ", monthIndex="
                                        + segmentMonthlyLuckAnalysis.monthIndex()
                        );
                    }


                    if (
                            activation
                                    .monthlyLuckAnalysis()
                                    != segmentMonthlyLuckAnalysis
                    ) {

                        throw new IllegalStateException(
                                "Timeline Range Monthly Functional Activation이 "
                                        + "현재 Segment MonthlyLuckAnalysis Identity를 보존하지 않았습니다. "
                                        + "referenceDate="
                                        + referenceDate
                                        + ", sajuYear="
                                        + segmentMonthlyLuckAnalysis.sajuYear()
                                        + ", monthIndex="
                                        + segmentMonthlyLuckAnalysis.monthIndex()
                        );
                    }


                    if (
                            activation
                                    .annualFunctionalBaseline()
                                    .annualLuckAnalysis()
                                    != segmentAnalysis
                                    .annualLuckAnalysis()
                    ) {

                        throw new IllegalStateException(
                                "Timeline Range Monthly Functional Activation의 Annual Functional Baseline이 "
                                        + "현재 Segment AnnualLuckAnalysis Identity를 보존하지 않았습니다. "
                                        + "referenceDate="
                                        + referenceDate
                                        + ", sajuYear="
                                        + segmentMonthlyLuckAnalysis.sajuYear()
                                        + ", monthIndex="
                                        + segmentMonthlyLuckAnalysis.monthIndex()
                        );
                    }


                    return Optional.of(
                            activation
                    );
                };


        /*
         * =================================================
         * 18차 Evidence Resolution
         * =================================================
         *
         * Natal
         * +
         * Decade Context
         * +
         * Annual Context
         * +
         * Annual Functional Context
         * +
         * Monthly Context
         * +
         * Monthly Functional Context
         * +
         * Shinsal
         */

        LifeDomainEvidenceResolution
                evidenceResolution =
                Objects.requireNonNull(
                        lifeDomainEvidenceResolver
                                .resolve(
                                        integratedAnalysis,
                                        shinsalAnalysis,
                                        natalEvidenceResolver,
                                        decadeContextSourceResolver,
                                        annualFunctionalSourceResolver,
                                        monthlyFunctionalSourceResolver
                                ),
                        "Timeline Range 18차 Evidence Resolution이 null을 반환했습니다."
                );


        if (
                evidenceResolution
                        .luckIntegratedAnalysis()
                        != integratedAnalysis
        ) {

            throw new IllegalStateException(
                    "Timeline Range 18차 Evidence Resolution이 현재 LuckIntegratedAnalysis를 보존하지 않았습니다."
            );
        }


        if (
                evidenceResolution
                        .shinsalAnalysis()
                        != shinsalAnalysis
        ) {

            throw new IllegalStateException(
                    "Timeline Range 18차 Evidence Resolution이 현재 ShinsalAnalysis를 보존하지 않았습니다."
            );
        }


        /*
         * =================================================
         * 18차 LifeDomain
         * =================================================
         */

        LifeDomainAnalysis
                lifeDomainAnalysis =
                Objects.requireNonNull(
                        lifeDomainAnalysisEngine
                                .analyze(
                                        evidenceResolution
                                ),
                        "Timeline Range 18차 분석이 null을 반환했습니다."
                );


        if (
                lifeDomainAnalysis
                        .evidenceResolution()
                        != evidenceResolution
        ) {

            throw new IllegalStateException(
                    "Timeline Range 18차 LifeDomainAnalysis가 현재 EvidenceResolution Identity를 보존하지 않았습니다."
            );
        }


        return lifeDomainAnalysis;
    }


    private static void cancelFutures(
            List<Future<YearAnalysisResult>> futures
    ) {

        if (
                futures == null
                        || futures.isEmpty()
        ) {

            return;
        }


        for (
                Future<YearAnalysisResult> future
                : futures
        ) {

            if (
                    future == null
                            || future.isDone()
            ) {

                continue;
            }


            future.cancel(
                    true
            );
        }
    }


    private static RuntimeException
    propagateExecutionFailure(
            ExecutionException exception
    ) {

        Objects.requireNonNull(
                exception,
                "Timeline Range ExecutionException이 필요합니다."
        );


        Throwable cause =
                exception.getCause();


        if (
                cause instanceof RuntimeException
                        runtimeException
        ) {

            return runtimeException;
        }


        if (
                cause instanceof Error error
        ) {

            throw error;
        }


        return new IllegalStateException(
                "Timeline Range 연도 병렬 분석 중 오류가 발생했습니다.",
                cause == null
                        ? exception
                        : cause
        );
    }


    private record YearTask(

            int targetYear,

            boolean past,

            ZoneId zoneId,

            NatalChartAdvancedAnalysis
            advancedAnalysis,

            TenGodCompositeAnalysis
            tenGodCompositeAnalysis,

            PalaceAnalysis palaceAnalysis,

            DecadeLuckAnalysis
            decadeLuckAnalysis,

            LuckIntegratedLayerAnalysisResolver
            layerResolver,

            LifeDomainNatalEvidenceSourceResolver
            natalEvidenceResolver

    ) {

        private YearTask {

            Objects.requireNonNull(
                    zoneId,
                    "YearTask ZoneId가 필요합니다."
            );

            Objects.requireNonNull(
                    advancedAnalysis,
                    "YearTask NatalChartAdvancedAnalysis가 필요합니다."
            );

            Objects.requireNonNull(
                    tenGodCompositeAnalysis,
                    "YearTask TenGodCompositeAnalysis가 필요합니다."
            );

            Objects.requireNonNull(
                    palaceAnalysis,
                    "YearTask PalaceAnalysis가 필요합니다."
            );

            Objects.requireNonNull(
                    decadeLuckAnalysis,
                    "YearTask DecadeLuckAnalysis가 필요합니다."
            );

            Objects.requireNonNull(
                    layerResolver,
                    "YearTask LuckIntegratedLayerAnalysisResolver가 필요합니다."
            );

            Objects.requireNonNull(
                    natalEvidenceResolver,
                    "YearTask LifeDomainNatalEvidenceSourceResolver가 필요합니다."
            );
        }
    }


    private record YearAnalysisResult(

            int targetYear,

            boolean past,

            List<LifeDomainAnalysis> analyses

    ) {

        private YearAnalysisResult {

            Objects.requireNonNull(
                    analyses,
                    "YearAnalysisResult analyses가 필요합니다."
            );


            if (
                    analyses.size()
                            != 12
            ) {

                throw new IllegalArgumentException(
                        "Timeline Range 하나의 명리연도는 정확히 12개 기준시점이어야 합니다. "
                                + "targetYear="
                                + targetYear
                                + ", actual="
                                + analyses.size()
                );
            }


            analyses =
                    List.copyOf(
                            analyses
                    );
        }
    }
}