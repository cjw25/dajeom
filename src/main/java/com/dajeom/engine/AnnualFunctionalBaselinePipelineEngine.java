package com.dajeom.engine;

import com.dajeom.model.AnnualCrossFunctionalSynthesisAnalysis;
import com.dajeom.model.AnnualDecadeFunctionalAnalysis;
import com.dajeom.model.AnnualDecadeTransformationAnalysis;
import com.dajeom.model.AnnualFunctionalBaselineAnalysis;
import com.dajeom.model.AnnualFunctionalCapacityAnalysis;
import com.dajeom.model.AnnualFunctionalProjectionAnalysis;
import com.dajeom.model.AnnualLuckAnalysis;
import com.dajeom.model.AnnualResolvedCrossFunctionalAnalysis;
import com.dajeom.model.AnnualResolvedFunctionalAnalysis;
import com.dajeom.model.DayMasterElementRelationAnalysis;
import com.dajeom.model.DecadeCrossFunctionalAnalysis;
import com.dajeom.model.DecadeFunctionalAnalysis;
import com.dajeom.model.DecadeFunctionalCapacityAnalysis;
import com.dajeom.model.DecadeFunctionalMigrationAnalysis;
import com.dajeom.model.DecadeFunctionalOperationAnalysis;
import com.dajeom.model.DecadeFunctionalSynthesisAnalysis;
import com.dajeom.model.DecadeResolvedFunctionalAnalysis;
import com.dajeom.model.LuckIntegratedAnalysis;
import com.dajeom.model.LuckIntegratedSegmentAnalysis;
import com.dajeom.model.NatalChartAdvancedAnalysis;
import com.dajeom.model.NatalCrossFunctionalAnalysis;
import com.dajeom.model.NatalFunctionalAnalysis;
import com.dajeom.model.TenGodCompositeAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Annual Functional 전체 체인의 공식 Pipeline.
 *
 * =================================================
 *
 * Natal:
 *
 * NatalFunctionalAnalysis
 * ->
 * NatalCrossFunctionalAnalysis
 *
 * =================================================
 *
 * Decade:
 *
 * DecadeFunctionalAnalysis
 * ->
 * DecadeFunctionalOperationAnalysis
 * ->
 * DecadeFunctionalCapacityAnalysis
 * ->
 * DecadeFunctionalMigrationAnalysis
 * ->
 * DecadeResolvedFunctionalAnalysis
 * ->
 * DecadeCrossFunctionalAnalysis
 * ->
 * DecadeFunctionalSynthesisAnalysis
 *
 * =================================================
 *
 * Annual:
 *
 * AnnualFunctionalProjectionAnalysis
 * ->
 * AnnualFunctionalCapacityAnalysis
 * ->
 * AnnualDecadeFunctionalAnalysis
 * ->
 * AnnualDecadeTransformationAnalysis
 * ->
 * AnnualResolvedFunctionalAnalysis
 * ->
 * AnnualResolvedCrossFunctionalAnalysis
 * ->
 * AnnualCrossFunctionalSynthesisAnalysis
 * ->
 * AnnualFunctionalBaselineAnalysis
 *
 * =================================================
 *
 * 같은 AnnualLuckAnalysis 객체가
 * 여러 Timeline Segment에서 재사용되면
 * Annual Functional 계산은 한 번만 수행한다.
 *
 * =================================================
 * Runtime Diagnostic
 * =================================================
 *
 * 현재 Timeline Range 성능 병목 확인을 위해
 * 각 Functional 단계의 실행 시간을 기록한다.
 *
 * 분석 결과나 판정 로직은 변경하지 않는다.
 */
@Component
public class AnnualFunctionalBaselinePipelineEngine {

    private static final Logger log =
            LoggerFactory.getLogger(
                    AnnualFunctionalBaselinePipelineEngine.class
            );

    private static final AtomicLong
            DIAGNOSTIC_SEQUENCE =
            new AtomicLong();

    private final NatalFunctionalAnalysisEngine
            natalFunctionalAnalysisEngine;

    private final NatalCrossFunctionalAnalysisEngine
            natalCrossFunctionalAnalysisEngine;

    private final DecadeFunctionalAnalysisEngine
            decadeFunctionalAnalysisEngine;

    private final DecadeFunctionalOperationEngine
            decadeFunctionalOperationEngine;

    private final DecadeFunctionalCapacityAnalysisEngine
            decadeFunctionalCapacityAnalysisEngine;

    private final DecadeFunctionalMigrationAnalysisEngine
            decadeFunctionalMigrationAnalysisEngine;

    private final DecadeResolvedFunctionalAnalysisEngine
            decadeResolvedFunctionalAnalysisEngine;

    private final DecadeCrossFunctionalAnalysisEngine
            decadeCrossFunctionalAnalysisEngine;

    private final DecadeFunctionalSynthesisAnalysisEngine
            decadeFunctionalSynthesisAnalysisEngine;

    private final AnnualFunctionalProjectionEngine
            annualFunctionalProjectionEngine;

    private final AnnualFunctionalCapacityAnalysisEngine
            annualFunctionalCapacityAnalysisEngine;

    private final AnnualDecadeFunctionalAnalysisEngine
            annualDecadeFunctionalAnalysisEngine;

    private final AnnualDecadeTransformationResolverEngine
            annualDecadeTransformationResolverEngine;

    private final AnnualResolvedFunctionalAnalysisEngine
            annualResolvedFunctionalAnalysisEngine;

    private final AnnualResolvedCrossFunctionalAnalysisEngine
            annualResolvedCrossFunctionalAnalysisEngine;

    private final AnnualCrossFunctionalSynthesisAnalysisEngine
            annualCrossFunctionalSynthesisAnalysisEngine;

    private final AnnualFunctionalBaselineAnalysisEngine
            annualFunctionalBaselineAnalysisEngine;

    public AnnualFunctionalBaselinePipelineEngine(

            NatalFunctionalAnalysisEngine
                    natalFunctionalAnalysisEngine,

            NatalCrossFunctionalAnalysisEngine
                    natalCrossFunctionalAnalysisEngine,

            DecadeFunctionalAnalysisEngine
                    decadeFunctionalAnalysisEngine,

            DecadeFunctionalOperationEngine
                    decadeFunctionalOperationEngine,

            DecadeFunctionalCapacityAnalysisEngine
                    decadeFunctionalCapacityAnalysisEngine,

            DecadeFunctionalMigrationAnalysisEngine
                    decadeFunctionalMigrationAnalysisEngine,

            DecadeResolvedFunctionalAnalysisEngine
                    decadeResolvedFunctionalAnalysisEngine,

            DecadeCrossFunctionalAnalysisEngine
                    decadeCrossFunctionalAnalysisEngine,

            DecadeFunctionalSynthesisAnalysisEngine
                    decadeFunctionalSynthesisAnalysisEngine,

            AnnualFunctionalProjectionEngine
                    annualFunctionalProjectionEngine,

            AnnualFunctionalCapacityAnalysisEngine
                    annualFunctionalCapacityAnalysisEngine,

            AnnualDecadeFunctionalAnalysisEngine
                    annualDecadeFunctionalAnalysisEngine,

            AnnualDecadeTransformationResolverEngine
                    annualDecadeTransformationResolverEngine,

            AnnualResolvedFunctionalAnalysisEngine
                    annualResolvedFunctionalAnalysisEngine,

            AnnualResolvedCrossFunctionalAnalysisEngine
                    annualResolvedCrossFunctionalAnalysisEngine,

            AnnualCrossFunctionalSynthesisAnalysisEngine
                    annualCrossFunctionalSynthesisAnalysisEngine,

            AnnualFunctionalBaselineAnalysisEngine
                    annualFunctionalBaselineAnalysisEngine

    ) {

        this.natalFunctionalAnalysisEngine =
                Objects.requireNonNull(
                        natalFunctionalAnalysisEngine
                );

        this.natalCrossFunctionalAnalysisEngine =
                Objects.requireNonNull(
                        natalCrossFunctionalAnalysisEngine
                );

        this.decadeFunctionalAnalysisEngine =
                Objects.requireNonNull(
                        decadeFunctionalAnalysisEngine
                );

        this.decadeFunctionalOperationEngine =
                Objects.requireNonNull(
                        decadeFunctionalOperationEngine
                );

        this.decadeFunctionalCapacityAnalysisEngine =
                Objects.requireNonNull(
                        decadeFunctionalCapacityAnalysisEngine
                );

        this.decadeFunctionalMigrationAnalysisEngine =
                Objects.requireNonNull(
                        decadeFunctionalMigrationAnalysisEngine
                );

        this.decadeResolvedFunctionalAnalysisEngine =
                Objects.requireNonNull(
                        decadeResolvedFunctionalAnalysisEngine
                );

        this.decadeCrossFunctionalAnalysisEngine =
                Objects.requireNonNull(
                        decadeCrossFunctionalAnalysisEngine
                );

        this.decadeFunctionalSynthesisAnalysisEngine =
                Objects.requireNonNull(
                        decadeFunctionalSynthesisAnalysisEngine
                );

        this.annualFunctionalProjectionEngine =
                Objects.requireNonNull(
                        annualFunctionalProjectionEngine
                );

        this.annualFunctionalCapacityAnalysisEngine =
                Objects.requireNonNull(
                        annualFunctionalCapacityAnalysisEngine
                );

        this.annualDecadeFunctionalAnalysisEngine =
                Objects.requireNonNull(
                        annualDecadeFunctionalAnalysisEngine
                );

        this.annualDecadeTransformationResolverEngine =
                Objects.requireNonNull(
                        annualDecadeTransformationResolverEngine
                );

        this.annualResolvedFunctionalAnalysisEngine =
                Objects.requireNonNull(
                        annualResolvedFunctionalAnalysisEngine
                );

        this.annualResolvedCrossFunctionalAnalysisEngine =
                Objects.requireNonNull(
                        annualResolvedCrossFunctionalAnalysisEngine
                );

        this.annualCrossFunctionalSynthesisAnalysisEngine =
                Objects.requireNonNull(
                        annualCrossFunctionalSynthesisAnalysisEngine
                );

        this.annualFunctionalBaselineAnalysisEngine =
                Objects.requireNonNull(
                        annualFunctionalBaselineAnalysisEngine
                );
    }

    public Map<
            AnnualLuckAnalysis,
            AnnualFunctionalBaselineAnalysis
            > analyze(

            NatalChartAdvancedAnalysis advancedAnalysis,

            TenGodCompositeAnalysis tenGodCompositeAnalysis,

            LuckIntegratedAnalysis luckIntegratedAnalysis

    ) {

        Objects.requireNonNull(
                advancedAnalysis,
                "Annual Functional Pipeline에는 NatalChartAdvancedAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodCompositeAnalysis,
                "Annual Functional Pipeline에는 TenGodCompositeAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                luckIntegratedAnalysis,
                "Annual Functional Pipeline에는 LuckIntegratedAnalysis가 필요합니다."
        );

        long diagnosticId =
                DIAGNOSTIC_SEQUENCE
                        .incrementAndGet();

        long pipelineStartedAt =
                System.nanoTime();

        log.info(
                "[ANNUAL-FUNC-DIAG] id={} pipeline-start segmentCount={}",
                diagnosticId,
                luckIntegratedAnalysis
                        .segmentAnalyses()
                        .size()
        );

        /*
         * =================================================
         * Natal Functional
         * =================================================
         */

        long stageStartedAt =
                System.nanoTime();

        NatalFunctionalAnalysis natalFunctionalAnalysis =
                natalFunctionalAnalysisEngine
                        .analyze(
                                advancedAnalysis,
                                tenGodCompositeAnalysis
                        );

        logStage(
                diagnosticId,
                "natal-functional",
                stageStartedAt
        );

        stageStartedAt =
                System.nanoTime();

        NatalCrossFunctionalAnalysis
                natalCrossFunctionalAnalysis =
                natalCrossFunctionalAnalysisEngine
                        .analyze(
                                natalFunctionalAnalysis
                        );

        logStage(
                diagnosticId,
                "natal-cross-functional",
                stageStartedAt
        );

        /*
         * =================================================
         * Decade Functional
         * =================================================
         */

        stageStartedAt =
                System.nanoTime();

        DecadeFunctionalAnalysis decadeFunctionalAnalysis =
                decadeFunctionalAnalysisEngine
                        .analyze(
                                natalCrossFunctionalAnalysis,
                                luckIntegratedAnalysis
                                        .decadeLuckAnalysis()
                        );

        logStage(
                diagnosticId,
                "decade-functional",
                stageStartedAt
        );

        stageStartedAt =
                System.nanoTime();

        DecadeFunctionalOperationAnalysis
                decadeOperationAnalysis =
                decadeFunctionalOperationEngine
                        .analyze(
                                decadeFunctionalAnalysis
                        );

        logStage(
                diagnosticId,
                "decade-operation",
                stageStartedAt
        );

        stageStartedAt =
                System.nanoTime();

        DecadeFunctionalCapacityAnalysis
                decadeCapacityAnalysis =
                decadeFunctionalCapacityAnalysisEngine
                        .analyze(
                                natalFunctionalAnalysis,
                                decadeOperationAnalysis
                        );

        logStage(
                diagnosticId,
                "decade-capacity",
                stageStartedAt
        );

        DayMasterElementRelationAnalysis
                elementRelationAnalysis =
                advancedAnalysis
                        .dayMasterStrengthAnalysis()
                        .elementRelationAnalysis();

        stageStartedAt =
                System.nanoTime();

        DecadeFunctionalMigrationAnalysis
                decadeMigrationAnalysis =
                decadeFunctionalMigrationAnalysisEngine
                        .analyze(
                                elementRelationAnalysis,
                                decadeCapacityAnalysis
                        );

        logStage(
                diagnosticId,
                "decade-migration",
                stageStartedAt
        );

        stageStartedAt =
                System.nanoTime();

        DecadeResolvedFunctionalAnalysis
                decadeResolvedAnalysis =
                decadeResolvedFunctionalAnalysisEngine
                        .analyze(
                                decadeMigrationAnalysis
                        );

        logStage(
                diagnosticId,
                "decade-resolved",
                stageStartedAt
        );

        stageStartedAt =
                System.nanoTime();

        DecadeCrossFunctionalAnalysis decadeCrossAnalysis =
                decadeCrossFunctionalAnalysisEngine
                        .analyze(
                                decadeResolvedAnalysis
                        );

        logStage(
                diagnosticId,
                "decade-cross",
                stageStartedAt
        );

        stageStartedAt =
                System.nanoTime();

        DecadeFunctionalSynthesisAnalysis
                decadeSynthesisAnalysis =
                decadeFunctionalSynthesisAnalysisEngine
                        .analyze(
                                decadeCrossAnalysis
                        );

        logStage(
                diagnosticId,
                "decade-synthesis",
                stageStartedAt
        );

        /*
         * =================================================
         * Annual
         * =================================================
         */

        IdentityHashMap<
                AnnualLuckAnalysis,
                AnnualFunctionalBaselineAnalysis
                > baselines =
                new IdentityHashMap<>();

        for (
                LuckIntegratedSegmentAnalysis segmentAnalysis
                : luckIntegratedAnalysis.segmentAnalyses()
        ) {

            AnnualLuckAnalysis annualLuckAnalysis =
                    segmentAnalysis
                            .annualLuckAnalysis();

            if (
                    baselines.containsKey(
                            annualLuckAnalysis
                    )
            ) {

                log.info(
                        "[ANNUAL-FUNC-DIAG] id={} annual-identity-reuse targetYear={}",
                        diagnosticId,
                        annualLuckAnalysis.targetYear()
                );

                continue;
            }

            validateAnnualIdentity(
                    luckIntegratedAnalysis,
                    annualLuckAnalysis
            );

            long annualStartedAt =
                    System.nanoTime();

            log.info(
                    "[ANNUAL-FUNC-DIAG] id={} annual-start targetYear={}",
                    diagnosticId,
                    annualLuckAnalysis.targetYear()
            );

            AnnualFunctionalBaselineAnalysis baseline =
                    analyzeAnnual(
                            diagnosticId,
                            natalFunctionalAnalysis,
                            elementRelationAnalysis,
                            decadeSynthesisAnalysis,
                            annualLuckAnalysis
                    );

            log.info(
                    "[ANNUAL-FUNC-DIAG] id={} annual-complete targetYear={} elapsedMs={}",
                    diagnosticId,
                    annualLuckAnalysis.targetYear(),
                    elapsedMillis(
                            annualStartedAt
                    )
            );

            if (
                    baseline
                            .annualLuckAnalysis()
                            != annualLuckAnalysis
            ) {

                throw new IllegalStateException(
                        "Annual Functional Baseline이 입력 AnnualLuckAnalysis Identity를 보존하지 않았습니다. "
                                + "targetYear="
                                + annualLuckAnalysis.targetYear()
                );
            }

            baselines.put(
                    annualLuckAnalysis,
                    baseline
            );
        }

        for (
                LuckIntegratedSegmentAnalysis segmentAnalysis
                : luckIntegratedAnalysis.segmentAnalyses()
        ) {

            AnnualLuckAnalysis annualLuckAnalysis =
                    segmentAnalysis
                            .annualLuckAnalysis();

            if (
                    !baselines.containsKey(
                            annualLuckAnalysis
                    )
            ) {

                throw new IllegalStateException(
                        "16차 Timeline Segment의 Annual Functional Baseline이 누락되었습니다. "
                                + "targetYear="
                                + annualLuckAnalysis.targetYear()
                );
            }
        }

        log.info(
                "[ANNUAL-FUNC-DIAG] id={} pipeline-complete baselineCount={} elapsedMs={}",
                diagnosticId,
                baselines.size(),
                elapsedMillis(
                        pipelineStartedAt
                )
        );

        return Collections.unmodifiableMap(
                baselines
        );
    }

    private AnnualFunctionalBaselineAnalysis
    analyzeAnnual(

            long diagnosticId,

            NatalFunctionalAnalysis natalFunctionalAnalysis,

            DayMasterElementRelationAnalysis elementRelationAnalysis,

            DecadeFunctionalSynthesisAnalysis decadeSynthesisAnalysis,

            AnnualLuckAnalysis annualLuckAnalysis

    ) {

        long stageStartedAt =
                System.nanoTime();

        AnnualFunctionalProjectionAnalysis projectionAnalysis =
                annualFunctionalProjectionEngine
                        .analyze(
                                natalFunctionalAnalysis,
                                annualLuckAnalysis
                        );

        logAnnualStage(
                diagnosticId,
                annualLuckAnalysis,
                "annual-projection",
                stageStartedAt
        );

        stageStartedAt =
                System.nanoTime();

        AnnualFunctionalCapacityAnalysis capacityAnalysis =
                annualFunctionalCapacityAnalysisEngine
                        .analyze(
                                elementRelationAnalysis,
                                projectionAnalysis
                        );

        logAnnualStage(
                diagnosticId,
                annualLuckAnalysis,
                "annual-capacity",
                stageStartedAt
        );

        stageStartedAt =
                System.nanoTime();

        AnnualDecadeFunctionalAnalysis decadeFunctionalAnalysis =
                annualDecadeFunctionalAnalysisEngine
                        .analyze(
                                decadeSynthesisAnalysis,
                                capacityAnalysis
                        );

        logAnnualStage(
                diagnosticId,
                annualLuckAnalysis,
                "annual-decade-functional",
                stageStartedAt
        );

        stageStartedAt =
                System.nanoTime();

        AnnualDecadeTransformationAnalysis transformationAnalysis =
                annualDecadeTransformationResolverEngine
                        .analyze(
                                decadeFunctionalAnalysis
                        );

        logAnnualStage(
                diagnosticId,
                annualLuckAnalysis,
                "annual-decade-transformation",
                stageStartedAt
        );

        stageStartedAt =
                System.nanoTime();

        AnnualResolvedFunctionalAnalysis resolvedFunctionalAnalysis =
                annualResolvedFunctionalAnalysisEngine
                        .analyze(
                                transformationAnalysis
                        );

        logAnnualStage(
                diagnosticId,
                annualLuckAnalysis,
                "annual-resolved-functional",
                stageStartedAt
        );

        stageStartedAt =
                System.nanoTime();

        AnnualResolvedCrossFunctionalAnalysis
                resolvedCrossFunctionalAnalysis =
                annualResolvedCrossFunctionalAnalysisEngine
                        .analyze(
                                resolvedFunctionalAnalysis
                        );

        logAnnualStage(
                diagnosticId,
                annualLuckAnalysis,
                "annual-resolved-cross",
                stageStartedAt
        );

        stageStartedAt =
                System.nanoTime();

        AnnualCrossFunctionalSynthesisAnalysis synthesisAnalysis =
                annualCrossFunctionalSynthesisAnalysisEngine
                        .analyze(
                                resolvedCrossFunctionalAnalysis
                        );

        logAnnualStage(
                diagnosticId,
                annualLuckAnalysis,
                "annual-cross-synthesis",
                stageStartedAt
        );

        stageStartedAt =
                System.nanoTime();

        AnnualFunctionalBaselineAnalysis baseline =
                annualFunctionalBaselineAnalysisEngine
                        .analyze(
                                synthesisAnalysis
                        );

        logAnnualStage(
                diagnosticId,
                annualLuckAnalysis,
                "annual-baseline",
                stageStartedAt
        );

        return baseline;
    }

    private void validateAnnualIdentity(

            LuckIntegratedAnalysis luckIntegratedAnalysis,

            AnnualLuckAnalysis annualLuckAnalysis

    ) {

        Objects.requireNonNull(
                annualLuckAnalysis,
                "16차 Timeline의 AnnualLuckAnalysis는 null일 수 없습니다."
        );

        if (
                annualLuckAnalysis
                        .decadeLuckAnalysis()
                        != luckIntegratedAnalysis
                        .decadeLuckAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "Annual Functional Pipeline의 AnnualLuckAnalysis가 "
                            + "16차와 다른 DecadeLuckAnalysis를 참조합니다. "
                            + "targetYear="
                            + annualLuckAnalysis.targetYear()
            );
        }

        if (
                annualLuckAnalysis
                        .natalAnalysis()
                        != luckIntegratedAnalysis
                        .natalAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "Annual Functional Pipeline의 AnnualLuckAnalysis가 "
                            + "16차와 다른 PalaceAnalysis를 참조합니다. "
                            + "targetYear="
                            + annualLuckAnalysis.targetYear()
            );
        }
    }

    /*
     * =================================================
     * Diagnostic
     * =================================================
     */

    private void logStage(

            long diagnosticId,

            String stage,

            long startedAt

    ) {

        log.info(
                "[ANNUAL-FUNC-DIAG] id={} stage={} elapsedMs={}",
                diagnosticId,
                stage,
                elapsedMillis(
                        startedAt
                )
        );
    }

    private void logAnnualStage(

            long diagnosticId,

            AnnualLuckAnalysis annualLuckAnalysis,

            String stage,

            long startedAt

    ) {

        log.info(
                "[ANNUAL-FUNC-DIAG] id={} targetYear={} stage={} elapsedMs={}",
                diagnosticId,
                annualLuckAnalysis.targetYear(),
                stage,
                elapsedMillis(
                        startedAt
                )
        );
    }

    private long elapsedMillis(
            long startedAt
    ) {

        return (
                System.nanoTime()
                        -
                        startedAt
        )
                /
                1_000_000L;
    }
}