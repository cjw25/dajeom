package com.dajeom.model;

import com.dajeom.type.DecadeLuckTenGodSourceType;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.StructureTenGodType;

import java.util.Objects;

/**
 * 정밀화 13차-4.
 *
 * 기존 지장간/십성 계산기가 해결한
 * 대운 십성 Source.
 *
 * =================================================
 *
 * 이 객체에는:
 *
 * - 실제 Source 천간
 * - Source 위치
 * - 일간 기준 십성
 *
 * 만 담는다.
 *
 * 어느 대운 Period인지는
 * Analyzer가 별도로 연결한다.
 */
public record DecadeLuckResolvedTenGodSource(

        DecadeLuckTenGodSourceType sourceType,

        HeavenlyStem sourceStem,

        StructureTenGodType tenGodType

) {

    public DecadeLuckResolvedTenGodSource {

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