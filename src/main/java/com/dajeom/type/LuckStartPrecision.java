package com.dajeom.type;

/**
 * 정밀화 13차-2.
 *
 * 대운 기산 시점의 확정 범위.
 *
 * =================================================
 *
 * EXACT
 *
 * 출생시간이 확인되어
 * 하나의 정확한 출생시각을 기준으로 계산한 상태.
 *
 * =================================================
 *
 * TIME_UNKNOWN_RANGE
 *
 * 출생일은 알지만 시간이 미상인 상태.
 *
 * 00:00 ~ 23:59:59 범위에서
 * 가능한 기산 결과를 계산한다.
 *
 * =================================================
 *
 * SOLAR_TERM_BOUNDARY_UNCERTAIN
 *
 * 출생시간이 미상이고,
 * 출생일 안에 월 절입 자체가 존재하는 상태.
 *
 * 이 경우 출생시각에 따라:
 *
 * - 절입 이전
 * - 절입 이후
 *
 * 가 달라질 수 있으므로
 * 일반적인 하루 내 시간 오차보다
 * 불확실성이 훨씬 크다.
 *
 * =================================================
 *
 * 중요:
 *
 * 불확실하다고 정오나 자정을
 * 가짜 출생시간으로 사용하지 않는다.
 */
public enum LuckStartPrecision {

    EXACT(
            "정확한 출생시간 기준"
    ),

    TIME_UNKNOWN_RANGE(
            "출생시간 미상 범위"
    ),

    SOLAR_TERM_BOUNDARY_UNCERTAIN(
            "절입 경계 포함 시간 미상"
    );

    private final String koreanName;

    LuckStartPrecision(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean exact() {

        return this == EXACT;
    }

    public boolean range() {

        return this != EXACT;
    }

    public boolean solarTermBoundaryUncertain() {

        return this
                == SOLAR_TERM_BOUNDARY_UNCERTAIN;
    }
}