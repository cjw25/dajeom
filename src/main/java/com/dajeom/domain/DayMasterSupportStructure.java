package com.dajeom.domain;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 일간을 중심으로 원국의 세력 관계를 정리한 구조.
 *
 * 아직 신강/신약을 판정하지 않는다.
 *
 * 보존하는 것:
 *
 * - 일간의 통근 상태
 * - 일간을 생하는 천간/지장간
 * - 일간과 같은 오행
 * - 일간이 생하는 천간/지장간
 * - 일간을 극하는 천간/지장간
 * - 일간이 극하는 천간/지장간
 * - 월령 MAIN과 일간의 관계
 *
 * 숫자 점수, 가중치, 합산 없음.
 */
public record DayMasterSupportStructure(

        HeavenlyStem dayMaster,

        StemGroundAnalysis rooting,

        MonthCommandDayMasterAnalysis monthCommand,

        DayMasterRelationAnalysis relations

) {

    public DayMasterSupportStructure {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                rooting,
                "일간 통근 분석은 필수입니다."
        );

        Objects.requireNonNull(
                monthCommand,
                "월령-일간 관계 분석은 필수입니다."
        );

        Objects.requireNonNull(
                relations,
                "일간 생극관계 분석은 필수입니다."
        );

        if (
                rooting.targetStem()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "통근 분석 대상 천간이 일간과 일치하지 않습니다."
            );
        }

        if (
                monthCommand.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "월령 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                relations.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "생극관계 분석의 일간이 일치하지 않습니다."
            );
        }
    }

    /*
     * =================================================
     * 통근
     * =================================================
     */

    /**
     * 동일 천간 글자가 지장간에 존재하는 연결.
     */
    public List<StemGroundHit> exactStemRoots() {

        return rooting
                .exactStemHits();
    }

    /**
     * 동일 오행이지만 다른 천간 글자의 연결.
     *
     * 동일 천간 근과 구분해서 보존한다.
     */
    public List<StemGroundHit> sameElementOtherStemRoots() {

        return rooting
                .sameElementOtherStemHits();
    }

    public boolean hasExactStemRoot() {

        return rooting
                .hasExactStemConnection();
    }

    public boolean hasSameElementOtherStemRoot() {

        return rooting
                .hasSameElementOtherStemConnection();
    }

    /*
     * =================================================
     * 천간 - 일간을 생함
     * =================================================
     */

    public List<DayMasterVisibleStemRelation>
    visibleGeneratingDayMaster() {

        return relations
                .visibleRelationsOf(
                        DayMasterRelationType.GENERATES_DAY_MASTER
                );
    }

    /*
     * =================================================
     * 천간 - 같은 오행
     * =================================================
     */

    public List<DayMasterVisibleStemRelation>
    visibleSameElement() {

        return relations
                .visibleRelationsOf(
                        DayMasterRelationType.SAME_ELEMENT
                );
    }

    /*
     * =================================================
     * 천간 - 일간이 생함
     * =================================================
     */

    public List<DayMasterVisibleStemRelation>
    visibleGeneratedByDayMaster() {

        return relations
                .visibleRelationsOf(
                        DayMasterRelationType.GENERATED_BY_DAY_MASTER
                );
    }

    /*
     * =================================================
     * 천간 - 일간을 극함
     * =================================================
     */

    public List<DayMasterVisibleStemRelation>
    visibleControllingDayMaster() {

        return relations
                .visibleRelationsOf(
                        DayMasterRelationType.CONTROLS_DAY_MASTER
                );
    }

    /*
     * =================================================
     * 천간 - 일간이 극함
     * =================================================
     */

    public List<DayMasterVisibleStemRelation>
    visibleControlledByDayMaster() {

        return relations
                .visibleRelationsOf(
                        DayMasterRelationType.CONTROLLED_BY_DAY_MASTER
                );
    }

    /*
     * =================================================
     * 지장간 - 일간을 생함
     * =================================================
     */

    public List<DayMasterHiddenStemRelation>
    hiddenGeneratingDayMaster() {

        return relations
                .hiddenRelationsOf(
                        DayMasterRelationType.GENERATES_DAY_MASTER
                );
    }

    /*
     * =================================================
     * 지장간 - 같은 오행
     * =================================================
     */

    public List<DayMasterHiddenStemRelation>
    hiddenSameElement() {

        return relations
                .hiddenRelationsOf(
                        DayMasterRelationType.SAME_ELEMENT
                );
    }

    /*
     * =================================================
     * 지장간 - 일간이 생함
     * =================================================
     */

    public List<DayMasterHiddenStemRelation>
    hiddenGeneratedByDayMaster() {

        return relations
                .hiddenRelationsOf(
                        DayMasterRelationType.GENERATED_BY_DAY_MASTER
                );
    }

    /*
     * =================================================
     * 지장간 - 일간을 극함
     * =================================================
     */

    public List<DayMasterHiddenStemRelation>
    hiddenControllingDayMaster() {

        return relations
                .hiddenRelationsOf(
                        DayMasterRelationType.CONTROLS_DAY_MASTER
                );
    }

    /*
     * =================================================
     * 지장간 - 일간이 극함
     * =================================================
     */

    public List<DayMasterHiddenStemRelation>
    hiddenControlledByDayMaster() {

        return relations
                .hiddenRelationsOf(
                        DayMasterRelationType.CONTROLLED_BY_DAY_MASTER
                );
    }

    /*
     * =================================================
     * 월령
     * =================================================
     */

    public Optional<MonthCommandStemRelation>
    monthMainRelation() {

        return monthCommand
                .mainRelation();
    }

    public Optional<DayMasterRelationType>
    monthMainRelationType() {

        return monthCommand
                .mainRelationType();
    }

    public boolean monthMainExposed() {

        return monthCommand
                .mainExposed();
    }

    public List<MonthCommandStemRelation>
    monthGeneratingDayMaster() {

        return monthCommand
                .generatingDayMasterRelations();
    }

    public List<MonthCommandStemRelation>
    monthSameElement() {

        return monthCommand
                .sameElementRelations();
    }

    public List<MonthCommandStemRelation>
    monthGeneratedByDayMaster() {

        return monthCommand
                .generatedByDayMasterRelations();
    }

    public List<MonthCommandStemRelation>
    monthControllingDayMaster() {

        return monthCommand
                .controllingDayMasterRelations();
    }

    public List<MonthCommandStemRelation>
    monthControlledByDayMaster() {

        return monthCommand
                .controlledByDayMasterRelations();
    }
}