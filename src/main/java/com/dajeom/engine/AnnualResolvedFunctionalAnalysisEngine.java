package com.dajeom.engine;

import com.dajeom.model.AnnualDecadeFunctionSegmentEffect;
import com.dajeom.model.AnnualDecadeTransformationAnalysis;
import com.dajeom.model.AnnualDecadeTransformationParticipantResolution;
import com.dajeom.model.AnnualDecadeTransformationResolution;
import com.dajeom.model.AnnualResolvedFunctionEffect;
import com.dajeom.model.AnnualResolvedFunctionalAnalysis;
import com.dajeom.model.AnnualResolvedFunctionalSegmentAnalysis;
import com.dajeom.type.AnnualResolvedFunctionActivationMode;
import com.dajeom.type.AnnualSegmentOriginalInputState;
import com.dajeom.type.NatalTenGodFunctionType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 세운 Functional Layer의
 * Segment별 최종 Function Resolution Engine.
 *
 * =================================================
 *
 * 입력:
 *
 * AnnualDecadeTransformationAnalysis
 *
 * =================================================
 *
 * 각 Function마다:
 *
 * 1.
 * 대운 기준선 활성
 *
 * 2.
 * 세운 자체 활성
 *
 * 3.
 * 14-5 Cross-Luck Transformation Target
 *
 * 4.
 * 대운 원기능 상태
 *
 * 5.
 * 세운 원기능 상태
 *
 * 6.
 * Natal Fixed Capacity
 *
 * 7.
 * 교운 Segment
 *
 * 를 하나의 AnnualResolvedFunctionEffect로
 * 수렴시킨다.
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * - 신강신약 재계산
 * - 대운/세운 평균
 * - 활성 Source 점수화
 * - 용신 재선정
 * - 길흉
 * - 사건 생성
 */
@Component
public class AnnualResolvedFunctionalAnalysisEngine {

    public AnnualResolvedFunctionalAnalysis analyze(

            AnnualDecadeTransformationAnalysis
                    transformationAnalysis

    ) {

        Objects.requireNonNull(
                transformationAnalysis,
                "Annual Resolved Functional Analysis에는 AnnualDecadeTransformationAnalysis가 필요합니다."
        );

        boolean completeChart =
                transformationAnalysis
                        .completeChart();

        /*
         * =================================================
         * unavailable
         * =================================================
         */

        if (
                !transformationAnalysis
                        .analysisAvailable()
        ) {

            return new AnnualResolvedFunctionalAnalysis(
                    transformationAnalysis,
                    List.of(),
                    completeChart
            );
        }

        List<AnnualResolvedFunctionalSegmentAnalysis>
                segmentAnalyses =
                new ArrayList<>();

        for (
                AnnualDecadeTransformationResolution transformationResolution
                : transformationAnalysis.segmentResolutions()
        ) {

            segmentAnalyses.add(
                    resolveSegment(
                            transformationResolution,
                            completeChart
                    )
            );
        }

        return new AnnualResolvedFunctionalAnalysis(
                transformationAnalysis,
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

    private AnnualResolvedFunctionalSegmentAnalysis
    resolveSegment(

            AnnualDecadeTransformationResolution
                    transformationResolution,

            boolean completeChart

    ) {

        List<AnnualResolvedFunctionEffect> effects =
                new ArrayList<>();

        for (
                NatalTenGodFunctionType functionType
                : NatalTenGodFunctionType.values()
        ) {

            AnnualDecadeFunctionSegmentEffect segmentFunctionEffect =
                    transformationResolution
                            .sourceSegment()
                            .requireFunctionEffect(
                                    functionType
                            );

            List<AnnualDecadeTransformationParticipantResolution>
                    incomingCrossLuckTargets =
                    transformationResolution
                            .activeResolutionsTargeting(
                                    functionType
                            );

            AnnualResolvedFunctionActivationMode activationMode =
                    AnnualResolvedFunctionActivationMode.of(
                            segmentFunctionEffect
                                    .decadeActive(),
                            segmentFunctionEffect
                                    .annualActive(),
                            !incomingCrossLuckTargets
                                    .isEmpty()
                    );

            AnnualSegmentOriginalInputState decadeOriginalInputState =
                    AnnualResolvedFunctionEffect
                            .resolveDecadeOriginalInputState(
                                    segmentFunctionEffect,
                                    transformationResolution
                            );

            AnnualSegmentOriginalInputState annualOriginalInputState =
                    AnnualResolvedFunctionEffect
                            .resolveAnnualOriginalInputState(
                                    segmentFunctionEffect,
                                    transformationResolution
                            );

            effects.add(
                    new AnnualResolvedFunctionEffect(
                            transformationResolution
                                    .sourceSegment(),
                            transformationResolution,
                            segmentFunctionEffect,
                            incomingCrossLuckTargets,
                            activationMode,
                            decadeOriginalInputState,
                            annualOriginalInputState,
                            completeChart
                    )
            );
        }

        return new AnnualResolvedFunctionalSegmentAnalysis(
                transformationResolution,
                List.copyOf(
                        effects
                ),
                completeChart
        );
    }
}