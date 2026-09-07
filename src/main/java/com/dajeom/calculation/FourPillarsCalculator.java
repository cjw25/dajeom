package com.dajeom.calculation;

import com.dajeom.domain.BirthDateTime;
import com.dajeom.domain.FourPillars;
import com.dajeom.domain.Pillar;

import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class FourPillarsCalculator {

    private final YearPillarCalculator yearPillarCalculator;
    private final MonthPillarCalculator monthPillarCalculator;
    private final DayPillarCalculator dayPillarCalculator;
    private final HourPillarCalculator hourPillarCalculator;

    public FourPillarsCalculator(
            YearPillarCalculator yearPillarCalculator,
            MonthPillarCalculator monthPillarCalculator,
            DayPillarCalculator dayPillarCalculator,
            HourPillarCalculator hourPillarCalculator
    ) {

        this.yearPillarCalculator =
                Objects.requireNonNull(
                        yearPillarCalculator,
                        "YearPillarCalculator가 필요합니다."
                );

        this.monthPillarCalculator =
                Objects.requireNonNull(
                        monthPillarCalculator,
                        "MonthPillarCalculator가 필요합니다."
                );

        this.dayPillarCalculator =
                Objects.requireNonNull(
                        dayPillarCalculator,
                        "DayPillarCalculator가 필요합니다."
                );

        this.hourPillarCalculator =
                Objects.requireNonNull(
                        hourPillarCalculator,
                        "HourPillarCalculator가 필요합니다."
                );
    }

    public FourPillars calculate(
            BirthDateTime birth
    ) {

        Objects.requireNonNull(
                birth,
                "출생정보가 필요합니다."
        );

        if (!birth.isSolar()) {

            throw new IllegalArgumentException(
                    "FourPillarsCalculator에는 양력으로 정규화된 출생정보만 입력할 수 있습니다."
            );
        }

        /*
         * 1. 년주
         */
        Pillar year =
                yearPillarCalculator.calculate(
                        birth
                );

        /*
         * 2. 월주
         *
         * 년간을 기준으로 오호둔 계산.
         */
        Pillar month =
                monthPillarCalculator.calculate(
                        birth,
                        year.stem()
                );

        /*
         * 3. 일주
         *
         * 현재는 양력 출생일 기준.
         */
        Pillar day =
                dayPillarCalculator.calculate(
                        birth
                );

        /*
         * 4. 시주
         *
         * 출생시간이 있을 때만 계산.
         *
         * HourPillarCalculator 내부에서
         * -30분 경도보정 + 한국 DST 보정을 적용한다.
         */
        Pillar hour = null;

        if (birth.hasKnownTime()) {

            hour =
                    hourPillarCalculator.calculate(
                            birth,
                            day.stem()
                    );
        }

        return new FourPillars(
                year,
                month,
                day,
                hour
        );
    }
}