package com.dajeom.type;

/**
 * 정밀화 5차-5.
 *
 * 하나의 성격(成格) 보조 패턴이
 * 원국 안에서 어느 정도 실제 근거를
 * 가지고 있는지 나타낸다.
 *
 * STRONG
 * 보조 오행의 세력도 충분하고
 * 천간 투출 또는 지장간 투출 근거도 명확함.
 *
 * ACTIVE
 * 실제 보조 구조가 작동할 근거가 있음.
 *
 * WEAK
 * 관련 오행은 존재하지만
 * 성격 요소로서 표현력이 약함.
 *
 * NONE
 * 해당 보조구조를 만들 수 있는
 * 실질적인 source가 없음.
 *
 * 주의:
 *
 * 격국 최종 점수나 확률이 아니다.
 */
public enum StructureSupportLevel {

    STRONG(
            "강한 성격 요소",
            3
    ),

    ACTIVE(
            "성격 요소 작동",
            2
    ),

    WEAK(
            "약한 성격 요소",
            1
    ),

    NONE(
            "성격 요소 없음",
            0
    );

    private final String koreanName;

    private final int rank;

    StructureSupportLevel(
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