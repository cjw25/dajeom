package com.dajeom.type;

/**
 * 정밀화 9차-3.
 *
 * 천간합 합화 성립도 판정 근거.
 *
 * =================================================
 *
 * 9-1의 기본 환경과
 * 9-2의 경쟁·방해 상태를
 * 최종 상태로 연결하기 위한 Evidence.
 *
 * =================================================
 *
 * 점수 합산 방식으로 사용하지 않는다.
 *
 * Reason은:
 *
 * "왜 이 상태가 되었는가"
 *
 * 를 추적하기 위한 설명 Evidence다.
 */
public enum HeavenlyStemCombinationTransformationReasonType {

    /*
     * =================================================
     * 9-1 기본 적용도
     * =================================================
     */

    STRONG_BASE_CONDITION(
            "합화 목표 오행의 기본 환경이 강합니다."
    ),

    ACTIVE_BASE_CONDITION(
            "합화 목표 오행의 기본 환경이 활성 상태입니다."
    ),

    CONDITIONAL_BASE_CONDITION(
            "합화 목표 오행의 기본 환경에 추가 조건이 필요합니다."
    ),

    WEAK_BASE_CONDITION(
            "합화 목표 오행의 기본 환경이 약합니다."
    ),

    /*
     * =================================================
     * 9-2 경쟁
     * =================================================
     */

    NO_COMPETITION(
            "동일 천간을 공유하는 경쟁 합 후보가 없습니다."
    ),

    WEAKER_COMPETITION(
            "경쟁 합 후보가 존재하지만 현재 후보보다 기본 적용도가 낮습니다."
    ),

    EQUAL_OR_STRONGER_COMPETITION(
            "동급 또는 더 강한 경쟁 합 후보가 존재합니다."
    ),

    DAY_MASTER_MULTIPLE_COMBINATION(
            "일간이 둘 이상의 천간합 후보에 동시에 참여합니다."
    ),

    /*
     * =================================================
     * 9-2 외부 방해
     * =================================================
     */

    EXTERNAL_OBSTRUCTION_PRESENT(
            "합 구성원에 외부 천간 충·극 방해가 존재합니다."
    ),

    ADJACENT_EXTERNAL_OBSTRUCTION(
            "합 구성원과 인접한 외부 천간의 방해가 존재합니다."
    ),

    MULTIPLE_EXTERNAL_OBSTRUCTIONS(
            "합 구성원에 복수의 외부 방해가 존재합니다."
    ),

    /*
     * =================================================
     * 합화 결과
     * =================================================
     */

    FULL_TRANSFORMATION_CONDITIONS_MET(
            "강한 합화 환경과 비경쟁 구조가 함께 형성되었습니다."
    ),

    PARTIAL_TRANSFORMATION_CONDITIONS_MET(
            "합화 방향은 의미 있게 작동하지만 완전한 합화 조건에는 부족합니다."
    ),

    TRANSFORMATION_REQUIRES_CONDITIONS(
            "합화 가능성은 있으나 추가 조건 검토가 필요합니다."
    ),

    COMBINATION_REMAINS_WITHOUT_TRANSFORMATION(
            "천간합 관계는 유지되지만 실제 합화는 성립하지 않습니다."
    ),

    TRANSFORMATION_SUPPRESSED(
            "합화 환경이 약하거나 경쟁·방해로 합화 작용이 억제됩니다."
    );

    private final String koreanDescription;

    HeavenlyStemCombinationTransformationReasonType(
            String koreanDescription
    ) {

        this.koreanDescription =
                koreanDescription;
    }

    public String koreanDescription() {

        return koreanDescription;
    }
}