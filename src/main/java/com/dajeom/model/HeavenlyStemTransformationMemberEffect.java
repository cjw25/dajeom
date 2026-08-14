package com.dajeom.model;

import com.dajeom.type.CombinationTransformationEffectReasonType;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.TransformationOriginalFunctionState;
import com.dajeom.type.TransformationTargetActivationState;
import com.dajeom.type.VisibleStemPosition;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 9차-9.
 *
 * 천간합 하나의 구성원 천간 하나에 대한
 * 기능 변화 Evidence.
 *
 * =================================================
 *
 * 예:
 *
 * 丁壬合木
 *
 * TRANSFORMED
 *
 * 이라면:
 *
 * DAY 丁
 * originalFunctionState = STRONGLY_REDUCED
 * targetElement = WOOD
 * targetActivationState = STRONG
 *
 * HOUR 壬
 * originalFunctionState = STRONGLY_REDUCED
 * targetElement = WOOD
 * targetActivationState = STRONG
 *
 * 두 개의 MemberEffect가 생성된다.
 *
 * =================================================
 *
 * 천간 자체를 삭제하거나
 * HeavenlyStem enum을 Wood로 교체하지 않는다.
 */
public record HeavenlyStemTransformationMemberEffect(

        HeavenlyStemCombinationTransformationEvidence sourceEvidence,

        VisibleStemPosition position,

        HeavenlyStem originalStem,

        TransformationOriginalFunctionState originalFunctionState,

        FiveElement targetElement,

        TransformationTargetActivationState targetActivationState,

        List<CombinationTransformationEffectReasonType> reasons

) {

    public HeavenlyStemTransformationMemberEffect {

        Objects.requireNonNull(
                sourceEvidence,
                "9-3 천간 합화 Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                position,
                "천간 위치가 필요합니다."
        );

        Objects.requireNonNull(
                originalStem,
                "원래 천간이 필요합니다."
        );

        Objects.requireNonNull(
                originalFunctionState,
                "원래 천간 기능 보존 상태가 필요합니다."
        );

        Objects.requireNonNull(
                targetElement,
                "합화 목표 오행이 필요합니다."
        );

        Objects.requireNonNull(
                targetActivationState,
                "합화 목표 오행 활성 상태가 필요합니다."
        );

        if (
                !sourceEvidence.involves(
                        position
                )
        ) {

            throw new IllegalArgumentException(
                    "천간 위치가 원본 9-3 합화 Evidence의 구성원이 아닙니다."
            );
        }

        if (
                !sourceEvidence.involves(
                        originalStem
                )
        ) {

            throw new IllegalArgumentException(
                    "원래 천간이 원본 9-3 합화 Evidence의 구성원이 아닙니다."
            );
        }

        HeavenlyStem expectedStem =
                expectedStemAt(
                        sourceEvidence,
                        position
                );

        if (
                expectedStem
                        != originalStem
        ) {

            throw new IllegalArgumentException(
                    "천간 위치와 원래 천간이 서로 일치하지 않습니다."
            );
        }

        if (
                targetElement
                        != sourceEvidence.targetElement()
        ) {

            throw new IllegalArgumentException(
                    "목표 오행이 9-3 합화 목표 오행과 일치하지 않습니다."
            );
        }

        reasons =
                reasons == null
                        ? List.of()
                        : List.copyOf(
                        reasons
                );

        if (
                reasons.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "천간 기능 변화 Evidence에는 하나 이상의 근거가 필요합니다."
            );
        }
    }

    private static HeavenlyStem expectedStemAt(

            HeavenlyStemCombinationTransformationEvidence sourceEvidence,

            VisibleStemPosition position

    ) {

        if (
                sourceEvidence.firstPosition()
                        == position
        ) {

            return sourceEvidence.firstStem();
        }

        if (
                sourceEvidence.secondPosition()
                        == position
        ) {

            return sourceEvidence.secondStem();
        }

        return null;
    }

    public boolean dayMaster() {

        return position
                == VisibleStemPosition.DAY;
    }

    public boolean originalFunctionReduced() {

        return originalFunctionState
                .reduced();
    }

    public boolean targetActivated() {

        return targetActivationState
                .active();
    }

    public boolean strongTargetActivation() {

        return targetActivationState
                .strong();
    }

    public boolean hasReason(
            CombinationTransformationEffectReasonType reason
    ) {

        if (
                reason == null
        ) {

            return false;
        }

        return reasons.contains(
                reason
        );
    }
}