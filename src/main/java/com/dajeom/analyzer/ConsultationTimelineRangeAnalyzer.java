package com.dajeom.analyzer;

import com.dajeom.engine.AnnualFunctionalBaselinePipelineEngine;
import com.dajeom.engine.LifeDomainAnalysisEngine;
import com.dajeom.engine.LuckIntegratedAnalysisEngine;
import com.dajeom.engine.ShinsalAnalysisEngine;
import com.dajeom.model.AnnualFunctionalBaselineAnalysis;
import com.dajeom.model.AnnualLuckAnalysis;
import com.dajeom.model.ConsultationTimelineRangeAnalysis;
import com.dajeom.model.DecadeLuckAnalysis;
import com.dajeom.model.LifeDomainAnalysis;
import com.dajeom.model.LifeDomainEvidenceResolution;
import com.dajeom.model.LuckIntegratedAnalysis;
import com.dajeom.model.LuckReferenceContext;
import com.dajeom.model.NatalChartAdvancedAnalysis;
import com.dajeom.model.PalaceAnalysis;
import com.dajeom.model.ShinsalAnalysis;
import com.dajeom.model.TenGodCompositeAnalysis;
import com.dajeom.resolver.LifeDomainAnnualFunctionalSourceResolver;
import com.dajeom.resolver.LifeDomainDecadeContextSourceResolver;
import com.dajeom.resolver.LifeDomainEvidenceResolver;
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
 * - targetYear 숫자로 Annual Functional 결과를
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
 * - 각 기준시점에서 생성되는
 *   LuckReferenceContext
 *   LuckIntegratedAnalysis
 *   AnnualLuckAnalysis
 *   ShinsalAnalysis
 *   LifeDomainEvidenceResolution
 *   LifeDomainAnalysis
 *
 *   객체 Identity는 기존과 동일하게 독립적으로 보존한다.
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
         *
         * 전체 10개 명리연도를 먼저 Task로 구성한다.
         *
         * Task 자체는:
         *
         * 과거 5년
         * ->
         * 미래 5년
         *
         * 순서로 등록한다.
         *
         * 실행은 최대 3개 연도가 병렬로 진행되지만
         * 결과 수집 순서는 등록 순서 그대로 유지한다.
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

            /*
             * =================================================
             * 모든 연도 Task 제출
             * =================================================
             *
             * 앞 연도 결과를 기다리면서
             * 다음 연도를 제출하지 않는다.
             *
             * 먼저 전부 제출해야
             * 최대 3개의 연도가 실제 병렬 실행된다.
             */

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


            /*
             * =================================================
             * 등록 순서대로 결과 회수
             * =================================================
             *
             * Future.get() 순서는 실행 병렬성을 제거하지 않는다.
             *
             * 모든 Task는 위에서 이미 제출되었기 때문에
             * worker thread에서 독립적으로 진행 중이다.
             *
             * 결과 List 순서만 deterministic하게 보존한다.
             */

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


    /*
     * =================================================
     * Year Task 생성
     * =================================================
     *
     * Resolver Factory 호출은
     * 요청 Thread에서 순차적으로 수행한다.
     *
     * 각 연도 Task는 서로 다른 Resolver 인스턴스를
     * 전달받는다.
     *
     * 따라서:
     *
     * - 연도끼리 Resolver 상태를 공유하지 않는다.
     * - Factory 자체를 worker thread에서
     *   동시에 호출하지 않는다.
     */

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


    /*
     * =================================================
     * Year Task 실행
     * =================================================
     */

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


    /*
     * =================================================
     * 하나의 명리연도
     * =================================================
     *
     * targetYear:
     *
     * 2월 ~ 12월
     * +
     * targetYear + 1의 1월
     *
     * 총 12개 기준점.
     *
     * 실제 Annual / Monthly / Decade Candidate 판단은
     * 16차가 수행한다.
     *
     * 한 명리연도 내부의 12개 날짜는
     * 의도적으로 순차 실행한다.
     */

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


        /*
         * =================================================
         * targetYear 2월 ~ 12월
         * =================================================
         */

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


        /*
         * =================================================
         * 다음 Gregorian 연도 1월
         * =================================================
         *
         * 명리연도는 아직 targetYear의
         * 세운 범위 안에 있을 수 있으므로
         * 16차가 실제 Candidate를 결정한다.
         */

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


    /*
     * =================================================
     * 한 기준날짜
     * =================================================
     */

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
         *
         * 이 Reference Date에서 16차가 실제 생성한
         * AnnualLuckAnalysis 객체들만 사용한다.
         *
         * 별도의 targetYear 계산을 하지 않는다.
         *
         * 다른 연도 Task의 AnnualLuckAnalysis와
         * 숫자가 같더라도 절대 공유하지 않는다.
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
         * Annual Functional Source Resolver
         * =================================================
         *
         * LifeDomain Segment가 보유한
         * AnnualLuckAnalysis Identity로만 조회한다.
         *
         * 연도 숫자로 조회하지 않는다.
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
         * 18차 Evidence Resolution
         * =================================================
         *
         * 기존:
         *
         * Natal
         * +
         * Decade Context
         * +
         * Annual Context
         * +
         * Monthly Context
         * +
         * Shinsal
         *
         * 에 추가로:
         *
         * Annual Functional Context
         *
         * 를 같은 Segment에 연결한다.
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
                                        annualFunctionalSourceResolver
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


    /*
     * =================================================
     * Future 취소
     * =================================================
     */

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


    /*
     * =================================================
     * Worker Exception 전달
     * =================================================
     */

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


    /*
     * =================================================
     * Year Task
     * =================================================
     */

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


    /*
     * =================================================
     * Year Result
     * =================================================
     */

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