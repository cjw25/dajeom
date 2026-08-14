package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.PalaceTenGodSourceType;
import com.dajeom.type.StructureTenGodType;

import java.util.Objects;

/**
 * 정밀화 12차-3.
 *
 * 기존 십성 계산 엔진에서 해결된
 * 하나의 궁위 십성 Source.
 *
 * =================================================
 *
 * 이 객체를 별도로 두는 이유:
 *
 * 12차가 기존:
 *
 * HiddenStemTable
 * StructureTenGodCalculator
 *
 * 의 내부 API에 직접 결합하지 않도록 하기 위함이다.
 *
 * =================================================
 *
 * 예:
 *
 * MONTH 庚申
 *
 * 庚
 * -> VISIBLE_STEM
 * -> 정재
 *
 * 申의 庚
 * -> BRANCH_MAIN
 * -> 정재
 *
 * 申의 壬
 * -> BRANCH_MIDDLE
 * -> 정관
 *
 * 申의 戊
 * -> BRANCH_RESIDUAL
 * -> 상관
 *
 * =================================================
 *
 * 실제 십성 이름은 반드시 기존
 * StructureTenGodType을 사용한다.
 *
 * 12차 전용 십성 enum을 만들지 않는다.
 */
public record PalaceResolvedTenGodSource(

        PalacePosition position,

        PalaceTenGodSourceType sourceType,

        HeavenlyStem sourceStem,

        StructureTenGodType tenGodType

) {

    public PalaceResolvedTenGodSource {

        Objects.requireNonNull(
                position,
                "궁위 위치가 필요합니다."
        );

        Objects.requireNonNull(
                sourceType,
                "궁위 십성 Source Type이 필요합니다."
        );

        Objects.requireNonNull(
                sourceStem,
                "궁위 십성 Source 천간이 필요합니다."
        );

        Objects.requireNonNull(
                tenGodType,
                "궁위 십성 종류가 필요합니다."
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