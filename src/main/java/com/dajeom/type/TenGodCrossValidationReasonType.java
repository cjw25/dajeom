package com.dajeom.type;

/**
 * 정밀화 10차-9.
 *
 * 십성 Pattern 교차검증 근거.
 *
 * 점수 합산용이 아니다.
 */
public enum TenGodCrossValidationReasonType {

    /*
     * =================================================
     * 10-8 원래 Pattern
     * =================================================
     */

    CORE_PATTERN_REFERENCE(
            "10-8 Pattern이 참고 수준입니다."
    ),

    CORE_PATTERN_MEANINGFUL(
            "10-8 Pattern이 유의미한 수준입니다."
    ),

    CORE_PATTERN_STRONG(
            "10-8 Pattern이 강한 수준입니다."
    ),

    /*
     * =================================================
     * 격국
     * =================================================
     */

    STRUCTURE_ESTABLISHED(
            "격국이 안정적으로 성립된 상태입니다."
    ),

    STRUCTURE_ESTABLISHED_WITH_STRAIN(
            "격국은 성립하지만 일부 부담 또는 긴장이 존재합니다."
    ),

    STRUCTURE_COMPROMISED(
            "격국이 일부 손상된 상태입니다."
    ),

    STRUCTURE_UNSTABLE(
            "격국이 불안정하여 Pattern 해석력을 낮춰야 합니다."
    ),

    STRUCTURE_SPECIAL_REVIEW(
            "특수격 또는 별도 구조 검토가 필요한 상태입니다."
    ),

    /*
     * =================================================
     * 신강·신약
     * =================================================
     */

    STRENGTH_SUPPORTS_PATTERN(
            "일간 강약 상태가 해당 Pattern의 구조적 의미를 뒷받침합니다."
    ),

    STRENGTH_REQUIRES_QUALIFICATION(
            "Pattern은 존재하지만 일간 강약에 따라 해석을 제한해야 합니다."
    ),

    STRENGTH_CONFLICTS_PATTERN(
            "Pattern의 전제와 현재 일간 강약 판정이 충돌합니다."
    ),

    EXTREME_WEAKNESS_REQUIRES_CAUTION(
            "극약 방향에서는 제어·재성·관살 Pattern의 일반 해석을 그대로 적용하기 어렵습니다."
    ),

    STRONG_DAY_MASTER_SUPPORTS_COMPANION_COMPETITION(
            "신강 방향에서는 비겁 경쟁 구조의 작용성이 높아질 수 있습니다."
    ),

    WEAK_DAY_MASTER_CHANGES_COMPANION_FUNCTION(
            "신약 방향에서는 비겁이 경쟁뿐 아니라 보조 기능도 가질 수 있습니다."
    ),

    /*
     * =================================================
     * 용신
     * =================================================
     */

    USEFUL_GOD_SUPPORTS_PATTERN(
            "용신 분석이 해당 십성 Pattern의 작용 방향을 뒷받침합니다."
    ),

    USEFUL_GOD_CONFLICTS_PATTERN(
            "용신 분석이 해당 Pattern의 일반적인 작용 방향과 충돌합니다."
    ),

    USEFUL_GOD_METHOD_DISAGREEMENT(
            "용신 방법론 사이에 Pattern에 대한 지지와 충돌이 함께 존재합니다."
    ),

    USEFUL_GOD_NEUTRAL(
            "용신 분석에서 해당 Pattern을 강화하거나 약화하는 결정적 근거가 없습니다."
    ),

    /*
     * =================================================
     * 합화
     * =================================================
     */

    TRANSFORMATION_SUPPORTS_PATTERN(
            "합화 결과가 해당 Pattern의 관련 기능을 추가로 활성화합니다."
    ),

    TRANSFORMATION_WEAKENS_PATTERN(
            "합화로 관련 십성의 원래 기능이 일부 감소합니다."
    ),

    TRANSFORMATION_CONFLICTS_PATTERN(
            "합화 결과가 Pattern의 핵심 전제를 크게 흔듭니다."
    ),

    TRANSFORMATION_MIXED_EFFECT(
            "합화에서 Pattern을 강화하는 요소와 약화하는 요소가 동시에 존재합니다."
    ),

    TRANSFORMATION_NEUTRAL(
            "합화가 해당 Pattern에 의미 있는 기능 변화를 만들지 않습니다."
    ),

    /*
     * =================================================
     * 시간 미상
     * =================================================
     */

    INCOMPLETE_CHART_REQUIRES_QUALIFICATION(
            "출생시간 미상으로 시주가 없어 최종 확정도를 한 단계 제한합니다."
    ),

    /*
     * =================================================
     * 최종
     * =================================================
     */

    FINAL_SUPPORTED(
            "교차검증 결과 Pattern이 다른 분석축에서도 지지됩니다."
    ),

    FINAL_QUALIFIED(
            "Pattern은 유지되지만 조건부 해석이 필요합니다."
    ),

    FINAL_WEAKENED(
            "Pattern은 존재하지만 실제 작용력이 약화된 것으로 판단합니다."
    ),

    FINAL_CONFLICTED(
            "서로 다른 분석축이 상반된 결과를 보여 충돌 상태로 보존합니다."
    );

    private final String koreanDescription;

    TenGodCrossValidationReasonType(
            String koreanDescription
    ) {

        this.koreanDescription =
                koreanDescription;
    }

    public String koreanDescription() {

        return koreanDescription;
    }
}