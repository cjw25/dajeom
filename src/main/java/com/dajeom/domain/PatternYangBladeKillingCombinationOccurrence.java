package com.dajeom.domain;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 陽刃 한 occurrence와
 * 투출 七殺 한 노드 사이의 羊刃合殺.
 */
public record PatternYangBladeKillingCombinationOccurrence(

        PatternYangBladeOccurrence yangBlade,

        PatternTenGodNode sevenKillings,

        PatternYangBladeKillingCombinationState state,

        List<PatternBranchDisruptionOccurrence> bladeDisruptions,

        Set<PatternYangBladeKillingCombinationReason> reasons

) {

    public PatternYangBladeKillingCombinationOccurrence {

        Objects.requireNonNull(
                yangBlade,
                "陽刃 occurrence는 필수입니다."
        );

        Objects.requireNonNull(
                sevenKillings,
                "七殺 노드는 필수입니다."
        );

        Objects.requireNonNull(
                state,
                "陽刃合殺 상태는 필수입니다."
        );

        bladeDisruptions =
                bladeDisruptions == null
                        ? List.of()
                        : List.copyOf(
                        bladeDisruptions
                );

        reasons =
                reasons == null
                        ? Set.of()
                        : Set.copyOf(reasons);

        if (!sevenKillings.visible()) {

            throw new IllegalArgumentException(
                    "陽刃合殺의 七殺은 투출 천간이어야 합니다."
            );
        }

        if (
                !sevenKillings.hasTenGodType(
                        MonthCommandPatternCandidateType
                                .SEVEN_KILLINGS
                )
        ) {

            throw new IllegalArgumentException(
                    "陽刃合殺 대상 천간은 七殺이어야 합니다."
            );
        }

        for (
                PatternBranchDisruptionOccurrence disruption
                : bladeDisruptions
        ) {

            Objects.requireNonNull(
                    disruption,
                    "陽刃 교란 목록에 null이 포함될 수 없습니다."
            );

            if (
                    !disruption.involvesPosition(
                            yangBlade.position()
                    )
            ) {

                throw new IllegalArgumentException(
                        "bladeDisruptions에는 해당 陽刃 위치가 포함된 교란만 들어갈 수 있습니다."
                );
            }
        }
    }

    public boolean active() {

        return state
                == PatternYangBladeKillingCombinationState
                .ACTIVE;
    }

    public boolean reviewRequired() {

        return state
                == PatternYangBladeKillingCombinationState
                .BLADE_BRANCH_DISRUPTED_REVIEW_REQUIRED;
    }
}