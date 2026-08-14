package com.dajeom.rule;

import com.dajeom.model.EarthlyBranchDirectionalCombinationEvidence;
import com.dajeom.type.EarthlyBranchDirectionalCombinationForm;
import com.dajeom.type.EarthlyBranchDirectionalCombinationFormationReasonType;
import com.dajeom.type.EarthlyBranchDirectionalCombinationFormationState;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class EarthlyBranchDirectionalCombinationFormationRule {

    public static final int TARGET_PRESENT_BASIS_POINTS =
            1_200;

    public static final int TARGET_STRONG_BASIS_POINTS =
            2_500;

    public static final int TARGET_VERY_SCARCE_BASIS_POINTS =
            500;

    public static final int SEASON_DIRECTION_ALIGNED_RATE =
            120;

    public static final int SEASONAL_SUPPORTED_RATE =
            110;

    public static final int SEASONAL_NEUTRAL_RATE =
            100;

    public EarthlyBranchDirectionalCombinationFormationState
    determineState(

            EarthlyBranchDirectionalCombinationEvidence evidence,

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
                    "완전 방합 내부의 종속 Pair는 9-6에서 별도 국 성립 대상으로 평가할 수 없습니다."
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

    public EarthlyBranchDirectionalCombinationFormationState
    determineState(

            EarthlyBranchDirectionalCombinationForm form,

            boolean compact,

            boolean monthBranchInvolved,

            int targetShareBasisPoints,

            int targetSeasonalRate,

            int monthBranchPresenceWeight

    ) {

        Objects.requireNonNull(
                form,
                "방합 구성 형태가 필요합니다."
        );

        validateInputs(
                targetShareBasisPoints,
                targetSeasonalRate,
                monthBranchPresenceWeight
        );

        boolean seasonDirectionAligned =
                targetSeasonalRate
                        >= SEASON_DIRECTION_ALIGNED_RATE;

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
                        == EarthlyBranchDirectionalCombinationForm
                        .COMPLETE
        ) {

            if (
                    seasonDirectionAligned
                            && (
                            targetStrong
                                    || targetPresentInMonth
                    )
            ) {

                return EarthlyBranchDirectionalCombinationFormationState
                        .PROVISIONALLY_ESTABLISHED;
            }

            if (
                    monthBranchInvolved
                            && seasonallySupported
                            && (
                            targetPresent
                                    || targetPresentInMonth
                    )
            ) {

                return EarthlyBranchDirectionalCombinationFormationState
                        .PROVISIONALLY_ESTABLISHED;
            }

            if (
                    seasonallySupported
                            || targetStrong
                            || targetPresentInMonth
            ) {

                return EarthlyBranchDirectionalCombinationFormationState
                        .PROVISIONALLY_PARTIAL;
            }

            return EarthlyBranchDirectionalCombinationFormationState
                    .CONDITIONAL;
        }

        if (
                form
                        == EarthlyBranchDirectionalCombinationForm
                        .PARTIAL_WITH_CARDINAL
        ) {

            if (
                    monthBranchInvolved
                            && seasonDirectionAligned
                            && (
                            targetPresent
                                    || targetPresentInMonth
                    )
            ) {

                return EarthlyBranchDirectionalCombinationFormationState
                        .PROVISIONALLY_PARTIAL;
            }

            if (
                    compact
                            && seasonDirectionAligned
                            && targetStrong
            ) {

                return EarthlyBranchDirectionalCombinationFormationState
                        .PROVISIONALLY_PARTIAL;
            }

            if (
                    seasonallySupported
                            && targetPresent
                            && (
                            monthBranchInvolved
                                    || compact
                    )
            ) {

                return EarthlyBranchDirectionalCombinationFormationState
                        .PROVISIONALLY_PARTIAL;
            }

            if (
                    seasonallySupported
                            || targetPresent
                            || targetPresentInMonth
                            || seasonallyNeutralOrBetter
            ) {

                return EarthlyBranchDirectionalCombinationFormationState
                        .CONDITIONAL;
            }

            return EarthlyBranchDirectionalCombinationFormationState
                    .RELATION_ONLY;
        }

        if (
                form
                        == EarthlyBranchDirectionalCombinationForm
                        .PARTIAL_WITHOUT_CARDINAL
        ) {

            if (
                    seasonDirectionAligned
                            && targetStrong
                            && targetPresentInMonth
            ) {

                return EarthlyBranchDirectionalCombinationFormationState
                        .CONDITIONAL;
            }

            if (
                    monthBranchInvolved
                            && seasonDirectionAligned
                            && targetPresent
            ) {

                return EarthlyBranchDirectionalCombinationFormationState
                        .CONDITIONAL;
            }

            return EarthlyBranchDirectionalCombinationFormationState
                    .RELATION_ONLY;
        }

        throw new IllegalStateException(
                "알 수 없는 방합 구성 형태입니다: "
                        + form
        );
    }

    public boolean meaningful(
            EarthlyBranchDirectionalCombinationFormationState state
    ) {

        Objects.requireNonNull(
                state
        );

        return state
                == EarthlyBranchDirectionalCombinationFormationState
                .PROVISIONALLY_ESTABLISHED

                || state
                == EarthlyBranchDirectionalCombinationFormationState
                .PROVISIONALLY_PARTIAL;
    }

    public boolean candidateRemains(
            EarthlyBranchDirectionalCombinationFormationState state
    ) {

        Objects.requireNonNull(
                state
        );

        return state
                != EarthlyBranchDirectionalCombinationFormationState
                .RELATION_ONLY;
    }

    public List<EarthlyBranchDirectionalCombinationFormationReasonType>
    buildReasons(

            EarthlyBranchDirectionalCombinationEvidence evidence,

            EarthlyBranchDirectionalCombinationFormationState state,

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

        List<EarthlyBranchDirectionalCombinationFormationReasonType>
                reasons =
                new ArrayList<>();

        if (
                evidence.completeCombination()
        ) {

            reasons.add(
                    EarthlyBranchDirectionalCombinationFormationReasonType
                            .COMPLETE_DIRECTIONAL_COMBINATION
            );

        } else if (
                evidence.partialWithCardinal()
        ) {

            reasons.add(
                    EarthlyBranchDirectionalCombinationFormationReasonType
                            .PARTIAL_DIRECTIONAL_WITH_CARDINAL
            );

        } else {

            reasons.add(
                    EarthlyBranchDirectionalCombinationFormationReasonType
                            .PARTIAL_DIRECTIONAL_WITHOUT_CARDINAL
            );
        }

        reasons.add(
                evidence.cardinalBranchIncluded()
                        ? EarthlyBranchDirectionalCombinationFormationReasonType
                        .CARDINAL_BRANCH_INCLUDED
                        : EarthlyBranchDirectionalCombinationFormationReasonType
                        .CARDINAL_BRANCH_ABSENT
        );

        reasons.add(
                evidence.compact()
                        ? EarthlyBranchDirectionalCombinationFormationReasonType
                        .COMPACT_PLACEMENT
                        : EarthlyBranchDirectionalCombinationFormationReasonType
                        .DISPERSED_PLACEMENT
        );

        reasons.add(
                evidence.monthBranchInvolved()
                        ? EarthlyBranchDirectionalCombinationFormationReasonType
                        .MONTH_BRANCH_INVOLVED
                        : EarthlyBranchDirectionalCombinationFormationReasonType
                        .MONTH_BRANCH_NOT_INVOLVED
        );

        if (
                evidence.dayBranchInvolved()
        ) {

            reasons.add(
                    EarthlyBranchDirectionalCombinationFormationReasonType
                            .DAY_BRANCH_INVOLVED
            );
        }

        if (
                targetSeasonalRate
                        >= SEASON_DIRECTION_ALIGNED_RATE
        ) {

            reasons.add(
                    EarthlyBranchDirectionalCombinationFormationReasonType
                            .TARGET_SEASON_DIRECTION_ALIGNED
            );

        } else if (
                targetSeasonalRate
                        >= SEASONAL_SUPPORTED_RATE
        ) {

            reasons.add(
                    EarthlyBranchDirectionalCombinationFormationReasonType
                            .TARGET_SEASONALLY_SUPPORTED
            );

        } else if (
                targetSeasonalRate
                        >= SEASONAL_NEUTRAL_RATE
        ) {

            reasons.add(
                    EarthlyBranchDirectionalCombinationFormationReasonType
                            .TARGET_SEASONALLY_NEUTRAL
            );

        } else {

            reasons.add(
                    EarthlyBranchDirectionalCombinationFormationReasonType
                            .TARGET_SEASONALLY_WEAK
            );
        }

        reasons.add(
                monthBranchPresenceWeight > 0
                        ? EarthlyBranchDirectionalCombinationFormationReasonType
                        .TARGET_PRESENT_IN_MONTH_BRANCH
                        : EarthlyBranchDirectionalCombinationFormationReasonType
                        .TARGET_ABSENT_FROM_MONTH_BRANCH
        );

        if (
                targetShareBasisPoints
                        >= TARGET_STRONG_BASIS_POINTS
        ) {

            reasons.add(
                    EarthlyBranchDirectionalCombinationFormationReasonType
                            .TARGET_ELEMENT_STRONG_IN_CHART
            );

        } else if (
                targetShareBasisPoints
                        >= TARGET_PRESENT_BASIS_POINTS
        ) {

            reasons.add(
                    EarthlyBranchDirectionalCombinationFormationReasonType
                            .TARGET_ELEMENT_PRESENT_IN_CHART
            );

        } else if (
                targetShareBasisPoints
                        >= TARGET_VERY_SCARCE_BASIS_POINTS
        ) {

            reasons.add(
                    EarthlyBranchDirectionalCombinationFormationReasonType
                            .TARGET_ELEMENT_SCARCE_IN_CHART
            );

        } else {

            reasons.add(
                    EarthlyBranchDirectionalCombinationFormationReasonType
                            .TARGET_ELEMENT_VERY_SCARCE_IN_CHART
            );
        }

        switch (
                state
        ) {

            case PROVISIONALLY_ESTABLISHED ->
                    reasons.add(
                            EarthlyBranchDirectionalCombinationFormationReasonType
                                    .PROVISIONAL_FORMATION_ESTABLISHED
                    );

            case PROVISIONALLY_PARTIAL ->
                    reasons.add(
                            EarthlyBranchDirectionalCombinationFormationReasonType
                                    .PROVISIONAL_PARTIAL_FORMATION
                    );

            case CONDITIONAL ->
                    reasons.add(
                            EarthlyBranchDirectionalCombinationFormationReasonType
                                    .FORMATION_REQUIRES_FURTHER_CONDITIONS
                    );

            case RELATION_ONLY ->
                    reasons.add(
                            EarthlyBranchDirectionalCombinationFormationReasonType
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