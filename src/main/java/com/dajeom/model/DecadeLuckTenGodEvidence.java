package com.dajeom.model;

import com.dajeom.type.DecadeLuckTenGodSourceType;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.StructureTenGodType;

import java.util.Objects;

/**
 * 정밀화 13차-4.
 *
 * 특정 대운 구간에서 발생하는
 * 하나의 십성 Evidence.
 *
 * =================================================
 *
 * 예:
 *
 * 1대운 己未
 *
 * visible:
 *
 * 己
 * -> 식신
 *
 * hidden:
 *
 * 未 지장간 각각
 * -> 일간 기준 십성
 *
 * =================================================
 *
 * 원국 십성과 혼합하지 않는다.
 */
public record DecadeLuckTenGodEvidence(

        HeavenlyStem dayMaster,

        DecadeLuckPeriod period,

        DecadeLuckTenGodSourceType sourceType,

        HeavenlyStem sourceStem,

        StructureTenGodType tenGodType

) {

    public DecadeLuckTenGodEvidence {

        Objects.requireNonNull(
                dayMaster,
                "대운 십성 Evidence에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                period,
                "대운 십성 Evidence에는 대운 Period가 필요합니다."
        );

        Objects.requireNonNull(
                sourceType,
                "대운 십성 Source Type이 필요합니다."
        );

        Objects.requireNonNull(
                sourceStem,
                "대운 십성 Source 천간이 필요합니다."
        );

        Objects.requireNonNull(
                tenGodType,
                "대운 십성 종류가 필요합니다."
        );

        /*
         * =================================================
         * 천간 Source 정합성
         * =================================================
         *
         * VISIBLE_STEM은 반드시
         * 현재 대운의 천간 자체여야 한다.
         */

        if (
                sourceType.visibleStem()
                        && sourceStem
                        != period
                        .pillar()
                        .stem()
        ) {

            throw new IllegalArgumentException(
                    "대운 천간 십성 Source가 실제 대운 천간과 일치하지 않습니다."
            );
        }
    }

    public int sequenceNumber() {

        return period
                .sequenceNumber();
    }

    public SexagenaryPillar luckPillar() {

        return period
                .pillar();
    }

    public HeavenlyStem luckStem() {

        return luckPillar()
                .stem();
    }

    public EarthlyBranch luckBranch() {

        return luckPillar()
                .branch();
    }

    public boolean visible() {

        return sourceType
                .visibleStem();
    }

    public boolean hidden() {

        return sourceType
                .hiddenStem();
    }

    public boolean mainHiddenStem() {

        return sourceType
                .mainHiddenStem();
    }

    public boolean middleHiddenStem() {

        return sourceType
                .middleHiddenStem();
    }

    public boolean residualHiddenStem() {

        return sourceType
                .residualHiddenStem();
    }

    public boolean is(
            StructureTenGodType expected
    ) {

        return expected != null
                && tenGodType == expected;
    }
}