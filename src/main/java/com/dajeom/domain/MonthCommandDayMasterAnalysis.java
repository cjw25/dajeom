package com.dajeom.domain;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 월령 전체와 일간의 생극관계 분석.
 *
 * 아직 해석 결론을 내리지 않는다.
 *
 * 보존:
 *
 * - 월지
 * - 월령 EARLY / MIDDLE / MAIN
 * - 각 지장간과 일간의 생극관계
 * - 각 지장간의 천간 투출 여부/위치
 */
public record MonthCommandDayMasterAnalysis(

        HeavenlyStem dayMaster,

        EarthlyBranch monthBranch,

        List<MonthCommandStemRelation> relations

) {

    public MonthCommandDayMasterAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지는 필수입니다."
        );

        Objects.requireNonNull(
                relations,
                "월령 관계 목록은 필수입니다."
        );

        relations =
                List.copyOf(
                        relations
                );

        if (relations.isEmpty()) {

            throw new IllegalArgumentException(
                    "월령에는 최소 하나의 지장간 관계가 필요합니다."
            );
        }

        for (
                MonthCommandStemRelation relation
                : relations
        ) {

            Objects.requireNonNull(
                    relation,
                    "월령 관계 목록에 null이 포함될 수 없습니다."
            );

            if (
                    relation.dayMaster()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "월령 관계의 일간이 일치하지 않습니다."
                );
            }
        }
    }

    public Optional<MonthCommandStemRelation> earlyRelation() {

        return relationOf(
                HiddenStemPhase.EARLY
        );
    }

    public Optional<MonthCommandStemRelation> middleRelation() {

        return relationOf(
                HiddenStemPhase.MIDDLE
        );
    }

    public Optional<MonthCommandStemRelation> mainRelation() {

        return relationOf(
                HiddenStemPhase.MAIN
        );
    }

    /**
     * 월령 MAIN 지장간과 일간의 관계.
     */
    public Optional<DayMasterRelationType> mainRelationType() {

        return mainRelation()
                .map(
                        MonthCommandStemRelation::relationType
                );
    }

    /**
     * 월령 MAIN 지장간이
     * 천간에 투출했는지.
     */
    public boolean mainExposed() {

        return mainRelation()
                .map(
                        MonthCommandStemRelation::exposed
                )
                .orElse(
                        false
                );
    }

    /**
     * 월령에서 일간을 생하는 지장간.
     */
    public List<MonthCommandStemRelation>
    generatingDayMasterRelations() {

        return relationsOf(
                DayMasterRelationType.GENERATES_DAY_MASTER
        );
    }

    /**
     * 월령에서 일간과 같은 오행.
     */
    public List<MonthCommandStemRelation>
    sameElementRelations() {

        return relationsOf(
                DayMasterRelationType.SAME_ELEMENT
        );
    }

    /**
     * 월령에서 일간이 생하는 대상.
     */
    public List<MonthCommandStemRelation>
    generatedByDayMasterRelations() {

        return relationsOf(
                DayMasterRelationType.GENERATED_BY_DAY_MASTER
        );
    }

    /**
     * 월령에서 일간을 극하는 지장간.
     */
    public List<MonthCommandStemRelation>
    controllingDayMasterRelations() {

        return relationsOf(
                DayMasterRelationType.CONTROLS_DAY_MASTER
        );
    }

    /**
     * 월령에서 일간이 극하는 대상.
     */
    public List<MonthCommandStemRelation>
    controlledByDayMasterRelations() {

        return relationsOf(
                DayMasterRelationType.CONTROLLED_BY_DAY_MASTER
        );
    }

    private Optional<MonthCommandStemRelation> relationOf(
            HiddenStemPhase phase
    ) {

        return relations
                .stream()
                .filter(
                        relation ->
                                relation.phase()
                                        == phase
                )
                .findFirst();
    }

    private List<MonthCommandStemRelation> relationsOf(
            DayMasterRelationType type
    ) {

        Objects.requireNonNull(
                type,
                "관계 종류는 필수입니다."
        );

        return relations
                .stream()
                .filter(
                        relation ->
                                relation.relationType()
                                        == type
                )
                .toList();
    }
}