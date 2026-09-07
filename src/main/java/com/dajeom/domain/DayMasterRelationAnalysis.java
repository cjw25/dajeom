package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 일간 기준 원국 생극관계 전체.
 *
 * 판단 결과가 아니라 계산 사실의 모음이다.
 */
public record DayMasterRelationAnalysis(

        HeavenlyStem dayMaster,

        List<DayMasterVisibleStemRelation> visibleStemRelations,

        List<DayMasterHiddenStemRelation> hiddenStemRelations

) {

    public DayMasterRelationAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        visibleStemRelations =
                visibleStemRelations == null
                        ? List.of()
                        : List.copyOf(
                        visibleStemRelations
                );

        hiddenStemRelations =
                hiddenStemRelations == null
                        ? List.of()
                        : List.copyOf(
                        hiddenStemRelations
                );
    }

    public List<DayMasterVisibleStemRelation> visibleRelationsOf(
            DayMasterRelationType type
    ) {

        Objects.requireNonNull(
                type,
                "관계 종류는 필수입니다."
        );

        return visibleStemRelations
                .stream()
                .filter(
                        relation ->
                                relation.relationType()
                                        == type
                )
                .toList();
    }

    public List<DayMasterHiddenStemRelation> hiddenRelationsOf(
            DayMasterRelationType type
    ) {

        Objects.requireNonNull(
                type,
                "관계 종류는 필수입니다."
        );

        return hiddenStemRelations
                .stream()
                .filter(
                        relation ->
                                relation.relationType()
                                        == type
                )
                .toList();
    }

    public List<DayMasterHiddenStemRelation> hiddenRelationsAt(
            PillarPosition position
    ) {

        Objects.requireNonNull(
                position,
                "기둥 위치는 필수입니다."
        );

        return hiddenStemRelations
                .stream()
                .filter(
                        relation ->
                                relation.branchPosition()
                                        == position
                )
                .toList();
    }

    public List<DayMasterHiddenStemRelation> monthCommandRelations() {

        return hiddenRelationsAt(
                PillarPosition.MONTH
        );
    }
}