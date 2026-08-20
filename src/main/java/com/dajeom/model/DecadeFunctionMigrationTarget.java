package com.dajeom.model;

import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.DecadeLuckNatalRelationLayer;
import com.dajeom.type.FiveElement;
import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.TransformationTargetActivationState;

import java.util.List;
import java.util.Objects;

/**
 * 대운 Transformation 하나가
 * 실제 활성화한 목표 Function.
 *
 * =================================================
 *
 * 예:
 *
 * 원래 대운 천간:
 * 관살 Function
 *
 * ↓
 *
 * 천간합 합화
 *
 * ↓
 *
 * targetElement = WATER
 *
 * ↓
 *
 * 일간 기준 WATER의 역할 = RESOURCE
 *
 * ↓
 *
 * targetFunctionType = PROCESSING
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 이 객체는 원래 Function을 삭제하지 않는다.
 *
 * sourceInputOperations
 * +
 * sourceTransformation
 * +
 * targetRole
 * +
 * targetFunctionType
 *
 * 를 모두 보존한다.
 *
 * =================================================
 *
 * 또한:
 *
 * targetFunctionType가 원래 Function과
 * 같을 수도 있다.
 *
 * 이 경우는 Function Migration이라기보다
 * 동일 Function 방향의 Transformation Target
 * 활성화다.
 *
 * 그 구분은 상위
 * DecadeFunctionMigration에서 처리한다.
 */
public record DecadeFunctionMigrationTarget(

        DecadeLuckNatalTransformationReference
        sourceTransformation,

        List<DecadeFunctionInputOperation>
        sourceInputOperations,

        DayMasterElementRole targetRole,

        NatalTenGodFunctionType targetFunctionType

) {

    public DecadeFunctionMigrationTarget {

        Objects.requireNonNull(
                sourceTransformation,
                "Function Migration Target에는 Transformation Reference가 필요합니다."
        );

        sourceInputOperations =
                sourceInputOperations == null
                        ? List.of()
                        : List.copyOf(
                        sourceInputOperations
                );

        Objects.requireNonNull(
                targetRole,
                "Function Migration Target에는 일간 기준 목표 역할이 필요합니다."
        );

        Objects.requireNonNull(
                targetFunctionType,
                "Function Migration Target에는 목표 Function이 필요합니다."
        );

        /*
         * =================================================
         * 실제 Target 활성 필수
         * =================================================
         *
         * Transformation Context가 존재한다는 이유만으로
         * Migration을 만들지 않는다.
         *
         * 목표 오행이 실제 활성된 경우에만
         * 이 객체를 생성한다.
         */

        if (
                !sourceTransformation
                        .activated()
        ) {

            throw new IllegalArgumentException(
                    "활성되지 않은 Transformation으로 "
                            + "Function Migration Target을 만들 수 없습니다. code="
                            + sourceTransformation.transformationCode()
            );
        }

        /*
         * =================================================
         * Source Input 필수
         * =================================================
         */

        if (
                sourceInputOperations
                        .isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Function Migration Target에는 "
                            + "Transformation에 실제 접촉한 대운 Input이 필요합니다."
            );
        }

        /*
         * =================================================
         * Role ↔ Function 정합성
         * =================================================
         */

        NatalTenGodFunctionType expectedFunction =
                functionTypeOf(
                        targetRole
                );

        if (
                targetFunctionType
                        != expectedFunction
        ) {

            throw new IllegalArgumentException(
                    "Transformation Target Role과 Function이 일치하지 않습니다. "
                            + "role="
                            + targetRole
                            + ", expected="
                            + expectedFunction
                            + ", actual="
                            + targetFunctionType
            );
        }

        /*
         * =================================================
         * Input ↔ Transformation 정합성
         * =================================================
         */

        for (
                DecadeFunctionInputOperation sourceInput
                : sourceInputOperations
        ) {

            Objects.requireNonNull(
                    sourceInput,
                    "Function Migration Source Input은 null일 수 없습니다."
            );

            if (
                    !sourceInput
                            .transformationContexts()
                            .contains(
                                    sourceTransformation
                            )
            ) {

                throw new IllegalArgumentException(
                        "Migration Source Input이 현재 Transformation을 "
                                + "실제로 참조하지 않습니다. code="
                                + sourceTransformation.transformationCode()
                );
            }

            DecadeLuckNatalRelationLayer expectedLayer =
                    sourceInput.visible()
                            ? DecadeLuckNatalRelationLayer.HEAVENLY_STEM
                            : DecadeLuckNatalRelationLayer.EARTHLY_BRANCH;

            if (
                    sourceTransformation.layer()
                            != expectedLayer
            ) {

                throw new IllegalArgumentException(
                        "Migration Source Input과 Transformation Layer가 일치하지 않습니다."
                );
            }
        }
    }

    /*
     * =================================================
     * Canonical Role → Functional Family
     * =================================================
     *
     * FiveElement의 상생상극을 여기서
     * 다시 계산하지 않는다.
     *
     * DayMasterElementRelationAnalysis가 이미
     * 확정한 DayMasterElementRole을
     * 기존 Functional Family로 번역만 한다.
     */

    public static NatalTenGodFunctionType functionTypeOf(
            DayMasterElementRole role
    ) {

        Objects.requireNonNull(
                role,
                "Function 변환에는 DayMasterElementRole이 필요합니다."
        );

        return switch (
                role
                ) {

            case COMPANION ->
                    NatalTenGodFunctionType
                            .SELF_ASSERTION;

            case OUTPUT ->
                    NatalTenGodFunctionType
                            .EXPRESSION;

            case WEALTH ->
                    NatalTenGodFunctionType
                            .RESOURCE_REALITY;

            case OFFICER ->
                    NatalTenGodFunctionType
                            .RESPONSIBILITY;

            case RESOURCE ->
                    NatalTenGodFunctionType
                            .PROCESSING;
        };
    }

    /*
     * =================================================
     * Target
     * =================================================
     */

    public FiveElement targetElement() {

        return sourceTransformation
                .targetElement();
    }

    public TransformationTargetActivationState
    targetActivationState() {

        return sourceTransformation
                .targetActivationState();
    }

    public boolean strongTargetActivation() {

        return sourceTransformation
                .strongTargetActivation();
    }

    /*
     * =================================================
     * Source Layer
     * =================================================
     */

    public DecadeLuckNatalRelationLayer sourceLayer() {

        return sourceTransformation
                .layer();
    }

    public boolean heavenlyStemSource() {

        return sourceTransformation
                .heavenlyStemTransformation();
    }

    public boolean earthlyBranchSource() {

        return sourceTransformation
                .earthlyBranchTransformation();
    }

    /*
     * =================================================
     * Original Function 상태
     * =================================================
     */

    public boolean sourceOriginalFunctionReduced() {

        return sourceTransformation
                .luckOriginalFunctionReduced();
    }

    public boolean sourceOriginalFunctionStronglyReduced() {

        return sourceTransformation
                .luckOriginalFunctionStronglyReduced();
    }
}