package com.dajeom.dto;

import com.dajeom.domain.SpecialStarHit;

import java.util.Objects;

public record SpecialStarResponse(
        String type,
        String name,
        String nameHanja,

        String referenceType,

        String referenceStem,
        String referenceStemKorean,

        String monthBranchReference,
        String monthBranchReferenceKorean,

        String expectedStem,
        String expectedStemKorean,

        String expectedBranch,
        String expectedBranchKorean,

        String expectedGanji,

        String position,
        String positionKorean,

        String stem,
        String stemKorean,

        String branch,
        String branchKorean,

        String ganji,

        String evidence
) {

    public static SpecialStarResponse from(
            SpecialStarHit hit
    ) {

        Objects.requireNonNull(
                hit,
                "신살 적중 결과가 필요합니다."
        );

        String referenceStem =
                hit.dayStemReference() == null
                        ? null
                        : hit.dayStemReference().hanja();

        String referenceStemKorean =
                hit.dayStemReference() == null
                        ? null
                        : hit.dayStemReference().korean();

        String monthBranchReference =
                hit.monthBranchReference() == null
                        ? null
                        : hit.monthBranchReference().hanja();

        String monthBranchReferenceKorean =
                hit.monthBranchReference() == null
                        ? null
                        : hit.monthBranchReference().korean();

        String expectedStem =
                hit.expectedStem() == null
                        ? null
                        : hit.expectedStem().hanja();

        String expectedStemKorean =
                hit.expectedStem() == null
                        ? null
                        : hit.expectedStem().korean();

        String expectedBranch =
                hit.expectedBranch() == null
                        ? null
                        : hit.expectedBranch().hanja();

        String expectedBranchKorean =
                hit.expectedBranch() == null
                        ? null
                        : hit.expectedBranch().korean();

        String expectedGanji =
                hit.expectedGanji() == null
                        ? null
                        : hit.expectedGanji().hanja();

        return new SpecialStarResponse(
                hit.type().name(),
                hit.type().korean(),
                hit.type().hanja(),

                hit.referenceType().name(),

                referenceStem,
                referenceStemKorean,

                monthBranchReference,
                monthBranchReferenceKorean,

                expectedStem,
                expectedStemKorean,

                expectedBranch,
                expectedBranchKorean,

                expectedGanji,

                hit.targetPosition().name(),
                hit.targetPosition().korean(),

                hit.targetStem().hanja(),
                hit.targetStem().korean(),

                hit.targetBranch().hanja(),
                hit.targetBranch().korean(),

                hit.targetGanji().hanja(),

                hit.evidence()
        );
    }
}