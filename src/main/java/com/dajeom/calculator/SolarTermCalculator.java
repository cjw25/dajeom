package com.dajeom.calculator;

import com.dajeom.constant.SolarTermTable;
import com.dajeom.exception.BirthTimeRequiredException;
import com.dajeom.model.NormalizedBirthData;
import com.dajeom.model.SolarTermDefinition;
import com.dajeom.model.SolarTermPoint;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 24절기의 실제 시각을 계산한다.
 *
 * 명리 계산에서는:
 *
 * 년주:
 * 입춘 시각을 기준으로 변경
 *
 * 월주:
 * 입춘, 경칩, 청명, 입하, 망종, 소서,
 * 입추, 백로, 한로, 입동, 대설, 소한의
 * 실제 절입 시각을 기준으로 변경
 *
 * 고정 날짜를 사용하지 않는다.
 */
@Component
public class SolarTermCalculator {

    /**
     * 절기 탐색 간격.
     *
     * 6시간 단위로 전체 구간을 탐색한 뒤
     * 실제 절기 지점은 이진 탐색으로 정밀화한다.
     */
    private static final Duration SCAN_STEP =
            Duration.ofHours(6);

    /**
     * 계산 결과 캐시.
     *
     * 같은 연도/시간대의 24절기를
     * 매 요청마다 다시 계산하지 않는다.
     */
    private final Map<
            YearZoneKey,
            List<SolarTermPoint>
            > yearlyCache =
            new ConcurrentHashMap<>();

    /**
     * 특정 연도의 24절기를 계산한다.
     */
    public List<SolarTermPoint> calculateAll(
            int year,
            ZoneId zoneId
    ) {

        Objects.requireNonNull(
                zoneId,
                "시간대는 필수입니다."
        );

        YearZoneKey key =
                new YearZoneKey(
                        year,
                        zoneId.getId()
                );

        return yearlyCache.computeIfAbsent(
                key,
                ignored ->
                        calculateYear(
                                year,
                                zoneId
                        )
        );
    }

    /**
     * 특정 절기의 실제 발생 시각을 계산한다.
     */
    public SolarTermPoint calculate(
            int year,
            SolarTermDefinition definition,
            ZoneId zoneId
    ) {

        Objects.requireNonNull(
                definition,
                "절기 정의는 필수입니다."
        );

        return calculateAll(
                year,
                zoneId
        )
                .stream()
                .filter(
                        point ->
                                point.name().equals(
                                        definition.koreanName()
                                )
                )
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "절기를 찾을 수 없습니다: "
                                                + definition.koreanName()
                                                + " / "
                                                + year
                                )
                );
    }

    /**
     * 특정 연도의 입춘 시각.
     */
    public SolarTermPoint calculateIpchun(
            int year,
            ZoneId zoneId
    ) {

        SolarTermDefinition ipchun =
                SolarTermTable
                        .getAll()
                        .stream()
                        .filter(
                                definition ->
                                        "입춘".equals(
                                                definition.koreanName()
                                        )
                        )
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "입춘 정의를 찾을 수 없습니다."
                                        )
                        );

        return calculate(
                year,
                ipchun,
                zoneId
        );
    }

    /**
     * 사주 년도를 결정한다.
     *
     * 양력 1월 1일이 새로운 사주년의 시작이 아니다.
     * 실제 입춘 시각이 새로운 사주년의 시작이다.
     */
    public int determineSajuYear(
            NormalizedBirthData birthData
    ) {

        Objects.requireNonNull(
                birthData,
                "출생정보는 필수입니다."
        );

        int calendarYear =
                birthData
                        .solarBirthDate()
                        .getYear();

        SolarTermPoint ipchun =
                calculateIpchun(
                        calendarYear,
                        birthData.zoneId()
                );

        /*
         * 출생시간을 알고 있는 경우
         * 실제 입춘 시각과 정확하게 비교한다.
         */
        if (birthData.hasKnownBirthTime()) {

            ZonedDateTime birthDateTime =
                    birthData
                            .birthDateTime()
                            .orElseThrow()
                            .atZone(
                                    birthData.zoneId()
                            );

            if (
                    birthDateTime.isBefore(
                            ipchun.dateTime()
                    )
            ) {

                return calendarYear - 1;
            }

            return calendarYear;
        }

        /*
         * 출생시간을 모르는 경우.
         *
         * 입춘일보다 이전 날짜라면 전년도,
         * 이후 날짜라면 해당 연도로 확정 가능하다.
         */
        LocalDate birthDate =
                birthData.solarBirthDate();

        LocalDate ipchunDate =
                ipchun
                        .dateTime()
                        .toLocalDate();

        if (
                birthDate.isBefore(
                        ipchunDate
                )
        ) {

            return calendarYear - 1;
        }

        if (
                birthDate.isAfter(
                        ipchunDate
                )
        ) {

            return calendarYear;
        }

        /*
         * 입춘 당일인데 출생시간을 모르면
         * 입춘 전/후를 구분할 수 없다.
         */
        throw new BirthTimeRequiredException(
                "출생일이 입춘 당일이므로 "
                        + "정확한 년주 계산에는 출생시간이 필요합니다."
        );
    }

    /**
     * 사주 월 인덱스를 결정한다.
     *
     * 0 = 인월
     * 1 = 묘월
     * 2 = 진월
     * ...
     * 10 = 자월
     * 11 = 축월
     */
    public int determineMonthIndex(
            NormalizedBirthData birthData
    ) {

        Objects.requireNonNull(
                birthData,
                "출생정보는 필수입니다."
        );

        int year =
                birthData
                        .solarBirthDate()
                        .getYear();

        List<SolarTermPoint> boundaries =
                getMonthBoundaryCandidates(
                        year,
                        birthData.zoneId()
                );

        /*
         * 출생시간을 알고 있으면
         * 실제 절입 시각과 비교한다.
         */
        if (birthData.hasKnownBirthTime()) {

            ZonedDateTime birthDateTime =
                    birthData
                            .birthDateTime()
                            .orElseThrow()
                            .atZone(
                                    birthData.zoneId()
                            );

            return boundaries
                    .stream()
                    .filter(
                            point ->
                                    !point
                                            .dateTime()
                                            .isAfter(
                                                    birthDateTime
                                            )
                    )
                    .max(
                            Comparator.comparing(
                                    SolarTermPoint::dateTime
                            )
                    )
                    .map(
                            SolarTermPoint::monthIndex
                    )
                    .orElseThrow(
                            () ->
                                    new IllegalStateException(
                                            "출생일 이전의 월 경계 절기를 찾을 수 없습니다."
                                    )
                    );
        }

        /*
         * 출생시간을 모르는 경우에는
         * 날짜만으로 확정 가능한지 확인한다.
         */
        LocalDate birthDate =
                birthData.solarBirthDate();

        boolean boundaryDay =
                boundaries
                        .stream()
                        .anyMatch(
                                point ->
                                        point
                                                .dateTime()
                                                .toLocalDate()
                                                .equals(
                                                        birthDate
                                                )
                        );

        if (boundaryDay) {

            throw new BirthTimeRequiredException(
                    "출생일이 사주 월이 바뀌는 절입 당일이므로 "
                            + "정확한 월주 계산에는 출생시간이 필요합니다."
            );
        }

        return boundaries
                .stream()
                .filter(
                        point ->
                                point
                                        .dateTime()
                                        .toLocalDate()
                                        .isBefore(
                                                birthDate
                                        )
                )
                .max(
                        Comparator.comparing(
                                SolarTermPoint::dateTime
                        )
                )
                .map(
                        SolarTermPoint::monthIndex
                )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "출생일 이전의 월 경계 절기를 찾을 수 없습니다."
                                )
                );
    }

    /**
     * 특정 시각 이전의 가장 가까운
     * 월 경계 절기를 찾는다.
     *
     * 이후 대운 시작 계산에서 사용한다.
     */
    public SolarTermPoint findPreviousMonthBoundary(
            ZonedDateTime dateTime
    ) {

        Objects.requireNonNull(
                dateTime,
                "기준시각은 필수입니다."
        );

        return getMonthBoundaryCandidates(
                dateTime.getYear(),
                dateTime.getZone()
        )
                .stream()
                .filter(
                        point ->
                                !point
                                        .dateTime()
                                        .isAfter(
                                                dateTime
                                        )
                )
                .max(
                        Comparator.comparing(
                                SolarTermPoint::dateTime
                        )
                )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "이전 절입을 찾을 수 없습니다."
                                )
                );
    }

    /**
     * 특정 시각 이후의 가장 가까운
     * 월 경계 절기를 찾는다.
     *
     * 이후 대운 시작 계산에서 사용한다.
     */
    public SolarTermPoint findNextMonthBoundary(
            ZonedDateTime dateTime
    ) {

        Objects.requireNonNull(
                dateTime,
                "기준시각은 필수입니다."
        );

        return getMonthBoundaryCandidates(
                dateTime.getYear(),
                dateTime.getZone()
        )
                .stream()
                .filter(
                        point ->
                                point
                                        .dateTime()
                                        .isAfter(
                                                dateTime
                                        )
                )
                .min(
                        Comparator.comparing(
                                SolarTermPoint::dateTime
                        )
                )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "다음 절입을 찾을 수 없습니다."
                                )
                );
    }

    /**
     * 한 해의 24절기를 실제로 계산한다.
     */
    private List<SolarTermPoint> calculateYear(
            int year,
            ZoneId zoneId
    ) {

        Instant start =
                ZonedDateTime.of(
                                LocalDate.of(
                                        year,
                                        1,
                                        1
                                ),
                                LocalTime.MIDNIGHT,
                                ZoneOffset.UTC
                        )
                        .toInstant();

        Instant end =
                ZonedDateTime.of(
                                LocalDate.of(
                                        year + 1,
                                        1,
                                        1
                                ),
                                LocalTime.MIDNIGHT,
                                ZoneOffset.UTC
                        )
                        .toInstant();

        List<SolarTermDefinition> definitions =
                SolarTermTable.getAll();

        Set<SolarTermDefinition> found =
                new HashSet<>();

        List<SolarTermPoint> points =
                new ArrayList<>();

        Instant current =
                start;

        double currentLongitude =
                calculateApparentSolarLongitude(
                        current
                );

        while (
                current.isBefore(end)
        ) {

            Instant next =
                    current.plus(
                            SCAN_STEP
                    );

            if (next.isAfter(end)) {

                next = end;
            }

            double nextLongitude =
                    calculateApparentSolarLongitude(
                            next
                    );

            for (
                    SolarTermDefinition definition
                    : definitions
            ) {

                if (
                        found.contains(
                                definition
                        )
                ) {

                    continue;
                }

                if (
                        crossesLongitude(
                                currentLongitude,
                                nextLongitude,
                                definition.solarLongitude()
                        )
                ) {

                    Instant exactInstant =
                            refineCrossing(
                                    current,
                                    next,
                                    definition
                                            .solarLongitude()
                            );

                    ZonedDateTime exactDateTime =
                            ZonedDateTime.ofInstant(
                                    exactInstant,
                                    zoneId
                            );

                    points.add(
                            new SolarTermPoint(
                                    definition.koreanName(),
                                    definition.solarLongitude(),
                                    exactDateTime,
                                    definition.monthIndex()
                            )
                    );

                    found.add(
                            definition
                    );
                }
            }

            current =
                    next;

            currentLongitude =
                    nextLongitude;
        }

        if (
                points.size()
                        != definitions.size()
        ) {

            throw new IllegalStateException(
                    year
                            + "년의 24절기를 모두 계산하지 못했습니다. "
                            + "계산된 개수: "
                            + points.size()
            );
        }

        points.sort(
                Comparator.comparing(
                        SolarTermPoint::dateTime
                )
        );

        return List.copyOf(
                points
        );
    }

    /**
     * 두 시각 사이에 목표 황경을
     * 통과했는지 확인한다.
     */
    private boolean crossesLongitude(
            double startLongitude,
            double endLongitude,
            double targetLongitude
    ) {

        double adjustedEnd =
                endLongitude;

        if (
                adjustedEnd
                        < startLongitude
        ) {

            adjustedEnd += 360.0;
        }

        double adjustedTarget =
                targetLongitude;

        while (
                adjustedTarget
                        < startLongitude
        ) {

            adjustedTarget += 360.0;
        }

        return adjustedTarget
                >= startLongitude
                && adjustedTarget
                <= adjustedEnd;
    }

    /**
     * 절기 발생 시각을 이진 탐색으로 정밀화한다.
     */
    private Instant refineCrossing(
            Instant start,
            Instant end,
            double targetLongitude
    ) {

        Instant left =
                start;

        Instant right =
                end;

        double leftLongitude =
                calculateApparentSolarLongitude(
                        left
                );

        double adjustedTarget =
                targetLongitude;

        while (
                adjustedTarget
                        < leftLongitude
        ) {

            adjustedTarget += 360.0;
        }

        for (
                int iteration = 0;
                iteration < 50;
                iteration++
        ) {

            long millis =
                    Duration.between(
                                    left,
                                    right
                            )
                            .toMillis();

            if (millis <= 1000L) {

                break;
            }

            Instant middle =
                    left.plusMillis(
                            millis / 2L
                    );

            double middleLongitude =
                    calculateApparentSolarLongitude(
                            middle
                    );

            middleLongitude =
                    unwrapAroundTarget(
                            middleLongitude,
                            adjustedTarget
                    );

            if (
                    middleLongitude
                            < adjustedTarget
            ) {

                left =
                        middle;

            } else {

                right =
                        middle;
            }
        }

        long remainingMillis =
                Duration.between(
                                left,
                                right
                        )
                        .toMillis();

        return left.plusMillis(
                remainingMillis / 2L
        );
    }

    /**
     * 0/360도 경계에서 값이 끊기지 않도록
     * 목표 황경 주변으로 보정한다.
     */
    private double unwrapAroundTarget(
            double longitude,
            double target
    ) {

        double result =
                longitude;

        while (
                result
                        < target - 180.0
        ) {

            result += 360.0;
        }

        while (
                result
                        > target + 180.0
        ) {

            result -= 360.0;
        }

        return result;
    }

    /**
     * 태양의 겉보기 황경을 계산한다.
     *
     * Julian Day를 기준으로 태양의
     * 평균황경, 평균근점이각,
     * 중심차를 계산한 뒤 겉보기 황경을 구한다.
     */
    private double calculateApparentSolarLongitude(
            Instant instant
    ) {

        double julianDay =
                2440587.5
                        + instant.getEpochSecond()
                        / 86400.0
                        + instant.getNano()
                        / 86_400_000_000_000.0;

        double t =
                (
                        julianDay
                                - 2451545.0
                )
                        / 36525.0;

        double meanLongitude =
                280.46646
                        + t
                        * (
                        36000.76983
                                + 0.0003032
                                * t
                );

        double meanAnomaly =
                357.52911
                        + t
                        * (
                        35999.05029
                                - 0.0001537
                                * t
                );

        double meanAnomalyRadians =
                Math.toRadians(
                        meanAnomaly
                );

        double center =
                Math.sin(
                        meanAnomalyRadians
                )
                        * (
                        1.914602
                                - t
                                * (
                                0.004817
                                        + 0.000014
                                        * t
                        )
                )
                        + Math.sin(
                        2.0
                                * meanAnomalyRadians
                )
                        * (
                        0.019993
                                - 0.000101
                                * t
                )
                        + Math.sin(
                        3.0
                                * meanAnomalyRadians
                )
                        * 0.000289;

        double trueLongitude =
                meanLongitude
                        + center;

        double omega =
                125.04
                        - 1934.136
                        * t;

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
     * 각도를 0 이상 360 미만으로 정규화한다.
     */
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

    /**
     * 해당 연도 주변의 월 경계 절기를 가져온다.
     *
     * 1월 출생자의 경우 이전 해 대설 등이 필요하고,
     * 다음 절입 탐색에서는 다음 해 소한 등이 필요하므로
     * 전년도/해당년도/다음년도를 함께 가져온다.
     */
    private List<SolarTermPoint>
    getMonthBoundaryCandidates(
            int year,
            ZoneId zoneId
    ) {

        List<SolarTermPoint> result =
                new ArrayList<>();

        addMonthBoundaries(
                result,
                calculateAll(
                        year - 1,
                        zoneId
                )
        );

        addMonthBoundaries(
                result,
                calculateAll(
                        year,
                        zoneId
                )
        );

        addMonthBoundaries(
                result,
                calculateAll(
                        year + 1,
                        zoneId
                )
        );

        result.sort(
                Comparator.comparing(
                        SolarTermPoint::dateTime
                )
        );

        return List.copyOf(
                result
        );
    }

    private void addMonthBoundaries(
            List<SolarTermPoint> target,
            List<SolarTermPoint> source
    ) {

        source
                .stream()
                .filter(
                        SolarTermPoint::isMonthBoundary
                )
                .forEach(
                        target::add
                );
    }

    /**
     * 연도 + 시간대 캐시 키.
     */
    private record YearZoneKey(
            int year,
            String zoneId
    ) {
    }
}