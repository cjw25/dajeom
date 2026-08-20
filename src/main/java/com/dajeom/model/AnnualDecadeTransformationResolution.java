package com.dajeom.model;

import com.dajeom.type.NatalTenGodFunctionType;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 하나의 Annual-Decade Functional Segment에 존재하는
 * 모든 14-5 Transformation의
 * Source Participant Resolution.
 *
 * =================================================
 *
 * 하나의 14-5 Transformation
 *
 * =
 *
 * 하나의
 * AnnualDecadeTransformationParticipantResolution
 *
 * =================================================
 *
 * 지장간 Source Function이 여러 개여도
 * Transformation 자체를 여러 개로 복제하지 않는다.
 */
public record AnnualDecadeTransformationResolution(

        AnnualDecadeFunctionalSegment sourceSegment,

        List<AnnualDecadeTransformationParticipantResolution>
        transformationResolutions,

        boolean completeChart

) {

    public AnnualDecadeTransformationResolution {

        Objects.requireNonNull(
                sourceSegment,
                "Annual-Decade Transformation Resolution에는 Functional Segment가 필요합니다."
        );

        transformationResolutions =
                transformationResolutions == null
                        ? List.of()
                        : List.copyOf(
                        transformationResolutions
                );

        if (
                sourceSegment.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Annual-Decade Functional Segment와 Transformation Resolution의 completeChart가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 14-5 Transformation Coverage
         * =================================================
         */

        if (
                transformationResolutions.size()
                        != sourceSegment
                        .crossLuckTransformations()
                        .size()
        ) {

            throw new IllegalArgumentException(
                    "14-5 Transformation 수와 Participant Resolution 수가 일치하지 않습니다."
            );
        }

        Set<AnnualDecadeLuckTransformationReference> actual =
                new LinkedHashSet<>();

        for (
                AnnualDecadeTransformationParticipantResolution resolution
                : transformationResolutions
        ) {

            Objects.requireNonNull(
                    resolution,
                    "Transformation Participant Resolution은 null일 수 없습니다."
            );

            if (
                    resolution.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Transformation Participant Resolution의 completeChart가 일치하지 않습니다."
                );
            }

            if (
                    !sourceSegment
                            .crossLuckTransformations()
                            .contains(
                                    resolution
                                            .sourceTransformation()
                            )
            ) {

                throw new IllegalArgumentException(
                        "현재 14-5 Segment에 존재하지 않는 Transformation이 Resolution에 포함되었습니다."
                );
            }

            if (
                    !actual.add(
                            resolution
                                    .sourceTransformation()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 14-5 Transformation이 Participant Resolution에 중복되었습니다: "
                                + resolution
                                .sourceTransformation()
                                .transformationCode()
                );
            }
        }

        Set<AnnualDecadeLuckTransformationReference> expected =
                new LinkedHashSet<>(
                        sourceSegment
                                .crossLuckTransformations()
                );

        if (
                !actual.equals(
                        expected
                )
        ) {

            throw new IllegalArgumentException(
                    "14-5 Transformation 일부가 Participant Resolution에서 누락되었습니다."
            );
        }
    }

    public int targetYear() {

        return sourceSegment
                .targetYear();
    }

    public int decadeSequenceNumber() {

        return sourceSegment
                .decadeSequenceNumber();
    }

    public AnnualDecadeLuckInteraction interaction() {

        return sourceSegment
                .interaction();
    }

    /*
     * =================================================
     * 조회
     * =================================================
     */

    public Optional<AnnualDecadeTransformationParticipantResolution>
    resolutionOf(
            AnnualDecadeLuckTransformationReference transformation
    ) {

        if (
                transformation == null
        ) {

            return Optional.empty();
        }

        return transformationResolutions
                .stream()
                .filter(
                        resolution ->
                                resolution
                                        .sourceTransformation()
                                        == transformation
                )
                .findFirst();
    }

    public List<AnnualDecadeTransformationParticipantResolution>
    activeResolutions() {

        return transformationResolutions
                .stream()
                .filter(
                        AnnualDecadeTransformationParticipantResolution
                                ::activated
                )
                .toList();
    }

    public boolean activeTransformationExists() {

        return !activeResolutions()
                .isEmpty();
    }

    /*
     * =================================================
     * Source Function
     * =================================================
     */

    public List<AnnualDecadeTransformationParticipantResolution>
    resolutionsAffectingDecadeSourceFunction(
            NatalTenGodFunctionType functionType
    ) {

        if (
                functionType == null
        ) {

            return List.of();
        }

        return transformationResolutions
                .stream()
                .filter(
                        resolution ->
                                resolution
                                        .decadeSourceIncludes(
                                                functionType
                                        )
                )
                .toList();
    }

    public List<AnnualDecadeTransformationParticipantResolution>
    resolutionsAffectingAnnualSourceFunction(
            NatalTenGodFunctionType functionType
    ) {

        if (
                functionType == null
        ) {

            return List.of();
        }

        return transformationResolutions
                .stream()
                .filter(
                        resolution ->
                                resolution
                                        .annualSourceIncludes(
                                                functionType
                                        )
                )
                .toList();
    }

    public boolean decadeSourceFunctionReductionExists(
            NatalTenGodFunctionType functionType
    ) {

        return resolutionsAffectingDecadeSourceFunction(
                functionType
        )
                .stream()
                .anyMatch(
                        AnnualDecadeTransformationParticipantResolution
                                ::decadeOriginalFunctionReduced
                );
    }

    public boolean decadeSourceFunctionStrongReductionExists(
            NatalTenGodFunctionType functionType
    ) {

        return resolutionsAffectingDecadeSourceFunction(
                functionType
        )
                .stream()
                .anyMatch(
                        AnnualDecadeTransformationParticipantResolution
                                ::decadeOriginalFunctionStronglyReduced
                );
    }

    public boolean annualSourceFunctionReductionExists(
            NatalTenGodFunctionType functionType
    ) {

        return resolutionsAffectingAnnualSourceFunction(
                functionType
        )
                .stream()
                .anyMatch(
                        AnnualDecadeTransformationParticipantResolution
                                ::annualOriginalFunctionReduced
                );
    }

    public boolean annualSourceFunctionStrongReductionExists(
            NatalTenGodFunctionType functionType
    ) {

        return resolutionsAffectingAnnualSourceFunction(
                functionType
        )
                .stream()
                .anyMatch(
                        AnnualDecadeTransformationParticipantResolution
                                ::annualOriginalFunctionStronglyReduced
                );
    }

    /*
     * =================================================
     * Target Function
     * =================================================
     */

    public List<AnnualDecadeTransformationParticipantResolution>
    resolutionsTargeting(
            NatalTenGodFunctionType functionType
    ) {

        if (
                functionType == null
        ) {

            return List.of();
        }

        return transformationResolutions
                .stream()
                .filter(
                        resolution ->
                                resolution.targets(
                                        functionType
                                )
                )
                .toList();
    }

    public List<AnnualDecadeTransformationParticipantResolution>
    activeResolutionsTargeting(
            NatalTenGodFunctionType functionType
    ) {

        return resolutionsTargeting(
                functionType
        )
                .stream()
                .filter(
                        AnnualDecadeTransformationParticipantResolution
                                ::activated
                )
                .toList();
    }

    public boolean activeTargetFunction(
            NatalTenGodFunctionType functionType
    ) {

        return !activeResolutionsTargeting(
                functionType
        )
                .isEmpty();
    }

    public boolean strongTargetFunction(
            NatalTenGodFunctionType functionType
    ) {

        return activeResolutionsTargeting(
                functionType
        )
                .stream()
                .anyMatch(
                        AnnualDecadeTransformationParticipantResolution
                                ::strongTargetActivation
                );
    }

    /*
     * =================================================
     * Distinct Target Functions
     * =================================================
     */

    public List<NatalTenGodFunctionType>
    activeTargetFunctions() {

        Set<NatalTenGodFunctionType> result =
                new LinkedHashSet<>();

        for (
                AnnualDecadeTransformationParticipantResolution resolution
                : activeResolutions()
        ) {

            result.add(
                    resolution
                            .targetFunctionType()
            );
        }

        return List.copyOf(
                result
        );
    }
}