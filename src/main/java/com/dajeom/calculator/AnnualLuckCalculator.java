package com.dajeom.calculator;

import com.dajeom.constant.GanjiCycleTable;
import com.dajeom.constant.HiddenStemTable;
import com.dajeom.model.AnnualLuck;
import com.dajeom.model.FourPillars;
import com.dajeom.model.Pillar;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HiddenStemRole;
import com.dajeom.type.TenGod;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * 특정 연도의 세운을 계산한다.
 */
@Component
public class AnnualLuckCalculator {

    private static final int GAPJA_BASE_YEAR =
            4;

    private final SolarTermCalculator
            solarTermCalculator;

    private final TenGodCalculator
            tenGodCalculator;

    public AnnualLuckCalculator(
            SolarTermCalculator solarTermCalculator,
            TenGodCalculator tenGodCalculator
    ) {
        this.solarTermCalculator =
                solarTermCalculator;

        this.tenGodCalculator =
                tenGodCalculator;
    }

    public AnnualLuck calculate(
            FourPillars fourPillars,
            int targetYear,
            ZoneId zoneId
    ) {
        int cycleIndex =
                Math.floorMod(
                        targetYear
                                - GAPJA_BASE_YEAR,
                        60
                );

        Pillar annualPillar =
                GanjiCycleTable.get(
                        cycleIndex
                );

        ZonedDateTime startDateTime =
                solarTermCalculator.calculateIpchun(
                        targetYear,
                        zoneId
                );

        ZonedDateTime endDateTime =
                solarTermCalculator.calculateIpchun(
                        targetYear + 1,
                        zoneId
                ).minusNanos(1);

        HeavenlyStem dayMaster =
                fourPillars
                        .dayPillar()
                        .stem();

        TenGod stemTenGod =
                tenGodCalculator.calculateTenGod(
                        dayMaster,
                        annualPillar.stem()
                );

        HeavenlyStem branchMainStem =
                HiddenStemTable
                        .get(annualPillar.branch())
                        .stream()
                        .filter(definition ->
                                definition.role()
                                        == HiddenStemRole.MAIN
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "세운 지지 본기를 찾을 수 없습니다."
                                )
                        )
                        .stem();

        TenGod branchMainTenGod =
                tenGodCalculator.calculateTenGod(
                        dayMaster,
                        branchMainStem
                );

        return new AnnualLuck(
                targetYear,
                annualPillar,
                startDateTime,
                endDateTime,
                stemTenGod,
                branchMainTenGod
        );
    }
}