package com.dajeom.calculator;

import com.dajeom.constant.GanjiCycleTable;
import com.dajeom.model.AnnualLuckPeriod;
import com.dajeom.model.Pillar;
import com.dajeom.model.SolarTermPoint;
import com.dajeom.rule.AnnualLuckRule;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.Objects;

/**
 * 특정 연도의 세운을 계산한다.
 *
 * 세운 간지:
 * 육십갑자 기준 계산
 *
 * 세운 기간:
 * 해당 연도 입춘
 * ~
 * 다음 연도 입춘
 */
@Component
public class AnnualLuckCalculator {

    private final SolarTermCalculator
            solarTermCalculator;

    public AnnualLuckCalculator(
            SolarTermCalculator solarTermCalculator
    ) {

        this.solarTermCalculator =
                solarTermCalculator;
    }

    public AnnualLuckPeriod calculate(
            int targetYear,
            ZoneId zoneId
    ) {

        Objects.requireNonNull(
                zoneId,
                "시간대는 필수입니다."
        );

        /*
         * 1.
         * 해당 연도의 육십갑자 index 계산.
         */
        int cycleIndex =
                AnnualLuckRule
                        .calculateCycleIndex(
                                targetYear
                        );

        /*
         * 2.
         * 실제 세운 간지.
         */
        Pillar annualPillar =
                GanjiCycleTable.get(
                        cycleIndex
                );

        /*
         * 3.
         * 해당 연도 입춘.
         */
        SolarTermPoint startIpchun =
                solarTermCalculator
                        .calculateIpchun(
                                targetYear,
                                zoneId
                        );

        /*
         * 4.
         * 다음 연도 입춘.
         */
        SolarTermPoint endIpchun =
                solarTermCalculator
                        .calculateIpchun(
                                targetYear + 1,
                                zoneId
                        );

        return new AnnualLuckPeriod(
                targetYear,
                annualPillar,
                startIpchun.dateTime(),
                endIpchun.dateTime()
        );
    }
}