package com.dajeom.model;

import com.dajeom.type.NatalTenGodFunctionType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 하나의 세운 전체에 대한
 * 대운 Segment별 14-5 Transformation
 * Source Participant Resolution Aggregate.
 *
 * =================================================
 *
 * AnnualDecadeFunctionalAnalysis
 *
 * 의 Segment Coverage를 그대로 따른다.
 *
 * =================================================
 *
 * 대운 Segment가 unavailable이면
 * 세운 자체 Functional 분석은 그대로 보존되고,
 * 이 Aggregate의 Segment Resolution만 비어 있다.
 */
public record AnnualDecadeTransformationAnalysis(

        AnnualDecadeFunctionalAnalysis
        functionalAnalysis,

        List<AnnualDecadeTransformationResolution>
        segmentResolutions,

        boolean completeChart

) {

    public AnnualDecadeTransformationAnalysis {

        Objects.requireNonNull(
                functionalAnalysis,
                "Annual-Decade Transformation Analysis에는 Functional Analysis가 필요합니다."
        );

        segmentResolutions =
                segmentResolutions == null
                        ? List.of()
                        : List.copyOf(
                        segmentResolutions
                );

        if (
                functionalAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Annual-Decade Functional Analysis와 Transformation Analysis의 completeChart가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Functional Analysis unavailable
         * =================================================
         *
         * compact constructor에서는 return을 사용할 수 없다.
         *
         * 따라서 unavailable / available 검증을
         * if / else로 명확하게 분리한다.
         */

        if (
                !functionalAnalysis
                        .analysisAvailable()
        ) {

            if (
                    !segmentResolutions
                            .isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "Annual-Decade Functional Analysis가 unavailable인데 Transformation Segment Resolution이 존재합니다."
                );
            }

        } else {

            /*
             * =================================================
             * Segment Coverage
             * =================================================
             */

            if (
                    segmentResolutions.size()
                            != functionalAnalysis
                            .segments()
                            .size()
            ) {

                throw new IllegalArgumentException(
                        "Annual-Decade Functional Segment 수와 Transformation Resolution 수가 일치하지 않습니다."
                );
            }

            /*
             * =================================================
             * 각 Functional Segment마다
             * 정확히 하나의 Transformation Resolution
             * =================================================
             */

            for (
                    AnnualDecadeFunctionalSegment segment
                    : functionalAnalysis.segments()
            ) {

                long count =
                        segmentResolutions
                                .stream()
                                .filter(
                                        resolution ->
                                                resolution
                                                        .sourceSegment()
                                                        == segment
                                )
                                .count();

                if (
                        count != 1
                ) {

                    throw new IllegalArgumentException(
                            "각 Annual-Decade Functional Segment에는 정확히 하나의 Transformation Resolution이 필요합니다. "
                                    + "sequence="
                                    + segment.decadeSequenceNumber()
                                    + ", count="
                                    + count
                    );
                }
            }

            /*
             * =================================================
             * Resolution Identity / completeChart
             * =================================================
             */

            for (
                    AnnualDecadeTransformationResolution resolution
                    : segmentResolutions
            ) {

                Objects.requireNonNull(
                        resolution,
                        "AnnualDecadeTransformationResolution은 null일 수 없습니다."
                );

                if (
                        resolution.completeChart()
                                != completeChart
                ) {

                    throw new IllegalArgumentException(
                            "Transformation Segment Resolution의 completeChart가 일치하지 않습니다."
                    );
                }

                if (
                        !functionalAnalysis
                                .segments()
                                .contains(
                                        resolution
                                                .sourceSegment()
                                )
                ) {

                    throw new IllegalArgumentException(
                            "현재 Annual-Decade Functional Analysis에 없는 Segment가 Transformation Resolution에 연결되었습니다."
                    );
                }
            }
        }
    }

    public AnnualLuckAnalysis annualLuckAnalysis() {

        return functionalAnalysis
                .annualLuckAnalysis();
    }

    public int targetYear() {

        return functionalAnalysis
                .targetYear();
    }

    public AnnualLuckPeriod period() {

        return functionalAnalysis
                .period();
    }

    public boolean analysisAvailable() {

        return functionalAnalysis
                .analysisAvailable();
    }

    public Optional<String> unavailableReason() {

        return functionalAnalysis
                .unavailableReasonOptional();
    }

    /*
     * =================================================
     * Segment
     * =================================================
     */

    public Optional<AnnualDecadeTransformationResolution>
    resolutionOfDecade(
            int sequenceNumber
    ) {

        if (
                sequenceNumber < 1
                        || !analysisAvailable()
        ) {

            return Optional.empty();
        }

        return segmentResolutions
                .stream()
                .filter(
                        resolution ->
                                resolution
                                        .decadeSequenceNumber()
                                        == sequenceNumber
                )
                .findFirst();
    }

    public AnnualDecadeTransformationResolution
    requireResolutionOfDecade(
            int sequenceNumber
    ) {

        return resolutionOfDecade(
                sequenceNumber
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Annual-Decade Transformation Resolution을 찾을 수 없습니다: "
                                                + sequenceNumber
                                )
                );
    }

    /*
     * =================================================
     * Function
     * =================================================
     */

    public List<AnnualDecadeTransformationResolution>
    segmentsTargeting(
            NatalTenGodFunctionType functionType
    ) {

        if (
                functionType == null
        ) {

            return List.of();
        }

        return segmentResolutions
                .stream()
                .filter(
                        resolution ->
                                resolution
                                        .activeTargetFunction(
                                                functionType
                                        )
                )
                .toList();
    }

    public List<AnnualDecadeTransformationResolution>
    segmentsReducingAnnualSourceFunction(
            NatalTenGodFunctionType functionType
    ) {

        if (
                functionType == null
        ) {

            return List.of();
        }

        return segmentResolutions
                .stream()
                .filter(
                        resolution ->
                                resolution
                                        .annualSourceFunctionReductionExists(
                                                functionType
                                        )
                )
                .toList();
    }

    public List<AnnualDecadeTransformationResolution>
    segmentsReducingDecadeSourceFunction(
            NatalTenGodFunctionType functionType
    ) {

        if (
                functionType == null
        ) {

            return List.of();
        }

        return segmentResolutions
                .stream()
                .filter(
                        resolution ->
                                resolution
                                        .decadeSourceFunctionReductionExists(
                                                functionType
                                        )
                )
                .toList();
    }
}