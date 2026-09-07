package com.dajeom.domain;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 투출 官殺混雜의 制化取清 분석.
 */
public record PatternOfficerKillingsPurificationAnalysis(

        HeavenlyStem dayMaster,

        PatternOfficerKillingsPurificationState state,

        List<PatternTenGodNode> visibleOfficers,

        List<PatternTenGodNode> visibleKillings,

        List<PatternTenGodNode> controlledOfficers,

        List<PatternTenGodNode> controlledKillings,

        List<PatternTenGodNode> retainedOfficers,

        List<PatternTenGodNode> retainedKillings,

        Set<PatternOfficerKillingsPurificationReason> reasons

) {

    public PatternOfficerKillingsPurificationAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                state,
                "官殺取清 상태는 필수입니다."
        );

        visibleOfficers =
                visibleOfficers == null
                        ? List.of()
                        : List.copyOf(visibleOfficers);

        visibleKillings =
                visibleKillings == null
                        ? List.of()
                        : List.copyOf(visibleKillings);

        controlledOfficers =
                controlledOfficers == null
                        ? List.of()
                        : List.copyOf(controlledOfficers);

        controlledKillings =
                controlledKillings == null
                        ? List.of()
                        : List.copyOf(controlledKillings);

        retainedOfficers =
                retainedOfficers == null
                        ? List.of()
                        : List.copyOf(retainedOfficers);

        retainedKillings =
                retainedKillings == null
                        ? List.of()
                        : List.copyOf(retainedKillings);

        reasons =
                reasons == null
                        ? Set.of()
                        : Set.copyOf(reasons);

        validateSubset(
                controlledOfficers,
                visibleOfficers,
                "controlledOfficers"
        );

        validateSubset(
                controlledKillings,
                visibleKillings,
                "controlledKillings"
        );

        validateSubset(
                retainedOfficers,
                visibleOfficers,
                "retainedOfficers"
        );

        validateSubset(
                retainedKillings,
                visibleKillings,
                "retainedKillings"
        );
    }

    public boolean mixed() {

        return !visibleOfficers.isEmpty()
                &&
                !visibleKillings.isEmpty();
    }

    public boolean killingsControlledLeavingOfficer() {

        return state
                == PatternOfficerKillingsPurificationState
                .KILLINGS_CONTROLLED_OFFICER_RETAINED;
    }

    public boolean officersControlledLeavingKillings() {

        return state
                == PatternOfficerKillingsPurificationState
                .OFFICERS_CONTROLLED_KILLINGS_RETAINED;
    }

    public boolean mixtureCleared() {

        return killingsControlledLeavingOfficer()
                ||
                officersControlledLeavingKillings();
    }

    public boolean partiallyCleared() {

        return state
                == PatternOfficerKillingsPurificationState
                .PARTIAL_CLEARING;
    }

    public boolean reviewRequired() {

        return state
                == PatternOfficerKillingsPurificationState
                .REVIEW_REQUIRED;
    }

    private static void validateSubset(

            List<PatternTenGodNode> subset,

            List<PatternTenGodNode> source,

            String name

    ) {

        if (!source.containsAll(subset)) {

            throw new IllegalArgumentException(
                    name + "는 visible 官殺 목록의 부분집합이어야 합니다."
            );
        }
    }
}