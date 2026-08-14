package com.dajeom.type;

/**
 * 정밀화 6차-9.
 *
 * 6-8 조후·병약·통관 통합 후보를
 * 신강·신약과 격국에 다시 대조한 결과.
 *
 * SUPPORTED
 * 신강신약 또는 격국 구조가
 * 해당 오행 후보를 의미 있게 지지.
 *
 * NEUTRAL
 * 특별한 지지나 충돌이 없음.
 *
 * STRAINED
 * 후보의 작용은 가능하지만
 * 일간 강약 또는 격국에 부담이 존재.
 *
 * CONTRADICTORY
 * 현재 핵심 구조와 직접 충돌.
 *
 * 주의:
 *
 * 길흉 판정이나 확률이 아니다.
 */
public enum ClimateHealthCrossValidationState {

    SUPPORTED(
            "교차검증 지지",
            4
    ),

    NEUTRAL(
            "교차검증 중립",
            3
    ),

    STRAINED(
            "교차검증 부담",
            2
    ),

    CONTRADICTORY(
            "교차검증 충돌",
            1
    );

    private final String koreanName;

    private final int rank;

    ClimateHealthCrossValidationState(
            String koreanName,
            int rank
    ) {

        this.koreanName =
                koreanName;

        this.rank =
                rank;
    }

    public String koreanName() {

        return koreanName;
    }

    public int rank() {

        return rank;
    }

    public boolean supported() {

        return this == SUPPORTED;
    }

    public boolean neutral() {

        return this == NEUTRAL;
    }

    public boolean strained() {

        return this == STRAINED;
    }

    public boolean contradictory() {

        return this == CONTRADICTORY;
    }

    public boolean acceptable() {

        return this == SUPPORTED
                || this == NEUTRAL;
    }

    public boolean requiresCaution() {

        return this == STRAINED
                || this == CONTRADICTORY;
    }
}