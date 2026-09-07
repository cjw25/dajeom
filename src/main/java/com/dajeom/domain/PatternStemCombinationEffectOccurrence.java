package com.dajeom.domain;

import java.util.Objects;
import java.util.Set;

/**
 * 하나의 天干五合 occurrence에 대한
 * 구조적 효과 판정.
 *
 * =================================================
 *
 * 예:
 *
 * 日乙 + 月庚
 *
 * → SELF_COMBINATION_NOT_REMOVAL
 *
 * =================================================
 *
 * 年庚 + 月乙
 * 日甲
 *
 * → NON_DAY_MASTER_REMOVAL_CANDIDATE
 *
 * =================================================
 *
 * 月壬 + 日丁 + 時壬
 *
 * → 두 壬이 丁에 가까이 붙어 있으므로
 *   COMPETING_COMBINATION_REVIEW_REQUIRED
 *
 * =================================================
 *
 * 중요:
 *
 * NON_DAY_MASTER_REMOVAL_CANDIDATE도
 * 아직 최종 合去 확정은 아니다.
 *
 * 이후:
 *
 * - 다른 천간의 克으로 合이 막히는가
 * - 合化 조건
 * - 추가 爭合
 *
 * 등을 더 확인한다.
 */
public record PatternStemCombinationEffectOccurrence(

        PatternStemCombinationOccurrence combination,

        PatternStemCombinationEffectState state,

        Set<PatternStemCombinationEffectReason> reasons

) {

    public PatternStemCombinationEffectOccurrence {

        Objects.requireNonNull(
                combination,
                "천간합 occurrence는 필수입니다."
        );

        Objects.requireNonNull(
                state,
                "천간합 효과 상태는 필수입니다."
        );

        reasons =
                reasons == null
                        ? Set.of()
                        : Set.copyOf(reasons);

        if (reasons.isEmpty()) {

            throw new IllegalArgumentException(
                    "천간합 효과에는 최소 하나의 판정 이유가 필요합니다."
            );
        }
    }

    public boolean selfCombination() {

        return state
                == PatternStemCombinationEffectState
                .SELF_COMBINATION_NOT_REMOVAL;
    }

    public boolean removalCandidate() {

        return state
                == PatternStemCombinationEffectState
                .NON_DAY_MASTER_REMOVAL_CANDIDATE;
    }

    public boolean competingReviewRequired() {

        return state
                == PatternStemCombinationEffectState
                .COMPETING_COMBINATION_REVIEW_REQUIRED;
    }

    public boolean distantReviewRequired() {

        return state
                == PatternStemCombinationEffectState
                .DISTANT_COMBINATION_REVIEW_REQUIRED;
    }

    public boolean reviewRequired() {

        return competingReviewRequired()
                ||
                distantReviewRequired();
    }

    public boolean involvesTenGod(
            MonthCommandPatternCandidateType type
    ) {

        return combination
                .involvesTenGod(type);
    }

    public boolean betweenTenGods(

            MonthCommandPatternCandidateType first,

            MonthCommandPatternCandidateType second

    ) {

        return combination
                .betweenTenGods(
                        first,
                        second
                );
    }
}