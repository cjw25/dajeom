package com.dajeom.model;

import com.dajeom.type.NatalCrossFunctionalPatternType;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 하나의 세운 전체에 대한
 * Segment별 최종 CrossFunctional Synthesis.
 *
 * =================================================
 *
 * 구성:
 *
 * AnnualResolvedCrossFunctionalAnalysis
 *
 * +
 *
 * 14-6 Context Match
 *
 * =================================================
 *
 * 교운 중 여러 Segment가 있으면
 * 각각 별도 Synthesis를 유지한다.
 *
 * 서로 다른 대운 Segment 결과를
 * 평균내거나 다수결로 통합하지 않는다.
 */
public record AnnualCrossFunctionalSynthesisAnalysis(

        AnnualResolvedCrossFunctionalAnalysis
        sourceAnalysis,

        List<AnnualCrossFunctionalSynthesisSegmentAnalysis>
        segmentAnalyses,

        boolean completeChart

) {

    public AnnualCrossFunctionalSynthesisAnalysis {

        Objects.requireNonNull(
                sourceAnalysis,
                "Annual CrossFunctional Synthesis Analysis에는 Source Analysis가 필요합니다."
        );

        segmentAnalyses =
                segmentAnalyses == null
                        ? List.of()
                        : List.copyOf(
                        segmentAnalyses
                );

        if (
                sourceAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Annual Resolved CrossFunctional Analysis와 Synthesis Analysis의 completeChart가 일치하지 않습니다."
            );
        }

        if (
                !sourceAnalysis
                        .analysisAvailable()
        ) {

            if (
                    !segmentAnalyses
                            .isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "Annual CrossFunctional Source가 unavailable인데 Synthesis Segment가 존재합니다."
                );
            }

        } else {

            if (
                    segmentAnalyses.size()
                            != sourceAnalysis
                            .segmentAnalyses()
                            .size()
            ) {

                throw new IllegalArgumentException(
                        "Annual CrossFunctional Segment 수와 Synthesis Segment 수가 일치하지 않습니다."
                );
            }

            for (
                    AnnualResolvedCrossFunctionalSegmentAnalysis sourceSegment
                    : sourceAnalysis.segmentAnalyses()
            ) {

                long count =
                        segmentAnalyses
                                .stream()
                                .filter(
                                        segment ->
                                                segment
                                                        .sourceSegmentAnalysis()
                                                        == sourceSegment
                                )
                                .count();

                if (
                        count != 1
                ) {

                    throw new IllegalArgumentException(
                            "각 Annual CrossFunctional Segment에는 정확히 하나의 Synthesis Segment가 필요합니다. "
                                    + "sequence="
                                    + sourceSegment.decadeSequenceNumber()
                                    + ", count="
                                    + count
                    );
                }
            }

            for (
                    AnnualCrossFunctionalSynthesisSegmentAnalysis segment
                    : segmentAnalyses
            ) {

                Objects.requireNonNull(
                        segment,
                        "AnnualCrossFunctionalSynthesisSegmentAnalysis는 null일 수 없습니다."
                );

                if (
                        segment.completeChart()
                                != completeChart
                ) {

                    throw new IllegalArgumentException(
                            "Annual CrossFunctional Synthesis Segment의 completeChart가 일치하지 않습니다."
                    );
                }

                if (
                        !sourceAnalysis
                                .segmentAnalyses()
                                .contains(
                                        segment
                                                .sourceSegmentAnalysis()
                                )
                ) {

                    throw new IllegalArgumentException(
                            "현재 Annual CrossFunctional Source에 없는 Segment가 Synthesis에 연결되었습니다."
                    );
                }
            }
        }
    }

    public boolean analysisAvailable() {

        return sourceAnalysis
                .analysisAvailable();
    }

    public Optional<String> unavailableReason() {

        return sourceAnalysis
                .unavailableReason();
    }

    public AnnualLuckAnalysis annualLuckAnalysis() {

        return sourceAnalysis
                .annualLuckAnalysis();
    }

    public int targetYear() {

        return sourceAnalysis
                .targetYear();
    }

    public AnnualLuckPeriod period() {

        return sourceAnalysis
                .period();
    }

    /*
     * =================================================
     * Segment
     * =================================================
     */

    public Optional<AnnualCrossFunctionalSynthesisSegmentAnalysis>
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

    public AnnualCrossFunctionalSynthesisSegmentAnalysis
    requireSegmentOfDecade(
            int sequenceNumber
    ) {

        return segmentOfDecade(
                sequenceNumber
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Annual CrossFunctional Synthesis Segment를 찾을 수 없습니다: "
                                                + sequenceNumber
                                )
                );
    }

    /*
     * =================================================
     * 특정 시각
     * =================================================
     */

    public List<AnnualCrossFunctionalSynthesisSegmentAnalysis>
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

    public Optional<AnnualCrossFunctionalSynthesisSegmentAnalysis>
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

        List<AnnualCrossFunctionalSynthesisSegmentAnalysis> matched =
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
                    "동일 시각에 확정 Annual CrossFunctional Synthesis Segment가 2개 이상입니다."
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

    public List<AnnualCrossFunctionalSynthesis>
    synthesesOf(
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
                                        .requireSynthesis(
                                                patternType
                                        )
                )
                .toList();
    }

    public List<AnnualCrossFunctionalSynthesisSegmentAnalysis>
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
                                        .requireSynthesis(
                                                patternType
                                        )
                                        .usableAsEffectiveAnnualPatternContact()
                )
                .toList();
    }

    public List<AnnualCrossFunctionalSynthesisSegmentAnalysis>
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
                                        .requireSynthesis(
                                                patternType
                                        )
                                        .effectiveNewCompletionCandidate()
                )
                .toList();
    }

    public List<AnnualCrossFunctionalSynthesisSegmentAnalysis>
    segmentsWithExactContext(
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
                                        .requireSynthesis(
                                                patternType
                                        )
                                        .exactTenGodContextMatchExists()
                )
                .toList();
    }

    public List<AnnualCrossFunctionalSynthesisSegmentAnalysis>
    qualifiedSegments() {

        return segmentAnalyses
                .stream()
                .filter(
                        segment ->
                                !segment
                                        .qualifiedSyntheses()
                                        .isEmpty()
                )
                .toList();
    }
}