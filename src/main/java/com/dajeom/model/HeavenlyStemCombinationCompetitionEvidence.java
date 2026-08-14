package com.dajeom.model;

import com.dajeom.type.HeavenlyStemCombinationCompetitionReasonType;
import com.dajeom.type.HeavenlyStemCombinationCompetitionState;
import com.dajeom.type.VisibleStemPosition;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 9차-2.
 *
 * 하나의 9-1 천간합 후보에 대한
 * 경쟁·쟁합·외부 방해 Evidence.
 *
 * =================================================
 *
 * candidate
 *
 * 9-1 기본 합화 후보.
 *
 * =================================================
 *
 * competingCandidates
 *
 * 현재 candidate와 하나 이상의
 * 동일 천간 위치를 공유하는 다른 합 후보.
 *
 * 예:
 *
 * YEAR 己
 * DAY 甲
 * HOUR 己
 *
 * candidate A
 * YEAR己 - DAY甲
 *
 * candidate B
 * DAY甲 - HOUR己
 *
 * 두 후보는 DAY 위치를 공유하므로
 * 서로 competingCandidates가 된다.
 *
 * =================================================
 *
 * externalObstructions
 *
 * 합 Pair 자체가 아닌 제3의 천간과 발생한
 * 충·극 Evidence 중 실제 방해 구조.
 *
 * =================================================
 *
 * 중요:
 *
 * 丙辛合에서 丙火가 辛金을 극하는 것처럼
 * 합 Pair 내부 자체의 CONTROL_ONLY Evidence는
 * 외부 방해에 포함하지 않는다.
 */
public record HeavenlyStemCombinationCompetitionEvidence(

        HeavenlyStemCombinationCandidate candidate,

        HeavenlyStemCombinationCompetitionState state,

        List<HeavenlyStemCombinationCandidate>
        competingCandidates,

        List<HeavenlyStemCombinationCandidate>
        equalOrStrongerCompetingCandidates,

        List<HeavenlyStemControlEvidence>
        externalObstructions,

        boolean adjacentCompetitorExists,

        boolean adjacentExternalObstructionExists,

        boolean dayMasterMultipleCombination,

        List<HeavenlyStemCombinationCompetitionReasonType>
        reasons

) {

    public HeavenlyStemCombinationCompetitionEvidence {

        Objects.requireNonNull(
                candidate,
                "9-1 천간합 후보가 필요합니다."
        );

        Objects.requireNonNull(
                state,
                "천간합 경쟁 상태가 필요합니다."
        );

        competingCandidates =
                competingCandidates == null
                        ? List.of()
                        : List.copyOf(
                        competingCandidates
                );

        equalOrStrongerCompetingCandidates =
                equalOrStrongerCompetingCandidates == null
                        ? List.of()
                        : List.copyOf(
                        equalOrStrongerCompetingCandidates
                );

        externalObstructions =
                externalObstructions == null
                        ? List.of()
                        : List.copyOf(
                        externalObstructions
                );

        reasons =
                reasons == null
                        ? List.of()
                        : List.copyOf(
                        reasons
                );

        if (
                competingCandidates.contains(
                        candidate
                )
        ) {

            throw new IllegalArgumentException(
                    "현재 후보 자신을 경쟁 후보로 등록할 수 없습니다."
            );
        }

        for (
                HeavenlyStemCombinationCandidate competitor
                : competingCandidates
        ) {

            if (
                    !sharesMember(
                            candidate,
                            competitor
                    )
            ) {

                throw new IllegalArgumentException(
                        "경쟁 후보는 현재 후보와 하나 이상의 천간 위치를 공유해야 합니다."
                );
            }
        }

        for (
                HeavenlyStemCombinationCandidate competitor
                : equalOrStrongerCompetingCandidates
        ) {

            if (
                    !competingCandidates.contains(
                            competitor
                    )
            ) {

                throw new IllegalArgumentException(
                        "동급/상위 경쟁 후보는 전체 경쟁 후보 목록에 포함되어야 합니다."
                );
            }

            if (
                    competitor
                            .applicability()
                            .rank()

                            < candidate
                            .applicability()
                            .rank()
            ) {

                throw new IllegalArgumentException(
                        "동급/상위 경쟁 후보의 적용도가 현재 후보보다 낮습니다."
                );
            }
        }

        boolean expectedAdjacentCompetitorExists =
                competingCandidates
                        .stream()
                        .anyMatch(
                                HeavenlyStemCombinationCandidate
                                        ::adjacent
                        );

        if (
                adjacentCompetitorExists
                        != expectedAdjacentCompetitorExists
        ) {

            throw new IllegalArgumentException(
                    "인접 경쟁 후보 상태가 실제 경쟁 후보와 일치하지 않습니다."
            );
        }

        boolean expectedAdjacentExternalObstruction =
                externalObstructions
                        .stream()
                        .anyMatch(
                                HeavenlyStemControlEvidence
                                        ::adjacent
                        );

        if (
                adjacentExternalObstructionExists
                        != expectedAdjacentExternalObstruction
        ) {

            throw new IllegalArgumentException(
                    "인접 외부 방해 상태가 실제 Evidence와 일치하지 않습니다."
            );
        }

        if (
                state.uncontested()
                        && (
                        !competingCandidates.isEmpty()
                                || !externalObstructions.isEmpty()
                )
        ) {

            throw new IllegalArgumentException(
                    "UNCONTESTED 상태에는 경쟁 또는 외부 방해 Evidence가 존재할 수 없습니다."
            );
        }

        if (
                state.competing()
                        && (
                        competingCandidates.isEmpty()
                                || !equalOrStrongerCompetingCandidates
                                .isEmpty()
                                || !externalObstructions.isEmpty()
                )
        ) {

            throw new IllegalArgumentException(
                    "COMPETING 상태는 더 약한 경쟁 후보만 존재해야 합니다."
            );
        }

        if (
                state.contested()
                        && (
                        equalOrStrongerCompetingCandidates
                                .isEmpty()

                                || !externalObstructions
                                .isEmpty()
                )
        ) {

            throw new IllegalArgumentException(
                    "CONTESTED 상태는 동급/상위 경쟁 후보가 있고 외부 방해가 없어야 합니다."
            );
        }

        if (
                state.obstructed()
                        && externalObstructions
                        .isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "OBSTRUCTED 상태에는 외부 방해 Evidence가 필요합니다."
            );
        }

        if (
                dayMasterMultipleCombination
                        && !candidate.dayMasterInvolved()
        ) {

            throw new IllegalArgumentException(
                    "일간 다중합 상태는 일간이 현재 후보에 참여할 때만 가능합니다."
            );
        }
    }

    private static boolean sharesMember(

            HeavenlyStemCombinationCandidate first,

            HeavenlyStemCombinationCandidate second

    ) {

        return first.involves(
                second.firstPosition()
        )
                || first.involves(
                second.secondPosition()
        );
    }

    public boolean hasCompetition() {

        return !competingCandidates
                .isEmpty();
    }

    public int competingCandidateCount() {

        return competingCandidates
                .size();
    }

    public boolean hasEqualOrStrongerCompetitor() {

        return !equalOrStrongerCompetingCandidates
                .isEmpty();
    }

    public int equalOrStrongerCompetitorCount() {

        return equalOrStrongerCompetingCandidates
                .size();
    }

    public boolean hasExternalObstruction() {

        return !externalObstructions
                .isEmpty();
    }

    public int externalObstructionCount() {

        return externalObstructions
                .size();
    }

    public boolean multipleExternalObstructions() {

        return externalObstructionCount() >= 2;
    }

    public boolean multipleCompetitors() {

        return competingCandidateCount() >= 2;
    }

    public boolean uncontested() {

        return state.uncontested();
    }

    public boolean competing() {

        return state.competing();
    }

    public boolean contested() {

        return state.contested();
    }

    public boolean obstructed() {

        return state.obstructed();
    }

    /**
     * 9-3에서 특별히 검토해야 할 후보.
     */
    public boolean requiresStrongReview() {

        return state
                .requiresCompetitionReview();
    }

    public boolean hasReason(
            HeavenlyStemCombinationCompetitionReasonType reason
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

    /**
     * 현재 후보에서 경쟁이 발생하는
     * 원국 위치 목록.
     *
     * 경쟁 후보와 공유되는 위치를 반환한다.
     */
    public List<VisibleStemPosition> contestedPositions() {

        return competingCandidates
                .stream()
                .flatMap(
                        competitor ->
                                List.of(
                                                candidate.firstPosition(),
                                                candidate.secondPosition()
                                        )
                                        .stream()
                                        .filter(
                                                competitor::involves
                                        )
                )
                .distinct()
                .toList();
    }

    /**
     * 제3 천간 방해를 직접 받는
     * 합 구성원의 위치.
     */
    public List<VisibleStemPosition> obstructedMemberPositions() {

        return externalObstructions
                .stream()
                .flatMap(
                        obstruction ->
                                List.of(
                                                candidate.firstPosition(),
                                                candidate.secondPosition()
                                        )
                                        .stream()
                                        .filter(
                                                obstruction::involves
                                        )
                )
                .distinct()
                .toList();
    }
}