package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;

import java.util.Objects;

/**
 * 육십갑자 한 기둥.
 *
 * =================================================
 *
 * cycleIndex:
 *
 * 0  = 甲子
 * 1  = 乙丑
 * 2  = 丙寅
 * ...
 * 59 = 癸亥
 *
 * =================================================
 *
 * 대운뿐 아니라 이후:
 *
 * 14차 세운
 * 15차 월운
 *
 * 에서도 재사용할 수 있도록
 * 대운 전용 모델로 만들지 않는다.
 */
public record SexagenaryPillar(

        int cycleIndex,

        HeavenlyStem stem,

        EarthlyBranch branch

) {

    public SexagenaryPillar {

        if (
                cycleIndex < 0
                        || cycleIndex >= 60
        ) {

            throw new IllegalArgumentException(
                    "육십갑자 cycleIndex는 0~59 범위여야 합니다."
            );
        }

        Objects.requireNonNull(
                stem,
                "육십갑자에는 천간이 필요합니다."
        );

        Objects.requireNonNull(
                branch,
                "육십갑자에는 지지가 필요합니다."
        );
    }

    public boolean samePillar(
            HeavenlyStem expectedStem,
            EarthlyBranch expectedBranch
    ) {

        return stem == expectedStem
                && branch == expectedBranch;
    }
}