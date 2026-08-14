package com.dajeom.calculator;

import com.dajeom.model.DecadeLuckDirectionAnalysis;
import com.dajeom.model.DecadeLuckStartAnalysis;
import com.dajeom.model.LuckStartAge;
import com.dajeom.model.LuckStartCandidate;
import com.dajeom.model.SolarTermPoint;
import com.dajeom.type.LuckStartBoundaryType;
import com.dajeom.type.LuckStartPrecision;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 13차-2.
 *
 * 대운 시작 나이 및 교운시점 계산.
 *
 * =================================================
 *
 * 전통 환산 정책:
 *
 * 실제 3일
 * -> 기산 1년
 *
 * 실제 1일
 * -> 기산 4개월
 *
 * 실제 1시간
 * -> 기산 5일
 *
 * 따라서:
 *
 * 실제 12분
 * -> 기산 1일
 *
 * =================================================
 *
 * 중요:
 *
 * 1.
 * 실제 절입 시각을 사용한다.
 *
 * 2.
 * Duration에 시·분을 포함한다.
 *
 * 3.
 * 출생시간 미상 시 정오를 넣지 않는다.
 *
 * 4.
 * 시간 미상은 범위로 반환한다.
 *
 * 5.
 * 출생일 자체가 절입일이면
 * SOLAR_TERM_BOUNDARY_UNCERTAIN으로 반환한다.
 */
@Component
public class LuckStartCalculator {

    /**
     * 실제 12분 =
     * 대운 기산 나이 1일.
     */
    private static final long
            REAL_SECONDS_PER_TRADITIONAL_DAY =
            12L * 60L;

    private static final int
            TRADITIONAL_DAYS_PER_MONTH =
            30;

    private static final int
            TRADITIONAL_MONTHS_PER_YEAR =
            12;

    private static final int
            TRADITIONAL_DAYS_PER_YEAR =
            TRADITIONAL_DAYS_PER_MONTH
                    * TRADITIONAL_MONTHS_PER_YEAR;

    private final SolarTermCalculator
            solarTermCalculator;

    public LuckStartCalculator(
            SolarTermCalculator solarTermCalculator
    ) {

        this.solarTermCalculator =
                Objects.requireNonNull(
                        solarTermCalculator,
                        "SolarTermCalculator가 필요합니다."
                );
    }

    /**
     * =================================================
     * 정확한 출생시간
     * =================================================
     *
     * 공식 EXACT API.
     */
    public DecadeLuckStartAnalysis analyze(

            LocalDateTime birthDateTime,

            ZoneId zoneId,

            DecadeLuckDirectionAnalysis directionAnalysis

    ) {

        Objects.requireNonNull(
                birthDateTime,
                "대운 기산에는 출생일시가 필요합니다."
        );

        Objects.requireNonNull(
                zoneId,
                "대운 기산에는 ZoneId가 필요합니다."
        );

        Objects.requireNonNull(
                directionAnalysis,
                "13-1 대운 방향 분석이 필요합니다."
        );

        LuckStartCandidate candidate =
                calculateCandidate(
                        birthDateTime,
                        zoneId,
                        directionAnalysis
                );

        return new DecadeLuckStartAnalysis(
                directionAnalysis,
                LuckStartPrecision.EXACT,
                birthDateTime.toLocalDate(),
                zoneId,
                candidate,
                candidate,
                false
        );
    }

    /**
     * =================================================
     * 출생시간 미상
     * =================================================
     *
     * 정오나 자정을 하나의 출생시간으로
     * 확정하지 않는다.
     *
     * 해당 출생일 안에서 가능한 시간을
     * 범위 분석한다.
     */
    public DecadeLuckStartAnalysis analyzeUnknownTime(

            LocalDate birthDate,

            ZoneId zoneId,

            DecadeLuckDirectionAnalysis directionAnalysis

    ) {

        Objects.requireNonNull(
                birthDate,
                "출생시간 미상 분석에는 출생일이 필요합니다."
        );

        Objects.requireNonNull(
                zoneId,
                "대운 기산에는 ZoneId가 필요합니다."
        );

        Objects.requireNonNull(
                directionAnalysis,
                "13-1 대운 방향 분석이 필요합니다."
        );

        LocalDateTime startOfDay =
                birthDate.atStartOfDay();

        LocalDateTime endOfDay =
                LocalDateTime.of(
                        birthDate,
                        LocalTime.of(
                                23,
                                59,
                                59
                        )
                );

        /*
         * =================================================
         * Candidate 계산시각
         * =================================================
         *
         * 기본:
         *
         * 00:00:00
         * 23:59:59
         *
         * 출생일 안에 절입이 있으면:
         *
         * 절입 직전
         * 절입 순간
         *
         * 도 추가한다.
         *
         * 그래야 절입을 사이에 두고
         * 기산값이 크게 바뀌는 경우를 놓치지 않는다.
         */

        Set<LocalDateTime> candidateDateTimes =
                new LinkedHashSet<>();

        candidateDateTimes.add(
                startOfDay
        );

        candidateDateTimes.add(
                endOfDay
        );

        SolarTermPoint boundaryWithinDate =
                findBoundaryWithinDate(
                        birthDate,
                        zoneId
                );

        boolean boundaryCrossing =
                boundaryWithinDate != null;

        if (
                boundaryWithinDate != null
        ) {

            ZonedDateTime boundaryDateTime =
                    boundaryWithinDate
                            .dateTime()
                            .withZoneSameInstant(
                                    zoneId
                            );

            LocalDateTime exactBoundary =
                    boundaryDateTime
                            .toLocalDateTime();

            if (
                    exactBoundary
                            .toLocalDate()
                            .equals(
                                    birthDate
                            )
            ) {

                candidateDateTimes.add(
                        exactBoundary
                );
            }

            LocalDateTime immediatelyBefore =
                    exactBoundary
                            .minusSeconds(
                                    1
                            );

            if (
                    immediatelyBefore
                            .toLocalDate()
                            .equals(
                                    birthDate
                            )
            ) {

                candidateDateTimes.add(
                        immediatelyBefore
                );
            }
        }

        List<LuckStartCandidate> candidates =
                new ArrayList<>();

        for (
                LocalDateTime candidateBirthDateTime
                : candidateDateTimes
        ) {

            candidates.add(
                    calculateCandidate(
                            candidateBirthDateTime,
                            zoneId,
                            directionAnalysis
                    )
            );
        }

        Comparator<LuckStartCandidate> comparator =
                Comparator
                        .comparing(
                                LuckStartCandidate::startAge
                        )
                        .thenComparing(
                                LuckStartCandidate
                                        ::firstLuckStartDateTime
                        );

        LuckStartCandidate minimum =
                candidates
                        .stream()
                        .min(
                                comparator
                        )
                        .orElseThrow();

        LuckStartCandidate maximum =
                candidates
                        .stream()
                        .max(
                                comparator
                        )
                        .orElseThrow();

        LuckStartPrecision precision =
                boundaryCrossing

                        ? LuckStartPrecision
                        .SOLAR_TERM_BOUNDARY_UNCERTAIN

                        : LuckStartPrecision
                        .TIME_UNKNOWN_RANGE;

        return new DecadeLuckStartAnalysis(
                directionAnalysis,
                precision,
                birthDate,
                zoneId,
                minimum,
                maximum,
                boundaryCrossing
        );
    }

    /**
     * =================================================
     * 하나의 출생시각 기준 계산
     * =================================================
     */
    private LuckStartCandidate calculateCandidate(

            LocalDateTime birthDateTime,

            ZoneId zoneId,

            DecadeLuckDirectionAnalysis directionAnalysis

    ) {

        LuckStartBoundaryType boundaryType =
                directionAnalysis
                        .startBoundaryType();

        SolarTermPoint boundaryPoint =
                findBoundary(
                        birthDateTime,
                        zoneId,
                        boundaryType
                );

        ZonedDateTime birth =
                birthDateTime
                        .atZone(
                                zoneId
                        );

        ZonedDateTime boundary =
                boundaryPoint
                        .dateTime()
                        .withZoneSameInstant(
                                zoneId
                        );

        Duration sourceDuration =
                switch (
                        boundaryType
                        ) {

                    case NEXT_MONTH_BOUNDARY ->
                            Duration.between(
                                    birth,
                                    boundary
                            );

                    case PREVIOUS_MONTH_BOUNDARY ->
                            Duration.between(
                                    boundary,
                                    birth
                            );
                };

        if (
                sourceDuration.isNegative()
        ) {

            throw new IllegalStateException(
                    "출생시각과 절입시각의 방향이 올바르지 않습니다."
            );
        }

        LuckStartAge startAge =
                convertDurationToStartAge(
                        sourceDuration
                );

        ZonedDateTime firstLuckStartDateTime =
                applyStartAge(
                        birth,
                        startAge
                );

        return new LuckStartCandidate(
                birth,
                boundaryPoint,
                boundaryType,
                sourceDuration,
                startAge,
                firstLuckStartDateTime
        );
    }

    /**
     * =================================================
     * 절입 선택
     * =================================================
     *
     * 기존 SolarTermCalculator를 그대로 사용한다.
     */
    private SolarTermPoint findBoundary(

            LocalDateTime birthDateTime,

            ZoneId zoneId,

            LuckStartBoundaryType boundaryType

    ) {

        return switch (
                boundaryType
                ) {

            case NEXT_MONTH_BOUNDARY ->
                    solarTermCalculator
                            .findNextMonthBoundary(
                                    birthDateTime,
                                    zoneId
                            );

            case PREVIOUS_MONTH_BOUNDARY ->
                    solarTermCalculator
                            .findPreviousMonthBoundary(
                                    birthDateTime,
                                    zoneId
                            );
        };
    }

    /**
     * =================================================
     * Duration -> 대운 시작 나이
     * =================================================
     *
     * 실제:
     *
     * 3일
     *
     * =
     *
     * symbolic:
     *
     * 360일
     *
     * 따라서:
     *
     * 실제 12분
     * =
     * symbolic 1일
     *
     * =================================================
     *
     * 전체를 먼저 symbolic day로 환산한 뒤
     * 년/월/일로 나누므로
     *
     * 11개월 30일
     *
     * 같은 비정규 상태를 만들지 않는다.
     */
    public LuckStartAge convertDurationToStartAge(
            Duration sourceDuration
    ) {

        Objects.requireNonNull(
                sourceDuration,
                "대운 기산 Duration이 필요합니다."
        );

        if (
                sourceDuration.isNegative()
        ) {

            throw new IllegalArgumentException(
                    "대운 기산 Duration은 음수일 수 없습니다."
            );
        }

        double totalSeconds =
                sourceDuration.getSeconds()
                        + sourceDuration.getNano()
                        / 1_000_000_000.0;

        long totalTraditionalDays =
                Math.round(
                        totalSeconds
                                / REAL_SECONDS_PER_TRADITIONAL_DAY
                );

        int years =
                Math.toIntExact(
                        totalTraditionalDays
                                / TRADITIONAL_DAYS_PER_YEAR
                );

        long remainderAfterYears =
                totalTraditionalDays
                        % TRADITIONAL_DAYS_PER_YEAR;

        int months =
                (int) (
                        remainderAfterYears
                                / TRADITIONAL_DAYS_PER_MONTH
                );

        int days =
                (int) (
                        remainderAfterYears
                                % TRADITIONAL_DAYS_PER_MONTH
                );

        return new LuckStartAge(
                years,
                months,
                days
        );
    }

    /**
     * =================================================
     * 기산 나이 -> 실제 교운시점
     * =================================================
     *
     * symbolic age를
     * 실제 출생 DateTime에:
     *
     * 년
     * 월
     * 일
     *
     * 순으로 더한다.
     */
    public ZonedDateTime applyStartAge(

            ZonedDateTime birthDateTime,

            LuckStartAge startAge

    ) {

        Objects.requireNonNull(
                birthDateTime,
                "출생시각이 필요합니다."
        );

        Objects.requireNonNull(
                startAge,
                "대운 시작 나이가 필요합니다."
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

    /**
     * =================================================
     * 출생일 안에 절입이 존재하는지 확인
     * =================================================
     *
     * 시간 미상인데 그날 절입이 있으면
     *
     * 절입 이전인지
     * 절입 이후인지
     *
     * 알 수 없기 때문에
     * 별도 Precision으로 올린다.
     */
    private SolarTermPoint findBoundaryWithinDate(

            LocalDate birthDate,

            ZoneId zoneId

    ) {

        LocalDateTime start =
                birthDate
                        .atStartOfDay();

        ZonedDateTime startZoned =
                start
                        .atZone(
                                zoneId
                        );

        ZonedDateTime endZoned =
                birthDate
                        .plusDays(
                                1
                        )
                        .atStartOfDay(
                                zoneId
                        )
                        .minusSeconds(
                                1
                        );

        /*
         * =================================================
         * 자정에 정확히 절입한 경우 확인
         * =================================================
         */

        SolarTermPoint previous =
                solarTermCalculator
                        .findPreviousMonthBoundary(
                                start,
                                zoneId
                        );

        ZonedDateTime previousDateTime =
                previous
                        .dateTime()
                        .withZoneSameInstant(
                                zoneId
                        );

        if (
                !previousDateTime
                        .isBefore(
                                startZoned
                        )

                        && !previousDateTime
                        .isAfter(
                                endZoned
                        )
        ) {

            return previous;
        }

        /*
         * =================================================
         * 당일 자정 이후 절입 확인
         * =================================================
         */

        SolarTermPoint next =
                solarTermCalculator
                        .findNextMonthBoundary(
                                start,
                                zoneId
                        );

        ZonedDateTime nextDateTime =
                next
                        .dateTime()
                        .withZoneSameInstant(
                                zoneId
                        );

        if (
                !nextDateTime
                        .isBefore(
                                startZoned
                        )

                        && !nextDateTime
                        .isAfter(
                                endZoned
                        )
        ) {

            return next;
        }

        return null;
    }
}