package com.dajeom.rule;

import com.dajeom.model.EarthlyBranchSixCombinationEvidence;
import com.dajeom.type.EarthlyBranchSixCombinationTransformationReasonType;
import com.dajeom.type.EarthlyBranchSixCombinationTransformationState;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class EarthlyBranchSixCombinationTransformationRule {

    public static final int TARGET_PRESENT_BASIS_POINTS =
            1_200;

    public static final int TARGET_STRONG_BASIS_POINTS =
            2_500;

    public static final int TARGET_VERY_SCARCE_BASIS_POINTS =
            500;

    public static final int SEASONAL_SUPPORTED_RATE =
            110;

    public static final int SEASONAL_NEUTRAL_RATE =
            100;

    public EarthlyBranchSixCombinationTransformationState
    determineState(

            EarthlyBranchSixCombinationEvidence evidence,

            int targetShareBasisPoints,

            int targetSeasonalRate,

            int monthBranchPresenceWeight

    ) {

        Objects.requireNonNull(
                evidence,
                "8-3 육합 Evidence가 필요합니다."
        );

        return determineState(
                evidence.adjacent(),
                evidence.monthBranchInvolved(),
                targetShareBasisPoints,
                targetSeasonalRate,
                monthBranchPresenceWeight
        );
    }

    public EarthlyBranchSixCombinationTransformationState
    determineState(

            boolean adjacent,

            boolean monthBranchInvolved,

            int targetShareBasisPoints,

            int targetSeasonalRate,

            int monthBranchPresenceWeight

    ) {

        validateInputs(
                targetShareBasisPoints,
                targetSeasonalRate,
                monthBranchPresenceWeight
        );

        boolean seasonallySupported =
                targetSeasonalRate
                        >= SEASONAL_SUPPORTED_RATE;

        boolean seasonallyNeutralOrBetter =
                targetSeasonalRate
                        >= SEASONAL_NEUTRAL_RATE;

        boolean targetPresentInMonth =
                monthBranchPresenceWeight > 0;

        boolean targetStrong =
                targetShareBasisPoints
                        >= TARGET_STRONG_BASIS_POINTS;

        boolean targetPresent =
                targetShareBasisPoints
                        >= TARGET_PRESENT_BASIS_POINTS;

        if (
                adjacent
                        && seasonallySupported
                        && (
                        targetStrong
                                || targetPresentInMonth
                )
        ) {

            return EarthlyBranchSixCombinationTransformationState
                    .PROVISIONALLY_TRANSFORMED;
        }

        if (
                adjacent
                        && monthBranchInvolved
                        && targetStrong
                        && seasonallyNeutralOrBetter
        ) {

            return EarthlyBranchSixCombinationTransformationState
                    .PROVISIONALLY_TRANSFORMED;
        }

        if (
                adjacent
                        && (
                        seasonallySupported
                                || targetPresent
                                || targetPresentInMonth
                )
        ) {

            return EarthlyBranchSixCombinationTransformationState
                    .PROVISIONALLY_PARTIAL;
        }

        if (
                !adjacent
                        && seasonallySupported
                        && targetStrong
                        && targetPresentInMonth
        ) {

            return EarthlyBranchSixCombinationTransformationState
                    .PROVISIONALLY_PARTIAL;
        }

        if (
                adjacent
        ) {

            return EarthlyBranchSixCombinationTransformationState
                    .CONDITIONAL;
        }

        if (
                (
                        seasonallySupported
                                && targetPresent
                )
                        || (
                        monthBranchInvolved
                                && targetPresent
                )
                        || (
                        targetPresentInMonth
                                && targetPresent
                )
        ) {

            return EarthlyBranchSixCombinationTransformationState
                    .CONDITIONAL;
        }

        return EarthlyBranchSixCombinationTransformationState
                .COMBINED_NOT_TRANSFORMED;
    }

    public boolean meaningful(
            EarthlyBranchSixCombinationTransformationState state
    ) {

        Objects.requireNonNull(
                state
        );

        return state
                == EarthlyBranchSixCombinationTransformationState
                .PROVISIONALLY_TRANSFORMED

                || state
                == EarthlyBranchSixCombinationTransformationState
                .PROVISIONALLY_PARTIAL;
    }

    public boolean candidateRemains(
            EarthlyBranchSixCombinationTransformationState state
    ) {

        Objects.requireNonNull(
                state
        );

        return state
                != EarthlyBranchSixCombinationTransformationState
                .COMBINED_NOT_TRANSFORMED;
    }

    public List<EarthlyBranchSixCombinationTransformationReasonType>
    buildReasons(

            EarthlyBranchSixCombinationEvidence evidence,

            EarthlyBranchSixCombinationTransformationState state,

            int targetShareBasisPoints,

            int targetSeasonalRate,

            int monthBranchPresenceWeight

    ) {

        Objects.requireNonNull(
                evidence
        );

        Objects.requireNonNull(
                state
        );

        validateInputs(
                targetShareBasisPoints,
                targetSeasonalRate,
                monthBranchPresenceWeight
        );

        List<EarthlyBranchSixCombinationTransformationReasonType>
                reasons =
                new ArrayList<>();

        reasons.add(
                evidence.adjacent()
                        ? EarthlyBranchSixCombinationTransformationReasonType
                        .ADJACENT_PAIR
                        : EarthlyBranchSixCombinationTransformationReasonType
                        .DISTANT_PAIR
        );

        reasons.add(
                evidence.monthBranchInvolved()
                        ? EarthlyBranchSixCombinationTransformationReasonType
                        .MONTH_BRANCH_INVOLVED
                        : EarthlyBranchSixCombinationTransformationReasonType
                        .MONTH_BRANCH_NOT_INVOLVED
        );

        if (
                evidence.dayBranchInvolved()
        ) {

            reasons.add(
                    EarthlyBranchSixCombinationTransformationReasonType
                            .DAY_BRANCH_INVOLVED
            );
        }

        if (
                targetSeasonalRate
                        >= SEASONAL_SUPPORTED_RATE
        ) {

            reasons.add(
                    EarthlyBranchSixCombinationTransformationReasonType
                            .TARGET_SEASONALLY_SUPPORTED
            );

        } else if (
                targetSeasonalRate
                        >= SEASONAL_NEUTRAL_RATE
        ) {

            reasons.add(
                    EarthlyBranchSixCombinationTransformationReasonType
                            .TARGET_SEASONALLY_NEUTRAL
            );

        } else {

            reasons.add(
                    EarthlyBranchSixCombinationTransformationReasonType
                            .TARGET_SEASONALLY_WEAK
            );
        }

        reasons.add(
                monthBranchPresenceWeight > 0
                        ? EarthlyBranchSixCombinationTransformationReasonType
                        .TARGET_PRESENT_IN_MONTH_BRANCH
                        : EarthlyBranchSixCombinationTransformationReasonType
                        .TARGET_ABSENT_FROM_MONTH_BRANCH
        );

        if (
                targetShareBasisPoints
                        >= TARGET_STRONG_BASIS_POINTS
        ) {

            reasons.add(
                    EarthlyBranchSixCombinationTransformationReasonType
                            .TARGET_ELEMENT_STRONG_IN_CHART
            );

        } else if (
                targetShareBasisPoints
                        >= TARGET_PRESENT_BASIS_POINTS
        ) {

            reasons.add(
                    EarthlyBranchSixCombinationTransformationReasonType
                            .TARGET_ELEMENT_PRESENT_IN_CHART
            );

        } else if (
                targetShareBasisPoints
                        >= TARGET_VERY_SCARCE_BASIS_POINTS
        ) {

            reasons.add(
                    EarthlyBranchSixCombinationTransformationReasonType
                            .TARGET_ELEMENT_SCARCE_IN_CHART
            );

        } else {

            reasons.add(
                    EarthlyBranchSixCombinationTransformationReasonType
                            .TARGET_ELEMENT_VERY_SCARCE_IN_CHART
            );
        }

        switch (
                state
        ) {

            case PROVISIONALLY_TRANSFORMED ->
                    reasons.add(
                            EarthlyBranchSixCombinationTransformationReasonType
                                    .PROVISIONAL_TRANSFORMATION_ESTABLISHED
                    );

            case PROVISIONALLY_PARTIAL ->
                    reasons.add(
                            EarthlyBranchSixCombinationTransformationReasonType
                                    .PROVISIONAL_PARTIAL_TRANSFORMATION
                    );

            case CONDITIONAL ->
                    reasons.add(
                            EarthlyBranchSixCombinationTransformationReasonType
                                    .TRANSFORMATION_REQUIRES_FURTHER_CONDITIONS
                    );

            case COMBINED_NOT_TRANSFORMED ->
                    reasons.add(
                            EarthlyBranchSixCombinationTransformationReasonType
                                    .COMBINATION_REMAINS_WITHOUT_TRANSFORMATION
                    );
        }

        return List.copyOf(
                reasons
        );
    }

    private void validateInputs(

            int targetShareBasisPoints,

            int targetSeasonalRate,

            int monthBranchPresenceWeight

    ) {

        if (
                targetShareBasisPoints < 0
                        || targetShareBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "목표 오행 비율은 0~10000bp 범위여야 합니다."
            );
        }

        if (
                targetSeasonalRate < 0
        ) {

            throw new IllegalArgumentException(
                    "목표 오행 계절 배율은 음수일 수 없습니다."
            );
        }

        if (
                monthBranchPresenceWeight < 0
        ) {

            throw new IllegalArgumentException(
                    "월지 목표 오행 존재 가중치는 음수일 수 없습니다."
            );
        }
    }
}