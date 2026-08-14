package com.dajeom.type;

import java.util.Objects;

/**
 * 정밀화 6차-6.
 *
 * 五行 상극 관계와
 * 그 사이를 통관하는 오행을 정의한다.
 *
 * =================================================
 *
 * 木剋土
 * 木 → 火 → 土
 *
 * 火剋金
 * 火 → 土 → 金
 *
 * 土剋水
 * 土 → 金 → 水
 *
 * 金剋木
 * 金 → 水 → 木
 *
 * 水剋火
 * 水 → 木 → 火
 *
 * =================================================
 *
 * 통관은 "극을 없앤다"기보다
 * 생의 흐름을 만들어
 * 두 세력의 직접 충돌을 완화하는
 * 구조로 취급한다.
 */
public enum PassageConflictType {

    WOOD_CONTROLS_EARTH(
            "목토 대립",
            FiveElement.WOOD,
            FiveElement.EARTH,
            FiveElement.FIRE
    ),

    FIRE_CONTROLS_METAL(
            "화금 대립",
            FiveElement.FIRE,
            FiveElement.METAL,
            FiveElement.EARTH
    ),

    EARTH_CONTROLS_WATER(
            "토수 대립",
            FiveElement.EARTH,
            FiveElement.WATER,
            FiveElement.METAL
    ),

    METAL_CONTROLS_WOOD(
            "금목 대립",
            FiveElement.METAL,
            FiveElement.WOOD,
            FiveElement.WATER
    ),

    WATER_CONTROLS_FIRE(
            "수화 대립",
            FiveElement.WATER,
            FiveElement.FIRE,
            FiveElement.WOOD
    );

    private final String koreanName;

    private final FiveElement controllerElement;

    private final FiveElement controlledElement;

    private final FiveElement bridgeElement;

    PassageConflictType(
            String koreanName,
            FiveElement controllerElement,
            FiveElement controlledElement,
            FiveElement bridgeElement
    ) {

        this.koreanName =
                koreanName;

        this.controllerElement =
                controllerElement;

        this.controlledElement =
                controlledElement;

        this.bridgeElement =
                bridgeElement;
    }

    public String koreanName() {

        return koreanName;
    }

    public FiveElement controllerElement() {

        return controllerElement;
    }

    public FiveElement controlledElement() {

        return controlledElement;
    }

    public FiveElement bridgeElement() {

        return bridgeElement;
    }

    public boolean involves(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return false;
        }

        return controllerElement == element
                || controlledElement == element;
    }

    public boolean controllerIs(
            FiveElement element
    ) {

        return controllerElement
                == element;
    }

    public boolean controlledIs(
            FiveElement element
    ) {

        return controlledElement
                == element;
    }

    public boolean bridgeIs(
            FiveElement element
    ) {

        return bridgeElement
                == element;
    }

    public FiveElement opposingElementOf(
            FiveElement element
    ) {

        Objects.requireNonNull(
                element,
                "오행이 필요합니다."
        );

        if (
                controllerElement
                        == element
        ) {

            return controlledElement;
        }

        if (
                controlledElement
                        == element
        ) {

            return controllerElement;
        }

        throw new IllegalArgumentException(
                "해당 오행은 이 상극 관계에 포함되지 않습니다: "
                        + element
        );
    }
}