package com.dajeom.model;

import com.dajeom.type.NatalTenGodFunctionType;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 하나의 세운 전체에 대한
 * Segment별 최종 Functional Analysis.
 *
 * =================================================
 *
 * 교운이 없으면 일반적으로 Segment 1개.
 *
 * 교운이 세운 중간에 존재하면
 * Segment 2개 이상 가능.
 *
 * =================================================
 *
 * 출생시간 미상으로 교운시점이 불확실하면
 * possible / definite overlap을 그대로 보존한다.
 *
 * =================================================
 *
 * 대운 Segment가 unavailable이어도
 * upstream Annual Functional 자체는
 * AnnualDecadeTransformationAnalysis를 통해
 * 계속 추적 가능하다.
 */
public record AnnualResolvedFunctionalAnalysis(

        AnnualDecadeTransformationAnalysis
        transformationAnalysis,

        List<AnnualResolvedFunctionalSegmentAnalysis>
        segmentAnalyses,

        boolean completeChart

) {

    public AnnualResolvedFunctionalAnalysis {

        Objects.requireNonNull(
                transformationAnalysis,
                "Annual Resolved Functional Analysis에는 Transformation Analysis가 필요합니다."
        );

        segmentAnalyses =
                segmentAnalyses == null
                        ? List.of()
                        : List.copyOf(
                        segmentAnalyses
                );

        if (
                transformationAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Transformation Analysis와 Annual Resolved Functional Analysis의 completeChart가 일치하지 않습니다."
            );
        }

        if (
                !transformationAnalysis
                        .analysisAvailable()
        ) {

            if (
                    !segmentAnalyses
                            .isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "Annual-Decade Transformation Analysis가 unavailable인데 Resolved Segment가 존재합니다."
                );
            }

        } else {

            if (
                    segmentAnalyses.size()
                            != transformationAnalysis
                            .segmentResolutions()
                            .size()
            ) {

                throw new IllegalArgumentException(
                        "Transformation Segment 수와 Annual Resolved Segment 수가 일치하지 않습니다."
                );
            }

            for (
                    AnnualDecadeTransformationResolution resolution
                    : transformationAnalysis
                    .segmentResolutions()
            ) {

                long count =
                        segmentAnalyses
                                .stream()
                                .filter(
                                        segment ->
                                                segment
                                                        .transformationResolution()
                                                        == resolution
                                )
                                .count();

                if (
                        count != 1
                ) {

                    throw new IllegalArgumentException(
                            "각 Transformation Resolution에는 정확히 하나의 Annual Resolved Segment가 필요합니다. "
                                    + "sequence="
                                    + resolution.decadeSequenceNumber()
                                    + ", count="
                                    + count
                    );
                }
            }

            for (
                    AnnualResolvedFunctionalSegmentAnalysis segment
                    : segmentAnalyses
            ) {

                Objects.requireNonNull(
                        segment,
                        "AnnualResolvedFunctionalSegmentAnalysis는 null일 수 없습니다."
                );

                if (
                        segment.completeChart()
                                != completeChart
                ) {

                    throw new IllegalArgumentException(
                            "Annual Resolved Segment의 completeChart가 일치하지 않습니다."
                    );
                }

                if (
                        !transformationAnalysis
                                .segmentResolutions()
                                .contains(
                                        segment
                                                .transformationResolution()
                                )
                ) {

                    throw new IllegalArgumentException(
                            "현재 Transformation Analysis에 없는 Resolution이 Annual Resolved Segment에 연결되었습니다."
                    );
                }
            }
        }
    }

    public boolean analysisAvailable() {

        return transformationAnalysis
                .analysisAvailable();
    }

    public Optional<String> unavailableReason() {

        return transformationAnalysis
                .unavailableReason();
    }

    public AnnualLuckAnalysis annualLuckAnalysis() {

        return transformationAnalysis
                .annualLuckAnalysis();
    }

    public int targetYear() {

        return transformationAnalysis
                .targetYear();
    }

    public AnnualLuckPeriod period() {

        return transformationAnalysis
                .period();
    }

    /*
     * =================================================
     * Segment
     * =================================================
     */

    public Optional<AnnualResolvedFunctionalSegmentAnalysis>
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

    public AnnualResolvedFunctionalSegmentAnalysis
    requireSegmentOfDecade(
            int sequenceNumber
    ) {

        return segmentOfDecade(
                sequenceNumber
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Annual Resolved Functional Segment를 찾을 수 없습니다: "
                                                + sequenceNumber
                                )
                );
    }

    /*
     * =================================================
     * 특정 시각
     * =================================================
     */

    public List<AnnualResolvedFunctionalSegmentAnalysis>
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

    public Optional<AnnualResolvedFunctionalSegmentAnalysis>
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

        List<AnnualResolvedFunctionalSegmentAnalysis> matched =
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
                    "동일 시각에 확정 Annual Resolved Functional Segment가 2개 이상입니다."
            );
        }

        return matched
                .stream()
                .findFirst();
    }

    /*
     * =================================================
     * Function across segments
     * =================================================
     */

    public List<AnnualResolvedFunctionEffect>
    effectsOf(
            NatalTenGodFunctionType functionType
    ) {

        if (
                functionType == null
        ) {

            return List.of();
        }

        return segmentAnalyses
                .stream()
                .map(
                        segment ->
                                segment
                                        .requireEffect(
                                                functionType
                                        )
                )
                .toList();
    }

    public List<AnnualResolvedFunctionalSegmentAnalysis>
    segmentsWithActiveFunction(
            NatalTenGodFunctionType functionType
    ) {

        if (
                functionType == null
        ) {

            return List.of();
        }

        return segmentAnalyses
                .stream()
                .filter(
                        segment ->
                                segment
                                        .requireEffect(
                                                functionType
                                        )
                                        .active()
                )
                .toList();
    }

    public List<AnnualResolvedFunctionalSegmentAnalysis>
    segmentsWithCrossLuckTarget(
            NatalTenGodFunctionType functionType
    ) {

        if (
                functionType == null
        ) {

            return List.of();
        }

        return segmentAnalyses
                .stream()
                .filter(
                        segment ->
                                segment
                                        .requireEffect(
                                                functionType
                                        )
                                        .crossLuckTargetActive()
                )
                .toList();
    }

    public List<AnnualResolvedFunctionalSegmentAnalysis>
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