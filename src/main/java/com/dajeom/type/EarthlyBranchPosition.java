package com.dajeom.type;

/**
 * 지지의 원국 위치.
 *
 * 정밀화 8차의:
 *
 * - 육합
 * - 삼합
 * - 방합
 * - 충
 * - 형
 * - 파
 * - 해
 *
 * 에서 공통으로 사용한다.
 *
 * 순서는 반드시:
 *
 * YEAR
 * MONTH
 * DAY
 * HOUR
 *
 * 이다.
 *
 * ordinal()을 이용해
 * 원국 위치 거리와 인접 여부를 계산한다.
 */
public enum EarthlyBranchPosition {

    YEAR(
            "년지"
    ),

    MONTH(
            "월지"
    ),

    DAY(
            "일지"
    ),

    HOUR(
            "시지"
    );

    private final String koreanName;

    EarthlyBranchPosition(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean year() {

        return this == YEAR;
    }

    public boolean month() {

        return this == MONTH;
    }

    public boolean day() {

        return this == DAY;
    }

    public boolean hour() {

        return this == HOUR;
    }
}