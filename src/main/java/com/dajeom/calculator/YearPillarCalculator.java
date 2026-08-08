package com.dajeom.calculator;

import com.dajeom.constant.GanjiCycleTable;
import com.dajeom.constant.SajuConstants;
import com.dajeom.model.NormalizedBirthData;
import com.dajeom.model.Pillar;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 년주 계산기.
 *
 * 양력 1월 1일이 아닌
 * 실제 입춘 시각을 사주년의 경계로 사용한다.
 */
@Component
public class YearPillarCalculator {

    private final SolarTermCalculator
            solarTermCalculator;

    public YearPillarCalculator(
            SolarTermCalculator solarTermCalculator
    ) {
        this.solarTermCalculator =
                solarTermCalculator;
    }

    public Pillar calculate(
            NormalizedBirthData birthData
    ) {

        Objects.requireNonNull(
                birthData,
                "출생정보는 필수입니다."
        );

        int sajuYear =
                solarTermCalculator
                        .determineSajuYear(
                                birthData
                        );

        int cycleIndex =
                Math.floorMod(
                        sajuYear
                                - SajuConstants
                                .GAPJA_BASE_YEAR,
                        SajuConstants
                                .GANJI_CYCLE_SIZE
                );

        return GanjiCycleTable.get(
                cycleIndex
        );
    }
}