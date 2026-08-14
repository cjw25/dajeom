package com.dajeom.calculator;

import com.dajeom.constant.GanjiCycleTable;
import com.dajeom.model.Pillar;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * 사주의 월주를 계산한다.
 *
 * 핵심 원칙
 *
 * 1. 양력 월 초를 월주 경계로 사용하지 않는다.
 * 2. 음력 초하루도 월주 경계로 사용하지 않는다.
 * 3. 명리 월주는 실제 12절입 시각을 기준으로 변경한다.
 *
 * 寅월 = 입춘
 * 卯월 = 경칩
 * 辰월 = 청명
 * 巳월 = 입하
 * 午월 = 망종
 * 未월 = 소서
 * 申월 = 입추
 * 酉월 = 백로
 * 戌월 = 한로
 * 亥월 = 입동
 * 子월 = 대설
 * 丑월 = 소한
 *
 * 월간은 오호둔(五虎遁)을 적용한다.
 *
 * 甲己年 -> 丙寅부터
 * 乙庚年 -> 戊寅부터
 * 丙辛年 -> 庚寅부터
 * 丁壬年 -> 壬寅부터
 * 戊癸年 -> 甲寅부터
 */
@Component
public class MonthPillarCalculator {

    /**
     * 지지 인덱스 기준
     *
     * 子 = 0
     * 丑 = 1
     * 寅 = 2
     */
    private static final int TIGER_BRANCH_INDEX =
            2;

    private static final int STEM_COUNT =
            10;

    private static final int BRANCH_COUNT =
            12;

    private static final int MONTH_INDEX_MIN =
            0;

    private static final int MONTH_INDEX_MAX =
            11;

    private final SolarTermCalculator
            solarTermCalculator;

    private final YearPillarCalculator
            yearPillarCalculator;

    public MonthPillarCalculator(
            SolarTermCalculator solarTermCalculator,
            YearPillarCalculator yearPillarCalculator
    ) {
        this.solarTermCalculator =
                Objects.requireNonNull(
                        solarTermCalculator,
                        "SolarTermCalculator가 필요합니다."
                );

        this.yearPillarCalculator =
                Objects.requireNonNull(
                        yearPillarCalculator,
                        "YearPillarCalculator가 필요합니다."
                );
    }

    /**
     * 출생시간이 존재하는 경우 월주 계산.
     */
    public Pillar calculate(
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

        /*
         * 월간은 명리상의 년간을 기준으로 계산해야 한다.
         *
         * 따라서 양력 연도 자체가 아니라
         * 입춘을 통과했는지 확인한 사주 연도를 사용한다.
         */
        int sajuYear =
                yearPillarCalculator
                        .determineSajuYear(
                                birthDateTime,
                                zoneId
                        );

        /*
         * 실제 절입 시각을 기준으로
         * 寅월 ~ 丑월의 인덱스를 계산한다.
         */
        int monthIndex =
                solarTermCalculator
                        .determineMonthIndex(
                                birthDateTime,
                                zoneId
                        );

        return calculateFromSajuYearAndMonthIndex(
                sajuYear,
                monthIndex
        );
    }

    /**
     * 출생시간이 없는 경우 월주 계산.
     *
     * 다음 두 경우에는 확정하지 않는다.
     *
     * 1. 입춘 당일
     *    -> 년주 자체가 시간에 따라 달라질 수 있음
     *
     * 2. 다른 절입 당일
     *    -> 월주가 절입 전/후로 달라질 수 있음
     *
     * 임의로 00:00 또는 12:00를 사용하지 않는다.
     */
    public Optional<Pillar> calculate(
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

        OptionalInt sajuYear =
                yearPillarCalculator
                        .determineSajuYear(
                                birthDate,
                                zoneId
                        );

        if (
                sajuYear.isEmpty()
        ) {
            return Optional.empty();
        }

        OptionalInt monthIndex =
                solarTermCalculator
                        .determineMonthIndex(
                                birthDate,
                                zoneId
                        );

        if (
                monthIndex.isEmpty()
        ) {
            return Optional.empty();
        }

        return Optional.of(
                calculateFromSajuYearAndMonthIndex(
                        sajuYear.getAsInt(),
                        monthIndex.getAsInt()
                )
        );
    }

    /**
     * 이미 사주 연도와 절입 월 인덱스를 알고 있는 경우
     * 직접 월주를 계산한다.
     *
     * monthIndex:
     *
     * 0  寅
     * 1  卯
     * 2  辰
     * 3  巳
     * 4  午
     * 5  未
     * 6  申
     * 7  酉
     * 8  戌
     * 9  亥
     * 10 子
     * 11 丑
     */
    public Pillar calculateFromSajuYearAndMonthIndex(
            int sajuYear,
            int monthIndex
    ) {
        validateMonthIndex(
                monthIndex
        );

        /*
         * 1984년 = 甲子년이므로
         *
         * 사주 연도의 천간 인덱스는
         * 1984 기준 10간 순환으로 계산할 수 있다.
         *
         * 甲 = 0
         * 乙 = 1
         * 丙 = 2
         * 丁 = 3
         * 戊 = 4
         * 己 = 5
         * 庚 = 6
         * 辛 = 7
         * 壬 = 8
         * 癸 = 9
         */
        int yearStemIndex =
                Math.floorMod(
                        sajuYear - 1984,
                        STEM_COUNT
                );

        return calculateFromYearStemIndexAndMonthIndex(
                yearStemIndex,
                monthIndex
        );
    }

    /**
     * 오호둔을 사용해 월간을 계산한다.
     */
    public Pillar calculateFromYearStemIndexAndMonthIndex(
            int yearStemIndex,
            int monthIndex
    ) {
        validateMonthIndex(
                monthIndex
        );

        int normalizedYearStemIndex =
                Math.floorMod(
                        yearStemIndex,
                        STEM_COUNT
                );

        /*
         * 오호둔:
         *
         * 甲/己 -> 丙寅
         * 乙/庚 -> 戊寅
         * 丙/辛 -> 庚寅
         * 丁/壬 -> 壬寅
         * 戊/癸 -> 甲寅
         *
         * 년간을 5개 그룹으로 줄이면
         *
         * 0 -> 丙 = 2
         * 1 -> 戊 = 4
         * 2 -> 庚 = 6
         * 3 -> 壬 = 8
         * 4 -> 甲 = 0
         *
         * 공식:
         *
         * firstStem =
         * ((yearStemIndex % 5) * 2 + 2) % 10
         */
        int tigerMonthStemIndex =
                Math.floorMod(
                        (
                                normalizedYearStemIndex
                                        % 5
                        )
                                * 2
                                + 2,
                        STEM_COUNT
                );

        int monthStemIndex =
                Math.floorMod(
                        tigerMonthStemIndex
                                + monthIndex,
                        STEM_COUNT
                );

        int monthBranchIndex =
                Math.floorMod(
                        TIGER_BRANCH_INDEX
                                + monthIndex,
                        BRANCH_COUNT
                );

        HeavenlyStem stem =
                HeavenlyStem.fromIndex(
                        monthStemIndex
                );

        EarthlyBranch branch =
                EarthlyBranch.fromIndex(
                        monthBranchIndex
                );

        int cycleIndex =
                GanjiCycleTable.findIndex(
                        stem,
                        branch
                );

        return GanjiCycleTable.get(
                cycleIndex
        );
    }

    private void validateMonthIndex(
            int monthIndex
    ) {
        if (
                monthIndex
                        < MONTH_INDEX_MIN
                        || monthIndex
                        > MONTH_INDEX_MAX
        ) {
            throw new IllegalArgumentException(
                    "명리 월 인덱스는 0부터 11까지입니다: "
                            + monthIndex
            );
        }
    }
}