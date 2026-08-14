package com.dajeom.type;

/**
 * 정밀화 16차-1.
 *
 * 운 분석 기준시각의 입력 정밀도.
 *
 * =================================================
 *
 * EXACT_DATE_TIME
 *
 * 사용자가 실제 날짜와 시각을 제공했다.
 *
 * 예:
 *
 * 2026-08-20 15:30
 *
 * 이 경우 대운·세운·월운 모두
 * 해당 정확한 순간을 기준으로 선택할 수 있다.
 *
 * =================================================
 *
 * DATE_ONLY
 *
 * 사용자가 날짜만 제공했다.
 *
 * 예:
 *
 * 2026-08-20
 *
 * 이 경우 임의로:
 *
 * 00:00
 * 12:00
 * 23:59
 *
 * 등을 생성하지 않는다.
 *
 * 그 날짜 전체를 가능한 시간범위로 유지한다.
 *
 * =================================================
 *
 * 날짜 안에:
 *
 * - 입춘
 * - 월 절입
 * - 대운 교체
 *
 * 가 존재한다면 복수 Candidate가
 * 발생할 수 있다.
 */
public enum LuckReferencePrecision {

    EXACT_DATE_TIME(
            "정확한 일시"
    ),

    DATE_ONLY(
            "날짜만 확인"
    );

    private final String koreanName;

    LuckReferencePrecision(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean exactDateTime() {

        return this
                == EXACT_DATE_TIME;
    }

    public boolean dateOnly() {

        return this
                == DATE_ONLY;
    }
}