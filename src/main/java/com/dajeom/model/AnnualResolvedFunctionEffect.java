package com.dajeom.model;

import com.dajeom.type.AnnualDecadeLuckRelationLayer;
import com.dajeom.type.AnnualResolvedFunctionActivationMode;
import com.dajeom.type.AnnualSegmentOriginalInputState;
import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.NatalTenGodFunctionType;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 하나의 Annual-Decade Segment에서
 * 하나의 Function을 최종 Functional 상태로
 * 수렴시킨 결과.
 *
 * =================================================
 *
 * 결합 Source:
 *
 * 1.
 * DecadeResolvedFunctionEffect
 *
 * 2.
 * AnnualFunctionCapacityContext
 *
 * 3.
 * 14-5
 * AnnualDecadeTransformationParticipantResolution
 *
 * =================================================
 *
 * 최종 Activation Source:
 *
 * - decade baseline
 * - annual self
 * - cross-luck transformation target
 *
 * =================================================
 *
 * 원기능 상태도 분리한다.
 *
 * decadeOriginalInputState
 *
 * =
 *
 * 대운 자체 Transformation
 * +
 * 14-5 대운 Source 감소 Context
 *
 * =================================================
 *
 * annualOriginalInputState
 *
 * =
 *
 * 원국 × 세운 14-4 Transformation
 * +
 * 14-5 세운 Source 감소 Context
 *
 * =================================================
 *
 * 중요:
 *
 * 대운과 세운의 상태를 평균내지 않는다.
 *
 * Function 활성 Source 개수를
 * 강도 점수로 사용하지 않는다.
 *
 * 길흉을 만들지 않는다.
 */
public record AnnualResolvedFunctionEffect(

        AnnualDecadeFunctionalSegment sourceSegment,

        AnnualDecadeTransformationResolution
        transformationResolution,

        AnnualDecadeFunctionSegmentEffect
        segmentFunctionEffect,

        List<AnnualDecadeTransformationParticipantResolution>
        incomingCrossLuckTargets,

        AnnualResolvedFunctionActivationMode
        activationMode,

        AnnualSegmentOriginalInputState
        decadeOriginalInputState,

        AnnualSegmentOriginalInputState
        annualOriginalInputState,

        boolean completeChart

) {

    public AnnualResolvedFunctionEffect {

        /*
         * =================================================
         * Compact Constructor 주의
         * =================================================
         *
         * record compact constructor 본문이 실행되는 동안에는
         * record field의 최종 대입이 아직 완료되지 않았다.
         *
         * 따라서 생성자 내부에서는:
         *
         * functionType()
         * targetYear()
         * decadeSequenceNumber()
         *
         * 처럼 this.segmentFunctionEffect 또는
         * this.sourceSegment를 읽는 instance helper를
         * 호출하면 안 된다.
         *
         * 반드시 생성자 매개변수에서 직접 필요한 값을
         * 꺼내 지역변수로 사용한다.
         */

        Objects.requireNonNull(
                sourceSegment,
                "Annual Resolved Function Effect에는 Source Segment가 필요합니다."
        );

        Objects.requireNonNull(
                transformationResolution,
                "Annual Resolved Function Effect에는 Transformation Resolution이 필요합니다."
        );

        Objects.requireNonNull(
                segmentFunctionEffect,
                "Annual Resolved Function Effect에는 Segment Function Effect가 필요합니다."
        );

        incomingCrossLuckTargets =
                incomingCrossLuckTargets == null
                        ? List.of()
                        : List.copyOf(
                        incomingCrossLuckTargets
                );

        Objects.requireNonNull(
                activationMode,
                "Annual Resolved Function Effect에는 Activation Mode가 필요합니다."
        );

        Objects.requireNonNull(
                decadeOriginalInputState,
                "Annual Resolved Function Effect에는 대운 원기능 상태가 필요합니다."
        );

        Objects.requireNonNull(
                annualOriginalInputState,
                "Annual Resolved Function Effect에는 세운 원기능 상태가 필요합니다."
        );

        /*
         * =================================================
         * Constructor-local Identity
         * =================================================
         *
         * 기존 compact constructor에서는
         * 아래 검증 과정에서 functionType()을 호출했다.
         *
         * functionType()
         *
         * ->
         *
         * this.segmentFunctionEffect.functionType()
         *
         * 그런데 compact constructor가 끝나기 전에는
         * this.segmentFunctionEffect가 아직 null일 수 있다.
         *
         * 따라서 생성자 매개변수에서 직접 확정한다.
         */

        NatalTenGodFunctionType resolvedFunctionType =
                segmentFunctionEffect
                        .functionType();

        /*
         * =================================================
         * Identity
         * =================================================
         */

        if (
                transformationResolution
                        .sourceSegment()
                        != sourceSegment
        ) {

            throw new IllegalArgumentException(
                    "Transformation Resolution이 현재 Annual-Decade Segment를 참조하지 않습니다."
            );
        }

        AnnualDecadeFunctionSegmentEffect expectedSegmentEffect =
                sourceSegment
                        .requireFunctionEffect(
                                resolvedFunctionType
                        );

        if (
                expectedSegmentEffect
                        != segmentFunctionEffect
        ) {

            throw new IllegalArgumentException(
                    "Annual Resolved Function Effect가 Segment Function Identity를 보존하지 않았습니다. function="
                            + resolvedFunctionType
            );
        }

        if (
                sourceSegment.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Source Segment와 Annual Resolved Function Effect의 completeChart가 일치하지 않습니다."
            );
        }

        if (
                transformationResolution.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Transformation Resolution과 Annual Resolved Function Effect의 completeChart가 일치하지 않습니다."
            );
        }

        if (
                segmentFunctionEffect.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Segment Function Effect와 Annual Resolved Function Effect의 completeChart가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Cross-Luck Target Coverage
         * =================================================
         *
         * 기존:
         *
         * transformationResolution
         *     .activeResolutionsTargeting(
         *         functionType()
         *     )
         *
         * 수정:
         *
         * compact constructor에서는
         * resolvedFunctionType 지역값 사용.
         */

        Set<AnnualDecadeTransformationParticipantResolution>
                expectedIncoming =
                new LinkedHashSet<>(
                        transformationResolution
                                .activeResolutionsTargeting(
                                        resolvedFunctionType
                                )
                );

        Set<AnnualDecadeTransformationParticipantResolution>
                actualIncoming =
                new LinkedHashSet<>(
                        incomingCrossLuckTargets
                );

        if (
                !expectedIncoming.equals(
                        actualIncoming
                )
        ) {

            throw new IllegalArgumentException(
                    "Cross-Luck Transformation Target Coverage가 일치하지 않습니다. function="
                            + resolvedFunctionType
            );
        }

        /*
         * =================================================
         * Activation Mode
         * =================================================
         */

        AnnualResolvedFunctionActivationMode expectedMode =
                AnnualResolvedFunctionActivationMode.of(
                        segmentFunctionEffect.decadeActive(),
                        segmentFunctionEffect.annualActive(),
                        !incomingCrossLuckTargets.isEmpty()
                );

        if (
                activationMode
                        != expectedMode
        ) {

            throw new IllegalArgumentException(
                    "Annual Resolved Function Activation Mode가 실제 Source와 일치하지 않습니다. "
                            + "function="
                            + resolvedFunctionType
                            + ", expected="
                            + expectedMode
                            + ", actual="
                            + activationMode
            );
        }

        /*
         * =================================================
         * Original Input State
         * =================================================
         */

        AnnualSegmentOriginalInputState expectedDecadeState =
                resolveDecadeOriginalInputState(
                        segmentFunctionEffect,
                        transformationResolution
                );

        if (
                decadeOriginalInputState
                        != expectedDecadeState
        ) {

            throw new IllegalArgumentException(
                    "대운 원기능 상태가 실제 Input Context와 일치하지 않습니다. "
                            + "function="
                            + resolvedFunctionType
                            + ", expected="
                            + expectedDecadeState
                            + ", actual="
                            + decadeOriginalInputState
            );
        }

        AnnualSegmentOriginalInputState expectedAnnualState =
                resolveAnnualOriginalInputState(
                        segmentFunctionEffect,
                        transformationResolution
                );

        if (
                annualOriginalInputState
                        != expectedAnnualState
        ) {

            throw new IllegalArgumentException(
                    "세운 원기능 상태가 실제 Input Context와 일치하지 않습니다. "
                            + "function="
                            + resolvedFunctionType
                            + ", expected="
                            + expectedAnnualState
                            + ", actual="
                            + annualOriginalInputState
            );
        }
    }

    /*
     * =================================================
     * Identity
     * =================================================
     */

    public int targetYear() {

        return sourceSegment
                .targetYear();
    }

    public int decadeSequenceNumber() {

        return sourceSegment
                .decadeSequenceNumber();
    }

    public NatalTenGodFunctionType functionType() {

        return segmentFunctionEffect
                .functionType();
    }

    /*
     * =================================================
     * Activation
     * =================================================
     */

    public boolean active() {

        return activationMode
                .active();
    }

    public boolean decadeBaselineActive() {

        return activationMode
                .decadeActive();
    }

    public boolean annualSelfActive() {

        return activationMode
                .annualActive();
    }

    public boolean crossLuckTargetActive() {

        return activationMode
                .crossLuckTargetActive();
    }

    public boolean crossLuckTargetOnly() {

        return activationMode
                .crossLuckTargetOnly();
    }

    public boolean multipleActivationSources() {

        return activationMode
                .multipleActivationSources();
    }

    /*
     * =================================================
     * Decade Source
     * =================================================
     */

    public DecadeResolvedFunctionEffect
    decadeFunctionEffect() {

        return segmentFunctionEffect
                .decadeFunctionEffect();
    }

    public boolean decadeOriginalInputActive() {

        return decadeFunctionEffect()
                .originalInputActive();
    }

    public boolean decadeTransformationTargetActive() {

        return decadeFunctionEffect()
                .transformedTargetActive();
    }

    /*
     * =================================================
     * Annual Source
     * =================================================
     */

    public AnnualFunctionCapacityContext
    annualFunctionContext() {

        return segmentFunctionEffect
                .annualFunctionContext();
    }

    public boolean annualDirectInputActive() {

        return annualFunctionContext()
                .directInputActive();
    }

    /**
     * 원국 × 세운 14-4 합화 Target으로
     * 활성된 Function.
     */
    public boolean annualNatalTransformationTargetActive() {

        return annualFunctionContext()
                .transformedTargetActive();
    }

    /*
     * =================================================
     * 14-5 Cross-Luck Target
     * =================================================
     */

    public List<AnnualDecadeLuckTransformationReference>
    distinctIncomingCrossLuckTransformations() {

        Set<AnnualDecadeLuckTransformationReference> result =
                new LinkedHashSet<>();

        for (
                AnnualDecadeTransformationParticipantResolution resolution
                : incomingCrossLuckTargets
        ) {

            result.add(
                    resolution
                            .sourceTransformation()
            );
        }

        return List.copyOf(
                result
        );
    }

    public boolean strongCrossLuckTargetActive() {

        return incomingCrossLuckTargets
                .stream()
                .anyMatch(
                        AnnualDecadeTransformationParticipantResolution
                                ::strongTargetActivation
                );
    }

    /*
     * =================================================
     * Cross-Luck Source Reduction
     * =================================================
     */

    public boolean crossLuckDecadeSourceReductionExists() {

        return transformationResolution
                .decadeSourceFunctionReductionExists(
                        functionType()
                );
    }

    public boolean crossLuckDecadeSourceStrongReductionExists() {

        return transformationResolution
                .decadeSourceFunctionStrongReductionExists(
                        functionType()
                );
    }

    public boolean crossLuckAnnualSourceReductionExists() {

        return transformationResolution
                .annualSourceFunctionReductionExists(
                        functionType()
                );
    }

    public boolean crossLuckAnnualSourceStrongReductionExists() {

        return transformationResolution
                .annualSourceFunctionStrongReductionExists(
                        functionType()
                );
    }

    /*
     * =================================================
     * Resolved Original State
     * =================================================
     */

    public boolean decadeOriginalInputPresent() {

        return decadeOriginalInputState
                .present();
    }

    public boolean decadeOriginalInputReduced() {

        return decadeOriginalInputState
                .reduced();
    }

    public boolean decadeOriginalInputStronglyReduced() {

        return decadeOriginalInputState
                .stronglyReduced();
    }

    public boolean decadeOriginalInputMixed() {

        return decadeOriginalInputState
                .mixed();
    }

    public boolean annualOriginalInputPresent() {

        return annualOriginalInputState
                .present();
    }

    public boolean annualOriginalInputReduced() {

        return annualOriginalInputState
                .reduced();
    }

    public boolean annualOriginalInputStronglyReduced() {

        return annualOriginalInputState
                .stronglyReduced();
    }

    public boolean annualOriginalInputMixed() {

        return annualOriginalInputState
                .mixed();
    }

    /*
     * =================================================
     * Continuation / New Activation
     * =================================================
     */

    public boolean decadeAnnualContinuation() {

        return decadeBaselineActive()
                && annualSelfActive();
    }

    /**
     * 대운에는 없던 Function이
     * 세운 자체 또는 14-5 Target으로
     * 새로 활성.
     */
    public boolean newAgainstDecadeBaseline() {

        return !decadeBaselineActive()

                && (
                annualSelfActive()
                        || crossLuckTargetActive()
        );
    }

    /**
     * 이미 대운에서 활성된 Function에
     * 14-5 Transformation Target도 겹침.
     */
    public boolean crossLuckTargetJoinsDecadeBaseline() {

        return decadeBaselineActive()
                && crossLuckTargetActive();
    }

    /*
     * =================================================
     * Fixed Natal Capacity
     * =================================================
     */

    public DayMasterStrengthLevel natalStrengthLevel() {

        return annualFunctionContext()
                .natalStrengthLevel();
    }

    public boolean natalStrongSide() {

        return annualFunctionContext()
                .natalStrongSide();
    }

    public boolean natalWeakSide() {

        return annualFunctionContext()
                .natalWeakSide();
    }

    public boolean natalBalancedZone() {

        return annualFunctionContext()
                .natalBalancedZone();
    }

    public boolean natalExtreme() {

        return annualFunctionContext()
                .natalExtreme();
    }

    public boolean followStructureReviewRequired() {

        return annualFunctionContext()
                .followStructureReviewRequired();
    }

    public boolean supportSide() {

        return annualFunctionContext()
                .supportSide();
    }

    public boolean pressureSide() {

        return annualFunctionContext()
                .pressureSide();
    }

    /**
     * Cross-Luck Target까지 포함한
     * 최종 활성 상태 기준.
     */
    public boolean resolvedStrongSideSupportContact() {

        return active()
                && natalStrongSide()
                && supportSide();
    }

    public boolean resolvedWeakSidePressureContact() {

        return active()
                && natalWeakSide()
                && pressureSide();
    }

    public boolean resolvedStrongSidePressureContact() {

        return active()
                && natalStrongSide()
                && pressureSide();
    }

    public boolean resolvedWeakSideSupportContact() {

        return active()
                && natalWeakSide()
                && supportSide();
    }

    public boolean sameDirectionAccumulationReviewRequired() {

        return resolvedStrongSideSupportContact()
                || resolvedWeakSidePressureContact();
    }

    public boolean counterDirectionContact() {

        return resolvedStrongSidePressureContact()
                || resolvedWeakSideSupportContact();
    }

    /*
     * =================================================
     * 14-5 Relation Contact
     * =================================================
     *
     * 관계 자체는 대운 글자 ↔ 세운 글자 관계다.
     *
     * 현재 Function이 그 Layer에 실제 Source Input을
     * 가지고 있을 때만 Function Context로 연결한다.
     */

    public boolean crossLuckRelationContactExists() {

        return sourceSegment
                .crossLuckRelations()
                .stream()
                .anyMatch(
                        relation ->
                                functionParticipatesInLayer(
                                        relation.layer()
                                )
                );
    }

    public boolean restrictiveCrossLuckRelationContactExists() {

        return sourceSegment
                .crossLuckRelations()
                .stream()
                .anyMatch(
                        relation ->
                                relation.restrictiveInteraction()

                                        && functionParticipatesInLayer(
                                        relation.layer()
                                )
                );
    }

    /**
     * 대운 천간이 세운 천간을 극하고,
     * 현재 Function이 세운 Visible Source로
     * 실제 참여하는가.
     */
    public boolean decadeControlsAnnualFunctionContact() {

        if (
                !annualVisibleSourceExists()
        ) {

            return false;
        }

        return sourceSegment
                .crossLuckRelations()
                .stream()
                .anyMatch(
                        relation ->
                                relation
                                        .decadeControlsAnnual()
                );
    }

    /**
     * 세운 천간이 대운 천간을 극하고,
     * 현재 Function이 대운 Visible Source로
     * 실제 참여하는가.
     */
    public boolean annualControlsDecadeFunctionContact() {

        if (
                !decadeVisibleSourceExists()
        ) {

            return false;
        }

        return sourceSegment
                .crossLuckRelations()
                .stream()
                .anyMatch(
                        relation ->
                                relation
                                        .annualControlsDecade()
                );
    }

    /*
     * =================================================
     * Timing
     * =================================================
     */

    public boolean timingUncertain() {

        return sourceSegment
                .timingUncertain();
    }

    /*
     * =================================================
     * Qualification
     * =================================================
     */

    public boolean qualificationRequired() {

        if (
                !active()
        ) {

            return false;
        }

        return timingUncertain()

                || multipleActivationSources()

                || crossLuckTargetActive()

                || decadeOriginalInputReduced()

                || annualOriginalInputReduced()

                || restrictiveCrossLuckRelationContactExists()

                || sameDirectionAccumulationReviewRequired()

                || natalBalancedZone()

                || natalExtreme()

                || followStructureReviewRequired();
    }

    public boolean capacityReviewRequired() {

        if (
                !active()
        ) {

            return false;
        }

        return sameDirectionAccumulationReviewRequired()

                || natalExtreme()

                || followStructureReviewRequired()

                || crossLuckTargetActive()

                || decadeOriginalInputReduced()

                || annualOriginalInputReduced();
    }

    /*
     * =================================================
     * Original Input State Resolver
     * =================================================
     */

    public static AnnualSegmentOriginalInputState
    resolveDecadeOriginalInputState(

            AnnualDecadeFunctionSegmentEffect segmentFunctionEffect,

            AnnualDecadeTransformationResolution transformationResolution

    ) {

        Objects.requireNonNull(
                segmentFunctionEffect,
                "대운 원기능 상태 판정에는 Segment Function Effect가 필요합니다."
        );

        Objects.requireNonNull(
                transformationResolution,
                "대운 원기능 상태 판정에는 Transformation Resolution이 필요합니다."
        );

        List<DecadeFunctionInputOperation> inputs =
                segmentFunctionEffect
                        .decadeFunctionEffect()
                        .sourceOperation()
                        .inputOperations();

        if (
                inputs.isEmpty()
        ) {

            return AnnualSegmentOriginalInputState
                    .ABSENT;
        }

        Set<AnnualSegmentOriginalInputState> states =
                new LinkedHashSet<>();

        for (
                DecadeFunctionInputOperation input
                : inputs
        ) {

            boolean contextExists =
                    false;

            boolean reduced =
                    false;

            boolean stronglyReduced =
                    false;

            boolean retained =
                    false;

            /*
             * =================================================
             * 대운 자체 Natal × Decade Transformation
             * =================================================
             */

            for (
                    DecadeLuckNatalTransformationReference transformation
                    : input.transformationContexts()
            ) {

                contextExists =
                        true;

                if (
                        transformation
                                .luckOriginalFunctionReduced()
                ) {

                    reduced =
                            true;

                    if (
                            transformation
                                    .luckOriginalFunctionStronglyReduced()
                    ) {

                        stronglyReduced =
                                true;
                    }

                } else {

                    retained =
                            true;
                }
            }

            /*
             * =================================================
             * 14-5 Decade × Annual Transformation
             * =================================================
             */

            for (
                    AnnualDecadeTransformationParticipantResolution resolution
                    : transformationResolution
                    .transformationResolutions()
            ) {

                if (
                        !resolution
                                .decadeSourceInputs()
                                .contains(
                                        input
                                )
                ) {

                    continue;
                }

                contextExists =
                        true;

                if (
                        resolution
                                .decadeOriginalFunctionReduced()
                ) {

                    reduced =
                            true;

                    if (
                            resolution
                                    .decadeOriginalFunctionStronglyReduced()
                    ) {

                        stronglyReduced =
                                true;
                    }

                } else {

                    retained =
                            true;
                }
            }

            if (
                    !contextExists
            ) {

                retained =
                        true;
            }

            states.add(
                    resolveSingleInputState(
                            reduced,
                            stronglyReduced,
                            retained
                    )
            );
        }

        return mergeInputStates(
                states
        );
    }

    public static AnnualSegmentOriginalInputState
    resolveAnnualOriginalInputState(

            AnnualDecadeFunctionSegmentEffect segmentFunctionEffect,

            AnnualDecadeTransformationResolution transformationResolution

    ) {

        Objects.requireNonNull(
                segmentFunctionEffect,
                "세운 원기능 상태 판정에는 Segment Function Effect가 필요합니다."
        );

        Objects.requireNonNull(
                transformationResolution,
                "세운 원기능 상태 판정에는 Transformation Resolution이 필요합니다."
        );

        List<AnnualFunctionInputOperation> inputs =
                segmentFunctionEffect
                        .annualFunctionContext()
                        .sourceMigration()
                        .sourceEffect()
                        .inputOperations();

        if (
                inputs.isEmpty()
        ) {

            return AnnualSegmentOriginalInputState
                    .ABSENT;
        }

        Set<AnnualSegmentOriginalInputState> states =
                new LinkedHashSet<>();

        for (
                AnnualFunctionInputOperation input
                : inputs
        ) {

            boolean contextExists =
                    false;

            boolean reduced =
                    false;

            boolean stronglyReduced =
                    false;

            boolean retained =
                    false;

            /*
             * =================================================
             * 14-4 Natal × Annual Transformation
             * =================================================
             */

            for (
                    AnnualLuckNatalTransformationReference transformation
                    : input.natalTransformations()
            ) {

                contextExists =
                        true;

                if (
                        transformation
                                .annualOriginalFunctionReduced()
                ) {

                    reduced =
                            true;

                    if (
                            transformation
                                    .annualOriginalFunctionStronglyReduced()
                    ) {

                        stronglyReduced =
                                true;
                    }

                } else {

                    retained =
                            true;
                }
            }

            /*
             * =================================================
             * 14-5 Decade × Annual Transformation
             * =================================================
             */

            for (
                    AnnualDecadeTransformationParticipantResolution resolution
                    : transformationResolution
                    .transformationResolutions()
            ) {

                if (
                        !resolution
                                .annualSourceInputs()
                                .contains(
                                        input
                                )
                ) {

                    continue;
                }

                contextExists =
                        true;

                if (
                        resolution
                                .annualOriginalFunctionReduced()
                ) {

                    reduced =
                            true;

                    if (
                            resolution
                                    .annualOriginalFunctionStronglyReduced()
                    ) {

                        stronglyReduced =
                                true;
                    }

                } else {

                    retained =
                            true;
                }
            }

            if (
                    !contextExists
            ) {

                retained =
                        true;
            }

            states.add(
                    resolveSingleInputState(
                            reduced,
                            stronglyReduced,
                            retained
                    )
            );
        }

        return mergeInputStates(
                states
        );
    }

    private static AnnualSegmentOriginalInputState
    resolveSingleInputState(

            boolean reduced,

            boolean stronglyReduced,

            boolean retained

    ) {

        if (
                reduced
                        && retained
        ) {

            return AnnualSegmentOriginalInputState
                    .MIXED;
        }

        if (
                stronglyReduced
        ) {

            return AnnualSegmentOriginalInputState
                    .STRONGLY_REDUCED;
        }

        if (
                reduced
        ) {

            return AnnualSegmentOriginalInputState
                    .REDUCED;
        }

        return AnnualSegmentOriginalInputState
                .RETAINED;
    }

    private static AnnualSegmentOriginalInputState
    mergeInputStates(

            Set<AnnualSegmentOriginalInputState> states

    ) {

        if (
                states.isEmpty()
        ) {

            return AnnualSegmentOriginalInputState
                    .ABSENT;
        }

        if (
                states.size()
                        == 1
        ) {

            return states
                    .iterator()
                    .next();
        }

        return AnnualSegmentOriginalInputState
                .MIXED;
    }

    /*
     * =================================================
     * Layer Helpers
     * =================================================
     */

    private boolean functionParticipatesInLayer(
            AnnualDecadeLuckRelationLayer layer
    ) {

        if (
                layer
                        == AnnualDecadeLuckRelationLayer
                        .HEAVENLY_STEM
        ) {

            return decadeVisibleSourceExists()
                    || annualVisibleSourceExists();
        }

        if (
                layer
                        == AnnualDecadeLuckRelationLayer
                        .EARTHLY_BRANCH
        ) {

            return decadeHiddenSourceExists()
                    || annualHiddenSourceExists();
        }

        return false;
    }

    private boolean decadeVisibleSourceExists() {

        return decadeFunctionEffect()
                .sourceOperation()
                .visibleInputOperation()
                .isPresent();
    }

    private boolean decadeHiddenSourceExists() {

        return !decadeFunctionEffect()
                .sourceOperation()
                .hiddenInputOperations()
                .isEmpty();
    }

    private boolean annualVisibleSourceExists() {

        return !annualFunctionContext()
                .sourceMigration()
                .sourceEffect()
                .visibleInputOperations()
                .isEmpty();
    }

    private boolean annualHiddenSourceExists() {

        return !annualFunctionContext()
                .sourceMigration()
                .sourceEffect()
                .hiddenInputOperations()
                .isEmpty();
    }
}