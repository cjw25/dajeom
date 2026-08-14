package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchDirectionalCombinationForm;
import com.dajeom.type.EarthlyBranchDirectionalCombinationFormationReasonType;
import com.dajeom.type.EarthlyBranchDirectionalCombinationFormationState;
import com.dajeom.type.EarthlyBranchDirectionalCombinationType;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.FiveElement;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 9차-6.
 *
 * 하나의 독립 방합계 Evidence에 대해
 * 방해·경쟁 검토 전 方局 성립 상태를 붙인 결과.
 *
 * =================================================
 *
 * sourceEvidence
 *
 * 8-5에서 탐지된:
 *
 * COMPLETE
 * PARTIAL_WITH_CARDINAL
 * PARTIAL_WITHOUT_CARDINAL
 *
 * 중 하나.
 *
 * =================================================
 *
 * Analyzer에서는 반드시:
 *
 * directionalCombinationAnalysis.independentRelations()
 *
 * 만 사용한다.
 *
 * 완전 방합 내부의 종속 Pair를
 * 국 성립 후보로 다시 계산하지 않는다.
 *
 * =================================================
 *
 * seasonDirectionAligned
 *
 * 방합 목표 오행이
 * 현재 월령 계절에서 旺 수준의
 * 직접적인 계절 방향을 얻고 있는가.
 *
 * 현재 다점 정책:
 *
 * seasonalRate >= 120
 *
 * =================================================
 *
 * 아직:
 *
 * 沖
 * 刑
 * 破
 * 害
 *
 * 및 六合·三合과의 경쟁은
 * 최종 적용하지 않는다.
 */
public record EarthlyBranchDirectionalCombinationFormationEvidence(

        EarthlyBranchDirectionalCombinationEvidence sourceEvidence,

        EarthlyBranchDirectionalCombinationFormationState state,

        FiveElement targetElement,

        int targetShareBasisPoints,

        int targetSeasonalRate,

        int monthBranchPresenceWeight,

        boolean seasonDirectionAligned,

        boolean targetSeasonallySupported,

        boolean targetPresentInMonthBranch,

        boolean targetStrongInChart,

        boolean targetPresentInChart,

        List<EarthlyBranchDirectionalCombinationFormationReasonType>
        reasons

) {

    public EarthlyBranchDirectionalCombinationFormationEvidence {

        Objects.requireNonNull(
                sourceEvidence,
                "8-5 방합 Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                state,
                "방합 국 성립 상태가 필요합니다."
        );

        Objects.requireNonNull(
                targetElement,
                "방합 목표 오행이 필요합니다."
        );

        if (
                sourceEvidence
                        .subordinateToCompleteCombination()
        ) {

            throw new IllegalArgumentException(
                    "완전 방합 내부의 종속 Pair Evidence는 9-6에서 독립적으로 평가할 수 없습니다."
            );
        }

        if (
                targetElement
                        != sourceEvidence
                        .nominalTransformationElement()
        ) {

            throw new IllegalArgumentException(
                    "방합 목표 오행이 8-5 명목상 목표 오행과 일치하지 않습니다."
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

        boolean expectedSeasonDirectionAligned =
                targetSeasonalRate >= 120;

        if (
                seasonDirectionAligned
                        != expectedSeasonDirectionAligned
        ) {

            throw new IllegalArgumentException(
                    "방합 목표 오행의 계절 방향 일치 상태가 계절 배율과 일치하지 않습니다."
            );
        }

        boolean expectedSeasonallySupported =
                targetSeasonalRate >= 110;

        if (
                targetSeasonallySupported
                        != expectedSeasonallySupported
        ) {

            throw new IllegalArgumentException(
                    "방합 목표 오행의 계절 지지 상태가 계절 배율과 일치하지 않습니다."
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
                    "방합 국 성립 판정에는 하나 이상의 근거가 필요합니다."
            );
        }
    }

    public EarthlyBranchDirectionalCombinationType combinationType() {

        return sourceEvidence
                .combinationType();
    }

    public EarthlyBranchDirectionalCombinationForm form() {

        return sourceEvidence
                .form();
    }

    public List<EarthlyBranchPosition> positions() {

        return sourceEvidence
                .positions();
    }

    public List<EarthlyBranch> branches() {

        return sourceEvidence
                .branches();
    }

    public boolean completeCombination() {

        return sourceEvidence
                .completeCombination();
    }

    public boolean partialWithCardinal() {

        return sourceEvidence
                .partialWithCardinal();
    }

    public boolean partialWithoutCardinal() {

        return sourceEvidence
                .partialWithoutCardinal();
    }

    public boolean cardinalBranchIncluded() {

        return sourceEvidence
                .cardinalBranchIncluded();
    }

    public boolean compact() {

        return sourceEvidence
                .compact();
    }

    public int positionSpan() {

        return sourceEvidence
                .positionSpan();
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

    public boolean provisionallyEstablished() {

        return state
                .provisionallyEstablished();
    }

    public boolean provisionallyPartial() {

        return state
                .provisionallyPartial();
    }

    public boolean conditional() {

        return state
                .conditional();
    }

    public boolean relationOnly() {

        return state
                .relationOnly();
    }

    public boolean meaningfulFormation() {

        return state
                .meaningfulFormation();
    }

    public boolean formationCandidateRemains() {

        return state
                .formationCandidateRemains();
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
            EarthlyBranchDirectionalCombinationFormationReasonType reason
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