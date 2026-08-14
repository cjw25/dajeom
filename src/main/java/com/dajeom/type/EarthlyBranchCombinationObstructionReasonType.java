package com.dajeom.type;

/**
 * 정밀화 9차-7.
 *
 * 지지 결합 방해 판정의 근거.
 *
 * =================================================
 *
 * 점수 합산용이 아니다.
 *
 * 어떤 이유로 방해 수준이
 * NONE / MINOR / MEANINGFUL / SEVERE
 * 가 되었는지를 추적하기 위한 Evidence다.
 */
public enum EarthlyBranchCombinationObstructionReasonType {

    NO_OBSTRUCTION(
            "결합 구성원에 영향을 주는 충·형·파·해 관계가 없습니다."
    ),

    INTERNAL_MEMBER_OBSTRUCTION(
            "결합 구성원끼리 직접 충·형·파·해 관계를 형성합니다."
    ),

    EXTERNAL_MEMBER_OBSTRUCTION(
            "결합 구성원이 결합 외부 지지와 충·형·파·해 관계를 형성합니다."
    ),

    CLASH_PRESENT(
            "결합 구성원에 충 관계가 존재합니다."
    ),

    PUNISHMENT_PRESENT(
            "결합 구성원에 형 관계가 존재합니다."
    ),

    BREAK_PRESENT(
            "결합 구성원에 파 관계가 존재합니다."
    ),

    HARM_PRESENT(
            "결합 구성원에 해 관계가 존재합니다."
    ),

    ADJACENT_OBSTRUCTION(
            "결합 구성원과 인접 궁위에서 방해 관계가 발생합니다."
    ),

    DISTANT_ONLY_OBSTRUCTION(
            "확인된 방해 관계가 모두 원거리 관계입니다."
    ),

    CARDINAL_BRANCH_AFFECTED(
            "삼합·방합의 중심 왕지가 방해 관계에 직접 관여합니다."
    ),

    CARDINAL_BRANCH_CLASHED(
            "삼합·방합의 중심 왕지가 충 관계에 직접 관여합니다."
    ),

    MULTIPLE_MEMBERS_AFFECTED(
            "결합 후보의 둘 이상의 구성원이 방해 관계에 관여합니다."
    ),

    MULTIPLE_OBSTRUCTION_RELATIONS(
            "하나의 결합 후보에 둘 이상의 방해 Pair가 존재합니다."
    ),

    MULTIPLE_OBSTRUCTION_KINDS(
            "하나의 결합 후보에 둘 이상의 충·형·파·해 종류가 중첩됩니다."
    ),

    INTERNAL_MULTIPLE_OBSTRUCTION_KINDS(
            "결합 내부에서 둘 이상의 충·형·파·해 종류가 중첩됩니다."
    ),

    MINOR_OBSTRUCTION_STATE(
            "방해 Evidence는 있으나 현재 구조에서는 경미한 수준입니다."
    ),

    MEANINGFUL_OBSTRUCTION_STATE(
            "결합 성립도를 실제로 조정할 필요가 있는 방해 구조입니다."
    ),

    SEVERE_OBSTRUCTION_STATE(
            "결합 구조를 강하게 흔드는 복합 방해가 존재합니다."
    );

    private final String koreanDescription;

    EarthlyBranchCombinationObstructionReasonType(
            String koreanDescription
    ) {

        this.koreanDescription = koreanDescription;
    }

    public String koreanDescription() {

        return koreanDescription;
    }
}