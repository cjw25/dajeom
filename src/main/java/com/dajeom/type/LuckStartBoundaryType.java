package com.dajeom.type;

/**
 * 정밀화 13차-1.
 *
 * 대운 기산을 계산할 때
 * 출생시점의 어느 절입을 기준으로 삼을 것인지 나타낸다.
 *
 * =================================================
 *
 * NEXT_MONTH_BOUNDARY
 *
 * 순행자의 기준.
 *
 * 출생시점 이후의 가장 가까운
 * 월 절입까지의 시간을 사용한다.
 *
 * =================================================
 *
 * PREVIOUS_MONTH_BOUNDARY
 *
 * 역행자의 기준.
 *
 * 출생시점 이전의 가장 가까운
 * 월 절입부터 출생시점까지의 시간을 사용한다.
 *
 * =================================================
 *
 * 실제 시간 차이를 몇 세 몇 개월로 환산하는 것은
 * 13-2 LuckStartCalculator의 책임이다.
 */
public enum LuckStartBoundaryType {

    NEXT_MONTH_BOUNDARY(
            "다음 절입 기준"
    ),

    PREVIOUS_MONTH_BOUNDARY(
            "이전 절입 기준"
    );

    private final String koreanName;

    LuckStartBoundaryType(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean nextBoundary() {

        return this
                == NEXT_MONTH_BOUNDARY;
    }

    public boolean previousBoundary() {

        return this
                == PREVIOUS_MONTH_BOUNDARY;
    }
}