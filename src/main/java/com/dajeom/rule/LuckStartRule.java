package com.dajeom.rule;

import com.dajeom.model.LuckStartAge;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 대운 시작 나이 계산 Rule.
 *
 * V1 환산 기준:
 *
 * 실제 절입 거리 3일
 * =
 * 대운 나이 1년
 *
 * 따라서:
 *
 * 1일 = 4개월
 * 6시간 = 1개월
 * 2시간 = 10일
 * 1시간 = 5일
 * 12분 = 1일
 *
 * =================================================
 *
 * 명리상의 환산년은:
 *
 * 1년 = 12개월
 * 1개월 = 30일
 * 1년 = 360일
 *
 * 기준이다.
 *
 * 이는 Gregorian Calendar의 실제
 * 월별 일수와는 별개의 symbolic age 계산이다.
 */
public final class LuckStartRule {

    public static final String RULE_VERSION =
            "DAJEOM_LUCK_START_V1";

    /**
     * 실제 시간 12분
     * =
     * 대운 기산 나이 1일.
     */
    private static final long
            REAL_SECONDS_PER_AGE_DAY =
            12L * 60L;

    private static final int
            AGE_DAYS_PER_MONTH =
            30;

    private static final int
            AGE_MONTHS_PER_YEAR =
            12;

    private static final int
            AGE_DAYS_PER_YEAR =
            AGE_DAYS_PER_MONTH
                    * AGE_MONTHS_PER_YEAR;

    private LuckStartRule() {

        throw new IllegalStateException(
                "LuckStartRule은 생성할 수 없습니다."
        );
    }

    /**
     * 실제 출생시각과 절입 사이의 Duration을
     * 대운 시작 나이로 변환한다.
     *
     * =================================================
     *
     * 현재 LuckStartAge는:
     *
     * years
     * months
     * days
     *
     * 세 값만 보존한다.
     *
     * 이전 V1의 decimalYears 값은
     * 더 이상 모델에 저장하지 않는다.
     */
    public static LuckStartAge convert(
            Duration interval
    ) {

        Objects.requireNonNull(
                interval,
                "절입 시간 간격은 필수입니다."
        );

        if (
                interval.isNegative()
        ) {

            throw new IllegalArgumentException(
                    "절입 시간 간격은 음수일 수 없습니다."
            );
        }

        /*
         * 실제 12분을
         * 대운 나이 1일로 환산한다.
         */
        long totalAgeDays =
                Math.round(
                        interval.getSeconds()
                                / (double)
                                REAL_SECONDS_PER_AGE_DAY
                );

        int years =
                (int) (
                        totalAgeDays
                                / AGE_DAYS_PER_YEAR
                );

        long remainingDays =
                totalAgeDays
                        % AGE_DAYS_PER_YEAR;

        int months =
                (int) (
                        remainingDays
                                / AGE_DAYS_PER_MONTH
                );

        int days =
                (int) (
                        remainingDays
                                % AGE_DAYS_PER_MONTH
                );

        return new LuckStartAge(
                years,
                months,
                days
        );
    }

    /**
     * 출생시각에 대운 시작 나이를 더해
     * 실제 대운 시작시점을 계산한다.
     *
     * LuckStartAge의:
     *
     * years
     * months
     * days
     *
     * 를 실제 Calendar에 순서대로 더한다.
     */
    public static ZonedDateTime
    calculateStartDateTime(
            ZonedDateTime birthDateTime,
            LuckStartAge startAge
    ) {

        Objects.requireNonNull(
                birthDateTime,
                "출생시각은 필수입니다."
        );

        Objects.requireNonNull(
                startAge,
                "대운 시작 나이는 필수입니다."
        );

        return birthDateTime
                .plusYears(
                        startAge.years()
                )
                .plusMonths(
                        startAge.months()
                )
                .plusDays(
                        startAge.days()
                );
    }
}