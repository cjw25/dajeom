package com.dajeom.model;

import com.dajeom.type.AnnualLuckTenGodSourceType;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.StructureTenGodType;

import java.util.Objects;

/**
 * 정밀화 14차-3.
 *
 * 기존 지장간 / 십성 계산기가
 * 해결한 세운 십성 Source.
 *
 * =================================================
 *
 * 예:
 *
 * 세운 丙午
 *
 * VISIBLE_STEM
 * sourceStem = 丙
 * tenGodType = ...
 *
 * BRANCH_MAIN
 * sourceStem = 丁
 * tenGodType = ...
 *
 * =================================================
 *
 * 어느 연도의 세운인지는
 * Analyzer에서 AnnualLuckPeriod와 결합한다.
 */
public record AnnualLuckResolvedTenGodSource(

        AnnualLuckTenGodSourceType sourceType,

        HeavenlyStem sourceStem,

        StructureTenGodType tenGodType

) {

    public AnnualLuckResolvedTenGodSource {

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
    }

    public boolean visible() {

        return sourceType
                .visibleStem();
    }

    public boolean hidden() {

        return sourceType
                .hiddenStem();
    }
}