package com.dajeom.model;

import com.dajeom.type.CombinationTransformationEffectReasonType;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.FiveElement;
import com.dajeom.type.TransformationOriginalFunctionState;
import com.dajeom.type.TransformationTargetActivationState;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 9차-9.
 *
 * 9-8 지지 결합 후보의
 * 구성원 지지 하나에 대한 기능 변화 Evidence.
 *
 * =================================================
 *
 * 지지는 천간보다 보수적으로 처리한다.
 *
 * 이유:
 *
 * 하나의 지지 안에는 여러 지장간이 존재하며,
 * 합국 성립만으로 원래 지장간을 모두
 * 삭제하는 방식은 지나치게 단순하다.
 *
 * =================================================
 *
 * 따라서 현재 9-9 정책에서는
 * 지지의 originalFunctionState를
 * STRONGLY_REDUCED까지 내리지 않는다.
 *
 * 최대:
 *
 * PARTIALLY_REDUCED
 *
 * 로 유지한다.
 *
 * =================================================
 *
 * 같은 지지가 여러 결합 후보에 들어가면
 * MemberEffect도 여러 개 존재할 수 있다.
 *
 * 예:
 *
 * 酉
 *
 * 辰酉六合 → Metal
 * 酉丑半合 → Metal
 * 申酉方合 → Metal
 *
 * 각각 별도의 Effect를 보존한다.
 *
 * 이 중 최종 효과 통합은 9-10에서 수행한다.
 */
public record EarthlyBranchTransformationMemberEffect(

        EarthlyBranchCombinationIntegratedCandidate sourceCandidate,

        EarthlyBranchPosition position,

        EarthlyBranch originalBranch,

        boolean cardinalMember,

        TransformationOriginalFunctionState originalFunctionState,

        FiveElement targetElement,

        TransformationTargetActivationState targetActivationState,

        List<CombinationTransformationEffectReasonType> reasons

) {

    public EarthlyBranchTransformationMemberEffect {

        Objects.requireNonNull(
                sourceCandidate,
                "9-8 지지 결합 통합 후보가 필요합니다."
        );

        Objects.requireNonNull(
                position,
                "지지 위치가 필요합니다."
        );

        Objects.requireNonNull(
                originalBranch,
                "원래 지지가 필요합니다."
        );

        Objects.requireNonNull(
                originalFunctionState,
                "원래 지지 기능 보존 상태가 필요합니다."
        );

        Objects.requireNonNull(
                targetElement,
                "결합 목표 오행이 필요합니다."
        );

        Objects.requireNonNull(
                targetActivationState,
                "목표 오행 활성 상태가 필요합니다."
        );

        if (
                !sourceCandidate.involves(
                        position
                )
        ) {

            throw new IllegalArgumentException(
                    "지지 위치가 9-8 결합 후보의 구성원이 아닙니다."
            );
        }

        EarthlyBranch expectedBranch =
                expectedBranchAt(
                        sourceCandidate,
                        position
                );

        if (
                expectedBranch
                        != originalBranch
        ) {

            throw new IllegalArgumentException(
                    "지지 위치와 원래 지지가 서로 일치하지 않습니다."
            );
        }

        if (
                targetElement
                        != sourceCandidate.targetElement()
        ) {

            throw new IllegalArgumentException(
                    "목표 오행이 9-8 결합 후보의 목표 오행과 일치하지 않습니다."
            );
        }

        if (
                originalFunctionState
                        .stronglyReduced()
        ) {

            throw new IllegalArgumentException(
                    "현재 9-9 정책에서는 지지 원래 기능을 STRONGLY_REDUCED로 처리하지 않습니다."
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
                    "지지 기능 변화 Evidence에는 하나 이상의 근거가 필요합니다."
            );
        }
    }

    private static EarthlyBranch expectedBranchAt(

            EarthlyBranchCombinationIntegratedCandidate candidate,

            EarthlyBranchPosition position

    ) {

        EarthlyBranchCombinationObstructionEvidence source =
                candidate.sourceEvidence();

        if (
                source.candidateKind()
                        .sixCombination()
        ) {

            EarthlyBranchSixCombinationTransformationEvidence
                    evidence =
                    source.sixCombinationEvidence();

            if (
                    evidence.firstPosition()
                            == position
            ) {

                return evidence.firstBranch();
            }

            if (
                    evidence.secondPosition()
                            == position
            ) {

                return evidence.secondBranch();
            }

            return null;
        }

        if (
                source.candidateKind()
                        .threeCombination()
        ) {

            List<EarthlyBranchPosition> positions =
                    source
                            .threeCombinationEvidence()
                            .positions();

            List<EarthlyBranch> branches =
                    source
                            .threeCombinationEvidence()
                            .branches();

            for (
                    int index = 0;
                    index < positions.size();
                    index++
            ) {

                if (
                        positions.get(index)
                                == position
                ) {

                    return branches.get(index);
                }
            }

            return null;
        }

        List<EarthlyBranchPosition> positions =
                source
                        .directionalCombinationEvidence()
                        .positions();

        List<EarthlyBranch> branches =
                source
                        .directionalCombinationEvidence()
                        .branches();

        for (
                int index = 0;
                index < positions.size();
                index++
        ) {

            if (
                    positions.get(index)
                            == position
            ) {

                return branches.get(index);
            }
        }

        return null;
    }

    public boolean dayBranch() {

        return position
                == EarthlyBranchPosition.DAY;
    }

    public boolean monthBranch() {

        return position
                == EarthlyBranchPosition.MONTH;
    }

    public boolean originalFunctionReduced() {

        return originalFunctionState
                .reduced();
    }

    public boolean targetActivated() {

        return targetActivationState
                .active();
    }

    public boolean strongTargetActivation() {

        return targetActivationState
                .strong();
    }

    public boolean hasReason(
            CombinationTransformationEffectReasonType reason
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