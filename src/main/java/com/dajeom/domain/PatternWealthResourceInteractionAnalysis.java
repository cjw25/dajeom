package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 원국 전체 財印 위치 관계 분석.
 *
 * =================================================
 *
 * visibleRelations
 *
 * 투출 財 ↔ 투출 印 관계.
 *
 * → 財印不相礙 판단에 사용.
 *
 * =================================================
 *
 * selectedMonthResourceRelations
 *
 * 印格일 때:
 *
 * 투출 財 ↔ 月令 MAIN 印
 *
 * → 財破印 실제 작동 여부에 사용.
 *
 * =================================================
 */
public record PatternWealthResourceInteractionAnalysis(

        HeavenlyStem dayMaster,

        List<PatternWealthResourceRelationOccurrence> visibleRelations,

        List<PatternWealthResourceRelationOccurrence>
        selectedMonthResourceRelations

) {

    public PatternWealthResourceInteractionAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        visibleRelations =
                visibleRelations == null
                        ? List.of()
                        : List.copyOf(visibleRelations);

        selectedMonthResourceRelations =
                selectedMonthResourceRelations == null
                        ? List.of()
                        : List.copyOf(
                        selectedMonthResourceRelations
                );

        for (
                PatternWealthResourceRelationOccurrence relation
                : visibleRelations
        ) {

            Objects.requireNonNull(
                    relation,
                    "visible 財印 관계에 null이 포함될 수 없습니다."
            );

            if (!relation.resource().visible()) {

                throw new IllegalArgumentException(
                        "visibleRelations의 印은 투출 천간이어야 합니다."
                );
            }
        }

        for (
                PatternWealthResourceRelationOccurrence relation
                : selectedMonthResourceRelations
        ) {

            Objects.requireNonNull(
                    relation,
                    "월령 印 관계에 null이 포함될 수 없습니다."
            );

            if (!relation.resourceIsMonthCommandHiddenStem()) {

                throw new IllegalArgumentException(
                        "selectedMonthResourceRelations는 月令 지장간 印을 대상으로 해야 합니다."
                );
            }
        }
    }

    /**
     * 투출 財와 투출 印이 모두 존재하고,
     * 모든 財印 pair가 직접 충돌 없이
     * 통관 또는 위치 분리 상태인가.
     *
     * 하나라도 DIRECT_CONFLICT이면 false.
     */
    public boolean visibleWealthAndResourceMutuallyNonObstructing() {

        if (visibleRelations.isEmpty()) {

            return false;
        }

        return visibleRelations
                .stream()
                .allMatch(
                        PatternWealthResourceRelationOccurrence
                                ::mutuallyNonObstructing
                );
    }

    public boolean visibleDirectConflictActive() {

        return visibleRelations
                .stream()
                .anyMatch(
                        PatternWealthResourceRelationOccurrence
                                ::directConflict
                );
    }

    /**
     * 印格의 月令 MAIN 印을
     * 투출 財가 직접 치고 있는가.
     */
    public boolean selectedMonthResourceConflictActive() {

        return selectedMonthResourceRelations
                .stream()
                .anyMatch(
                        PatternWealthResourceRelationOccurrence
                                ::directConflict
                );
    }

    /**
     * 月令 印이 財의 직접 공격을 받지만
     * 官殺 통관으로 연결되는 구조가 있는가.
     */
    public boolean selectedMonthResourceHasAuthorityBridge() {

        return selectedMonthResourceRelations
                .stream()
                .anyMatch(
                        PatternWealthResourceRelationOccurrence
                                ::authorityBridged
                );
    }

    /**
     * 印格에서 財破印 검토용.
     *
     * 月令 MAIN 印 직접 충돌
     * 또는 투출 印 직접 충돌이 확인되면 true.
     */
    public boolean resourcePatternConflictActive() {

        return selectedMonthResourceConflictActive()
                ||
                visibleDirectConflictActive();
    }

    public boolean hasAnyAuthorityBridge() {

        return visibleRelations
                .stream()
                .anyMatch(
                        PatternWealthResourceRelationOccurrence
                                ::authorityBridged
                )
                ||
                selectedMonthResourceRelations
                        .stream()
                        .anyMatch(
                                PatternWealthResourceRelationOccurrence
                                        ::authorityBridged
                        );
    }
}