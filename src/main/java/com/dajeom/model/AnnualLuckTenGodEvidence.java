package com.dajeom.model;

import com.dajeom.type.AnnualLuckTenGodSourceType;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.StructureTenGodType;

import java.util.Objects;

/**
 * 정밀화 14차-3.
 *
 * 특정 세운에서 발생하는
 * 하나의 십성 Evidence.
 *
 * =================================================
 *
 * 원국 십성과 구분한다.
 *
 * 원국:
 *
 * Natal Evidence
 *
 * 세운:
 *
 * Annual Incoming Evidence
 *
 * =================================================
 *
 * 세운에서 들어오는 글자가
 * 일간과 같은 천간이라 하더라도
 * DAY MASTER Anchor가 아니다.
 *
 * 운에서 유입된 외부 십성이다.
 */
public record AnnualLuckTenGodEvidence(

        HeavenlyStem dayMaster,

        AnnualLuckPeriod period,

        AnnualLuckTenGodSourceType sourceType,

        HeavenlyStem sourceStem,

        StructureTenGodType tenGodType

) {

    public AnnualLuckTenGodEvidence {

        Objects.requireNonNull(
                dayMaster,
                "세운 십성 Evidence에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                period,
                "세운 십성 Evidence에는 AnnualLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                sourceType,
                "세운 십성 Source Type이 필요합니다."
        );

        Objects.requireNonNull(
                sourceStem,
                "세운 십성 Source 천간이 필요합니다."
        );

        Objects.requireNonNull(
                tenGodType,
                "세운 십성 종류가 필요합니다."
        );

        /*
         * =================================================
         * 세운 천간 정합성
         * =================================================
         */

        if (
                sourceType.visibleStem()
                        && sourceStem
                        != period.stem()
        ) {

            throw new IllegalArgumentException(
                    "세운 천간 십성 Source가 실제 세운 천간과 일치하지 않습니다."
            );
        }
    }

    public int targetYear() {

        return period
                .targetYear();
    }

    public SexagenaryPillar annualPillar() {

        return period
                .pillar();
    }

    public HeavenlyStem annualStem() {

        return period
                .stem();
    }

    public EarthlyBranch annualBranch() {

        return period
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
                && tenGodType
                == expected;
    }
}