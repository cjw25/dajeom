package com.dajeom.domain;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 최종 격국 하나를 아직 선택할 수 없는 경우.
 *
 * 대표적으로:
 *
 * STANDARD_CHANGE_REVIEW
 * STANDARD_MULTIPLE_CHANGE_REVIEW
 *
 * 에서 변격 후보 선택이 아직 끝나지 않은 상태.
 *
 * =================================================
 *
 * NatalPatternDecision과 일부러 분리한다.
 *
 * "검토 필요"를 억지로 특정 格으로 확정하지 않기 위해서다.
 */
public record NatalPatternReview(

        HeavenlyStem dayMaster,

        MonthCommandPatternSelectionPath selectionPath,

        List<MonthCommandPatternCandidate> reviewCandidates,

        Set<NatalPatternDecisionReason> reasons

) {

    public NatalPatternReview {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                selectionPath,
                "월령 선택 경로는 필수입니다."
        );

        reviewCandidates =
                reviewCandidates == null
                        ? List.of()
                        : List.copyOf(reviewCandidates);

        reasons =
                reasons == null
                        ? Set.of()
                        : Set.copyOf(reasons);

        if (
                selectionPath
                        != MonthCommandPatternSelectionPath
                        .STANDARD_CHANGE_REVIEW

                        &&

                        selectionPath
                                != MonthCommandPatternSelectionPath
                                .STANDARD_MULTIPLE_CHANGE_REVIEW
        ) {

            throw new IllegalArgumentException(
                    "NatalPatternReview는 표준 변격 검토 경로에서만 사용할 수 있습니다."
            );
        }

        if (reviewCandidates.isEmpty()) {

            throw new IllegalArgumentException(
                    "NatalPatternReview에는 최소 하나의 검토 후보가 필요합니다."
            );
        }
    }
}