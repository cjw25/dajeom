package com.dajeom.calculation;

import com.dajeom.domain.Ganji;
import com.dajeom.domain.SewoonPeriod;
import com.dajeom.domain.SexagenaryCycle;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 세운 계산기.
 *
 * 세운 규칙:
 *
 * 해당 명리연도 입춘
 * ~
 * 다음 명리연도 입춘 직전
 *
 * 세운 간지는
 * 기존 60갑자 SexagenaryCycle을 사용한다.
 */
@Component
public class SewoonCalculator {

    /**
     * 1984년 = 甲子년.
     *
     * 기존 SexagenaryCycle:
     * index 0 = 甲子
     *
     * 따라서 연도 간지는
     *
     * floorMod(year - 1984, 60)
     *
     * 으로 계산 가능하다.
     */
    private static final int JIA_ZI_REFERENCE_YEAR = 1984;

    private static final int SEXAGENARY_CYCLE_SIZE = 60;

    /**
     * 특정 명리연도의 세운 기간 생성.
     *
     * lichunStart와 nextLichun은
     * SolarTermRepository가 계산한 값을 전달한다.
     *
     * 여기서는 절기를 다시 계산하지 않는다.
     */
    public SewoonPeriod calculate(

            int sajuYear,

            LocalDateTime lichunStart,

            LocalDateTime nextLichun

    ) {

        Objects.requireNonNull(
                lichunStart,
                "해당 연도 입춘시각은 필수입니다."
        );

        Objects.requireNonNull(
                nextLichun,
                "다음 연도 입춘시각은 필수입니다."
        );

        if (
                !nextLichun.isAfter(
                        lichunStart
                )
        ) {

            throw new IllegalArgumentException(
                    "다음 연도 입춘은 현재 연도 입춘보다 뒤여야 합니다."
            );
        }

        int cycleIndex =
                Math.floorMod(
                        sajuYear
                                - JIA_ZI_REFERENCE_YEAR,
                        SEXAGENARY_CYCLE_SIZE
                );

        Ganji ganji =
                SexagenaryCycle.ofIndex(
                        cycleIndex
                );

        return new SewoonPeriod(
                sajuYear,
                ganji,
                lichunStart,
                nextLichun
        );
    }

    /**
     * 기준시각이 Gregorian year의 입춘 전/후인지
     * 판정하여 실제 세운연도를 결정한다.
     *
     * 예:
     *
     * 2026-01-20
     * <
     * 2026 입춘
     *
     * → 2025 세운
     */
    public int resolveSajuYear(

            LocalDateTime targetDateTime,

            LocalDateTime lichunOfGregorianYear

    ) {

        Objects.requireNonNull(
                targetDateTime,
                "세운 기준시각은 필수입니다."
        );

        Objects.requireNonNull(
                lichunOfGregorianYear,
                "해당 Gregorian 연도의 입춘시각은 필수입니다."
        );

        int gregorianYear =
                targetDateTime.getYear();

        if (
                targetDateTime.isBefore(
                        lichunOfGregorianYear
                )
        ) {

            return gregorianYear - 1;
        }

        return gregorianYear;
    }

    /**
     * 특정 시점이 만들어진 세운 안에
     * 포함되는지 확인한다.
     */
    public boolean isActiveAt(

            SewoonPeriod period,

            LocalDateTime targetDateTime

    ) {

        Objects.requireNonNull(
                period,
                "세운 기간은 필수입니다."
        );

        return period.contains(
                targetDateTime
        );
    }
}