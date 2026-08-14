package com.dajeom.type;

/**
 * 정밀화 6차-2.
 *
 * 한난 또는 조습 축에서
 * 조후 조정이 어느 정도 필요한지를 나타낸다.
 *
 * NONE
 * 실질적인 조정 필요성이 없음.
 *
 * LOW
 * 약한 불균형.
 *
 * MODERATE
 * 분명한 불균형.
 *
 * HIGH
 * 강한 불균형.
 *
 * CRITICAL
 * 극단적인 조후 불균형.
 *
 * 주의:
 *
 * 확률이나 과학적 위험 수치가 아니다.
 *
 * 6-1에서 계산된 명리 내부 상대지표를
 * 조후 분석 단계로 변환한 등급이다.
 */
public enum ClimateAdjustmentNeedLevel {

    NONE(
            "조정 필요 없음",
            0
    ),

    LOW(
            "낮은 조정 필요",
            1
    ),

    MODERATE(
            "중간 조정 필요",
            2
    ),

    HIGH(
            "높은 조정 필요",
            3
    ),

    CRITICAL(
            "매우 높은 조정 필요",
            4
    );

    private final String koreanName;

    private final int rank;

    ClimateAdjustmentNeedLevel(
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

    public boolean none() {

        return this == NONE;
    }

    public boolean low() {

        return this == LOW;
    }

    public boolean moderate() {

        return this == MODERATE;
    }

    public boolean high() {

        return this == HIGH;
    }

    public boolean critical() {

        return this == CRITICAL;
    }

    public boolean required() {

        return this != NONE;
    }

    public boolean meaningful() {

        return this == MODERATE
                || this == HIGH
                || this == CRITICAL;
    }

    public boolean highPriority() {

        return this == HIGH
                || this == CRITICAL;
    }

    public static ClimateAdjustmentNeedLevel max(

            ClimateAdjustmentNeedLevel first,

            ClimateAdjustmentNeedLevel second

    ) {

        if (
                first == null
                        || second == null
        ) {

            throw new IllegalArgumentException(
                    "조후 필요도 비교 대상이 필요합니다."
            );
        }

        return first.rank >= second.rank
                ? first
                : second;
    }
}