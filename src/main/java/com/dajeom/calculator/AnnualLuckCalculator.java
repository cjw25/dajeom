package com.dajeom.calculator;

import com.dajeom.constant.GanjiCycleTable;
import com.dajeom.model.AnnualLuckBoundaryAnalysis;
import com.dajeom.model.AnnualLuckPeriod;
import com.dajeom.model.Pillar;
import com.dajeom.model.SexagenaryPillar;
import com.dajeom.rule.AnnualLuckRule;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 특정 연도의 세운을 계산한다.
 *
 * =================================================
 *
 * 세운 간지:
 *
 * 육십갑자 기준 계산.
 *
 * =================================================
 *
 * 세운 기간:
 *
 * 해당 연도 입춘
 * ~
 * 다음 연도 입춘
 *
 * 즉:
 *
 * [targetYear 입춘,
 *  targetYear + 1 입춘)
 *
 * 구조다.
 *
 * =================================================
 *
 * 정밀화 14차 구조:
 *
 * 14-1 AnnualLuckBoundaryAnalysis
 * 14-2 SexagenaryPillar
 *
 * 를 생성한 뒤
 *
 * AnnualLuckPeriod로 결합한다.
 */
@Component
public class AnnualLuckCalculator {

    private final SolarTermCalculator
            solarTermCalculator;

    public AnnualLuckCalculator(
            SolarTermCalculator solarTermCalculator
    ) {

        this.solarTermCalculator =
                Objects.requireNonNull(
                        solarTermCalculator,
                        "SolarTermCalculator가 필요합니다."
                );
    }

    public AnnualLuckPeriod calculate(
            int targetYear,
            ZoneId zoneId
    ) {

        Objects.requireNonNull(
                zoneId,
                "시간대는 필수입니다."
        );

        if (
                targetYear < 1
        ) {

            throw new IllegalArgumentException(
                    "세운 대상 연도는 1 이상이어야 합니다."
            );
        }

        /*
         * =================================================
         * 1.
         * 해당 연도의 육십갑자 index
         * =================================================
         */
        int cycleIndex =
                AnnualLuckRule
                        .calculateCycleIndex(
                                targetYear
                        );

        /*
         * SexagenaryPillar는 반드시
         * 0 ~ 59 범위를 요구한다.
         */
        int normalizedCycleIndex =
                Math.floorMod(
                        cycleIndex,
                        60
                );

        /*
         * =================================================
         * 2.
         * 기존 GanjiCycleTable에서
         * 실제 천간 / 지지 계산
         * =================================================
         */
        Pillar calculatedPillar =
                GanjiCycleTable.get(
                        normalizedCycleIndex
                );

        /*
         * 정밀화 14차에서는
         * 단순 Pillar가 아니라
         * cycleIndex까지 보존하는
         * SexagenaryPillar를 사용한다.
         */
        SexagenaryPillar annualPillar =
                new SexagenaryPillar(
                        normalizedCycleIndex,
                        calculatedPillar.stem(),
                        calculatedPillar.branch()
                );

        /*
         * =================================================
         * 3.
         * 해당 연도 입춘
         * =================================================
         *
         * 현재 SolarTermCalculator.calculateIpchun()
         * 반환형은 ZonedDateTime이다.
         *
         * 예전 SolarTermPoint로 받지 않는다.
         */
        ZonedDateTime startIpchun =
                solarTermCalculator
                        .calculateIpchun(
                                targetYear,
                                zoneId
                        );

        /*
         * =================================================
         * 4.
         * 다음 연도 입춘
         * =================================================
         */
        ZonedDateTime endIpchun =
                solarTermCalculator
                        .calculateIpchun(
                                targetYear + 1,
                                zoneId
                        );

        /*
         * =================================================
         * 5.
         * 14-1 세운 경계
         * =================================================
         */
        AnnualLuckBoundaryAnalysis boundaryAnalysis =
                new AnnualLuckBoundaryAnalysis(
                        targetYear,
                        zoneId,
                        startIpchun,
                        endIpchun
                );

        /*
         * =================================================
         * 6.
         * 14-2 세운 Period
         * =================================================
         */
        return new AnnualLuckPeriod(
                boundaryAnalysis,
                annualPillar
        );
    }
}