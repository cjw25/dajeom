package com.dajeom.model;

import com.dajeom.type.AnnualLuckNatalRelationKind;
import com.dajeom.type.AnnualLuckNatalRelationLayer;
import com.dajeom.type.StructureTenGodType;
import com.dajeom.type.TransformationOriginalFunctionState;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 하나의 세운 십성 Input에 연결된
 * 원국 관계 / Transformation 작동 Context.
 *
 * =================================================
 *
 * Source:
 *
 * 14-3 AnnualLuckTenGodEvidence
 *
 * =================================================
 *
 * Context:
 *
 * 14-4 AnnualLuckNatalRelationReference
 *
 * +
 *
 * 14-4 AnnualLuckNatalTransformationReference
 *
 * =================================================
 *
 * Layer 정책:
 *
 * VISIBLE_STEM
 *
 * ->
 *
 * HEAVENLY_STEM 관계 / 합화만 연결
 *
 * =================================================
 *
 * BRANCH_MAIN
 * BRANCH_MIDDLE
 * BRANCH_RESIDUAL
 *
 * ->
 *
 * EARTHLY_BRANCH 관계 / 합화만 연결
 *
 * =================================================
 *
 * 중요:
 *
 * 세운 지지 하나의 Transformation은
 * 여러 지장간 Input에 동시에 연결될 수 있다.
 *
 * 따라서 이 객체의 Transformation 개수를
 * 독립 합화 Evidence 개수로 사용하면 안 된다.
 *
 * Aggregate에서 반드시 distinct 처리한다.
 *
 * =================================================
 *
 * 또한 지지 Transformation이
 * 지장간 Input에 연결되었다고 해서
 *
 * "해당 지장간 십성이 완전히 사라졌다"
 *
 * 고 판단하지 않는다.
 *
 * 현재 14-4에서 보존된
 * annualOriginalFunctionState는
 * 세운 지지 Participant 전체의
 * Transformation Context다.
 */
public record AnnualFunctionInputOperation(

        AnnualLuckTenGodEvidence sourceEvidence,

        List<AnnualLuckNatalRelationReference> natalRelations,

        List<AnnualLuckNatalTransformationReference> natalTransformations,

        boolean completeChart

) {

    public AnnualFunctionInputOperation {

        Objects.requireNonNull(
                sourceEvidence,
                "Annual Function Input Operation에는 세운 십성 Evidence가 필요합니다."
        );

        natalRelations =
                natalRelations == null
                        ? List.of()
                        : List.copyOf(
                        natalRelations
                );

        natalTransformations =
                natalTransformations == null
                        ? List.of()
                        : List.copyOf(
                        natalTransformations
                );

        AnnualLuckNatalRelationLayer expectedLayer =
                sourceEvidence.visible()
                        ? AnnualLuckNatalRelationLayer.HEAVENLY_STEM
                        : AnnualLuckNatalRelationLayer.EARTHLY_BRANCH;

        /*
         * =================================================
         * Relation Layer
         * =================================================
         */

        for (
                AnnualLuckNatalRelationReference relation
                : natalRelations
        ) {

            Objects.requireNonNull(
                    relation,
                    "Annual Natal Relation은 null일 수 없습니다."
            );

            if (
                    relation.layer()
                            != expectedLayer
            ) {

                throw new IllegalArgumentException(
                        "Annual Function Input과 Natal Relation Layer가 일치하지 않습니다. "
                                + "source="
                                + sourceEvidence.sourceType()
                                + ", relationLayer="
                                + relation.layer()
                );
            }
        }

        /*
         * =================================================
         * Transformation Layer
         * =================================================
         */

        for (
                AnnualLuckNatalTransformationReference transformation
                : natalTransformations
        ) {

            Objects.requireNonNull(
                    transformation,
                    "Annual Natal Transformation은 null일 수 없습니다."
            );

            if (
                    transformation.layer()
                            != expectedLayer
            ) {

                throw new IllegalArgumentException(
                        "Annual Function Input과 Transformation Layer가 일치하지 않습니다. "
                                + "source="
                                + sourceEvidence.sourceType()
                                + ", transformationLayer="
                                + transformation.layer()
                );
            }
        }
    }

    public int targetYear() {

        return sourceEvidence
                .targetYear();
    }

    public AnnualLuckPeriod period() {

        return sourceEvidence
                .period();
    }

    public StructureTenGodType tenGodType() {

        return sourceEvidence
                .tenGodType();
    }

    /*
     * =================================================
     * Source Layer
     * =================================================
     */

    public boolean visible() {

        return sourceEvidence
                .visible();
    }

    public boolean hidden() {

        return sourceEvidence
                .hidden();
    }

    public boolean mainHiddenStem() {

        return sourceEvidence
                .mainHiddenStem();
    }

    public boolean middleHiddenStem() {

        return sourceEvidence
                .middleHiddenStem();
    }

    public boolean residualHiddenStem() {

        return sourceEvidence
                .residualHiddenStem();
    }

    public AnnualLuckNatalRelationLayer layer() {

        return visible()
                ? AnnualLuckNatalRelationLayer.HEAVENLY_STEM
                : AnnualLuckNatalRelationLayer.EARTHLY_BRANCH;
    }

    /*
     * =================================================
     * Relation
     * =================================================
     */

    public boolean relationContextExists() {

        return !natalRelations
                .isEmpty();
    }

    public boolean hasRelationKind(
            AnnualLuckNatalRelationKind relationKind
    ) {

        if (
                relationKind == null
        ) {

            return false;
        }

        return natalRelations
                .stream()
                .anyMatch(
                        relation ->
                                relation.relationKind()
                                        == relationKind
                );
    }

    public boolean restrictiveRelationContextExists() {

        return natalRelations
                .stream()
                .anyMatch(
                        AnnualLuckNatalRelationReference
                                ::restrictiveInteraction
                );
    }

    public boolean combinationRelationContextExists() {

        return natalRelations
                .stream()
                .anyMatch(
                        AnnualLuckNatalRelationReference
                                ::combinationFamily
                );
    }

    /*
     * =================================================
     * Transformation
     * =================================================
     */

    public boolean transformationContextExists() {

        return !natalTransformations
                .isEmpty();
    }

    public boolean activeTransformationExists() {

        return natalTransformations
                .stream()
                .anyMatch(
                        AnnualLuckNatalTransformationReference
                                ::activated
                );
    }

    public boolean strongTargetActivationExists() {

        return natalTransformations
                .stream()
                .anyMatch(
                        AnnualLuckNatalTransformationReference
                                ::strongTargetActivation
                );
    }

    /*
     * =================================================
     * Annual Participant Original Function
     * =================================================
     *
     * 반드시 annual participant 상태만 본다.
     *
     * 기존 broad:
     *
     * originalFunctionReduced()
     *
     * 를 여기서 사용하지 않는다.
     */

    public boolean annualOriginalFunctionReductionExists() {

        return natalTransformations
                .stream()
                .anyMatch(
                        AnnualLuckNatalTransformationReference
                                ::annualOriginalFunctionReduced
                );
    }

    public boolean annualOriginalFunctionStrongReductionExists() {

        return natalTransformations
                .stream()
                .anyMatch(
                        AnnualLuckNatalTransformationReference
                                ::annualOriginalFunctionStronglyReduced
                );
    }

    /**
     * 같은 Input에 연결된 Transformation들이
     * 세운 원기능 상태를 서로 다르게 가리키는 경우.
     *
     * 예:
     *
     * Transformation A
     * = 원기능 유지
     *
     * Transformation B
     * = 원기능 감소
     *
     * 후속에서는 하나로 단순화하지 않고
     * MIXED Context로 취급해야 한다.
     */
    public boolean mixedAnnualOriginalFunctionTransformationContext() {

        if (
                natalTransformations.size()
                        <= 1
        ) {

            return false;
        }

        Set<TransformationOriginalFunctionState> states =
                new LinkedHashSet<>();

        for (
                AnnualLuckNatalTransformationReference transformation
                : natalTransformations
        ) {

            states.add(
                    transformation
                            .annualOriginalFunctionState()
            );
        }

        return states.size()
                >= 2;
    }

    /*
     * =================================================
     * Transformation Aggregate
     * =================================================
     */

    public List<AnnualLuckNatalTransformationReference>
    activeTransformations() {

        return natalTransformations
                .stream()
                .filter(
                        AnnualLuckNatalTransformationReference
                                ::activated
                )
                .toList();
    }
}