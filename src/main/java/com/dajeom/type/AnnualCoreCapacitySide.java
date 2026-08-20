package com.dajeom.type;

import java.util.Objects;

/**
 * 세운 Functional Input이
 * 기존 DayMasterStrengthAnalysis의
 * 어느 측에 속하는지를 나타낸다.
 *
 * =================================================
 *
 * SUPPORT_SIDE
 *
 * 기존 4-5 지원측:
 *
 * - 비겁
 * - 인성
 *
 * =================================================
 *
 * PRESSURE_SIDE
 *
 * 기존 4-6 압력측:
 *
 * - 식상
 * - 재성
 * - 관살
 *
 * =================================================
 *
 * 중요:
 *
 * 이것은 길흉이 아니다.
 *
 * SUPPORT_SIDE = 좋음
 * PRESSURE_SIDE = 나쁨
 *
 * 으로 해석하지 않는다.
 *
 * 또한 세운 때문에
 * 일간 신강신약을 다시 계산하지 않는다.
 *
 * 원국에서 이미 확정된
 * NatalCoreSelfEffect를 기준으로
 * 어떤 방향의 Function이 접촉했는지만 본다.
 */
public enum AnnualCoreCapacitySide {

    SUPPORT_SIDE,

    PRESSURE_SIDE;

    public boolean supportSide() {

        return this == SUPPORT_SIDE;
    }

    public boolean pressureSide() {

        return this == PRESSURE_SIDE;
    }

    public static AnnualCoreCapacitySide from(
            NatalTenGodFunctionType functionType
    ) {

        Objects.requireNonNull(
                functionType,
                "Annual Capacity Side 판정에는 Function Type이 필요합니다."
        );

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