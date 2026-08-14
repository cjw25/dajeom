package com.dajeom.rule;

import com.dajeom.model.EarthlyBranchThreeCombinationEvidence;
import com.dajeom.type.EarthlyBranchThreeCombinationForm;
import com.dajeom.type.EarthlyBranchThreeCombinationFormationReasonType;
import com.dajeom.type.EarthlyBranchThreeCombinationFormationState;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class EarthlyBranchThreeCombinationFormationRule {

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

    public EarthlyBranchThreeCombinationFormationState
    determineState(

            EarthlyBranchThreeCombinationEvidence evidence,

            int targetShareBasisPoints,

            int targetSeasonalRate,

            int monthBranchPresenceWeight

    ) {

        Objects.requireNonNull(
                evidence
        );

        if (
                evidence.subordinateToCompleteCombination()
        ) {

            throw new IllegalArgumentException(
                    "완전 삼합 내부의 종속 Pair는 9-5에서 별도 국 성립 대상으로 평가할 수 없습니다."
            );
        }

        return determineState(
                evidence.form(),
                evidence.compact(),
                evidence.monthBranchInvolved(),
                targetShareBasisPoints,
                targetSeasonalRate,
                monthBranchPresenceWeight
        );
    }

    public EarthlyBranchThreeCombinationFormationState
    determineState(

            EarthlyBranchThreeCombinationForm form,

            boolean compact,

            boolean monthBranchInvolved,

            int targetShareBasisPoints,

            int targetSeasonalRate,

            int monthBranchPresenceWeight

    ) {

        Objects.requireNonNull(
                form,
                "삼합 구성 형태가 필요합니다."
        );

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
                form
                        == EarthlyBranchThreeCombinationForm.COMPLETE
        ) {

            if (
                    seasonallySupported
                            && (
                            targetStrong
                                    || targetPresentInMonth
                    )
            ) {

                return EarthlyBranchThreeCombinationFormationState
                        .PROVISIONALLY_ESTABLISHED;
            }

            if (
                    monthBranchInvolved
                            && targetStrong
                            && seasonallyNeutralOrBetter
            ) {

                return EarthlyBranchThreeCombinationFormationState
                        .PROVISIONALLY_ESTABLISHED;
            }

            if (
                    targetPresent
                            || targetPresentInMonth
                            || seasonallyNeutralOrBetter
            ) {

                return EarthlyBranchThreeCombinationFormationState
                        .PROVISIONALLY_PARTIAL;
            }

            return EarthlyBranchThreeCombinationFormationState
                    .CONDITIONAL;
        }

        if (
                form
                        == EarthlyBranchThreeCombinationForm
                        .HALF_WITH_CARDINAL
        ) {

            if (
                    compact
                            && seasonallySupported
                            && (
                            targetPresent
                                    || targetPresentInMonth
                    )
            ) {

                return EarthlyBranchThreeCombinationFormationState
                        .PROVISIONALLY_PARTIAL;
            }

            if (
                    monthBranchInvolved
                            && targetStrong
                            && seasonallyNeutralOrBetter
            ) {

                return EarthlyBranchThreeCombinationFormationState
                        .PROVISIONALLY_PARTIAL;
            }

            if (
                    seasonallyNeutralOrBetter
                            || targetPresent
                            || targetPresentInMonth
            ) {

                return EarthlyBranchThreeCombinationFormationState
                        .CONDITIONAL;
            }

            return EarthlyBranchThreeCombinationFormationState
                    .RELATION_ONLY;
        }

        if (
                form
                        == EarthlyBranchThreeCombinationForm
                        .PARTIAL_WITHOUT_CARDINAL
        ) {

            if (
                    seasonallySupported
                            && targetStrong
                            && targetPresentInMonth
            ) {

                return EarthlyBranchThreeCombinationFormationState
                        .CONDITIONAL;
            }

            if (
                    monthBranchInvolved
                            && seasonallySupported
                            && targetPresent
            ) {

                return EarthlyBranchThreeCombinationFormationState
                        .CONDITIONAL;
            }

            return EarthlyBranchThreeCombinationFormationState
                    .RELATION_ONLY;
        }

        throw new IllegalStateException(
                "알 수 없는 삼합 구성 형태입니다: "
                        + form
        );
    }

    public boolean meaningful(
            EarthlyBranchThreeCombinationFormationState state
    ) {

        Objects.requireNonNull(
                state
        );

        return state
                == EarthlyBranchThreeCombinationFormationState
                .PROVISIONALLY_ESTABLISHED

                || state
                == EarthlyBranchThreeCombinationFormationState
                .PROVISIONALLY_PARTIAL;
    }

    public boolean candidateRemains(
            EarthlyBranchThreeCombinationFormationState state
    ) {

        Objects.requireNonNull(
                state
        );

        return state
                != EarthlyBranchThreeCombinationFormationState
                .RELATION_ONLY;
    }

    public List<EarthlyBranchThreeCombinationFormationReasonType>
    buildReasons(

            EarthlyBranchThreeCombinationEvidence evidence,

            EarthlyBranchThreeCombinationFormationState state,

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

        List<EarthlyBranchThreeCombinationFormationReasonType>
                reasons =
                new ArrayList<>();

        if (
                evidence.completeCombination()
        ) {

            reasons.add(
                    EarthlyBranchThreeCombinationFormationReasonType
                            .COMPLETE_THREE_COMBINATION
            );

        } else if (
                evidence.halfCombination()
        ) {

            reasons.add(
                    EarthlyBranchThreeCombinationFormationReasonType
                            .HALF_COMBINATION_WITH_CARDINAL
            );

        } else {

            reasons.add(
                    EarthlyBranchThreeCombinationFormationReasonType
                            .PARTIAL_COMBINATION_WITHOUT_CARDINAL
            );
        }

        reasons.add(
                evidence.cardinalBranchIncluded()
                        ? EarthlyBranchThreeCombinationFormationReasonType
                        .CARDINAL_BRANCH_INCLUDED
                        : EarthlyBranchThreeCombinationFormationReasonType
                        .CARDINAL_BRANCH_ABSENT
        );

        reasons.add(
                evidence.compact()
                        ? EarthlyBranchThreeCombinationFormationReasonType
                        .COMPACT_PLACEMENT
                        : EarthlyBranchThreeCombinationFormationReasonType
                        .DISPERSED_PLACEMENT
        );

        reasons.add(
                evidence.monthBranchInvolved()
                        ? EarthlyBranchThreeCombinationFormationReasonType
                        .MONTH_BRANCH_INVOLVED
                        : EarthlyBranchThreeCombinationFormationReasonType
                        .MONTH_BRANCH_NOT_INVOLVED
        );

        if (
                evidence.dayBranchInvolved()
        ) {

            reasons.add(
                    EarthlyBranchThreeCombinationFormationReasonType
                            .DAY_BRANCH_INVOLVED
            );
        }

        if (
                targetSeasonalRate
                        >= SEASONAL_SUPPORTED_RATE
        ) {

            reasons.add(
                    EarthlyBranchThreeCombinationFormationReasonType
                            .TARGET_SEASONALLY_SUPPORTED
            );

        } else if (
                targetSeasonalRate
                        >= SEASONAL_NEUTRAL_RATE
        ) {

            reasons.add(
                    EarthlyBranchThreeCombinationFormationReasonType
                            .TARGET_SEASONALLY_NEUTRAL
            );

        } else {

            reasons.add(
                    EarthlyBranchThreeCombinationFormationReasonType
                            .TARGET_SEASONALLY_WEAK
            );
        }

        reasons.add(
                monthBranchPresenceWeight > 0
                        ? EarthlyBranchThreeCombinationFormationReasonType
                        .TARGET_PRESENT_IN_MONTH_BRANCH
                        : EarthlyBranchThreeCombinationFormationReasonType
                        .TARGET_ABSENT_FROM_MONTH_BRANCH
        );

        if (
                targetShareBasisPoints
                        >= TARGET_STRONG_BASIS_POINTS
        ) {

            reasons.add(
                    EarthlyBranchThreeCombinationFormationReasonType
                            .TARGET_ELEMENT_STRONG_IN_CHART
            );

        } else if (
                targetShareBasisPoints
                        >= TARGET_PRESENT_BASIS_POINTS
        ) {

            reasons.add(
                    EarthlyBranchThreeCombinationFormationReasonType
                            .TARGET_ELEMENT_PRESENT_IN_CHART
            );

        } else if (
                targetShareBasisPoints
                        >= TARGET_VERY_SCARCE_BASIS_POINTS
        ) {

            reasons.add(
                    EarthlyBranchThreeCombinationFormationReasonType
                            .TARGET_ELEMENT_SCARCE_IN_CHART
            );

        } else {

            reasons.add(
                    EarthlyBranchThreeCombinationFormationReasonType
                            .TARGET_ELEMENT_VERY_SCARCE_IN_CHART
            );
        }

        switch (
                state
        ) {

            case PROVISIONALLY_ESTABLISHED ->
                    reasons.add(
                            EarthlyBranchThreeCombinationFormationReasonType
                                    .PROVISIONAL_FORMATION_ESTABLISHED
                    );

            case PROVISIONALLY_PARTIAL ->
                    reasons.add(
                            EarthlyBranchThreeCombinationFormationReasonType
                                    .PROVISIONAL_PARTIAL_FORMATION
                    );

            case CONDITIONAL ->
                    reasons.add(
                            EarthlyBranchThreeCombinationFormationReasonType
                                    .FORMATION_REQUIRES_FURTHER_CONDITIONS
                    );

            case RELATION_ONLY ->
                    reasons.add(
                            EarthlyBranchThreeCombinationFormationReasonType
                                    .RELATION_EXISTS_WITHOUT_FORMATION
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