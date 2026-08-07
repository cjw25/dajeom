package com.dajeom.calculator;

import com.dajeom.model.LuckStartAge;
import com.dajeom.model.NormalizedBirthData;
import com.dajeom.model.SolarTermPoint;
import com.dajeom.type.LuckDirection;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 대운 시작 시점을 계산한다.
 *
 * 전통적인 3일 = 1년 환산 방식을 사용한다.
 *
 * 환산 기준:
 *
 * 실제 절입 차이 3일
 * = 대운 나이 1년
 *
 * 실제 절입 차이 6시간
 * = 대운 나이 1개월
 *
 * 실제 절입 차이 12분
 * = 대운 나이 1일
 *
 * 순행:
 * 출생 이후 다음 절입까지의 시간을 사용한다.
 *
 * 역행:
 * 출생 이전 직전 절입까지의 시간을 사용한다.
 */
@Component
public class LuckStartCalculator {

    /**
     * 3일 = 72시간 = 4,320분
     */
    private static final long MINUTES_PER_LUCK_YEAR =
            4_320L;

    /**
     * 6시간 = 360분
     */
    private static final long MINUTES_PER_LUCK_MONTH =
            360L;

    /**
     * 12분 = 대운 나이 1일
     */
    private static final double MINUTES_PER_LUCK_DAY =
            12.0;

    private final SolarTermCalculator
            solarTermCalculator;

    public LuckStartCalculator(
            SolarTermCalculator solarTermCalculator
    ) {

        this.solarTermCalculator =
                solarTermCalculator;
    }

    /**
     * 대운 시작 나이와 시작일,
     * 계산 기준 절입을 반환한다.
     */
    public LuckStartResult calculate(

            NormalizedBirthData birthData,

            LuckDirection direction
    ) {

        Objects.requireNonNull(
                birthData,
                "출생정보는 필수입니다."
        );

        Objects.requireNonNull(
                direction,
                "대운 방향은 필수입니다."
        );

        boolean forward =
                direction.getStep() > 0;

        /*
         * 순행은 다음 절입,
         * 역행은 이전 절입을 기준으로 한다.
         */
        SolarTermPoint referencePoint =
                forward

                        ? solarTermCalculator
                        .findNextMonthBoundary(
                                birthData.birthDateTime(),
                                birthData.zoneId()
                        )

                        : solarTermCalculator
                        .findPreviousMonthBoundary(
                                birthData.birthDateTime(),
                                birthData.zoneId()
                        );

        ZonedDateTime birthDateTime =
                birthData
                        .birthDateTime()
                        .atZone(
                                birthData.zoneId()
                        );

        ZonedDateTime referenceDateTime =
                referencePoint.dateTime();

        /*
         * 출생시각과 기준 절입의
         * 실제 분 차이를 계산한다.
         */
        long differenceMinutes =
                Math.abs(
                        Duration.between(
                                birthDateTime,
                                referenceDateTime
                        ).toMinutes()
                );

        ConvertedLuckAge convertedAge =
                convertDifference(
                        differenceMinutes
                );

        /*
         * 3일 = 1년 방식의
         * 소수점 나이 표현이다.
         */
        double decimalYears =
                roundDecimalYears(
                        differenceMinutes
                                / (double)
                                MINUTES_PER_LUCK_YEAR
                );

        LuckStartAge startAge =
                new LuckStartAge(

                        convertedAge.years(),

                        convertedAge.months(),

                        convertedAge.days(),

                        decimalYears
                );

        /*
         * 대운 실제 시작 날짜
         */
        LocalDate startDate =
                birthData
                        .birthDate()
                        .plusYears(
                                convertedAge.years()
                        )
                        .plusMonths(
                                convertedAge.months()
                        )
                        .plusDays(
                                convertedAge.days()
                        );

        return new LuckStartResult(

                startAge,

                startDate,

                referencePoint
        );
    }

    /**
     * 실제 출생시각과 절입의 차이를
     * 대운 시작 나이의 연·월·일로 환산한다.
     */
    private ConvertedLuckAge convertDifference(
            long differenceMinutes
    ) {

        long remainingMinutes =
                differenceMinutes;

        int years =
                (int) (
                        remainingMinutes
                                / MINUTES_PER_LUCK_YEAR
                );

        remainingMinutes =
                remainingMinutes
                        % MINUTES_PER_LUCK_YEAR;

        int months =
                (int) (
                        remainingMinutes
                                / MINUTES_PER_LUCK_MONTH
                );

        remainingMinutes =
                remainingMinutes
                        % MINUTES_PER_LUCK_MONTH;

        int days =
                (int) Math.round(
                        remainingMinutes
                                / MINUTES_PER_LUCK_DAY
                );

        /*
         * 30일 이상이면
         * 1개월로 올린다.
         */
        if (days >= 30) {

            days -=
                    30;

            months++;
        }

        /*
         * 12개월 이상이면
         * 1년으로 올린다.
         */
        if (months >= 12) {

            years +=
                    months / 12;

            months =
                    months % 12;
        }

        return new ConvertedLuckAge(

                years,

                months,

                days
        );
    }

    /**
     * decimalYears가 지나치게 긴
     * 실수값으로 노출되지 않도록 정리한다.
     */
    private double roundDecimalYears(
            double value
    ) {

        return Math.round(
                value * 1_000_000.0
        ) / 1_000_000.0;
    }

    /**
     * LuckAnalysisEngine에서 사용하는
     * 대운 시작 계산 결과다.
     */
    public record LuckStartResult(

            LuckStartAge startAge,

            LocalDate startDate,

            SolarTermPoint referenceSolarTerm
    ) {

        public LuckStartResult {

            Objects.requireNonNull(
                    startAge,
                    "대운 시작 나이는 필수입니다."
            );

            Objects.requireNonNull(
                    startDate,
                    "대운 시작 날짜는 필수입니다."
            );

            Objects.requireNonNull(
                    referenceSolarTerm,
                    "기준 절입은 필수입니다."
            );
        }
    }

    /**
     * 계산 과정에서만 사용하는
     * 연·월·일 변환 결과다.
     */
    private record ConvertedLuckAge(

            int years,

            int months,

            int days
    ) {
    }
}