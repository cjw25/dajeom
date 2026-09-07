package com.dajeom.domain;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 격국 후보와 원국 전체 십성 배치를 연결한 공통 분석.
 *
 * =================================================
 *
 * 이 객체를 이후:
 *
 * - 正官格 Rule
 * - 七殺格 Rule
 * - 正財/偏財格 Rule
 * - 正印/偏印格 Rule
 * - 食神格 Rule
 * - 傷官格 Rule
 *
 * 이 공통으로 사용한다.
 *
 * =================================================
 *
 * 포함:
 *
 * - 현재 월령 격국 선택 경로
 * - 천간에 드러난 십성
 * - 지장간에 존재하는 십성
 * - 천간 관계
 * - 지지 관계
 *
 * =================================================
 *
 * 숫자 / 점수 없음.
 */
public record PatternInteractionAnalysis(

        HeavenlyStem dayMaster,

        MonthCommandPatternSelectionAnalysis patternSelection,

        List<PatternInteractionVisibleStem>
        visibleInteractions,

        List<PatternInteractionHiddenStem>
        hiddenInteractions

) {

    public PatternInteractionAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                patternSelection,
                "월령 격국 선택 결과는 필수입니다."
        );

        visibleInteractions =
                visibleInteractions == null
                        ? List.of()
                        : List.copyOf(
                        visibleInteractions
                );

        hiddenInteractions =
                hiddenInteractions == null
                        ? List.of()
                        : List.copyOf(
                        hiddenInteractions
                );

        if (
                patternSelection.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "격국 선택 결과의 일간이 분석 대상과 일치하지 않습니다."
            );
        }

        for (
                PatternInteractionVisibleStem interaction
                : visibleInteractions
        ) {

            Objects.requireNonNull(
                    interaction,
                    "천간 Interaction 목록에 null이 포함될 수 없습니다."
            );

            if (
                    interaction.dayMaster()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "천간 Interaction의 일간이 일치하지 않습니다."
                );
            }
        }

        for (
                PatternInteractionHiddenStem interaction
                : hiddenInteractions
        ) {

            Objects.requireNonNull(
                    interaction,
                    "지장간 Interaction 목록에 null이 포함될 수 없습니다."
            );

            if (
                    interaction.dayMaster()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "지장간 Interaction의 일간이 일치하지 않습니다."
                );
            }
        }
    }

    /*
     * =================================================
     * 현재 월령 격국
     * =================================================
     */

    public MonthCommandPatternCandidate primaryCandidate() {

        return patternSelection
                .primaryCandidate();
    }

    public Optional<MonthCommandPatternCandidate>
    provisionalStandardCandidate() {

        return patternSelection
                .provisionalStandardCandidate();
    }

    public MonthCommandPatternSelectionPath selectionPath() {

        return patternSelection
                .path();
    }

    /*
     * =================================================
     * 천간 십성 조회
     * =================================================
     */

    public List<PatternInteractionVisibleStem> visibleOf(
            MonthCommandPatternCandidateType type
    ) {

        Objects.requireNonNull(
                type,
                "십성 종류는 필수입니다."
        );

        return visibleInteractions
                .stream()
                .filter(
                        interaction ->
                                interaction.tenGodType()
                                        == type
                )
                .toList();
    }

    /*
     * =================================================
     * 지장간 십성 조회
     * =================================================
     */

    public List<PatternInteractionHiddenStem> hiddenOf(
            MonthCommandPatternCandidateType type
    ) {

        Objects.requireNonNull(
                type,
                "십성 종류는 필수입니다."
        );

        return hiddenInteractions
                .stream()
                .filter(
                        interaction ->
                                interaction.tenGodType()
                                        == type
                )
                .toList();
    }

    /*
     * =================================================
     * 존재 여부
     * =================================================
     */

    public boolean hasVisible(
            MonthCommandPatternCandidateType type
    ) {

        return !visibleOf(
                type
        ).isEmpty();
    }

    public boolean hasHidden(
            MonthCommandPatternCandidateType type
    ) {

        return !hiddenOf(
                type
        ).isEmpty();
    }

    public boolean hasAny(
            MonthCommandPatternCandidateType type
    ) {

        return hasVisible(
                type
        )
                ||
                hasHidden(
                        type
                );
    }

    /*
     * =================================================
     * 주요 격국 상호작용 편의 조회
     * =================================================
     */

    public boolean hasDirectOfficer() {

        return hasAny(
                MonthCommandPatternCandidateType
                        .DIRECT_OFFICER
        );
    }

    public boolean hasSevenKillings() {

        return hasAny(
                MonthCommandPatternCandidateType
                        .SEVEN_KILLINGS
        );
    }

    public boolean hasDirectWealth() {

        return hasAny(
                MonthCommandPatternCandidateType
                        .DIRECT_WEALTH
        );
    }

    public boolean hasIndirectWealth() {

        return hasAny(
                MonthCommandPatternCandidateType
                        .INDIRECT_WEALTH
        );
    }

    public boolean hasEatingGod() {

        return hasAny(
                MonthCommandPatternCandidateType
                        .EATING_GOD
        );
    }

    public boolean hasHurtingOfficer() {

        return hasAny(
                MonthCommandPatternCandidateType
                        .HURTING_OFFICER
        );
    }

    public boolean hasDirectResource() {

        return hasAny(
                MonthCommandPatternCandidateType
                        .DIRECT_RESOURCE
        );
    }

    public boolean hasIndirectResource() {

        return hasAny(
                MonthCommandPatternCandidateType
                        .INDIRECT_RESOURCE
        );
    }

    public boolean hasCompanion() {

        return hasAny(
                MonthCommandPatternCandidateType
                        .MONTH_COMPANION
        );
    }

    public boolean hasRobWealth() {

        return hasAny(
                MonthCommandPatternCandidateType
                        .MONTH_ROB_WEALTH
        );
    }

    /*
     * =================================================
     * 천간에 실제 투출되었는지
     *
     * 成格/破格에서는
     * "존재"와 "透干"을 구분해서 읽어야 한다.
     * =================================================
     */

    public boolean directOfficerExposed() {

        return hasVisible(
                MonthCommandPatternCandidateType
                        .DIRECT_OFFICER
        );
    }

    public boolean sevenKillingsExposed() {

        return hasVisible(
                MonthCommandPatternCandidateType
                        .SEVEN_KILLINGS
        );
    }

    public boolean wealthExposed() {

        return hasVisible(
                MonthCommandPatternCandidateType
                        .DIRECT_WEALTH
        )
                ||
                hasVisible(
                        MonthCommandPatternCandidateType
                                .INDIRECT_WEALTH
                );
    }

    public boolean resourceExposed() {

        return hasVisible(
                MonthCommandPatternCandidateType
                        .DIRECT_RESOURCE
        )
                ||
                hasVisible(
                        MonthCommandPatternCandidateType
                                .INDIRECT_RESOURCE
                );
    }

    public boolean outputExposed() {

        return hasVisible(
                MonthCommandPatternCandidateType
                        .EATING_GOD
        )
                ||
                hasVisible(
                        MonthCommandPatternCandidateType
                                .HURTING_OFFICER
                );
    }

    public boolean companionOrRobWealthExposed() {

        return hasVisible(
                MonthCommandPatternCandidateType
                        .MONTH_COMPANION
        )
                ||
                hasVisible(
                        MonthCommandPatternCandidateType
                                .MONTH_ROB_WEALTH
                );
    }
}