package com.dajeom.domain;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 월령 전체를 기준으로 정리한
 * 沖 / 刑 / 破 / 害 회합 해소 결과.
 *
 * =================================================
 *
 * 개별 occurrence 결과:
 *
 * PatternBranchDisruptionResolutionOccurrence
 *
 * 를 월령 전체 기준으로 묶는다.
 *
 * =================================================
 */
public record PatternMonthCommandDisruptionResolutionAnalysis(

        EarthlyBranch monthBranch,

        PatternMonthCommandDisruptionResolutionState state,

        List<PatternBranchDisruptionResolutionOccurrence>
        monthCommandResolutions,

        Set<PatternMonthCommandDisruptionResolutionReason>
        reasons

) {

    public PatternMonthCommandDisruptionResolutionAnalysis {

        Objects.requireNonNull(
                monthBranch,
                "월지는 필수입니다."
        );

        Objects.requireNonNull(
                state,
                "월령 교란 해소 상태는 필수입니다."
        );

        monthCommandResolutions =
                monthCommandResolutions == null
                        ? List.of()
                        : List.copyOf(
                        monthCommandResolutions
                );

        reasons =
                reasons == null
                        ? Set.of()
                        : Set.copyOf(
                        reasons
                );

        for (
                PatternBranchDisruptionResolutionOccurrence resolution
                : monthCommandResolutions
        ) {

            Objects.requireNonNull(
                    resolution,
                    "월령 교란 해소 목록에 null이 포함될 수 없습니다."
            );

            if (!resolution.involvesMonthCommand()) {

                throw new IllegalArgumentException(
                        "월령 해소 분석에는 월령이 포함된 교란만 들어갈 수 있습니다."
                );
            }
        }

        if (
                state
                        == PatternMonthCommandDisruptionResolutionState
                        .NO_DISRUPTION

                        &&

                        !monthCommandResolutions.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "NO_DISRUPTION 상태에는 월령 교란 occurrence가 존재할 수 없습니다."
            );
        }

        if (
                state
                        != PatternMonthCommandDisruptionResolutionState
                        .NO_DISRUPTION

                        &&

                        monthCommandResolutions.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "월령 교란 상태에는 최소 하나의 월령 교란 occurrence가 필요합니다."
            );
        }
    }

    public boolean noDisruption() {

        return state
                == PatternMonthCommandDisruptionResolutionState
                .NO_DISRUPTION;
    }

    public boolean resolvedByCombination() {

        return state
                == PatternMonthCommandDisruptionResolutionState
                .RESOLVED_BY_COMBINATION;
    }

    public boolean partiallyResolved() {

        return state
                == PatternMonthCommandDisruptionResolutionState
                .PARTIALLY_RESOLVED;
    }

    public boolean reviewRequired() {

        return state
                == PatternMonthCommandDisruptionResolutionState
                .REVIEW_REQUIRED;
    }

    public boolean unresolved() {

        return state
                == PatternMonthCommandDisruptionResolutionState
                .UNRESOLVED;
    }

    public boolean hasOriginalDisruption() {

        return !noDisruption();
    }

    /**
     * 격국 RelationContext에
     * "회합으로 해소됐다"를 전달해도 되는가.
     *
     * PARTIALLY_RESOLVED나 REVIEW_REQUIRED는
     * true로 올리지 않는다.
     */
    public boolean usableAsResolvedByCombination() {

        return resolvedByCombination();
    }
}