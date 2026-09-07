package com.dajeom.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 원국 십성 전체의 foundation 분석 묶음.
 */
public record PatternTenGodFoundationProfile(

        HeavenlyStem dayMaster,

        List<PatternTenGodFoundationAnalysis> foundations

) {

    public PatternTenGodFoundationProfile {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        foundations =
                foundations == null
                        ? List.of()
                        : List.copyOf(foundations);

        for (
                PatternTenGodFoundationAnalysis foundation
                : foundations
        ) {

            Objects.requireNonNull(
                    foundation,
                    "foundation 목록에 null이 포함될 수 없습니다."
            );
        }
    }

    public PatternTenGodFoundationAnalysis foundationOf(
            MonthCommandPatternCandidateType type
    ) {

        Objects.requireNonNull(
                type,
                "십성 타입은 필수입니다."
        );

        return foundations
                .stream()
                .filter(
                        foundation ->
                                foundation.tenGodType()
                                        == type
                )
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "해당 십성의 foundation 분석이 없습니다: "
                                                + type
                                )
                );
    }

    public boolean foundationStable(
            MonthCommandPatternCandidateType type
    ) {

        return foundationOf(type)
                .foundationStable();
    }

    /*
     * =================================================
     * 격국에서 사용하는 계열 helper
     * =================================================
     */

    public boolean wealthFoundationStable() {

        return anyFoundationStable(
                MonthCommandPatternCandidateType.DIRECT_WEALTH,
                MonthCommandPatternCandidateType.INDIRECT_WEALTH
        );
    }

    public boolean resourceFoundationStable() {

        return anyFoundationStable(
                MonthCommandPatternCandidateType.DIRECT_RESOURCE,
                MonthCommandPatternCandidateType.INDIRECT_RESOURCE
        );
    }

    public boolean eatingGodFoundationStable() {

        return foundationStable(
                MonthCommandPatternCandidateType
                        .EATING_GOD
        );
    }

    public boolean hurtingOfficerFoundationStable() {

        return foundationStable(
                MonthCommandPatternCandidateType
                        .HURTING_OFFICER
        );
    }

    public boolean sevenKillingsFoundationStable() {

        return foundationStable(
                MonthCommandPatternCandidateType
                        .SEVEN_KILLINGS
        );
    }

    public boolean directOfficerFoundationStable() {

        return foundationStable(
                MonthCommandPatternCandidateType
                        .DIRECT_OFFICER
        );
    }

    private boolean anyFoundationStable(
            MonthCommandPatternCandidateType... types
    ) {

        return Arrays
                .stream(types)
                .anyMatch(
                        this::foundationStable
                );
    }
}