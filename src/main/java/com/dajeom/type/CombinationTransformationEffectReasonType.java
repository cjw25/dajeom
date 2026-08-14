package com.dajeom.type;

/**
 * 정밀화 9차-9.
 *
 * 원래 기능 보존 상태와
 * 목표 오행 활성 상태를 결정한 근거.
 *
 * 점수 합산용이 아니다.
 */
public enum CombinationTransformationEffectReasonType {

    /*
     * =================================================
     * 천간 9-3
     * =================================================
     */

    HEAVENLY_FULL_TRANSFORMATION(
            "천간합이 완전 합화 상태입니다."
    ),

    HEAVENLY_PARTIAL_TRANSFORMATION(
            "천간합이 부분 합화 상태입니다."
    ),

    HEAVENLY_CONDITIONAL_TRANSFORMATION(
            "천간합이 조건부 합화 상태입니다."
    ),

    HEAVENLY_COMBINED_WITHOUT_TRANSFORMATION(
            "천간합 관계는 있으나 실제 합화는 성립하지 않았습니다."
    ),

    HEAVENLY_TRANSFORMATION_SUPPRESSED(
            "천간 합화 작용이 경쟁 또는 방해에 의해 억제되었습니다."
    ),

    /*
     * =================================================
     * 지지 9-8 역할
     * =================================================
     */

    EARTHLY_PRIMARY_COMBINATION(
            "지지 결합 후보가 주결합 역할을 합니다."
    ),

    EARTHLY_SUPPORTING_COMBINATION(
            "지지 결합 후보가 동일 방향의 보조결합 역할을 합니다."
    ),

    EARTHLY_COMPETING_COMBINATION(
            "지지 결합 후보가 다른 목표 오행과 경쟁하고 있습니다."
    ),

    EARTHLY_DEFERRED_COMBINATION(
            "지지 결합 후보가 후순위로 보류되었습니다."
    ),

    EARTHLY_SUPPRESSED_COMBINATION(
            "지지 결합 후보의 실질 작용이 억제되었습니다."
    ),

    /*
     * =================================================
     * 지지 기본 상태
     * =================================================
     */

    EARTHLY_BASE_MEANINGFUL(
            "지지 결합의 기본 합화·국 작용이 의미 있게 성립했습니다."
    ),

    EARTHLY_BASE_CONDITIONAL(
            "지지 결합은 존재하지만 기본 성립 상태가 조건부입니다."
    ),

    /*
     * =================================================
     * 지지 방해
     * =================================================
     */

    NO_EARTHLY_OBSTRUCTION(
            "지지 결합에 충·형·파·해 방해가 없습니다."
    ),

    MINOR_EARTHLY_OBSTRUCTION(
            "지지 결합에 경미한 방해가 존재합니다."
    ),

    MEANINGFUL_EARTHLY_OBSTRUCTION(
            "지지 결합에 유의미한 방해가 존재합니다."
    ),

    SEVERE_EARTHLY_OBSTRUCTION(
            "지지 결합에 강한 방해가 존재합니다."
    ),

    CARDINAL_MEMBER(
            "현재 지지는 삼합·방합의 중심 왕지입니다."
    ),

    /*
     * =================================================
     * 원래 기능
     * =================================================
     */

    ORIGINAL_FUNCTION_PRESERVED(
            "원래 오행 기능을 유지합니다."
    ),

    ORIGINAL_FUNCTION_MOSTLY_PRESERVED(
            "원래 오행 기능이 대부분 유지됩니다."
    ),

    ORIGINAL_FUNCTION_PARTIALLY_REDUCED(
            "원래 오행 기능이 부분적으로 감소합니다."
    ),

    ORIGINAL_FUNCTION_STRONGLY_REDUCED(
            "원래 오행 기능이 크게 감소합니다."
    ),

    /*
     * =================================================
     * 목표 오행 활성
     * =================================================
     */

    TARGET_ELEMENT_INACTIVE(
            "합화 목표 오행의 실질 활성은 없습니다."
    ),

    TARGET_ELEMENT_CONDITIONAL(
            "합화 목표 오행이 조건부로 활성됩니다."
    ),

    TARGET_ELEMENT_PARTIAL(
            "합화 목표 오행이 부분적으로 활성됩니다."
    ),

    TARGET_ELEMENT_STRONG(
            "합화 목표 오행이 강하게 활성됩니다."
    ),

    /*
     * =================================================
     * 보존 정책
     * =================================================
     */

    EARTHLY_HIDDEN_STEMS_PRESERVED(
            "지지 합화가 성립해도 원래 지장간 기능을 즉시 삭제하지 않습니다."
    );

    private final String koreanDescription;

    CombinationTransformationEffectReasonType(
            String koreanDescription
    ) {

        this.koreanDescription = koreanDescription;
    }

    public String koreanDescription() {

        return koreanDescription;
    }
}