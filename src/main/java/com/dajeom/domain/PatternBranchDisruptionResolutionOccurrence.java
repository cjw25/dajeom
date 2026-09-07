package com.dajeom.domain;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 하나의 沖 / 刑 / 破 / 害에 대한
 * 회합 기반 해소 검토 결과.
 *
 * =================================================
 *
 * 예:
 *
 * 月酉 ↔ 日卯
 * = 沖
 *
 * 日卯 ↔ 時戌
 * = 六合
 *
 * 그러면:
 *
 * disruption
 * = 卯酉沖
 *
 * candidateCombinations
 * = 卯戌六合
 *
 * state
 * = RESOLUTION_CANDIDATE
 *
 * =================================================
 *
 * 주의:
 *
 * RESOLUTION_CANDIDATE
 * !=
 * 실제 沖 소멸 확정
 */
public record PatternBranchDisruptionResolutionOccurrence(

        PatternBranchDisruptionOccurrence disruption,

        List<PatternBranchCombinationOccurrence> candidateCombinations,

        PatternBranchDisruptionResolutionState state,

        Set<PatternBranchDisruptionResolutionReason> reasons

) {

    public PatternBranchDisruptionResolutionOccurrence {

        Objects.requireNonNull(
                disruption,
                "지지 교란은 필수입니다."
        );

        candidateCombinations =
                candidateCombinations == null
                        ? List.of()
                        : List.copyOf(candidateCombinations);

        Objects.requireNonNull(
                state,
                "해소 검토 상태는 필수입니다."
        );

        reasons =
                reasons == null
                        ? Set.of()
                        : Set.copyOf(reasons);

        for (
                PatternBranchCombinationOccurrence combination
                : candidateCombinations
        ) {

            Objects.requireNonNull(
                    combination,
                    "해소 후보 회합 목록에 null이 포함될 수 없습니다."
            );

            boolean involvesLeft =
                    combination.involvesPosition(
                            disruption.leftPosition()
                    );

            boolean involvesRight =
                    combination.involvesPosition(
                            disruption.rightPosition()
                    );

            /*
             * 교란 양쪽 중 정확히 한쪽만
             * 다른 회합으로 묶여야 해소 후보로 본다.
             *
             * 巳申처럼 동일 두 글자가
             * 六合이면서 刑/破인 경우,
             *
             * 같은 pair의 六合을
             * 자기 자신의 刑/破 해소라고
             * 자동 처리하지 않는다.
             */
            if (
                    involvesLeft
                            == involvesRight
            ) {

                throw new IllegalArgumentException(
                        "해소 후보 회합은 교란의 양쪽 중 정확히 한쪽만 포함해야 합니다."
                );
            }
        }

        if (
                state
                        == PatternBranchDisruptionResolutionState
                        .NO_RESOLUTION_CANDIDATE

                        &&

                        !candidateCombinations.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "NO_RESOLUTION_CANDIDATE 상태에는 회합 후보가 존재할 수 없습니다."
            );
        }

        if (
                state
                        != PatternBranchDisruptionResolutionState
                        .NO_RESOLUTION_CANDIDATE

                        &&

                        candidateCombinations.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "해소 후보 상태에는 최소 하나의 회합 후보가 필요합니다."
            );
        }
    }

    public boolean involvesMonthCommand() {

        return disruption
                .involvesMonthCommand();
    }

    public boolean hasResolutionCandidate() {

        return state
                != PatternBranchDisruptionResolutionState
                .NO_RESOLUTION_CANDIDATE;
    }

    public boolean uncontestedCandidate() {

        return state
                == PatternBranchDisruptionResolutionState
                .RESOLUTION_CANDIDATE;
    }

    public boolean contested() {

        return state
                == PatternBranchDisruptionResolutionState
                .CONTESTED_REVIEW_REQUIRED;
    }
}