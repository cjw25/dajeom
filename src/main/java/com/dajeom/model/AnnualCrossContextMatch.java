package com.dajeom.model;

import com.dajeom.type.AnnualContextTenGodMatchLevel;
import com.dajeom.type.AnnualCrossContextScope;
import com.dajeom.type.AnnualLuckContextStatus;
import com.dajeom.type.StructureTenGodType;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 하나의 Annual CrossFunctional Pattern과
 * 하나의 14-6 Context Signal 사이의
 * 구조적 십성 Match.
 *
 * =================================================
 *
 * Pattern Source:
 *
 * NatalCrossFunctionalPattern
 * ->
 * TenGodCrossValidatedPattern
 * ->
 * TenGodCorePatternSignal
 * ->
 * involvedTenGodTypes
 *
 * =================================================
 *
 * Context Source:
 *
 * AnnualLuckContextSignal.tenGodTypes
 *
 * =================================================
 *
 * 문자열:
 *
 * code
 * summary
 * reasons
 *
 * 은 Match 판정에 사용하지 않는다.
 *
 * =================================================
 *
 * 중요:
 *
 * 이 객체는 matched Context만 생성한다.
 *
 * NONE은 Resolver 결과로는 존재할 수 있지만
 * 실제 AnnualCrossContextMatch 객체에는
 * 저장하지 않는다.
 *
 * =================================================
 *
 * EXACT도 독립 Evidence가 아니다.
 *
 * =================================================
 * Identity Provenance
 * =================================================
 *
 * Context Signal provenance 검증에서는
 * List.contains()의 구조적 equals를 사용하지 않는다.
 *
 * 이 객체에 전달되는 contextSignal은
 * AnnualLuckDecadeContextAnalysis가 실제 보유한
 * annualSignals / segmentSignals 중 하나여야 한다.
 *
 * 따라서 같은 값을 가진 별도 객체가 아니라
 * 실제 원본 Signal 객체 Identity가 존재하는지를
 * 기준으로 검증한다.
 *
 * 이는:
 *
 * - derived context의 provenance를 엄격하게 유지하고
 * - 큰 분석 객체 그래프에 대한 반복 equals 비교를 방지하며
 * - Annual Cross Synthesis의 의미를 변경하지 않는다.
 */
public record AnnualCrossContextMatch(

        AnnualCrossFunctionalPatternEffect
        sourceEffect,

        AnnualLuckContextSignal
        contextSignal,

        AnnualCrossContextScope
        contextScope,

        AnnualContextTenGodMatchLevel
        matchLevel,

        boolean completeChart

) {

    public AnnualCrossContextMatch {

        Objects.requireNonNull(
                sourceEffect,
                "Annual Cross Context Match에는 Pattern Effect가 필요합니다."
        );

        Objects.requireNonNull(
                contextSignal,
                "Annual Cross Context Match에는 Context Signal이 필요합니다."
        );

        Objects.requireNonNull(
                contextScope,
                "Annual Cross Context Match에는 Context Scope가 필요합니다."
        );

        Objects.requireNonNull(
                matchLevel,
                "Annual Cross Context Match에는 Match Level이 필요합니다."
        );

        if (
                sourceEffect.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Annual Cross Pattern Effect와 Context Match의 completeChart가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Context Source Scope 검증
         * =================================================
         *
         * 값 동등성(equals)이 아니라
         * 실제 Context Analysis가 보유한
         * Signal 객체 Identity를 검증한다.
         */

        AnnualLuckDecadeContextAnalysis contextAnalysis =
                sourceEffect
                        .resolvedSegment()
                        .sourceSegment()
                        .annualDecadeContext();

        boolean knownSignal =
                switch (
                        contextScope
                        ) {

                    case ANNUAL_COMMON ->
                            containsIdentity(
                                    contextAnalysis
                                            .annualSignals(),
                                    contextSignal
                            );

                    case DECADE_SEGMENT ->
                            containsIdentity(
                                    contextAnalysis
                                            .segmentSignals(),
                                    contextSignal
                            );
                };

        if (
                !knownSignal
        ) {

            throw new IllegalArgumentException(
                    "지정된 Context Scope에 존재하지 않는 AnnualLuckContextSignal입니다. "
                            + "scope="
                            + contextScope
                            + ", code="
                            + contextSignal.code()
            );
        }

        /*
         * =================================================
         * Match Level 검증
         * =================================================
         */

        AnnualContextTenGodMatchLevel expected =
                resolveMatchLevel(
                        sourceEffect,
                        contextSignal
                );

        if (
                matchLevel
                        != expected
        ) {

            throw new IllegalArgumentException(
                    "Annual Context TenGod Match Level이 실제 Set 관계와 일치하지 않습니다. "
                            + "pattern="
                            + sourceEffect.patternType()
                            + ", signal="
                            + contextSignal.code()
                            + ", expected="
                            + expected
                            + ", actual="
                            + matchLevel
            );
        }

        if (
                !matchLevel
                        .matched()
        ) {

            throw new IllegalArgumentException(
                    "TenGod Match가 없는 Context Signal은 AnnualCrossContextMatch로 생성하지 않습니다. "
                            + "pattern="
                            + sourceEffect.patternType()
                            + ", signal="
                            + contextSignal.code()
            );
        }
    }

    /*
     * =================================================
     * Structure
     * =================================================
     */

    public Set<StructureTenGodType>
    patternTenGodTypes() {

        return patternTenGodTypesOf(
                sourceEffect
        );
    }

    public Set<StructureTenGodType>
    contextTenGodTypes() {

        return contextSignal
                .tenGodTypes();
    }

    public Set<StructureTenGodType>
    overlappingTenGodTypes() {

        Set<StructureTenGodType> result =
                new LinkedHashSet<>(
                        patternTenGodTypes()
                );

        result.retainAll(
                contextTenGodTypes()
        );

        return Set.copyOf(
                result
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
     * Scope
     * =================================================
     */

    public boolean annualCommonContext() {

        return contextScope
                .annualCommon();
    }

    public boolean decadeSegmentContext() {

        return contextScope
                .decadeSegment();
    }

    /*
     * =================================================
     * Status
     * =================================================
     */

    public AnnualLuckContextStatus status() {

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
     * Resolver
     * =================================================
     */

    public static AnnualContextTenGodMatchLevel
    resolveMatchLevel(

            AnnualCrossFunctionalPatternEffect sourceEffect,

            AnnualLuckContextSignal contextSignal

    ) {

        Objects.requireNonNull(
                sourceEffect,
                "Annual Context Match 판정에는 Pattern Effect가 필요합니다."
        );

        Objects.requireNonNull(
                contextSignal,
                "Annual Context Match 판정에는 Context Signal이 필요합니다."
        );

        Set<StructureTenGodType> patternTypes =
                patternTenGodTypesOf(
                        sourceEffect
                );

        Set<StructureTenGodType> contextTypes =
                contextSignal
                        .tenGodTypes();

        /*
         * tenGodTypes가 없는 Context는
         * 특정 Cross Pattern에 직접 연결하지 않는다.
         *
         * Global Context로 upstream에 그대로 남는다.
         */

        if (
                patternTypes.isEmpty()
                        || contextTypes.isEmpty()
        ) {

            return AnnualContextTenGodMatchLevel
                    .NONE;
        }

        if (
                patternTypes.equals(
                        contextTypes
                )
        ) {

            return AnnualContextTenGodMatchLevel
                    .EXACT;
        }

        boolean overlap =
                patternTypes
                        .stream()
                        .anyMatch(
                                contextTypes::contains
                        );

        return overlap
                ? AnnualContextTenGodMatchLevel.OVERLAP
                : AnnualContextTenGodMatchLevel.NONE;
    }

    public static Set<StructureTenGodType>
    patternTenGodTypesOf(
            AnnualCrossFunctionalPatternEffect sourceEffect
    ) {

        Objects.requireNonNull(
                sourceEffect,
                "Pattern TenGodType 조회에는 Annual CrossFunctional Pattern Effect가 필요합니다."
        );

        return Set.copyOf(
                sourceEffect
                        .sourcePattern()
                        .sourcePattern()
                        .sourceSignal()
                        .involvedTenGodTypes()
        );
    }

    /*
     * =================================================
     * Identity
     * =================================================
     */

    private static boolean containsIdentity(

            List<AnnualLuckContextSignal> signals,

            AnnualLuckContextSignal target

    ) {

        Objects.requireNonNull(
                signals,
                "Context Signal 목록은 null일 수 없습니다."
        );

        Objects.requireNonNull(
                target,
                "Context Signal Identity 조회 대상은 null일 수 없습니다."
        );

        for (
                AnnualLuckContextSignal signal
                : signals
        ) {

            if (
                    signal
                            == target
            ) {

                return true;
            }
        }

        return false;
    }
}