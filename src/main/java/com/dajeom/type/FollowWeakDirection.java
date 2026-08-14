package com.dajeom.type;

/**
 * 종약 계열 후보가 발생했을 때
 * 어느 세력이 원국을 주도하는지를
 * 보조적으로 기록한다.
 *
 * NONE
 * 종약 후보가 아님.
 *
 * OUTPUT
 * 식상 중심.
 * 이후 격국 분석에서 종아 방향 후보로
 * 참고할 수 있다.
 *
 * WEALTH
 * 재성 중심.
 * 이후 종재 방향 후보로 참고.
 *
 * OFFICER
 * 관살 중심.
 * 이후 종관/종살 방향 후보로 참고.
 *
 * MIXED
 * 식상·재성·관살 중
 * 하나가 명확하게 독점하지 않는
 * 혼합 약세 구조.
 *
 * 이 enum 자체가 격국을 확정하지 않는다.
 */
public enum FollowWeakDirection {

    NONE(
            "해당 없음"
    ),

    OUTPUT(
            "식상 중심 종약 후보"
    ),

    WEALTH(
            "재성 중심 종약 후보"
    ),

    OFFICER(
            "관살 중심 종약 후보"
    ),

    MIXED(
            "혼합 종약 후보"
    );

    private final String koreanName;

    FollowWeakDirection(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean none() {

        return this == NONE;
    }

    public boolean output() {

        return this == OUTPUT;
    }

    public boolean wealth() {

        return this == WEALTH;
    }

    public boolean officer() {

        return this == OFFICER;
    }

    public boolean mixed() {

        return this == MIXED;
    }
}