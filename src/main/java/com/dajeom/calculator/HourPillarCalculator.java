package com.dajeom.calculator;

import com.dajeom.model.NormalizedBirthData;
import com.dajeom.model.Pillar;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

/**
 * 출생기록 시간과 일간을 기준으로 시주를 계산한다.
 */
@Component
public class HourPillarCalculator {

    public Pillar calculate(
            NormalizedBirthData birthData,
            Pillar dayPillar
    ) {
        int branchIndex =
                calculateHourBranchIndex(
                        birthData.birthTime()
                );

        EarthlyBranch branch =
                EarthlyBranch.fromIndex(
                        branchIndex
                );

        /*
         * 일간별 자시 천간:
         *
         * 갑·기일: 갑자
         * 을·경일: 병자
         * 병·신일: 무자
         * 정·임일: 경자
         * 무·계일: 임자
         */
        int ratHourStemIndex =
                Math.floorMod(
                        dayPillar
                                .stem()
                                .getIndex(),
                        5
                ) * 2;

        HeavenlyStem stem =
                HeavenlyStem.fromIndex(
                        ratHourStemIndex
                                + branchIndex
                );

        return new Pillar(
                stem,
                branch
        );
    }

    /**
     * 출생기록 시간을 지지 인덱스로 변환한다.
     *
     * 자시: 23:00~00:59
     * 축시: 01:00~02:59
     * 인시: 03:00~04:59
     * 묘시: 05:00~06:59
     * 진시: 07:00~08:59
     */
    private int calculateHourBranchIndex(
            LocalTime birthTime
    ) {
        int hour =
                birthTime.getHour();

        return Math.floorMod(
                (hour + 1) / 2,
                12
        );
    }
}