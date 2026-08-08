package com.dajeom.calculator;

import com.dajeom.constant.GanjiCycleTable;
import com.dajeom.constant.SajuConstants;
import com.dajeom.model.NormalizedBirthData;
import com.dajeom.model.Pillar;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Objects;

/**
 * 일주 계산기.
 *
 * Gregorian Calendar 날짜를 Julian Day Number로
 * 변환한 뒤 육십갑자 순환 위치를 계산한다.
 *
 * 다점의 일주 변경 기준:
 * 00:00
 *
 * 야자시는 사용하지 않는다.
 */
@Component
public class DayPillarCalculator {

    /**
     * 1970-01-01의 Julian Day Number.
     */
    private static final long
            UNIX_EPOCH_JDN =
            2440588L;

    /**
     * JDN에서 갑자 index를 얻기 위한
     * 육십갑자 보정값.
     */
    private static final long
            SEXAGENARY_DAY_OFFSET =
            49L;

    public Pillar calculate(
            NormalizedBirthData birthData
    ) {

        Objects.requireNonNull(
                birthData,
                "출생정보는 필수입니다."
        );

        return calculate(
                birthData.solarBirthDate()
        );
    }

    public Pillar calculate(
            LocalDate solarDate
    ) {

        Objects.requireNonNull(
                solarDate,
                "양력 날짜는 필수입니다."
        );

        long julianDayNumber =
                solarDate.toEpochDay()
                        + UNIX_EPOCH_JDN;

        int cycleIndex =
                (int)
                        Math.floorMod(
                                julianDayNumber
                                        + SEXAGENARY_DAY_OFFSET,
                                (long)
                                        SajuConstants
                                                .GANJI_CYCLE_SIZE
                        );

        return GanjiCycleTable.get(
                cycleIndex
        );
    }
}