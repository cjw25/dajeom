package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 격국 판정에서 공통으로 사용하는
 * 天干五合 구조 Context.
 *
 * =================================================
 *
 * 이 객체에서 알 수 있는 것:
 *
 * - 어떤 십성이 합에 걸렸는가
 * - 그 합 상대가 日主인가
 * - 동일 천간이 복수 합 후보에 걸렸는가
 *
 * =================================================
 *
 * 아직 알 수 없는 것:
 *
 * - 合化 성립
 * - 合去
 * - 合留
 * - 合而不化
 * - 실제 格의 보존/파괴
 *
 * =================================================
 *
 * 숫자 / 점수 없음.
 */
public record PatternStemCombinationContext(

        HeavenlyStem dayMaster,

        List<PatternStemCombinationOccurrence> combinations,

        List<PatternStemCombinationParticipation> participations

) {

    public PatternStemCombinationContext {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        combinations =
                combinations == null
                        ? List.of()
                        : List.copyOf(combinations);

        participations =
                participations == null
                        ? List.of()
                        : List.copyOf(participations);

        for (
                PatternStemCombinationOccurrence combination
                : combinations
        ) {

            Objects.requireNonNull(
                    combination,
                    "천간합 목록에 null이 포함될 수 없습니다."
            );
        }

        for (
                PatternStemCombinationParticipation participation
                : participations
        ) {

            Objects.requireNonNull(
                    participation,
                    "천간합 참여 목록에 null이 포함될 수 없습니다."
            );

            for (
                    PatternStemCombinationOccurrence occurrence
                    : participation.combinations()
            ) {

                if (!combinations.contains(occurrence)) {

                    throw new IllegalArgumentException(
                            "Participation이 Context에 없는 천간합을 참조합니다."
                    );
                }
            }
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

    /*
     * =================================================
     * 십성별 합 여부
     * =================================================
     */

    public boolean directOfficerCombined() {

        return combined(
                MonthCommandPatternCandidateType
                        .DIRECT_OFFICER
        );
    }

    public boolean sevenKillingsCombined() {

        return combined(
                MonthCommandPatternCandidateType
                        .SEVEN_KILLINGS
        );
    }

    public boolean directWealthCombined() {

        return combined(
                MonthCommandPatternCandidateType
                        .DIRECT_WEALTH
        );
    }

    public boolean indirectWealthCombined() {

        return combined(
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

        return combined(
                MonthCommandPatternCandidateType
                        .DIRECT_RESOURCE
        );
    }

    public boolean indirectResourceCombined() {

        return combined(
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

        return combined(
                MonthCommandPatternCandidateType
                        .EATING_GOD
        );
    }

    public boolean hurtingOfficerCombined() {

        return combined(
                MonthCommandPatternCandidateType
                        .HURTING_OFFICER
        );
    }

    /*
     * =================================================
     * 日主와 직접 합하는가
     * =================================================
     */

    public boolean directOfficerCombinedWithDayMaster() {

        return combinedWithDayMaster(
                MonthCommandPatternCandidateType
                        .DIRECT_OFFICER
        );
    }

    public boolean sevenKillingsCombinedWithDayMaster() {

        return combinedWithDayMaster(
                MonthCommandPatternCandidateType
                        .SEVEN_KILLINGS
        );
    }

    public boolean wealthCombinedWithDayMaster() {

        return combinedWithDayMaster(
                MonthCommandPatternCandidateType
                        .DIRECT_WEALTH
        )
                ||
                combinedWithDayMaster(
                        MonthCommandPatternCandidateType
                                .INDIRECT_WEALTH
                );
    }

    public boolean resourceCombinedWithDayMaster() {

        return combinedWithDayMaster(
                MonthCommandPatternCandidateType
                        .DIRECT_RESOURCE
        )
                ||
                combinedWithDayMaster(
                        MonthCommandPatternCandidateType
                                .INDIRECT_RESOURCE
                );
    }

    public boolean eatingGodCombinedWithDayMaster() {

        return combinedWithDayMaster(
                MonthCommandPatternCandidateType
                        .EATING_GOD
        );
    }

    public boolean hurtingOfficerCombinedWithDayMaster() {

        return combinedWithDayMaster(
                MonthCommandPatternCandidateType
                        .HURTING_OFFICER
        );
    }

    /*
     * =================================================
     * 爭合/妬合 검토 후보
     * =================================================
     */

    public boolean hasCompetingCombinationCandidate() {

        return participations
                .stream()
                .anyMatch(
                        PatternStemCombinationParticipation
                                ::competingCombinationCandidate
                );
    }

    public boolean hasCompetingCombinationCandidate(
            MonthCommandPatternCandidateType type
    ) {

        Objects.requireNonNull(
                type,
                "십성 타입은 필수입니다."
        );

        return participations
                .stream()
                .anyMatch(
                        participation ->
                                participation
                                        .hasTenGodType(type)

                                        &&

                                        participation
                                                .competingCombinationCandidate()
                );
    }

    public List<PatternStemCombinationParticipation>
    competingParticipations() {

        return participations
                .stream()
                .filter(
                        PatternStemCombinationParticipation
                                ::competingCombinationCandidate
                )
                .toList();
    }

    /*
     * =================================================
     * occurrence 조회
     * =================================================
     */

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

    private boolean combined(
            MonthCommandPatternCandidateType type
    ) {

        return combinations
                .stream()
                .anyMatch(
                        occurrence ->
                                occurrence.involvesTenGod(type)
                );
    }

    private boolean combinedWithDayMaster(
            MonthCommandPatternCandidateType type
    ) {

        return combinations
                .stream()
                .anyMatch(
                        occurrence ->
                                occurrence.involvesDayMaster()

                                        &&

                                        occurrence
                                                .involvesTenGod(type)
                );
    }
}