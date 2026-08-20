package com.dajeom.engine;

import com.dajeom.model.AnnualCrossFunctionalPatternEffect;
import com.dajeom.model.AnnualResolvedCrossFunctionalAnalysis;
import com.dajeom.model.AnnualResolvedCrossFunctionalSegmentAnalysis;
import com.dajeom.model.AnnualResolvedFunctionEffect;
import com.dajeom.model.AnnualResolvedFunctionalAnalysis;
import com.dajeom.model.AnnualResolvedFunctionalSegmentAnalysis;
import com.dajeom.model.DecadeCrossFunctionalSynthesis;
import com.dajeom.type.AnnualCrossFunctionalContactLevel;
import com.dajeom.type.AnnualCrossFunctionalResolutionType;
import com.dajeom.type.NatalCrossFunctionalScope;
import com.dajeom.type.NatalTenGodFunctionType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 세운 Segment별 CrossFunctional Pattern Resolution Engine.
 *
 * =================================================
 *
 * 입력:
 *
 * AnnualResolvedFunctionalAnalysis
 *
 * =================================================
 *
 * 대운 기준:
 *
 * 각 Segment의
 * DecadePeriodFunctionalSynthesisAnalysis
 *
 * =================================================
 *
 * 판정:
 *
 * - 대운 기존 Full Contact의 continuation
 * - 대운 Partial + 세운 반대 Participant
 * - 세운 자체 양쪽 Participant
 * - 14-5 Transformation Target에 의한 Completion
 * - FUNCTION_TO_CORE_SELF
 * - WITHIN_FUNCTION
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 1.
 * 원국 Pattern을 새로 만들지 않는다.
 *
 * 2.
 * 원국 Pattern Level을 변경하지 않는다.
 *
 * 3.
 * REFERENCE를 승격하지 않는다.
 *
 * 4.
 * 대운에서 이미 Full Contact인 Pattern에
 *    세운이 같은 Function을 넣어도
 *    새로운 Completion으로 세지 않는다.
 *
 * 5.
 * Transformation Target은
 *    Function Activation Provenance이며
 *    별도 독립 Pattern Evidence가 아니다.
 *
 * 6.
 * 아직 14-6 Annual Context Match로
 *    Pattern을 지지/제한하지 않는다.
 *
 *    그 작업은 다음 Context Qualification Layer 책임이다.
 */
@Component
public class AnnualResolvedCrossFunctionalAnalysisEngine {

    public AnnualResolvedCrossFunctionalAnalysis analyze(

            AnnualResolvedFunctionalAnalysis
                    resolvedFunctionalAnalysis

    ) {

        Objects.requireNonNull(
                resolvedFunctionalAnalysis,
                "Annual CrossFunctional Analysis에는 AnnualResolvedFunctionalAnalysis가 필요합니다."
        );

        boolean completeChart =
                resolvedFunctionalAnalysis
                        .completeChart();

        if (
                !resolvedFunctionalAnalysis
                        .analysisAvailable()
        ) {

            return new AnnualResolvedCrossFunctionalAnalysis(
                    resolvedFunctionalAnalysis,
                    List.of(),
                    completeChart
            );
        }

        List<AnnualResolvedCrossFunctionalSegmentAnalysis>
                segmentAnalyses =
                new ArrayList<>();

        for (
                AnnualResolvedFunctionalSegmentAnalysis resolvedSegment
                : resolvedFunctionalAnalysis.segmentAnalyses()
        ) {

            segmentAnalyses.add(
                    resolveSegment(
                            resolvedSegment,
                            completeChart
                    )
            );
        }

        return new AnnualResolvedCrossFunctionalAnalysis(
                resolvedFunctionalAnalysis,
                List.copyOf(
                        segmentAnalyses
                ),
                completeChart
        );
    }

    /*
     * =================================================
     * Segment
     * =================================================
     */

    private AnnualResolvedCrossFunctionalSegmentAnalysis
    resolveSegment(

            AnnualResolvedFunctionalSegmentAnalysis
                    resolvedSegment,

            boolean completeChart

    ) {

        List<AnnualCrossFunctionalPatternEffect>
                patternEffects =
                new ArrayList<>();

        for (
                DecadeCrossFunctionalSynthesis decadeSynthesis
                : resolvedSegment
                .sourceSegment()
                .decadeBaseline()
                .crossSyntheses()
        ) {

            patternEffects.add(
                    resolvePattern(
                            resolvedSegment,
                            decadeSynthesis,
                            completeChart
                    )
            );
        }

        return new AnnualResolvedCrossFunctionalSegmentAnalysis(
                resolvedSegment,
                List.copyOf(
                        patternEffects
                ),
                completeChart
        );
    }

    /*
     * =================================================
     * Pattern
     * =================================================
     */

    private AnnualCrossFunctionalPatternEffect
    resolvePattern(

            AnnualResolvedFunctionalSegmentAnalysis
                    resolvedSegment,

            DecadeCrossFunctionalSynthesis
                    decadeSynthesis,

            boolean completeChart

    ) {

        NatalCrossFunctionalScope scope =
                decadeSynthesis
                        .sourceEffect()
                        .scope();

        NatalTenGodFunctionType primaryFunction =
                decadeSynthesis
                        .patternType()
                        .primaryFunction();

        AnnualResolvedFunctionEffect primaryEffect =
                resolvedSegment
                        .requireEffect(
                                primaryFunction
                        );

        List<AnnualResolvedFunctionEffect> participants =
                new ArrayList<>();

        participants.add(
                primaryEffect
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

            NatalTenGodFunctionType secondaryFunction =
                    decadeSynthesis
                            .patternType()
                            .secondaryFunction()
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "BETWEEN_FUNCTIONS Pattern에 Secondary Function 정의가 없습니다: "
                                                            + decadeSynthesis.patternType()
                                            )
                            );

            AnnualResolvedFunctionEffect secondaryEffect =
                    resolvedSegment
                            .requireEffect(
                                    secondaryFunction
                            );

            participants.add(
                    secondaryEffect
            );
        }

        List<AnnualResolvedFunctionEffect>
                participantList =
                List.copyOf(
                        participants
                );

        AnnualCrossFunctionalContactLevel contactLevel =
                AnnualCrossFunctionalPatternEffect
                        .resolveContactLevel(
                                scope,
                                participantList
                        );

        AnnualCrossFunctionalResolutionType resolutionType =
                AnnualCrossFunctionalPatternEffect
                        .resolveResolutionType(
                                decadeSynthesis,
                                participantList,
                                contactLevel
                        );

        return new AnnualCrossFunctionalPatternEffect(
                decadeSynthesis,
                resolvedSegment,
                participantList,
                contactLevel,
                resolutionType,
                completeChart
        );
    }
}