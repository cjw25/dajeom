package com.dajeom.type;

/**
 * 정밀화 6차-6.
 *
 * 상극 관계 사이에서
 * 통관(通關)이 어느 정도 필요한지를 나타낸다.
 *
 * NONE
 * 통관을 적극적으로 검토할 필요가 없음.
 *
 * LOW
 * 상극 관계는 있으나
 * 구조적인 충돌이 약하거나
 * 이미 중간 오행이 충분한 상태.
 *
 * MODERATE
 * 통관 필요성이 의미 있게 존재.
 *
 * HIGH
 * 강한 상극 대립과
 * 부족한 중간 흐름이 동시에 존재.
 *
 * CRITICAL
 * 매우 강한 대립,
 * 중간 흐름 부재,
 * 일간 불리 등의 조건이 겹친 상태.
 *
 * 주의:
 *
 * 실제 위험도나 확률이 아니다.
 * 명리 엔진 내부의 상대적 구조 단계다.
 */
public enum PassageNeedLevel {

    NONE(
            "통관 필요 없음",
            0
    ),

    LOW(
            "낮은 통관 필요",
            1
    ),

    MODERATE(
            "중간 통관 필요",
            2
    ),

    HIGH(
            "높은 통관 필요",
            3
    ),

    CRITICAL(
            "매우 높은 통관 필요",
            4
    );

    private final String koreanName;

    private final int rank;

    PassageNeedLevel(
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
}