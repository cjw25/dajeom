package com.dajeom.model;

import com.dajeom.type.AnnualFunctionalBaselineStatus;
import com.dajeom.type.NatalCrossFunctionalPatternType;
import com.dajeom.type.NatalTenGodFunctionType;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 세운 Functional Layer의 공식 최종 기준선.
 *
 * =================================================
 *
 * 전체 체인:
 *
 * AnnualLuckAnalysis
 *
 * ->
 *
 * AnnualFunctionalProjectionAnalysis
 *
 * ->
 *
 * AnnualFunctionalCapacityAnalysis
 *
 * ->
 *
 * AnnualDecadeFunctionalAnalysis
 *
 * ->
 *
 * AnnualDecadeTransformationAnalysis
 *
 * ->
 *
 * AnnualResolvedFunctionalAnalysis
 *
 * ->
 *
 * AnnualResolvedCrossFunctionalAnalysis
 *
 * ->
 *
 * AnnualCrossFunctionalSynthesisAnalysis
 *
 * ->
 *
 * AnnualFunctionalBaselineAnalysis
 *
 * =================================================
 *
 * 이 Aggregate가 이후:
 *
 * CURRENT_YEAR_LUCK
 * FUTURE_LUCK
 * PAST_LUCK
 *
 * 및 각 LifeDomain의 세운 해석이 사용할
 * 공식 Functional 기준선이다.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 1.
 * Annual 자체 Functional 결과를 보존한다.
 *
 * 2.
 * 대운 Segment가 unavailable이어도
 *    Annual Functional 결과를 삭제하지 않는다.
 *
 * 3.
 * 교운 시 여러 대운 Segment를
 *    하나로 평균내지 않는다.
 *
 * 4.
 * CrossFunctional Pattern의 원국 수준을
 *    변경하지 않는다.
 *
 * 5.
 * Context Exact Match를
 *    독립 Evidence로 추가하지 않는다.
 *
 * 6.
 * 신강신약을 재계산하지 않는다.
 *
 * 7.
 * 용신을 재선정하지 않는다.
 *
 * 8.
 * 길흉 / 사건 / LifeDomain 판정을
 *    이 Aggregate 자체에서 만들지 않는다.
 */
public record AnnualFunctionalBaselineAnalysis(

        AnnualCrossFunctionalSynthesisAnalysis
        crossFunctionalSynthesisAnalysis,

        AnnualFunctionalBaselineStatus
        baselineStatus,

        boolean completeChart

) {

    public AnnualFunctionalBaselineAnalysis {

        Objects.requireNonNull(
                crossFunctionalSynthesisAnalysis,
                "Annual Functional Baseline에는 CrossFunctional Synthesis Analysis가 필요합니다."
        );

        Objects.requireNonNull(
                baselineStatus,
                "Annual Functional Baseline에는 상태가 필요합니다."
        );

        if (
                crossFunctionalSynthesisAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Annual CrossFunctional Synthesis와 Functional Baseline의 completeChart가 일치하지 않습니다."
            );
        }

        AnnualFunctionalBaselineStatus expected =
                AnnualFunctionalBaselineStatus.of(
                        crossFunctionalSynthesisAnalysis
                                .analysisAvailable(),
                        crossFunctionalSynthesisAnalysis
                                .segmentAnalyses()
                                .size()
                );

        if (
                baselineStatus
                        != expected
        ) {

            throw new IllegalArgumentException(
                    "Annual Functional Baseline 상태가 실제 Segment 구조와 일치하지 않습니다. "
                            + "expected="
                            + expected
                            + ", actual="
                            + baselineStatus
            );
        }

        /*
         * =================================================
         * Annual Functional 자체 Identity
         * =================================================
         */

        AnnualFunctionalCapacityAnalysis capacityAnalysis =
                annualCapacityAnalysisOf(
                        crossFunctionalSynthesisAnalysis
                );

        if (
                capacityAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Annual Functional Capacity와 최종 Baseline의 completeChart가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Segment Identity
         * =================================================
         */

        for (
                AnnualCrossFunctionalSynthesisSegmentAnalysis segment
                : crossFunctionalSynthesisAnalysis
                .segmentAnalyses()
        ) {

            Objects.requireNonNull(
                    segment,
                    "Annual Functional Baseline Segment는 null일 수 없습니다."
            );

            if (
                    segment.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Annual Functional Baseline Segment의 completeChart가 일치하지 않습니다."
                );
            }

            if (
                    segment
                            .sourceSegment()
                            .annualCapacityAnalysis()
                            != capacityAnalysis
            ) {

                throw new IllegalArgumentException(
                        "Annual Segment가 공식 Annual Functional Capacity Identity를 보존하지 않았습니다. "
                                + "sequence="
                                + segment.decadeSequenceNumber()
                );
            }
        }
    }

    /*
     * =================================================
     * Upstream
     * =================================================
     */

    public AnnualResolvedCrossFunctionalAnalysis
    resolvedCrossFunctionalAnalysis() {

        return crossFunctionalSynthesisAnalysis
                .sourceAnalysis();
    }

    public AnnualResolvedFunctionalAnalysis
    resolvedFunctionalAnalysis() {

        return resolvedCrossFunctionalAnalysis()
                .resolvedFunctionalAnalysis();
    }

    public AnnualDecadeTransformationAnalysis
    decadeTransformationAnalysis() {

        return resolvedFunctionalAnalysis()
                .transformationAnalysis();
    }

    public AnnualDecadeFunctionalAnalysis
    decadeFunctionalAnalysis() {

        return decadeTransformationAnalysis()
                .functionalAnalysis();
    }

    public AnnualFunctionalCapacityAnalysis
    annualCapacityAnalysis() {

        return decadeFunctionalAnalysis()
                .annualFunctionalCapacityAnalysis();
    }

    public AnnualFunctionalProjectionAnalysis
    annualProjectionAnalysis() {

        return annualCapacityAnalysis()
                .projectionAnalysis();
    }

    public AnnualLuckAnalysis
    annualLuckAnalysis() {

        return annualProjectionAnalysis()
                .annualLuckAnalysis();
    }

    /*
     * =================================================
     * Identity
     * =================================================
     */

    public int targetYear() {

        return annualCapacityAnalysis()
                .targetYear();
    }

    public AnnualLuckPeriod period() {

        return annualCapacityAnalysis()
                .period();
    }

    /*
     * =================================================
     * Availability
     * =================================================
     */

    /**
     * 세운 자체 Functional 분석은
     * 이 Aggregate가 존재하는 이상
     * 사용할 수 있다.
     */
    public boolean annualFunctionalAvailable() {

        return true;
    }

    /**
     * 대운과 결합한 Segment Functional 분석을
     * 사용할 수 있는가.
     */
    public boolean decadeSegmentAvailable() {

        return baselineStatus
                .decadeSegmentAvailable();
    }

    public Optional<String>
    decadeSegmentUnavailableReason() {

        if (
                decadeSegmentAvailable()
        ) {

            return Optional.empty();
        }

        return crossFunctionalSynthesisAnalysis
                .unavailableReason();
    }

    public boolean annualOnly() {

        return baselineStatus
                .annualOnly();
    }

    public boolean hasDecadeSegment() {

        return baselineStatus
                .hasDecadeSegment();
    }

    public boolean multipleDecadeSegments() {

        return baselineStatus
                .multipleDecadeSegments();
    }

    /*
     * =================================================
     * 대운 범위 밖 Annual
     * =================================================
     */

    public boolean beforeFirstDecadeLuck() {

        return decadeFunctionalAnalysis()
                .beforeFirstDecadeLuck();
    }

    public boolean afterGeneratedDecadeRange() {

        return decadeFunctionalAnalysis()
                .afterGeneratedDecadeRange();
    }

    /*
     * =================================================
     * Annual 자체 5 Function
     * =================================================
     */

    public List<AnnualFunctionCapacityContext>
    annualFunctionContexts() {

        return annualCapacityAnalysis()
                .capacityContexts();
    }

    public Optional<AnnualFunctionCapacityContext>
    annualFunctionContextOf(
            NatalTenGodFunctionType functionType
    ) {

        if (
                functionType == null
        ) {

            return Optional.empty();
        }

        return annualCapacityAnalysis()
                .contextOf(
                        functionType
                );
    }

    public AnnualFunctionCapacityContext
    requireAnnualFunctionContext(
            NatalTenGodFunctionType functionType
    ) {

        Objects.requireNonNull(
                functionType,
                "Annual Functional Baseline Function 조회에는 Function Type이 필요합니다."
        );

        return annualCapacityAnalysis()
                .requireContext(
                        functionType
                );
    }

    public List<AnnualFunctionCapacityContext>
    annualActiveFunctionContexts() {

        return annualCapacityAnalysis()
                .activeContexts();
    }

    public List<AnnualFunctionCapacityContext>
    annualTransformationTargetContexts() {

        return annualCapacityAnalysis()
                .transformedTargetContexts();
    }

    public List<AnnualFunctionCapacityContext>
    annualCapacityReviewContexts() {

        return annualCapacityAnalysis()
                .contextsRequiringCapacityReview();
    }

    /*
     * =================================================
     * Segment
     * =================================================
     */

    public List<AnnualCrossFunctionalSynthesisSegmentAnalysis>
    segments() {

        return crossFunctionalSynthesisAnalysis
                .segmentAnalyses();
    }

    public Optional<AnnualCrossFunctionalSynthesisSegmentAnalysis>
    segmentOfDecade(
            int sequenceNumber
    ) {

        return crossFunctionalSynthesisAnalysis
                .segmentOfDecade(
                        sequenceNumber
                );
    }

    public AnnualCrossFunctionalSynthesisSegmentAnalysis
    requireSegmentOfDecade(
            int sequenceNumber
    ) {

        return crossFunctionalSynthesisAnalysis
                .requireSegmentOfDecade(
                        sequenceNumber
                );
    }

    public List<AnnualCrossFunctionalSynthesisSegmentAnalysis>
    possibleSegmentsAt(
            ZonedDateTime dateTime
    ) {

        return crossFunctionalSynthesisAnalysis
                .possibleSegmentsAt(
                        dateTime
                );
    }

    public Optional<AnnualCrossFunctionalSynthesisSegmentAnalysis>
    definiteSegmentAt(
            ZonedDateTime dateTime
    ) {

        return crossFunctionalSynthesisAnalysis
                .definiteSegmentAt(
                        dateTime
                );
    }

    public boolean timingUncertain() {

        return segments()
                .stream()
                .anyMatch(
                        AnnualCrossFunctionalSynthesisSegmentAnalysis
                                ::timingUncertain
                );
    }

    /*
     * =================================================
     * Segment별 Resolved Function
     * =================================================
     */

    public List<AnnualResolvedFunctionEffect>
    resolvedEffectsOf(
            NatalTenGodFunctionType functionType
    ) {

        if (
                functionType == null
                        || !hasDecadeSegment()
        ) {

            return List.of();
        }

        return segments()
                .stream()
                .map(
                        segment ->
                                segment
                                        .sourceSegmentAnalysis()
                                        .resolvedFunctionalSegment()
                                        .requireEffect(
                                                functionType
                                        )
                )
                .toList();
    }

    public List<AnnualCrossFunctionalSynthesisSegmentAnalysis>
    segmentsWithActiveFunction(
            NatalTenGodFunctionType functionType
    ) {

        if (
                functionType == null
        ) {

            return List.of();
        }

        return segments()
                .stream()
                .filter(
                        segment ->
                                segment
                                        .sourceSegmentAnalysis()
                                        .resolvedFunctionalSegment()
                                        .requireEffect(
                                                functionType
                                        )
                                        .active()
                )
                .toList();
    }

    /*
     * =================================================
     * Cross Pattern
     * =================================================
     */

    public List<AnnualCrossFunctionalSynthesis>
    synthesesOf(
            NatalCrossFunctionalPatternType patternType
    ) {

        if (
                patternType == null
        ) {

            return List.of();
        }

        return crossFunctionalSynthesisAnalysis
                .synthesesOf(
                        patternType
                );
    }

    public List<AnnualCrossFunctionalSynthesisSegmentAnalysis>
    segmentsWithEffectiveFullPatternContact(
            NatalCrossFunctionalPatternType patternType
    ) {

        if (
                patternType == null
        ) {

            return List.of();
        }

        return crossFunctionalSynthesisAnalysis
                .segmentsWithEffectiveFullContact(
                        patternType
                );
    }

    public List<AnnualCrossFunctionalSynthesisSegmentAnalysis>
    segmentsWithEffectiveNewPatternCompletion(
            NatalCrossFunctionalPatternType patternType
    ) {

        if (
                patternType == null
        ) {

            return List.of();
        }

        return crossFunctionalSynthesisAnalysis
                .segmentsWithEffectiveNewCompletion(
                        patternType
                );
    }

    public List<AnnualCrossFunctionalSynthesisSegmentAnalysis>
    segmentsWithExactPatternContext(
            NatalCrossFunctionalPatternType patternType
    ) {

        if (
                patternType == null
        ) {

            return List.of();
        }

        return crossFunctionalSynthesisAnalysis
                .segmentsWithExactContext(
                        patternType
                );
    }

    /*
     * =================================================
     * Qualification
     * =================================================
     */

    public List<AnnualCrossFunctionalSynthesisSegmentAnalysis>
    qualifiedSegments() {

        return crossFunctionalSynthesisAnalysis
                .qualifiedSegments();
    }

    public boolean qualificationRequired() {

        return timingUncertain()

                || !annualCapacityReviewContexts()
                .isEmpty()

                || !qualifiedSegments()
                .isEmpty();
    }

    /*
     * =================================================
     * Internal
     * =================================================
     */

    private static AnnualFunctionalCapacityAnalysis
    annualCapacityAnalysisOf(

            AnnualCrossFunctionalSynthesisAnalysis source

    ) {

        return source
                .sourceAnalysis()
                .resolvedFunctionalAnalysis()
                .transformationAnalysis()
                .functionalAnalysis()
                .annualFunctionalCapacityAnalysis();
    }
}