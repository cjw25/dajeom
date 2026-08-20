package com.dajeom.model;

import com.dajeom.type.AnnualCrossFunctionalContactLevel;
import com.dajeom.type.AnnualCrossFunctionalResolutionType;
import com.dajeom.type.NatalCrossFunctionalInteractionType;
import com.dajeom.type.NatalCrossFunctionalPatternType;
import com.dajeom.type.NatalCrossFunctionalScope;
import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.TenGodCorePatternLevel;
import com.dajeom.type.TenGodCrossValidationStatus;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 하나의 원국 CrossFunctional Pattern에 대해
 * 특정 Annual-Decade Segment가
 * 어떤 시간적 접촉을 만드는지 나타낸다.
 *
 * =================================================
 *
 * 기준선:
 *
 * DecadeCrossFunctionalSynthesis
 *
 * =================================================
 *
 * 현재 Segment:
 *
 * AnnualResolvedFunctionalSegmentAnalysis
 *
 * =================================================
 *
 * Participant:
 *
 * AnnualResolvedFunctionEffect
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 1.
 * 원국 Pattern 자체는 절대 변경하지 않는다.
 *
 * 2.
 * 원국 REFERENCE Pattern은
 * 세운에서 Full Contact가 되어도
 * 강한 Pattern으로 승격하지 않는다.
 *
 * 3.
 * 대운에서 이미 Full Contact였다면
 * 세운에서 같은 Function이 다시 들어와도
 * 새로운 Pattern 완성으로 세지 않는다.
 *
 * 4.
 * Transformation Target은
 * Participant 활성 provenance 중 하나일 뿐,
 * 독립 Pattern Evidence를 하나 더 추가하는 것이 아니다.
 *
 * 5.
 * FUNCTION_TO_CORE_SELF에서
 * CoreSelf는 Natal-fixed Anchor다.
 *
 * 6.
 * WITHIN_FUNCTION에서는
 * 하나의 Function만 Participant다.
 */
public record AnnualCrossFunctionalPatternEffect(

        DecadeCrossFunctionalSynthesis
        decadeSynthesis,

        AnnualResolvedFunctionalSegmentAnalysis
        resolvedSegment,

        List<AnnualResolvedFunctionEffect>
        participantEffects,

        AnnualCrossFunctionalContactLevel
        contactLevel,

        AnnualCrossFunctionalResolutionType
        resolutionType,

        boolean completeChart

) {

    public AnnualCrossFunctionalPatternEffect {

        /*
         * =================================================
         * Compact Constructor 주의
         * =================================================
         *
         * record compact constructor 본문이 실행되는 동안에는
         * record field의 최종 대입이 아직 완료되지 않았다.
         *
         * 따라서 이 생성자 내부에서는:
         *
         * patternType()
         * scope()
         * primaryParticipant()
         * secondaryParticipant()
         *
         * 또는 위 메서드들을 내부적으로 호출하는
         * instance helper를 사용하면 안 된다.
         *
         * 반드시 생성자 매개변수에서 직접 필요한 값을
         * 확정해서 사용한다.
         */

        Objects.requireNonNull(
                decadeSynthesis,
                "Annual CrossFunctional Effect에는 Decade CrossFunctional Synthesis가 필요합니다."
        );

        Objects.requireNonNull(
                resolvedSegment,
                "Annual CrossFunctional Effect에는 Annual Resolved Segment가 필요합니다."
        );

        participantEffects =
                participantEffects == null
                        ? List.of()
                        : List.copyOf(
                        participantEffects
                );

        /*
         * =================================================
         * Lambda-safe normalized participant list
         * =================================================
         *
         * participantEffects 생성자 매개변수는 위에서
         * 정규화를 위해 재대입되었으므로 effectively final이 아니다.
         *
         * 이후 람다 내부에서 안전하게 사용하기 위해
         * 재대입하지 않는 지역변수로 고정한다.
         */

        List<AnnualResolvedFunctionEffect> normalizedParticipantEffects =
                participantEffects;

        Objects.requireNonNull(
                contactLevel,
                "Annual CrossFunctional Effect에는 Contact Level이 필요합니다."
        );

        Objects.requireNonNull(
                resolutionType,
                "Annual CrossFunctional Effect에는 Resolution Type이 필요합니다."
        );

        /*
         * =================================================
         * Constructor-local Pattern Identity
         * =================================================
         */

        NatalCrossFunctionalPatternType resolvedPatternType =
                Objects.requireNonNull(
                        decadeSynthesis.patternType(),
                        "Annual CrossFunctional Effect에는 Pattern Type이 필요합니다."
                );

        NatalCrossFunctionalScope resolvedScope =
                Objects.requireNonNull(
                        resolvedPatternType.scope(),
                        "Annual CrossFunctional Effect에는 Pattern Scope가 필요합니다."
                );

        if (
                decadeSynthesis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Decade CrossFunctional Synthesis와 Annual Cross Effect의 completeChart가 일치하지 않습니다."
            );
        }

        if (
                resolvedSegment.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Annual Resolved Segment와 Cross Effect의 completeChart가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Decade Synthesis Identity
         * =================================================
         */

        DecadeCrossFunctionalSynthesis expectedSynthesis =
                resolvedSegment
                        .sourceSegment()
                        .decadeBaseline()
                        .requireSynthesis(
                                resolvedPatternType
                        );

        if (
                expectedSynthesis
                        != decadeSynthesis
        ) {

            throw new IllegalArgumentException(
                    "Annual CrossFunctional Effect가 현재 대운 Segment의 Cross Synthesis Identity를 보존하지 않았습니다. pattern="
                            + resolvedPatternType
            );
        }

        /*
         * =================================================
         * Participant 구성
         * =================================================
         */

        if (
                resolvedScope
                        == NatalCrossFunctionalScope
                        .BETWEEN_FUNCTIONS
        ) {

            validateBetweenFunctionsParticipants(
                    resolvedPatternType,
                    normalizedParticipantEffects
            );

        } else {

            validateSingleFunctionParticipant(
                    resolvedPatternType,
                    normalizedParticipantEffects
            );
        }

        for (
                AnnualResolvedFunctionEffect participant
                : normalizedParticipantEffects
        ) {

            Objects.requireNonNull(
                    participant,
                    "Annual CrossFunctional Participant는 null일 수 없습니다."
            );

            if (
                    participant.sourceSegment()
                            != resolvedSegment
                            .sourceSegment()
            ) {

                throw new IllegalArgumentException(
                        "Annual CrossFunctional Participant가 현재 Segment를 참조하지 않습니다."
                );
            }

            if (
                    participant.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Annual CrossFunctional Participant의 completeChart가 일치하지 않습니다."
                );
            }
        }

        /*
         * =================================================
         * Constructor-local Participant
         * =================================================
         */

        NatalTenGodFunctionType primaryFunction =
                resolvedPatternType
                        .primaryFunction();

        AnnualResolvedFunctionEffect resolvedPrimaryParticipant =
                normalizedParticipantEffects
                        .stream()
                        .filter(
                                participant ->
                                        participant
                                                .functionType()
                                                == primaryFunction
                        )
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Annual CrossFunctional Primary Participant가 없습니다. pattern="
                                                        + resolvedPatternType
                                        )
                        );

        Optional<NatalTenGodFunctionType> secondaryFunction =
                resolvedPatternType
                        .secondaryFunction();

        Optional<AnnualResolvedFunctionEffect> resolvedSecondaryParticipant =
                secondaryFunction
                        .flatMap(
                                function ->
                                        normalizedParticipantEffects
                                                .stream()
                                                .filter(
                                                        participant ->
                                                                participant
                                                                        .functionType()
                                                                        == function
                                                )
                                                .findFirst()
                        );

        /*
         * =================================================
         * Decade Participant ↔ Annual Decade Baseline
         * =================================================
         */

        DecadeCrossFunctionalEffect decadeEffect =
                decadeSynthesis
                        .sourceEffect();

        if (
                resolvedPrimaryParticipant
                        .decadeBaselineActive()
                        != decadeEffect
                        .primaryParticipant()
                        .active()
        ) {

            throw new IllegalArgumentException(
                    "Primary Function의 대운 활성 상태가 Decade Cross Effect와 Annual Resolved Effect 사이에서 일치하지 않습니다. pattern="
                            + resolvedPatternType
            );
        }

        if (
                resolvedScope
                        == NatalCrossFunctionalScope
                        .BETWEEN_FUNCTIONS
        ) {

            boolean decadeSecondaryActive =
                    decadeEffect
                            .secondaryParticipant()
                            .orElseThrow(
                                    () ->
                                            new IllegalArgumentException(
                                                    "BETWEEN_FUNCTIONS Pattern의 Decade Secondary Participant가 없습니다."
                                            )
                            )
                            .active();

            AnnualResolvedFunctionEffect annualSecondaryParticipant =
                    resolvedSecondaryParticipant
                            .orElseThrow(
                                    () ->
                                            new IllegalArgumentException(
                                                    "BETWEEN_FUNCTIONS Pattern의 Annual Secondary Participant가 없습니다. pattern="
                                                            + resolvedPatternType
                                            )
                            );

            if (
                    annualSecondaryParticipant
                            .decadeBaselineActive()
                            != decadeSecondaryActive
            ) {

                throw new IllegalArgumentException(
                        "Secondary Function의 대운 활성 상태가 Decade Cross Effect와 Annual Resolved Effect 사이에서 일치하지 않습니다. pattern="
                                + resolvedPatternType
                );
            }
        }

        /*
         * =================================================
         * Contact Level 검증
         * =================================================
         */

        AnnualCrossFunctionalContactLevel expectedContact =
                resolveContactLevel(
                        resolvedScope,
                        normalizedParticipantEffects
                );

        if (
                contactLevel
                        != expectedContact
        ) {

            throw new IllegalArgumentException(
                    "Annual CrossFunctional Contact Level이 실제 Participant 상태와 일치하지 않습니다. "
                            + "pattern="
                            + resolvedPatternType
                            + ", expected="
                            + expectedContact
                            + ", actual="
                            + contactLevel
            );
        }

        /*
         * =================================================
         * Resolution Type 검증
         * =================================================
         */

        AnnualCrossFunctionalResolutionType expectedResolution =
                resolveResolutionType(
                        decadeSynthesis,
                        normalizedParticipantEffects,
                        expectedContact
                );

        if (
                resolutionType
                        != expectedResolution
        ) {

            throw new IllegalArgumentException(
                    "Annual CrossFunctional Resolution Type이 실제 시간 구조와 일치하지 않습니다. "
                            + "pattern="
                            + resolvedPatternType
                            + ", expected="
                            + expectedResolution
                            + ", actual="
                            + resolutionType
            );
        }
    }

    /*
     * =================================================
     * Source Pattern
     * =================================================
     */

    public NatalCrossFunctionalPattern sourcePattern() {

        return decadeSynthesis
                .sourceEffect()
                .sourcePattern();
    }

    public NatalCrossFunctionalPatternType patternType() {

        return decadeSynthesis
                .patternType();
    }

    public NatalCrossFunctionalScope scope() {

        return patternType()
                .scope();
    }

    public NatalCrossFunctionalInteractionType interactionType() {

        return patternType()
                .interactionType();
    }

    public TenGodCorePatternLevel sourceLevel() {

        return decadeSynthesis
                .sourceEffect()
                .sourceLevel();
    }

    public TenGodCrossValidationStatus validationStatus() {

        return decadeSynthesis
                .sourceEffect()
                .validationStatus();
    }

    public boolean structurallyEffectiveNatalPattern() {

        return decadeSynthesis
                .sourceEffect()
                .structurallyEffectiveNatalPattern();
    }

    public boolean strongNatalPattern() {

        return decadeSynthesis
                .sourceEffect()
                .strongNatalPattern();
    }

    /*
     * =================================================
     * Participant
     * =================================================
     */

    public AnnualResolvedFunctionEffect
    primaryParticipant() {

        NatalTenGodFunctionType primary =
                patternType()
                        .primaryFunction();

        return participantEffects
                .stream()
                .filter(
                        participant ->
                                participant
                                        .functionType()
                                        == primary
                )
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Annual CrossFunctional Primary Participant가 없습니다. pattern="
                                                + patternType()
                                )
                );
    }

    public Optional<AnnualResolvedFunctionEffect>
    secondaryParticipant() {

        Optional<NatalTenGodFunctionType> secondary =
                patternType()
                        .secondaryFunction();

        if (
                secondary.isEmpty()
        ) {

            return Optional.empty();
        }

        return participantEffects
                .stream()
                .filter(
                        participant ->
                                participant
                                        .functionType()
                                        == secondary.get()
                )
                .findFirst();
    }

    /*
     * =================================================
     * Contact
     * =================================================
     */

    public boolean temporalContactExists() {

        return contactLevel
                .contact();
    }

    public boolean partialPatternContact() {

        return contactLevel
                .partialContact();
    }

    public boolean fullPatternContact() {

        return contactLevel
                .fullPatternContact();
    }

    public boolean effectiveFullPatternContact() {

        return structurallyEffectiveNatalPattern()
                && fullPatternContact();
    }

    public boolean referencePatternContact() {

        return temporalContactExists()
                && !structurallyEffectiveNatalPattern();
    }

    public boolean referenceOnly() {

        return referencePatternContact();
    }

    /*
     * =================================================
     * Resolution
     * =================================================
     */

    public boolean decadeFullContinuation() {

        return resolutionType
                .continuation();
    }

    public boolean newlyCompleted() {

        return resolutionType
                .newlyCompleted();
    }

    public boolean annualCompletion() {

        return resolutionType
                == AnnualCrossFunctionalResolutionType
                .ANNUAL_COMPLETION;
    }

    public boolean crossLayerCompletion() {

        return resolutionType
                == AnnualCrossFunctionalResolutionType
                .CROSS_LAYER_COMPLETION;
    }

    public boolean transformationCompletion() {

        return resolutionType
                .transformationDependentCompletion();
    }

    public boolean effectiveNewCompletionCandidate() {

        return newlyCompleted()
                && structurallyEffectiveNatalPattern();
    }

    /*
     * =================================================
     * Decade Contact
     * =================================================
     */

    public boolean decadeTemporalContactExists() {

        return decadeSynthesis
                .temporalContactExists();
    }

    public boolean decadeFullPatternContact() {

        return decadeSynthesis
                .fullPatternContact();
    }

    public boolean decadePartialPatternContact() {

        return decadeSynthesis
                .partialPatternContact();
    }

    /*
     * =================================================
     * Annual Self Contact
     * =================================================
     */

    public boolean primaryAnnualSelfActive() {

        return primaryParticipant()
                .annualSelfActive();
    }

    public boolean secondaryAnnualSelfActive() {

        return secondaryParticipant()
                .map(
                        AnnualResolvedFunctionEffect
                                ::annualSelfActive
                )
                .orElse(
                        false
                );
    }

    public boolean annualSelfFullParticipantContact() {

        if (
                scope()
                        != NatalCrossFunctionalScope
                        .BETWEEN_FUNCTIONS
        ) {

            return primaryAnnualSelfActive();
        }

        return primaryAnnualSelfActive()
                && secondaryAnnualSelfActive();
    }

    /*
     * =================================================
     * 14-5 Transformation Target
     * =================================================
     */

    public boolean crossLuckTargetParticipantContact() {

        return participantEffects
                .stream()
                .anyMatch(
                        AnnualResolvedFunctionEffect
                                ::crossLuckTargetActive
                );
    }

    public boolean crossLuckTargetOnlyParticipantExists() {

        return participantEffects
                .stream()
                .anyMatch(
                        AnnualResolvedFunctionEffect
                                ::crossLuckTargetOnly
                );
    }

    /*
     * =================================================
     * Source Reduction
     * =================================================
     */

    public boolean decadeOriginalInputReductionExists() {

        return participantEffects
                .stream()
                .filter(
                        AnnualResolvedFunctionEffect
                                ::active
                )
                .anyMatch(
                        AnnualResolvedFunctionEffect
                                ::decadeOriginalInputReduced
                );
    }

    public boolean annualOriginalInputReductionExists() {

        return participantEffects
                .stream()
                .filter(
                        AnnualResolvedFunctionEffect
                                ::active
                )
                .anyMatch(
                        AnnualResolvedFunctionEffect
                                ::annualOriginalInputReduced
                );
    }

    public boolean mixedOriginalInputStateExists() {

        return participantEffects
                .stream()
                .filter(
                        AnnualResolvedFunctionEffect
                                ::active
                )
                .anyMatch(
                        participant ->
                                participant
                                        .decadeOriginalInputMixed()

                                        || participant
                                        .annualOriginalInputMixed()
                );
    }

    /*
     * =================================================
     * Capacity / Relation
     * =================================================
     */

    public boolean capacityReviewRequired() {

        return participantEffects
                .stream()
                .filter(
                        AnnualResolvedFunctionEffect
                                ::active
                )
                .anyMatch(
                        AnnualResolvedFunctionEffect
                                ::capacityReviewRequired
                );
    }

    public boolean restrictiveCrossLuckRelationExists() {

        return participantEffects
                .stream()
                .filter(
                        AnnualResolvedFunctionEffect
                                ::active
                )
                .anyMatch(
                        AnnualResolvedFunctionEffect
                                ::restrictiveCrossLuckRelationContactExists
                );
    }

    /*
     * =================================================
     * Timing
     * =================================================
     */

    public boolean timingUncertain() {

        return resolvedSegment
                .timingUncertain();
    }

    /*
     * =================================================
     * Qualification
     * =================================================
     */

    public boolean qualificationRequired() {

        if (
                !temporalContactExists()
        ) {

            return false;
        }

        return referencePatternContact()

                || partialPatternContact()

                || decadeSynthesis
                .qualificationRequired()

                || crossLuckTargetParticipantContact()

                || decadeOriginalInputReductionExists()

                || annualOriginalInputReductionExists()

                || mixedOriginalInputStateExists()

                || capacityReviewRequired()

                || restrictiveCrossLuckRelationExists()

                || timingUncertain()

                || participantEffects
                .stream()
                .filter(
                        AnnualResolvedFunctionEffect
                                ::active
                )
                .anyMatch(
                        AnnualResolvedFunctionEffect
                                ::qualificationRequired
                );
    }

    public boolean usableAsEffectiveAnnualPatternContact() {

        return effectiveFullPatternContact();
    }

    /*
     * =================================================
     * Validation
     * =================================================
     */

    private static void validateBetweenFunctionsParticipants(

            NatalCrossFunctionalPatternType patternType,

            List<AnnualResolvedFunctionEffect> participants

    ) {

        Objects.requireNonNull(
                patternType,
                "BETWEEN_FUNCTIONS Participant 검증에는 Pattern Type이 필요합니다."
        );

        if (
                participants.size()
                        != 2
        ) {

            throw new IllegalArgumentException(
                    "BETWEEN_FUNCTIONS Annual Pattern에는 정확히 2개의 Function Participant가 필요합니다."
            );
        }

        NatalTenGodFunctionType primary =
                patternType
                        .primaryFunction();

        NatalTenGodFunctionType secondary =
                patternType
                        .secondaryFunction()
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "BETWEEN_FUNCTIONS Pattern에 Secondary Function 정의가 없습니다."
                                        )
                        );

        long primaryCount =
                participants
                        .stream()
                        .filter(
                                participant ->
                                        participant
                                                .functionType()
                                                == primary
                        )
                        .count();

        long secondaryCount =
                participants
                        .stream()
                        .filter(
                                participant ->
                                        participant
                                                .functionType()
                                                == secondary
                        )
                        .count();

        if (
                primaryCount != 1
                        || secondaryCount != 1
        ) {

            throw new IllegalArgumentException(
                    "BETWEEN_FUNCTIONS Annual Pattern의 Primary/Secondary Participant 구성이 잘못되었습니다. "
                            + "pattern="
                            + patternType
            );
        }
    }

    private static void validateSingleFunctionParticipant(

            NatalCrossFunctionalPatternType patternType,

            List<AnnualResolvedFunctionEffect> participants

    ) {

        Objects.requireNonNull(
                patternType,
                "단일 Function Participant 검증에는 Pattern Type이 필요합니다."
        );

        if (
                participants.size()
                        != 1
        ) {

            throw new IllegalArgumentException(
                    "FUNCTION_TO_CORE_SELF / WITHIN_FUNCTION Annual Pattern에는 정확히 하나의 Function Participant가 필요합니다."
            );
        }

        if (
                participants
                        .get(0)
                        .functionType()
                        != patternType
                        .primaryFunction()
        ) {

            throw new IllegalArgumentException(
                    "단일 Function Annual Pattern의 Participant Function이 정의와 다릅니다. pattern="
                            + patternType
            );
        }
    }

    /*
     * =================================================
     * Contact Resolver
     * =================================================
     */

    public static AnnualCrossFunctionalContactLevel
    resolveContactLevel(

            NatalCrossFunctionalScope scope,

            List<AnnualResolvedFunctionEffect> participants

    ) {

        Objects.requireNonNull(
                scope,
                "Annual CrossFunctional Contact 판정에는 Scope가 필요합니다."
        );

        if (
                participants == null
                        || participants.isEmpty()
        ) {

            return AnnualCrossFunctionalContactLevel
                    .NO_CONTACT;
        }

        AnnualResolvedFunctionEffect primary =
                participants.get(
                        0
                );

        if (
                scope
                        == NatalCrossFunctionalScope
                        .FUNCTION_TO_CORE_SELF
        ) {

            return primary.active()
                    ? AnnualCrossFunctionalContactLevel
                    .FUNCTION_TO_CORE_SELF
                    : AnnualCrossFunctionalContactLevel
                    .NO_CONTACT;
        }

        if (
                scope
                        == NatalCrossFunctionalScope
                        .WITHIN_FUNCTION
        ) {

            return primary.active()
                    ? AnnualCrossFunctionalContactLevel
                    .WITHIN_FUNCTION
                    : AnnualCrossFunctionalContactLevel
                    .NO_CONTACT;
        }

        if (
                participants.size()
                        != 2
        ) {

            throw new IllegalArgumentException(
                    "BETWEEN_FUNCTIONS Contact 판정에는 2개 Participant가 필요합니다."
            );
        }

        AnnualResolvedFunctionEffect secondary =
                participants.get(
                        1
                );

        if (
                primary.active()
                        && secondary.active()
        ) {

            return AnnualCrossFunctionalContactLevel
                    .BOTH_FUNCTIONS;
        }

        if (
                primary.active()
        ) {

            return AnnualCrossFunctionalContactLevel
                    .PRIMARY_ONLY;
        }

        if (
                secondary.active()
        ) {

            return AnnualCrossFunctionalContactLevel
                    .SECONDARY_ONLY;
        }

        return AnnualCrossFunctionalContactLevel
                .NO_CONTACT;
    }

    /*
     * =================================================
     * Resolution Type Resolver
     * =================================================
     */

    public static AnnualCrossFunctionalResolutionType
    resolveResolutionType(

            DecadeCrossFunctionalSynthesis decadeSynthesis,

            List<AnnualResolvedFunctionEffect> participants,

            AnnualCrossFunctionalContactLevel contactLevel

    ) {

        Objects.requireNonNull(
                decadeSynthesis,
                "Annual CrossFunctional Resolution에는 Decade Synthesis가 필요합니다."
        );

        Objects.requireNonNull(
                contactLevel,
                "Annual CrossFunctional Resolution에는 Contact Level이 필요합니다."
        );

        NatalCrossFunctionalScope scope =
                decadeSynthesis
                        .sourceEffect()
                        .scope();

        if (
                !contactLevel
                        .contact()
        ) {

            return AnnualCrossFunctionalResolutionType
                    .NO_CONTACT;
        }

        if (
                scope
                        == NatalCrossFunctionalScope
                        .FUNCTION_TO_CORE_SELF
        ) {

            return AnnualCrossFunctionalResolutionType
                    .FUNCTION_TO_CORE_SELF_CONTACT;
        }

        if (
                scope
                        == NatalCrossFunctionalScope
                        .WITHIN_FUNCTION
        ) {

            return AnnualCrossFunctionalResolutionType
                    .WITHIN_FUNCTION_CONTACT;
        }

        if (
                !contactLevel
                        .fullPatternContact()
        ) {

            return AnnualCrossFunctionalResolutionType
                    .PARTIAL_CONTACT;
        }

        if (
                decadeSynthesis
                        .fullPatternContact()
        ) {

            return AnnualCrossFunctionalResolutionType
                    .DECADE_FULL_CONTINUATION;
        }

        if (
                participants == null
                        || participants.size()
                        != 2
        ) {

            throw new IllegalArgumentException(
                    "BETWEEN_FUNCTIONS Resolution에는 2개 Participant가 필요합니다."
            );
        }

        AnnualResolvedFunctionEffect primary =
                participants.get(
                        0
                );

        AnnualResolvedFunctionEffect secondary =
                participants.get(
                        1
                );

        boolean decadePrimary =
                primary
                        .decadeBaselineActive();

        boolean decadeSecondary =
                secondary
                        .decadeBaselineActive();

        boolean annualPrimary =
                primary
                        .annualSelfActive();

        boolean annualSecondary =
                secondary
                        .annualSelfActive();

        boolean crossPrimary =
                primary
                        .crossLuckTargetActive();

        boolean crossSecondary =
                secondary
                        .crossLuckTargetActive();

        boolean primaryWithoutCross =
                decadePrimary
                        || annualPrimary;

        boolean secondaryWithoutCross =
                decadeSecondary
                        || annualSecondary;

        boolean fullWithoutCross =
                primaryWithoutCross
                        && secondaryWithoutCross;

        if (
                !fullWithoutCross

                        && (
                        crossPrimary
                                || crossSecondary
                )
        ) {

            return AnnualCrossFunctionalResolutionType
                    .TRANSFORMATION_COMPLETION;
        }

        if (
                !decadePrimary
                        && !decadeSecondary

                        && annualPrimary
                        && annualSecondary
        ) {

            return AnnualCrossFunctionalResolutionType
                    .ANNUAL_COMPLETION;
        }

        boolean annualCompletesMissingSecondary =
                decadePrimary
                        && !decadeSecondary
                        && annualSecondary;

        boolean annualCompletesMissingPrimary =
                decadeSecondary
                        && !decadePrimary
                        && annualPrimary;

        if (
                annualCompletesMissingSecondary
                        || annualCompletesMissingPrimary
        ) {

            return AnnualCrossFunctionalResolutionType
                    .CROSS_LAYER_COMPLETION;
        }

        return AnnualCrossFunctionalResolutionType
                .FULL_CONTACT;
    }
}