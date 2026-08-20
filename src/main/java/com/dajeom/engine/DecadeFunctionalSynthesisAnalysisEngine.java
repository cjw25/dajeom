package com.dajeom.engine;

import com.dajeom.model.DecadeCrossContextMatch;
import com.dajeom.model.DecadeCrossFunctionalAnalysis;
import com.dajeom.model.DecadeCrossFunctionalEffect;
import com.dajeom.model.DecadeCrossFunctionalSynthesis;
import com.dajeom.model.DecadeFunctionalSynthesisAnalysis;
import com.dajeom.model.DecadeLuckContextSignal;
import com.dajeom.model.DecadePeriodCrossFunctionalAnalysis;
import com.dajeom.model.DecadePeriodFunctionalSynthesisAnalysis;
import com.dajeom.model.DecadePeriodResolvedFunctionalAnalysis;
import com.dajeom.model.DecadeResolvedFunctionalAnalysis;
import com.dajeom.type.DecadeContextTenGodMatchLevel;
import com.dajeom.type.StructureTenGodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 대운 Functional Layer의 최종 Synthesis Engine.
 *
 * =================================================
 *
 * 입력:
 *
 * DecadeCrossFunctionalAnalysis
 *
 * =================================================
 *
 * 내부 Source:
 *
 * DecadeResolvedFunctionalAnalysis
 *
 * +
 *
 * DecadePeriodCrossFunctionalAnalysis
 *
 * +
 *
 * 13-6 DecadeLuckContextSignal
 *
 * =================================================
 *
 * 핵심:
 *
 * Cross Pattern의:
 *
 * sourceSignal.involvedTenGodTypes
 *
 * 와
 *
 * Context Signal의:
 *
 * tenGodTypes
 *
 * 를 구조적으로 비교한다.
 *
 * =================================================
 *
 * 판정:
 *
 * EXACT
 * =
 * Set이 정확히 동일
 *
 * OVERLAP
 * =
 * 일부 십성 공통
 *
 * NONE
 * =
 * 연결하지 않음
 *
 * =================================================
 *
 * 문자열:
 *
 * code
 * summary
 * reasons
 *
 * 는 Match 판정에 사용하지 않는다.
 *
 * =================================================
 *
 * 중요한 제한:
 *
 * EXACT Match를
 * 독립 Evidence 추가로 세지 않는다.
 *
 * SUPPORTED Context를
 * Function 강화로 변환하지 않는다.
 *
 * CONFLICTED Context를
 * Function 약화로 직접 변환하지 않는다.
 *
 * 원국 Cross Pattern의
 * sourceLevel / validationStatus 역시
 * 절대로 변경하지 않는다.
 */
@Component
public class DecadeFunctionalSynthesisAnalysisEngine {

    public DecadeFunctionalSynthesisAnalysis analyze(

            DecadeCrossFunctionalAnalysis
                    crossFunctionalAnalysis

    ) {

        Objects.requireNonNull(
                crossFunctionalAnalysis,
                "DecadeFunctionalSynthesisAnalysis에는 DecadeCrossFunctionalAnalysis가 필요합니다."
        );

        DecadeResolvedFunctionalAnalysis resolvedFunctionalAnalysis =
                crossFunctionalAnalysis
                        .resolvedFunctionalAnalysis();

        boolean completeChart =
                crossFunctionalAnalysis
                        .completeChart();

        if (
                resolvedFunctionalAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "CrossFunctional Analysis와 Resolved Functional Analysis의 completeChart가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * unavailable
         * =================================================
         */

        if (
                !crossFunctionalAnalysis
                        .analysisAvailable()
        ) {

            return new DecadeFunctionalSynthesisAnalysis(
                    resolvedFunctionalAnalysis,
                    crossFunctionalAnalysis,
                    List.of(),
                    completeChart
            );
        }

        List<DecadePeriodFunctionalSynthesisAnalysis>
                periodAnalyses =
                new ArrayList<>();

        for (
                DecadePeriodCrossFunctionalAnalysis crossPeriod
                : crossFunctionalAnalysis.periodAnalyses()
        ) {

            DecadePeriodResolvedFunctionalAnalysis resolvedPeriod =
                    crossPeriod
                            .resolvedFunctionalAnalysis();

            periodAnalyses.add(
                    analyzePeriod(
                            resolvedPeriod,
                            crossPeriod,
                            completeChart
                    )
            );
        }

        return new DecadeFunctionalSynthesisAnalysis(
                resolvedFunctionalAnalysis,
                crossFunctionalAnalysis,
                List.copyOf(
                        periodAnalyses
                ),
                completeChart
        );
    }

    /*
     * =================================================
     * Period
     * =================================================
     */

    private DecadePeriodFunctionalSynthesisAnalysis
    analyzePeriod(

            DecadePeriodResolvedFunctionalAnalysis
                    resolvedPeriod,

            DecadePeriodCrossFunctionalAnalysis
                    crossPeriod,

            boolean completeChart

    ) {

        /*
         * =================================================
         * 13-6의 전체 Context Signal을 사용한다.
         *
         * FunctionEffect.contextSignals()만 사용하지 않는다.
         *
         * 이유:
         *
         * 하나의 Context Signal이
         * Cross Pattern 양쪽 Function을 동시에
         * 포함할 수 있기 때문이다.
         *
         * Function projection 단계에서 중복 연결된
         * Signal을 다시 모으는 대신,
         * 원본 Period ContextAnalysis에서
         * 한 번만 가져온다.
         * =================================================
         */

        List<DecadeLuckContextSignal> contextSignals =
                resolvedPeriod
                        .migrationAnalysis()
                        .capacityAnalysis()
                        .operationAnalysis()
                        .functionalAnalysis()
                        .contextAnalysis()
                        .signals();

        List<DecadeCrossFunctionalSynthesis>
                syntheses =
                new ArrayList<>();

        for (
                DecadeCrossFunctionalEffect crossEffect
                : crossPeriod.crossFunctionalEffects()
        ) {

            syntheses.add(
                    synthesizeCrossEffect(
                            crossEffect,
                            contextSignals,
                            completeChart
                    )
            );
        }

        return new DecadePeriodFunctionalSynthesisAnalysis(
                resolvedPeriod,
                crossPeriod,
                List.copyOf(
                        syntheses
                ),
                completeChart
        );
    }

    /*
     * =================================================
     * Cross Effect
     * =================================================
     */

    private DecadeCrossFunctionalSynthesis
    synthesizeCrossEffect(

            DecadeCrossFunctionalEffect crossEffect,

            List<DecadeLuckContextSignal> contextSignals,

            boolean completeChart

    ) {

        Set<StructureTenGodType> patternTenGodTypes =
                new HashSet<>(
                        crossEffect
                                .sourcePattern()
                                .sourcePattern()
                                .sourceSignal()
                                .involvedTenGodTypes()
                );

        if (
                patternTenGodTypes
                        .isEmpty()
        ) {

            throw new IllegalStateException(
                    "Cross Pattern의 involvedTenGodTypes가 비어 있습니다: "
                            + crossEffect.patternType()
            );
        }

        List<DecadeCrossContextMatch> matches =
                new ArrayList<>();

        for (
                DecadeLuckContextSignal signal
                : contextSignals
        ) {

            /*
             * 특정 십성 구조가 없는
             * Global Context는
             * Cross Pattern에 억지로 연결하지 않는다.
             */
            if (
                    signal.tenGodTypes()
                            .isEmpty()
            ) {

                continue;
            }

            DecadeContextTenGodMatchLevel matchLevel =
                    DecadeCrossContextMatch
                            .determineMatchLevel(
                                    Set.copyOf(
                                            patternTenGodTypes
                                    ),
                                    signal.tenGodTypes()
                            );

            if (
                    !matchLevel.matches()
            ) {

                continue;
            }

            matches.add(
                    new DecadeCrossContextMatch(
                            crossEffect,
                            signal,
                            matchLevel,
                            completeChart
                    )
            );
        }

        return new DecadeCrossFunctionalSynthesis(
                crossEffect,
                List.copyOf(
                        matches
                ),
                completeChart
        );
    }
}