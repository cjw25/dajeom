package com.dajeom.type;

/**
 * 정밀화 5차-6.
 *
 * 하나의 파격(破格) 요소가
 * 원국 안에서 어느 정도 실제 근거를
 * 가지고 있는지를 나타낸다.
 *
 * STRONG
 * 강한 파격 신호.
 *
 * ACTIVE
 * 실제 방해 구조가 작동할 근거가 있음.
 *
 * WEAK
 * 관련 구조는 있으나 영향 근거가 약함.
 *
 * NONE
 * 해당 파격 구조가 확인되지 않음.
 *
 * 주의:
 *
 * STRONG이라고 해서
 * 최종적으로 파격 확정이라는 뜻은 아니다.
 *
 * 5-5 성격 요소와
 * 5-7 순도·혼잡도를 같이 봐야 한다.
 */
public enum StructureDamageLevel {

    STRONG(
            "강한 파격 요소",
            3
    ),

    ACTIVE(
            "파격 요소 작동",
            2
    ),

    WEAK(
            "약한 파격 요소",
            1
    ),

    NONE(
            "파격 요소 없음",
            0
    );

    private final String koreanName;

    private final int rank;

    StructureDamageLevel(
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

    public boolean strong() {

        return this == STRONG;
    }

    public boolean active() {

        return this == ACTIVE;
    }

    public boolean weak() {

        return this == WEAK;
    }

    public boolean none() {

        return this == NONE;
    }

    public boolean meaningful() {

        return this == STRONG
                || this == ACTIVE;
    }

    public boolean exists() {

        return this != NONE;
    }
}