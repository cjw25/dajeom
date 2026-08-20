package com.dajeom.model;

import com.dajeom.type.NatalCrossFunctionalPatternType;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 하나의 Annual-Decade Segment에 대한
 * 최종 CrossFunctional + 14-6 Context Synthesis.
 *
 * =================================================
 *
 * Source:
 *
 * AnnualResolvedCrossFunctionalSegmentAnalysis
 *
 * =================================================
 *
 * Pattern별:
 *
 * AnnualCrossFunctionalSynthesis
 *
 * =================================================
 *
 * tenGodTypes가 비어 있는 Context는
 * 특정 Pattern에 강제로 연결하지 않는다.
 *
 * 원본 AnnualLuckDecadeContextAnalysis에
 * Global Context로 그대로 보존한다.
 *
 * =================================================
 * Identity Provenance
 * =================================================
 *
 * sourceSegmentAnalysis가 보유한
 * AnnualCrossFunctionalPatternEffect와
 * Synthesis가 참조하는 sourceEffect의 관계는
 * 값 동등성(equals/hashCode)이 아니라
 * 객체 Identity로 검증한다.
 *
 * AnnualCrossFunctionalPatternEffect는
 * 원국 -> 대운 -> 세운 분석 객체 그래프를
 * 구조적으로 연결하고 있으므로
 * HashSet/contains 기반의 구조적 hashCode/equals를
 * provenance 검증에 사용하지 않는다.
 *
 * 이 Segment의 Synthesis는
 * 반드시 sourceSegmentAnalysis.patternEffects()가
 * 실제 보유한 동일 객체를 참조해야 한다.
 */
public record AnnualCrossFunctionalSynthesisSegmentAnalysis(

        AnnualResolvedCrossFunctionalSegmentAnalysis
        sourceSegmentAnalysis,

        List<AnnualCrossFunctionalSynthesis>
        syntheses,

        boolean completeChart

) {

    public AnnualCrossFunctionalSynthesisSegmentAnalysis {

        Objects.requireNonNull(
                sourceSegmentAnalysis,
                "Annual CrossFunctional Synthesis Segment에는 Source CrossFunctional Segment가 필요합니다."
        );

        syntheses =
                syntheses == null
                        ? List.of()
                        : List.copyOf(
                        syntheses
                );

        if (
                sourceSegmentAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Source CrossFunctional Segment와 Synthesis Segment의 completeChart가 일치하지 않습니다."
            );
        }

        List<AnnualCrossFunctionalPatternEffect>
                sourcePatternEffects =
                sourceSegmentAnalysis
                        .patternEffects();

        if (
                syntheses.size()
                        != sourcePatternEffects
                        .size()
        ) {

            throw new IllegalArgumentException(
                    "Annual CrossFunctional Pattern Effect 수와 Synthesis 수가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Identity 기반 중복 검증
         * =================================================
         *
         * HashSet을 사용하지 않는다.
         *
         * AnnualCrossFunctionalPatternEffect의
         * 구조적 hashCode/equals를 호출하지 않고
         * 실제 Source 객체 Identity만 추적한다.
         */

        Set<AnnualCrossFunctionalPatternEffect> seen =
                Collections.newSetFromMap(
                        new IdentityHashMap<>()
                );

        for (
                AnnualCrossFunctionalSynthesis synthesis
                : syntheses
        ) {

            Objects.requireNonNull(
                    synthesis,
                    "AnnualCrossFunctionalSynthesis는 null일 수 없습니다."
            );

            if (
                    synthesis.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Annual CrossFunctional Synthesis의 completeChart가 일치하지 않습니다."
                );
            }

            AnnualCrossFunctionalPatternEffect
                    sourceEffect =
                    Objects.requireNonNull(
                            synthesis.sourceEffect(),
                            "Annual CrossFunctional Synthesis에는 Source Pattern Effect가 필요합니다."
                    );

            /*
             * 현재 Source Segment가 실제 보유한
             * Pattern Effect 객체인지 Identity로 검증한다.
             */

            if (
                    !containsIdentity(
                            sourcePatternEffects,
                            sourceEffect
                    )
            ) {

                throw new IllegalArgumentException(
                        "현재 Annual CrossFunctional Segment에 없는 Pattern Effect가 Synthesis에 연결되었습니다."
                );
            }

            /*
             * 동일 객체가 두 개 이상의 Synthesis에
             * 중복 연결되는 것을 금지한다.
             */

            if (
                    !seen.add(
                            sourceEffect
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 Annual CrossFunctional Pattern Effect가 Synthesis에 중복되었습니다: "
                                + synthesis.patternType()
                );
            }
        }

        /*
         * =================================================
         * 누락 검증
         * =================================================
         *
         * syntheses.size()와 sourcePatternEffects.size()는
         * 이미 동일함을 검증했다.
         *
         * 추가로 모든 Source Pattern Effect 객체가
         * Identity Set에 실제 존재하는지 확인한다.
         *
         * HashSet 복사/equals는 사용하지 않는다.
         */

        for (
                AnnualCrossFunctionalPatternEffect sourceEffect
                : sourcePatternEffects
        ) {

            if (
                    !seen.contains(
                            sourceEffect
                    )
            ) {

                throw new IllegalArgumentException(
                        "Annual CrossFunctional Pattern Effect 일부가 Synthesis에서 누락되었습니다."
                );
            }
        }
    }

    /*
     * =================================================
     * Identity
     * =================================================
     */

    public AnnualDecadeFunctionalSegment sourceSegment() {

        return sourceSegmentAnalysis
                .sourceSegment();
    }

    public int targetYear() {

        return sourceSegmentAnalysis
                .targetYear();
    }

    public int decadeSequenceNumber() {

        return sourceSegmentAnalysis
                .decadeSequenceNumber();
    }

    public AnnualDecadeLuckOverlap overlap() {

        return sourceSegmentAnalysis
                .overlap();
    }

    public boolean timingUncertain() {

        return sourceSegmentAnalysis
                .timingUncertain();
    }

    /*
     * =================================================
     * Synthesis
     * =================================================
     */

    public Optional<AnnualCrossFunctionalSynthesis>
    synthesisOf(
            NatalCrossFunctionalPatternType patternType
    ) {

        if (
                patternType == null
        ) {

            return Optional.empty();
        }

        return syntheses
                .stream()
                .filter(
                        synthesis ->
                                synthesis
                                        .patternType()
                                        == patternType
                )
                .findFirst();
    }

    public AnnualCrossFunctionalSynthesis
    requireSynthesis(
            NatalCrossFunctionalPatternType patternType
    ) {

        Objects.requireNonNull(
                patternType,
                "Annual CrossFunctional Synthesis 조회에는 Pattern Type이 필요합니다."
        );

        return synthesisOf(
                patternType
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Annual CrossFunctional Synthesis를 찾을 수 없습니다: "
                                                + patternType
                                )
                );
    }

    /*
     * =================================================
     * Pattern Aggregate
     * =================================================
     */

    public List<AnnualCrossFunctionalSynthesis>
    contactedSyntheses() {

        return syntheses
                .stream()
                .filter(
                        AnnualCrossFunctionalSynthesis
                                ::temporalContactExists
                )
                .toList();
    }

    public List<AnnualCrossFunctionalSynthesis>
    effectiveFullContacts() {

        return syntheses
                .stream()
                .filter(
                        AnnualCrossFunctionalSynthesis
                                ::usableAsEffectiveAnnualPatternContact
                )
                .toList();
    }

    public List<AnnualCrossFunctionalSynthesis>
    newCompletionCandidates() {

        return syntheses
                .stream()
                .filter(
                        AnnualCrossFunctionalSynthesis
                                ::newlyCompleted
                )
                .toList();
    }

    public List<AnnualCrossFunctionalSynthesis>
    effectiveNewCompletionCandidates() {

        return syntheses
                .stream()
                .filter(
                        AnnualCrossFunctionalSynthesis
                                ::effectiveNewCompletionCandidate
                )
                .toList();
    }

    public List<AnnualCrossFunctionalSynthesis>
    decadeContinuations() {

        return syntheses
                .stream()
                .filter(
                        AnnualCrossFunctionalSynthesis
                                ::decadeFullContinuation
                )
                .toList();
    }

    public List<AnnualCrossFunctionalSynthesis>
    transformationCompletions() {

        return syntheses
                .stream()
                .filter(
                        AnnualCrossFunctionalSynthesis
                                ::transformationCompletion
                )
                .toList();
    }

    public List<AnnualCrossFunctionalSynthesis>
    effectiveFullContactsWithExactContext() {

        return syntheses
                .stream()
                .filter(
                        AnnualCrossFunctionalSynthesis
                                ::effectiveFullContactWithExactContext
                )
                .toList();
    }

    public List<AnnualCrossFunctionalSynthesis>
    effectiveNewCompletionsWithExactContext() {

        return syntheses
                .stream()
                .filter(
                        AnnualCrossFunctionalSynthesis
                                ::effectiveNewCompletionWithExactContext
                )
                .toList();
    }

    public List<AnnualCrossFunctionalSynthesis>
    referenceContacts() {

        return syntheses
                .stream()
                .filter(
                        AnnualCrossFunctionalSynthesis
                                ::referenceOnly
                )
                .toList();
    }

    public List<AnnualCrossFunctionalSynthesis>
    qualifiedSyntheses() {

        return syntheses
                .stream()
                .filter(
                        AnnualCrossFunctionalSynthesis
                                ::qualificationRequired
                )
                .toList();
    }

    /*
     * =================================================
     * 14-6 Context
     * =================================================
     */

    public AnnualLuckDecadeContextAnalysis contextAnalysis() {

        return sourceSegment()
                .annualDecadeContext();
    }

    public List<AnnualLuckContextSignal>
    allContextSignals() {

        return contextAnalysis()
                .signals();
    }

    /**
     * tenGodTypes가 비어 있어
     * 특정 Cross Pattern에 구조적으로
     * 연결하지 않은 Context.
     */
    public List<AnnualLuckContextSignal>
    globalContextSignals() {

        return allContextSignals()
                .stream()
                .filter(
                        signal ->
                                signal
                                        .tenGodTypes()
                                        .isEmpty()
                )
                .toList();
    }

    /**
     * 하나 이상의 Cross Pattern과
     * TenGod 구조 Match가 있었던 Signal.
     *
     * 같은 Signal이 여러 Pattern과 겹쳐도
     * 여기서는 한 번만 반환한다.
     *
     * Context Match는 upstream Context Analysis가
     * 실제 보유한 Signal 객체를 그대로 참조하므로
     * Identity 기준으로 중복을 제거한다.
     */
    public List<AnnualLuckContextSignal>
    matchedContextSignals() {

        Set<AnnualLuckContextSignal> result =
                Collections.newSetFromMap(
                        new IdentityHashMap<>()
                );

        List<AnnualLuckContextSignal> ordered =
                new java.util.ArrayList<>();

        for (
                AnnualCrossFunctionalSynthesis synthesis
                : syntheses
        ) {

            for (
                    AnnualCrossContextMatch match
                    : synthesis.contextMatches()
            ) {

                AnnualLuckContextSignal signal =
                        match.contextSignal();

                if (
                        result.add(
                                signal
                        )
                ) {

                    ordered.add(
                            signal
                    );
                }
            }
        }

        return List.copyOf(
                ordered
        );
    }

    /**
     * tenGodTypes는 존재하지만
     * 현재 원국 Cross Pattern 어디에도
     * 구조적으로 겹치지 않은 Context.
     *
     * 삭제하지 않고 Context 자체로 보존한다.
     */
    public List<AnnualLuckContextSignal>
    unmatchedStructuredContextSignals() {

        Set<AnnualLuckContextSignal> matched =
                Collections.newSetFromMap(
                        new IdentityHashMap<>()
                );

        matched.addAll(
                matchedContextSignals()
        );

        return allContextSignals()
                .stream()
                .filter(
                        signal ->
                                !signal
                                        .tenGodTypes()
                                        .isEmpty()
                )
                .filter(
                        signal ->
                                !matched.contains(
                                        signal
                                )
                )
                .toList();
    }

    public boolean mixedContext() {

        return contextAnalysis()
                .mixedContext();
    }

    /*
     * =================================================
     * Identity Utility
     * =================================================
     */

    private static boolean containsIdentity(

            List<AnnualCrossFunctionalPatternEffect> effects,

            AnnualCrossFunctionalPatternEffect target

    ) {

        Objects.requireNonNull(
                effects,
                "Annual CrossFunctional Pattern Effect 목록은 null일 수 없습니다."
        );

        Objects.requireNonNull(
                target,
                "Annual CrossFunctional Pattern Effect Identity 조회 대상은 null일 수 없습니다."
        );

        for (
                AnnualCrossFunctionalPatternEffect effect
                : effects
        ) {

            if (
                    effect
                            == target
            ) {

                return true;
            }
        }

        return false;
    }
}