package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.MonthlyLuckTenGodSourceType;
import com.dajeom.type.StructureTenGodType;

import java.util.Objects;

/**
 * 정밀화 15차-3.
 *
 * 특정 월운에서 들어오는
 * 하나의 십성 Evidence.
 *
 * =================================================
 *
 * Natal
 * = 원국 십성
 *
 * Decade
 * = 대운 십성
 *
 * Annual
 * = 세운 십성
 *
 * Monthly
 * = 월운 십성
 *
 * =================================================
 *
 * 네 시간층을 서로 합치지 않는다.
 */
public record MonthlyLuckTenGodEvidence(

        HeavenlyStem dayMaster,

        MonthlyLuckPeriod period,

        MonthlyLuckTenGodSourceType sourceType,

        HeavenlyStem sourceStem,

        StructureTenGodType tenGodType

) {

    public MonthlyLuckTenGodEvidence {

        Objects.requireNonNull(
                dayMaster,
                "월운 십성 Evidence에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                period,
                "월운 십성 Evidence에는 MonthlyLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                sourceType,
                "월운 십성 Source Type이 필요합니다."
        );

        Objects.requireNonNull(
                sourceStem,
                "월운 십성 Source 천간이 필요합니다."
        );

        Objects.requireNonNull(
                tenGodType,
                "월운 십성 종류가 필요합니다."
        );

        /*
         * =================================================
         * 월운 천간 Source 검증
         * =================================================
         */

        if (
                sourceType.visibleStem()
                        && sourceStem != period.stem()
        ) {

            throw new IllegalArgumentException(
                    "월운 천간 십성 Source가 실제 월운 천간과 일치하지 않습니다."
            );
        }
    }

    public int sajuYear() {

        return period.sajuYear();
    }

    public int monthIndex() {

        return period.monthIndex();
    }

    public int monthOrder() {

        return period.monthOrder();
    }

    public SexagenaryPillar monthlyPillar() {

        return period.pillar();
    }

    public HeavenlyStem monthlyStem() {

        return period.stem();
    }

    public EarthlyBranch monthlyBranch() {

        return period.branch();
    }

    public boolean visible() {

        return sourceType.visibleStem();
    }

    public boolean hidden() {

        return sourceType.hiddenStem();
    }

    public boolean mainHiddenStem() {

        return sourceType.mainHiddenStem();
    }

    public boolean middleHiddenStem() {

        return sourceType.middleHiddenStem();
    }

    public boolean residualHiddenStem() {

        return sourceType.residualHiddenStem();
    }

    public boolean is(
            StructureTenGodType expected
    ) {

        return expected != null
                && tenGodType == expected;
    }
}