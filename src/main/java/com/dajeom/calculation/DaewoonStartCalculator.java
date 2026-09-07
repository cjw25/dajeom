package com.dajeom.calculation;

import com.dajeom.domain.BirthDateTime;
import com.dajeom.domain.DaewoonDirection;
import com.dajeom.domain.DaewoonStart;
import com.dajeom.solar.SolarTermInstant;
import com.dajeom.solar.SolarTermRepository;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.zone.ZoneRules;
import java.util.List;
import java.util.Objects;

/**
 * 대운 시작시점 계산.
 *
 * 기본 절입거리:
 *
 * 순행
 * → 출생시각부터 다음 절입까지
 *
 * 역행
 * → 이전 절입부터 출생시각까지
 *
 * 대운수 환산:
 *
 * 실제 3일 = 대운 1년
 *
 * 전통 표시 환산:
 *
 * 3일  = 1년
 * 6시간 = 1개월
 * 12분 = 1일
 *
 * 첫대운 정밀 시작시점:
 *
 * 정밀 대운수 × 365일을
 * 출생 순간에 더한다.
 *
 * 화면의 "첫대운 ...경" 날짜는
 * 정밀 한국시간을 가장 가까운 날짜로 반올림한다.
 */
@Component
public class DaewoonStartCalculator {

    private static final long THREE_DAYS_MILLIS =
            Duration
                    .ofDays(3)
                    .toMillis();

    /**
     * 3일 = 1년이므로
     *
     * 3일 / 12개월
     * = 6시간 / 1개월
     */
    private static final long SIX_HOURS_MILLIS =
            Duration
                    .ofHours(6)
                    .toMillis();

    /**
     * 표시용 1개월을 30일로 분해하면
     *
     * 6시간 / 30
     * = 12분 / 1일
     */
    private static final long TWELVE_MINUTES_MILLIS =
            Duration
                    .ofMinutes(12)
                    .toMillis();

    /**
     * 첫대운의 실제 시간축 변환에는
     * 1년 = 365일을 사용한다.
     *
     * 1993 검증 사례:
     *
     * 정밀값 약 5.475716년
     * → 1999-02-12 22:49경
     * → 최근접 날짜 1999-02-13
     */
    private static final long DAYS_PER_YEAR =
            365L;

    private static final int AGE_SCALE =
            9;

    private final SolarTermRepository solarTermRepository;

    public DaewoonStartCalculator(
            SolarTermRepository solarTermRepository
    ) {

        this.solarTermRepository =
                Objects.requireNonNull(
                        solarTermRepository,
                        "SolarTermRepository가 필요합니다."
                );
    }

    public DaewoonStart calculate(
            BirthDateTime solarBirth,
            DaewoonDirection direction
    ) {

        Objects.requireNonNull(
                solarBirth,
                "대운 시작 계산에는 출생정보가 필요합니다."
        );

        Objects.requireNonNull(
                direction,
                "대운 시작 계산에는 방향이 필요합니다."
        );

        if (!solarBirth.isSolar()) {

            throw new IllegalArgumentException(
                    "대운 시작 계산에는 양력으로 정규화된 출생정보가 필요합니다."
            );
        }

        if (!solarBirth.hasKnownTime()) {

            throw new IllegalArgumentException(
                    "정밀 대운 시작 계산에는 출생시간이 필요합니다."
            );
        }

        LocalDateTime birthLocalDateTime =
                solarBirth.requireDateTime();

        Instant birthInstant =
                resolveBirthInstant(
                        birthLocalDateTime
                );

        SolarTermInstant referenceTerm =
                findReferenceTerm(
                        birthInstant,
                        direction
                );

        Duration distance =
                calculateDistance(
                        birthInstant,
                        referenceTerm,
                        direction
                );

        long distanceMillis =
                distance.toMillis();

        /*
         * 정밀 대운수.
         *
         * 실제 절입거리 ÷ 3일.
         */
        BigDecimal preciseStartAgeYears =
                BigDecimal
                        .valueOf(
                                distanceMillis
                        )
                        .divide(
                                BigDecimal.valueOf(
                                        THREE_DAYS_MILLIS
                                ),
                                AGE_SCALE,
                                RoundingMode.HALF_UP
                        );

        /*
         * 화면 표기용:
         *
         * 3일 = 1년
         * 6시간 = 1개월
         * 12분 = 1일
         */
        TraditionalDisplay traditionalDisplay =
                calculateTraditionalDisplay(
                        distanceMillis
                );

        /*
         * 화면의 대운수는
         * 완성된 년수 부분을 사용한다.
         *
         * 5년 5개월 21일
         * → 대운수 5
         */
        int daewoonNumber =
                traditionalDisplay.years();

        /*
         * 첫대운 정밀 시작시간.
         *
         * 정밀 대운수 × 365일
         *
         * algebra:
         *
         * distanceMillis
         * ---------------- × 365일
         * 3일
         *
         * 시간 단위가 동일하므로
         *
         * distanceMillis × 365 / 3
         */
        long firstDaewoonOffsetMillis =
                BigDecimal
                        .valueOf(
                                distanceMillis
                        )
                        .multiply(
                                BigDecimal.valueOf(
                                        DAYS_PER_YEAR
                                )
                        )
                        .divide(
                                BigDecimal.valueOf(
                                        3L
                                ),
                                0,
                                RoundingMode.HALF_UP
                        )
                        .longValueExact();

        Instant firstDaewoonInstant =
                birthInstant.plusMillis(
                        firstDaewoonOffsetMillis
                );

        LocalDateTime firstDaewoonKoreaDateTime =
                firstDaewoonInstant
                        .atZone(
                                SolarTermInstant.KOREA_ZONE
                        )
                        .toLocalDateTime();

        /*
         * 만세력 화면의 "...일경" 표시용.
         *
         * 00:00 ~ 11:59
         * → 당일
         *
         * 12:00 ~ 23:59
         * → 다음 날짜
         *
         * 즉 가장 가까운 날짜로 반올림한다.
         */
        LocalDate firstDaewoonApproxDate =
                roundToNearestDate(
                        firstDaewoonKoreaDateTime
                );

        return new DaewoonStart(
                direction,

                referenceTerm.term(),

                referenceTerm.instant(),
                referenceTerm.koreaLocalDateTime(),

                birthInstant,
                birthLocalDateTime,

                distanceMillis,

                preciseStartAgeYears,

                daewoonNumber,

                traditionalDisplay.years(),
                traditionalDisplay.months(),
                traditionalDisplay.days(),

                firstDaewoonInstant,
                firstDaewoonKoreaDateTime,

                firstDaewoonApproxDate
        );
    }

    private SolarTermInstant findReferenceTerm(
            Instant birthInstant,
            DaewoonDirection direction
    ) {

        if (direction.isForward()) {

            return solarTermRepository
                    .findNextMonthBoundaryAfter(
                            birthInstant
                    );
        }

        return solarTermRepository
                .findLatestMonthBoundaryAtOrBefore(
                        birthInstant
                );
    }

    private Duration calculateDistance(
            Instant birthInstant,
            SolarTermInstant referenceTerm,
            DaewoonDirection direction
    ) {

        Duration distance;

        if (direction.isForward()) {

            distance =
                    Duration.between(
                            birthInstant,
                            referenceTerm.instant()
                    );

        } else {

            distance =
                    Duration.between(
                            referenceTerm.instant(),
                            birthInstant
                    );
        }

        if (distance.isNegative()) {

            throw new IllegalStateException(
                    "대운 절입거리 계산 결과가 음수입니다. direction="
                            + direction
                            + ", birth="
                            + birthInstant
                            + ", reference="
                            + referenceTerm.instant()
            );
        }

        return distance;
    }

    /**
     * 실제 절입거리를
     *
     * 년 / 개월 / 일
     *
     * 표시값으로 환산한다.
     *
     * 예:
     *
     * 16일 10시간 15분 5초
     *
     * 15일
     * → 5년
     *
     * 남은 1일
     * → 4개월
     *
     * 남은 10시간
     * → 1개월 + 4시간
     *
     * 남은 4시간 15분
     * → 21일 + 잔여
     *
     * 결과:
     * 5년 5개월 21일
     */
    private TraditionalDisplay calculateTraditionalDisplay(
            long distanceMillis
    ) {

        long remaining =
                distanceMillis;

        int years =
                Math.toIntExact(
                        remaining
                                / THREE_DAYS_MILLIS
                );

        remaining =
                remaining
                        % THREE_DAYS_MILLIS;

        int months =
                Math.toIntExact(
                        remaining
                                / SIX_HOURS_MILLIS
                );

        remaining =
                remaining
                        % SIX_HOURS_MILLIS;

        int days =
                Math.toIntExact(
                        remaining
                                / TWELVE_MINUTES_MILLIS
                );

        return new TraditionalDisplay(
                years,
                months,
                days
        );
    }

    private LocalDate roundToNearestDate(
            LocalDateTime dateTime
    ) {

        if (!dateTime
                .toLocalTime()
                .isBefore(
                        LocalTime.NOON
                )) {

            return dateTime
                    .toLocalDate()
                    .plusDays(
                            1
                    );
        }

        return dateTime.toLocalDate();
    }

    /**
     * 출생 당시 실제 한국 민간시각을
     * Instant로 변환한다.
     *
     * 역사적 DST는
     * Asia/Seoul ZoneRules를 그대로 따른다.
     */
    private Instant resolveBirthInstant(
            LocalDateTime birthLocalDateTime
    ) {

        ZoneRules rules =
                SolarTermInstant
                        .KOREA_ZONE
                        .getRules();

        List<ZoneOffset> validOffsets =
                rules.getValidOffsets(
                        birthLocalDateTime
                );

        if (validOffsets.isEmpty()) {

            throw new IllegalArgumentException(
                    "출생시각이 역사적 시간대 전환으로 존재하지 않는 시각입니다. birth="
                            + birthLocalDateTime
            );
        }

        if (validOffsets.size()
                != 1) {

            throw new IllegalArgumentException(
                    "출생시각이 역사적 시간대 전환으로 중복되는 시각입니다. birth="
                            + birthLocalDateTime
                            + ", offsets="
                            + validOffsets
            );
        }

        return birthLocalDateTime
                .toInstant(
                        validOffsets.get(0)
                );
    }

    private record TraditionalDisplay(
            int years,
            int months,
            int days
    ) {
    }
}