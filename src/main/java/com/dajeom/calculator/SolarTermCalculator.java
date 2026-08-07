package com.dajeom.calculator;

import com.dajeom.config.SajuCalculationPolicy;
import com.dajeom.model.SolarTermPoint;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 입춘과 월주 변경에 필요한 12개 절입 시각을 계산한다.
 */
@Component
public class SolarTermCalculator {

    private static final List<SolarTermDefinition>
            MONTH_BOUNDARY_TERMS = List.of(

            new SolarTermDefinition(
                    "입춘",
                    315.0,
                    Month.FEBRUARY,
                    4,
                    0
            ),

            new SolarTermDefinition(
                    "경칩",
                    345.0,
                    Month.MARCH,
                    6,
                    1
            ),

            new SolarTermDefinition(
                    "청명",
                    15.0,
                    Month.APRIL,
                    5,
                    2
            ),

            new SolarTermDefinition(
                    "입하",
                    45.0,
                    Month.MAY,
                    6,
                    3
            ),

            new SolarTermDefinition(
                    "망종",
                    75.0,
                    Month.JUNE,
                    6,
                    4
            ),

            new SolarTermDefinition(
                    "소서",
                    105.0,
                    Month.JULY,
                    7,
                    5
            ),

            new SolarTermDefinition(
                    "입추",
                    135.0,
                    Month.AUGUST,
                    8,
                    6
            ),

            new SolarTermDefinition(
                    "백로",
                    165.0,
                    Month.SEPTEMBER,
                    8,
                    7
            ),

            new SolarTermDefinition(
                    "한로",
                    195.0,
                    Month.OCTOBER,
                    8,
                    8
            ),

            new SolarTermDefinition(
                    "입동",
                    225.0,
                    Month.NOVEMBER,
                    7,
                    9
            ),

            new SolarTermDefinition(
                    "대설",
                    255.0,
                    Month.DECEMBER,
                    7,
                    10
            ),

            new SolarTermDefinition(
                    "소한",
                    285.0,
                    Month.JANUARY,
                    6,
                    11
            )
    );

    public ZonedDateTime calculateIpchun(
            int year,
            ZoneId zoneId
    ) {
        SolarTermDefinition definition =
                getDefinitionByName("입춘");

        return calculateSolarTerm(
                year,
                definition,
                zoneId
        );
    }

    public int determineSajuYear(
            LocalDateTime birthDateTime,
            ZoneId zoneId
    ) {
        int calendarYear =
                birthDateTime.getYear();

        ZonedDateTime birth =
                birthDateTime.atZone(zoneId);

        ZonedDateTime ipchun =
                calculateIpchun(
                        calendarYear,
                        zoneId
                );

        return birth.isBefore(ipchun)
                ? calendarYear - 1
                : calendarYear;
    }

    public int determineMonthIndex(
            LocalDateTime birthDateTime,
            ZoneId zoneId
    ) {
        return findPreviousMonthBoundary(
                birthDateTime,
                zoneId
        ).monthIndex();
    }

    /**
     * 출생 시점 이전에 가장 가깝게 지난 절입을 반환한다.
     */
    public SolarTermPoint findPreviousMonthBoundary(
            LocalDateTime dateTime,
            ZoneId zoneId
    ) {
        ZonedDateTime target =
                dateTime.atZone(zoneId);

        return createOccurrences(
                dateTime.getYear(),
                zoneId
        ).stream()
                .filter(point ->
                        !point.dateTime().isAfter(target)
                )
                .max(
                        Comparator.comparing(
                                SolarTermPoint::dateTime
                        )
                )
                .orElseThrow(() ->
                        new IllegalStateException(
                                "이전 절입을 찾을 수 없습니다."
                        )
                );
    }

    /**
     * 출생 시점 이후에 가장 가까운 절입을 반환한다.
     */
    public SolarTermPoint findNextMonthBoundary(
            LocalDateTime dateTime,
            ZoneId zoneId
    ) {
        ZonedDateTime target =
                dateTime.atZone(zoneId);

        return createOccurrences(
                dateTime.getYear(),
                zoneId
        ).stream()
                .filter(point ->
                        point.dateTime().isAfter(target)
                )
                .min(
                        Comparator.comparing(
                                SolarTermPoint::dateTime
                        )
                )
                .orElseThrow(() ->
                        new IllegalStateException(
                                "다음 절입을 찾을 수 없습니다."
                        )
                );
    }

    private List<SolarTermPoint> createOccurrences(
            int calendarYear,
            ZoneId zoneId
    ) {
        List<SolarTermPoint> occurrences =
                new ArrayList<>();

        for (int year = calendarYear - 1;
             year <= calendarYear + 1;
             year++) {

            for (SolarTermDefinition definition
                    : MONTH_BOUNDARY_TERMS) {

                ZonedDateTime dateTime =
                        calculateSolarTerm(
                                year,
                                definition,
                                zoneId
                        );

                occurrences.add(
                        new SolarTermPoint(
                                definition.name(),
                                definition.longitude(),
                                dateTime,
                                definition.monthIndex()
                        )
                );
            }
        }

        return occurrences;
    }

    private ZonedDateTime calculateSolarTerm(
            int year,
            SolarTermDefinition definition,
            ZoneId zoneId
    ) {
        validateYear(year);

        LocalDate seedDate =
                LocalDate.of(
                        year,
                        definition.seedMonth(),
                        definition.seedDay()
                );

        Instant lower =
                seedDate
                        .minusDays(3)
                        .atStartOfDay(ZoneOffset.UTC)
                        .toInstant();

        Instant upper =
                seedDate
                        .plusDays(3)
                        .atTime(LocalTime.MAX)
                        .atZone(ZoneOffset.UTC)
                        .toInstant();

        for (int iteration = 0;
             iteration < 80;
             iteration++) {

            Instant middle =
                    midpoint(lower, upper);

            double currentLongitude =
                    calculateApparentSolarLongitude(
                            middle
                    );

            double difference =
                    signedAngleDifference(
                            currentLongitude,
                            definition.longitude()
                    );

            if (difference < 0.0) {
                lower = middle;
            } else {
                upper = middle;
            }
        }

        return upper.atZone(zoneId);
    }

    private Instant midpoint(
            Instant lower,
            Instant upper
    ) {
        long middleSecond =
                lower.getEpochSecond()
                        + (
                        upper.getEpochSecond()
                                - lower.getEpochSecond()
                ) / 2;

        return Instant.ofEpochSecond(
                middleSecond
        );
    }

    private double calculateApparentSolarLongitude(
            Instant instant
    ) {
        double julianDay =
                toJulianDay(instant);

        double centuries =
                (julianDay - 2451545.0)
                        / 36525.0;

        double meanLongitude =
                normalizeDegrees(
                        280.46646
                                + 36000.76983
                                * centuries
                                + 0.0003032
                                * centuries
                                * centuries
                );

        double meanAnomaly =
                normalizeDegrees(
                        357.52911
                                + 35999.05029
                                * centuries
                                - 0.0001537
                                * centuries
                                * centuries
                );

        double anomalyRadians =
                Math.toRadians(meanAnomaly);

        double equationOfCenter =
                Math.sin(anomalyRadians)
                        * (
                        1.914602
                                - 0.004817 * centuries
                                - 0.000014
                                * centuries
                                * centuries
                )
                        + Math.sin(
                        2.0 * anomalyRadians
                ) * (
                        0.019993
                                - 0.000101
                                * centuries
                )
                        + Math.sin(
                        3.0 * anomalyRadians
                ) * 0.000289;

        double trueLongitude =
                meanLongitude
                        + equationOfCenter;

        double omega =
                125.04
                        - 1934.136
                        * centuries;

        return normalizeDegrees(
                trueLongitude
                        - 0.00569
                        - 0.00478
                        * Math.sin(
                        Math.toRadians(omega)
                )
        );
    }

    private double toJulianDay(
            Instant instant
    ) {
        return instant.toEpochMilli()
                / 86_400_000.0
                + 2440587.5;
    }

    private double normalizeDegrees(
            double degrees
    ) {
        double normalized =
                degrees % 360.0;

        if (normalized < 0.0) {
            normalized += 360.0;
        }

        return normalized;
    }

    private double signedAngleDifference(
            double current,
            double target
    ) {
        double difference =
                normalizeDegrees(
                        current - target
                );

        if (difference > 180.0) {
            difference -= 360.0;
        }

        return difference;
    }

    private SolarTermDefinition getDefinitionByName(
            String name
    ) {
        return MONTH_BOUNDARY_TERMS.stream()
                .filter(definition ->
                        definition.name().equals(name)
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "등록되지 않은 절기입니다: "
                                        + name
                        )
                );
    }

    private void validateYear(int year) {
        if (year
                < SajuCalculationPolicy.MIN_SUPPORTED_YEAR
                || year
                > SajuCalculationPolicy.MAX_SUPPORTED_YEAR) {

            throw new IllegalArgumentException(
                    "지원하는 연도는 "
                            + SajuCalculationPolicy
                            .MIN_SUPPORTED_YEAR
                            + "년부터 "
                            + SajuCalculationPolicy
                            .MAX_SUPPORTED_YEAR
                            + "년까지입니다."
            );
        }
    }

    private record SolarTermDefinition(
            String name,
            double longitude,
            Month seedMonth,
            int seedDay,
            int monthIndex
    ) {
    }
}