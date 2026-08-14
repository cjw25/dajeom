package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.EarthlyBranchSixCombinationTransformationReasonType;
import com.dajeom.type.EarthlyBranchSixCombinationTransformationState;
import com.dajeom.type.EarthlyBranchSixCombinationType;
import com.dajeom.type.FiveElement;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 9차-4.
 *
 * 하나의 六合 Evidence에 대해
 * 방해 검토 전 기본 합화 상태를 붙인 결과.
 *
 * =================================================
 *
 * sourceEvidence
 *
 * 8-3에서 탐지한 실제 六合 관계.
 *
 * =================================================
 *
 * targetElement
 *
 * 육합의 명목상 합화 목표 오행.
 *
 * 子丑 -> 土
 * 寅亥 -> 木
 * 卯戌 -> 火
 * 辰酉 -> 金
 * 巳申 -> 水
 * 午未 -> 土
 *
 * =================================================
 *
 * targetShareBasisPoints
 *
 * 합화 적용 전 원국 전체에서
 * 목표 오행이 차지하는 기존 세력 비율.
 *
 * =================================================
 *
 * targetSeasonalRate
 *
 * 기존 3차 계절 분석에서 계산된
 * 목표 오행의 계절 배율.
 *
 * =================================================
 *
 * monthBranchPresenceWeight
 *
 * 월지 지장간 구조 안에서
 * 목표 오행이 갖는 기반.
 *
 * =================================================
 *
 * 주의:
 *
 * 이 Evidence에는 아직:
 *
 * - 沖
 * - 刑
 * - 破
 * - 害
 *
 * 에 의한 최종 방해를 적용하지 않는다.
 *
 * 해당 책임은 9-7이다.
 */
public record EarthlyBranchSixCombinationTransformationEvidence(

        EarthlyBranchSixCombinationEvidence sourceEvidence,

        EarthlyBranchSixCombinationTransformationState state,

        FiveElement targetElement,

        int targetShareBasisPoints,

        int targetSeasonalRate,

        int monthBranchPresenceWeight,

        boolean targetSeasonallySupported,

        boolean targetPresentInMonthBranch,

        boolean targetStrongInChart,

        boolean targetPresentInChart,

        List<EarthlyBranchSixCombinationTransformationReasonType>
        reasons

) {

    public EarthlyBranchSixCombinationTransformationEvidence {

        Objects.requireNonNull(
                sourceEvidence,
                "8-3 육합 Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                state,
                "육합 합화 상태가 필요합니다."
        );

        Objects.requireNonNull(
                targetElement,
                "육합 합화 목표 오행이 필요합니다."
        );

        if (
                targetElement
                        != sourceEvidence
                        .nominalTransformationElement()
        ) {

            throw new IllegalArgumentException(
                    "육합 목표 오행이 8-3 명목상 합화 오행과 일치하지 않습니다."
            );
        }

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

        boolean expectedSeasonallySupported =
                targetSeasonalRate >= 110;

        if (
                targetSeasonallySupported
                        != expectedSeasonallySupported
        ) {

            throw new IllegalArgumentException(
                    "목표 오행 계절 지지 상태가 계절 배율과 일치하지 않습니다."
            );
        }

        boolean expectedPresentInMonth =
                monthBranchPresenceWeight > 0;

        if (
                targetPresentInMonthBranch
                        != expectedPresentInMonth
        ) {

            throw new IllegalArgumentException(
                    "월지 목표 오행 존재 상태가 실제 가중치와 일치하지 않습니다."
            );
        }

        boolean expectedStrongInChart =
                targetShareBasisPoints >= 2_500;

        if (
                targetStrongInChart
                        != expectedStrongInChart
        ) {

            throw new IllegalArgumentException(
                    "목표 오행 강세 상태가 기존 오행 비율과 일치하지 않습니다."
            );
        }

        boolean expectedPresentInChart =
                targetShareBasisPoints >= 1_200;

        if (
                targetPresentInChart
                        != expectedPresentInChart
        ) {

            throw new IllegalArgumentException(
                    "목표 오행 존재 상태가 기존 오행 비율과 일치하지 않습니다."
            );
        }

        reasons =
                reasons == null
                        ? List.of()
                        : List.copyOf(
                        reasons
                );

        if (
                reasons.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "육합 합화 판정에는 하나 이상의 판정 근거가 필요합니다."
            );
        }
    }

    public EarthlyBranchSixCombinationType combinationType() {

        return sourceEvidence
                .combinationType();
    }

    public EarthlyBranchPosition firstPosition() {

        return sourceEvidence
                .firstPosition();
    }

    public EarthlyBranch firstBranch() {

        return sourceEvidence
                .firstBranch();
    }

    public EarthlyBranchPosition secondPosition() {

        return sourceEvidence
                .secondPosition();
    }

    public EarthlyBranch secondBranch() {

        return sourceEvidence
                .secondBranch();
    }

    public int positionDistance() {

        return sourceEvidence
                .positionDistance();
    }

    public boolean adjacent() {

        return sourceEvidence
                .adjacent();
    }

    public boolean dayBranchInvolved() {

        return sourceEvidence
                .dayBranchInvolved();
    }

    public boolean monthBranchInvolved() {

        return sourceEvidence
                .monthBranchInvolved();
    }

    public boolean yearBranchInvolved() {

        return sourceEvidence
                .yearBranchInvolved();
    }

    public boolean hourBranchInvolved() {

        return sourceEvidence
                .hourBranchInvolved();
    }

    public boolean provisionallyTransformed() {

        return state
                .provisionallyTransformed();
    }

    public boolean provisionallyPartial() {

        return state
                .provisionallyPartial();
    }

    public boolean conditional() {

        return state
                .conditional();
    }

    public boolean combinedNotTransformed() {

        return state
                .combinedNotTransformed();
    }

    public boolean meaningfulTransformation() {

        return state
                .meaningfulTransformation();
    }

    public boolean transformationCandidateRemains() {

        return state
                .transformationCandidateRemains();
    }

    public boolean targetSeasonallyNeutralOrBetter() {

        return targetSeasonalRate >= 100;
    }

    public boolean targetSeasonallyWeak() {

        return targetSeasonalRate < 100;
    }

    public boolean targetScarceInChart() {

        return targetShareBasisPoints < 1_200;
    }

    public boolean targetVeryScarceInChart() {

        return targetShareBasisPoints < 500;
    }

    public double targetSharePercent() {

        return targetShareBasisPoints / 100.0;
    }

    public boolean involves(
            EarthlyBranchPosition position
    ) {

        return sourceEvidence
                .involves(
                        position
                );
    }

    public boolean involves(
            EarthlyBranch branch
    ) {

        return sourceEvidence
                .involves(
                        branch
                );
    }

    public boolean hasReason(
            EarthlyBranchSixCombinationTransformationReasonType reason
    ) {

        if (
                reason == null
        ) {

            return false;
        }

        return reasons.contains(
                reason
        );
    }
}