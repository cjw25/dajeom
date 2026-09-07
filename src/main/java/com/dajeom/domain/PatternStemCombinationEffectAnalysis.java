package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 원국 天干五合 전체의 효과 분석.
 *
 * =================================================
 *
 * 이 객체로 구분 가능:
 *
 * - 本身之合
 * - 비일간 合去 후보
 * - 爭合 검토
 * - 隔位 검토
 *
 * =================================================
 *
 * 아직 최종 확정하지 않는 것:
 *
 * - 合去 확정
 * - 合化
 * - 合而不化
 * - 合留
 *
 * =================================================
 */
public record PatternStemCombinationEffectAnalysis(

        HeavenlyStem dayMaster,

        List<PatternStemCombinationEffectOccurrence> effects

) {

    public PatternStemCombinationEffectAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        effects =
                effects == null
                        ? List.of()
                        : List.copyOf(effects);

        for (
                PatternStemCombinationEffectOccurrence effect
                : effects
        ) {

            Objects.requireNonNull(
                    effect,
                    "천간합 효과 목록에 null이 포함될 수 없습니다."
            );
        }
    }

    public boolean hasAnyCombinationEffect() {

        return !effects.isEmpty();
    }

    public boolean hasSelfCombination() {

        return effects
                .stream()
                .anyMatch(
                        PatternStemCombinationEffectOccurrence
                                ::selfCombination
                );
    }

    public boolean hasRemovalCandidate() {

        return effects
                .stream()
                .anyMatch(
                        PatternStemCombinationEffectOccurrence
                                ::removalCandidate
                );
    }

    public boolean hasReviewRequiredCombination() {

        return effects
                .stream()
                .anyMatch(
                        PatternStemCombinationEffectOccurrence
                                ::reviewRequired
                );
    }

    public List<PatternStemCombinationEffectOccurrence>
    effectsInvolving(
            MonthCommandPatternCandidateType type
    ) {

        Objects.requireNonNull(
                type,
                "십성 타입은 필수입니다."
        );

        return effects
                .stream()
                .filter(
                        effect ->
                                effect.involvesTenGod(type)
                )
                .toList();
    }

    public List<PatternStemCombinationEffectOccurrence>
    removalCandidatesInvolving(
            MonthCommandPatternCandidateType type
    ) {

        return effectsInvolving(type)
                .stream()
                .filter(
                        PatternStemCombinationEffectOccurrence
                                ::removalCandidate
                )
                .toList();
    }

    public boolean hasRemovalCandidateFor(
            MonthCommandPatternCandidateType type
    ) {

        return !removalCandidatesInvolving(type)
                .isEmpty();
    }

    public boolean hasRemovalCandidateBetween(

            MonthCommandPatternCandidateType first,

            MonthCommandPatternCandidateType second

    ) {

        return effects
                .stream()
                .filter(
                        PatternStemCombinationEffectOccurrence
                                ::removalCandidate
                )
                .anyMatch(
                        effect ->
                                effect.betweenTenGods(
                                        first,
                                        second
                                )
                );
    }

    /*
     * =================================================
     * 자주 쓰는 십성별 helper
     * =================================================
     */

    public boolean directOfficerRemovalCandidate() {

        return hasRemovalCandidateFor(
                MonthCommandPatternCandidateType
                        .DIRECT_OFFICER
        );
    }

    public boolean sevenKillingsRemovalCandidate() {

        return hasRemovalCandidateFor(
                MonthCommandPatternCandidateType
                        .SEVEN_KILLINGS
        );
    }

    public boolean wealthRemovalCandidate() {

        return hasRemovalCandidateFor(
                MonthCommandPatternCandidateType
                        .DIRECT_WEALTH
        )
                ||
                hasRemovalCandidateFor(
                        MonthCommandPatternCandidateType
                                .INDIRECT_WEALTH
                );
    }

    public boolean resourceRemovalCandidate() {

        return hasRemovalCandidateFor(
                MonthCommandPatternCandidateType
                        .DIRECT_RESOURCE
        )
                ||
                hasRemovalCandidateFor(
                        MonthCommandPatternCandidateType
                                .INDIRECT_RESOURCE
                );
    }

    /**
     * 食神과 七殺 사이의
     * 직접적인 비일간 合去 후보.
     *
     * 아직 合殺存財 확정은 아니다.
     */
    public boolean eatingGodSevenKillingsRemovalCandidate() {

        return hasRemovalCandidateBetween(
                MonthCommandPatternCandidateType
                        .EATING_GOD,
                MonthCommandPatternCandidateType
                        .SEVEN_KILLINGS
        );
    }

    public boolean sevenKillingsNeedsCombinationReview() {

        return effectsInvolving(
                MonthCommandPatternCandidateType
                        .SEVEN_KILLINGS
        )
                .stream()
                .anyMatch(
                        PatternStemCombinationEffectOccurrence
                                ::reviewRequired
                );
    }

    public boolean directOfficerNeedsCombinationReview() {

        return effectsInvolving(
                MonthCommandPatternCandidateType
                        .DIRECT_OFFICER
        )
                .stream()
                .anyMatch(
                        PatternStemCombinationEffectOccurrence
                                ::reviewRequired
                );
    }

    public boolean wealthNeedsCombinationReview() {

        return effects
                .stream()
                .filter(
                        effect ->
                                effect.involvesTenGod(
                                        MonthCommandPatternCandidateType
                                                .DIRECT_WEALTH
                                )
                                        ||
                                        effect.involvesTenGod(
                                                MonthCommandPatternCandidateType
                                                        .INDIRECT_WEALTH
                                        )
                )
                .anyMatch(
                        PatternStemCombinationEffectOccurrence
                                ::reviewRequired
                );
    }
}