package com.dajeom.type;

/**
 * 정밀화 6차-1.
 *
 * 한난 + 조습을 결합한
 * 원국의 전체 조후 환경 유형.
 *
 * 이 타입은 아직:
 *
 * - 조후용신
 * - 희신
 * - 기신
 *
 * 을 의미하지 않는다.
 *
 * 현재 원국 환경이 어떤 방향으로
 * 치우쳐 있는지를 표현한다.
 */
public enum ClimateEnvironmentType {

    BALANCED(
            "한난조습 균형"
    ),

    COLD(
            "한기 중심"
    ),

    HEAT(
            "열기 중심"
    ),

    DRY(
            "조기 중심"
    ),

    WET(
            "습기 중심"
    ),

    COLD_DRY(
            "한조"
    ),

    COLD_WET(
            "한습"
    ),

    HEAT_DRY(
            "난조"
    ),

    HEAT_WET(
            "난습"
    );

    private final String koreanName;

    ClimateEnvironmentType(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean balanced() {

        return this == BALANCED;
    }

    public boolean cold() {

        return this == COLD
                || this == COLD_DRY
                || this == COLD_WET;
    }

    public boolean hot() {

        return this == HEAT
                || this == HEAT_DRY
                || this == HEAT_WET;
    }

    public boolean dry() {

        return this == DRY
                || this == COLD_DRY
                || this == HEAT_DRY;
    }

    public boolean wet() {

        return this == WET
                || this == COLD_WET
                || this == HEAT_WET;
    }

    public boolean combinedImbalance() {

        return this == COLD_DRY
                || this == COLD_WET
                || this == HEAT_DRY
                || this == HEAT_WET;
    }
}