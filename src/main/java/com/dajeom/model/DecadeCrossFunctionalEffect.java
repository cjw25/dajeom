package com.dajeom.model;

import com.dajeom.type.DecadeCrossFunctionalContactLevel;
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
 * 특정 대운 Period가 어떤 범위로
 * 접촉했는지를 나타낸다.
 *
 * =================================================
 *
 * 원국 Pattern 자체는 변경하지 않는다.
 *
 * 즉:
 *
 * sourcePattern.sourceLevel()
 * sourcePattern.validationStatus()
 * sourcePattern.structurallyEffective()
 * sourcePattern.strongSourcePattern()
 *
 * 를 그대로 보존한다.
 *
 * =================================================
 *
 * 대운이 추가하는 정보는:
 *
 * - Primary Function 활성 여부
 * - Secondary Function 활성 여부
 * - Transformation Target에 의한 활성 여부
 * - 원기능 감소 여부
 * - Capacity qualification
 * - Contact Level
 *
 * 뿐이다.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * BOTH_FUNCTIONS
 *
 * 라고 해서 원국 REFERENCE Pattern을
 * MEANINGFUL 또는 STRONG으로 승격하지 않는다.
 *
 * 반대로 원국 STRONG Pattern이라도
 * 이번 대운에서 Participant가 하나도
 * 활성되지 않았다면 NO_CONTACT일 수 있다.
 */
public record DecadeCrossFunctionalEffect(

        NatalCrossFunctionalPattern sourcePattern,

        List<DecadeCrossFunctionalParticipantEffect>
        participantEffects,

        DecadeCrossFunctionalContactLevel contactLevel,

        boolean completeChart

) {

    public DecadeCrossFunctionalEffect {

        Objects.requireNonNull(
                sourcePattern,
                "DecadeCrossFunctionalEffect에는 원국 Cross Pattern이 필요합니다."
        );

        participantEffects =
                participantEffects == null
                        ? List.of()
                        : List.copyOf(
                        participantEffects
                );

        Objects.requireNonNull(
                contactLevel,
                "DecadeCrossFunctionalEffect에는 Contact Level이 필요합니다."
        );

        if (
                sourcePattern.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "원국 Cross Pattern과 대운 Cross Effect의 completeChart가 일치하지 않습니다."
            );
        }

        NatalCrossFunctionalScope scope =
                sourcePattern
                        .patternType()
                        .scope();

        /*
         * =================================================
         * Participant 수 / 구성 검증
         * =================================================
         */

        if (
                scope
                        == NatalCrossFunctionalScope
                        .BETWEEN_FUNCTIONS
        ) {

            validateBetweenFunctionsParticipants(
                    sourcePattern,
                    participantEffects
            );

        } else {

            validateSingleFunctionParticipant(
                    sourcePattern,
                    participantEffects
            );
        }

        for (
                DecadeCrossFunctionalParticipantEffect participant
                : participantEffects
        ) {

            if (
                    participant.sourcePattern()
                            != sourcePattern
            ) {

                throw new IllegalArgumentException(
                        "Participant가 현재 원국 Cross Pattern을 참조하지 않습니다."
                );
            }

            if (
                    participant.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Cross Participant의 completeChart가 일치하지 않습니다."
                );
            }
        }

        /*
         * =================================================
         * Contact Level 검증
         * =================================================
         */

        DecadeCrossFunctionalContactLevel expected =
                resolveContactLevel(
                        sourcePattern,
                        participantEffects
                );

        if (
                contactLevel
                        != expected
        ) {

            throw new IllegalArgumentException(
                    "대운 CrossFunctional Contact Level이 "
                            + "실제 Participant 상태와 일치하지 않습니다. "
                            + "pattern="
                            + sourcePattern.patternType()
                            + ", expected="
                            + expected
                            + ", actual="
                            + contactLevel
            );
        }
    }

    /*
     * =================================================
     * 원국 Pattern
     * =================================================
     */

    public NatalCrossFunctionalPatternType patternType() {

        return sourcePattern
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

        return sourcePattern
                .sourceLevel();
    }

    public TenGodCrossValidationStatus validationStatus() {

        return sourcePattern
                .validationStatus();
    }

    public boolean structurallyEffectiveNatalPattern() {

        return sourcePattern
                .structurallyEffective();
    }

    public boolean strongNatalPattern() {

        return sourcePattern
                .strongSourcePattern();
    }

    public boolean natalSupported() {

        return sourcePattern
                .supported();
    }

    public boolean natalQualified() {

        return sourcePattern
                .qualified();
    }

    public boolean natalWeakened() {

        return sourcePattern
                .weakened();
    }

    public boolean natalConflicted() {

        return sourcePattern
                .conflicted();
    }

    /*
     * =================================================
     * Participant
     * =================================================
     */

    public DecadeCrossFunctionalParticipantEffect
    primaryParticipant() {

        return participantEffects
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
    }

    public Optional<DecadeCrossFunctionalParticipantEffect>
    secondaryParticipant() {

        return participantEffects
                .stream()
                .filter(
                        DecadeCrossFunctionalParticipantEffect
                                ::secondary
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

    /**
     * 원국 Pattern의 scope가 요구하는
     * temporal Function Participant가
     * 이번 대운에서 모두 접촉된 상태.
     *
     * Pattern 승격을 의미하지 않는다.
     */
    public boolean fullPatternContact() {

        return contactLevel
                .fullPatternContact();
    }

    public boolean partialPatternContact() {

        return contactLevel
                .partialContact();
    }

    /**
     * 원국에서 실제 Pattern으로 취급 가능한
     * MEANINGFUL/STRONG Pattern이면서
     * 이번 대운에서 full participant contact가 존재.
     */
    public boolean effectiveFullPatternContact() {

        return structurallyEffectiveNatalPattern()
                && fullPatternContact();
    }

    /**
     * 원국 REFERENCE Pattern에
     * 대운 Participant가 접촉한 경우.
     *
     * 보존은 하지만
     * 강한 해석 근거로 승격하면 안 된다.
     */
    public boolean referencePatternContact() {

        return temporalContactExists()
                && !structurallyEffectiveNatalPattern();
    }

    public boolean strongNatalPatternFullContact() {

        return effectiveFullPatternContact()
                && strongNatalPattern();
    }

    /*
     * =================================================
     * Activation Source
     * =================================================
     */

    public boolean originalInputParticipantContact() {

        return participantEffects
                .stream()
                .anyMatch(
                        participant ->
                                participant.active()
                                        && participant
                                        .originalInputActive()
                );
    }

    public boolean transformedTargetParticipantContact() {

        return participantEffects
                .stream()
                .anyMatch(
                        participant ->
                                participant.active()
                                        && participant
                                        .transformedTargetActive()
                );
    }

    public boolean transformedTargetOnlyParticipantExists() {

        return participantEffects
                .stream()
                .anyMatch(
                        participant ->
                                participant.active()
                                        && participant
                                        .transformedTargetOnly()
                );
    }

    /*
     * =================================================
     * Original Function 제한
     * =================================================
     */

    public boolean activeParticipantOriginalInputReductionExists() {

        return participantEffects
                .stream()
                .anyMatch(
                        participant ->
                                participant.active()
                                        && participant
                                        .originalInputReduced()
                );
    }

    public boolean activeParticipantStrongOriginalInputReductionExists() {

        return participantEffects
                .stream()
                .anyMatch(
                        participant ->
                                participant.active()
                                        && participant
                                        .originalInputStronglyReduced()
                );
    }

    public boolean activeParticipantMixedOriginalInputStateExists() {

        return participantEffects
                .stream()
                .anyMatch(
                        participant ->
                                participant.active()
                                        && participant
                                        .mixedOriginalInputState()
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
                        DecadeCrossFunctionalParticipantEffect
                                ::active
                )
                .anyMatch(
                        DecadeCrossFunctionalParticipantEffect
                                ::capacityReviewRequired
                );
    }

    public boolean restrictiveRelationContextExists() {

        return participantEffects
                .stream()
                .filter(
                        DecadeCrossFunctionalParticipantEffect
                                ::active
                )
                .anyMatch(
                        DecadeCrossFunctionalParticipantEffect
                                ::restrictiveRelationContextExists
                );
    }

    /*
     * =================================================
     * Qualification
     * =================================================
     */

    /**
     * 이 Pattern Contact를 단순한:
     *
     * "대운에 식신제살이 발동한다"
     *
     * 같은 문장 하나로 확정하면 안 되는 상태.
     */
    public boolean qualificationRequired() {

        if (
                !temporalContactExists()
        ) {

            return false;
        }

        return partialPatternContact()

                || natalQualified()

                || natalWeakened()

                || natalConflicted()

                || referencePatternContact()

                || transformedTargetParticipantContact()

                || activeParticipantOriginalInputReductionExists()

                || activeParticipantMixedOriginalInputStateExists()

                || capacityReviewRequired()

                || restrictiveRelationContextExists()

                || participantEffects
                .stream()
                .filter(
                        DecadeCrossFunctionalParticipantEffect
                                ::active
                )
                .anyMatch(
                        DecadeCrossFunctionalParticipantEffect
                                ::qualificationRequired
                );
    }

    /*
     * =================================================
     * Validation
     * =================================================
     */

    private static void validateBetweenFunctionsParticipants(

            NatalCrossFunctionalPattern sourcePattern,

            List<DecadeCrossFunctionalParticipantEffect>
                    participantEffects

    ) {

        if (
                participantEffects.size()
                        != 2
        ) {

            throw new IllegalArgumentException(
                    "BETWEEN_FUNCTIONS Pattern에는 "
                            + "Primary/Secondary Participant가 정확히 2개 필요합니다."
            );
        }

        long primaryCount =
                participantEffects
                        .stream()
                        .filter(
                                DecadeCrossFunctionalParticipantEffect
                                        ::primary
                        )
                        .count();

        long secondaryCount =
                participantEffects
                        .stream()
                        .filter(
                                DecadeCrossFunctionalParticipantEffect
                                        ::secondary
                        )
                        .count();

        if (
                primaryCount != 1
                        || secondaryCount != 1
        ) {

            throw new IllegalArgumentException(
                    "BETWEEN_FUNCTIONS Pattern에는 "
                            + "Primary 1개와 Secondary 1개가 필요합니다."
            );
        }

        NatalTenGodFunctionType expectedSecondary =
                sourcePattern
                        .patternType()
                        .secondaryFunction()
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "BETWEEN_FUNCTIONS Pattern의 Secondary 정의가 없습니다."
                                        )
                        );

        if (
                participantEffects
                        .stream()
                        .filter(
                                DecadeCrossFunctionalParticipantEffect
                                        ::secondary
                        )
                        .noneMatch(
                                participant ->
                                        participant.functionType()
                                                == expectedSecondary
                        )
        ) {

            throw new IllegalArgumentException(
                    "BETWEEN_FUNCTIONS Pattern의 Secondary Participant가 정의와 다릅니다."
            );
        }
    }

    private static void validateSingleFunctionParticipant(

            NatalCrossFunctionalPattern sourcePattern,

            List<DecadeCrossFunctionalParticipantEffect>
                    participantEffects

    ) {

        if (
                participantEffects.size()
                        != 1
        ) {

            throw new IllegalArgumentException(
                    "FUNCTION_TO_CORE_SELF / WITHIN_FUNCTION Pattern에는 "
                            + "Function Participant가 정확히 1개 필요합니다."
            );
        }

        DecadeCrossFunctionalParticipantEffect participant =
                participantEffects.get(
                        0
                );

        if (
                !participant.primary()
        ) {

            throw new IllegalArgumentException(
                    "단일 Function Pattern의 Participant는 Primary여야 합니다."
            );
        }

        if (
                participant.functionType()
                        != sourcePattern
                        .patternType()
                        .primaryFunction()
        ) {

            throw new IllegalArgumentException(
                    "단일 Function Pattern의 Primary Function이 정의와 다릅니다."
            );
        }
    }

    private static DecadeCrossFunctionalContactLevel
    resolveContactLevel(

            NatalCrossFunctionalPattern sourcePattern,

            List<DecadeCrossFunctionalParticipantEffect>
                    participantEffects

    ) {

        NatalCrossFunctionalScope scope =
                sourcePattern
                        .patternType()
                        .scope();

        DecadeCrossFunctionalParticipantEffect primary =
                participantEffects
                        .stream()
                        .filter(
                                DecadeCrossFunctionalParticipantEffect
                                        ::primary
                        )
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Primary Participant가 없습니다."
                                        )
                        );

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

        DecadeCrossFunctionalParticipantEffect secondary =
                participantEffects
                        .stream()
                        .filter(
                                DecadeCrossFunctionalParticipantEffect
                                        ::secondary
                        )
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "BETWEEN_FUNCTIONS Pattern의 Secondary Participant가 없습니다."
                                        )
                        );

        if (
                primary.active()
                        && secondary.active()
        ) {

            return DecadeCrossFunctionalContactLevel
                    .BOTH_FUNCTIONS;
        }

        if (
                primary.active()
        ) {

            return DecadeCrossFunctionalContactLevel
                    .PRIMARY_ONLY;
        }

        if (
                secondary.active()
        ) {

            return DecadeCrossFunctionalContactLevel
                    .SECONDARY_ONLY;
        }

        return DecadeCrossFunctionalContactLevel
                .NO_CONTACT;
    }
}