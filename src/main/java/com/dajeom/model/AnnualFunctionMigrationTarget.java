package com.dajeom.model;

import com.dajeom.type.AnnualLuckNatalRelationLayer;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.FiveElement;
import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.TransformationTargetActivationState;

import java.util.List;
import java.util.Objects;

/**
 * 하나의 활성된 세운 Transformation이
 * 실제로 만들어내는 Target Function.
 *
 * =================================================
 *
 * Source:
 *
 * AnnualLuckNatalTransformationReference
 *
 * +
 *
 * 그 Transformation에 실제 연결된
 * AnnualFunctionInputOperation
 *
 * =================================================
 *
 * Target:
 *
 * FiveElement
 *
 * ↓
 *
 * DayMasterElementRelationAnalysis에서
 * 확정된 DayMasterElementRole
 *
 * ↓
 *
 * NatalTenGodFunctionType
 *
 * =================================================
 *
 * 중요:
 *
 * 원래 세운 Function을 삭제하지 않는다.
 *
 * Source Function
 * +
 * Target Function
 *
 * 을 동시에 보존한다.
 *
 * =================================================
 *
 * 예:
 *
 * 세운 원래 관살
 *
 * +
 *
 * 합화 target = 인성 오행
 *
 * ↓
 *
 * RESPONSIBILITY
 * +
 * PROCESSING
 *
 * 형태로 보존한다.
 */
public record AnnualFunctionMigrationTarget(

        AnnualLuckNatalTransformationReference
        sourceTransformation,

        List<AnnualFunctionInputOperation>
        sourceInputOperations,

        DayMasterElementRole targetRole,

        NatalTenGodFunctionType targetFunctionType

) {

    public AnnualFunctionMigrationTarget {

        Objects.requireNonNull(
                sourceTransformation,
                "Annual Function Migration Target에는 Transformation이 필요합니다."
        );

        sourceInputOperations =
                sourceInputOperations == null
                        ? List.of()
                        : List.copyOf(
                        sourceInputOperations
                );

        Objects.requireNonNull(
                targetRole,
                "Annual Function Migration Target에는 Target Role이 필요합니다."
        );

        Objects.requireNonNull(
                targetFunctionType,
                "Annual Function Migration Target에는 Target Function이 필요합니다."
        );

        /*
         * =================================================
         * 실제 활성된 Target만 Migration으로 생성
         * =================================================
         */

        if (
                !sourceTransformation
                        .activated()
        ) {

            throw new IllegalArgumentException(
                    "활성되지 않은 세운 Transformation으로 "
                            + "Function Migration Target을 만들 수 없습니다. code="
                            + sourceTransformation.transformationCode()
            );
        }

        if (
                sourceInputOperations
                        .isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Annual Function Migration Target에는 "
                            + "실제 Source Input이 필요합니다."
            );
        }

        NatalTenGodFunctionType expectedFunction =
                functionTypeOf(
                        targetRole
                );

        if (
                targetFunctionType
                        != expectedFunction
        ) {

            throw new IllegalArgumentException(
                    "Target Role과 Target Function이 일치하지 않습니다. "
                            + "role="
                            + targetRole
                            + ", expected="
                            + expectedFunction
                            + ", actual="
                            + targetFunctionType
            );
        }

        for (
                AnnualFunctionInputOperation input
                : sourceInputOperations
        ) {

            Objects.requireNonNull(
                    input,
                    "Annual Migration Source Input은 null일 수 없습니다."
            );

            if (
                    !input
                            .natalTransformations()
                            .contains(
                                    sourceTransformation
                            )
            ) {

                throw new IllegalArgumentException(
                        "Source Input이 현재 Transformation을 참조하지 않습니다. code="
                                + sourceTransformation.transformationCode()
                );
            }

            AnnualLuckNatalRelationLayer expectedLayer =
                    input.visible()
                            ? AnnualLuckNatalRelationLayer.HEAVENLY_STEM
                            : AnnualLuckNatalRelationLayer.EARTHLY_BRANCH;

            if (
                    sourceTransformation.layer()
                            != expectedLayer
            ) {

                throw new IllegalArgumentException(
                        "Annual Migration Source Input과 Transformation Layer가 일치하지 않습니다."
                );
            }
        }
    }

    /*
     * =================================================
     * Role → Function Family
     * =================================================
     *
     * FiveElement 상생상극을 여기서
     * 새로 계산하지 않는다.
     *
     * DayMasterElementRelationAnalysis가
     * 이미 확정한 Role을
     * Functional Family로 번역만 한다.
     */

    public static NatalTenGodFunctionType functionTypeOf(
            DayMasterElementRole role
    ) {

        Objects.requireNonNull(
                role,
                "Target Function 변환에는 DayMasterElementRole이 필요합니다."
        );

        return switch (
                role
                ) {

            case COMPANION ->
                    NatalTenGodFunctionType.SELF_ASSERTION;

            case OUTPUT ->
                    NatalTenGodFunctionType.EXPRESSION;

            case WEALTH ->
                    NatalTenGodFunctionType.RESOURCE_REALITY;

            case OFFICER ->
                    NatalTenGodFunctionType.RESPONSIBILITY;

            case RESOURCE ->
                    NatalTenGodFunctionType.PROCESSING;
        };
    }

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
     * Annual Participant 원기능
     * =================================================
     */

    public boolean annualOriginalFunctionReduced() {

        return sourceTransformation
                .annualOriginalFunctionReduced();
    }

    public boolean annualOriginalFunctionStronglyReduced() {

        return sourceTransformation
                .annualOriginalFunctionStronglyReduced();
    }
}