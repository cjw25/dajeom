package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.NatalTenGodFunctionType;

import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 하나의 AnnualLuckPeriod 안에서
 * 특정 DecadeLuckPeriod가 실제로 가능한
 * 시간 Segment의 Functional Aggregate.
 *
 * =================================================
 *
 * 하나의 Segment는 다음 Identity를 묶는다.
 *
 * 14-5
 * AnnualDecadeLuckInteraction
 *
 * +
 *
 * 14-6
 * AnnualLuckDecadeContextAnalysis
 *
 * +
 *
 * 해당 대운
 * DecadePeriodFunctionalSynthesisAnalysis
 *
 * +
 *
 * 해당 세운
 * AnnualFunctionalCapacityAnalysis
 *
 * =================================================
 *
 * 교운기에 대운이 2개 걸리면
 * 이 객체도 2개 생성된다.
 *
 * =================================================
 *
 * 이 단계에서는
 * 대운과 세운의 Function 상태를
 * 평균내거나 하나로 압축하지 않는다.
 */
public record AnnualDecadeFunctionalSegment(

        AnnualDecadeLuckInteraction interaction,

        AnnualLuckDecadeContextAnalysis annualDecadeContext,

        DecadePeriodFunctionalSynthesisAnalysis decadeBaseline,

        AnnualFunctionalCapacityAnalysis annualCapacityAnalysis,

        List<AnnualDecadeFunctionSegmentEffect> functionEffects,

        boolean completeChart

) {

    public AnnualDecadeFunctionalSegment {

        Objects.requireNonNull(
                interaction,
                "Annual-Decade Functional Segment에는 14-5 Interaction이 필요합니다."
        );

        Objects.requireNonNull(
                annualDecadeContext,
                "Annual-Decade Functional Segment에는 14-6 Segment Context가 필요합니다."
        );

        Objects.requireNonNull(
                decadeBaseline,
                "Annual-Decade Functional Segment에는 Decade Functional 기준선이 필요합니다."
        );

        Objects.requireNonNull(
                annualCapacityAnalysis,
                "Annual-Decade Functional Segment에는 Annual Functional Capacity가 필요합니다."
        );

        functionEffects =
                functionEffects == null
                        ? List.of()
                        : List.copyOf(
                        functionEffects
                );

        /*
         * =================================================
         * Interaction ↔ 14-6
         * =================================================
         */

        if (
                annualDecadeContext.interaction()
                        != interaction
        ) {

            throw new IllegalArgumentException(
                    "14-6 Segment Context가 현재 14-5 Interaction Identity를 보존하지 않았습니다."
            );
        }

        if (
                annualDecadeContext.annualPeriod()
                        != interaction.annualPeriod()
        ) {

            throw new IllegalArgumentException(
                    "14-6 Segment Context와 14-5 Interaction이 서로 다른 AnnualLuckPeriod를 참조합니다."
            );
        }

        /*
         * =================================================
         * Interaction ↔ Decade Functional
         * =================================================
         */

        if (
                decadeBaseline.sequenceNumber()
                        != interaction.decadeSequenceNumber()
        ) {

            throw new IllegalArgumentException(
                    "대운 Functional 기준선과 14-5 Interaction의 Sequence가 일치하지 않습니다."
            );
        }

        if (
                decadeBaseline.period()
                        != interaction.decadePeriod()
        ) {

            throw new IllegalArgumentException(
                    "대운 Functional 기준선과 14-5 Interaction이 서로 다른 DecadeLuckPeriod를 참조합니다."
            );
        }

        /*
         * =================================================
         * Interaction ↔ Annual Functional
         * =================================================
         */

        if (
                annualCapacityAnalysis.period()
                        != interaction.annualPeriod()
        ) {

            throw new IllegalArgumentException(
                    "Annual Functional Capacity와 14-5 Interaction이 서로 다른 AnnualLuckPeriod를 참조합니다."
            );
        }

        /*
         * =================================================
         * completeChart
         * =================================================
         */

        if (
                decadeBaseline.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Decade Functional 기준선과 Segment의 completeChart가 일치하지 않습니다."
            );
        }

        if (
                annualCapacityAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Annual Functional Capacity와 Segment의 completeChart가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 5 Function
         * =================================================
         */

        if (
                functionEffects.size()
                        != NatalTenGodFunctionType
                        .values()
                        .length
        ) {

            throw new IllegalArgumentException(
                    "Annual-Decade Functional Segment에는 정확히 5개 Function Effect가 필요합니다."
            );
        }

        Set<NatalTenGodFunctionType> seen =
                EnumSet.noneOf(
                        NatalTenGodFunctionType.class
                );

        for (
                AnnualDecadeFunctionSegmentEffect effect
                : functionEffects
        ) {

            Objects.requireNonNull(
                    effect,
                    "AnnualDecadeFunctionSegmentEffect는 null일 수 없습니다."
            );

            if (
                    effect.interaction()
                            != interaction
            ) {

                throw new IllegalArgumentException(
                        "Function Segment Effect가 현재 Interaction Identity를 보존하지 않았습니다."
                );
            }

            if (
                    effect.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Function Segment Effect의 completeChart가 일치하지 않습니다."
                );
            }

            DecadeResolvedFunctionEffect expectedDecade =
                    decadeBaseline
                            .requireFunctionEffect(
                                    effect.functionType()
                            );

            if (
                    effect.decadeFunctionEffect()
                            != expectedDecade
            ) {

                throw new IllegalArgumentException(
                        "Function Segment Effect가 Decade Function Identity를 보존하지 않았습니다. function="
                                + effect.functionType()
                );
            }

            AnnualFunctionCapacityContext expectedAnnual =
                    annualCapacityAnalysis
                            .requireContext(
                                    effect.functionType()
                            );

            if (
                    effect.annualFunctionContext()
                            != expectedAnnual
            ) {

                throw new IllegalArgumentException(
                        "Function Segment Effect가 Annual Function Identity를 보존하지 않았습니다. function="
                                + effect.functionType()
                );
            }

            if (
                    !seen.add(
                            effect.functionType()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 Function Segment Effect가 중복되었습니다: "
                                + effect.functionType()
                );
            }
        }

        if (
                !seen.equals(
                        EnumSet.allOf(
                                NatalTenGodFunctionType.class
                        )
                )
        ) {

            throw new IllegalArgumentException(
                    "Annual-Decade Functional Segment의 5개 Function Coverage가 완전하지 않습니다."
            );
        }
    }

    /*
     * =================================================
     * Identity
     * =================================================
     */

    public int targetYear() {

        return interaction
                .targetYear();
    }

    public int decadeSequenceNumber() {

        return interaction
                .decadeSequenceNumber();
    }

    public AnnualDecadeLuckOverlap overlap() {

        return interaction
                .overlap();
    }

    public boolean timingUncertain() {

        return interaction
                .timingUncertain();
    }

    /*
     * =================================================
     * Function
     * =================================================
     */

    public Optional<AnnualDecadeFunctionSegmentEffect>
    functionEffectOf(
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

    public AnnualDecadeFunctionSegmentEffect
    requireFunctionEffect(
            NatalTenGodFunctionType functionType
    ) {

        Objects.requireNonNull(
                functionType,
                "Annual-Decade Segment Function 조회에는 Function Type이 필요합니다."
        );

        return functionEffectOf(
                functionType
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Annual-Decade Segment Function Effect를 찾을 수 없습니다: "
                                                + functionType
                                )
                );
    }

    public List<AnnualDecadeFunctionSegmentEffect>
    continuationEffects() {

        return functionEffects
                .stream()
                .filter(
                        AnnualDecadeFunctionSegmentEffect
                                ::continuationContact
                )
                .toList();
    }

    public List<AnnualDecadeFunctionSegmentEffect>
    annualNewActivationEffects() {

        return functionEffects
                .stream()
                .filter(
                        AnnualDecadeFunctionSegmentEffect
                                ::annualNewActivation
                )
                .toList();
    }

    public List<AnnualDecadeFunctionSegmentEffect>
    decadeBaselineOnlyEffects() {

        return functionEffects
                .stream()
                .filter(
                        AnnualDecadeFunctionSegmentEffect
                                ::decadeBaselineOnly
                )
                .toList();
    }

    public List<AnnualDecadeFunctionSegmentEffect>
    sameFunctionAccumulationCandidates() {

        return functionEffects
                .stream()
                .filter(
                        AnnualDecadeFunctionSegmentEffect
                                ::sameFunctionAccumulationCandidate
                )
                .toList();
    }

    public List<AnnualDecadeFunctionSegmentEffect>
    transformationMediatedEffects() {

        return functionEffects
                .stream()
                .filter(
                        AnnualDecadeFunctionSegmentEffect
                                ::transformationMediatedContact
                )
                .toList();
    }

    /*
     * =================================================
     * 대운 ↔ 세운 Capacity-side 교차
     * =================================================
     *
     * 이것은 서로 다른 Function Family까지 포함한
     * 시간 Layer 간 방향 접촉을 확인한다.
     *
     * 예:
     *
     * 대운 support-side 활성
     * +
     * 세운 pressure-side 활성
     *
     * =================================================
     *
     * 이것 자체가 상쇄/길흉을 의미하지 않는다.
     */

    public boolean opposingCapacitySideContactExists() {

        boolean decadeSupport =
                functionEffects
                        .stream()
                        .anyMatch(
                                effect ->
                                        effect.decadeActive()

                                                && effect
                                                .decadeFunctionEffect()
                                                .supportSide()
                        );

        boolean decadePressure =
                functionEffects
                        .stream()
                        .anyMatch(
                                effect ->
                                        effect.decadeActive()

                                                && effect
                                                .decadeFunctionEffect()
                                                .pressureSide()
                        );

        boolean annualSupport =
                functionEffects
                        .stream()
                        .anyMatch(
                                effect ->
                                        effect.annualActive()

                                                && effect
                                                .annualFunctionContext()
                                                .supportSide()
                        );

        boolean annualPressure =
                functionEffects
                        .stream()
                        .anyMatch(
                                effect ->
                                        effect.annualActive()

                                                && effect
                                                .annualFunctionContext()
                                                .pressureSide()
                        );

        return decadeSupport
                && annualPressure

                || decadePressure
                && annualSupport;
    }

    /*
     * =================================================
     * 14-5 Typed Relation
     * =================================================
     */

    public List<AnnualDecadeLuckRelationReference>
    crossLuckRelations() {

        return interaction
                .relations();
    }

    public boolean crossLuckRelationExists() {

        return !crossLuckRelations()
                .isEmpty();
    }

    public boolean restrictiveCrossLuckRelationExists() {

        return crossLuckRelations()
                .stream()
                .anyMatch(
                        AnnualDecadeLuckRelationReference
                                ::restrictiveInteraction
                );
    }

    public boolean decadeControlsAnnualExists() {

        return crossLuckRelations()
                .stream()
                .anyMatch(
                        AnnualDecadeLuckRelationReference
                                ::decadeControlsAnnual
                );
    }

    public boolean annualControlsDecadeExists() {

        return crossLuckRelations()
                .stream()
                .anyMatch(
                        AnnualDecadeLuckRelationReference
                                ::annualControlsDecade
                );
    }

    /*
     * =================================================
     * 14-5 Typed Transformation
     * =================================================
     */

    public List<AnnualDecadeLuckTransformationReference>
    crossLuckTransformations() {

        return interaction
                .transformations();
    }

    public List<AnnualDecadeLuckTransformationReference>
    activeCrossLuckTransformations() {

        return crossLuckTransformations()
                .stream()
                .filter(
                        AnnualDecadeLuckTransformationReference
                                ::activated
                )
                .toList();
    }

    public boolean activeCrossLuckTransformationExists() {

        return !activeCrossLuckTransformations()
                .isEmpty();
    }

    public boolean strongCrossLuckTransformationExists() {

        return activeCrossLuckTransformations()
                .stream()
                .anyMatch(
                        AnnualDecadeLuckTransformationReference
                                ::strongTargetActivation
                );
    }

    public boolean crossLuckAnnualOriginalFunctionReductionExists() {

        return crossLuckTransformations()
                .stream()
                .anyMatch(
                        AnnualDecadeLuckTransformationReference
                                ::annualOriginalFunctionReduced
                );
    }

    public boolean crossLuckDecadeOriginalFunctionReductionExists() {

        return crossLuckTransformations()
                .stream()
                .anyMatch(
                        AnnualDecadeLuckTransformationReference
                                ::decadeOriginalFunctionReduced
                );
    }

    public boolean mixedCrossLuckParticipantStateExists() {

        return crossLuckTransformations()
                .stream()
                .anyMatch(
                        AnnualDecadeLuckTransformationReference
                                ::mixedParticipantOriginalFunctionState
                );
    }

    /*
     * =================================================
     * 14-5 Transformation Target Function
     * =================================================
     *
     * 아직 Source Function에 배분하지 않는다.
     *
     * Target Element가 어떤 Functional Family로
     * 이동하는지만 구조적으로 확정한다.
     */

    public List<NatalTenGodFunctionType>
    activeCrossLuckTransformationTargetFunctions() {

        Set<NatalTenGodFunctionType> result =
                new LinkedHashSet<>();

        for (
                AnnualDecadeLuckTransformationReference transformation
                : activeCrossLuckTransformations()
        ) {

            result.add(
                    resolveTargetFunction(
                            transformation.targetElement()
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    /**
     * 대운/세운 양쪽 기존 Function에는 없었지만
     * 14-5 Transformation Target으로
     * 새로 나타나는 Function Candidate.
     *
     * =================================================
     *
     * 아직 최종 활성 확정이 아니라
     * Cross-Luck Transformation Target Candidate다.
     */
    public List<NatalTenGodFunctionType>
    crossLuckTransformationTargetOnlyCandidates() {

        return activeCrossLuckTransformationTargetFunctions()
                .stream()
                .filter(
                        functionType -> {

                            AnnualDecadeFunctionSegmentEffect effect =
                                    requireFunctionEffect(
                                            functionType
                                    );

                            return !effect.decadeActive()
                                    && !effect.annualActive();
                        }
                )
                .toList();
    }

    private NatalTenGodFunctionType resolveTargetFunction(
            FiveElement targetElement
    ) {

        Objects.requireNonNull(
                targetElement,
                "14-5 Transformation Target Element가 필요합니다."
        );

        int matches =
                0;

        NatalTenGodFunctionType result =
                null;

        for (
                Map.Entry<
                        com.dajeom.type.DayMasterElementRole,
                        FiveElement
                        > entry
                : annualCapacityAnalysis
                .elementRelationAnalysis()
                .elementByRole()
                .entrySet()
        ) {

            if (
                    entry.getValue()
                            != targetElement
            ) {

                continue;
            }

            result =
                    AnnualFunctionMigrationTarget
                            .functionTypeOf(
                                    entry.getKey()
                            );

            matches++;
        }

        if (
                matches != 1
                        || result == null
        ) {

            throw new IllegalStateException(
                    "14-5 Transformation Target Element를 Function 하나로 확정할 수 없습니다. "
                            + "targetElement="
                            + targetElement
                            + ", matches="
                            + matches
            );
        }

        return result;
    }

    /*
     * =================================================
     * 14-6 Segment Context
     * =================================================
     */

    public List<AnnualLuckContextSignal>
    annualContextSignals() {

        return annualDecadeContext
                .annualSignals();
    }

    public List<AnnualLuckContextSignal>
    segmentContextSignals() {

        return annualDecadeContext
                .segmentSignals();
    }

    /*
     * =================================================
     * Final Qualification
     * =================================================
     */

    public boolean qualificationRequired() {

        return timingUncertain()

                || restrictiveCrossLuckRelationExists()

                || activeCrossLuckTransformationExists()

                || crossLuckAnnualOriginalFunctionReductionExists()

                || crossLuckDecadeOriginalFunctionReductionExists()

                || mixedCrossLuckParticipantStateExists()

                || opposingCapacitySideContactExists()

                || functionEffects
                .stream()
                .anyMatch(
                        AnnualDecadeFunctionSegmentEffect
                                ::qualificationRequired
                );
    }
}