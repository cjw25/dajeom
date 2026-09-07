package com.dajeom.domain;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 하나의 天干五合 occurrence가
 * 실제 격국 판정에 사용할 수 있는 상태인지 나타낸다.
 */
public record PatternStemCombinationActivationOccurrence(

        PatternStemCombinationEffectOccurrence effect,

        PatternStemCombinationActivationState state,

        List<PatternTenGodNode> interveningStems,

        Set<PatternStemCombinationActivationReason> reasons

) {

    public PatternStemCombinationActivationOccurrence {

        Objects.requireNonNull(
                effect,
                "천간합 Effect는 필수입니다."
        );

        Objects.requireNonNull(
                state,
                "천간합 Activation 상태는 필수입니다."
        );

        interveningStems =
                interveningStems == null
                        ? List.of()
                        : List.copyOf(
                        interveningStems
                );

        reasons =
                reasons == null
                        ? Set.of()
                        : Set.copyOf(
                        reasons
                );

        for (
                PatternTenGodNode node
                : interveningStems
        ) {

            Objects.requireNonNull(
                    node,
                    "중간 천간 목록에 null이 포함될 수 없습니다."
            );

            if (node.hidden()) {

                throw new IllegalArgumentException(
                        "interveningStems에는 지장간이 들어갈 수 없습니다."
                );
            }
        }

        if (reasons.isEmpty()) {

            throw new IllegalArgumentException(
                    "천간합 Activation에는 최소 하나의 판정 이유가 필요합니다."
            );
        }
    }

    public PatternStemCombinationOccurrence combination() {

        return effect.combination();
    }

    public boolean selfCombination() {

        return state
                == PatternStemCombinationActivationState
                .SELF_COMBINATION_NOT_REMOVAL;
    }

    public boolean activeNonDayMasterCombination() {

        return state
                == PatternStemCombinationActivationState
                .ACTIVE_NON_DAY_MASTER_COMBINATION;
    }

    public boolean blockedByInterveningControl() {

        return state
                == PatternStemCombinationActivationState
                .BLOCKED_BY_INTERVENING_CONTROL;
    }

    public boolean distantWeakCombination() {

        return state
                == PatternStemCombinationActivationState
                .DISTANT_WEAK_COMBINATION;
    }

    public boolean competingReviewRequired() {

        return state
                == PatternStemCombinationActivationState
                .COMPETING_REVIEW_REQUIRED;
    }

    /**
     * 合去/合一留一 같은 후속 Rule에서
     * 실제 active 합으로 사용 가능한가.
     *
     * 本身之合 / 爭合 / 遠合 / 制約合은 제외.
     */
    public boolean usableAsNonDayMasterRemoval() {

        return activeNonDayMasterCombination();
    }

    public boolean involvesTenGod(
            MonthCommandPatternCandidateType type
    ) {

        Objects.requireNonNull(
                type,
                "십성 타입은 필수입니다."
        );

        return combination()
                .involvesTenGod(type);
    }

    public boolean betweenTenGods(

            MonthCommandPatternCandidateType first,

            MonthCommandPatternCandidateType second

    ) {

        return combination()
                .betweenTenGods(
                        first,
                        second
                );
    }
}