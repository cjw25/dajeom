package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.MonthlyLuckTenGodSourceType;
import com.dajeom.type.StructureTenGodType;

import java.util.Objects;

/**
 * 정밀화 15차-3.
 *
 * 기존 지장간 / 십성 계산기가
 * 해결한 월운 십성 Source.
 *
 * =================================================
 *
 * 예:
 *
 * 월운 庚寅
 *
 * VISIBLE_STEM
 * sourceStem = 庚
 * tenGodType = DIRECT_WEALTH
 *
 * BRANCH_MAIN
 * sourceStem = 甲
 * tenGodType = ...
 *
 * =================================================
 *
 * 실제 지장간 구성과 순서는
 * 기존 2차 canonical source를 사용한다.
 */
public record MonthlyLuckResolvedTenGodSource(

        MonthlyLuckTenGodSourceType sourceType,

        HeavenlyStem sourceStem,

        StructureTenGodType tenGodType

) {

    public MonthlyLuckResolvedTenGodSource {

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
    }

    public boolean visible() {

        return sourceType.visibleStem();
    }

    public boolean hidden() {

        return sourceType.hiddenStem();
    }
}