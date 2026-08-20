package com.dajeom.model;

import com.dajeom.type.NatalCrossFunctionalPatternType;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 하나의 세운 전체에 대한
 * Segment별 CrossFunctional Resolution.
 *
 * =================================================
 *
 * 교운:
 *
 * 하나의 AnnualLuckPeriod에
 * 여러 대운 Segment가 존재할 수 있으므로
 * Pattern Effect도 Segment별로 따로 보존한다.
 *
 * =================================================
 *
 * 서로 다른 Segment의 결과를
 * 평균내거나 다수결로 합치지 않는다.
 */
public record AnnualResolvedCrossFunctionalAnalysis(

        AnnualResolvedFunctionalAnalysis
        resolvedFunctionalAnalysis,

        List<AnnualResolvedCrossFunctionalSegmentAnalysis>
        segmentAnalyses,

        boolean completeChart

) {

    public AnnualResolvedCrossFunctionalAnalysis {

        Objects.requireNonNull(
                resolvedFunctionalAnalysis,
                "Annual CrossFunctional Analysis에는 AnnualResolvedFunctionalAnalysis가 필요합니다."
        );

        segmentAnalyses =
                segmentAnalyses == null
                        ? List.of()
                        : List.copyOf(
                        segmentAnalyses
                );

        if (
                resolvedFunctionalAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Annual Resolved Functional Analysis와 CrossFunctional Analysis의 completeChart가 일치하지 않습니다."
            );
        }

        if (
                !resolvedFunctionalAnalysis
                        .analysisAvailable()
        ) {

            if (
                    !segmentAnalyses
                            .isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "Annual Resolved Functional Analysis가 unavailable인데 CrossFunctional Segment가 존재합니다."
                );
            }

        } else {

            if (
                    segmentAnalyses.size()
                            != resolvedFunctionalAnalysis
                            .segmentAnalyses()
                            .size()
            ) {

                throw new IllegalArgumentException(
                        "Annual Resolved Functional Segment 수와 CrossFunctional Segment 수가 일치하지 않습니다."
                );
            }

            for (
                    AnnualResolvedFunctionalSegmentAnalysis resolvedSegment
                    : resolvedFunctionalAnalysis.segmentAnalyses()
            ) {

                long count =
                        segmentAnalyses
                                .stream()
                                .filter(
                                        segment ->
                                                segment
                                                        .resolvedFunctionalSegment()
                                                        == resolvedSegment
                                )
                                .count();

                if (
                        count != 1
                ) {

                    throw new IllegalArgumentException(
                            "각 Annual Resolved Functional Segment에는 정확히 하나의 CrossFunctional Segment가 필요합니다. "
                                    + "sequence="
                                    + resolvedSegment.decadeSequenceNumber()
                                    + ", count="
                                    + count
                    );
                }
            }

            for (
                    AnnualResolvedCrossFunctionalSegmentAnalysis segment
                    : segmentAnalyses
            ) {

                Objects.requireNonNull(
                        segment,
                        "AnnualResolvedCrossFunctionalSegmentAnalysis는 null일 수 없습니다."
                );

                if (
                        segment.completeChart()
                                != completeChart
                ) {

                    throw new IllegalArgumentException(
                            "Annual CrossFunctional Segment의 completeChart가 일치하지 않습니다."
                    );
                }

                if (
                        !resolvedFunctionalAnalysis
                                .segmentAnalyses()
                                .contains(
                                        segment
                                                .resolvedFunctionalSegment()
                                )
                ) {

                    throw new IllegalArgumentException(
                            "현재 Annual Resolved Functional Analysis에 없는 Segment가 CrossFunctional Analysis에 연결되었습니다."
                    );
                }
            }
        }
    }

    public boolean analysisAvailable() {

        return resolvedFunctionalAnalysis
                .analysisAvailable();
    }

    public Optional<String> unavailableReason() {

        return resolvedFunctionalAnalysis
                .unavailableReason();
    }

    public AnnualLuckAnalysis annualLuckAnalysis() {

        return resolvedFunctionalAnalysis
                .annualLuckAnalysis();
    }

    public int targetYear() {

        return resolvedFunctionalAnalysis
                .targetYear();
    }

    public AnnualLuckPeriod period() {

        return resolvedFunctionalAnalysis
                .period();
    }

    /*
     * =================================================
     * Segment
     * =================================================
     */

    public Optional<AnnualResolvedCrossFunctionalSegmentAnalysis>
    segmentOfDecade(
            int sequenceNumber
    ) {

        if (
                sequenceNumber < 1
                        || !analysisAvailable()
        ) {

            return Optional.empty();
        }

        return segmentAnalyses
                .stream()
                .filter(
                        segment ->
                                segment
                                        .decadeSequenceNumber()
                                        == sequenceNumber
                )
                .findFirst();
    }

    public AnnualResolvedCrossFunctionalSegmentAnalysis
    requireSegmentOfDecade(
            int sequenceNumber
    ) {

        return segmentOfDecade(
                sequenceNumber
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Annual CrossFunctional Segment를 찾을 수 없습니다: "
                                                + sequenceNumber
                                )
                );
    }

    /*
     * =================================================
     * 특정 시각
     * =================================================
     */

    public List<AnnualResolvedCrossFunctionalSegmentAnalysis>
    possibleSegmentsAt(
            ZonedDateTime dateTime
    ) {

        if (
                dateTime == null
                        || !analysisAvailable()
                        || !period()
                        .contains(
                                dateTime
                        )
        ) {

            return List.of();
        }

        return segmentAnalyses
                .stream()
                .filter(
                        segment ->
                                segment
                                        .overlap()
                                        .possiblyContains(
                                                dateTime
                                        )
                )
                .toList();
    }

    public Optional<AnnualResolvedCrossFunctionalSegmentAnalysis>
    definiteSegmentAt(
            ZonedDateTime dateTime
    ) {

        if (
                dateTime == null
                        || !analysisAvailable()
                        || !period()
                        .contains(
                                dateTime
                        )
        ) {

            return Optional.empty();
        }

        List<AnnualResolvedCrossFunctionalSegmentAnalysis> matched =
                segmentAnalyses
                        .stream()
                        .filter(
                                segment ->
                                        segment
                                                .overlap()
                                                .definitelyContains(
                                                        dateTime
                                                )
                        )
                        .toList();

        if (
                matched.size()
                        > 1
        ) {

            throw new IllegalStateException(
                    "동일 시각에 확정 Annual CrossFunctional Segment가 2개 이상입니다."
            );
        }

        return matched
                .stream()
                .findFirst();
    }

    /*
     * =================================================
     * Pattern across Segments
     * =================================================
     */

    public List<AnnualCrossFunctionalPatternEffect>
    effectsOf(
            NatalCrossFunctionalPatternType patternType
    ) {

        if (
                patternType == null
        ) {

            return List.of();
        }

        return segmentAnalyses
                .stream()
                .map(
                        segment ->
                                segment
                                        .requireEffect(
                                                patternType
                                        )
                )
                .toList();
    }

    public List<AnnualResolvedCrossFunctionalSegmentAnalysis>
    segmentsWithEffectiveFullContact(
            NatalCrossFunctionalPatternType patternType
    ) {

        if (
                patternType == null
        ) {

            return List.of();
        }

        return segmentAnalyses
                .stream()
                .filter(
                        segment ->
                                segment
                                        .requireEffect(
                                                patternType
                                        )
                                        .effectiveFullPatternContact()
                )
                .toList();
    }

    public List<AnnualResolvedCrossFunctionalSegmentAnalysis>
    segmentsWithEffectiveNewCompletion(
            NatalCrossFunctionalPatternType patternType
    ) {

        if (
                patternType == null
        ) {

            return List.of();
        }

        return segmentAnalyses
                .stream()
                .filter(
                        segment ->
                                segment
                                        .requireEffect(
                                                patternType
                                        )
                                        .effectiveNewCompletionCandidate()
                )
                .toList();
    }

    public List<AnnualResolvedCrossFunctionalSegmentAnalysis>
    qualifiedSegments() {

        return segmentAnalyses
                .stream()
                .filter(
                        segment ->
                                !segment
                                        .qualifiedEffects()
                                        .isEmpty()
                )
                .toList();
    }
}