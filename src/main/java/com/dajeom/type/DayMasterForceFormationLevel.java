package com.dajeom.type;

/**
 * 일간 주변의 비겁·인성 지원망 수준.
 *
 * 이것은 최종 신강/신약 등급이 아니다.
 *
 * NONE
 * 지원 기반 없음
 *
 * LIMITED
 * 일부 위치에서만 지원
 *
 * ESTABLISHED
 * 여러 위치에서 지원망 형성
 *
 * WIDESPREAD
 * 천간·지지에 넓게 지원망 형성
 */
public enum DayMasterForceFormationLevel {

    NONE(
            "득세 기반 없음",
            0
    ),

    LIMITED(
            "제한적 득세",
            1
    ),

    ESTABLISHED(
            "득세 기반 형성",
            2
    ),

    WIDESPREAD(
            "강한 득세 기반",
            3
    );

    private final String koreanName;

    private final int rank;

    DayMasterForceFormationLevel(
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

    public boolean limited() {

        return this == LIMITED;
    }

    public boolean established() {

        return this == ESTABLISHED;
    }

    public boolean widespread() {

        return this == WIDESPREAD;
    }

    public boolean hasSupportFormation() {

        return this != NONE;
    }
}