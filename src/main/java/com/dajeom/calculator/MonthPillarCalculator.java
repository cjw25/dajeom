package com.dajeom.calculator;

import com.dajeom.model.NormalizedBirthData;
import com.dajeom.model.Pillar;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 월주 계산기.
 *
 * 사주 월은 음력 월이 아니라
 * 절입을 기준으로 결정한다.
 *
 * 인월을 monthIndex 0으로 사용한다.
 */
@Component
public class MonthPillarCalculator {

    private final SolarTermCalculator
            solarTermCalculator;

    public MonthPillarCalculator(
            SolarTermCalculator solarTermCalculator
    ) {
        this.solarTermCalculator =
                solarTermCalculator;
    }

    /**
     * 년주를 먼저 계산한 뒤
     * 그 년주의 천간을 기준으로 월간을 계산한다.
     */
    public Pillar calculate(
            NormalizedBirthData birthData,
            Pillar yearPillar
    ) {

        Objects.requireNonNull(
                birthData,
                "출생정보는 필수입니다."
        );

        Objects.requireNonNull(
                yearPillar,
                "년주는 필수입니다."
        );

        int monthIndex =
                solarTermCalculator
                        .determineMonthIndex(
                                birthData
                        );

        /*
         * 인(寅)을 0번째 사주 월로 사용한다.
         */
        EarthlyBranch monthBranch =
                EarthlyBranch.fromIndex(
                        EarthlyBranch.YIN
                                .getIndex()
                                + monthIndex
                );

        /*
         * 년간별 인월 월간.
         *
         * 갑/기년 → 병인
         * 을/경년 → 무인
         * 병/신년 → 경인
         * 정/임년 → 임인
         * 무/계년 → 갑인
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
                        yearStemGroup
                                * 2
                                + 2,
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