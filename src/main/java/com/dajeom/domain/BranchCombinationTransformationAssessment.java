package com.dajeom.domain;

import java.util.Objects;

/**
 * 하나의 지지 합 구조에 대한
 * 성국 상태 + 합화 판정 결과.
 *
 * =================================================
 *
 * formationState
 * → 합 구조 자체가 무엇인가
 *
 * transformationState
 * → 실제 합화를 인정할 수 있는가
 *
 * =================================================
 *
 * 둘을 절대 같은 의미로 사용하지 않는다.
 */
public record BranchCombinationTransformationAssessment(

        BranchCombinationOccurrence occurrence,

        BranchCombinationFormationState formationState,

        BranchCombinationTransformationState transformationState,

        BranchCombinationTransformationContext context

) {

    public BranchCombinationTransformationAssessment {

        Objects.requireNonNull(
                occurrence,
                "지지 합 관계는 필수입니다."
        );

        Objects.requireNonNull(
                formationState,
                "합 구조 성립 상태는 필수입니다."
        );

        Objects.requireNonNull(
                transformationState,
                "합화 판정 상태는 필수입니다."
        );

        Objects.requireNonNull(
                context,
                "합화 Context는 필수입니다."
        );

        if (
                context.occurrence()
                        != occurrence
        ) {

            throw new IllegalArgumentException(
                    "합화 Context의 합 관계가 Assessment와 일치하지 않습니다."
            );
        }

        validateFormationState(
                occurrence.kind(),
                formationState
        );
    }

    public BranchCombinationKind kind() {

        return occurrence.kind();
    }

    public FiveElement transformationElement() {

        return occurrence
                .transformationElement();
    }

    public boolean transformationAccepted() {

        return transformationState
                == BranchCombinationTransformationState.ACCEPTED;
    }

    public boolean transformationContested() {

        return transformationState
                == BranchCombinationTransformationState.CONTESTED;
    }

    /**
     * 합 관계 자체는 존재하지만
     * 실제 합화를 인정하지 않는 상태.
     */
    public boolean combinationWithoutAcceptedTransformation() {

        return !transformationAccepted();
    }

    private static void validateFormationState(

            BranchCombinationKind kind,

            BranchCombinationFormationState state

    ) {

        boolean valid =
                switch (kind) {

                    case SIX_HARMONY ->
                            state
                                    == BranchCombinationFormationState
                                    .SIX_HARMONY_RELATION;

                    case THREE_HARMONY ->
                            state
                                    == BranchCombinationFormationState
                                    .THREE_HARMONY_COMPLETE;

                    case DIRECTIONAL_HARMONY ->
                            state
                                    == BranchCombinationFormationState
                                    .DIRECTIONAL_HARMONY_COMPLETE;
                };

        if (!valid) {

            throw new IllegalArgumentException(
                    "합 종류와 성립 상태가 일치하지 않습니다: "
                            + kind
                            + " / "
                            + state
            );
        }
    }
}