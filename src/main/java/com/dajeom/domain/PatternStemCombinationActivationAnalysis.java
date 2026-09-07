package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 원국 天干五合 전체의 activation 결과.
 *
 * =================================================
 *
 * 이후 격국 Rule에서:
 *
 * - 合殺留官
 * - 合官留殺
 * - 合殺存財
 * - 合財存印
 * - 食神合殺
 * - 合一留一
 *
 * 등을 판단할 기반으로 사용한다.
 *
 * =================================================
 *
 * 아직 合化는 별도 문제다.
 */
public record PatternStemCombinationActivationAnalysis(

        HeavenlyStem dayMaster,

        List<PatternStemCombinationActivationOccurrence> activations

) {

    public PatternStemCombinationActivationAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        activations =
                activations == null
                        ? List.of()
                        : List.copyOf(
                        activations
                );

        for (
                PatternStemCombinationActivationOccurrence activation
                : activations
        ) {

            Objects.requireNonNull(
                    activation,
                    "Activation 목록에 null이 포함될 수 없습니다."
            );
        }
    }

    public List<PatternStemCombinationActivationOccurrence>
    activeNonDayMasterCombinations() {

        return activations
                .stream()
                .filter(
                        PatternStemCombinationActivationOccurrence
                                ::activeNonDayMasterCombination
                )
                .toList();
    }

    public boolean hasActiveNonDayMasterCombination() {

        return !activeNonDayMasterCombinations()
                .isEmpty();
    }

    public List<PatternStemCombinationActivationOccurrence>
    activeCombinationsInvolving(
            MonthCommandPatternCandidateType type
    ) {

        Objects.requireNonNull(
                type,
                "십성 타입은 필수입니다."
        );

        return activations
                .stream()
                .filter(
                        PatternStemCombinationActivationOccurrence
                                ::usableAsNonDayMasterRemoval
                )
                .filter(
                        activation ->
                                activation
                                        .involvesTenGod(type)
                )
                .toList();
    }

    public boolean hasActiveCombinationFor(
            MonthCommandPatternCandidateType type
    ) {

        return !activeCombinationsInvolving(type)
                .isEmpty();
    }

    public boolean hasActiveCombinationBetween(

            MonthCommandPatternCandidateType first,

            MonthCommandPatternCandidateType second

    ) {

        return activations
                .stream()
                .filter(
                        PatternStemCombinationActivationOccurrence
                                ::usableAsNonDayMasterRemoval
                )
                .anyMatch(
                        activation ->
                                activation
                                        .betweenTenGods(
                                                first,
                                                second
                                        )
                );
    }

    public boolean directOfficerActiveRemoval() {

        return hasActiveCombinationFor(
                MonthCommandPatternCandidateType
                        .DIRECT_OFFICER
        );
    }

    public boolean sevenKillingsActiveRemoval() {

        return hasActiveCombinationFor(
                MonthCommandPatternCandidateType
                        .SEVEN_KILLINGS
        );
    }

    public boolean wealthActiveRemoval() {

        return hasActiveCombinationFor(
                MonthCommandPatternCandidateType
                        .DIRECT_WEALTH
        )
                ||
                hasActiveCombinationFor(
                        MonthCommandPatternCandidateType
                                .INDIRECT_WEALTH
                );
    }

    public boolean resourceActiveRemoval() {

        return hasActiveCombinationFor(
                MonthCommandPatternCandidateType
                        .DIRECT_RESOURCE
        )
                ||
                hasActiveCombinationFor(
                        MonthCommandPatternCandidateType
                                .INDIRECT_RESOURCE
                );
    }

    /**
     * 食神과 七殺 자체가 직접 五合하는
     * active pair인지 확인.
     *
     * 주의:
     *
     * 대부분의 실제 合殺 구조는
     * 七殺과 그 五合 상대 십성을 보는 것이므로
     * 이 helper 하나만으로 合殺存財를 확정하지 않는다.
     */
    public boolean eatingGodSevenKillingsActiveCombination() {

        return hasActiveCombinationBetween(
                MonthCommandPatternCandidateType
                        .EATING_GOD,
                MonthCommandPatternCandidateType
                        .SEVEN_KILLINGS
        );
    }

    public boolean hasBlockedCombinationFor(
            MonthCommandPatternCandidateType type
    ) {

        return activations
                .stream()
                .filter(
                        PatternStemCombinationActivationOccurrence
                                ::blockedByInterveningControl
                )
                .anyMatch(
                        activation ->
                                activation
                                        .involvesTenGod(type)
                );
    }

    public boolean hasCompetingCombinationFor(
            MonthCommandPatternCandidateType type
    ) {

        return activations
                .stream()
                .filter(
                        PatternStemCombinationActivationOccurrence
                                ::competingReviewRequired
                )
                .anyMatch(
                        activation ->
                                activation
                                        .involvesTenGod(type)
                );
    }

    public boolean hasDistantCombinationFor(
            MonthCommandPatternCandidateType type
    ) {

        return activations
                .stream()
                .filter(
                        PatternStemCombinationActivationOccurrence
                                ::distantWeakCombination
                )
                .anyMatch(
                        activation ->
                                activation
                                        .involvesTenGod(type)
                );
    }
}