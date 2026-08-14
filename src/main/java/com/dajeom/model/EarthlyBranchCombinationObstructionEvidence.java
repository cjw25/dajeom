package com.dajeom.model;

import com.dajeom.type.EarthlyBranchCombinationCandidateKind;
import com.dajeom.type.EarthlyBranchCombinationObstructionLevel;
import com.dajeom.type.EarthlyBranchCombinationObstructionReasonType;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.FiveElement;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 9차-7.
 *
 * 하나의 9-4 / 9-5 / 9-6 후보에 대한
 * 충·형·파·해 방해 분석 결과.
 *
 * =================================================
 *
 * 세 후보 타입 중 정확히 하나만 존재한다.
 *
 * sixCombinationEvidence
 *
 * 또는
 *
 * threeCombinationEvidence
 *
 * 또는
 *
 * directionalCombinationEvidence
 *
 * =================================================
 *
 * 후보를 하나의 공통 부모 타입으로
 * 억지로 변경하지 않고,
 * 기존 9-4/5/6 모델을 그대로 보존하면서
 * 9-7에서 통합 조회하기 위한 구조다.
 *
 * =================================================
 *
 * level
 *
 * NONE
 * MINOR
 * MEANINGFUL
 * SEVERE
 *
 * =================================================
 *
 * 이 단계에서도 원본 결합 후보를
 * 삭제하거나 state를 직접 수정하지 않는다.
 *
 * 실제 다운그레이드는 9-8 책임이다.
 */
public record EarthlyBranchCombinationObstructionEvidence(

        EarthlyBranchCombinationCandidateKind candidateKind,

        EarthlyBranchSixCombinationTransformationEvidence
        sixCombinationEvidence,

        EarthlyBranchThreeCombinationFormationEvidence
        threeCombinationEvidence,

        EarthlyBranchDirectionalCombinationFormationEvidence
        directionalCombinationEvidence,

        EarthlyBranchCombinationObstructionLevel level,

        List<EarthlyBranchCombinationObstructionRelation>
        obstructionRelations,

        int affectedMemberCount,

        boolean cardinalBranchAffected,

        boolean internalObstructionExists,

        List<EarthlyBranchCombinationObstructionReasonType>
        reasons

) {

    public EarthlyBranchCombinationObstructionEvidence {

        Objects.requireNonNull(
                candidateKind,
                "결합 후보 종류가 필요합니다."
        );

        Objects.requireNonNull(
                level,
                "결합 방해 수준이 필요합니다."
        );

        int sourceCount = 0;

        if (
                sixCombinationEvidence != null
        ) {

            sourceCount++;
        }

        if (
                threeCombinationEvidence != null
        ) {

            sourceCount++;
        }

        if (
                directionalCombinationEvidence != null
        ) {

            sourceCount++;
        }

        if (
                sourceCount != 1
        ) {

            throw new IllegalArgumentException(
                    "9-7 Evidence에는 9-4/9-5/9-6 원본 후보 중 정확히 하나만 존재해야 합니다."
            );
        }

        if (
                candidateKind.sixCombination()
                        && sixCombinationEvidence == null
        ) {

            throw new IllegalArgumentException(
                    "SIX_COMBINATION 후보에는 9-4 Evidence가 필요합니다."
            );
        }

        if (
                candidateKind.threeCombination()
                        && threeCombinationEvidence == null
        ) {

            throw new IllegalArgumentException(
                    "THREE_COMBINATION 후보에는 9-5 Evidence가 필요합니다."
            );
        }

        if (
                candidateKind.directionalCombination()
                        && directionalCombinationEvidence == null
        ) {

            throw new IllegalArgumentException(
                    "DIRECTIONAL_COMBINATION 후보에는 9-6 Evidence가 필요합니다."
            );
        }

        obstructionRelations =
                obstructionRelations == null
                        ? List.of()
                        : List.copyOf(
                        obstructionRelations
                );

        reasons =
                reasons == null
                        ? List.of()
                        : List.copyOf(
                        reasons
                );

        int expectedAffectedMemberCount =
                obstructionRelations
                        .stream()
                        .flatMap(
                                relation ->
                                        relation
                                                .affectedCandidatePositions()
                                                .stream()
                        )
                        .distinct()
                        .toList()
                        .size();

        if (
                affectedMemberCount
                        != expectedAffectedMemberCount
        ) {

            throw new IllegalArgumentException(
                    "영향받는 결합 구성원 수가 실제 방해 Evidence와 일치하지 않습니다."
            );
        }

        boolean expectedCardinalAffected =
                obstructionRelations
                        .stream()
                        .anyMatch(
                                EarthlyBranchCombinationObstructionRelation
                                        ::cardinalBranchAffected
                        );

        if (
                cardinalBranchAffected
                        != expectedCardinalAffected
        ) {

            throw new IllegalArgumentException(
                    "왕지 방해 상태가 실제 방해 Evidence와 일치하지 않습니다."
            );
        }

        boolean expectedInternalObstruction =
                obstructionRelations
                        .stream()
                        .anyMatch(
                                EarthlyBranchCombinationObstructionRelation
                                        ::internalToCandidate
                        );

        if (
                internalObstructionExists
                        != expectedInternalObstruction
        ) {

            throw new IllegalArgumentException(
                    "결합 내부 방해 상태가 실제 방해 Evidence와 일치하지 않습니다."
            );
        }

        if (
                level.none()
                        && !obstructionRelations.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "NONE 상태에는 방해 관계가 존재할 수 없습니다."
            );
        }

        if (
                !level.none()
                        && obstructionRelations.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "방해 상태에는 하나 이상의 방해 관계가 필요합니다."
            );
        }

        if (
                reasons.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "9-7 방해 판정에는 하나 이상의 근거가 필요합니다."
            );
        }
    }

    /*
     * =================================================
     * 기본 후보 정보
     * =================================================
     */

    public FiveElement targetElement() {

        if (
                sixCombinationEvidence != null
        ) {

            return sixCombinationEvidence
                    .targetElement();
        }

        if (
                threeCombinationEvidence != null
        ) {

            return threeCombinationEvidence
                    .targetElement();
        }

        return directionalCombinationEvidence
                .targetElement();
    }

    public List<EarthlyBranchPosition> candidatePositions() {

        if (
                sixCombinationEvidence != null
        ) {

            return List.of(
                    sixCombinationEvidence
                            .firstPosition(),

                    sixCombinationEvidence
                            .secondPosition()
            );
        }

        if (
                threeCombinationEvidence != null
        ) {

            return threeCombinationEvidence
                    .positions();
        }

        return directionalCombinationEvidence
                .positions();
    }

    public int candidateMemberCount() {

        return candidatePositions()
                .size();
    }

    public boolean involves(
            EarthlyBranchPosition position
    ) {

        if (
                position == null
        ) {

            return false;
        }

        return candidatePositions()
                .contains(
                        position
                );
    }

    public boolean monthBranchInvolved() {

        return involves(
                EarthlyBranchPosition.MONTH
        );
    }

    public boolean dayBranchInvolved() {

        return involves(
                EarthlyBranchPosition.DAY
        );
    }

    /*
     * =================================================
     * 기존 9-4/5/6 상태
     * =================================================
     */

    public int baseStateRank() {

        if (
                sixCombinationEvidence != null
        ) {

            return sixCombinationEvidence
                    .state()
                    .rank();
        }

        if (
                threeCombinationEvidence != null
        ) {

            return threeCombinationEvidence
                    .state()
                    .rank();
        }

        return directionalCombinationEvidence
                .state()
                .rank();
    }

    public boolean baseMeaningful() {

        if (
                sixCombinationEvidence != null
        ) {

            return sixCombinationEvidence
                    .meaningfulTransformation();
        }

        if (
                threeCombinationEvidence != null
        ) {

            return threeCombinationEvidence
                    .meaningfulFormation();
        }

        return directionalCombinationEvidence
                .meaningfulFormation();
    }

    public boolean baseCandidateRemains() {

        if (
                sixCombinationEvidence != null
        ) {

            return sixCombinationEvidence
                    .transformationCandidateRemains();
        }

        if (
                threeCombinationEvidence != null
        ) {

            return threeCombinationEvidence
                    .formationCandidateRemains();
        }

        return directionalCombinationEvidence
                .formationCandidateRemains();
    }

    /*
     * =================================================
     * 방해
     * =================================================
     */

    public boolean hasObstruction() {

        return level
                .hasObstruction();
    }

    public boolean minorObstruction() {

        return level
                .minor();
    }

    public boolean meaningfulObstruction() {

        return level
                .meaningful();
    }

    public boolean severeObstruction() {

        return level
                .severe();
    }

    public boolean requiresAdjustment() {

        return level
                .requiresAdjustment();
    }

    public int obstructionRelationCount() {

        return obstructionRelations
                .size();
    }

    public boolean multipleMembersAffected() {

        return affectedMemberCount >= 2;
    }

    public boolean multipleObstructionRelations() {

        return obstructionRelationCount() >= 2;
    }

    public boolean hasClash() {

        return obstructionRelations
                .stream()
                .anyMatch(
                        EarthlyBranchCombinationObstructionRelation
                                ::hasClash
                );
    }

    public boolean hasPunishment() {

        return obstructionRelations
                .stream()
                .anyMatch(
                        EarthlyBranchCombinationObstructionRelation
                                ::hasPunishment
                );
    }

    public boolean hasBreak() {

        return obstructionRelations
                .stream()
                .anyMatch(
                        EarthlyBranchCombinationObstructionRelation
                                ::hasBreak
                );
    }

    public boolean hasHarm() {

        return obstructionRelations
                .stream()
                .anyMatch(
                        EarthlyBranchCombinationObstructionRelation
                                ::hasHarm
                );
    }

    public boolean hasReason(
            EarthlyBranchCombinationObstructionReasonType reason
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