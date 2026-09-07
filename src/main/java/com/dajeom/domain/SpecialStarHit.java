package com.dajeom.domain;

import java.util.Objects;

public record SpecialStarHit(
        SpecialStarType type,
        SpecialStarReferenceType referenceType,

        HeavenlyStem dayStemReference,
        EarthlyBranch monthBranchReference,

        HeavenlyStem expectedStem,
        EarthlyBranch expectedBranch,
        Ganji expectedGanji,

        PillarPosition targetPosition,
        Pillar targetPillar,

        String evidence
) {

    public SpecialStarHit {

        Objects.requireNonNull(
                type,
                "신살 종류가 필요합니다."
        );

        Objects.requireNonNull(
                referenceType,
                "신살 기준 종류가 필요합니다."
        );

        Objects.requireNonNull(
                targetPosition,
                "신살 적중 기둥 위치가 필요합니다."
        );

        Objects.requireNonNull(
                targetPillar,
                "신살 적중 기둥이 필요합니다."
        );

        Objects.requireNonNull(
                evidence,
                "신살 계산 근거가 필요합니다."
        );

        if (evidence.isBlank()) {

            throw new IllegalArgumentException(
                    "신살 계산 근거는 비어 있을 수 없습니다."
            );
        }

        switch (referenceType) {

            case DAY_STEM_BRANCH -> {

                Objects.requireNonNull(
                        dayStemReference,
                        "일간 기준 신살에는 기준 일간이 필요합니다."
                );

                Objects.requireNonNull(
                        expectedBranch,
                        "일간 기준 신살에는 목표 지지가 필요합니다."
                );

                if (monthBranchReference != null
                        || expectedStem != null
                        || expectedGanji != null) {

                    throw new IllegalArgumentException(
                            "DAY_STEM_BRANCH 방식에 사용할 수 없는 기준값이 있습니다."
                    );
                }

                if (targetPillar.branch()
                        != expectedBranch) {

                    throw new IllegalArgumentException(
                            "신살 목표 지지와 실제 지지가 다릅니다. expected="
                                    + expectedBranch.hanja()
                                    + ", actual="
                                    + targetPillar.branch().hanja()
                    );
                }
            }

            case PILLAR_GANJI -> {

                Objects.requireNonNull(
                        expectedGanji,
                        "간지 기준 신살에는 목표 간지가 필요합니다."
                );

                if (dayStemReference != null
                        || monthBranchReference != null
                        || expectedStem != null
                        || expectedBranch != null) {

                    throw new IllegalArgumentException(
                            "PILLAR_GANJI 방식에 사용할 수 없는 기준값이 있습니다."
                    );
                }

                if (!targetPillar.ganji()
                        .equals(expectedGanji)) {

                    throw new IllegalArgumentException(
                            "신살 목표 간지와 실제 간지가 다릅니다. expected="
                                    + expectedGanji.hanja()
                                    + ", actual="
                                    + targetPillar.ganji().hanja()
                    );
                }
            }

            case PILLAR_CHARACTER -> {

                if (dayStemReference != null
                        || monthBranchReference != null
                        || expectedStem != null
                        || expectedBranch != null
                        || expectedGanji != null) {

                    throw new IllegalArgumentException(
                            "PILLAR_CHARACTER 방식에는 별도 기준값을 사용하지 않습니다."
                    );
                }
            }

            case MONTH_BRANCH_CHARACTER -> {

                Objects.requireNonNull(
                        monthBranchReference,
                        "월지 기준 신살에는 기준 월지가 필요합니다."
                );

                if (dayStemReference != null
                        || expectedGanji != null) {

                    throw new IllegalArgumentException(
                            "MONTH_BRANCH_CHARACTER 방식에 사용할 수 없는 기준값이 있습니다."
                    );
                }

                boolean hasExpectedStem =
                        expectedStem != null;

                boolean hasExpectedBranch =
                        expectedBranch != null;

                /*
                 * 천간 또는 지지 둘 중 정확히 하나만 목표여야 한다.
                 */
                if (hasExpectedStem
                        == hasExpectedBranch) {

                    throw new IllegalArgumentException(
                            "월지 기준 신살은 목표 천간 또는 목표 지지 중 정확히 하나가 필요합니다."
                    );
                }

                if (hasExpectedStem
                        && targetPillar.stem()
                        != expectedStem) {

                    throw new IllegalArgumentException(
                            "신살 목표 천간과 실제 천간이 다릅니다. expected="
                                    + expectedStem.hanja()
                                    + ", actual="
                                    + targetPillar.stem().hanja()
                    );
                }

                if (hasExpectedBranch
                        && targetPillar.branch()
                        != expectedBranch) {

                    throw new IllegalArgumentException(
                            "신살 목표 지지와 실제 지지가 다릅니다. expected="
                                    + expectedBranch.hanja()
                                    + ", actual="
                                    + targetPillar.branch().hanja()
                    );
                }
            }
        }
    }

    public static SpecialStarHit dayStemBranch(
            SpecialStarType type,
            HeavenlyStem dayStem,
            EarthlyBranch expectedBranch,
            PillarPosition position,
            Pillar pillar
    ) {

        return new SpecialStarHit(
                type,
                SpecialStarReferenceType.DAY_STEM_BRANCH,

                dayStem,
                null,

                null,
                expectedBranch,
                null,

                position,
                pillar,

                "DAY_STEM="
                        + dayStem.hanja()
                        + ", TARGET_BRANCH="
                        + expectedBranch.hanja()
        );
    }

    public static SpecialStarHit pillarGanji(
            SpecialStarType type,
            Ganji expectedGanji,
            PillarPosition position,
            Pillar pillar
    ) {

        return new SpecialStarHit(
                type,
                SpecialStarReferenceType.PILLAR_GANJI,

                null,
                null,

                null,
                null,
                expectedGanji,

                position,
                pillar,

                "PILLAR_GANJI="
                        + expectedGanji.hanja()
        );
    }

    public static SpecialStarHit pillarCharacter(
            SpecialStarType type,
            PillarPosition position,
            Pillar pillar,
            String evidence
    ) {

        return new SpecialStarHit(
                type,
                SpecialStarReferenceType.PILLAR_CHARACTER,

                null,
                null,

                null,
                null,
                null,

                position,
                pillar,

                evidence
        );
    }

    public static SpecialStarHit monthBranchStem(
            SpecialStarType type,
            EarthlyBranch monthBranch,
            HeavenlyStem expectedStem,
            PillarPosition position,
            Pillar pillar
    ) {

        return new SpecialStarHit(
                type,
                SpecialStarReferenceType.MONTH_BRANCH_CHARACTER,

                null,
                monthBranch,

                expectedStem,
                null,
                null,

                position,
                pillar,

                "MONTH_BRANCH="
                        + monthBranch.hanja()
                        + ", TARGET_STEM="
                        + expectedStem.hanja()
        );
    }

    public static SpecialStarHit monthBranchBranch(
            SpecialStarType type,
            EarthlyBranch monthBranch,
            EarthlyBranch expectedBranch,
            PillarPosition position,
            Pillar pillar
    ) {

        return new SpecialStarHit(
                type,
                SpecialStarReferenceType.MONTH_BRANCH_CHARACTER,

                null,
                monthBranch,

                null,
                expectedBranch,
                null,

                position,
                pillar,

                "MONTH_BRANCH="
                        + monthBranch.hanja()
                        + ", TARGET_BRANCH="
                        + expectedBranch.hanja()
        );
    }

    public HeavenlyStem targetStem() {
        return targetPillar.stem();
    }

    public EarthlyBranch targetBranch() {
        return targetPillar.branch();
    }

    public Ganji targetGanji() {
        return targetPillar.ganji();
    }
}