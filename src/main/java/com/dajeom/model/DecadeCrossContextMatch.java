package com.dajeom.model;

import com.dajeom.type.DecadeContextTenGodMatchLevel;
import com.dajeom.type.DecadeLuckContextAxis;
import com.dajeom.type.DecadeLuckContextStatus;
import com.dajeom.type.StructureTenGodType;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 하나의 대운 CrossFunctional Effect와
 * 하나의 13-6 Context Signal 사이의
 * 구조적 십성 Match.
 *
 * =================================================
 *
 * 비교 대상:
 *
 * Natal Cross Pattern
 * sourceSignal.involvedTenGodTypes
 *
 * ↔
 *
 * DecadeLuckContextSignal.tenGodTypes
 *
 * =================================================
 *
 * 문자열:
 *
 * - code
 * - summary
 * - reasons
 *
 * 는 Match 판정에 사용하지 않는다.
 *
 * =================================================
 *
 * 중요:
 *
 * 이 객체는 Context Signal이
 * Cross Pattern과 같은 십성 구조를
 * 가리키는지를 확인할 뿐이다.
 *
 * 동일 upstream Evidence에서
 * 파생되었는지까지 판단하지 않으므로
 * 독립 근거 개수로 세면 안 된다.
 */
public record DecadeCrossContextMatch(

        DecadeCrossFunctionalEffect sourceEffect,

        DecadeLuckContextSignal contextSignal,

        DecadeContextTenGodMatchLevel matchLevel,

        boolean completeChart

) {

    public DecadeCrossContextMatch {

        Objects.requireNonNull(
                sourceEffect,
                "Cross Context Match에는 DecadeCrossFunctionalEffect가 필요합니다."
        );

        Objects.requireNonNull(
                contextSignal,
                "Cross Context Match에는 DecadeLuckContextSignal이 필요합니다."
        );

        Objects.requireNonNull(
                matchLevel,
                "Cross Context Match에는 TenGod Match Level이 필요합니다."
        );

        if (
                sourceEffect.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Cross Effect와 Context Match의 completeChart가 일치하지 않습니다."
            );
        }

        DecadeContextTenGodMatchLevel expected =
                determineMatchLevel(
                        patternTenGodTypes(
                                sourceEffect
                        ),
                        contextSignal.tenGodTypes()
                );

        if (
                expected
                        == DecadeContextTenGodMatchLevel.NONE
        ) {

            throw new IllegalArgumentException(
                    "십성 구조가 전혀 겹치지 않는 Context Signal은 "
                            + "Cross Context Match로 만들 수 없습니다. "
                            + "pattern="
                            + sourceEffect.patternType()
                            + ", contextCode="
                            + contextSignal.code()
            );
        }

        if (
                matchLevel
                        != expected
        ) {

            throw new IllegalArgumentException(
                    "Cross Context Match Level이 실제 십성 Set과 일치하지 않습니다. "
                            + "pattern="
                            + sourceEffect.patternType()
                            + ", expected="
                            + expected
                            + ", actual="
                            + matchLevel
            );
        }
    }

    /*
     * =================================================
     * Pattern TenGod
     * =================================================
     */

    public Set<StructureTenGodType>
    patternTenGodTypes() {

        return patternTenGodTypes(
                sourceEffect
        );
    }

    /*
     * =================================================
     * Match
     * =================================================
     */

    public boolean exactTenGodMatch() {

        return matchLevel
                .exact();
    }

    public boolean overlappingTenGodMatch() {

        return matchLevel
                .overlap();
    }

    /*
     * =================================================
     * Context
     * =================================================
     */

    public DecadeLuckContextAxis axis() {

        return contextSignal
                .axis();
    }

    public DecadeLuckContextStatus status() {

        return contextSignal
                .status();
    }

    public boolean supported() {

        return contextSignal
                .supported();
    }

    public boolean qualified() {

        return contextSignal
                .qualified();
    }

    public boolean weakened() {

        return contextSignal
                .weakened();
    }

    public boolean conflicted() {

        return contextSignal
                .conflicted();
    }

    /*
     * =================================================
     * Static Match
     * =================================================
     */

    public static DecadeContextTenGodMatchLevel
    determineMatchLevel(

            Set<StructureTenGodType> patternTypes,

            Set<StructureTenGodType> contextTypes

    ) {

        if (
                patternTypes == null
                        || patternTypes.isEmpty()
                        || contextTypes == null
                        || contextTypes.isEmpty()
        ) {

            return DecadeContextTenGodMatchLevel
                    .NONE;
        }

        if (
                patternTypes.equals(
                        contextTypes
                )
        ) {

            return DecadeContextTenGodMatchLevel
                    .EXACT;
        }

        boolean overlap =
                patternTypes
                        .stream()
                        .anyMatch(
                                contextTypes::contains
                        );

        return overlap
                ? DecadeContextTenGodMatchLevel.OVERLAP
                : DecadeContextTenGodMatchLevel.NONE;
    }

    private static Set<StructureTenGodType>
    patternTenGodTypes(

            DecadeCrossFunctionalEffect sourceEffect

    ) {

        Objects.requireNonNull(
                sourceEffect,
                "Pattern TenGod 조회에는 Cross Effect가 필요합니다."
        );

        Set<StructureTenGodType> result =
                new HashSet<>(
                        sourceEffect
                                .sourcePattern()
                                .sourcePattern()
                                .sourceSignal()
                                .involvedTenGodTypes()
                );

        if (
                result.isEmpty()
        ) {

            throw new IllegalStateException(
                    "원국 Cross Pattern의 involvedTenGodTypes가 비어 있습니다: "
                            + sourceEffect.patternType()
            );
        }

        return Set.copyOf(
                result
        );
    }
}