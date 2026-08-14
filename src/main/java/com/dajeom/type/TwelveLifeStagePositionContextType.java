package com.dajeom.type;

/**
 * 정밀화 11차-4.
 *
 * 12운성이 원국의 어느 지지에서 나타났는지를
 * 해석하기 위한 위치 Context.
 *
 * =================================================
 *
 * 중요:
 *
 * 다음처럼 가족관계를 기계적으로 고정하지 않는다.
 *
 * YEAR  = 부모
 * MONTH = 형제
 * DAY   = 배우자
 * HOUR  = 자녀
 *
 * 이런 1:1 단정은 12차 궁위 분석의 책임도 침범한다.
 *
 * =================================================
 *
 * YEAR_BACKGROUND
 *
 * 원국의 바깥 배경·초기 기반에 가까운 위치.
 *
 * =================================================
 *
 * MONTH_ENVIRONMENT
 *
 * 계절·사회적 환경 및 원국 중심 환경에 가까운 위치.
 *
 * =================================================
 *
 * DAY_CORE
 *
 * 일간과 가장 직접적으로 맞닿아 있는
 * 핵심 생활 Context.
 *
 * =================================================
 *
 * HOUR_EXTENSION
 *
 * 원국에서 앞으로 확장되는 방향,
 * 후반부 및 외부로 전개되는 Context.
 *
 * =================================================
 *
 * 길흉 우선순위가 아니다.
 */
public enum TwelveLifeStagePositionContextType {

    YEAR_BACKGROUND(
            "배경·기반"
    ),

    MONTH_ENVIRONMENT(
            "환경·전개 기반"
    ),

    DAY_CORE(
            "핵심 생활축"
    ),

    HOUR_EXTENSION(
            "확장·후반 Context"
    );

    private final String koreanName;

    TwelveLifeStagePositionContextType(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean yearBackground() {

        return this == YEAR_BACKGROUND;
    }

    public boolean monthEnvironment() {

        return this == MONTH_ENVIRONMENT;
    }

    public boolean dayCore() {

        return this == DAY_CORE;
    }

    public boolean hourExtension() {

        return this == HOUR_EXTENSION;
    }
}