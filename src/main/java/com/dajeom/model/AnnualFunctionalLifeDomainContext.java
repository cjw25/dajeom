package com.dajeom.model;

import com.dajeom.type.AnnualFunctionalBaselineStatus;
import com.dajeom.type.NatalCrossFunctionalPatternType;
import com.dajeom.type.NatalTenGodFunctionType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Annual Functional 최종 기준선을
 * 기존 18차 LifeDomain Timeline Segment에
 * 연결하기 위한 typed adapter.
 *
 * =================================================
 *
 * Source:
 *
 * AnnualFunctionalBaselineAnalysis
 *
 * =================================================
 *
 * Timeline Anchor:
 *
 * LuckIntegratedSegmentAnalysis
 *
 * =================================================
 *
 * 중요한 이유:
 *
 * AnnualFunctionalBaselineAnalysis는
 * 하나의 세운 전체를 다루며,
 * 그 안에 여러 대운 Segment가 존재할 수 있다.
 *
 * 반면 18차 LifeDomain은
 * 16차 LuckIntegratedSegmentAnalysis 단위로 실행된다.
 *
 * 따라서 단순히:
 *
 * AnnualFunctionalBaselineAnalysis
 *
 * 만 Evidence Source로 넣지 않고,
 *
 * 현재 16차 Timeline Segment와 함께 묶어서
 * temporal provenance를 보존한다.
 *
 * =================================================
 *
 * 이 Adapter가 추가로 담당하는 것:
 *
 * 현재 16차 Timeline Segment가
 * 실제로 선택한 대운 Sequence를 이용해
 *
 * Annual Functional 내부의
 * 정확한 Annual-Decade Functional Segment를
 * 조회한다.
 *
 * =================================================
 *
 * 절대 하지 않는 것:
 *
 * - 날짜로 대운을 다시 계산
 * - 여러 대운 Segment 평균
 * - 첫 번째 Segment 임의 선택
 * - 가능한 대운 중 하나 강제 선택
 * - Function → LifeDomain 길흉 직접 변환
 * - Cross Pattern 승격
 *
 * =================================================
 *
 * BEFORE_FIRST /
 * AFTER_GENERATED_RANGE /
 * UNAVAILABLE
 *
 * 등 16차 Timeline에 활성 대운이 없는 경우에는
 * currentFunctionalSegment()도 empty다.
 *
 * =================================================
 *
 * Annual Functional Baseline 자체가
 * DECADE_SEGMENT_UNAVAILABLE인 경우에도
 *
 * Annual 자체 Function Capacity는
 * 계속 사용할 수 있다.
 *
 * 따라서 대운 Segment unavailable을
 * Annual Functional 전체 unavailable로
 * 해석하지 않는다.
 */
public record AnnualFunctionalLifeDomainContext(

        LuckIntegratedSegmentAnalysis segmentAnalysis,

        AnnualFunctionalBaselineAnalysis annualFunctionalBaseline

) {

    public AnnualFunctionalLifeDomainContext {

        Objects.requireNonNull(
                segmentAnalysis,
                "Annual Functional LifeDomain Context에는 Timeline Segment가 필요합니다."
        );

        Objects.requireNonNull(
                annualFunctionalBaseline,
                "Annual Functional LifeDomain Context에는 AnnualFunctionalBaselineAnalysis가 필요합니다."
        );

        /*
         * =================================================
         * Annual Year
         * =================================================
         */

        int segmentYear =
                segmentAnalysis
                        .annualCandidate()
                        .targetYear();

        if (
                annualFunctionalBaseline.targetYear()
                        != segmentYear
        ) {

            throw new IllegalArgumentException(
                    "Timeline Segment와 Annual Functional Baseline의 세운 연도가 일치하지 않습니다. "
                            + "segmentYear="
                            + segmentYear
                            + ", baselineYear="
                            + annualFunctionalBaseline.targetYear()
            );
        }

        /*
         * =================================================
         * 14-6 Annual Context Identity
         * =================================================
         *
         * 기존 16차가 가진 AnnualLuckContextAnalysis와
         * 신규 Functional Chain의 원본 AnnualLuckAnalysis가
         * 정확히 같은 14-6 객체를 참조해야 한다.
         *
         * 같은 연도라는 이유만으로 연결하지 않는다.
         */

        AnnualLuckContextAnalysis expectedAnnualContext =
                segmentAnalysis
                        .annualContextAnalysis();

        AnnualLuckContextAnalysis actualAnnualContext =
                annualFunctionalBaseline
                        .annualLuckAnalysis()
                        .contextAnalysis();

        if (
                actualAnnualContext
                        != expectedAnnualContext
        ) {

            throw new IllegalArgumentException(
                    "Timeline Segment와 Annual Functional Baseline이 "
                            + "서로 다른 AnnualLuckContextAnalysis를 참조합니다. "
                            + "targetYear="
                            + segmentYear
            );
        }

        /*
         * =================================================
         * 현재 대운 Functional Segment Coverage
         * =================================================
         *
         * 16차가 ACTIVE 대운 Sequence를 가지고 있고
         * Annual Functional Baseline도 대운 Segment를
         * 사용할 수 있다고 선언했다면,
         *
         * 동일 Sequence의 Functional Segment가
         * 반드시 존재해야 한다.
         *
         * 단:
         *
         * Baseline 자체가
         * DECADE_SEGMENT_UNAVAILABLE 상태라면
         * Annual 자체 Functional만 사용할 수 있으므로
         * 여기서 실패시키지 않는다.
         */

        validateCurrentDecadeSegmentCoverage(
                segmentAnalysis,
                annualFunctionalBaseline
        );
    }

    /*
     * =================================================
     * Identity
     * =================================================
     */

    public int targetYear() {

        return annualFunctionalBaseline
                .targetYear();
    }

    public AnnualLuckContextAnalysis
    annualContextAnalysis() {

        return segmentAnalysis
                .annualContextAnalysis();
    }

    public boolean completeChart() {

        return annualFunctionalBaseline
                .completeChart();
    }

    /*
     * =================================================
     * Baseline
     * =================================================
     */

    public AnnualFunctionalBaselineStatus
    baselineStatus() {

        return annualFunctionalBaseline
                .baselineStatus();
    }

    public boolean annualFunctionalAvailable() {

        return annualFunctionalBaseline
                .annualFunctionalAvailable();
    }

    public boolean decadeSegmentAvailable() {

        return annualFunctionalBaseline
                .decadeSegmentAvailable();
    }

    public boolean annualOnly() {

        return annualFunctionalBaseline
                .annualOnly();
    }

    public boolean hasDecadeSegment() {

        return annualFunctionalBaseline
                .hasDecadeSegment();
    }

    public boolean multipleDecadeSegments() {

        return annualFunctionalBaseline
                .multipleDecadeSegments();
    }

    public boolean timingUncertain() {

        return annualFunctionalBaseline
                .timingUncertain();
    }

    /*
     * =================================================
     * 16차 Timeline의 현재 대운
     * =================================================
     */

    /**
     * 현재 16차 Timeline Segment가
     * 실제 대운 Candidate를 가지고 있는가.
     *
     * 이는 Annual Functional Baseline 전체에
     * 대운 Segment가 존재하는가와 다른 개념이다.
     */
    public boolean timelineHasActiveDecade() {

        return segmentAnalysis
                .timelineSegment()
                .hasDecade();
    }

    /**
     * 현재 16차 Timeline Segment가
     * 실제 선택한 대운 Sequence.
     *
     * BEFORE_FIRST /
     * AFTER_GENERATED_RANGE /
     * UNAVAILABLE
     *
     * 에서는 empty.
     */
    public Optional<Integer>
    timelineDecadeSequenceNumber() {

        return segmentAnalysis
                .timelineSegment()
                .decadeSequenceNumber();
    }

    /*
     * =================================================
     * 현재 Annual-Decade Functional Segment
     * =================================================
     */

    /**
     * 현재 16차 Timeline Segment가 가리키는
     * 정확한 Annual Functional 대운 Segment.
     *
     * =================================================
     *
     * Sequence 기준으로만 조회한다.
     *
     * 날짜 재계산 없음.
     * 첫 Segment 선택 없음.
     * 여러 Segment 평균 없음.
     */
    public Optional<
            AnnualCrossFunctionalSynthesisSegmentAnalysis
            >
    currentFunctionalSegment() {

        Optional<Integer> sequenceNumber =
                timelineDecadeSequenceNumber();

        if (
                sequenceNumber.isEmpty()
        ) {

            return Optional.empty();
        }

        /*
         * Annual Functional 자체는 존재하지만
         * 대운 결합 분석이 unavailable일 수 있다.
         */

        if (
                !decadeSegmentAvailable()
        ) {

            return Optional.empty();
        }

        return annualFunctionalBaseline
                .segmentOfDecade(
                        sequenceNumber
                                .orElseThrow()
                );
    }

    /**
     * 현재 Timeline이 ACTIVE 대운을 가리키고
     * Functional Baseline도 대운 Segment를
     * 사용할 수 있는 상태라면
     * 정확한 Functional Segment가 반드시 필요하다.
     */
    public AnnualCrossFunctionalSynthesisSegmentAnalysis
    requireCurrentFunctionalSegment() {

        int sequenceNumber =
                timelineDecadeSequenceNumber()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "현재 Timeline Segment에는 활성 대운 Sequence가 없습니다. "
                                                        + "targetYear="
                                                        + targetYear()
                                        )
                        );

        if (
                !decadeSegmentAvailable()
        ) {

            throw new IllegalStateException(
                    "현재 Annual Functional Baseline은 대운 Segment 분석을 사용할 수 없습니다. "
                            + "targetYear="
                            + targetYear()
                            + ", sequence="
                            + sequenceNumber
                            + ", baselineStatus="
                            + baselineStatus()
            );
        }

        return annualFunctionalBaseline
                .segmentOfDecade(
                        sequenceNumber
                )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "현재 Timeline 대운 Sequence에 대응하는 Annual Functional Segment가 없습니다. "
                                                + "targetYear="
                                                + targetYear()
                                                + ", sequence="
                                                + sequenceNumber
                                )
                );
    }

    public boolean currentFunctionalSegmentAvailable() {

        return currentFunctionalSegment()
                .isPresent();
    }

    public boolean currentFunctionalSegmentTimingUncertain() {

        return currentFunctionalSegment()
                .map(
                        AnnualCrossFunctionalSynthesisSegmentAnalysis
                                ::timingUncertain
                )
                .orElse(
                        false
                );
    }

    /*
     * =================================================
     * Annual Function
     * =================================================
     */

    public List<AnnualFunctionCapacityContext>
    annualFunctionContexts() {

        return annualFunctionalBaseline
                .annualFunctionContexts();
    }

    public Optional<AnnualFunctionCapacityContext>
    annualFunctionContextOf(
            NatalTenGodFunctionType functionType
    ) {

        return annualFunctionalBaseline
                .annualFunctionContextOf(
                        functionType
                );
    }

    public AnnualFunctionCapacityContext
    requireAnnualFunctionContext(
            NatalTenGodFunctionType functionType
    ) {

        return annualFunctionalBaseline
                .requireAnnualFunctionContext(
                        functionType
                );
    }

    public List<AnnualFunctionCapacityContext>
    annualActiveFunctionContexts() {

        return annualFunctionalBaseline
                .annualActiveFunctionContexts();
    }

    public List<AnnualFunctionCapacityContext>
    annualTransformationTargetContexts() {

        return annualFunctionalBaseline
                .annualTransformationTargetContexts();
    }

    public List<AnnualFunctionCapacityContext>
    annualCapacityReviewContexts() {

        return annualFunctionalBaseline
                .annualCapacityReviewContexts();
    }

    /*
     * =================================================
     * 현재 Segment Resolved Function
     * =================================================
     *
     * Annual 자체 Capacity Active와
     * 대운 결합 후 현재 Segment에서의
     * 최종 Resolved Active는 구분한다.
     */

    public boolean currentResolvedFunctionActive(
            NatalTenGodFunctionType functionType
    ) {

        Objects.requireNonNull(
                functionType,
                "현재 Annual Functional Segment Function 조회에는 Function Type이 필요합니다."
        );

        return currentFunctionalSegment()
                .map(
                        functionalSegment ->
                                functionalSegment
                                        .sourceSegmentAnalysis()
                                        .resolvedFunctionalSegment()
                                        .requireEffect(
                                                functionType
                                        )
                                        .active()
                )
                .orElse(
                        false
                );
    }

    /*
     * =================================================
     * 현재 Segment CrossFunctional
     * =================================================
     */

    public List<AnnualCrossFunctionalSynthesis>
    currentContactedSyntheses() {

        return currentFunctionalSegment()
                .map(
                        AnnualCrossFunctionalSynthesisSegmentAnalysis
                                ::contactedSyntheses
                )
                .orElse(
                        List.of()
                );
    }

    public List<AnnualCrossFunctionalSynthesis>
    currentEffectiveFullContacts() {

        return currentFunctionalSegment()
                .map(
                        AnnualCrossFunctionalSynthesisSegmentAnalysis
                                ::effectiveFullContacts
                )
                .orElse(
                        List.of()
                );
    }

    public List<AnnualCrossFunctionalSynthesis>
    currentEffectiveNewCompletionCandidates() {

        return currentFunctionalSegment()
                .map(
                        AnnualCrossFunctionalSynthesisSegmentAnalysis
                                ::effectiveNewCompletionCandidates
                )
                .orElse(
                        List.of()
                );
    }

    public List<AnnualCrossFunctionalSynthesis>
    currentReferenceContacts() {

        return currentFunctionalSegment()
                .map(
                        AnnualCrossFunctionalSynthesisSegmentAnalysis
                                ::referenceContacts
                )
                .orElse(
                        List.of()
                );
    }

    public List<AnnualCrossFunctionalSynthesis>
    currentQualifiedSyntheses() {

        return currentFunctionalSegment()
                .map(
                        AnnualCrossFunctionalSynthesisSegmentAnalysis
                                ::qualifiedSyntheses
                )
                .orElse(
                        List.of()
                );
    }

    public Optional<AnnualCrossFunctionalSynthesis>
    currentSynthesisOf(
            NatalCrossFunctionalPatternType patternType
    ) {

        if (
                patternType == null
        ) {

            return Optional.empty();
        }

        return currentFunctionalSegment()
                .flatMap(
                        segment ->
                                segment
                                        .synthesisOf(
                                                patternType
                                        )
                );
    }

    /*
     * =================================================
     * 현재 Segment Context
     * =================================================
     */

    public List<AnnualLuckContextSignal>
    currentGlobalContextSignals() {

        return currentFunctionalSegment()
                .map(
                        AnnualCrossFunctionalSynthesisSegmentAnalysis
                                ::globalContextSignals
                )
                .orElse(
                        List.of()
                );
    }

    public List<AnnualLuckContextSignal>
    currentUnmatchedStructuredContextSignals() {

        return currentFunctionalSegment()
                .map(
                        AnnualCrossFunctionalSynthesisSegmentAnalysis
                                ::unmatchedStructuredContextSignals
                )
                .orElse(
                        List.of()
                );
    }

    public boolean currentMixedContext() {

        return currentFunctionalSegment()
                .map(
                        AnnualCrossFunctionalSynthesisSegmentAnalysis
                                ::mixedContext
                )
                .orElse(
                        false
                );
    }

    /*
     * =================================================
     * Qualification
     * =================================================
     */

    public boolean qualificationRequired() {

        return annualFunctionalBaseline
                .qualificationRequired();
    }

    public boolean currentSegmentQualificationRequired() {

        return !currentQualifiedSyntheses()
                .isEmpty();
    }

    /*
     * =================================================
     * Internal Validation
     * =================================================
     */

    private static void
    validateCurrentDecadeSegmentCoverage(

            LuckIntegratedSegmentAnalysis segmentAnalysis,

            AnnualFunctionalBaselineAnalysis baseline

    ) {

        Optional<Integer> sequenceOptional =
                segmentAnalysis
                        .timelineSegment()
                        .decadeSequenceNumber();

        /*
         * 16차 Timeline에 활성 대운이 없다.
         *
         * Annual-only Functional 사용 가능.
         */

        if (
                sequenceOptional.isEmpty()
        ) {

            return;
        }

        /*
         * Annual Functional Baseline 자체가
         * 대운 결합 Segment를 사용할 수 없는 경우.
         *
         * Annual Function Capacity는 여전히 유효하므로
         * 오류로 만들지 않는다.
         */

        if (
                !baseline
                        .decadeSegmentAvailable()
        ) {

            return;
        }

        int sequenceNumber =
                sequenceOptional
                        .orElseThrow();

        AnnualCrossFunctionalSynthesisSegmentAnalysis
                functionalSegment =
                baseline
                        .segmentOfDecade(
                                sequenceNumber
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "16차 Timeline의 활성 대운 Sequence에 대응하는 "
                                                        + "Annual Functional Segment가 없습니다. "
                                                        + "targetYear="
                                                        + baseline.targetYear()
                                                        + ", sequence="
                                                        + sequenceNumber
                                        )
                        );

        if (
                functionalSegment
                        .targetYear()
                        != baseline.targetYear()
        ) {

            throw new IllegalArgumentException(
                    "현재 Annual Functional Segment의 세운 연도가 Baseline과 일치하지 않습니다. "
                            + "baselineYear="
                            + baseline.targetYear()
                            + ", segmentYear="
                            + functionalSegment.targetYear()
            );
        }

        if (
                functionalSegment
                        .decadeSequenceNumber()
                        != sequenceNumber
        ) {

            throw new IllegalArgumentException(
                    "현재 Annual Functional Segment의 대운 Sequence가 "
                            + "16차 Timeline Sequence와 일치하지 않습니다. "
                            + "timelineSequence="
                            + sequenceNumber
                            + ", functionalSequence="
                            + functionalSegment.decadeSequenceNumber()
            );
        }

        if (
                functionalSegment
                        .completeChart()
                        != baseline.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "현재 Annual Functional Segment와 Baseline의 completeChart가 일치하지 않습니다. "
                            + "targetYear="
                            + baseline.targetYear()
                            + ", sequence="
                            + sequenceNumber
            );
        }
    }
}