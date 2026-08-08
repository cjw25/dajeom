package com.dajeom.engine;

import com.dajeom.calculator.DayPillarCalculator;
import com.dajeom.calculator.HourPillarCalculator;
import com.dajeom.calculator.MonthPillarCalculator;
import com.dajeom.calculator.YearPillarCalculator;
import com.dajeom.model.FourPillars;
import com.dajeom.model.NormalizedBirthData;
import com.dajeom.model.Pillar;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 사주 네 기둥을 조립하는 계산 엔진.
 *
 * 각 기둥의 실제 계산은 Calculator가 담당하고,
 * 이 Engine은 계산 순서와 조합만 담당한다.
 *
 * 출생시간을 모르는 경우:
 *
 * 년주 = 계산
 * 월주 = 계산
 * 일주 = 계산
 * 시주 = null
 */
@Component
public class FourPillarsCalculationEngine {

    private final YearPillarCalculator
            yearPillarCalculator;

    private final MonthPillarCalculator
            monthPillarCalculator;

    private final DayPillarCalculator
            dayPillarCalculator;

    private final HourPillarCalculator
            hourPillarCalculator;

    public FourPillarsCalculationEngine(
            YearPillarCalculator yearPillarCalculator,
            MonthPillarCalculator monthPillarCalculator,
            DayPillarCalculator dayPillarCalculator,
            HourPillarCalculator hourPillarCalculator
    ) {

        this.yearPillarCalculator =
                yearPillarCalculator;

        this.monthPillarCalculator =
                monthPillarCalculator;

        this.dayPillarCalculator =
                dayPillarCalculator;

        this.hourPillarCalculator =
                hourPillarCalculator;
    }

    /**
     * 정규화된 출생정보를 이용해
     * 사주 원국을 계산한다.
     */
    public FourPillars calculate(
            NormalizedBirthData birthData
    ) {

        Objects.requireNonNull(
                birthData,
                "출생정보는 필수입니다."
        );

        Pillar yearPillar =
                yearPillarCalculator.calculate(
                        birthData
                );

        Pillar monthPillar =
                monthPillarCalculator.calculate(
                        birthData,
                        yearPillar
                );

        Pillar dayPillar =
                dayPillarCalculator.calculate(
                        birthData
                );

        Pillar hourPillar =
                hourPillarCalculator
                        .calculate(
                                birthData,
                                dayPillar
                        )
                        .orElse(null);

        return new FourPillars(
                yearPillar,
                monthPillar,
                dayPillar,
                hourPillar
        );
    }
}