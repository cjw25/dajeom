package com.dajeom.type;

/**
 * 정밀화 7차-2.
 *
 * 억부용신 후보의 내부 선정 근거.
 *
 * 사용자용 최종 문장이 아니라
 * Explainability Evidence다.
 */
public enum BalancingUsefulGodReasonType {

    /*
     * =================================================
     * 강약
     * =================================================
     */

    EXTREME_WEAK_DAY_MASTER(
            "일간이 극도로 약함"
    ),

    WEAK_DAY_MASTER(
            "일간이 약함"
    ),

    BALANCED_WEAK_DAY_MASTER(
            "일간이 약간 약함"
    ),

    BALANCED_DAY_MASTER(
            "일간이 중화권"
    ),

    BALANCED_STRONG_DAY_MASTER(
            "일간이 약간 강함"
    ),

    STRONG_DAY_MASTER(
            "일간이 강함"
    ),

    EXTREME_STRONG_DAY_MASTER(
            "일간이 극도로 강함"
    ),

    /*
     * =================================================
     * 신약 방향
     * =================================================
     */

    RESOURCE_SUPPORT_DIRECTION(
            "인성 생조 방향"
    ),

    COMPANION_SUPPORT_DIRECTION(
            "비겁 보강 방향"
    ),

    RESOURCE_PRIORITY_FOR_OUTPUT_DRAIN(
            "식상 설기 과다를 인성으로 조절"
    ),

    RESOURCE_PRIORITY_FOR_OFFICER_PRESSURE(
            "관살 압력을 인성으로 통화"
    ),

    COMPANION_PRIORITY_FOR_WEALTH_PRESSURE(
            "재성 부담에 대한 비겁 보강"
    ),

    /*
     * =================================================
     * 신강 방향
     * =================================================
     */

    OUTPUT_RELEASE_DIRECTION(
            "식상 설기 방향"
    ),

    WEALTH_RELEASE_DIRECTION(
            "재성 소모 방향"
    ),

    OFFICER_CONTROL_DIRECTION(
            "관살 극제 방향"
    ),

    OUTPUT_PRIORITY_FOR_EXCESS_SUPPORT(
            "생조 과다를 식상으로 설기"
    ),

    /*
     * =================================================
     * 실제 세력
     * =================================================
     */

    LOW_EXISTING_ELEMENT_POWER(
            "현재 해당 오행 세력이 부족"
    ),

    MODERATE_EXISTING_ELEMENT_POWER(
            "현재 해당 오행 세력이 보통"
    ),

    HIGH_EXISTING_ELEMENT_POWER(
            "현재 해당 오행 세력이 많음"
    ),

    SATURATED_ELEMENT_POWER(
            "현재 해당 오행 세력이 과다"
    ),

    RELATIVELY_SCARCE_PREFERRED_ROLE(
            "억부 방향 후보 중 상대적으로 부족한 오행"
    ),

    /*
     * =================================================
     * 종격 / 억부 억제
     * =================================================
     */

    FOLLOW_STRUCTURE_SUPPRESSION(
            "종격 주격이므로 일반 억부 후보를 조건부로 제한"
    ),

    /*
     * =================================================
     * 후보 상태
     * =================================================
     */

    PREFERRED_BALANCING_ROLE(
            "현재 억부 방향의 우선 역할"
    ),

    SUPPORTING_BALANCING_ROLE(
            "현재 억부 방향의 보조 역할"
    ),

    NO_BALANCING_DIRECTION(
            "중화권이라 적극적인 억부 방향 없음"
    );

    private final String koreanName;

    BalancingUsefulGodReasonType(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean pathologyReason() {

        return this == RESOURCE_PRIORITY_FOR_OUTPUT_DRAIN
                || this == RESOURCE_PRIORITY_FOR_OFFICER_PRESSURE
                || this == COMPANION_PRIORITY_FOR_WEALTH_PRESSURE
                || this == OUTPUT_PRIORITY_FOR_EXCESS_SUPPORT;
    }

    public boolean presenceReason() {

        return this == LOW_EXISTING_ELEMENT_POWER
                || this == MODERATE_EXISTING_ELEMENT_POWER
                || this == HIGH_EXISTING_ELEMENT_POWER
                || this == SATURATED_ELEMENT_POWER
                || this == RELATIVELY_SCARCE_PREFERRED_ROLE;
    }
}