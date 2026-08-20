package com.dajeom.type;

/**
 * Annual-Decade Segment에서
 * 하나의 Function의 원래 직접 Input이
 * Transformation Context 안에서
 * 어떤 상태인지 나타낸다.
 *
 * =================================================
 *
 * ABSENT
 *
 * 해당 Layer에 직접 Input 자체가 없음.
 *
 * =================================================
 *
 * RETAINED
 *
 * 직접 Input은 존재하며
 * 원기능 감소가 확인되지 않음.
 *
 * =================================================
 *
 * REDUCED
 *
 * 원기능 감소 Context가 존재.
 *
 * =================================================
 *
 * STRONGLY_REDUCED
 *
 * 강한 원기능 감소 Context가 존재.
 *
 * =================================================
 *
 * MIXED
 *
 * 같은 Function의 여러 Input 또는
 * 같은 Input의 여러 Transformation에서
 *
 * 유지 / 감소 상태가 함께 존재.
 *
 * =================================================
 *
 * 중요:
 *
 * 이 값은 Function 삭제 여부가 아니다.
 *
 * Source Evidence는 항상 보존한다.
 */
public enum AnnualSegmentOriginalInputState {

    ABSENT,

    RETAINED,

    REDUCED,

    STRONGLY_REDUCED,

    MIXED;

    public boolean present() {

        return this != ABSENT;
    }

    public boolean retained() {

        return this == RETAINED;
    }

    public boolean reduced() {

        return this == REDUCED
                || this == STRONGLY_REDUCED
                || this == MIXED;
    }

    public boolean stronglyReduced() {

        return this == STRONGLY_REDUCED;
    }

    public boolean mixed() {

        return this == MIXED;
    }
}