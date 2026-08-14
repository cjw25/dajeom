package com.dajeom.type;

/**
 * 정밀화 7차-1.
 *
 * 특정 용신법의 적용도가 결정된 내부 근거.
 *
 * 사용자용 해석 문장 자체가 아니라
 * Explainability Evidence다.
 */
public enum UsefulGodMethodReasonType {

    /*
     * =================================================
     * 억부
     * =================================================
     */

    EXTREME_DAY_MASTER_IMBALANCE(
            "일간 강약이 극단적으로 치우침"
    ),

    CLEAR_DAY_MASTER_IMBALANCE(
            "일간 강약이 분명하게 치우침"
    ),

    SLIGHT_DAY_MASTER_IMBALANCE(
            "일간 강약이 약간 치우침"
    ),

    BALANCED_DAY_MASTER(
            "일간 강약이 중화권"
    ),

    FOLLOW_STRUCTURE_SUPPRESSES_NORMAL_BALANCING(
            "종격 후보이므로 일반 억부법을 우선 적용하지 않음"
    ),

    /*
     * =================================================
     * 조후
     * =================================================
     */

    CRITICAL_CLIMATE_NEED(
            "조후 조정 필요도가 매우 높음"
    ),

    HIGH_CLIMATE_NEED(
            "조후 조정 필요도가 높음"
    ),

    MODERATE_CLIMATE_NEED(
            "조후 조정 필요도가 의미 있게 존재"
    ),

    LOW_CLIMATE_NEED(
            "조후 조정 필요도가 낮음"
    ),

    NO_CLIMATE_NEED(
            "한난조습이 조후 조정 기준상 중화"
    ),

    /*
     * =================================================
     * 격국
     * =================================================
     */

    STANDARD_STRUCTURE_ESTABLISHED(
            "일반 주격이 성립"
    ),

    STRUCTURE_WITH_STRAIN(
            "주격은 성립하지만 강약 부담 존재"
    ),

    STRUCTURE_COMPROMISED(
            "주격에 의미 있는 구조 손상 존재"
    ),

    STRUCTURE_UNSTABLE(
            "주격 성립이 불안정"
    ),

    SPECIAL_STRUCTURE_PRIMARY(
            "특수격이 주격으로 선택됨"
    ),

    FOLLOW_STRUCTURE_HANDLED_SEPARATELY(
            "종격 주격은 종격용신법에서 별도 처리"
    ),

    /*
     * =================================================
     * 병약
     * =================================================
     */

    CRITICAL_PATHOLOGY(
            "매우 강한 병 구조 존재"
    ),

    HIGH_PATHOLOGY(
            "강한 병 구조 존재"
    ),

    MODERATE_PATHOLOGY(
            "의미 있는 병 구조 존재"
    ),

    LOW_PATHOLOGY(
            "약한 병 구조만 존재"
    ),

    NO_PATHOLOGY(
            "활성 병 구조 없음"
    ),

    /*
     * =================================================
     * 통관
     * =================================================
     */

    CRITICAL_PASSAGE_NEED(
            "통관 필요도가 매우 높음"
    ),

    HIGH_PASSAGE_NEED(
            "통관 필요도가 높음"
    ),

    MODERATE_PASSAGE_NEED(
            "의미 있는 통관 필요 존재"
    ),

    LOW_PASSAGE_NEED(
            "통관 필요도가 낮음"
    ),

    NO_PASSAGE_NEED(
            "의미 있는 통관 병목 없음"
    ),

    /*
     * =================================================
     * 종격
     * =================================================
     */

    FOLLOW_STRUCTURE_PRIMARY(
            "종격 후보가 실제 주격으로 선택됨"
    ),

    NO_FOLLOW_STRUCTURE_PRIMARY(
            "현재 주격이 종격 후보가 아님"
    );

    private final String koreanName;

    UsefulGodMethodReasonType(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }
}