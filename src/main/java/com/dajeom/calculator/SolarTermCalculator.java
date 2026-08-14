package com.dajeom.calculator;

import com.dajeom.config.SajuCalculationPolicy;
import com.dajeom.model.SolarTermPoint;
import org.springframework.stereotype.Component;

import java.time.Duration;
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
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * 사주 명리에서 사용하는 절입 시각을 계산한다.
 *
 * 핵심 정책
 *
 * 1. 년주 변경 기준은 실제 입춘 시각이다.
 * 2. 월주 변경 기준은 12개의 절입 시각이다.
 * 3. 단순 날짜 기준으로 월주를 변경하지 않는다.
 * 4. 출생시간이 없는 경우 절입 당일에는 임의의 시간을 넣지 않는다.
 * 5. 모든 계산 기준 시각은 Instant로 계산한 뒤
 *    요청 ZoneId로 변환한다.
 *
 * 다점 정책상 기본 사용 시간대는 Asia/Seoul이지만,
 * 이 Calculator 자체는 ZoneId를 명시적으로 받는다.
 */
@Component
public class SolarTermCalculator {

    /**
     * 이진 탐색 종료 정밀도.
     *
     * 천문 모델 자체의 오차와 별개로,
     * 수치 탐색 과정에서 초 단위로 값을 잘라내지 않기 위해
     * 50ms 이하까지 탐색한다.
     */
    private static final long SEARCH_PRECISION_NANOS =
            50_000_000L;

    /**
     * 절기 예상일 전후 탐색 범위.
     *
     * 일반적인 절기 시각은 seed 기준 ±수 일 범위 안에 있으므로
     * 우선 4일 범위를 잡고 실제 목표 황경이 그 범위에 존재하는지
     * 검증한다.
     */
    private static final int INITIAL_SEARCH_DAYS =
            4;

    /**
     * 최대 탐색 범위.
     *
     * 예상치 못한 천문 계산 오차가 있더라도
     * 무한 확장을 막는다.
     */
    private static final int MAX_SEARCH_DAYS =
            10;

    /**
     * 명리에서 월주가 바뀌는 12개의 절입.
     *
     * monthIndex
     *
     * 0  입춘 -> 寅월
     * 1  경칩 -> 卯월
     * 2  청명 -> 辰월
     * 3  입하 -> 巳월
     * 4  망종 -> 午월
     * 5  소서 -> 未월
     * 6  입추 -> 申월
     * 7  백로 -> 酉월
     * 8  한로 -> 戌월
     * 9  입동 -> 亥월
     * 10 대설 -> 子월
     * 11 소한 -> 丑월
     */
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

    /**
     * 지정 연도의 실제 입춘 시각을 계산한다.
     */
    public ZonedDateTime calculateIpchun(
            int year,
            ZoneId zoneId
    ) {
        validateZoneId(
                zoneId
        );

        SolarTermDefinition definition =
                getDefinitionByName(
                        "입춘"
                );

        return calculateSolarTerm(
                year,
                definition,
                zoneId
        );
    }

    /**
     * 특정 monthIndex의 절입 시각을 반환한다.
     *
     * 0 = 입춘
     * 1 = 경칩
     * ...
     * 11 = 소한
     */
    public ZonedDateTime calculateMonthBoundary(
            int year,
            int monthIndex,
            ZoneId zoneId
    ) {
        validateZoneId(
                zoneId
        );

        SolarTermDefinition definition =
                getDefinitionByMonthIndex(
                        monthIndex
                );

        return calculateSolarTerm(
                year,
                definition,
                zoneId
        );
    }

    /**
     * 지정 연도의 12개 월주 절입을 반환한다.
     *
     * 반환 목록은 실제 시간 순으로 정렬한다.
     *
     * 주의:
     * 소한은 1월이고 입춘은 2월이므로
     * monthIndex 순서와 실제 달력 시간 순서는 다르다.
     */
    public List<SolarTermPoint> getMonthBoundaries(
            int year,
            ZoneId zoneId
    ) {
        validateYear(
                year
        );

        validateZoneId(
                zoneId
        );

        List<SolarTermPoint> result =
                new ArrayList<>();

        for (
                SolarTermDefinition definition
                : MONTH_BOUNDARY_TERMS
        ) {
            result.add(
                    createPoint(
                            year,
                            definition,
                            zoneId
                    )
            );
        }

        result.sort(
                Comparator.comparing(
                        SolarTermPoint::dateTime
                )
        );

        return List.copyOf(
                result
        );
    }

    /**
     * 출생 시각이 주어진 경우 사주 년도를 결정한다.
     *
     * 입춘 직전까지는 전년도,
     * 입춘 시각부터는 해당 연도이다.
     */
    public int determineSajuYear(
            LocalDateTime birthDateTime,
            ZoneId zoneId
    ) {
        validateDateTime(
                birthDateTime
        );

        validateZoneId(
                zoneId
        );

        int calendarYear =
                birthDateTime.getYear();

        ZonedDateTime birth =
                birthDateTime.atZone(
                        zoneId
                );

        ZonedDateTime ipchun =
                calculateIpchun(
                        calendarYear,
                        zoneId
                );

        return birth.isBefore(
                ipchun
        )
                ? calendarYear - 1
                : calendarYear;
    }

    /**
     * 출생시간을 모르는 경우의 년주 판단.
     *
     * 출생일이 입춘일과 다르면 년주를 확정할 수 있다.
     *
     * 출생일이 입춘 당일이면 실제 출생시간에 따라
     * 년주가 달라질 수 있으므로 OptionalInt.empty()를 반환한다.
     *
     * 임의로 00:00 / 12:00를 넣지 않는다.
     */
    public OptionalInt determineSajuYear(
            LocalDate birthDate,
            ZoneId zoneId
    ) {
        validateDate(
                birthDate
        );

        validateZoneId(
                zoneId
        );

        int calendarYear =
                birthDate.getYear();

        ZonedDateTime ipchun =
                calculateIpchun(
                        calendarYear,
                        zoneId
                );

        LocalDate ipchunDate =
                ipchun.toLocalDate();

        if (
                birthDate.equals(
                        ipchunDate
                )
        ) {
            return OptionalInt.empty();
        }

        if (
                birthDate.isBefore(
                        ipchunDate
                )
        ) {
            return OptionalInt.of(
                    calendarYear - 1
            );
        }

        return OptionalInt.of(
                calendarYear
        );
    }

    /**
     * 출생 시각이 있을 때 사주 월 인덱스를 결정한다.
     */
    public int determineMonthIndex(
            LocalDateTime birthDateTime,
            ZoneId zoneId
    ) {
        validateDateTime(
                birthDateTime
        );

        validateZoneId(
                zoneId
        );

        return findPreviousMonthBoundary(
                birthDateTime,
                zoneId
        ).monthIndex();
    }

    /**
     * 출생시간을 모르는 경우의 월주 판단.
     *
     * 출생일이 절입일이면 월주를 확정하지 않는다.
     *
     * 절입일이 아니면 그 날짜 전체가 같은 월주에 속하므로
     * 안전하게 월 인덱스를 결정할 수 있다.
     */
    public OptionalInt determineMonthIndex(
            LocalDate birthDate,
            ZoneId zoneId
    ) {
        validateDate(
                birthDate
        );

        validateZoneId(
                zoneId
        );

        Optional<SolarTermPoint> boundary =
                findMonthBoundaryOnDate(
                        birthDate,
                        zoneId
                );

        if (
                boundary.isPresent()
        ) {
            return OptionalInt.empty();
        }

        LocalDateTime startOfDate =
                birthDate.atStartOfDay();

        return OptionalInt.of(
                determineMonthIndex(
                        startOfDate,
                        zoneId
                )
        );
    }

    /**
     * 해당 날짜에 월주 절입이 존재하는지 확인한다.
     */
    public boolean isMonthBoundaryDate(
            LocalDate date,
            ZoneId zoneId
    ) {
        return findMonthBoundaryOnDate(
                date,
                zoneId
        ).isPresent();
    }

    /**
     * 해당 날짜에 존재하는 월주 절입을 반환한다.
     *
     * 출생시간 미상 처리에서 사용한다.
     */
    public Optional<SolarTermPoint> findMonthBoundaryOnDate(
            LocalDate date,
            ZoneId zoneId
    ) {
        validateDate(
                date
        );

        validateZoneId(
                zoneId
        );

        return createOccurrences(
                date.getYear(),
                zoneId
        ).stream()
                .filter(point ->
                        point.dateTime()
                                .toLocalDate()
                                .equals(
                                        date
                                )
                )
                .min(
                        Comparator.comparing(
                                SolarTermPoint::dateTime
                        )
                );
    }

    /**
     * 지정 시점 이전 또는 정확히 같은 시각에
     * 가장 가깝게 지난 절입을 반환한다.
     *
     * 절입 시각과 정확히 같으면
     * 새 월주가 이미 시작된 것으로 본다.
     */
    public SolarTermPoint findPreviousMonthBoundary(
            LocalDateTime dateTime,
            ZoneId zoneId
    ) {
        validateDateTime(
                dateTime
        );

        validateZoneId(
                zoneId
        );

        ZonedDateTime target =
                dateTime.atZone(
                        zoneId
                );

        return createOccurrences(
                dateTime.getYear(),
                zoneId
        ).stream()
                .filter(point ->
                        !point.dateTime()
                                .isAfter(
                                        target
                                )
                )
                .max(
                        Comparator.comparing(
                                SolarTermPoint::dateTime
                        )
                )
                .orElseThrow(() ->
                        new IllegalStateException(
                                "이전 절입을 찾을 수 없습니다. "
                                        + "지원 연도 범위를 확인해주세요."
                        )
                );
    }

    /**
     * 지정 시점 이후의 가장 가까운 절입을 반환한다.
     *
     * 정확히 절입 시각인 경우
     * 현재 절입이 아니라 다음 절입을 찾는다.
     */
    public SolarTermPoint findNextMonthBoundary(
            LocalDateTime dateTime,
            ZoneId zoneId
    ) {
        validateDateTime(
                dateTime
        );

        validateZoneId(
                zoneId
        );

        ZonedDateTime target =
                dateTime.atZone(
                        zoneId
                );

        return createOccurrences(
                dateTime.getYear(),
                zoneId
        ).stream()
                .filter(point ->
                        point.dateTime()
                                .isAfter(
                                        target
                                )
                )
                .min(
                        Comparator.comparing(
                                SolarTermPoint::dateTime
                        )
                )
                .orElseThrow(() ->
                        new IllegalStateException(
                                "다음 절입을 찾을 수 없습니다. "
                                        + "지원 연도 범위를 확인해주세요."
                        )
                );
    }

    /**
     * 해당 연도를 중심으로 전후 절입 발생 시점을 만든다.
     *
     * 지원 범위를 넘어가는 연도는 계산하지 않는다.
     */
    private List<SolarTermPoint> createOccurrences(
            int calendarYear,
            ZoneId zoneId
    ) {
        List<SolarTermPoint> occurrences =
                new ArrayList<>();

        int startYear =
                Math.max(
                        SajuCalculationPolicy.MIN_SUPPORTED_YEAR,
                        calendarYear - 1
                );

        int endYear =
                Math.min(
                        SajuCalculationPolicy.MAX_SUPPORTED_YEAR,
                        calendarYear + 1
                );

        for (
                int year = startYear;
                year <= endYear;
                year++
        ) {
            for (
                    SolarTermDefinition definition
                    : MONTH_BOUNDARY_TERMS
            ) {
                occurrences.add(
                        createPoint(
                                year,
                                definition,
                                zoneId
                        )
                );
            }
        }

        occurrences.sort(
                Comparator.comparing(
                        SolarTermPoint::dateTime
                )
        );

        return occurrences;
    }

    private SolarTermPoint createPoint(
            int year,
            SolarTermDefinition definition,
            ZoneId zoneId
    ) {
        ZonedDateTime dateTime =
                calculateSolarTerm(
                        year,
                        definition,
                        zoneId
                );

        return new SolarTermPoint(
                definition.name(),
                definition.longitude(),
                dateTime,
                definition.monthIndex()
        );
    }

    /**
     * 특정 황경에 태양이 도달하는 시각을 계산한다.
     */
    private ZonedDateTime calculateSolarTerm(
            int year,
            SolarTermDefinition definition,
            ZoneId zoneId
    ) {
        validateYear(
                year
        );

        validateZoneId(
                zoneId
        );

        SearchRange range =
                createSearchRange(
                        year,
                        definition
                );

        Instant lower =
                range.lower();

        Instant upper =
                range.upper();

        double targetLongitude =
                definition.longitude();

        double lowerDifference =
                signedAngleDifference(
                        calculateApparentSolarLongitude(
                                lower
                        ),
                        targetLongitude
                );

        double upperDifference =
                signedAngleDifference(
                        calculateApparentSolarLongitude(
                                upper
                        ),
                        targetLongitude
                );

        if (
                !containsZeroCrossing(
                        lowerDifference,
                        upperDifference
                )
        ) {
            throw new IllegalStateException(
                    "절기 계산 범위 안에서 목표 황경을 찾지 못했습니다. "
                            + definition.name()
                            + " / "
                            + year
                            + "년"
            );
        }

        while (
                Duration.between(
                        lower,
                        upper
                ).toNanos()
                        > SEARCH_PRECISION_NANOS
        ) {
            Instant middle =
                    midpoint(
                            lower,
                            upper
                    );

            double currentLongitude =
                    calculateApparentSolarLongitude(
                            middle
                    );

            double difference =
                    signedAngleDifference(
                            currentLongitude,
                            targetLongitude
                    );

            if (
                    difference < 0.0
            ) {
                lower =
                        middle;
            } else {
                upper =
                        middle;
            }
        }

        Instant result =
                midpoint(
                        lower,
                        upper
                );

        return result.atZone(
                zoneId
        );
    }

    /**
     * 절기 예상일을 중심으로
     * 실제 목표 황경이 포함되는 탐색 범위를 만든다.
     */
    private SearchRange createSearchRange(
            int year,
            SolarTermDefinition definition
    ) {
        LocalDate seedDate =
                LocalDate.of(
                        year,
                        definition.seedMonth(),
                        definition.seedDay()
                );

        int searchDays =
                INITIAL_SEARCH_DAYS;

        while (
                searchDays
                        <= MAX_SEARCH_DAYS
        ) {
            Instant lower =
                    seedDate
                            .minusDays(
                                    searchDays
                            )
                            .atStartOfDay(
                                    ZoneOffset.UTC
                            )
                            .toInstant();

            Instant upper =
                    seedDate
                            .plusDays(
                                    searchDays
                            )
                            .atTime(
                                    LocalTime.MAX
                            )
                            .atZone(
                                    ZoneOffset.UTC
                            )
                            .toInstant();

            double lowerDifference =
                    signedAngleDifference(
                            calculateApparentSolarLongitude(
                                    lower
                            ),
                            definition.longitude()
                    );

            double upperDifference =
                    signedAngleDifference(
                            calculateApparentSolarLongitude(
                                    upper
                            ),
                            definition.longitude()
                    );

            if (
                    containsZeroCrossing(
                            lowerDifference,
                            upperDifference
                    )
            ) {
                return new SearchRange(
                        lower,
                        upper
                );
            }

            searchDays++;
        }

        throw new IllegalStateException(
                "절기 탐색 범위를 생성하지 못했습니다. "
                        + definition.name()
                        + " / "
                        + year
                        + "년"
        );
    }

    private boolean containsZeroCrossing(
            double lowerDifference,
            double upperDifference
    ) {
        return lowerDifference <= 0.0
                && upperDifference >= 0.0;
    }

    /**
     * Instant 중간값.
     *
     * 기존 epochSecond 기반 계산처럼
     * 소수 초를 버리지 않는다.
     */
    private Instant midpoint(
            Instant lower,
            Instant upper
    ) {
        long nanos =
                Duration.between(
                        lower,
                        upper
                ).toNanos();

        return lower.plusNanos(
                nanos / 2L
        );
    }

    /**
     * 태양의 apparent geocentric ecliptic longitude.
     *
     * Julian Ephemeris Century를 사용하기 위해
     * UT에서 TT로 Delta-T를 보정한다.
     *
     * 결과:
     * 0° <= longitude < 360°
     */
    private double calculateApparentSolarLongitude(
            Instant instant
    ) {
        double julianDayUt =
                toJulianDay(
                        instant
                );

        double decimalYear =
                toDecimalYear(
                        instant
                );

        double deltaTSeconds =
                estimateDeltaTSeconds(
                        decimalYear
                );

        double julianEphemerisDay =
                julianDayUt
                        + deltaTSeconds
                        / 86_400.0;

        double centuries =
                (
                        julianEphemerisDay
                                - 2451545.0
                )
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
                                + centuries
                                * centuries
                                * centuries
                                / 24_490_000.0
                );

        double anomalyRadians =
                Math.toRadians(
                        meanAnomaly
                );

        double equationOfCenter =
                Math.sin(
                        anomalyRadians
                )
                        * (
                        1.914602
                                - 0.004817
                                * centuries
                                - 0.000014
                                * centuries
                                * centuries
                )
                        + Math.sin(
                        2.0
                                * anomalyRadians
                )
                        * (
                        0.019993
                                - 0.000101
                                * centuries
                )
                        + Math.sin(
                        3.0
                                * anomalyRadians
                )
                        * 0.000289;

        double trueLongitude =
                meanLongitude
                        + equationOfCenter;

        double omega =
                125.04
                        - 1934.136
                        * centuries;

        double apparentLongitude =
                trueLongitude
                        - 0.00569
                        - 0.00478
                        * Math.sin(
                        Math.toRadians(
                                omega
                        )
                );

        return normalizeDegrees(
                apparentLongitude
        );
    }

    /**
     * UTC Instant를 Julian Day로 변환한다.
     */
    private double toJulianDay(
            Instant instant
    ) {
        return instant.getEpochSecond()
                / 86_400.0
                + instant.getNano()
                / 86_400_000_000_000.0
                + 2440587.5;
    }

    /**
     * Delta-T 계산용 소수 연도.
     */
    private double toDecimalYear(
            Instant instant
    ) {
        ZonedDateTime utc =
                instant.atZone(
                        ZoneOffset.UTC
                );

        int year =
                utc.getYear();

        int daysInYear =
                utc.toLocalDate()
                        .lengthOfYear();

        double dayFraction =
                (
                        utc.getHour()
                                * 3600.0
                                + utc.getMinute()
                                * 60.0
                                + utc.getSecond()
                                + utc.getNano()
                                / 1_000_000_000.0
                )
                        / 86_400.0;

        return year
                + (
                utc.getDayOfYear()
                        - 1
                        + dayFraction
        )
                / daysInYear;
    }

    /**
     * TT - UT1 근사값인 Delta-T를 초 단위로 계산한다.
     *
     * 다점의 현대 출생연도 범위에서
     * 태양 황경 계산 시각의 체계적인 시간 오차를 줄이기 위한 보정이다.
     *
     * 장기간 천체력 자체를 대체하는 값은 아니며
     * 절기 판정 정밀화를 위한 시간척도 보정으로 사용한다.
     */
    private double estimateDeltaTSeconds(
            double year
    ) {
        if (
                year < 1900.0
        ) {
            double t =
                    year
                            - 1900.0;

            return -2.79
                    + 1.494119
                    * t
                    - 0.0598939
                    * t
                    * t
                    + 0.0061966
                    * t
                    * t
                    * t
                    - 0.000197
                    * t
                    * t
                    * t
                    * t;
        }

        if (
                year < 1920.0
        ) {
            double t =
                    year
                            - 1900.0;

            return -2.79
                    + 1.494119
                    * t
                    - 0.0598939
                    * t
                    * t
                    + 0.0061966
                    * t
                    * t
                    * t
                    - 0.000197
                    * t
                    * t
                    * t
                    * t;
        }

        if (
                year < 1941.0
        ) {
            double t =
                    year
                            - 1920.0;

            return 21.20
                    + 0.84493
                    * t
                    - 0.076100
                    * t
                    * t
                    + 0.0020936
                    * t
                    * t
                    * t;
        }

        if (
                year < 1961.0
        ) {
            double t =
                    year
                            - 1950.0;

            return 29.07
                    + 0.407
                    * t
                    - t
                    * t
                    / 233.0
                    + t
                    * t
                    * t
                    / 2547.0;
        }

        if (
                year < 1986.0
        ) {
            double t =
                    year
                            - 1975.0;

            return 45.45
                    + 1.067
                    * t
                    - t
                    * t
                    / 260.0
                    - t
                    * t
                    * t
                    / 718.0;
        }

        if (
                year < 2005.0
        ) {
            double t =
                    year
                            - 2000.0;

            return 63.86
                    + 0.3345
                    * t
                    - 0.060374
                    * t
                    * t
                    + 0.0017275
                    * t
                    * t
                    * t
                    + 0.000651814
                    * Math.pow(
                    t,
                    4
            )
                    + 0.00002373599
                    * Math.pow(
                    t,
                    5
            );
        }

        if (
                year < 2050.0
        ) {
            double t =
                    year
                            - 2000.0;

            return 62.92
                    + 0.32217
                    * t
                    + 0.005589
                    * t
                    * t;
        }

        if (
                year < 2150.0
        ) {
            double u =
                    (
                            year
                                    - 1820.0
                    )
                            / 100.0;

            return -20.0
                    + 32.0
                    * u
                    * u
                    - 0.5628
                    * (
                    2150.0
                            - year
            );
        }

        double u =
                (
                        year
                                - 1820.0
                )
                        / 100.0;

        return -20.0
                + 32.0
                * u
                * u;
    }

    private double normalizeDegrees(
            double degrees
    ) {
        double normalized =
                degrees
                        % 360.0;

        if (
                normalized < 0.0
        ) {
            normalized +=
                    360.0;
        }

        return normalized;
    }

    /**
     * 현재 황경과 목표 황경 사이의
     * -180° ~ +180° 범위 차이를 계산한다.
     *
     * 0°/360° 경계에서도
     * 이진 탐색 방향이 깨지지 않도록 한다.
     */
    private double signedAngleDifference(
            double current,
            double target
    ) {
        double difference =
                normalizeDegrees(
                        current
                                - target
                );

        if (
                difference > 180.0
        ) {
            difference -=
                    360.0;
        }

        return difference;
    }

    private SolarTermDefinition getDefinitionByName(
            String name
    ) {
        return MONTH_BOUNDARY_TERMS.stream()
                .filter(definition ->
                        definition.name()
                                .equals(
                                        name
                                )
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "등록되지 않은 절기입니다: "
                                        + name
                        )
                );
    }

    private SolarTermDefinition getDefinitionByMonthIndex(
            int monthIndex
    ) {
        return MONTH_BOUNDARY_TERMS.stream()
                .filter(definition ->
                        definition.monthIndex()
                                == monthIndex
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "월 절입 인덱스는 0부터 11까지입니다: "
                                        + monthIndex
                        )
                );
    }

    private void validateYear(
            int year
    ) {
        if (
                year
                        < SajuCalculationPolicy.MIN_SUPPORTED_YEAR
                        || year
                        > SajuCalculationPolicy.MAX_SUPPORTED_YEAR
        ) {
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

    private void validateDateTime(
            LocalDateTime dateTime
    ) {
        Objects.requireNonNull(
                dateTime,
                "계산할 날짜와 시간이 필요합니다."
        );

        validateYear(
                dateTime.getYear()
        );
    }

    private void validateDate(
            LocalDate date
    ) {
        Objects.requireNonNull(
                date,
                "계산할 날짜가 필요합니다."
        );

        validateYear(
                date.getYear()
        );
    }

    private void validateZoneId(
            ZoneId zoneId
    ) {
        Objects.requireNonNull(
                zoneId,
                "시간대가 필요합니다."
        );
    }

    private record SearchRange(
            Instant lower,
            Instant upper
    ) {
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