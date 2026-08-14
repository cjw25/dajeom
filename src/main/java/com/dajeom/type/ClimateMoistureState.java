package com.dajeom.type;

/**
 * 정밀화 6차-1.
 *
 * 원국의 조습(燥濕) 환경 상태.
 *
 * 내부 moisture index:
 *
 * 음수
 * -> 燥 방향
 *
 * 0 부근
 * -> 조습 균형
 *
 * 양수
 * -> 濕 방향
 *
 * 실제 습도값이 아니라
 * 조후 분석을 위한 내부 상대 지표다.
 */
public enum ClimateMoistureState {

    SEVERE_DRY(
            "조기 강함",
            -2
    ),

    DRY(
            "조기",
            -1
    ),

    BALANCED(
            "조습 중화",
            0
    ),

    WET(
            "습기",
            1
    ),

    SEVERE_WET(
            "습기 강함",
            2
    );

    private final String koreanName;

    private final int direction;

    ClimateMoistureState(
            String koreanName,
            int direction
    ) {

        this.koreanName =
                koreanName;

        this.direction =
                direction;
    }

    public String koreanName() {

        return koreanName;
    }

    public int direction() {

        return direction;
    }

    public boolean severeDry() {

        return this == SEVERE_DRY;
    }

    public boolean dry() {

        return this == SEVERE_DRY
                || this == DRY;
    }

    public boolean balanced() {

        return this == BALANCED;
    }

    public boolean wet() {

        return this == WET
                || this == SEVERE_WET;
    }

    public boolean severeWet() {

        return this == SEVERE_WET;
    }

    public boolean extreme() {

        return severeDry()
                || severeWet();
    }
}