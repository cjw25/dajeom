package com.dajeom.calculator;

import com.dajeom.model.NormalizedBirthData;
import com.dajeom.model.Pillar;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import org.springframework.stereotype.Component;

/**
 * 절입과 년간을 기준으로 월주를 계산한다.
 */
@Component
public class MonthPillarCalculator {

    private final SolarTermCalculator
            solarTermCalculator;

    private final YearPillarCalculator
            yearPillarCalculator;

    public MonthPillarCalculator(
            SolarTermCalculator solarTermCalculator,
            YearPillarCalculator yearPillarCalculator
    ) {
        this.solarTermCalculator =
                solarTermCalculator;

        this.yearPillarCalculator =
                yearPillarCalculator;
    }

    public Pillar calculate(
            NormalizedBirthData birthData
    ) {
        Pillar yearPillar =
                yearPillarCalculator.calculate(
                        birthData
                );

        int monthIndex =
                solarTermCalculator.determineMonthIndex(
                        birthData.birthDateTime(),
                        birthData.zoneId()
                );

        /*
         * 인월은 지지 인덱스 2에서 시작한다.
         */
        EarthlyBranch monthBranch =
                EarthlyBranch.fromIndex(
                        EarthlyBranch.IN.getIndex()
                                + monthIndex
                );

        /*
         * 년간별 인월 월간:
         *
         * 갑·기년: 병인
         * 을·경년: 무인
         * 병·신년: 경인
         * 정·임년: 임인
         * 무·계년: 갑인
         */
        int yearStemGroup =
                Math.floorMod(
                        yearPillar
                                .stem()
                                .getIndex(),
                        5
                );

        int tigerMonthStemIndex =
                Math.floorMod(
                        yearStemGroup * 2 + 2,
                        10
                );

        HeavenlyStem monthStem =
                HeavenlyStem.fromIndex(
                        tigerMonthStemIndex
                                + monthIndex
                );

        return new Pillar(
                monthStem,
                monthBranch
        );
    }
}