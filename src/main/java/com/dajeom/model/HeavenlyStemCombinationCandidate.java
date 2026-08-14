package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HeavenlyStemCombinationApplicability;
import com.dajeom.type.HeavenlyStemCombinationCandidateReasonType;
import com.dajeom.type.HeavenlyStemCombinationType;
import com.dajeom.type.VisibleStemPosition;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 9차-1.
 *
 * 8-1에서 발견된 천간합 하나에
 * 합화 검토를 위한 기본 환경 Evidence를 붙인 후보.
 *
 * =================================================
 *
 * sourceEvidence
 *
 * 8-1에서 실제 발견된 천간합 관계.
 *
 * =================================================
 *
 * targetElement
 *
 * 해당 합의 명목상 합화 목표 오행.
 *
 * 예:
 *
 * 丁壬
 * -> 木
 *
 * =================================================
 *
 * targetShareBasisPoints
 *
 * 합화가 발생하기 전 원국에서
 * 목표 오행이 차지하는 기존 오행 세력 비율.
 *
 * 0 ~ 10000.
 *
 * =================================================
 *
 * targetSeasonalRate
 *
 * 기존 3차 계절 오행 세력에서 가져온
 * 목표 오행의 계절 배율.
 *
 * 예:
 *
 * 旺 120
 * 相 110
 * 休 100
 * 囚 90
 * 死 80
 *
 * =================================================
 *
 * monthBranchPresenceWeight
 *
 * 월지에서 목표 오행이
 * 어느 정도 직접 기반을 갖는지 나타내는
 * 기존 계절 분석 Evidence.
 *
 * =================================================
 *
 * 중요:
 *
 * 이 Candidate는 아직:
 *
 * - 실제 合化
 * - 합거
 * - 합반
 * - 쟁합
 * - 투합
 * - 다중합 우선순위
 *
 * 를 확정하지 않는다.
 */
public record HeavenlyStemCombinationCandidate(

        HeavenlyStemCombinationEvidence sourceEvidence,

        HeavenlyStemCombinationApplicability applicability,

        FiveElement targetElement,

        int targetShareBasisPoints,

        int targetSeasonalRate,

        int monthBranchPresenceWeight,

        boolean targetSeasonallySupported,

        boolean targetPresentInMonthBranch,

        boolean targetStrongInChart,

        boolean targetPresentInChart,

        List<HeavenlyStemCombinationCandidateReasonType> reasons

) {

    public HeavenlyStemCombinationCandidate {

        Objects.requireNonNull(
                sourceEvidence,
                "8-1 천간합 Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                applicability,
                "천간합 후보 적용도가 필요합니다."
        );

        Objects.requireNonNull(
                targetElement,
                "합화 목표 오행이 필요합니다."
        );

        if (
                targetElement
                        != sourceEvidence
                        .nominalTransformationElement()
        ) {

            throw new IllegalArgumentException(
                    "후보 목표 오행이 천간합의 명목상 합화 오행과 일치하지 않습니다."
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
                    "계절 배율은 음수일 수 없습니다."
            );
        }

        if (
                monthBranchPresenceWeight < 0
        ) {

            throw new IllegalArgumentException(
                    "월지 존재 가중치는 음수일 수 없습니다."
            );
        }

        boolean expectedTargetPresentInMonthBranch =
                monthBranchPresenceWeight > 0;

        if (
                targetPresentInMonthBranch
                        != expectedTargetPresentInMonthBranch
        ) {

            throw new IllegalArgumentException(
                    "월지 목표 오행 존재 상태가 실제 가중치와 일치하지 않습니다."
            );
        }

        /*
         * 다점 9-1 기준:
         *
         * 2500bp 이상
         * = 원국에서 목표 오행 세력이 충분한 편.
         */
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

        /*
         * 1200bp 이상이면
         * 의미 있는 기존 기반으로 본다.
         */
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

        /*
         * 계절 배율 110 이상을
         * 계절적 적극 지원으로 본다.
         */
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

        reasons =
                reasons == null
                        ? List.of()
                        : List.copyOf(
                        reasons
                );
    }

    public HeavenlyStemCombinationType combinationType() {

        return sourceEvidence
                .combinationType();
    }

    public VisibleStemPosition firstPosition() {

        return sourceEvidence
                .firstPosition();
    }

    public VisibleStemPosition secondPosition() {

        return sourceEvidence
                .secondPosition();
    }

    public HeavenlyStem firstStem() {

        return sourceEvidence
                .firstStem();
    }

    public HeavenlyStem secondStem() {

        return sourceEvidence
                .secondStem();
    }

    public int positionDistance() {

        return sourceEvidence
                .positionDistance();
    }

    public boolean adjacent() {

        return sourceEvidence
                .adjacent();
    }

    public boolean dayMasterInvolved() {

        return sourceEvidence
                .dayMasterInvolved();
    }

    public boolean distant() {

        return !adjacent();
    }

    public boolean strong() {

        return applicability
                .strong();
    }

    public boolean active() {

        return applicability
                .active();
    }

    public boolean conditional() {

        return applicability
                .conditional();
    }

    public boolean weak() {

        return applicability
                .weak();
    }

    public boolean meaningfulCandidate() {

        return applicability
                .meaningful();
    }

    public boolean requiresFurtherReview() {

        return applicability
                .requiresFurtherReview();
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

    public boolean hasReason(
            HeavenlyStemCombinationCandidateReasonType reason
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

    public boolean involves(
            VisibleStemPosition position
    ) {

        return sourceEvidence
                .involves(
                        position
                );
    }

    public boolean involves(
            HeavenlyStem stem
    ) {

        return sourceEvidence
                .involves(
                        stem
                );
    }
}