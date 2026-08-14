package com.dajeom.type;

/**
 * 정밀화 10차-8.
 *
 * 핵심 십성 Pattern의 판정 근거.
 *
 * 점수 합산용이 아니다.
 */
public enum TenGodCorePatternReasonType {

    /*
     * =================================================
     * 개별 십성
     * =================================================
     */

    PRIMARY_TEN_GOD_PRESENT(
            "핵심 첫 번째 십성이 원국에 존재합니다."
    ),

    SECONDARY_TEN_GOD_PRESENT(
            "핵심 두 번째 십성이 원국에 존재합니다."
    ),

    PRIMARY_TEN_GOD_MEANINGFUL(
            "첫 번째 십성의 세력이 유의미합니다."
    ),

    SECONDARY_TEN_GOD_MEANINGFUL(
            "두 번째 십성의 세력이 유의미합니다."
    ),

    PRIMARY_TEN_GOD_VISIBLE(
            "첫 번째 십성이 천간에 투출되어 있습니다."
    ),

    SECONDARY_TEN_GOD_VISIBLE(
            "두 번째 십성이 천간에 투출되어 있습니다."
    ),

    PRIMARY_MONTH_ANCHORED(
            "첫 번째 십성이 월주 또는 월지에 자리합니다."
    ),

    SECONDARY_MONTH_ANCHORED(
            "두 번째 십성이 월주 또는 월지에 자리합니다."
    ),

    /*
     * =================================================
     * 세력 관계
     * =================================================
     */

    PRIMARY_APPROACHES_SECONDARY_POWER(
            "첫 번째 세력이 두 번째 세력에 충분히 접근합니다."
    ),

    PRIMARY_EXCEEDS_SECONDARY_POWER(
            "첫 번째 세력이 두 번째 세력보다 강합니다."
    ),

    SECONDARY_EXCEEDS_PRIMARY_POWER(
            "두 번째 세력이 첫 번째 세력보다 강합니다."
    ),

    BOTH_SIDES_MEANINGFUL(
            "Pattern을 구성하는 양쪽 세력이 모두 유의미합니다."
    ),

    /*
     * =================================================
     * 10-7 Family 방향성
     * =================================================
     */

    FAMILY_INTERACTION_PRESENT(
            "10-7 Family 방향성 관계가 존재합니다."
    ),

    FAMILY_INTERACTION_MEANINGFUL(
            "10-7 Family 방향성 관계가 유의미합니다."
    ),

    FAMILY_INTERACTION_STRONG(
            "10-7 Family 방향성 관계가 강합니다."
    ),

    /*
     * =================================================
     * 교차분석
     * =================================================
     */

    WEAK_DAY_MASTER(
            "일간이 신약 방향에 있습니다."
    ),

    COMPANION_WEALTH_CONTEST_CORROBORATED(
            "10-2 비겁 분석에서도 재성 경쟁 구조가 확인됩니다."
    ),

    WEALTH_PRESSURE_CORROBORATED(
            "10-4 재성 분석에서도 비겁 압박 구조가 확인됩니다."
    ),

    WEALTH_BURDEN_CORROBORATED(
            "10-4 재성 분석에서 신약 재성 부담 구조가 확인됩니다."
    ),

    OFFICER_KILLINGS_MIXTURE_CORROBORATED(
            "10-5 관살 분석에서도 정관·칠살 혼재가 확인됩니다."
    ),

    OUTPUT_CONTROL_CONTEXT_CORROBORATED(
            "10-5 관살 분석에서 식상 제어 Context가 확인됩니다."
    ),

    RESOURCE_OUTPUT_CONTEXT_CORROBORATED(
            "10-6 인성 분석에서 식상 제약 Context가 확인됩니다."
    ),

    OFFICER_RESOURCE_CONTEXT_CORROBORATED(
            "10-5·10-6 분석에서 관살·인성 연결 Context가 확인됩니다."
    ),

    WEALTH_OFFICER_CONTEXT_CORROBORATED(
            "10-4·10-5 분석에서 재성·관살 연결 Context가 확인됩니다."
    ),

    WEALTH_RESOURCE_CONTEXT_CORROBORATED(
            "10-4·10-6 분석에서 재성·인성 대립 Context가 확인됩니다."
    ),

    /*
     * =================================================
     * Pattern
     * =================================================
     */

    EATING_GOD_CONTROLS_SEVEN_KILLINGS_PATTERN(
            "식신과 칠살 사이에 제살 구조가 형성되어 있습니다."
    ),

    HURTING_OFFICER_MEETS_DIRECT_OFFICER_PATTERN(
            "상관과 정관이 서로 대립하는 구조가 형성되어 있습니다."
    ),

    HURTING_OFFICER_WITH_RESOURCE_PATTERN(
            "상관과 인성의 제어·조절 구조가 형성되어 있습니다."
    ),

    INDIRECT_RESOURCE_SEIZES_EATING_GOD_PATTERN(
            "편인이 식신을 제약하는 구조가 형성되어 있습니다."
    ),

    WEALTH_GENERATES_DIRECT_OFFICER_PATTERN(
            "재성에서 정관으로 이어지는 생 구조가 형성되어 있습니다."
    ),

    DIRECT_OFFICER_GENERATES_RESOURCE_PATTERN(
            "정관에서 인성으로 이어지는 생 구조가 형성되어 있습니다."
    ),

    SEVEN_KILLINGS_GENERATES_RESOURCE_PATTERN(
            "칠살에서 인성으로 이어지는 생 구조가 형성되어 있습니다."
    ),

    WEALTH_CONTROLS_RESOURCE_PATTERN(
            "재성이 인성을 제약하는 구조가 형성되어 있습니다."
    ),

    COMPANION_CONTESTS_WEALTH_PATTERN(
            "비겁과 재성 사이의 경쟁 구조가 형성되어 있습니다."
    ),

    WEALTH_HEAVY_WEAK_DAY_MASTER_PATTERN(
            "신약 방향의 일간에 재성 부담이 큰 구조입니다."
    ),

    MIXED_OFFICER_KILLINGS_PATTERN(
            "정관과 칠살이 동시에 작동하는 혼재 구조입니다."
    ),

    /*
     * =================================================
     * Level
     * =================================================
     */

    REFERENCE_PATTERN(
            "Pattern이 참고 수준으로 확인됩니다."
    ),

    MEANINGFUL_PATTERN(
            "Pattern이 유의미한 수준으로 확인됩니다."
    ),

    STRONG_PATTERN(
            "Pattern이 강한 수준으로 확인됩니다."
    ),

    /*
     * =================================================
     * 다음 단계
     * =================================================
     */

    REQUIRES_PHASE_10_9_CROSS_VALIDATION(
            "격국·신강신약·용신·합화와의 최종 교차검증이 필요합니다."
    );

    private final String koreanDescription;

    TenGodCorePatternReasonType(
            String koreanDescription
    ) {

        this.koreanDescription =
                koreanDescription;
    }

    public String koreanDescription() {

        return koreanDescription;
    }
}