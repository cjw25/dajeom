package com.dajeom.calculator;

import com.dajeom.config.SajuCalculationPolicy;
import com.dajeom.model.SolarTermPoint;
import net.e175.klaus.solarpositioning.DeltaT;
import net.e175.klaus.solarpositioning.SPA;
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
 * 사주 명리의 년주/월주 경계에 사용하는
 * 12절의 실제 절입 시각을 계산한다.
 *
 * 핵심 정책
 *
 * 1. 년주 경계
 *    - 실제 입춘 시각
 *
 * 2. 월주 경계
 *    - 입춘
 *    - 경칩
 *    - 청명
 *    - 입하
 *    - 망종
 *    - 소서
 *    - 입추
 *    - 백로
 *    - 한로
 *    - 입동
 *    - 대설
 *    - 소한
 *
 * 3. 날짜만 보고 월주를 변경하지 않는다.
 *
 * 4. 출생시간이 없고 출생일이 절입 당일이면
 *    임의의 출생시간을 넣어 월주를 확정하지 않는다.
 *
 * 5. 천문 계산은 UTC Instant 기준으로 수행하고
 *    최종 결과만 요청받은 ZoneId로 변환한다.
 *
 * 6. 태양 위치는 NREL SPA 계열 계산을 사용한다.
 */
@Component
public class SolarTermCalculator {

    /**
     * 절입 시각 이진 탐색 정밀도.
     *
     * 50,000,000ns = 0.05초.
     *
     * 실제 서비스에서는 절입 초 단위까지
     * 충분히 안정적으로 수렴시키기 위한 값이다.
     */
    private static final long SEARCH_PRECISION_NANOS =
            50_000_000L;

    /**
     * 절기 예상일 전후 최초 탐색 범위.
     */
    private static final int INITIAL_SEARCH_DAYS =
            4;

    /**
     * 예상일에서 절기를 찾지 못했을 경우
     * 최대 전후 10일까지 탐색한다.
     */
    private static final int MAX_SEARCH_DAYS =
            10;

    /**
     * 사용자에게 허용하는 출생/계산 연도는
     * SajuCalculationPolicy의 기존 범위를 그대로 사용한다.
     *
     * 다만 1900년 1월의 이전 절입이나
     * 2050년 12월의 다음 절입을 찾으려면
     * 내부 천문 계산만 앞뒤 1년까지 필요하다.
     */
    private static final int INTERNAL_MIN_YEAR =
            SajuCalculationPolicy.MIN_SUPPORTED_YEAR - 1;

    private static final int INTERNAL_MAX_YEAR =
            SajuCalculationPolicy.MAX_SUPPORTED_YEAR + 1;

    /**
     * 명리에서 월주가 변경되는 12개의 절입.
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
     *
     * longitude는 태양 겉보기 지심 황경.
     *
     * 입춘 315°
     * 경칩 345°
     * 청명  15°
     * ...
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
     * 지정 연도의 실제 입춘 시각.
     */
    public ZonedDateTime calculateIpchun(
            int year,
            ZoneId zoneId
    ) {

        validateYear(
                year
        );

        validateZoneId(
                zoneId
        );

        return calculateSolarTerm(
                year,
                getDefinitionByName(
                        "입춘"
                ),
                zoneId
        );
    }

    /**
     * 특정 명리 월 경계의 실제 절입 시각.
     *
     * monthIndex:
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

        validateYear(
                year
        );

        validateZoneId(
                zoneId
        );

        return calculateSolarTerm(
                year,
                getDefinitionByMonthIndex(
                        monthIndex
                ),
                zoneId
        );
    }

    /**
     * 지정 달력 연도에 발생한
     * 12개 월주 절입을 실제 시간순으로 반환한다.
     *
     * 주의:
     *
     * monthIndex 순서는
     * 입춘 -> ... -> 소한이지만,
     *
     * 달력상의 실제 발생 순서는
     *
     * 소한(1월)
     * 입춘(2월)
     * 경칩(3월)
     * ...
     * 대설(12월)
     *
     * 이므로 반드시 dateTime으로 다시 정렬한다.
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
                new ArrayList<>(
                        12
                );

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
     * 출생 시각이 존재할 때 사주 년도를 결정한다.
     *
     * 예:
     *
     * 2026년 입춘 직전
     * -> 사주년도 2025
     *
     * 2026년 입춘 시각부터
     * -> 사주년도 2026
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
     * 출생시간이 없을 때 사주 년도 판정.
     *
     * 입춘 날짜와 출생 날짜가 다르면
     * 시간이 없어도 년주 확정 가능.
     *
     * 출생일 자체가 입춘일이면
     * 실제 입춘 시각 전/후에 따라 년주가 갈리므로
     * OptionalInt.empty()를 반환한다.
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

        LocalDate ipchunDate =
                calculateIpchun(
                        calendarYear,
                        zoneId
                ).toLocalDate();

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
     * 출생시간이 존재할 때
     * 실제 직전 절입을 기준으로 명리 월 인덱스를 결정한다.
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
     * 출생시간이 없을 때 월주 판정.
     *
     * 출생일에 절입이 존재하면
     * 실제 출생시간이 필요하므로 확정하지 않는다.
     *
     * 출생일이 절입일이 아니라면
     * 해당 날짜 전체는 동일한 명리 월에 속한다.
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

        if (
                findMonthBoundaryOnDate(
                        birthDate,
                        zoneId
                ).isPresent()
        ) {

            return OptionalInt.empty();
        }

        return OptionalInt.of(
                determineMonthIndex(
                        birthDate.atStartOfDay(),
                        zoneId
                )
        );
    }

    /**
     * 해당 날짜가 명리 월 절입일인지 확인한다.
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
     * 해당 날짜에 발생하는 명리 월 절입을 찾는다.
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
     * 존재하는 가장 가까운 절입을 반환한다.
     *
     * 절입 시각과 정확히 같은 경우에는
     * 이미 새로운 명리 월이 시작된 것으로 처리한다.
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
                                "이전 절입을 찾을 수 없습니다."
                        )
                );
    }

    /**
     * 지정 시점보다 뒤에 존재하는
     * 가장 가까운 절입을 반환한다.
     *
     * 지정 시각이 정확히 절입 시각이라면
     * 현재 절입이 아니라 다음 절입을 반환한다.
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
                                "다음 절입을 찾을 수 없습니다."
                        )
                );
    }

    /**
     * 특정 달력 연도를 중심으로
     * 전년도 / 해당년도 / 다음년도의 절입을 생성한다.
     *
     * 이유:
     *
     * 예를 들어 2026-01-01의 직전 절입은
     * 2025-12월 대설이므로 전년도 데이터가 필요하다.
     *
     * 2050-12-31의 다음 절입은
     * 2051년 소한일 수 있으므로
     * 공개 지원범위 끝에서도 내부적으로 +1년 계산이 필요하다.
     */
    private List<SolarTermPoint> createOccurrences(
            int calendarYear,
            ZoneId zoneId
    ) {

        List<SolarTermPoint> result =
                new ArrayList<>(
                        36
                );

        int startYear =
                Math.max(
                        INTERNAL_MIN_YEAR,
                        calendarYear - 1
                );

        int endYear =
                Math.min(
                        INTERNAL_MAX_YEAR,
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

                result.add(
                        createPointInternal(
                                year,
                                definition,
                                zoneId
                        )
                );
            }
        }

        result.sort(
                Comparator.comparing(
                        SolarTermPoint::dateTime
                )
        );

        return result;
    }

    private SolarTermPoint createPoint(
            int year,
            SolarTermDefinition definition,
            ZoneId zoneId
    ) {

        validateYear(
                year
        );

        return createPointInternal(
                year,
                definition,
                zoneId
        );
    }

    private SolarTermPoint createPointInternal(
            int year,
            SolarTermDefinition definition,
            ZoneId zoneId
    ) {

        ZonedDateTime dateTime =
                calculateSolarTermInternal(
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
     * 공개 지원연도에 대한 절기 계산.
     */
    private ZonedDateTime calculateSolarTerm(
            int year,
            SolarTermDefinition definition,
            ZoneId zoneId
    ) {

        validateYear(
                year
        );

        return calculateSolarTermInternal(
                year,
                definition,
                zoneId
        );
    }

    /**
     * 실제 태양 황경이 목표 황경을 통과하는 시각을 찾는다.
     *
     * 계산 과정:
     *
     * 예상 절기 날짜 주변 탐색
     * -> 목표 황경 전/후 구간 확인
     * -> 이진 탐색
     * -> 약 0.05초 구간까지 수렴
     */
    private ZonedDateTime calculateSolarTermInternal(
            int year,
            SolarTermDefinition definition,
            ZoneId zoneId
    ) {

        validateInternalYear(
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
                    "절기 계산 범위 안에서 목표 황경을 찾지 못했습니다: "
                            + year
                            + "년 "
                            + definition.name()
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

            double middleDifference =
                    signedAngleDifference(
                            calculateApparentSolarLongitude(
                                    middle
                            ),
                            targetLongitude
                    );

            /*
             * 태양 황경은 절기 주변에서 계속 증가한다.
             *
             * 목표 황경 이전:
             * difference < 0
             *
             * 목표 황경 이후:
             * difference >= 0
             */
            if (
                    middleDifference < 0.0
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
     * 각 절기의 대략적인 발생일을 기준으로
     * 목표 황경이 실제로 포함되는 탐색 범위를 생성한다.
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

        for (
                int searchDays = INITIAL_SEARCH_DAYS;
                searchDays <= MAX_SEARCH_DAYS;
                searchDays++
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
        }

        throw new IllegalStateException(
                "절기 탐색 범위를 생성하지 못했습니다: "
                        + year
                        + "년 "
                        + definition.name()
        );
    }

    /**
     * 목표 황경이 lower ~ upper 사이에 존재하는지 확인한다.
     *
     * 목표 이전:
     * 음수
     *
     * 목표 이후:
     * 양수
     */
    private boolean containsZeroCrossing(
            double lowerDifference,
            double upperDifference
    ) {

        return lowerDifference <= 0.0
                && upperDifference >= 0.0;
    }

    /**
     * 두 Instant의 정확한 중간값.
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
     * 태양의 apparent geocentric ecliptic longitude
     * 즉 겉보기 지심 황경을 계산한다.
     *
     * 절입 계산에서 필요한 값은
     * 특정 지역에서 바라본 태양 고도/방위각이 아니라
     * 태양의 지심 황경이다.
     *
     * solarpositioning의 SPA가 계산한:
     *
     * - apparent geocentric right ascension
     * - apparent geocentric declination
     *
     * 을 같은 시점의 true obliquity로
     * 다시 황도좌표계로 역변환한다.
     *
     * 반환값:
     *
     * 0 <= longitude < 360
     */
    private double calculateApparentSolarLongitude(
            Instant instant
    ) {

        ZonedDateTime utc =
                instant.atZone(
                        ZoneOffset.UTC
                );

        double deltaTSeconds =
                DeltaT.estimate(
                        utc.toLocalDate()
                );

        SPA.SpaTimeDependent timeDependent =
                SPA.calculateSpaTimeDependentParts(
                        utc,
                        deltaTSeconds
                );

        /*
         * SPA가 계산한 지심 적경.
         */
        double alpha =
                Math.toRadians(
                        timeDependent.alphaDegrees()
                );

        /*
         * SPA가 계산한 지심 적위.
         */
        double delta =
                Math.toRadians(
                        timeDependent.deltaDegrees()
                );

        /*
         * 같은 시점의 실제 황도경사.
         */
        double epsilon =
                Math.toRadians(
                        calculateTrueObliquityDegrees(
                                instant
                        )
                );

        /*
         * 적도좌표 -> 황도좌표 역변환.
         *
         * λ = atan2(
         *      sin α cos ε + tan δ sin ε,
         *      cos α
         * )
         */
        double longitude =
                Math.atan2(
                        Math.sin(
                                alpha
                        )
                                * Math.cos(
                                epsilon
                        )
                                + Math.tan(
                                delta
                        )
                                * Math.sin(
                                epsilon
                        ),
                        Math.cos(
                                alpha
                        )
                );

        return normalizeDegrees(
                Math.toDegrees(
                        longitude
                )
        );
    }

    /**
     * 해당 시점의 true obliquity of the ecliptic,
     * 즉 실제 황도경사를 계산한다.
     *
     * 공개 서비스 범위 1900~2050에서
     * 충분히 안정적인 평균 황도경사 계산과
     * 주요 nutation in obliquity 항을 사용한다.
     */
    private double calculateTrueObliquityDegrees(
            Instant instant
    ) {

        /*
         * J2000.0부터의 Julian Century.
         */
        double t =
                (
                        toJulianDay(
                                instant
                        )
                                - 2451545.0
                )
                        / 36525.0;

        /*
         * 평균 황도경사.
         *
         * 단위:
         * arcsecond
         */
        double meanObliquityArcSeconds =
                84381.448
                        - 46.8150
                        * t
                        - 0.00059
                        * t
                        * t
                        + 0.001813
                        * t
                        * t
                        * t;

        /*
         * 달의 승교점 평균황경.
         */
        double omega =
                Math.toRadians(
                        normalizeDegrees(
                                125.04452
                                        - 1934.136261
                                        * t
                                        + 0.0020708
                                        * t
                                        * t
                                        + t
                                        * t
                                        * t
                                        / 450000.0
                        )
                );

        /*
         * 태양 평균황경.
         */
        double meanSolarLongitude =
                Math.toRadians(
                        normalizeDegrees(
                                280.4665
                                        + 36000.7698
                                        * t
                        )
                );

        /*
         * 달 평균황경.
         */
        double meanLunarLongitude =
                Math.toRadians(
                        normalizeDegrees(
                                218.3165
                                        + 481267.8813
                                        * t
                        )
                );

        /*
         * 주요 nutation in obliquity 항.
         *
         * 단위:
         * arcsecond
         */
        double nutationObliquityArcSeconds =
                9.20
                        * Math.cos(
                        omega
                )
                        + 0.57
                        * Math.cos(
                        2.0
                                * meanSolarLongitude
                )
                        + 0.10
                        * Math.cos(
                        2.0
                                * meanLunarLongitude
                )
                        - 0.09
                        * Math.cos(
                        2.0
                                * omega
                );

        return (
                meanObliquityArcSeconds
                        + nutationObliquityArcSeconds
        )
                / 3600.0;
    }

    /**
     * UTC Instant를 Julian Day로 변환한다.
     */
    private double toJulianDay(
            Instant instant
    ) {

        return instant.getEpochSecond()
                / 86400.0
                + instant.getNano()
                / 86_400_000_000_000.0
                + 2440587.5;
    }

    /**
     * 각도를 0 이상 360 미만으로 정규화한다.
     */
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
     * 현재 황경과 목표 황경 차이를
     * -180도 ~ +180도 범위로 만든다.
     *
     * 예:
     *
     * 목표 = 345°
     * 현재 = 344°
     * -> -1°
     *
     * 목표 = 345°
     * 현재 = 346°
     * -> +1°
     *
     * 0° / 360° 경계에서도 연속적으로 비교할 수 있다.
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

    /**
     * 외부 공개 계산 연도 검증.
     */
    private void validateYear(
            int year
    ) {

        SajuCalculationPolicy.validateYear(
                year
        );
    }

    /**
     * 절입 검색에만 사용하는 내부 연도 검증.
     */
    private void validateInternalYear(
            int year
    ) {

        if (
                year < INTERNAL_MIN_YEAR
                        || year > INTERNAL_MAX_YEAR
        ) {

            throw new IllegalArgumentException(
                    "내부 절기 계산 가능 연도는 "
                            + INTERNAL_MIN_YEAR
                            + "년부터 "
                            + INTERNAL_MAX_YEAR
                            + "년까지입니다: "
                            + year
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

    /**
     * 이진 탐색 범위.
     */
    private record SearchRange(

            Instant lower,

            Instant upper

    ) {
    }

    /**
     * 월주 경계를 만드는 절기 정의.
     */
    private record SolarTermDefinition(

            String name,

            double longitude,

            Month seedMonth,

            int seedDay,

            int monthIndex

    ) {
    }
}