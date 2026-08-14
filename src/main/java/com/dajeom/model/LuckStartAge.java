package com.dajeom.model;

/**
 * 정밀화 13차-2.
 *
 * 전통적인 대운 기산 환산 결과.
 *
 * =================================================
 *
 * 내부 정규화 기준:
 *
 * 1년 = 12개월
 * 1개월 = 30일
 *
 * =================================================
 *
 * 이는 실제 Gregorian Calendar의
 * 한 달 길이가 항상 30일이라는 뜻이 아니다.
 *
 * 오직:
 *
 * 3 실제일 = 기산 1년
 *
 * 환산을:
 *
 * 년 / 개월 / 일
 *
 * 형태로 정규화하기 위한
 * symbolic age 단위다.
 */
public record LuckStartAge(

        int years,

        int months,

        int days

) implements Comparable<LuckStartAge> {

    private static final int
            MONTHS_PER_YEAR = 12;

    private static final int
            DAYS_PER_MONTH = 30;

    private static final int
            DAYS_PER_YEAR =
            MONTHS_PER_YEAR
                    * DAYS_PER_MONTH;

    public LuckStartAge {

        if (
                years < 0
        ) {

            throw new IllegalArgumentException(
                    "대운 시작 년수는 음수일 수 없습니다."
            );
        }

        if (
                months < 0
                        || months >= MONTHS_PER_YEAR
        ) {

            throw new IllegalArgumentException(
                    "대운 시작 개월은 0~11 범위여야 합니다."
            );
        }

        if (
                days < 0
                        || days >= DAYS_PER_MONTH
        ) {

            throw new IllegalArgumentException(
                    "대운 시작 일수는 0~29 범위여야 합니다."
            );
        }
    }

    /**
     * 비교를 위한 전통 환산 총일수.
     *
     * 실제 생존 일수나 Gregorian 날짜 차이가 아니다.
     */
    public long totalTraditionalDays() {

        return (long) years
                * DAYS_PER_YEAR

                + (long) months
                * DAYS_PER_MONTH

                + days;
    }

    public boolean zero() {

        return years == 0
                && months == 0
                && days == 0;
    }

    public String displayName() {

        return years
                + "년 "
                + months
                + "개월 "
                + days
                + "일";
    }

    @Override
    public int compareTo(
            LuckStartAge other
    ) {

        if (
                other == null
        ) {

            return 1;
        }

        return Long.compare(
                totalTraditionalDays(),
                other.totalTraditionalDays()
        );
    }
}