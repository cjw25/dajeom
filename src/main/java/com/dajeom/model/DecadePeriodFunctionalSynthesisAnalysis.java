package com.dajeom.model;

import com.dajeom.type.NatalCrossFunctionalPatternType;
import com.dajeom.type.NatalTenGodFunctionType;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 하나의 대운 Period에 대한
 * 최종 Functional Synthesis Aggregate.
 *
 * =================================================
 *
 * 구성:
 *
 * DecadePeriodResolvedFunctionalAnalysis
 *
 * +
 *
 * DecadePeriodCrossFunctionalAnalysis
 *
 * +
 *
 * Cross Pattern별
 * DecadeCrossFunctionalSynthesis
 *
 * =================================================
 *
 * 이 Aggregate가 이후 Annual Functional Layer가
 * 사용할 공식 대운 기능 기준선이다.
 *
 * =================================================
 *
 * 여기에는:
 *
 * - Function 활성
 * - 원기능 감소
 * - Transformation Target
 * - Capacity
 * - Natal Cross Pattern Contact
 * - 13-6 Context Match
 *
 * 가 모두 연결되어 있다.
 *
 * =================================================
 *
 * 그러나:
 *
 * - 좋음/나쁨
 * - 직업운/재물운
 * - 사건
 *
 * 은 아직 만들지 않는다.
 */
public record DecadePeriodFunctionalSynthesisAnalysis(

        DecadePeriodResolvedFunctionalAnalysis
        resolvedFunctionalAnalysis,

        DecadePeriodCrossFunctionalAnalysis
        crossFunctionalAnalysis,

        List<DecadeCrossFunctionalSynthesis>
        crossSyntheses,

        boolean completeChart

) {

    public DecadePeriodFunctionalSynthesisAnalysis {

        Objects.requireNonNull(
                resolvedFunctionalAnalysis,
                "Period Functional Synthesis에는 Resolved Functional Analysis가 필요합니다."
        );

        Objects.requireNonNull(
                crossFunctionalAnalysis,
                "Period Functional Synthesis에는 CrossFunctional Analysis가 필요합니다."
        );

        crossSyntheses =
                crossSyntheses == null
                        ? List.of()
                        : List.copyOf(
                        crossSyntheses
                );

        if (
                resolvedFunctionalAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Resolved Functional Period와 Synthesis의 completeChart가 일치하지 않습니다."
            );
        }

        if (
                crossFunctionalAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "CrossFunctional Period와 Synthesis의 completeChart가 일치하지 않습니다."
            );
        }

        if (
                crossFunctionalAnalysis
                        .resolvedFunctionalAnalysis()
                        != resolvedFunctionalAnalysis
        ) {

            throw new IllegalArgumentException(
                    "CrossFunctional Period가 현재 Resolved Functional Period를 참조하지 않습니다."
            );
        }

        /*
         * =================================================
         * Cross Effect Coverage
         * =================================================
         */

        if (
                crossSyntheses.size()
                        != crossFunctionalAnalysis
                        .crossFunctionalEffects()
                        .size()
        ) {

            throw new IllegalArgumentException(
                    "CrossFunctional Effect 수와 Synthesis 수가 일치하지 않습니다."
            );
        }

        Set<DecadeCrossFunctionalEffect> seen =
                new HashSet<>();

        for (
                DecadeCrossFunctionalSynthesis synthesis
                : crossSyntheses
        ) {

            Objects.requireNonNull(
                    synthesis,
                    "DecadeCrossFunctionalSynthesis는 null일 수 없습니다."
            );

            if (
                    synthesis.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "CrossFunctional Synthesis의 completeChart가 일치하지 않습니다."
                );
            }

            if (
                    !crossFunctionalAnalysis
                            .crossFunctionalEffects()
                            .contains(
                                    synthesis.sourceEffect()
                            )
            ) {

                throw new IllegalArgumentException(
                        "현재 Period의 Cross Effect가 아닌 Synthesis가 연결되었습니다."
                );
            }

            if (
                    !seen.add(
                            synthesis.sourceEffect()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 Cross Effect가 Synthesis에 중복되었습니다: "
                                + synthesis.patternType()
                );
            }
        }

        Set<DecadeCrossFunctionalEffect> expected =
                new HashSet<>(
                        crossFunctionalAnalysis
                                .crossFunctionalEffects()
                );

        if (
                !seen.equals(
                        expected
                )
        ) {

            throw new IllegalArgumentException(
                    "CrossFunctional Effect 일부가 Synthesis에서 누락되었습니다."
            );
        }
    }

    public int sequenceNumber() {

        return resolvedFunctionalAnalysis
                .sequenceNumber();
    }

    public DecadeLuckPeriod period() {

        return resolvedFunctionalAnalysis
                .period();
    }

    /*
     * =================================================
     * Function
     * =================================================
     */

    public Optional<DecadeResolvedFunctionEffect>
    functionEffectOf(

            NatalTenGodFunctionType functionType

    ) {

        return resolvedFunctionalAnalysis
                .effectOf(
                        functionType
                );
    }

    public DecadeResolvedFunctionEffect
    requireFunctionEffect(

            NatalTenGodFunctionType functionType

    ) {

        return resolvedFunctionalAnalysis
                .requireEffect(
                        functionType
                );
    }

    public List<DecadeResolvedFunctionEffect>
    activeFunctionEffects() {

        return resolvedFunctionalAnalysis
                .activeEffects();
    }

    /**
     * Function은 대운에서 활성됐지만
     * 어떤 원국 Cross Pattern에도
     * temporal contact가 없는 경우.
     *
     * 이것은 약한 Function이라는 뜻이 아니다.
     *
     * 단지 Cross Pattern 연결 없이
     * Function 자체로 활성된 상태다.
     */
    public List<DecadeResolvedFunctionEffect>
    functionOnlyEffects() {

        return activeFunctionEffects()
                .stream()
                .filter(
                        effect ->
                                !hasCrossContactFor(
                                        effect.functionType()
                                )
                )
                .toList();
    }

    public boolean hasCrossContactFor(

            NatalTenGodFunctionType functionType

    ) {

        if (
                functionType == null
        ) {

            return false;
        }

        return crossSyntheses
                .stream()
                .filter(
                        DecadeCrossFunctionalSynthesis
                                ::temporalContactExists
                )
                .map(
                        DecadeCrossFunctionalSynthesis
                                ::sourceEffect
                )
                .anyMatch(
                        effect -> {

                            if (
                                    effect
                                            .patternType()
                                            .primaryFunction()
                                            == functionType
                            ) {

                                return true;
                            }

                            return effect
                                    .patternType()
                                    .secondaryFunction()
                                    .map(
                                            secondary ->
                                                    secondary
                                                            == functionType
                                    )
                                    .orElse(
                                            false
                                    );
                        }
                );
    }

    /*
     * =================================================
     * Cross Synthesis
     * =================================================
     */

    public Optional<DecadeCrossFunctionalSynthesis>
    synthesisOf(

            NatalCrossFunctionalPatternType patternType

    ) {

        if (
                patternType == null
        ) {

            return Optional.empty();
        }

        return crossSyntheses
                .stream()
                .filter(
                        synthesis ->
                                synthesis.patternType()
                                        == patternType
                )
                .findFirst();
    }

    public DecadeCrossFunctionalSynthesis
    requireSynthesis(

            NatalCrossFunctionalPatternType patternType

    ) {

        Objects.requireNonNull(
                patternType,
                "Cross Pattern Type은 필수입니다."
        );

        return synthesisOf(
                patternType
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "대운 CrossFunctional Synthesis를 찾을 수 없습니다: "
                                                + patternType
                                )
                );
    }

    public List<DecadeCrossFunctionalSynthesis>
    contactedSyntheses() {

        return crossSyntheses
                .stream()
                .filter(
                        DecadeCrossFunctionalSynthesis
                                ::temporalContactExists
                )
                .toList();
    }

    public List<DecadeCrossFunctionalSynthesis>
    partialCrossContacts() {

        return crossSyntheses
                .stream()
                .filter(
                        DecadeCrossFunctionalSynthesis
                                ::partialPatternContact
                )
                .toList();
    }

    public List<DecadeCrossFunctionalSynthesis>
    effectiveFullCrossContacts() {

        return crossSyntheses
                .stream()
                .filter(
                        DecadeCrossFunctionalSynthesis
                                ::effectiveFullPatternContact
                )
                .toList();
    }

    public List<DecadeCrossFunctionalSynthesis>
    effectiveFullContactsWithExactContext() {

        return crossSyntheses
                .stream()
                .filter(
                        DecadeCrossFunctionalSynthesis
                                ::effectiveFullContactWithExactContext
                )
                .toList();
    }

    public List<DecadeCrossFunctionalSynthesis>
    referenceContacts() {

        return crossSyntheses
                .stream()
                .filter(
                        DecadeCrossFunctionalSynthesis
                                ::referencePatternContact
                )
                .toList();
    }

    public List<DecadeCrossFunctionalSynthesis>
    qualifiedSyntheses() {

        return crossSyntheses
                .stream()
                .filter(
                        DecadeCrossFunctionalSynthesis
                                ::qualificationRequired
                )
                .toList();
    }

    /*
     * =================================================
     * Global 13-6 Context
     * =================================================
     *
     * tenGodTypes가 비어 있어
     * 특정 Cross Pattern에 연결할 수 없는
     * Context는 여기서 Period Global로 보존한다.
     */

    public List<DecadeLuckContextSignal>
    globalContextSignals() {

        return resolvedFunctionalAnalysis
                .migrationAnalysis()
                .capacityAnalysis()
                .operationAnalysis()
                .functionalAnalysis()
                .globalContextSignals();
    }

    public boolean mixedGlobalContext() {

        return resolvedFunctionalAnalysis
                .migrationAnalysis()
                .capacityAnalysis()
                .operationAnalysis()
                .functionalAnalysis()
                .mixedGlobalContext();
    }
}