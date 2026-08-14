package com.dajeom.calculator;

import com.dajeom.constant.GanjiCycleTable;
import com.dajeom.model.Pillar;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;

/**
 * 사주의 시주(時柱)를 계산한다.
 *
 * 다점 시주 계산 정책
 *
 * 1. 사용자가 입력한 출생시간을 그대로 사용한다.
 * 2. 진태양시 보정을 사용하지 않는다.
 * 3. 경도 보정을 사용하지 않는다.
 * 4. 지역에 따른 임의 분 단위 보정을 사용하지 않는다.
 * 5. 子時는 23:00 ~ 00:59로 본다.
 * 6. 일주 자체의 변경은 00:00 기준이다.
 * 7. 야자시를 이유로 23시에 다음날 일간을 사용하지 않는다.
 *
 * 시천간은 오서둔(五鼠遁)을 사용한다.
 *
 * 甲己日 -> 甲子時
 * 乙庚日 -> 丙子時
 * 丙辛日 -> 戊子時
 * 丁壬日 -> 庚子時
 * 戊癸日 -> 壬子時
 */
@Component
public class HourPillarCalculator {

    private static final int STEM_COUNT =
            10;

    private static final int BRANCH_COUNT =
            12;

    /**
     * 일주와 출생시간을 받아 시주를 계산한다.
     *
     * 가장 기본이 되는 메서드.
     */
    public Pillar calculate(
            Pillar dayPillar,
            LocalTime birthTime
    ) {
        Objects.requireNonNull(
                dayPillar,
                "일주가 필요합니다."
        );

        Objects.requireNonNull(
                birthTime,
                "출생시간이 필요합니다."
        );

        return calculate(
                dayPillar.stem(),
                birthTime
        );
    }

    /**
     * 일주 + LocalDateTime 형태 지원.
     */
    public Pillar calculate(
            Pillar dayPillar,
            LocalDateTime birthDateTime
    ) {
        Objects.requireNonNull(
                birthDateTime,
                "출생 날짜와 시간이 필요합니다."
        );

        return calculate(
                dayPillar,
                birthDateTime.toLocalTime()
        );
    }

    /**
     * 기존 호출부와의 호환성을 위한
     * 인자 순서 오버로드.
     */
    public Pillar calculate(
            LocalDateTime birthDateTime,
            Pillar dayPillar
    ) {
        return calculate(
                dayPillar,
                birthDateTime
        );
    }

    /**
     * ZoneId를 받는 형태.
     *
     * 중요:
     *
     * 다점 정책에서는 사용자의 출생시간을
     * 다른 시간대로 변환하지 않는다.
     *
     * ZoneId는 입력 유효성 및
     * 다른 Calculator와 호출 구조를 맞추기 위해 받는다.
     */
    public Pillar calculate(
            Pillar dayPillar,
            LocalDateTime birthDateTime,
            ZoneId zoneId
    ) {
        Objects.requireNonNull(
                zoneId,
                "시간대가 필요합니다."
        );

        return calculate(
                dayPillar,
                birthDateTime
        );
    }

    /**
     * 일간과 출생시간만 알고 있을 때
     * 시주를 계산한다.
     */
    public Pillar calculate(
            HeavenlyStem dayStem,
            LocalTime birthTime
    ) {
        Objects.requireNonNull(
                dayStem,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                birthTime,
                "출생시간이 필요합니다."
        );

        int hourBranchIndex =
                determineHourBranchIndex(
                        birthTime
                );

        int hourStemIndex =
                determineHourStemIndex(
                        dayStem,
                        hourBranchIndex
                );

        HeavenlyStem hourStem =
                HeavenlyStem.fromIndex(
                        hourStemIndex
                );

        EarthlyBranch hourBranch =
                EarthlyBranch.fromIndex(
                        hourBranchIndex
                );

        int cycleIndex =
                GanjiCycleTable.findIndex(
                        hourStem,
                        hourBranch
                );

        return GanjiCycleTable.get(
                cycleIndex
        );
    }

    /**
     * 출생시간 미상 처리를 위한 메서드.
     *
     * birthTimeUnknown == true이면
     * 가짜 시주를 만들지 않는다.
     */
    public Optional<Pillar> calculateOptional(
            Pillar dayPillar,
            LocalTime birthTime,
            boolean birthTimeUnknown
    ) {
        if (
                birthTimeUnknown
        ) {
            return Optional.empty();
        }

        if (
                birthTime == null
        ) {
            return Optional.empty();
        }

        Objects.requireNonNull(
                dayPillar,
                "일주가 필요합니다."
        );

        return Optional.of(
                calculate(
                        dayPillar,
                        birthTime
                )
        );
    }

    /**
     * 출생시간이 어떤 지지의 시각에 해당하는지 계산한다.
     *
     * 결과:
     *
     * 0  = 子
     * 1  = 丑
     * 2  = 寅
     * 3  = 卯
     * 4  = 辰
     * 5  = 巳
     * 6  = 午
     * 7  = 未
     * 8  = 申
     * 9  = 酉
     * 10 = 戌
     * 11 = 亥
     */
    public int determineHourBranchIndex(
            LocalTime birthTime
    ) {
        Objects.requireNonNull(
                birthTime,
                "출생시간이 필요합니다."
        );

        int hour =
                birthTime.getHour();

        /*
         * 공식:
         *
         * 23 -> (23 + 1) / 2 = 12 -> 0
         * 00 -> ( 0 + 1) / 2 = 0
         *
         * 01 -> 1
         * 02 -> 1
         *
         * 03 -> 2
         * 04 -> 2
         *
         * ...
         *
         * 21 -> 11
         * 22 -> 11
         */
        return Math.floorMod(
                (hour + 1) / 2,
                BRANCH_COUNT
        );
    }

    /**
     * 시지 자체를 반환한다.
     */
    public EarthlyBranch determineHourBranch(
            LocalTime birthTime
    ) {
        return EarthlyBranch.fromIndex(
                determineHourBranchIndex(
                        birthTime
                )
        );
    }

    /**
     * 일간과 시지 인덱스를 이용해
     * 시천간 인덱스를 결정한다.
     *
     * 오서둔(五鼠遁)
     *
     * 甲 / 己 -> 甲子
     * 乙 / 庚 -> 丙子
     * 丙 / 辛 -> 戊子
     * 丁 / 壬 -> 庚子
     * 戊 / 癸 -> 壬子
     */
    public int determineHourStemIndex(
            HeavenlyStem dayStem,
            int hourBranchIndex
    ) {
        Objects.requireNonNull(
                dayStem,
                "일간이 필요합니다."
        );

        validateHourBranchIndex(
                hourBranchIndex
        );

        int dayStemIndex =
                dayStem.ordinal();

        /*
         * 오서둔 子時 시작 천간.
         *
         * 甲 index 0 -> 甲 index 0
         * 乙 index 1 -> 丙 index 2
         * 丙 index 2 -> 戊 index 4
         * 丁 index 3 -> 庚 index 6
         * 戊 index 4 -> 壬 index 8
         *
         * 己 index 5 -> 甲 index 0
         * 庚 index 6 -> 丙 index 2
         * 辛 index 7 -> 戊 index 4
         * 壬 index 8 -> 庚 index 6
         * 癸 index 9 -> 壬 index 8
         */
        int ziHourStemIndex =
                Math.floorMod(
                        (
                                dayStemIndex
                                        % 5
                        )
                                * 2,
                        STEM_COUNT
                );

        /*
         * 子 -> 丑 -> 寅 -> ...
         *
         * 시지가 한 칸 이동할 때마다
         * 시천간도 한 칸 이동한다.
         */
        return Math.floorMod(
                ziHourStemIndex
                        + hourBranchIndex,
                STEM_COUNT
        );
    }

    /**
     * LocalTime까지 한 번에 받아
     * 시천간 인덱스를 계산한다.
     */
    public int determineHourStemIndex(
            HeavenlyStem dayStem,
            LocalTime birthTime
    ) {
        return determineHourStemIndex(
                dayStem,
                determineHourBranchIndex(
                        birthTime
                )
        );
    }

    /**
     * 시천간 자체를 반환한다.
     */
    public HeavenlyStem determineHourStem(
            HeavenlyStem dayStem,
            LocalTime birthTime
    ) {
        return HeavenlyStem.fromIndex(
                determineHourStemIndex(
                        dayStem,
                        birthTime
                )
        );
    }

    private void validateHourBranchIndex(
            int hourBranchIndex
    ) {
        if (
                hourBranchIndex < 0
                        || hourBranchIndex >= BRANCH_COUNT
        ) {
            throw new IllegalArgumentException(
                    "시지 인덱스는 0부터 11까지입니다: "
                            + hourBranchIndex
            );
        }
    }
}