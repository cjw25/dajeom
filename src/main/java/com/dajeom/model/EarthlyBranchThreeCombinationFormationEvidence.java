package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.EarthlyBranchThreeCombinationForm;
import com.dajeom.type.EarthlyBranchThreeCombinationFormationReasonType;
import com.dajeom.type.EarthlyBranchThreeCombinationFormationState;
import com.dajeom.type.EarthlyBranchThreeCombinationType;
import com.dajeom.type.FiveElement;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 9차-5.
 *
 * 하나의 독립적인 삼합계 Evidence에 대해
 * 방해 검토 전 국 성립 상태를 붙인 결과.
 *
 * =================================================
 *
 * sourceEvidence
 *
 * 8-4에서 탐지된:
 *
 * COMPLETE
 * HALF_WITH_CARDINAL
 * PARTIAL_WITHOUT_CARDINAL
 *
 * 중 하나.
 *
 * =================================================
 *
 * 중요:
 *
 * 8-4 완전 삼합 내부에 포함된 Pair Evidence는
 * 9-5 입력으로 사용하지 않는다.
 *
 * 즉 Analyzer는:
 *
 * threeCombinationAnalysis.independentRelations()
 *
 * 만 사용한다.
 *
 * =================================================
 *
 * 이 단계에서는 아직:
 *
 * 沖
 * 刑
 * 破
 * 害
 *
 * 및 六合/方合과의 경쟁을
 * 최종 반영하지 않는다.
 */
public record EarthlyBranchThreeCombinationFormationEvidence(

        EarthlyBranchThreeCombinationEvidence sourceEvidence,

        EarthlyBranchThreeCombinationFormationState state,

        FiveElement targetElement,

        int targetShareBasisPoints,

        int targetSeasonalRate,

        int monthBranchPresenceWeight,

        boolean targetSeasonallySupported,

        boolean targetPresentInMonthBranch,

        boolean targetStrongInChart,

        boolean targetPresentInChart,

        List<EarthlyBranchThreeCombinationFormationReasonType>
        reasons

) {

    public EarthlyBranchThreeCombinationFormationEvidence {

        Objects.requireNonNull(
                sourceEvidence,
                "8-4 삼합 Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                state,
                "삼합 국 성립 상태가 필요합니다."
        );

        Objects.requireNonNull(
                targetElement,
                "삼합 목표 오행이 필요합니다."
        );

        if (
                sourceEvidence
                        .subordinateToCompleteCombination()
        ) {

            throw new IllegalArgumentException(
                    "완전 삼합 내부의 종속 Pair Evidence는 9-5에서 독립적으로 평가할 수 없습니다."
            );
        }

        if (
                targetElement
                        != sourceEvidence
                        .nominalTransformationElement()
        ) {

            throw new IllegalArgumentException(
                    "삼합 목표 오행이 8-4 명목상 목표 오행과 일치하지 않습니다."
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
                    "삼합 국 성립 판정에는 하나 이상의 근거가 필요합니다."
            );
        }
    }

    public EarthlyBranchThreeCombinationType combinationType() {

        return sourceEvidence
                .combinationType();
    }

    public EarthlyBranchThreeCombinationForm form() {

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

    public boolean halfCombination() {

        return sourceEvidence
                .halfCombination();
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
            EarthlyBranchThreeCombinationFormationReasonType reason
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