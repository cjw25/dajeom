package com.dajeom.calculation;

import com.dajeom.domain.BirthDateTime;
import com.dajeom.domain.Ganji;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.Pillar;
import com.dajeom.rule.SajuRuleBook;
import com.dajeom.solar.SolarTermInstant;
import com.dajeom.solar.SolarTermRepository;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 월주 계산기.
 *
 * 핵심 규칙:
 *
 * 양력 달을 그대로 사용하지 않는다.
 *
 * 월지는 12개의 실제 절입 순간으로 결정한다.
 *
 * 입춘 → 寅
 * 경칩 → 卯
 * 청명 → 辰
 * 입하 → 巳
 * 망종 → 午
 * 소서 → 未
 * 입추 → 申
 * 백로 → 酉
 * 한로 → 戌
 * 입동 → 亥
 * 대설 → 子
 * 소한 → 丑
 *
 * 월간은 해당 사주년의 년간을 기준으로
 * 오호둔 규칙으로 계산한다.
 */
@Component
public class MonthPillarCalculator {

    private final SolarTermRepository solarTermRepository;

    public MonthPillarCalculator(
            SolarTermRepository solarTermRepository
    ) {

        this.solarTermRepository =
                Objects.requireNonNull(
                        solarTermRepository,
                        "SolarTermRepository가 필요합니다."
                );
    }

    /**
     * @param birth    양력으로 정규화된 출생정보
     * @param yearStem 입춘 기준으로 이미 확정된 년간
     */
    public Pillar calculate(
            BirthDateTime birth,
            HeavenlyStem yearStem
    ) {

        Objects.requireNonNull(
                birth,
                "출생정보가 필요합니다."
        );

        Objects.requireNonNull(
                yearStem,
                "년간이 필요합니다."
        );

        requireSolarCalendar(
                birth
        );

        SolarTermInstant monthBoundary =
                determineMonthBoundary(
                        birth
                );

        int sajuMonthNumber =
                monthBoundary
                        .term()
                        .requireSajuMonthNumber();

        Ganji ganji =
                SajuRuleBook.monthGanji(
                        yearStem,
                        sajuMonthNumber
                );

        return new Pillar(
                ganji
        );
    }

    /**
     * 해당 출생시점에 적용되는 가장 최근 12절입을 찾는다.
     */
    public SolarTermInstant determineMonthBoundary(
            BirthDateTime birth
    ) {

        Objects.requireNonNull(
                birth,
                "출생정보가 필요합니다."
        );

        requireSolarCalendar(
                birth
        );

        if (birth.hasKnownTime()) {

            return determineWithKnownTime(
                    birth
            );
        }

        return determineWithUnknownTime(
                birth
        );
    }

    /**
     * 출생시간을 아는 경우에는 실제 순간으로 비교한다.
     */
    private SolarTermInstant determineWithKnownTime(
            BirthDateTime birth
    ) {

        LocalDateTime birthDateTime =
                birth.requireDateTime();

        Instant birthInstant =
                birthDateTime
                        .atZone(
                                SolarTermInstant.KOREA_ZONE
                        )
                        .toInstant();

        return solarTermRepository
                .findLatestMonthBoundaryAtOrBefore(
                        birthInstant
                );
    }

    /**
     * 출생시간을 모르는 경우.
     *
     * 절입 날짜와 출생 날짜가 다르면 날짜만으로
     * 안전하게 월을 결정할 수 있다.
     *
     * 그러나 출생일 자체가 절입일이면
     * 정확한 출생시간 없이는 월주를 확정할 수 없다.
     */
    private SolarTermInstant determineWithUnknownTime(
            BirthDateTime birth
    ) {

        LocalDate birthDate =
                birth.birthDate();

        int centerYear =
                birthDate.getYear();

        List<SolarTermInstant> boundaries =
                collectMonthBoundaries(
                        centerYear
                );

        /*
         * 먼저 출생일과 같은 날짜에 절입이 있는지 확인한다.
         *
         * 있다면 시간 미상이므로 경계 전/후 판정 불가능.
         */
        for (SolarTermInstant boundary : boundaries) {

            LocalDate boundaryDate =
                    boundary
                            .koreaLocalDateTime()
                            .toLocalDate();

            if (boundaryDate.equals(
                    birthDate
            )) {

                throw new IllegalStateException(
                        "출생일이 사주 월 절입 당일이지만 출생시간을 알 수 없어 "
                                + "월주를 확정할 수 없습니다. birthDate="
                                + birthDate
                                + ", boundary="
                                + boundary
                );
            }
        }

        /*
         * 출생 날짜보다 이전 날짜에 시작된
         * 가장 최근 절입을 선택한다.
         */
        return boundaries
                .stream()
                .filter(boundary -> {

                    LocalDate boundaryDate =
                            boundary
                                    .koreaLocalDateTime()
                                    .toLocalDate();

                    return boundaryDate.isBefore(
                            birthDate
                    );
                })
                .max(
                        Comparator.comparing(
                                SolarTermInstant::instant
                        )
                )
                .orElseThrow(() ->
                        new IllegalStateException(
                                "출생일 이전의 사주 월 절입을 찾을 수 없습니다. birthDate="
                                        + birthDate
                        )
                );
    }

    /**
     * 연말/연초까지 안전하게 처리하기 위해
     * 전년도·해당연도·다음연도의 12절입을 함께 조회한다.
     */
    private List<SolarTermInstant> collectMonthBoundaries(
            int centerYear
    ) {

        List<SolarTermInstant> result =
                new ArrayList<>(
                        36
                );

        result.addAll(
                solarTermRepository.findMonthBoundaries(
                        centerYear - 1
                )
        );

        result.addAll(
                solarTermRepository.findMonthBoundaries(
                        centerYear
                )
        );

        result.addAll(
                solarTermRepository.findMonthBoundaries(
                        centerYear + 1
                )
        );

        result.sort(
                Comparator.comparing(
                        SolarTermInstant::instant
                )
        );

        return List.copyOf(
                result
        );
    }

    private void requireSolarCalendar(
            BirthDateTime birth
    ) {

        if (!birth.isSolar()) {

            throw new IllegalArgumentException(
                    "MonthPillarCalculator에는 양력으로 정규화된 출생정보만 입력할 수 있습니다."
            );
        }
    }
}