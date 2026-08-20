package com.dajeom.model;

import com.dajeom.type.AnnualLuckNatalRelationLayer;
import com.dajeom.type.FiveElement;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.TransformationOriginalFunctionState;
import com.dajeom.type.TransformationTargetActivationState;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 정밀화 14차-4.
 *
 * 세운 유입으로 인해 발생하거나
 * 영향을 받는 합화 Context.
 *
 * =================================================
 *
 * 기존 압축 구조:
 *
 * targetElementName
 * activated
 * originalFunctionReduced
 *
 * =================================================
 *
 * 문제:
 *
 * 기존 구조만으로는:
 *
 * - 어느 Layer의 합화인지
 * - 실제 목표 FiveElement
 * - 목표 활성 단계
 * - 세운 Participant의 원기능 상태
 * - 원국 Participant의 원기능 상태
 *
 * 를 후속 Functional Layer에서
 * 정확하게 구분할 수 없었다.
 *
 * =================================================
 *
 * 보강 구조:
 *
 * layer
 *
 * targetElement
 *
 * targetActivationState
 *
 * annualOriginalFunctionState
 *
 * natalOriginalFunctionStates
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 세운 Participant와
 * 원국 Participant의 상태를
 * 구조적으로 분리해서 보존한다.
 *
 * 현재 14-4 동적 Transformation 계산은
 * 기존 9차 Rule이 반환한 candidate-level
 * OriginalFunctionState를 사용하므로
 *
 * 현재 생성 시점에는
 * 세운과 관련 원국 Participant에
 * 동일 상태가 들어갈 수 있다.
 *
 * 그러나 모델 수준에서 분리해 둠으로써
 * 향후 Participant별 계산이 정밀해져도
 * API를 다시 깨지 않아도 된다.
 *
 * =================================================
 *
 * 기존:
 *
 * targetElementName()
 * activated()
 * originalFunctionReduced()
 *
 * helper는 호환성을 위해 유지한다.
 */
public record AnnualLuckNatalTransformationReference(

        AnnualLuckNatalRelationLayer layer,

        Set<PalacePosition> natalPositions,

        String transformationCode,

        String transformationName,

        FiveElement targetElement,

        TransformationTargetActivationState
        targetActivationState,

        TransformationOriginalFunctionState
        annualOriginalFunctionState,

        Map<
                PalacePosition,
                TransformationOriginalFunctionState
                > natalOriginalFunctionStates

) {

    public AnnualLuckNatalTransformationReference {

        Objects.requireNonNull(
                layer,
                "세운 Transformation에는 Layer가 필요합니다."
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
                    "세운 합화 Context에는 최소 하나의 원국 Palace가 필요합니다."
            );
        }

        if (
                transformationCode == null
                        || transformationCode.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "세운 합화 Code가 필요합니다."
            );
        }

        transformationCode =
                transformationCode.trim();

        if (
                transformationName == null
                        || transformationName.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "세운 합화 이름이 필요합니다."
            );
        }

        transformationName =
                transformationName.trim();

        Objects.requireNonNull(
                targetElement,
                "세운 Transformation 목표 오행이 필요합니다."
        );

        Objects.requireNonNull(
                targetActivationState,
                "세운 Transformation 목표 활성 상태가 필요합니다."
        );

        Objects.requireNonNull(
                annualOriginalFunctionState,
                "세운 Participant 원기능 상태가 필요합니다."
        );

        natalOriginalFunctionStates =
                natalOriginalFunctionStates == null
                        ? Map.of()
                        : Map.copyOf(
                        natalOriginalFunctionStates
                );

        /*
         * =================================================
         * Natal Participant State Coverage
         * =================================================
         *
         * Transformation에 참여한 모든
         * 원국 Palace의 상태를 정확히 하나씩
         * 보존해야 한다.
         */

        if (
                !natalOriginalFunctionStates
                        .keySet()
                        .equals(
                                natalPositions
                        )
        ) {

            throw new IllegalArgumentException(
                    "세운 Transformation의 원국 Participant 상태가 "
                            + "natalPositions와 정확히 일치하지 않습니다. "
                            + "positions="
                            + natalPositions
                            + ", states="
                            + natalOriginalFunctionStates.keySet()
            );
        }

        for (
                Map.Entry<
                        PalacePosition,
                        TransformationOriginalFunctionState
                        > entry
                : natalOriginalFunctionStates.entrySet()
        ) {

            Objects.requireNonNull(
                    entry.getValue(),
                    "원국 Participant의 Transformation 원기능 상태는 null일 수 없습니다."
            );
        }
    }

    /*
     * =================================================
     * Layer
     * =================================================
     */

    public boolean heavenlyStemTransformation() {

        return layer
                .heavenlyStem();
    }

    public boolean earthlyBranchTransformation() {

        return layer
                .earthlyBranch();
    }

    /*
     * =================================================
     * Palace
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

    /*
     * =================================================
     * Target Element
     * =================================================
     */

    /**
     * 기존 String API 호환.
     *
     * 내부 판정에는 이 문자열을 사용하지 않는다.
     */
    public String targetElementName() {

        return targetElement
                .getKoreanName();
    }

    public boolean hasTargetElement() {

        return true;
    }

    /*
     * =================================================
     * Target Activation
     * =================================================
     */

    /**
     * 기존 boolean API 호환.
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
     * Annual Participant Original Function
     * =================================================
     */

    public boolean annualOriginalFunctionReduced() {

        return annualOriginalFunctionState
                .reduced();
    }

    public boolean annualOriginalFunctionStronglyReduced() {

        return annualOriginalFunctionState
                .stronglyReduced();
    }

    /*
     * =================================================
     * Natal Participant Original Function
     * =================================================
     */

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

    public boolean natalOriginalFunctionStronglyReducedAt(
            PalacePosition position
    ) {

        return natalOriginalFunctionStateOf(
                position
        )
                .map(
                        TransformationOriginalFunctionState
                                ::stronglyReduced
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

    public boolean natalOriginalFunctionStronglyReduced() {

        return natalOriginalFunctionStates
                .values()
                .stream()
                .anyMatch(
                        TransformationOriginalFunctionState
                                ::stronglyReduced
                );
    }

    /*
     * =================================================
     * Compatibility
     * =================================================
     */

    /**
     * 기존 AnnualLuckNatalRelationAnalysis가
     * 사용하던 broad helper.
     *
     * 세운 Participant 또는
     * 원국 Participant 중 어느 쪽이든
     * 원기능 감소가 있으면 true.
     *
     * 후속 Annual Functional Layer에서는
     * 이 broad helper 대신:
     *
     * annualOriginalFunctionReduced()
     *
     * 를 사용해야 한다.
     */
    public boolean originalFunctionReduced() {

        return annualOriginalFunctionReduced()
                || natalOriginalFunctionReduced();
    }

    /**
     * 단순 관계 존재가 아니라
     * 기능적으로 실제 변화가 발생했는지.
     */
    public boolean functionalChange() {

        return activated()
                || originalFunctionReduced();
    }
}