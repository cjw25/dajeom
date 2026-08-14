package com.dajeom.type;

/**
 * 정밀화 6차-3.
 *
 * 특정 조후 후보 오행이
 * 현재 원국 전체 세력에서
 * 어느 정도 존재하는지를 분류한다.
 *
 * 이 값은 오행의 길흉이나
 * 용신 여부가 아니다.
 *
 * 3차에서 이미 계산된 오행 비중을
 * "현재 얼마나 확보돼 있는가"라는
 * 관점으로만 재분류한다.
 */
public enum ClimateElementPresenceLevel {

    ABSENT(
            "거의 없음",
            0
    ),

    LOW(
            "부족",
            1
    ),

    MODERATE(
            "보통",
            2
    ),

    HIGH(
            "많음",
            3
    ),

    VERY_HIGH(
            "매우 많음",
            4
    );

    private final String koreanName;

    private final int rank;

    ClimateElementPresenceLevel(
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

    public boolean absent() {

        return this == ABSENT;
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

    public boolean veryHigh() {

        return this == VERY_HIGH;
    }

    public boolean scarce() {

        return this == ABSENT
                || this == LOW;
    }

    public boolean abundant() {

        return this == HIGH
                || this == VERY_HIGH;
    }

    public boolean saturated() {

        return this == VERY_HIGH;
    }

    /**
     * 3차 오행 비중을
     * 조후 후보의 현재 보유 상태로 변환한다.
     *
     * 10000bp = 전체 오행 세력의 100%.
     *
     * 절대적인 명리 법칙이 아니라
     * 다점 내부 후보선정 정책이다.
     */
    public static ClimateElementPresenceLevel
    fromShareBasisPoints(
            int shareBasisPoints
    ) {

        if (
                shareBasisPoints < 0
                        || shareBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "오행 비중은 0~10000bp 사이여야 합니다."
            );
        }

        if (
                shareBasisPoints < 100
        ) {

            return ABSENT;
        }

        if (
                shareBasisPoints < 1_200
        ) {

            return LOW;
        }

        if (
                shareBasisPoints < 2_500
        ) {

            return MODERATE;
        }

        if (
                shareBasisPoints < 3_500
        ) {

            return HIGH;
        }

        return VERY_HIGH;
    }
}