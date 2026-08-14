package com.dajeom.type;

/**
 * 정밀화 7차-9.
 *
 * 용신·희신·기신 최종 관계 분류.
 *
 * =================================================
 *
 * USEFUL_GOD
 *
 * 7-8에서 이미 주용신으로 선택된 오행.
 *
 * =================================================
 *
 * FAVORABLE_GOD
 *
 * 희신.
 *
 * 주용신을 생조하거나,
 * 주용신의 기능을 보완하거나,
 * 주용신을 공격하는 구조를 완화하는 오행.
 *
 * =================================================
 *
 * UNFAVORABLE_GOD
 *
 * 기신.
 *
 * 주용신을 직접 손상시키거나,
 * 실제 종격 흐름을 깨거나,
 * 핵심 CORE 구조와 직접 충돌하는 오행.
 *
 * 단순히 현재 세력이 많다는 이유만으로
 * 기신이 되지는 않는다.
 *
 * =================================================
 *
 * CONDITIONAL
 *
 * 긍정·부정 Evidence가 동시에 존재하거나
 * 주용신은 돕지만 다른 핵심 부담도 존재하는 경우.
 *
 * =================================================
 *
 * NEUTRAL
 *
 * 현재 용신·희신·기신으로 분류할
 * 충분한 근거가 없는 오행.
 */
public enum UsefulGodRelationRole {

    USEFUL_GOD(
            "용신",
            5
    ),

    FAVORABLE_GOD(
            "희신",
            4
    ),

    UNFAVORABLE_GOD(
            "기신",
            3
    ),

    CONDITIONAL(
            "조건부",
            2
    ),

    NEUTRAL(
            "중립",
            1
    );

    private final String koreanName;

    private final int rank;

    UsefulGodRelationRole(
            String koreanName,
            int rank
    ) {

        this.koreanName =
                koreanName;

        this.rank =
                rank;
    }

    public String koreanName() {

        return koreanName;
    }

    public int rank() {

        return rank;
    }

    public boolean usefulGod() {

        return this == USEFUL_GOD;
    }

    public boolean favorableGod() {

        return this == FAVORABLE_GOD;
    }

    public boolean unfavorableGod() {

        return this == UNFAVORABLE_GOD;
    }

    public boolean conditional() {

        return this == CONDITIONAL;
    }

    public boolean neutral() {

        return this == NEUTRAL;
    }

    public boolean positive() {

        return this == USEFUL_GOD
                || this == FAVORABLE_GOD;
    }

    public boolean negative() {

        return this == UNFAVORABLE_GOD;
    }

    public boolean resolved() {

        return this != CONDITIONAL;
    }
}