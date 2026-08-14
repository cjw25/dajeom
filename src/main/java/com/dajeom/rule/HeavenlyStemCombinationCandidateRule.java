package com.dajeom.rule;

import com.dajeom.model.HeavenlyStemCombinationEvidence;
import com.dajeom.type.HeavenlyStemCombinationApplicability;
import com.dajeom.type.HeavenlyStemCombinationCandidateReasonType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class HeavenlyStemCombinationCandidateRule {

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

    public HeavenlyStemCombinationApplicability
    determineApplicability(

            HeavenlyStemCombinationEvidence evidence,

            int targetShareBasisPoints,

            int targetSeasonalRate,

            int monthBranchPresenceWeight

    ) {

        Objects.requireNonNull(
                evidence,
                "천간합 Evidence가 필요합니다."
        );

        return determineApplicability(
                evidence.adjacent(),
                targetShareBasisPoints,
                targetSeasonalRate,
                monthBranchPresenceWeight
        );
    }

    /**
     * 원국 궁위 Evidence에 종속되지 않는
     * 9-1 공통 판정 경계.
     *
     * 원국 Analyzer와 운 Analyzer가
     * 동일한 명리 판정표를 사용한다.
     */
    public HeavenlyStemCombinationApplicability
    determineApplicability(

            boolean adjacent,

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
                        targetPresentInMonth
                                || targetStrong
                )
        ) {

            return HeavenlyStemCombinationApplicability
                    .STRONG;
        }

        if (
                adjacent
                        && (
                        targetPresentInMonth
                                || targetPresent
                                || seasonallyNeutralOrBetter
                )
        ) {

            return HeavenlyStemCombinationApplicability
                    .ACTIVE;
        }

        if (
                !adjacent
                        && seasonallySupported
                        && targetPresentInMonth
                        && targetStrong
        ) {

            return HeavenlyStemCombinationApplicability
                    .ACTIVE;
        }

        if (
                adjacent
        ) {

            return HeavenlyStemCombinationApplicability
                    .CONDITIONAL;
        }

        if (
                (
                        seasonallySupported
                                && targetPresent
                )
                        || (
                        targetPresentInMonth
                                && targetPresent
                )
        ) {

            return HeavenlyStemCombinationApplicability
                    .CONDITIONAL;
        }

        return HeavenlyStemCombinationApplicability
                .WEAK;
    }

    public List<HeavenlyStemCombinationCandidateReasonType>
    buildReasons(

            HeavenlyStemCombinationEvidence evidence,

            HeavenlyStemCombinationApplicability applicability,

            int targetShareBasisPoints,

            int targetSeasonalRate,

            int monthBranchPresenceWeight

    ) {

        Objects.requireNonNull(
                evidence,
                "천간합 Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                applicability,
                "천간합 적용도가 필요합니다."
        );

        validateInputs(
                targetShareBasisPoints,
                targetSeasonalRate,
                monthBranchPresenceWeight
        );

        List<HeavenlyStemCombinationCandidateReasonType> reasons =
                new ArrayList<>();

        if (
                evidence.adjacent()
        ) {

            reasons.add(
                    HeavenlyStemCombinationCandidateReasonType
                            .ADJACENT_PAIR
            );

        } else {

            reasons.add(
                    HeavenlyStemCombinationCandidateReasonType
                            .DISTANT_PAIR
            );
        }

        if (
                evidence.dayMasterInvolved()
        ) {

            reasons.add(
                    HeavenlyStemCombinationCandidateReasonType
                            .DAY_MASTER_INVOLVED
            );

        } else {

            reasons.add(
                    HeavenlyStemCombinationCandidateReasonType
                            .NON_DAY_MASTER_COMBINATION
            );
        }

        if (
                targetSeasonalRate
                        >= SEASONAL_SUPPORTED_RATE
        ) {

            reasons.add(
                    HeavenlyStemCombinationCandidateReasonType
                            .TARGET_SEASONALLY_SUPPORTED
            );

        } else if (
                targetSeasonalRate
                        >= SEASONAL_NEUTRAL_RATE
        ) {

            reasons.add(
                    HeavenlyStemCombinationCandidateReasonType
                            .TARGET_SEASONALLY_NEUTRAL
            );

        } else {

            reasons.add(
                    HeavenlyStemCombinationCandidateReasonType
                            .TARGET_SEASONALLY_WEAK
            );
        }

        if (
                monthBranchPresenceWeight > 0
        ) {

            reasons.add(
                    HeavenlyStemCombinationCandidateReasonType
                            .TARGET_PRESENT_IN_MONTH_BRANCH
            );

        } else {

            reasons.add(
                    HeavenlyStemCombinationCandidateReasonType
                            .TARGET_ABSENT_FROM_MONTH_BRANCH
            );
        }

        if (
                targetShareBasisPoints
                        >= TARGET_STRONG_BASIS_POINTS
        ) {

            reasons.add(
                    HeavenlyStemCombinationCandidateReasonType
                            .TARGET_ELEMENT_STRONG_IN_CHART
            );

        } else if (
                targetShareBasisPoints
                        >= TARGET_PRESENT_BASIS_POINTS
        ) {

            reasons.add(
                    HeavenlyStemCombinationCandidateReasonType
                            .TARGET_ELEMENT_PRESENT_IN_CHART
            );

        } else if (
                targetShareBasisPoints
                        >= TARGET_VERY_SCARCE_BASIS_POINTS
        ) {

            reasons.add(
                    HeavenlyStemCombinationCandidateReasonType
                            .TARGET_ELEMENT_SCARCE_IN_CHART
            );

        } else {

            reasons.add(
                    HeavenlyStemCombinationCandidateReasonType
                            .TARGET_ELEMENT_VERY_SCARCE_IN_CHART
            );
        }

        switch (
                applicability
        ) {

            case STRONG ->
                    reasons.add(
                            HeavenlyStemCombinationCandidateReasonType
                                    .STRONG_BASE_APPLICABILITY
                    );

            case ACTIVE ->
                    reasons.add(
                            HeavenlyStemCombinationCandidateReasonType
                                    .ACTIVE_BASE_APPLICABILITY
                    );

            case CONDITIONAL ->
                    reasons.add(
                            HeavenlyStemCombinationCandidateReasonType
                                    .CONDITIONAL_BASE_APPLICABILITY
                    );

            case WEAK ->
                    reasons.add(
                            HeavenlyStemCombinationCandidateReasonType
                                    .WEAK_BASE_APPLICABILITY
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