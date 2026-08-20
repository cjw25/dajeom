package com.dajeom.type;

/**
 * Annual CrossFunctional Pattern에 연결된
 * 14-6 Context Signal의 시간 범위.
 *
 * =================================================
 *
 * ANNUAL_COMMON
 *
 * AnnualLuckPeriod 전체에 공통인
 * 원국 × 세운 Context.
 *
 * =================================================
 *
 * DECADE_SEGMENT
 *
 * 특정 대운과 해당 세운이
 * 함께 작동하는 Segment에서만
 * 존재하는 Context.
 *
 * =================================================
 *
 * 두 Scope를 합쳐서
 * 독립 Evidence 수로 세지 않는다.
 *
 * 이것은 시간적 provenance다.
 */
public enum AnnualCrossContextScope {

    ANNUAL_COMMON,

    DECADE_SEGMENT;

    public boolean annualCommon() {

        return this == ANNUAL_COMMON;
    }

    public boolean decadeSegment() {

        return this == DECADE_SEGMENT;
    }
}