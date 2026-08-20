package com.dajeom.type;

/**
 * 대운 Function Input이
 * 원국 DayMaster Strength 체계에서
 * 어느 쪽에 속하는지를 나타낸다.
 *
 * =================================================
 *
 * SUPPORT_SIDE
 *
 * 4-5 지원력 계열:
 *
 * - SELF_ASSERTION = 비겁
 * - PROCESSING     = 인성
 *
 * =================================================
 *
 * PRESSURE_SIDE
 *
 * 4-6 압력 계열:
 *
 * - EXPRESSION       = 식상
 * - RESOURCE_REALITY = 재성
 * - RESPONSIBILITY   = 관살
 *
 * =================================================
 *
 * 중요:
 *
 * SUPPORT_SIDE = 좋음
 * PRESSURE_SIDE = 나쁨
 *
 * 이 아니다.
 *
 * 오직 기존 DayMasterStrengthAnalysis에서
 * 어떤 기능축으로 분류되는지를 보존한다.
 */
public enum DecadeCoreCapacitySide {

    SUPPORT_SIDE,

    PRESSURE_SIDE;

    public boolean supportSide() {

        return this == SUPPORT_SIDE;
    }

    public boolean pressureSide() {

        return this == PRESSURE_SIDE;
    }

    public static DecadeCoreCapacitySide from(
            NatalTenGodFunctionType functionType
    ) {

        if (
                functionType == null
        ) {

            throw new IllegalArgumentException(
                    "대운 Capacity Side 판정에는 기능축이 필요합니다."
            );
        }

        return switch (
                functionType
                ) {

            case SELF_ASSERTION,
                 PROCESSING ->
                    SUPPORT_SIDE;

            case EXPRESSION,
                 RESOURCE_REALITY,
                 RESPONSIBILITY ->
                    PRESSURE_SIDE;
        };
    }
}