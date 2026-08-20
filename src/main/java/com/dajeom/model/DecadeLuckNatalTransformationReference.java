package com.dajeom.model;

import com.dajeom.type.DecadeLuckNatalRelationLayer;
import com.dajeom.type.FiveElement;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.TransformationOriginalFunctionState;
import com.dajeom.type.TransformationTargetActivationState;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 정밀화 13차-5.
 *
 * 대운 유입에 의해 발생하거나
 * 영향을 받는 합화 Context.
 *
 * =================================================
 *
 * 기존 구조는:
 *
 * targetElementName
 * activated
 * originalFunctionReduced
 *
 * 정도만 보존했다.
 *
 * 이 방식은 9차에서 이미 계산된:
 *
 * TransformationOriginalFunctionState
 * TransformationTargetActivationState
 *
 * 를 boolean으로 압축해 버린다.
 *
 * =================================================
 *
 * 보강 후에는:
 *
 * 1.
 * 천간 Transformation인지
 * 지지 Transformation인지
 *
 * 2.
 * 목표 FiveElement
 *
 * 3.
 * 목표 활성 상태
 *
 * 4.
 * 대운 Participant의 원기능 상태
 *
 * 5.
 * 각 원국 Participant의 원기능 상태
 *
 * 를 구조적으로 보존한다.
 *
 * =================================================
 *
 * 매우 중요한 원칙:
 *
 * 대운 Participant와
 * 원국 Participant의 원기능 상태를
 * 구분해서 보존한다.
 *
 * 따라서 이후 계층에서:
 *
 * "원기능 감소가 있다"
 *
 * 만 보고
 * 대운 기능까지 무조건 감소했다고
 * 판단하지 않는다.
 *
 * =================================================
 *
 * 현재 13-5 Dynamic Transformation 정책에서는
 * 9-9 Rule이 Candidate 단위로 계산한
 * 원기능 상태가 모든 참여 Member에 적용된다.
 *
 * 따라서 현재 생성 시점에는:
 *
 * luckOriginalFunctionState
 * natalOriginalFunctionStates
 *
 * 가 같은 State를 가질 수 있다.
 *
 * 그러나 모델을 분리해 둠으로써
 * 향후 Member별 정책으로 정밀화되어도
 * 구조를 다시 바꿀 필요가 없다.
 */
public record DecadeLuckNatalTransformationReference(

        DecadeLuckNatalRelationLayer layer,

        Set<PalacePosition> natalPositions,

        String transformationCode,

        String transformationName,

        FiveElement targetElement,

        TransformationTargetActivationState
        targetActivationState,

        TransformationOriginalFunctionState
        luckOriginalFunctionState,

        Map<
                PalacePosition,
                TransformationOriginalFunctionState
                >
        natalOriginalFunctionStates

) {

    public DecadeLuckNatalTransformationReference {

        Objects.requireNonNull(
                layer,
                "대운 Transformation에는 Layer가 필요합니다."
        );

        natalPositions =
                natalPositions == null
                        ? Set.of()
                        : Set.copyOf(
                        natalPositions
                );

        if (
                natalPositions.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "대운 합화 Context에는 최소 하나의 원국 Palace가 필요합니다."
            );
        }

        transformationCode =
                requireText(
                        transformationCode,
                        "대운 합화 Code가 필요합니다."
                );

        transformationName =
                requireText(
                        transformationName,
                        "대운 합화 이름이 필요합니다."
                );

        Objects.requireNonNull(
                targetElement,
                "대운 Transformation 목표 오행이 필요합니다."
        );

        Objects.requireNonNull(
                targetActivationState,
                "대운 Transformation 목표 활성 상태가 필요합니다."
        );

        Objects.requireNonNull(
                luckOriginalFunctionState,
                "대운 Participant의 원기능 상태가 필요합니다."
        );

        natalOriginalFunctionStates =
                normalizeNatalOriginalFunctionStates(
                        natalPositions,
                        natalOriginalFunctionStates
                );
    }

    /*
     * =================================================
     * Layer
     * =================================================
     */

    public boolean heavenlyStemTransformation() {

        return layer
                == DecadeLuckNatalRelationLayer.HEAVENLY_STEM;
    }

    public boolean earthlyBranchTransformation() {

        return layer
                == DecadeLuckNatalRelationLayer.EARTHLY_BRANCH;
    }

    /*
     * =================================================
     * 원국 Participant
     * =================================================
     */

    public boolean involves(
            PalacePosition position
    ) {

        return position != null
                && natalPositions.contains(
                position
        );
    }

    public Optional<TransformationOriginalFunctionState>
    natalOriginalFunctionStateOf(

            PalacePosition position

    ) {

        if (
                position == null
        ) {

            return Optional.empty();
        }

        return Optional.ofNullable(
                natalOriginalFunctionStates
                        .get(
                                position
                        )
        );
    }

    public boolean natalOriginalFunctionReducedAt(

            PalacePosition position

    ) {

        return natalOriginalFunctionStateOf(
                position
        )
                .map(
                        TransformationOriginalFunctionState
                                ::reduced
                )
                .orElse(
                        false
                );
    }

    public boolean natalOriginalFunctionReduced() {

        return natalOriginalFunctionStates
                .values()
                .stream()
                .anyMatch(
                        TransformationOriginalFunctionState
                                ::reduced
                );
    }

    /*
     * =================================================
     * 대운 Participant
     * =================================================
     */

    public boolean luckOriginalFunctionReduced() {

        return luckOriginalFunctionState
                .reduced();
    }

    public boolean luckOriginalFunctionStronglyReduced() {

        return luckOriginalFunctionState
                .stronglyReduced();
    }

    /*
     * =================================================
     * Target
     * =================================================
     */

    /**
     * 기존 API 호환.
     */
    public String targetElementName() {

        return targetElement
                .getKoreanName();
    }

    /**
     * 현재 Transformation Reference는
     * 반드시 구조화된 목표 오행을 가진다.
     */
    public boolean hasTargetElement() {

        return true;
    }

    /**
     * 기존 API 호환.
     *
     * active boolean을 저장하지 않고
     * 원본 TargetActivationState에서 계산한다.
     */
    public boolean activated() {

        return targetActivationState
                .active();
    }

    public boolean strongTargetActivation() {

        return targetActivationState
                .strong();
    }

    /*
     * =================================================
     * 원기능 감소
     * =================================================
     */

    /**
     * 기존 API 호환.
     *
     * 대운 또는 원국 Participant 중
     * 하나라도 원기능 감소가 있는 경우 true.
     *
     * 이후 기능 판정에서는 이 메서드 하나만 보고
     * 대운 기능 감소를 확정하면 안 된다.
     *
     * 대운 기능 자체는:
     *
     * luckOriginalFunctionReduced()
     *
     * 를 사용해야 한다.
     */
    public boolean originalFunctionReduced() {

        return luckOriginalFunctionReduced()
                || natalOriginalFunctionReduced();
    }

    public boolean functionalChange() {

        return activated()
                || originalFunctionReduced();
    }

    /*
     * =================================================
     * Normalize
     * =================================================
     */

    private static Map<
            PalacePosition,
            TransformationOriginalFunctionState
            >
    normalizeNatalOriginalFunctionStates(

            Set<PalacePosition> natalPositions,

            Map<
                    PalacePosition,
                    TransformationOriginalFunctionState
                    >
                    source

    ) {

        Objects.requireNonNull(
                natalPositions,
                "원국 Palace 목록이 필요합니다."
        );

        if (
                source == null
                        || source.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "대운 Transformation에는 "
                            + "원국 Participant별 원기능 상태가 필요합니다."
            );
        }

        EnumMap<
                PalacePosition,
                TransformationOriginalFunctionState
                > result =
                new EnumMap<>(
                        PalacePosition.class
                );

        for (
                Map.Entry<
                        PalacePosition,
                        TransformationOriginalFunctionState
                        > entry
                : source.entrySet()
        ) {

            PalacePosition position =
                    Objects.requireNonNull(
                            entry.getKey(),
                            "Transformation 원국 Palace는 null일 수 없습니다."
                    );

            TransformationOriginalFunctionState state =
                    Objects.requireNonNull(
                            entry.getValue(),
                            "Transformation 원국 원기능 상태는 null일 수 없습니다."
                    );

            result.put(
                    position,
                    state
            );
        }

        if (
                !result
                        .keySet()
                        .equals(
                                natalPositions
                        )
        ) {

            throw new IllegalArgumentException(
                    "Transformation의 natalPositions와 "
                            + "natalOriginalFunctionStates의 Palace가 일치하지 않습니다. "
                            + "positions="
                            + natalPositions
                            + ", states="
                            + result.keySet()
            );
        }

        return Map.copyOf(
                result
        );
    }

    private static String requireText(

            String value,

            String message

    ) {

        Objects.requireNonNull(
                value,
                message
        );

        String normalized =
                value.trim();

        if (
                normalized.isBlank()
        ) {

            throw new IllegalArgumentException(
                    message
            );
        }

        return normalized;
    }
}