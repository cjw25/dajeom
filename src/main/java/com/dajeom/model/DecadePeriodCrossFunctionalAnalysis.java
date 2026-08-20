package com.dajeom.model;

import com.dajeom.type.NatalCrossFunctionalPatternType;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 하나의 대운 Period에서
 * 모든 NatalCrossFunctionalPattern에 대한
 * 접촉 결과를 보존한다.
 *
 * =================================================
 *
 * 원국 Pattern:
 *
 * REFERENCE 포함 전부 보존.
 *
 * 따라서:
 *
 * natalCrossFunctionalAnalysis.patterns()
 *
 * 와
 *
 * crossFunctionalEffects
 *
 * 는 정확히 1:1이다.
 *
 * =================================================
 *
 * 이후 실제 해석에서는:
 *
 * effectiveFullContacts()
 *
 * 등을 사용하고,
 *
 * reference contact는
 * 별도 Context로만 사용할 수 있다.
 */
public record DecadePeriodCrossFunctionalAnalysis(

        NatalCrossFunctionalAnalysis
        natalCrossFunctionalAnalysis,

        DecadePeriodResolvedFunctionalAnalysis
        resolvedFunctionalAnalysis,

        List<DecadeCrossFunctionalEffect>
        crossFunctionalEffects,

        boolean completeChart

) {

    public DecadePeriodCrossFunctionalAnalysis {

        Objects.requireNonNull(
                natalCrossFunctionalAnalysis,
                "Period CrossFunctional Analysis에는 NatalCrossFunctionalAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                resolvedFunctionalAnalysis,
                "Period CrossFunctional Analysis에는 Resolved Functional Analysis가 필요합니다."
        );

        crossFunctionalEffects =
                crossFunctionalEffects == null
                        ? List.of()
                        : List.copyOf(
                        crossFunctionalEffects
                );

        if (
                natalCrossFunctionalAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "NatalCrossFunctionalAnalysis와 Period Cross Analysis의 completeChart가 일치하지 않습니다."
            );
        }

        if (
                resolvedFunctionalAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Resolved Functional Period와 Cross Analysis의 completeChart가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 원국 Pattern 전체 Coverage
         * =================================================
         */

        if (
                crossFunctionalEffects.size()
                        != natalCrossFunctionalAnalysis
                        .patterns()
                        .size()
        ) {

            throw new IllegalArgumentException(
                    "원국 Cross Pattern 수와 "
                            + "대운 Cross Effect 수가 일치하지 않습니다."
            );
        }

        Set<NatalCrossFunctionalPattern> seen =
                new HashSet<>();

        for (
                DecadeCrossFunctionalEffect effect
                : crossFunctionalEffects
        ) {

            Objects.requireNonNull(
                    effect,
                    "DecadeCrossFunctionalEffect는 null일 수 없습니다."
            );

            if (
                    effect.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "대운 Cross Effect의 completeChart가 일치하지 않습니다."
                );
            }

            if (
                    !natalCrossFunctionalAnalysis
                            .patterns()
                            .contains(
                                    effect.sourcePattern()
                            )
            ) {

                throw new IllegalArgumentException(
                        "원국 CrossFunctionalAnalysis에 없는 Pattern이 "
                                + "대운 Cross Effect에 포함되었습니다."
                );
            }

            if (
                    !seen.add(
                            effect.sourcePattern()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 원국 Cross Pattern이 "
                                + "대운 Period에서 중복 변환되었습니다: "
                                + effect.patternType()
                );
            }

            for (
                    DecadeCrossFunctionalParticipantEffect participant
                    : effect.participantEffects()
            ) {

                DecadeResolvedFunctionEffect expected =
                        resolvedFunctionalAnalysis
                                .requireEffect(
                                        participant.functionType()
                                );

                if (
                        participant.resolvedFunctionEffect()
                                != expected
                ) {

                    throw new IllegalArgumentException(
                            "Cross Participant가 현재 Period의 "
                                    + "Resolved Function Effect Identity를 보존하지 않았습니다. function="
                                    + participant.functionType()
                    );
                }
            }
        }

        Set<NatalCrossFunctionalPattern> expected =
                new HashSet<>(
                        natalCrossFunctionalAnalysis
                                .patterns()
                );

        if (
                !seen.equals(
                        expected
                )
        ) {

            throw new IllegalArgumentException(
                    "원국 Cross Pattern 일부가 "
                            + "대운 CrossFunctional Analysis에서 누락되었습니다."
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
     * Pattern
     * =================================================
     */

    public Optional<DecadeCrossFunctionalEffect>
    effectOf(

            NatalCrossFunctionalPatternType patternType

    ) {

        if (
                patternType == null
        ) {

            return Optional.empty();
        }

        return crossFunctionalEffects
                .stream()
                .filter(
                        effect ->
                                effect.patternType()
                                        == patternType
                )
                .findFirst();
    }

    public DecadeCrossFunctionalEffect
    requireEffect(

            NatalCrossFunctionalPatternType patternType

    ) {

        Objects.requireNonNull(
                patternType,
                "CrossFunctional Pattern Type은 필수입니다."
        );

        return effectOf(
                patternType
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "대운 CrossFunctional Effect를 찾을 수 없습니다: "
                                                + patternType
                                )
                );
    }

    /*
     * =================================================
     * Contact
     * =================================================
     */

    public List<DecadeCrossFunctionalEffect>
    contactedEffects() {

        return crossFunctionalEffects
                .stream()
                .filter(
                        DecadeCrossFunctionalEffect
                                ::temporalContactExists
                )
                .toList();
    }

    public List<DecadeCrossFunctionalEffect>
    partialContacts() {

        return crossFunctionalEffects
                .stream()
                .filter(
                        DecadeCrossFunctionalEffect
                                ::partialPatternContact
                )
                .toList();
    }

    public List<DecadeCrossFunctionalEffect>
    fullContacts() {

        return crossFunctionalEffects
                .stream()
                .filter(
                        DecadeCrossFunctionalEffect
                                ::fullPatternContact
                )
                .toList();
    }

    /**
     * 원국에서 이미 structurallyEffective한 Pattern이며
     * 이번 대운에서 full participant contact가 발생한 것.
     */
    public List<DecadeCrossFunctionalEffect>
    effectiveFullContacts() {

        return crossFunctionalEffects
                .stream()
                .filter(
                        DecadeCrossFunctionalEffect
                                ::effectiveFullPatternContact
                )
                .toList();
    }

    public List<DecadeCrossFunctionalEffect>
    referencePatternContacts() {

        return crossFunctionalEffects
                .stream()
                .filter(
                        DecadeCrossFunctionalEffect
                                ::referencePatternContact
                )
                .toList();
    }

    public List<DecadeCrossFunctionalEffect>
    qualifiedContacts() {

        return crossFunctionalEffects
                .stream()
                .filter(
                        DecadeCrossFunctionalEffect
                                ::qualificationRequired
                )
                .toList();
    }
}