package com.dajeom.engine;

import com.dajeom.calculator.DayPillarCalculator;
import com.dajeom.calculator.HourPillarCalculator;
import com.dajeom.calculator.MonthPillarCalculator;
import com.dajeom.calculator.YearPillarCalculator;
import com.dajeom.model.FourPillars;
import com.dajeom.model.NormalizedBirthData;
import com.dajeom.model.Pillar;
import org.springframework.stereotype.Component;

/**
 * 년주, 월주, 일주, 시주 계산기를 통합 실행한다.
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

    public FourPillars calculate(
            NormalizedBirthData birthData
    ) {
        Pillar yearPillar =
                yearPillarCalculator.calculate(
                        birthData
                );

        Pillar monthPillar =
                monthPillarCalculator.calculate(
                        birthData
                );

        Pillar dayPillar =
                dayPillarCalculator.calculate(
                        birthData
                );

        Pillar hourPillar =
                hourPillarCalculator.calculate(
                        birthData,
                        dayPillar
                );

        return new FourPillars(
                yearPillar,
                monthPillar,
                dayPillar,
                hourPillar
        );
    }
}