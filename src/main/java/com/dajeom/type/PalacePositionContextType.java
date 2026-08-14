package com.dajeom.type;

/**
 * 정밀화 12차-1.
 *
 * 각 궁위가 원국에서 담당하는
 * 중립적인 위치 Context.
 *
 * =================================================
 *
 * BACKGROUND_FOUNDATION
 *
 * 원국의 외부 배경,
 * 초기 기반,
 * 비교적 바깥쪽 조건.
 *
 * YEAR에 대응.
 *
 * =================================================
 *
 * ENVIRONMENTAL_OPERATION
 *
 * 계절 및 원국의 환경,
 * 사회적 작동 기반.
 *
 * MONTH에 대응.
 *
 * =================================================
 *
 * CORE_LIFE_AXIS
 *
 * 일간과 가장 직접적으로 맞닿아 있는
 * 핵심 생활축.
 *
 * DAY에 대응.
 *
 * =================================================
 *
 * EXTENSION_LATER_DEVELOPMENT
 *
 * 현재 원국에서 앞으로 뻗어나가는
 * 확장·후반 전개 영역.
 *
 * HOUR에 대응.
 *
 * =================================================
 *
 * 이 Context 역시 가족관계를
 * 직접 확정하지 않는다.
 */
public enum PalacePositionContextType {

    BACKGROUND_FOUNDATION(
            "배경·초기 기반"
    ),

    ENVIRONMENTAL_OPERATION(
            "환경·사회적 작동"
    ),

    CORE_LIFE_AXIS(
            "핵심 생활축"
    ),

    EXTENSION_LATER_DEVELOPMENT(
            "확장·후반 전개"
    );

    private final String koreanName;

    PalacePositionContextType(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean backgroundFoundation() {

        return this
                == BACKGROUND_FOUNDATION;
    }

    public boolean environmentalOperation() {

        return this
                == ENVIRONMENTAL_OPERATION;
    }

    public boolean coreLifeAxis() {

        return this
                == CORE_LIFE_AXIS;
    }

    public boolean extensionLaterDevelopment() {

        return this
                == EXTENSION_LATER_DEVELOPMENT;
    }
}