package com.dajeom.engine;

import com.dajeom.calculator.DayPillarCalculator;
import com.dajeom.calculator.HourPillarCalculator;
import com.dajeom.calculator.MonthPillarCalculator;
import com.dajeom.calculator.YearPillarCalculator;
import com.dajeom.exception.BirthTimeRequiredException;
import com.dajeom.model.BirthTimeContext;
import com.dajeom.model.CalendarNormalizationResult;
import com.dajeom.model.FourPillars;
import com.dajeom.model.Pillar;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;

/**
 * 사주 네 기둥 계산을 통합하는 엔진.
 *
 * 역할:
 *
 * 1. 년주 계산
 * 2. 월주 계산
 * 3. 일주 계산
 * 4. 시주 계산
 * 5. 출생시간 미상 처리
 * 6. 절입 경계 모호성 차단
 *
 * 이 엔진은 음력 → 양력 변환을 직접 수행하지 않는다.
 *
 * 달력 정규화:
 * KoreanLunarCalendarCalculator
 *
 * 사주팔자 계산:
 * FourPillarsCalculationEngine
 *
 * 으로 책임을 분리한다.
 */
@Component
public class FourPillarsCalculationEngine {

    private final YearPillarCalculator
            yearPillarCalculator;

    private final MonthPillarCalculator
            monthPillarCalculator;

    private final DayPillarCalculator
            dayPillarCalculator;

    private final HourPillarCalculator
            hourPillarCalculator;

    public FourPillarsCalculationEngine(

            YearPillarCalculator yearPillarCalculator,

            MonthPillarCalculator monthPillarCalculator,

            DayPillarCalculator dayPillarCalculator,

            HourPillarCalculator hourPillarCalculator

    ) {

        this.yearPillarCalculator =
                Objects.requireNonNull(
                        yearPillarCalculator,
                        "YearPillarCalculator가 필요합니다."
                );

        this.monthPillarCalculator =
                Objects.requireNonNull(
                        monthPillarCalculator,
                        "MonthPillarCalculator가 필요합니다."
                );

        this.dayPillarCalculator =
                Objects.requireNonNull(
                        dayPillarCalculator,
                        "DayPillarCalculator가 필요합니다."
                );

        this.hourPillarCalculator =
                Objects.requireNonNull(
                        hourPillarCalculator,
                        "HourPillarCalculator가 필요합니다."
                );
    }

    /**
     * 핵심 계산 메서드.
     *
     * BirthTimeContext가
     * 출생시간 존재 여부를 명확하게 가지고 있기 때문에
     * 가짜 출생시간을 만들 필요가 없다.
     */
    public FourPillars calculate(
            BirthTimeContext context
    ) {

        Objects.requireNonNull(
                context,
                "출생시간 Context가 필요합니다."
        );

        if (
                context.hasKnownBirthTime()
        ) {

            return calculateKnownBirthTime(
                    context
            );
        }

        return calculateUnknownBirthTime(
                context
        );
    }

    /**
     * 달력 정규화 결과까지 받은 경우.
     *
     * 음력 입력이든 양력 입력이든
     * CalendarNormalizationResult.calculationDate()
     * 즉 양력 날짜를 사용한다.
     */
    public FourPillars calculate(

            CalendarNormalizationResult calendarResult,

            LocalTime birthTime,

            boolean birthTimeUnknown,

            ZoneId zoneId

    ) {

        Objects.requireNonNull(
                calendarResult,
                "달력 정규화 결과가 필요합니다."
        );

        Objects.requireNonNull(
                zoneId,
                "시간대가 필요합니다."
        );

        BirthTimeContext context;

        if (
                birthTimeUnknown
        ) {

            context =
                    BirthTimeContext.unknown(
                            calendarResult.calculationDate(),
                            zoneId
                    );

        } else {

            if (
                    birthTime == null
            ) {

                throw new IllegalArgumentException(
                        "출생시간 미상이 아니라면 "
                                + "출생시간이 필요합니다."
                );
            }

            context =
                    BirthTimeContext.known(
                            calendarResult.calculationDate(),
                            birthTime,
                            zoneId
                    );
        }

        return calculate(
                context
        );
    }

    /**
     * 기존 호출부에서 LocalDateTime을 사용하는 경우를 위한
     * 편의 메서드.
     *
     * 이 경우는 출생시간을 알고 있는 것으로 본다.
     */
    public FourPillars calculate(

            LocalDateTime birthDateTime,

            ZoneId zoneId

    ) {

        Objects.requireNonNull(
                birthDateTime,
                "출생 날짜와 시간이 필요합니다."
        );

        Objects.requireNonNull(
                zoneId,
                "시간대가 필요합니다."
        );

        BirthTimeContext context =
                BirthTimeContext.known(
                        birthDateTime.toLocalDate(),
                        birthDateTime.toLocalTime(),
                        zoneId
                );

        return calculate(
                context
        );
    }

    /**
     * 날짜만 전달된 경우.
     *
     * 출생시간 미상으로 처리한다.
     */
    public FourPillars calculate(

            LocalDate birthDate,

            ZoneId zoneId

    ) {

        Objects.requireNonNull(
                birthDate,
                "출생 날짜가 필요합니다."
        );

        Objects.requireNonNull(
                zoneId,
                "시간대가 필요합니다."
        );

        return calculate(
                BirthTimeContext.unknown(
                        birthDate,
                        zoneId
                )
        );
    }

    /**
     * 출생시간이 알려진 경우.
     *
     * 4주 8자를 모두 계산한다.
     */
    private FourPillars calculateKnownBirthTime(
            BirthTimeContext context
    ) {

        LocalDateTime birthDateTime =
                context.requireBirthDateTime();

        ZoneId zoneId =
                context.zoneId();

        /*
         * 년주
         *
         * 실제 입춘 시각을 기준으로 계산한다.
         */
        Pillar yearPillar =
                yearPillarCalculator.calculate(
                        birthDateTime,
                        zoneId
                );

        /*
         * 월주
         *
         * 실제 절입 시각을 기준으로 계산한다.
         */
        Pillar monthPillar =
                monthPillarCalculator.calculate(
                        birthDateTime,
                        zoneId
                );

        /*
         * 일주
         *
         * 다점 정책:
         * 현지 민간 날짜 00:00 기준.
         *
         * 야자시 일주 변경 없음.
         */
        Pillar dayPillar =
                dayPillarCalculator.calculate(
                        context.solarBirthDate()
                );

        /*
         * 시주
         *
         * 일간 + 실제 입력 출생시간으로 계산한다.
         */
        LocalTime birthTime =
                context.birthTimeOptional()
                        .orElseThrow(
                                BirthTimeRequiredException
                                        ::forHourPillar
                        );

        Pillar hourPillar =
                hourPillarCalculator.calculate(
                        dayPillar,
                        birthTime
                );

        return new FourPillars(
                yearPillar,
                monthPillar,
                dayPillar,
                hourPillar
        );
    }

    /**
     * 출생시간을 모르는 경우.
     *
     * 원칙:
     *
     * 년주:
     * 날짜만으로 확정 가능하면 계산
     *
     * 월주:
     * 날짜만으로 확정 가능하면 계산
     *
     * 일주:
     * 항상 계산
     *
     * 시주:
     * 계산하지 않음
     *
     * 입춘/절입 당일이면
     * 임의 시간을 넣지 않고 예외 발생.
     */
    private FourPillars calculateUnknownBirthTime(
            BirthTimeContext context
    ) {

        LocalDate birthDate =
                context.solarBirthDate();

        ZoneId zoneId =
                context.zoneId();

        /*
         * 년주 판단.
         *
         * 입춘 당일이면 Optional.empty().
         */
        Optional<Pillar> yearPillarOptional =
                yearPillarCalculator.calculate(
                        birthDate,
                        zoneId
                );

        Pillar yearPillar =
                yearPillarOptional
                        .orElseThrow(
                                () ->
                                        BirthTimeRequiredException
                                                .forYearBoundary(
                                                        birthDate
                                                )
                        );

        /*
         * 월주 판단.
         *
         * 12개 절입 당일이면 Optional.empty().
         */
        Optional<Pillar> monthPillarOptional =
                monthPillarCalculator.calculate(
                        birthDate,
                        zoneId
                );

        Pillar monthPillar =
                monthPillarOptional
                        .orElseThrow(
                                () ->
                                        BirthTimeRequiredException
                                                .forMonthBoundary(
                                                        birthDate
                                                )
                        );

        /*
         * 일주는 출생시간이 없어도 계산 가능.
         */
        Pillar dayPillar =
                dayPillarCalculator.calculate(
                        birthDate
                );

        /*
         * 출생시간 미상.
         *
         * 시주를 만들지 않는다.
         */
        Pillar hourPillar =
                null;

        return new FourPillars(
                yearPillar,
                monthPillar,
                dayPillar,
                hourPillar
        );
    }
}