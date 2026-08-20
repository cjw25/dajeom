package com.dajeom.model;

import com.dajeom.type.NatalTenGodFunctionType;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 하나의 Annual-Decade Segment에 대한
 * 최종 5축 Functional 상태.
 *
 * =================================================
 *
 * 하나의 Segment마다:
 *
 * 5개의 AnnualResolvedFunctionEffect
 *
 * 를 정확히 보존한다.
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * 이 객체는:
 *
 * - SELF_ASSERTION
 * - EXPRESSION
 * - RESOURCE_REALITY
 * - RESPONSIBILITY
 * - PROCESSING
 *
 * 5개 Function의 최종 Annual-Decade 상태를
 * 하나의 Segment 단위로 묶는다.
 *
 * =================================================
 *
 * 여기서는:
 *
 * - Function을 다시 계산하지 않는다.
 * - 특정 Function을 대표 Function으로 선택하지 않는다.
 * - 여러 Function을 길흉 점수로 합산하지 않는다.
 *
 * =================================================
 * Identity
 * =================================================
 *
 * 모든 AnnualResolvedFunctionEffect는:
 *
 * - 동일 Transformation Resolution
 * - 동일 Annual-Decade Functional Segment
 * - 동일 completeChart
 *
 * 를 보존해야 한다.
 */
public record AnnualResolvedFunctionalSegmentAnalysis(

        AnnualDecadeTransformationResolution
        transformationResolution,

        List<AnnualResolvedFunctionEffect>
        functionEffects,

        boolean completeChart

) {

    public AnnualResolvedFunctionalSegmentAnalysis {

        /*
         * =================================================
         * Compact Constructor 주의
         * =================================================
         *
         * Java record의 compact constructor 본문이
         * 실행되는 동안에는 record field의 최종 대입이
         * 아직 끝나지 않았다.
         *
         * 따라서 이 생성자 내부에서는:
         *
         * sourceSegment()
         * targetYear()
         * decadeSequenceNumber()
         * period()
         * overlap()
         * timingUncertain()
         *
         * 처럼 this.transformationResolution을 읽는
         * instance helper를 호출하면 안 된다.
         *
         * 반드시 생성자 매개변수
         * transformationResolution에서 직접
         * 필요한 값을 꺼내 사용한다.
         */

        Objects.requireNonNull(
                transformationResolution,
                "Annual Resolved Segment에는 Transformation Resolution이 필요합니다."
        );

        functionEffects =
                functionEffects == null
                        ? List.of()
                        : List.copyOf(
                        functionEffects
                );

        /*
         * =================================================
         * Constructor-local Source Segment
         * =================================================
         *
         * 기존 생성자에서는 아래 검증 중:
         *
         * sourceSegment()
         *
         * 을 호출했다.
         *
         * sourceSegment()
         * ->
         * this.transformationResolution.sourceSegment()
         *
         * 로 이어지기 때문에 compact constructor
         * 실행 중 NPE가 발생했다.
         *
         * 여기서는 생성자 매개변수에서 직접 확정한다.
         */

        AnnualDecadeFunctionalSegment currentSourceSegment =
                Objects.requireNonNull(
                        transformationResolution
                                .sourceSegment(),
                        "Annual Transformation Resolution에는 Source Segment가 필요합니다."
                );

        /*
         * =================================================
         * Complete Chart
         * =================================================
         */

        if (
                transformationResolution.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Transformation Resolution과 Annual Resolved Segment의 completeChart가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 5 Function Coverage
         * =================================================
         */

        if (
                functionEffects.size()
                        != NatalTenGodFunctionType
                        .values()
                        .length
        ) {

            throw new IllegalArgumentException(
                    "Annual Resolved Segment에는 정확히 5개의 Function Effect가 필요합니다."
            );
        }

        Set<NatalTenGodFunctionType> seen =
                EnumSet.noneOf(
                        NatalTenGodFunctionType.class
                );

        /*
         * =================================================
         * Function Effect 검증
         * =================================================
         */

        for (
                AnnualResolvedFunctionEffect effect
                : functionEffects
        ) {

            Objects.requireNonNull(
                    effect,
                    "AnnualResolvedFunctionEffect는 null일 수 없습니다."
            );

            /*
             * =================================================
             * Transformation Resolution Identity
             * =================================================
             */

            if (
                    effect.transformationResolution()
                            != transformationResolution
            ) {

                throw new IllegalArgumentException(
                        "Annual Resolved Function Effect가 현재 Transformation Resolution Identity를 보존하지 않았습니다."
                );
            }

            /*
             * =================================================
             * Functional Segment Identity
             * =================================================
             *
             * 기존:
             *
             * effect.sourceSegment()
             * != sourceSegment()
             *
             * 문제:
             *
             * sourceSegment()가 아직 대입되지 않은
             * this.transformationResolution을 읽었다.
             *
             * 수정:
             *
             * 생성자 매개변수에서 확정한
             * currentSourceSegment를 사용한다.
             */

            if (
                    effect.sourceSegment()
                            != currentSourceSegment
            ) {

                throw new IllegalArgumentException(
                        "Annual Resolved Function Effect가 현재 Functional Segment Identity를 보존하지 않았습니다."
                );
            }

            /*
             * =================================================
             * Complete Chart
             * =================================================
             */

            if (
                    effect.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Annual Resolved Function Effect의 completeChart가 일치하지 않습니다."
                );
            }

            /*
             * =================================================
             * Function 중복
             * =================================================
             */

            NatalTenGodFunctionType functionType =
                    Objects.requireNonNull(
                            effect.functionType(),
                            "Annual Resolved Function Effect에는 Function Type이 필요합니다."
                    );

            if (
                    !seen.add(
                            functionType
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 Annual Resolved Function Effect가 중복되었습니다. function="
                                + functionType
                );
            }
        }

        /*
         * =================================================
         * 정확한 5 Function Coverage
         * =================================================
         */

        if (
                !seen.equals(
                        EnumSet.allOf(
                                NatalTenGodFunctionType.class
                        )
                )
        ) {

            throw new IllegalArgumentException(
                    "Annual Resolved Segment의 5개 Function Coverage가 완전하지 않습니다. actual="
                            + seen
            );
        }
    }

    /*
     * =================================================
     * Source Segment
     * =================================================
     *
     * 생성 완료 이후에는
     * this.transformationResolution이 정상 대입되어 있으므로
     * 아래 helper들은 안전하게 사용할 수 있다.
     */

    public AnnualDecadeFunctionalSegment sourceSegment() {

        return transformationResolution
                .sourceSegment();
    }

    public int targetYear() {

        return sourceSegment()
                .targetYear();
    }

    public int decadeSequenceNumber() {

        return sourceSegment()
                .decadeSequenceNumber();
    }

    public AnnualLuckPeriod period() {

        return sourceSegment()
                .interaction()
                .annualPeriod();
    }

    public AnnualDecadeLuckOverlap overlap() {

        return sourceSegment()
                .overlap();
    }

    public boolean timingUncertain() {

        return sourceSegment()
                .timingUncertain();
    }

    /*
     * =================================================
     * Function
     * =================================================
     */

    public Optional<AnnualResolvedFunctionEffect>
    effectOf(

            NatalTenGodFunctionType functionType

    ) {

        if (
                functionType == null
        ) {

            return Optional.empty();
        }

        return functionEffects
                .stream()
                .filter(
                        effect ->
                                effect.functionType()
                                        == functionType
                )
                .findFirst();
    }

    public AnnualResolvedFunctionEffect
    requireEffect(

            NatalTenGodFunctionType functionType

    ) {

        Objects.requireNonNull(
                functionType,
                "Annual Resolved Function 조회에는 Function Type이 필요합니다."
        );

        return effectOf(
                functionType
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Annual Resolved Function Effect를 찾을 수 없습니다: "
                                                + functionType
                                )
                );
    }

    /*
     * =================================================
     * Aggregate
     * =================================================
     */

    public List<AnnualResolvedFunctionEffect>
    activeEffects() {

        return functionEffects
                .stream()
                .filter(
                        AnnualResolvedFunctionEffect
                                ::active
                )
                .toList();
    }

    public List<AnnualResolvedFunctionEffect>
    crossLuckTargetEffects() {

        return functionEffects
                .stream()
                .filter(
                        AnnualResolvedFunctionEffect
                                ::crossLuckTargetActive
                )
                .toList();
    }

    public List<AnnualResolvedFunctionEffect>
    newAgainstDecadeBaselineEffects() {

        return functionEffects
                .stream()
                .filter(
                        AnnualResolvedFunctionEffect
                                ::newAgainstDecadeBaseline
                )
                .toList();
    }

    public List<AnnualResolvedFunctionEffect>
    continuationEffects() {

        return functionEffects
                .stream()
                .filter(
                        AnnualResolvedFunctionEffect
                                ::decadeAnnualContinuation
                )
                .toList();
    }

    public List<AnnualResolvedFunctionEffect>
    effectsWithDecadeOriginalInputReduction() {

        return functionEffects
                .stream()
                .filter(
                        AnnualResolvedFunctionEffect
                                ::decadeOriginalInputReduced
                )
                .toList();
    }

    public List<AnnualResolvedFunctionEffect>
    effectsWithAnnualOriginalInputReduction() {

        return functionEffects
                .stream()
                .filter(
                        AnnualResolvedFunctionEffect
                                ::annualOriginalInputReduced
                )
                .toList();
    }

    public List<AnnualResolvedFunctionEffect>
    effectsRequiringCapacityReview() {

        return functionEffects
                .stream()
                .filter(
                        AnnualResolvedFunctionEffect
                                ::capacityReviewRequired
                )
                .toList();
    }

    public List<AnnualResolvedFunctionEffect>
    qualifiedEffects() {

        return functionEffects
                .stream()
                .filter(
                        AnnualResolvedFunctionEffect
                                ::qualificationRequired
                )
                .toList();
    }
}