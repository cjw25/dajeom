package com.dajeom.model;

import com.dajeom.type.NatalCrossFunctionalPatternType;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 하나의 Annual-Decade Segment에 대한
 * CrossFunctional Pattern Resolution Aggregate.
 *
 * =================================================
 *
 * 기반:
 *
 * AnnualResolvedFunctionalSegmentAnalysis
 *
 * =================================================
 *
 * Pattern Coverage:
 *
 * 해당 대운 Period의
 * DecadeCrossFunctionalSynthesis 전체.
 *
 * =================================================
 *
 * 원국 Pattern을 새로 생성하지 않는다.
 *
 * 대운 기준선에 보존된 동일 원국 Pattern들을
 * 현재 Annual Segment의 Function 상태와
 * 다시 접촉시키는 계층이다.
 */
public record AnnualResolvedCrossFunctionalSegmentAnalysis(

        AnnualResolvedFunctionalSegmentAnalysis
        resolvedFunctionalSegment,

        List<AnnualCrossFunctionalPatternEffect>
        patternEffects,

        boolean completeChart

) {

    public AnnualResolvedCrossFunctionalSegmentAnalysis {

        Objects.requireNonNull(
                resolvedFunctionalSegment,
                "Annual Resolved CrossFunctional Segment에는 Resolved Functional Segment가 필요합니다."
        );

        patternEffects =
                patternEffects == null
                        ? List.of()
                        : List.copyOf(
                        patternEffects
                );

        if (
                resolvedFunctionalSegment.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Resolved Functional Segment와 CrossFunctional Segment의 completeChart가 일치하지 않습니다."
            );
        }

        List<DecadeCrossFunctionalSynthesis> decadeSyntheses =
                resolvedFunctionalSegment
                        .sourceSegment()
                        .decadeBaseline()
                        .crossSyntheses();

        if (
                patternEffects.size()
                        != decadeSyntheses
                        .size()
        ) {

            throw new IllegalArgumentException(
                    "Decade CrossFunctional Synthesis 수와 Annual Pattern Effect 수가 일치하지 않습니다."
            );
        }

        Set<DecadeCrossFunctionalSynthesis> seen =
                new HashSet<>();

        for (
                AnnualCrossFunctionalPatternEffect effect
                : patternEffects
        ) {

            Objects.requireNonNull(
                    effect,
                    "AnnualCrossFunctionalPatternEffect는 null일 수 없습니다."
            );

            if (
                    effect.resolvedSegment()
                            != resolvedFunctionalSegment
            ) {

                throw new IllegalArgumentException(
                        "Annual CrossFunctional Pattern Effect가 현재 Resolved Segment를 참조하지 않습니다."
                );
            }

            if (
                    effect.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Annual CrossFunctional Pattern Effect의 completeChart가 일치하지 않습니다."
                );
            }

            if (
                    !decadeSyntheses
                            .contains(
                                    effect
                                            .decadeSynthesis()
                            )
            ) {

                throw new IllegalArgumentException(
                        "현재 대운 Period에 없는 Cross Synthesis가 Annual Segment에 연결되었습니다."
                );
            }

            if (
                    !seen.add(
                            effect
                                    .decadeSynthesis()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 Decade Cross Synthesis가 Annual Segment에 중복되었습니다: "
                                + effect.patternType()
                );
            }
        }

        if (
                !seen.equals(
                        new HashSet<>(
                                decadeSyntheses
                        )
                )
        ) {

            throw new IllegalArgumentException(
                    "Decade CrossFunctional Synthesis 일부가 Annual Segment에서 누락되었습니다."
            );
        }
    }

    public AnnualDecadeFunctionalSegment sourceSegment() {

        return resolvedFunctionalSegment
                .sourceSegment();
    }

    public int targetYear() {

        return resolvedFunctionalSegment
                .targetYear();
    }

    public int decadeSequenceNumber() {

        return resolvedFunctionalSegment
                .decadeSequenceNumber();
    }

    public AnnualDecadeLuckOverlap overlap() {

        return resolvedFunctionalSegment
                .overlap();
    }

    public boolean timingUncertain() {

        return resolvedFunctionalSegment
                .timingUncertain();
    }

    /*
     * =================================================
     * Pattern
     * =================================================
     */

    public Optional<AnnualCrossFunctionalPatternEffect>
    effectOf(
            NatalCrossFunctionalPatternType patternType
    ) {

        if (
                patternType == null
        ) {

            return Optional.empty();
        }

        return patternEffects
                .stream()
                .filter(
                        effect ->
                                effect
                                        .patternType()
                                        == patternType
                )
                .findFirst();
    }

    public AnnualCrossFunctionalPatternEffect
    requireEffect(
            NatalCrossFunctionalPatternType patternType
    ) {

        Objects.requireNonNull(
                patternType,
                "Annual CrossFunctional Pattern 조회에는 Pattern Type이 필요합니다."
        );

        return effectOf(
                patternType
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Annual CrossFunctional Pattern Effect를 찾을 수 없습니다: "
                                                + patternType
                                )
                );
    }

    /*
     * =================================================
     * Aggregate
     * =================================================
     */

    public List<AnnualCrossFunctionalPatternEffect>
    contactedEffects() {

        return patternEffects
                .stream()
                .filter(
                        AnnualCrossFunctionalPatternEffect
                                ::temporalContactExists
                )
                .toList();
    }

    public List<AnnualCrossFunctionalPatternEffect>
    partialContacts() {

        return patternEffects
                .stream()
                .filter(
                        AnnualCrossFunctionalPatternEffect
                                ::partialPatternContact
                )
                .toList();
    }

    public List<AnnualCrossFunctionalPatternEffect>
    fullContacts() {

        return patternEffects
                .stream()
                .filter(
                        AnnualCrossFunctionalPatternEffect
                                ::fullPatternContact
                )
                .toList();
    }

    public List<AnnualCrossFunctionalPatternEffect>
    effectiveFullContacts() {

        return patternEffects
                .stream()
                .filter(
                        AnnualCrossFunctionalPatternEffect
                                ::effectiveFullPatternContact
                )
                .toList();
    }

    public List<AnnualCrossFunctionalPatternEffect>
    decadeContinuations() {

        return patternEffects
                .stream()
                .filter(
                        AnnualCrossFunctionalPatternEffect
                                ::decadeFullContinuation
                )
                .toList();
    }

    public List<AnnualCrossFunctionalPatternEffect>
    newCompletionCandidates() {

        return patternEffects
                .stream()
                .filter(
                        AnnualCrossFunctionalPatternEffect
                                ::newlyCompleted
                )
                .toList();
    }

    public List<AnnualCrossFunctionalPatternEffect>
    effectiveNewCompletionCandidates() {

        return patternEffects
                .stream()
                .filter(
                        AnnualCrossFunctionalPatternEffect
                                ::effectiveNewCompletionCandidate
                )
                .toList();
    }

    public List<AnnualCrossFunctionalPatternEffect>
    transformationCompletions() {

        return patternEffects
                .stream()
                .filter(
                        AnnualCrossFunctionalPatternEffect
                                ::transformationCompletion
                )
                .toList();
    }

    public List<AnnualCrossFunctionalPatternEffect>
    referenceContacts() {

        return patternEffects
                .stream()
                .filter(
                        AnnualCrossFunctionalPatternEffect
                                ::referencePatternContact
                )
                .toList();
    }

    public List<AnnualCrossFunctionalPatternEffect>
    qualifiedEffects() {

        return patternEffects
                .stream()
                .filter(
                        AnnualCrossFunctionalPatternEffect
                                ::qualificationRequired
                )
                .toList();
    }
}