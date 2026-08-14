package com.dajeom.calculator;

import com.dajeom.model.SexagenaryPillar;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 육십갑자 연도 계산기.
 *
 * =================================================
 *
 * 기준:
 *
 * 1984 = 甲子
 * cycleIndex = 0
 *
 * =================================================
 *
 * 예:
 *
 * 1984 -> 甲子
 * 1985 -> 乙丑
 *
 * 2024 -> 甲辰
 * 2025 -> 乙巳
 * 2026 -> 丙午
 *
 * =================================================
 *
 * 중요:
 *
 * 이 Calculator가 받는 year는
 *
 * "달력 날짜에서 무조건 가져온 연도"
 *
 * 가 아니라 이미 입춘 경계가 반영된
 *
 * 명리 연도(Saju Year)
 *
 * 여야 한다.
 *
 * 따라서:
 *
 * 2026-01-15
 *
 * 같은 날짜를 직접 이 Calculator에 넣어
 * 2026이라고 판단해서는 안 된다.
 *
 * 먼저:
 *
 * AnnualLuckBoundaryCalculator
 *
 * 또는
 *
 * SolarTermCalculator.determineSajuYear()
 *
 * 를 통해 명리 연도를 확정해야 한다.
 *
 * =================================================
 *
 * 육십갑자 천간/지지 생성은
 * 13-3에서 만든 SexagenaryCycleCalculator를
 * canonical source로 재사용한다.
 */
@Component
public class SexagenaryYearCalculator {

    /**
     * 1984년은 甲子년.
     *
     * SexagenaryCycleCalculator에서:
     *
     * 甲子 = cycleIndex 0
     *
     * 을 사용하므로 기준을 맞춘다.
     */
    private static final int
            JIA_ZI_REFERENCE_YEAR = 1984;

    private static final int
            SEXAGENARY_CYCLE_SIZE = 60;

    private final SexagenaryCycleCalculator
            sexagenaryCycleCalculator;

    public SexagenaryYearCalculator(
            SexagenaryCycleCalculator
                    sexagenaryCycleCalculator
    ) {

        this.sexagenaryCycleCalculator =
                Objects.requireNonNull(
                        sexagenaryCycleCalculator,
                        "SexagenaryCycleCalculator가 필요합니다."
                );
    }

    /**
     * =================================================
     * 공식 API
     * =================================================
     *
     * 명리 연도 -> 육십갑자.
     */
    public SexagenaryPillar calculate(
            int sajuYear
    ) {

        int cycleIndex =
                cycleIndexOf(
                        sajuYear
                );

        return sexagenaryCycleCalculator
                .pillarAt(
                        cycleIndex
                );
    }

    /**
     * 명리 연도의 육십갑자 cycleIndex.
     *
     * 0 = 甲子
     * ...
     * 59 = 癸亥
     */
    public int cycleIndexOf(
            int sajuYear
    ) {

        validateYear(
                sajuYear
        );

        /*
         * int overflow를 피하기 위해
         * long으로 차이를 계산한다.
         */

        long offset =
                (long) sajuYear
                        - JIA_ZI_REFERENCE_YEAR;

        return (int) Math.floorMod(
                offset,
                (long) SEXAGENARY_CYCLE_SIZE
        );
    }

    public boolean is(

            int sajuYear,

            SexagenaryPillar expected

    ) {

        if (
                expected == null
        ) {

            return false;
        }

        return calculate(
                sajuYear
        )
                .equals(
                        expected
                );
    }

    private void validateYear(
            int sajuYear
    ) {

        if (
                sajuYear < 1
        ) {

            throw new IllegalArgumentException(
                    "육십갑자 연도는 1 이상이어야 합니다."
            );
        }
    }
}