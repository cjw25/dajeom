package com.dajeom.calculator;

import com.dajeom.constant.GanjiCycleTable;
import com.dajeom.model.NormalizedBirthData;
import com.dajeom.model.Pillar;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 출생일의 율리우스 적일 번호를 기준으로
 * 일주를 계산한다.
 *
 * 다점은 야자시를 적용하지 않고
 * 자정 00시에 날짜가 변경되는 기준을 사용한다.
 */
@Component
public class DayPillarCalculator {

    /**
     * 율리우스 적일을 육십갑자 인덱스로
     * 변환하기 위한 기준 보정값이다.
     */
    private static final int DAY_CYCLE_OFFSET = 49;

    public Pillar calculate(
            NormalizedBirthData birthData
    ) {
        LocalDate birthDate =
                birthData.birthDate();

        long julianDayNumber =
                calculateJulianDayNumber(
                        birthDate
                );

        int cycleIndex =
                Math.floorMod(
                        julianDayNumber
                                + DAY_CYCLE_OFFSET,
                        60
                );

        return GanjiCycleTable.get(
                cycleIndex
        );
    }

    /**
     * 그레고리력 날짜를 율리우스 적일 번호로 변환한다.
     */
    private long calculateJulianDayNumber(
            LocalDate date
    ) {
        int year =
                date.getYear();

        int month =
                date.getMonthValue();

        int day =
                date.getDayOfMonth();

        int adjustment =
                (14 - month) / 12;

        int adjustedYear =
                year + 4800 - adjustment;

        int adjustedMonth =
                month + 12 * adjustment - 3;

        return day
                + (153L * adjustedMonth + 2) / 5
                + 365L * adjustedYear
                + adjustedYear / 4
                - adjustedYear / 100
                + adjustedYear / 400
                - 32045;
    }
}