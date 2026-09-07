package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 원국의 天干五合 전체 분석.
 *
 * =================================================
 *
 * 여기서는:
 *
 * - 어떤 천간들이 합하는지
 * - 어느 위치끼리 합하는지
 * - 어떤 십성이 합에 관여하는지
 *
 * 만 확정한다.
 *
 * =================================================
 *
 * 아직 판단하지 않는 것:
 *
 * - 合化
 * - 合而不化
 * - 合去
 * - 爭合
 * - 妬合
 * - 遙合의 실제 강약
 * - 격국 보존/파괴
 *
 * 위 사항은 이후 별도 Rule에서 판단한다.
 */
public record PatternStemCombinationAnalysis(

        HeavenlyStem dayMaster,

        List<PatternStemCombinationOccurrence> combinations

) {

    public PatternStemCombinationAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        combinations =
                combinations == null
                        ? List.of()
                        : List.copyOf(combinations);

        for (
                PatternStemCombinationOccurrence occurrence
                : combinations
        ) {

            Objects.requireNonNull(
                    occurrence,
                    "천간합 목록에 null이 포함될 수 없습니다."
            );
        }
    }

    public boolean hasAnyCombination() {

        return !combinations.isEmpty();
    }

    public boolean dayMasterCombined() {

        return combinations
                .stream()
                .anyMatch(
                        PatternStemCombinationOccurrence
                                ::involvesDayMaster
                );
    }

    public List<PatternStemCombinationOccurrence>
    combinationsInvolvingDayMaster() {

        return combinations
                .stream()
                .filter(
                        PatternStemCombinationOccurrence
                                ::involvesDayMaster
                )
                .toList();
    }

    public boolean hasCombinationInvolving(
            MonthCommandPatternCandidateType type
    ) {

        return combinations
                .stream()
                .anyMatch(
                        occurrence ->
                                occurrence.involvesTenGod(type)
                );
    }

    public List<PatternStemCombinationOccurrence>
    combinationsInvolving(
            MonthCommandPatternCandidateType type
    ) {

        Objects.requireNonNull(
                type,
                "십성 타입은 필수입니다."
        );

        return combinations
                .stream()
                .filter(
                        occurrence ->
                                occurrence.involvesTenGod(type)
                )
                .toList();
    }

    public boolean hasCombinationBetween(

            MonthCommandPatternCandidateType first,

            MonthCommandPatternCandidateType second

    ) {

        return combinations
                .stream()
                .anyMatch(
                        occurrence ->
                                occurrence.betweenTenGods(
                                        first,
                                        second
                                )
                );
    }

    /*
     * =================================================
     * 격국 RelationContext에서 자주 사용하는
     * 공통 조회
     * =================================================
     */

    public boolean directOfficerCombined() {

        return hasCombinationInvolving(
                MonthCommandPatternCandidateType
                        .DIRECT_OFFICER
        );
    }

    public boolean sevenKillingsCombined() {

        return hasCombinationInvolving(
                MonthCommandPatternCandidateType
                        .SEVEN_KILLINGS
        );
    }

    public boolean directWealthCombined() {

        return hasCombinationInvolving(
                MonthCommandPatternCandidateType
                        .DIRECT_WEALTH
        );
    }

    public boolean indirectWealthCombined() {

        return hasCombinationInvolving(
                MonthCommandPatternCandidateType
                        .INDIRECT_WEALTH
        );
    }

    public boolean wealthCombined() {

        return directWealthCombined()
                ||
                indirectWealthCombined();
    }

    public boolean directResourceCombined() {

        return hasCombinationInvolving(
                MonthCommandPatternCandidateType
                        .DIRECT_RESOURCE
        );
    }

    public boolean indirectResourceCombined() {

        return hasCombinationInvolving(
                MonthCommandPatternCandidateType
                        .INDIRECT_RESOURCE
        );
    }

    public boolean resourceCombined() {

        return directResourceCombined()
                ||
                indirectResourceCombined();
    }

    public boolean eatingGodCombined() {

        return hasCombinationInvolving(
                MonthCommandPatternCandidateType
                        .EATING_GOD
        );
    }

    public boolean hurtingOfficerCombined() {

        return hasCombinationInvolving(
                MonthCommandPatternCandidateType
                        .HURTING_OFFICER
        );
    }
}