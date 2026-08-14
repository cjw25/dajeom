package com.dajeom.type;

/**
 * 정밀화 6차-1.
 *
 * 원국의 한난(寒暖) 환경 상태.
 *
 * 내부 temperature index:
 *
 * 음수
 * -> 寒 방향
 *
 * 0 부근
 * -> 온도 균형
 *
 * 양수
 * -> 暖/熱 방향
 *
 * 주의:
 *
 * 실제 기온이나 과학적 온도값이 아니다.
 *
 * 명리 엔진에서 조후 분석을 수행하기 위한
 * 내부 상대 지표다.
 */
public enum ClimateTemperatureState {

    SEVERE_COLD(
            "한기 강함",
            -2
    ),

    COLD(
            "한기",
            -1
    ),

    BALANCED(
            "한난 중화",
            0
    ),

    WARM(
            "온기",
            1
    ),

    SEVERE_HEAT(
            "열기 강함",
            2
    );

    private final String koreanName;

    private final int direction;

    ClimateTemperatureState(
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

    public boolean severeCold() {

        return this == SEVERE_COLD;
    }

    public boolean cold() {

        return this == SEVERE_COLD
                || this == COLD;
    }

    public boolean balanced() {

        return this == BALANCED;
    }

    public boolean warm() {

        return this == WARM
                || this == SEVERE_HEAT;
    }

    public boolean severeHeat() {

        return this == SEVERE_HEAT;
    }

    public boolean extreme() {

        return severeCold()
                || severeHeat();
    }
}