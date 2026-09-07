package com.dajeom.domain;

import java.util.Objects;
import java.util.Optional;

/**
 * 일간의 하나의 통근 지점에 대해:
 *
 * 1. 원래 통근
 * 2. 합충형파해 관계 Context
 * 3. ACCEPTED 합화 작용
 * 4. 합화 오행과 일간 관계
 *
 * 를 하나로 결합한 객체.
 *
 * =================================================
 *
 * 여기서도 최종적으로:
 *
 * ROOT_VALID
 * ROOT_INVALID
 * ROOT_DESTROYED
 *
 * 같은 판정을 하지 않는다.
 *
 * =================================================
 */
public record DayMasterEffectiveRootContext(

        StemGroundHit root,

        DayMasterRootInfluenceState relationInfluenceState,

        Optional<FiveElement> transformationElement,

        Optional<DayMasterRelationType> transformationRelation,

        DayMasterEffectiveRootCondition condition

) {

    public DayMasterEffectiveRootContext {

        Objects.requireNonNull(
                root,
                "통근 지점은 필수입니다."
        );

        Objects.requireNonNull(
                relationInfluenceState,
                "통근 관계 Context는 필수입니다."
        );

        Objects.requireNonNull(
                transformationElement,
                "합화 작용 오행 Optional은 필수입니다."
        );

        Objects.requireNonNull(
                transformationRelation,
                "합화-일간 관계 Optional은 필수입니다."
        );

        Objects.requireNonNull(
                condition,
                "통근 작용 상태는 필수입니다."
        );

        if (
                transformationElement.isPresent()
                        != transformationRelation.isPresent()
        ) {

            throw new IllegalArgumentException(
                    "합화 작용 오행과 일간 관계는 함께 존재하거나 함께 없어야 합니다."
            );
        }

        if (
                condition
                        == DayMasterEffectiveRootCondition
                        .ORIGINAL_ROOT_CONTEXT
        ) {

            if (
                    transformationElement.isPresent()
                            ||
                            transformationRelation.isPresent()
            ) {

                throw new IllegalArgumentException(
                        "ORIGINAL_ROOT_CONTEXT에는 합화 작용이 존재할 수 없습니다."
                );
            }

        } else {

            if (
                    transformationElement.isEmpty()
                            ||
                            transformationRelation.isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "합화 작용 상태에는 합화 오행과 일간 관계가 필요합니다."
                );
            }
        }
    }

    public HeavenlyStem dayMaster() {

        return root.targetStem();
    }

    public PillarPosition rootPosition() {

        return root.branchPosition();
    }

    public EarthlyBranch rootBranch() {

        return root.branch();
    }

    public HeavenlyStem rootHiddenStem() {

        return root.hiddenStem();
    }

    public HiddenStemPhase rootPhase() {

        return root.hiddenStemPhase();
    }

    public StemGroundRelationType rootRelationType() {

        return root.relationType();
    }

    public boolean exactStemRoot() {

        return root.exactStem();
    }

    public boolean sameElementOtherStemRoot() {

        return root.sameElementOtherStem();
    }

    /**
     * 합충형파해를 포함한
     * 직접 관계 Context가 존재하는가.
     *
     * COMBINATION_CONTEXT도 포함한다.
     */
    public boolean hasDirectRelationContext() {

        return relationInfluenceState
                != DayMasterRootInfluenceState
                .NO_DIRECT_RELATION;
    }

    /**
     * 충/형/파/해 등
     * 실제로 주의해서 읽어야 하는 Context.
     *
     * 단순 COMBINATION_CONTEXT는
     * 여기에는 포함하지 않는다.
     */
    public boolean hasDisturbanceContext() {

        return switch (relationInfluenceState) {

            case CLASH_IN_WANG_OR_XIANG,
                 CLASH_IN_XIU_QIU_OR_SI,
                 NON_CLASH_DISTURBANCE_CONTEXT,
                 MIXED_RELATION_CONTEXT,
                 CLASH_WITH_ADDITIONAL_DISTURBANCE ->
                    true;

            case NO_DIRECT_RELATION,
                 COMBINATION_CONTEXT ->
                    false;
        };
    }

    public boolean hasTransformationEffect() {

        return transformationElement
                .isPresent();
    }

    /**
     * 합화 작용이 일간을 생하거나
     * 같은 오행인 Context.
     *
     * 이것만으로 최종적으로
     * "근이 강화됐다"고 판정하지 않는다.
     */
    public boolean transformationSupportsDayMaster() {

        return condition
                == DayMasterEffectiveRootCondition
                .TRANSFORMATION_GENERATES_DAY_MASTER

                ||

                condition
                        == DayMasterEffectiveRootCondition
                        .TRANSFORMATION_SAME_ELEMENT;
    }

    /**
     * 합화 작용 오행이
     * 일간을 직접 극하는 Context.
     *
     * 이것만으로 기존 통근을 삭제하지 않는다.
     */
    public boolean transformationControlsDayMaster() {

        return condition
                == DayMasterEffectiveRootCondition
                .TRANSFORMATION_CONTROLS_DAY_MASTER;
    }
}