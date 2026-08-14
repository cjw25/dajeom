package com.dajeom.type;

/**
 * 정밀화 11차-4.
 *
 * 12운성 분석이 원국의 어느 범위까지
 * 확인되었는지를 나타내는 Scope.
 *
 * =================================================
 *
 * FULL_CHART
 *
 * 시주까지 존재하여
 * YEAR / MONTH / DAY / HOUR
 * 전체 분석이 가능한 상태.
 *
 * =================================================
 *
 * THREE_PILLAR_ONLY
 *
 * 출생시간 미상으로
 * YEAR / MONTH / DAY만 분석 가능한 상태.
 *
 * =================================================
 *
 * THREE_PILLAR_ONLY가
 * YEAR/MONTH/DAY 계산 자체가 불확실하다는 뜻은 아니다.
 *
 * 알려진 세 지주의 12운성은 그대로 확정값이다.
 *
 * 단지 시주를 포함한 "원국 전체 종합"의
 * 범위가 제한된다는 뜻이다.
 */
public enum TwelveLifeStageContextScope {

    FULL_CHART(
            "4주 전체"
    ),

    THREE_PILLAR_ONLY(
            "시주 제외 3주"
    );

    private final String koreanName;

    TwelveLifeStageContextScope(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean fullChart() {

        return this == FULL_CHART;
    }

    public boolean threePillarOnly() {

        return this == THREE_PILLAR_ONLY;
    }

    public boolean hourUnknown() {

        return threePillarOnly();
    }
}