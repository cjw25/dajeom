package com.dajeom.model;

import com.dajeom.type.NatalCrossFunctionalScope;
import com.dajeom.type.NatalTenGodFunctionType;

import java.util.Objects;

/**
 * 하나의 NatalCrossFunctionalPattern 안에서
 * 특정 Function Participant가
 * 이번 대운에서 어떤 상태인지 보존한다.
 *
 * =================================================
 *
 * 예:
 *
 * RESOURCE_GENERATES_RESPONSIBILITY
 *
 * Primary:
 * RESOURCE_REALITY
 *
 * Secondary:
 * RESPONSIBILITY
 *
 * 각각에 대해:
 *
 * DecadeResolvedFunctionEffect
 *
 * 를 연결한다.
 *
 * =================================================
 *
 * CoreSelf는 이 객체로 만들지 않는다.
 *
 * FUNCTION_TO_CORE_SELF의 CoreSelf는
 * 이미 NatalCrossFunctionalPattern.coreSelfEffect()
 * 에서 고정된 원국 Anchor로 보존된다.
 *
 * 대운마다 CoreSelf를 새로 만드는 것을 방지한다.
 */
public record DecadeCrossFunctionalParticipantEffect(

        NatalCrossFunctionalPattern sourcePattern,

        NatalTenGodFunctionType functionType,

        boolean primaryParticipant,

        DecadeResolvedFunctionEffect resolvedFunctionEffect,

        boolean completeChart

) {

    public DecadeCrossFunctionalParticipantEffect {

        Objects.requireNonNull(
                sourcePattern,
                "CrossFunctional Participant에는 원국 Pattern이 필요합니다."
        );

        Objects.requireNonNull(
                functionType,
                "CrossFunctional Participant에는 Function Type이 필요합니다."
        );

        Objects.requireNonNull(
                resolvedFunctionEffect,
                "CrossFunctional Participant에는 Resolved Function Effect가 필요합니다."
        );

        if (
                sourcePattern.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "원국 CrossFunctional Pattern과 "
                            + "대운 Participant Effect의 completeChart가 일치하지 않습니다."
            );
        }

        if (
                resolvedFunctionEffect.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Resolved Function Effect와 "
                            + "CrossFunctional Participant의 completeChart가 일치하지 않습니다."
            );
        }

        if (
                resolvedFunctionEffect.functionType()
                        != functionType
        ) {

            throw new IllegalArgumentException(
                    "Participant Function과 Resolved Function Effect의 "
                            + "기능축이 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Primary Participant
         * =================================================
         */

        if (
                primaryParticipant
        ) {

            if (
                    sourcePattern
                            .patternType()
                            .primaryFunction()
                            != functionType
            ) {

                throw new IllegalArgumentException(
                        "Primary Participant Function이 "
                                + "원국 Pattern 정의와 일치하지 않습니다. "
                                + "pattern="
                                + sourcePattern.patternType()
                                + ", function="
                                + functionType
                );
            }

        } else {

            /*
             * =================================================
             * Secondary Participant
             * =================================================
             *
             * Secondary는 BETWEEN_FUNCTIONS에서만 존재한다.
             */

            if (
                    sourcePattern
                            .patternType()
                            .scope()
                            != NatalCrossFunctionalScope
                            .BETWEEN_FUNCTIONS
            ) {

                throw new IllegalArgumentException(
                        "Secondary Function Participant는 "
                                + "BETWEEN_FUNCTIONS Pattern에서만 사용할 수 있습니다."
                );
            }

            NatalTenGodFunctionType expectedSecondary =
                    sourcePattern
                            .patternType()
                            .secondaryFunction()
                            .orElseThrow(
                                    () ->
                                            new IllegalArgumentException(
                                                    "BETWEEN_FUNCTIONS Pattern에 "
                                                            + "Secondary Function 정의가 없습니다."
                                            )
                            );

            if (
                    expectedSecondary
                            != functionType
            ) {

                throw new IllegalArgumentException(
                        "Secondary Participant Function이 "
                                + "원국 Pattern 정의와 일치하지 않습니다. "
                                + "pattern="
                                + sourcePattern.patternType()
                                + ", expected="
                                + expectedSecondary
                                + ", actual="
                                + functionType
                );
            }
        }
    }

    public int sequenceNumber() {

        return resolvedFunctionEffect
                .sequenceNumber();
    }

    /*
     * =================================================
     * 대운 활성
     * =================================================
     */

    public boolean active() {

        return resolvedFunctionEffect
                .active();
    }

    public boolean inactive() {

        return !active();
    }

    /*
     * =================================================
     * 활성 Source
     * =================================================
     */

    public boolean originalInputActive() {

        return resolvedFunctionEffect
                .originalInputActive();
    }

    public boolean transformedTargetActive() {

        return resolvedFunctionEffect
                .transformedTargetActive();
    }

    public boolean transformedTargetOnly() {

        return transformedTargetActive()
                && !originalInputActive();
    }

    public boolean originalAndTransformedTargetActive() {

        return resolvedFunctionEffect
                .originalAndTransformedTargetActive();
    }

    /*
     * =================================================
     * 원기능 상태
     * =================================================
     */

    public boolean originalInputReduced() {

        return resolvedFunctionEffect
                .originalInputReduced();
    }

    public boolean originalInputStronglyReduced() {

        return resolvedFunctionEffect
                .originalInputStronglyReduced();
    }

    public boolean mixedOriginalInputState() {

        return resolvedFunctionEffect
                .mixedOriginalInputState();
    }

    /*
     * =================================================
     * Capacity
     * =================================================
     */

    public boolean capacityReviewRequired() {

        return resolvedFunctionEffect
                .capacityReviewRequired();
    }

    /*
     * =================================================
     * Context
     * =================================================
     */

    public boolean qualificationRequired() {

        return resolvedFunctionEffect
                .qualificationRequired();
    }

    public boolean restrictiveRelationContextExists() {

        return resolvedFunctionEffect
                .restrictiveRelationContextExists();
    }

    public boolean primary() {

        return primaryParticipant;
    }

    public boolean secondary() {

        return !primaryParticipant;
    }
}