package com.dajeom.model;

import com.dajeom.type.DecadeLuckContextAxis;
import com.dajeom.type.DecadeLuckContextStatus;
import com.dajeom.type.NatalTenGodFunctionType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 하나의 대운 Period에 대한
 * 5대 기능축 분석 Aggregate.
 *
 * =================================================
 *
 * 구성:
 *
 * 13-4
 * DecadeLuckPeriodTenGodAnalysis
 *
 * +
 *
 * 13-5
 * DecadeLuckPeriodNatalRelationAnalysis
 *
 * +
 *
 * 13-6
 * DecadeLuckPeriodContextAnalysis
 *
 * ↓
 *
 * 5개 DecadeFunctionEffect
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * ContextSignal 중:
 *
 * tenGodTypes가 존재
 * ->
 * 해당 Function Effect에 연결
 *
 * tenGodTypes가 비어 있음
 * ->
 * globalContextSignals
 *
 * =================================================
 *
 * 따라서:
 *
 * STRUCTURE / USEFUL_GOD / RELATION 등의
 * 전역 Context를 임의로
 * 특정 Function에 귀속시키지 않는다.
 */
public record DecadePeriodFunctionalAnalysis(

        DecadeLuckPeriod period,

        DecadeLuckPeriodTenGodAnalysis
        tenGodAnalysis,

        DecadeLuckPeriodNatalRelationAnalysis
        natalRelationAnalysis,

        DecadeLuckPeriodContextAnalysis
        contextAnalysis,

        List<DecadeFunctionEffect>
        functionEffects,

        List<DecadeLuckContextSignal>
        globalContextSignals,

        boolean completeChart

) {

    public DecadePeriodFunctionalAnalysis {

        Objects.requireNonNull(
                period,
                "DecadePeriodFunctionalAnalysis에는 대운 Period가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodAnalysis,
                "DecadePeriodFunctionalAnalysis에는 13-4 십성 분석이 필요합니다."
        );

        Objects.requireNonNull(
                natalRelationAnalysis,
                "DecadePeriodFunctionalAnalysis에는 13-5 원국 관계 분석이 필요합니다."
        );

        Objects.requireNonNull(
                contextAnalysis,
                "DecadePeriodFunctionalAnalysis에는 13-6 Context 분석이 필요합니다."
        );

        functionEffects =
                functionEffects == null
                        ? List.of()
                        : List.copyOf(
                        functionEffects
                );

        globalContextSignals =
                globalContextSignals == null
                        ? List.of()
                        : List.copyOf(
                        globalContextSignals
                );

        /*
         * =================================================
         * Period Identity
         * =================================================
         */

        if (
                tenGodAnalysis.period()
                        != period
        ) {

            throw new IllegalArgumentException(
                    "13-4 대운 십성 분석이 현재 Period를 참조하지 않습니다."
            );
        }

        if (
                natalRelationAnalysis.period()
                        != period
        ) {

            throw new IllegalArgumentException(
                    "13-5 대운 관계 분석이 현재 Period를 참조하지 않습니다."
            );
        }

        if (
                contextAnalysis.period()
                        != period
        ) {

            throw new IllegalArgumentException(
                    "13-6 대운 Context가 현재 Period를 참조하지 않습니다."
            );
        }

        if (
                contextAnalysis.tenGodAnalysis()
                        != tenGodAnalysis
        ) {

            throw new IllegalArgumentException(
                    "13-6 Context가 현재 13-4 십성 분석을 참조하지 않습니다."
            );
        }

        if (
                contextAnalysis.natalRelationAnalysis()
                        != natalRelationAnalysis
        ) {

            throw new IllegalArgumentException(
                    "13-6 Context가 현재 13-5 원국 관계 분석을 참조하지 않습니다."
            );
        }

        /*
         * =================================================
         * Function은 정확히 5개.
         * =================================================
         */

        if (
                functionEffects.size()
                        != NatalTenGodFunctionType
                        .values()
                        .length
        ) {

            throw new IllegalArgumentException(
                    "각 대운 Period에는 정확히 "
                            + NatalTenGodFunctionType.values().length
                            + "개의 기능 Effect가 필요합니다."
            );
        }

        Set<NatalTenGodFunctionType> functionTypes =
                EnumSet.noneOf(
                        NatalTenGodFunctionType.class
                );

        for (
                DecadeFunctionEffect effect
                : functionEffects
        ) {

            Objects.requireNonNull(
                    effect,
                    "대운 Function Effect는 null일 수 없습니다."
            );

            if (
                    effect.period()
                            != period
            ) {

                throw new IllegalArgumentException(
                        "다른 Period의 Function Effect가 포함되었습니다."
                );
            }

            if (
                    effect.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Period Functional Analysis와 Function Effect의 "
                                + "completeChart 상태가 일치하지 않습니다."
                );
            }

            if (
                    !functionTypes.add(
                            effect.functionType()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 대운 기능축 Effect가 중복되었습니다: "
                                + effect.functionType()
                );
            }
        }

        Set<NatalTenGodFunctionType> expectedFunctions =
                EnumSet.allOf(
                        NatalTenGodFunctionType.class
                );

        if (
                !functionTypes.equals(
                        expectedFunctions
                )
        ) {

            throw new IllegalArgumentException(
                    "대운 기능축 5개가 모두 보존되지 않았습니다."
            );
        }

        /*
         * =================================================
         * 13-4 직접 십성 Evidence가
         * 정확히 하나의 기능축에 귀속되는지 검증.
         * =================================================
         */

        Set<DecadeLuckTenGodEvidence> assignedTenGodEvidences =
                new HashSet<>();

        for (
                DecadeFunctionEffect effect
                : functionEffects
        ) {

            for (
                    DecadeLuckTenGodEvidence evidence
                    : effect.directTenGodEvidences()
            ) {

                if (
                        !assignedTenGodEvidences.add(
                                evidence
                        )
                ) {

                    throw new IllegalArgumentException(
                            "동일 대운 십성 Evidence가 "
                                    + "여러 기능축에 중복 연결되었습니다."
                    );
                }
            }
        }

        Set<DecadeLuckTenGodEvidence> expectedTenGodEvidences =
                new HashSet<>(
                        tenGodAnalysis.evidences()
                );

        if (
                !assignedTenGodEvidences.equals(
                        expectedTenGodEvidences
                )
        ) {

            throw new IllegalArgumentException(
                    "13-4 대운 십성 Evidence가 "
                            + "5개 기능축에 정확히 보존되지 않았습니다."
            );
        }

        /*
         * =================================================
         * globalContextSignals
         * =================================================
         */

        for (
                DecadeLuckContextSignal signal
                : globalContextSignals
        ) {

            Objects.requireNonNull(
                    signal,
                    "대운 Global Context Signal은 null일 수 없습니다."
            );

            if (
                    !signal
                            .tenGodTypes()
                            .isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "십성 대상이 존재하는 Context Signal을 "
                                + "Global Context로 넣을 수 없습니다: "
                                + signal.code()
                );
            }

            if (
                    !contextAnalysis
                            .signals()
                            .contains(
                                    signal
                            )
            ) {

                throw new IllegalArgumentException(
                        "13-6 Context에 존재하지 않는 Signal이 "
                                + "Global Context에 포함되었습니다: "
                                + signal.code()
                );
            }
        }

        /*
         * =================================================
         * 13-6 Context Signal 손실 검증
         * =================================================
         *
         * 모든 Signal은:
         *
         * - 최소 하나의 Function Effect
         * 또는
         * - globalContextSignals
         *
         * 중 하나에 반드시 남아 있어야 한다.
         */

        for (
                DecadeLuckContextSignal sourceSignal
                : contextAnalysis.signals()
        ) {

            boolean preservedGlobally =
                    globalContextSignals
                            .contains(
                                    sourceSignal
                            );

            boolean preservedFunctionally =
                    functionEffects
                            .stream()
                            .anyMatch(
                                    effect ->
                                            effect
                                                    .contextSignals()
                                                    .contains(
                                                            sourceSignal
                                                    )
                            );

            if (
                    !preservedGlobally
                            && !preservedFunctionally
            ) {

                throw new IllegalArgumentException(
                        "13-6 Context Signal이 "
                                + "Functional Analysis에서 손실되었습니다: "
                                + sourceSignal.code()
                );
            }
        }
    }

    public int sequenceNumber() {

        return period
                .sequenceNumber();
    }

    /*
     * =================================================
     * Function
     * =================================================
     */

    public Optional<DecadeFunctionEffect>
    effectOf(

            NatalTenGodFunctionType functionType

    ) {

        if (
                functionType == null
        ) {

            return Optional.empty();
        }

        return functionEffects
                .stream()
                .filter(
                        effect ->
                                effect.functionType()
                                        == functionType
                )
                .findFirst();
    }

    public DecadeFunctionEffect
    requireEffect(

            NatalTenGodFunctionType functionType

    ) {

        Objects.requireNonNull(
                functionType,
                "대운 기능축은 필수입니다."
        );

        return effectOf(
                functionType
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "대운 기능 Effect를 찾을 수 없습니다: "
                                                + functionType
                                )
                );
    }

    public List<NatalTenGodFunctionType>
    directlyEnteredFunctions() {

        return functionEffects
                .stream()
                .filter(
                        DecadeFunctionEffect
                                ::directlyEntered
                )
                .map(
                        DecadeFunctionEffect
                                ::functionType
                )
                .toList();
    }

    public List<DecadeFunctionEffect>
    effectsWithMixedContext() {

        return functionEffects
                .stream()
                .filter(
                        DecadeFunctionEffect
                                ::mixedContext
                )
                .toList();
    }

    /*
     * =================================================
     * Global Context
     * =================================================
     */

    public List<DecadeLuckContextSignal>
    globalContextSignalsOf(

            DecadeLuckContextAxis axis

    ) {

        if (
                axis == null
        ) {

            return List.of();
        }

        return globalContextSignals
                .stream()
                .filter(
                        signal ->
                                signal.axis()
                                        == axis
                )
                .toList();
    }

    public List<DecadeLuckContextSignal>
    globalContextSignalsOf(

            DecadeLuckContextStatus status

    ) {

        if (
                status == null
        ) {

            return List.of();
        }

        return globalContextSignals
                .stream()
                .filter(
                        signal ->
                                signal.status()
                                        == status
                )
                .toList();
    }

    /**
     * 전체 Period 차원에서
     * Context 방향이 복합적인가.
     *
     * 기능별 Effect와는 별개다.
     */
    public boolean mixedGlobalContext() {

        boolean supportive =
                globalContextSignals
                        .stream()
                        .anyMatch(
                                signal ->
                                        signal.supported()
                                                || signal.qualified()
                        );

        boolean restrictive =
                globalContextSignals
                        .stream()
                        .anyMatch(
                                signal ->
                                        signal.weakened()
                                                || signal.conflicted()
                        );

        return supportive
                && restrictive;
    }

    /*
     * =================================================
     * 13-5 Transformation
     * =================================================
     *
     * 아직 어느 기능이 어떻게 바뀌었는지는
     * 이 단계에서 재판정하지 않는다.
     */

    public boolean activatedTransformationExists() {

        return natalRelationAnalysis
                .hasActivatedTransformation();
    }

    public boolean originalFunctionReductionExists() {

        return natalRelationAnalysis
                .hasReducedOriginalFunction();
    }

    /*
     * =================================================
     * Function별 Context index
     * =================================================
     */

    public Map<NatalTenGodFunctionType, DecadeFunctionEffect>
    functionEffectIndex() {

        Map<NatalTenGodFunctionType, DecadeFunctionEffect> result =
                new EnumMap<>(
                        NatalTenGodFunctionType.class
                );

        for (
                DecadeFunctionEffect effect
                : functionEffects
        ) {

            result.put(
                    effect.functionType(),
                    effect
            );
        }

        return Map.copyOf(
                result
        );
    }
}