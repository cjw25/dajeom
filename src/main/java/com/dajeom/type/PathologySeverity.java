package com.dajeom.type;

/**
 * 정밀화 6차-4.
 *
 * 명리상 병(病) 구조의 상대적 심각도.
 *
 * 주의:
 *
 * 의학적 질병이나 실제 건강 위험도를 의미하지 않는다.
 *
 * 사주 원국 안에서:
 *
 * - 오행 편중
 * - 일간 압박
 * - 한난조습 극단
 * - 격국 손상
 *
 * 등이 어느 정도 구조적인 병목을
 * 형성하는지를 나타내는 내부 단계다.
 */
public enum PathologySeverity {

    NONE(
            "병 구조 없음",
            0
    ),

    LOW(
            "약한 병 구조",
            1
    ),

    MODERATE(
            "중간 병 구조",
            2
    ),

    HIGH(
            "강한 병 구조",
            3
    ),

    CRITICAL(
            "매우 강한 병 구조",
            4
    );

    private final String koreanName;

    private final int rank;

    PathologySeverity(
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

    public boolean exists() {

        return this != NONE;
    }

    public boolean meaningful() {

        return this == MODERATE
                || this == HIGH
                || this == CRITICAL;
    }

    public boolean severe() {

        return this == HIGH
                || this == CRITICAL;
    }

    public static PathologySeverity max(

            PathologySeverity first,

            PathologySeverity second

    ) {

        if (
                first == null
                        || second == null
        ) {

            throw new IllegalArgumentException(
                    "병 구조 심각도 비교 대상이 필요합니다."
            );
        }

        return first.rank >= second.rank
                ? first
                : second;
    }
}