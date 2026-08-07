package com.dajeom.calculator;

import com.dajeom.constant.GanjiCycleTable;
import com.dajeom.model.NormalizedBirthData;
import com.dajeom.model.Pillar;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 입춘을 기준으로 사주의 년주를 계산한다.
 *
 * 일반적인 달력 연도 1월 1일이 아니라
 * 명리학의 년주 변경 기준인 입춘을 사용한다.
 *
 * 기준:
 * 1984년 = 갑자년
 *
 * 육십갑자는 60년 단위로 순환한다.
 */
@Component
public class YearPillarCalculator {

    private static final int GAPJA_BASE_YEAR =
            1984;

    private final SolarTermCalculator
            solarTermCalculator;

    public YearPillarCalculator(
            SolarTermCalculator solarTermCalculator
    ) {
        this.solarTermCalculator =
                solarTermCalculator;
    }

    /**
     * 출생정보를 기준으로 년주를 계산한다.
     *
     * @param birthData 정규화된 출생정보
     * @return 년주
     */
    public Pillar calculate(
            NormalizedBirthData birthData
    ) {
        Objects.requireNonNull(
                birthData,
                "출생정보는 필수입니다."
        );

        int sajuYear =
                solarTermCalculator.determineSajuYear(
                        birthData.birthDateTime(),
                        birthData.zoneId()
                );

        int cycleIndex =
                Math.floorMod(
                        sajuYear
                                - GAPJA_BASE_YEAR,
                        60
                );

        return GanjiCycleTable.get(
                cycleIndex
        );
    }
}