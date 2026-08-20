package com.dajeom.model;

import com.dajeom.type.NatalTenGodFunctionType;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 하나의 세운에 대한
 * 대운 Segment별 Functional Aggregate.
 *
 * =================================================
 *
 * 이 객체는:
 *
 * Annual Functional 자체
 *
 * 를 절대로 버리지 않는다.
 *
 * 대운 Sequence가 불확실하더라도
 * annualFunctionalCapacityAnalysis는
 * 그대로 보존한다.
 *
 * =================================================
 *
 * decadeSegmentAvailable=false
 *
 * 이면:
 *
 * 세운 자체 Functional 분석은 사용 가능하지만
 * 특정 대운과 결합한 Segment 분석만
 * 사용할 수 없다는 뜻이다.
 */
public record AnnualDecadeFunctionalAnalysis(

        DecadeFunctionalSynthesisAnalysis
        decadeFunctionalSynthesisAnalysis,

        AnnualFunctionalCapacityAnalysis
        annualFunctionalCapacityAnalysis,

        List<AnnualDecadeFunctionalSegment>
        segments,

        boolean decadeSegmentAvailable,

        String unavailableReason,

        boolean completeChart

) {

    public AnnualDecadeFunctionalAnalysis {

        Objects.requireNonNull(
                decadeFunctionalSynthesisAnalysis,
                "Annual-Decade Functional Analysis에는 Decade Functional Synthesis가 필요합니다."
        );

        Objects.requireNonNull(
                annualFunctionalCapacityAnalysis,
                "Annual-Decade Functional Analysis에는 Annual Functional Capacity가 필요합니다."
        );

        segments =
                segments == null
                        ? List.of()
                        : List.copyOf(
                        segments
                );

        unavailableReason =
                unavailableReason == null
                        ? ""
                        : unavailableReason.trim();

        if (
                decadeFunctionalSynthesisAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Decade Functional Synthesis와 Annual-Decade Analysis의 completeChart가 일치하지 않습니다."
            );
        }

        if (
                annualFunctionalCapacityAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Annual Functional Capacity와 Annual-Decade Analysis의 completeChart가 일치하지 않습니다."
            );
        }

        AnnualLuckAnalysis annualLuckAnalysis =
                annualFunctionalCapacityAnalysis
                        .projectionAnalysis()
                        .annualLuckAnalysis();

        AnnualDecadeLuckInteractionAnalysis interactionAnalysis =
                annualLuckAnalysis
                        .decadeInteractionAnalysis();

        if (
                decadeSegmentAvailable
        ) {

            if (
                    !interactionAnalysis.analysisAvailable()
            ) {

                throw new IllegalArgumentException(
                        "14-5 Interaction이 unavailable인데 Annual-Decade Functional Segment를 활성화할 수 없습니다."
                );
            }

            if (
                    !decadeFunctionalSynthesisAnalysis
                            .analysisAvailable()
            ) {

                throw new IllegalArgumentException(
                        "Decade Functional Synthesis가 unavailable인데 Annual-Decade Functional Segment를 활성화할 수 없습니다."
                );
            }

            if (
                    !annualLuckAnalysis
                            .decadeContextAvailable()
            ) {

                throw new IllegalArgumentException(
                        "14-6 Decade Context가 unavailable인데 Annual-Decade Functional Segment를 활성화할 수 없습니다."
                );
            }

            if (
                    !unavailableReason
                            .isBlank()
            ) {

                throw new IllegalArgumentException(
                        "사용 가능한 Annual-Decade Functional Analysis에는 unavailableReason이 없어야 합니다."
                );
            }

            if (
                    segments.size()
                            != interactionAnalysis
                            .interactions()
                            .size()
            ) {

                throw new IllegalArgumentException(
                        "14-5 Interaction 수와 Annual-Decade Functional Segment 수가 일치하지 않습니다."
                );
            }

            Set<AnnualDecadeLuckInteraction> seen =
                    new HashSet<>();

            for (
                    AnnualDecadeFunctionalSegment segment
                    : segments
            ) {

                Objects.requireNonNull(
                        segment,
                        "AnnualDecadeFunctionalSegment는 null일 수 없습니다."
                );

                if (
                        segment.completeChart()
                                != completeChart
                ) {

                    throw new IllegalArgumentException(
                            "Annual-Decade Functional Segment의 completeChart가 일치하지 않습니다."
                    );
                }

                if (
                        !interactionAnalysis
                                .interactions()
                                .contains(
                                        segment.interaction()
                                )
                ) {

                    throw new IllegalArgumentException(
                            "14-5에 존재하지 않는 Interaction이 Functional Segment에 연결되었습니다."
                    );
                }

                if (
                        !seen.add(
                                segment.interaction()
                        )
                ) {

                    throw new IllegalArgumentException(
                            "동일 14-5 Interaction이 Functional Segment에 중복되었습니다."
                    );
                }
            }

        } else {

            if (
                    !segments
                            .isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "대운 Segment 분석 불가 상태에서는 Functional Segment가 존재할 수 없습니다."
                );
            }

            if (
                    unavailableReason
                            .isBlank()
            ) {

                throw new IllegalArgumentException(
                        "Annual-Decade Functional Analysis 불가 사유가 필요합니다."
                );
            }
        }
    }

    public AnnualLuckAnalysis annualLuckAnalysis() {

        return annualFunctionalCapacityAnalysis
                .projectionAnalysis()
                .annualLuckAnalysis();
    }

    public int targetYear() {

        return annualFunctionalCapacityAnalysis
                .targetYear();
    }

    public AnnualLuckPeriod period() {

        return annualFunctionalCapacityAnalysis
                .period();
    }

    public boolean analysisAvailable() {

        return decadeSegmentAvailable;
    }

    public Optional<String>
    unavailableReasonOptional() {

        if (
                decadeSegmentAvailable
                        || unavailableReason.isBlank()
        ) {

            return Optional.empty();
        }

        return Optional.of(
                unavailableReason
        );
    }

    /*
     * =================================================
     * Segment
     * =================================================
     */

    public Optional<AnnualDecadeFunctionalSegment>
    segmentOfDecade(
            int sequenceNumber
    ) {

        if (
                sequenceNumber < 1
                        || !decadeSegmentAvailable
        ) {

            return Optional.empty();
        }

        return segments
                .stream()
                .filter(
                        segment ->
                                segment
                                        .decadeSequenceNumber()
                                        == sequenceNumber
                )
                .findFirst();
    }

    public AnnualDecadeFunctionalSegment
    requireSegmentOfDecade(
            int sequenceNumber
    ) {

        return segmentOfDecade(
                sequenceNumber
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Annual-Decade Functional Segment를 찾을 수 없습니다: "
                                                + sequenceNumber
                                )
                );
    }

    /*
     * =================================================
     * 특정 시각
     * =================================================
     */

    public List<AnnualDecadeFunctionalSegment>
    possibleSegmentsAt(
            ZonedDateTime dateTime
    ) {

        if (
                dateTime == null
                        || !decadeSegmentAvailable
                        || !period()
                        .contains(
                                dateTime
                        )
        ) {

            return List.of();
        }

        return segments
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

    public Optional<AnnualDecadeFunctionalSegment>
    definiteSegmentAt(
            ZonedDateTime dateTime
    ) {

        if (
                dateTime == null
                        || !decadeSegmentAvailable
                        || !period()
                        .contains(
                                dateTime
                        )
        ) {

            return Optional.empty();
        }

        List<AnnualDecadeFunctionalSegment> matched =
                segments
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
                    "동일 시각에 확정 Annual-Decade Functional Segment가 2개 이상입니다."
            );
        }

        return matched
                .stream()
                .findFirst();
    }

    /*
     * =================================================
     * Aggregate
     * =================================================
     */

    public boolean multipleDecadesInAnnualPeriod() {

        return segments.size()
                > 1;
    }

    public boolean timingUncertain() {

        return segments
                .stream()
                .anyMatch(
                        AnnualDecadeFunctionalSegment
                                ::timingUncertain
                );
    }

    public boolean beforeFirstDecadeLuck() {

        return annualLuckAnalysis()
                .beforeFirstDecadeLuck();
    }

    public boolean afterGeneratedDecadeRange() {

        return annualLuckAnalysis()
                .afterGeneratedDecadeRange();
    }

    public List<AnnualDecadeFunctionalSegment>
    segmentsWithContinuation(
            NatalTenGodFunctionType functionType
    ) {

        if (
                functionType == null
        ) {

            return List.of();
        }

        return segments
                .stream()
                .filter(
                        segment ->
                                segment
                                        .requireFunctionEffect(
                                                functionType
                                        )
                                        .continuationContact()
                )
                .toList();
    }

    public List<AnnualDecadeFunctionalSegment>
    segmentsWithAnnualNewActivation(
            NatalTenGodFunctionType functionType
    ) {

        if (
                functionType == null
        ) {

            return List.of();
        }

        return segments
                .stream()
                .filter(
                        segment ->
                                segment
                                        .requireFunctionEffect(
                                                functionType
                                        )
                                        .annualNewActivation()
                )
                .toList();
    }

    public List<AnnualDecadeFunctionalSegment>
    qualifiedSegments() {

        return segments
                .stream()
                .filter(
                        AnnualDecadeFunctionalSegment
                                ::qualificationRequired
                )
                .toList();
    }
}