package com.dajeom.engine;

import com.dajeom.model.DecadeCrossFunctionalAnalysis;
import com.dajeom.model.DecadeCrossFunctionalEffect;
import com.dajeom.model.DecadeCrossFunctionalParticipantEffect;
import com.dajeom.model.DecadePeriodCrossFunctionalAnalysis;
import com.dajeom.model.DecadePeriodResolvedFunctionalAnalysis;
import com.dajeom.model.DecadeResolvedFunctionEffect;
import com.dajeom.model.DecadeResolvedFunctionalAnalysis;
import com.dajeom.model.NatalCrossFunctionalAnalysis;
import com.dajeom.model.NatalCrossFunctionalPattern;
import com.dajeom.type.DecadeCrossFunctionalContactLevel;
import com.dajeom.type.NatalCrossFunctionalScope;
import com.dajeom.type.NatalTenGodFunctionType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 원국 CrossFunctional Pattern과
 * 대운 Resolved Function Effect를 연결한다.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 원국 Pattern을 대운에서
 * 새로 계산하지 않는다.
 *
 * 기존:
 *
 * NatalCrossFunctionalPattern
 *
 * 의:
 *
 * - sourceLevel
 * - validationStatus
 * - structurallyEffective
 * - strongSourcePattern
 * - supported
 * - qualified
 * - weakened
 * - conflicted
 *
 * 를 그대로 사용한다.
 *
 * =================================================
 *
 * 대운에서는 오직:
 *
 * - Primary Function 활성
 * - Secondary Function 활성
 * - Transformation Target 활성
 * - 원기능 감소 Context
 *
 * 를 연결해서
 * Contact Level을 결정한다.
 *
 * =================================================
 *
 * BETWEEN_FUNCTIONS
 *
 * Primary만 활성
 * ->
 * PRIMARY_ONLY
 *
 * Secondary만 활성
 * ->
 * SECONDARY_ONLY
 *
 * 둘 다 활성
 * ->
 * BOTH_FUNCTIONS
 *
 * =================================================
 *
 * FUNCTION_TO_CORE_SELF
 *
 * Primary Function 활성
 * +
 * 고정된 Natal CoreSelf
 *
 * ->
 * FUNCTION_TO_CORE_SELF
 *
 * CoreSelf를 대운마다 재계산하지 않는다.
 *
 * =================================================
 *
 * WITHIN_FUNCTION
 *
 * Primary Function 활성
 *
 * ->
 * WITHIN_FUNCTION
 *
 * =================================================
 *
 * 절대로:
 *
 * 양쪽 활성 = STRONG Pattern
 *
 * 으로 승격하지 않는다.
 */
@Component
public class DecadeCrossFunctionalAnalysisEngine {

    public DecadeCrossFunctionalAnalysis analyze(

            DecadeResolvedFunctionalAnalysis
                    resolvedFunctionalAnalysis

    ) {

        Objects.requireNonNull(
                resolvedFunctionalAnalysis,
                "DecadeCrossFunctionalAnalysis에는 "
                        + "DecadeResolvedFunctionalAnalysis가 필요합니다."
        );

        NatalCrossFunctionalAnalysis natalCrossFunctionalAnalysis =
                resolvedFunctionalAnalysis
                        .migrationAnalysis()
                        .capacityAnalysis()
                        .operationAnalysis()
                        .functionalAnalysis()
                        .natalCrossFunctionalAnalysis();

        boolean completeChart =
                resolvedFunctionalAnalysis
                        .completeChart();

        if (
                natalCrossFunctionalAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Natal CrossFunctional Analysis와 "
                            + "Resolved Decade Analysis의 completeChart가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * unavailable
         * =================================================
         */

        if (
                !resolvedFunctionalAnalysis
                        .analysisAvailable()
        ) {

            return new DecadeCrossFunctionalAnalysis(
                    natalCrossFunctionalAnalysis,
                    resolvedFunctionalAnalysis,
                    List.of(),
                    completeChart
            );
        }

        List<DecadePeriodCrossFunctionalAnalysis>
                periodAnalyses =
                new ArrayList<>();

        for (
                DecadePeriodResolvedFunctionalAnalysis period
                : resolvedFunctionalAnalysis.periodAnalyses()
        ) {

            periodAnalyses.add(
                    analyzePeriod(
                            natalCrossFunctionalAnalysis,
                            period,
                            completeChart
                    )
            );
        }

        return new DecadeCrossFunctionalAnalysis(
                natalCrossFunctionalAnalysis,
                resolvedFunctionalAnalysis,
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

    private DecadePeriodCrossFunctionalAnalysis
    analyzePeriod(

            NatalCrossFunctionalAnalysis
                    natalCrossFunctionalAnalysis,

            DecadePeriodResolvedFunctionalAnalysis
                    resolvedPeriod,

            boolean completeChart

    ) {

        List<DecadeCrossFunctionalEffect> effects =
                new ArrayList<>();

        /*
         * REFERENCE 포함 모든 원국 Pattern을 보존한다.
         */
        for (
                NatalCrossFunctionalPattern natalPattern
                : natalCrossFunctionalAnalysis.patterns()
        ) {

            effects.add(
                    analyzePattern(
                            natalPattern,
                            resolvedPeriod,
                            completeChart
                    )
            );
        }

        return new DecadePeriodCrossFunctionalAnalysis(
                natalCrossFunctionalAnalysis,
                resolvedPeriod,
                List.copyOf(
                        effects
                ),
                completeChart
        );
    }

    /*
     * =================================================
     * Pattern
     * =================================================
     */

    private DecadeCrossFunctionalEffect analyzePattern(

            NatalCrossFunctionalPattern natalPattern,

            DecadePeriodResolvedFunctionalAnalysis
                    resolvedPeriod,

            boolean completeChart

    ) {

        NatalCrossFunctionalScope scope =
                natalPattern
                        .patternType()
                        .scope();

        NatalTenGodFunctionType primaryFunctionType =
                natalPattern
                        .patternType()
                        .primaryFunction();

        DecadeResolvedFunctionEffect primaryResolvedEffect =
                resolvedPeriod
                        .requireEffect(
                                primaryFunctionType
                        );

        DecadeCrossFunctionalParticipantEffect primaryParticipant =
                new DecadeCrossFunctionalParticipantEffect(
                        natalPattern,
                        primaryFunctionType,
                        true,
                        primaryResolvedEffect,
                        completeChart
                );

        List<DecadeCrossFunctionalParticipantEffect>
                participants =
                new ArrayList<>();

        participants.add(
                primaryParticipant
        );

        /*
         * =================================================
         * BETWEEN_FUNCTIONS
         * =================================================
         */

        if (
                scope
                        == NatalCrossFunctionalScope
                        .BETWEEN_FUNCTIONS
        ) {

            NatalTenGodFunctionType secondaryFunctionType =
                    natalPattern
                            .patternType()
                            .secondaryFunction()
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "BETWEEN_FUNCTIONS Pattern에 "
                                                            + "Secondary Function 정의가 없습니다: "
                                                            + natalPattern.patternType()
                                            )
                            );

            DecadeResolvedFunctionEffect secondaryResolvedEffect =
                    resolvedPeriod
                            .requireEffect(
                                    secondaryFunctionType
                            );

            participants.add(
                    new DecadeCrossFunctionalParticipantEffect(
                            natalPattern,
                            secondaryFunctionType,
                            false,
                            secondaryResolvedEffect,
                            completeChart
                    )
            );
        }

        DecadeCrossFunctionalContactLevel contactLevel =
                resolveContactLevel(
                        scope,
                        participants
                );

        return new DecadeCrossFunctionalEffect(
                natalPattern,
                List.copyOf(
                        participants
                ),
                contactLevel,
                completeChart
        );
    }

    /*
     * =================================================
     * Contact Level
     * =================================================
     */

    private DecadeCrossFunctionalContactLevel
    resolveContactLevel(

            NatalCrossFunctionalScope scope,

            List<DecadeCrossFunctionalParticipantEffect>
                    participants

    ) {

        DecadeCrossFunctionalParticipantEffect primary =
                participants
                        .stream()
                        .filter(
                                DecadeCrossFunctionalParticipantEffect
                                        ::primary
                        )
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "Primary CrossFunctional Participant가 없습니다."
                                        )
                        );

        /*
         * =================================================
         * Function ↔ CoreSelf
         * =================================================
         *
         * CoreSelf는 원국에서 고정된 Anchor.
         *
         * Primary Function이 실제 대운 활성되면
         * 기존 원국 Function-to-CoreSelf Pattern과
         * temporal contact가 발생한 것으로 본다.
         */

        if (
                scope
                        == NatalCrossFunctionalScope
                        .FUNCTION_TO_CORE_SELF
        ) {

            return primary.active()
                    ? DecadeCrossFunctionalContactLevel
                    .FUNCTION_TO_CORE_SELF
                    : DecadeCrossFunctionalContactLevel
                    .NO_CONTACT;
        }

        /*
         * =================================================
         * Within Function
         * =================================================
         */

        if (
                scope
                        == NatalCrossFunctionalScope
                        .WITHIN_FUNCTION
        ) {

            return primary.active()
                    ? DecadeCrossFunctionalContactLevel
                    .WITHIN_FUNCTION
                    : DecadeCrossFunctionalContactLevel
                    .NO_CONTACT;
        }

        /*
         * =================================================
         * Between Functions
         * =================================================
         */

        DecadeCrossFunctionalParticipantEffect secondary =
                participants
                        .stream()
                        .filter(
                                DecadeCrossFunctionalParticipantEffect
                                        ::secondary
                        )
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "BETWEEN_FUNCTIONS Pattern의 "
                                                        + "Secondary Participant가 없습니다."
                                        )
                        );

        boolean primaryActive =
                primary.active();

        boolean secondaryActive =
                secondary.active();

        if (
                primaryActive
                        && secondaryActive
        ) {

            return DecadeCrossFunctionalContactLevel
                    .BOTH_FUNCTIONS;
        }

        if (
                primaryActive
        ) {

            return DecadeCrossFunctionalContactLevel
                    .PRIMARY_ONLY;
        }

        if (
                secondaryActive
        ) {

            return DecadeCrossFunctionalContactLevel
                    .SECONDARY_ONLY;
        }

        return DecadeCrossFunctionalContactLevel
                .NO_CONTACT;
    }
}