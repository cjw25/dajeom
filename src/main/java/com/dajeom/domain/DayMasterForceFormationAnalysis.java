package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 일간을 중심으로 원국에서 형성되는 세력 구조.
 *
 * 아직:
 *
 * - 득세
 * - 신강
 * - 신약
 *
 * 을 판정하지 않는다.
 *
 * 숫자 계산도 하지 않는다.
 *
 * 단순히:
 *
 * 1. 일간을 생조하는 구조
 * 2. 일간과 같은 오행의 구조
 * 3. 일간이 생하면서 기운이 나가는 구조
 * 4. 일간이 극하면서 힘을 쓰는 구조
 * 5. 일간을 극하는 구조
 *
 * 가 원국 어디에 존재하는지만 정리한다.
 */
public record DayMasterForceFormationAnalysis(

        HeavenlyStem dayMaster,

        List<DayMasterVisibleStemRelation> visibleGeneratingDayMaster,

        List<DayMasterVisibleStemRelation> visibleSameElement,

        List<DayMasterVisibleStemRelation> visibleGeneratedByDayMaster,

        List<DayMasterVisibleStemRelation> visibleControlledByDayMaster,

        List<DayMasterVisibleStemRelation> visibleControllingDayMaster,

        List<DayMasterHiddenStemRelation> hiddenGeneratingDayMaster,

        List<DayMasterHiddenStemRelation> hiddenSameElement,

        List<DayMasterHiddenStemRelation> hiddenGeneratedByDayMaster,

        List<DayMasterHiddenStemRelation> hiddenControlledByDayMaster,

        List<DayMasterHiddenStemRelation> hiddenControllingDayMaster

) {

    public DayMasterForceFormationAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        visibleGeneratingDayMaster =
                immutable(
                        visibleGeneratingDayMaster
                );

        visibleSameElement =
                immutable(
                        visibleSameElement
                );

        visibleGeneratedByDayMaster =
                immutable(
                        visibleGeneratedByDayMaster
                );

        visibleControlledByDayMaster =
                immutable(
                        visibleControlledByDayMaster
                );

        visibleControllingDayMaster =
                immutable(
                        visibleControllingDayMaster
                );

        hiddenGeneratingDayMaster =
                immutable(
                        hiddenGeneratingDayMaster
                );

        hiddenSameElement =
                immutable(
                        hiddenSameElement
                );

        hiddenGeneratedByDayMaster =
                immutable(
                        hiddenGeneratedByDayMaster
                );

        hiddenControlledByDayMaster =
                immutable(
                        hiddenControlledByDayMaster
                );

        hiddenControllingDayMaster =
                immutable(
                        hiddenControllingDayMaster
                );
    }

    /*
     * =================================================
     * 생조 구조
     * =================================================
     */

    public boolean hasVisibleGeneratingStructure() {

        return !visibleGeneratingDayMaster
                .isEmpty();
    }

    public boolean hasHiddenGeneratingStructure() {

        return !hiddenGeneratingDayMaster
                .isEmpty();
    }

    public boolean hasVisibleSameElementStructure() {

        return !visibleSameElement
                .isEmpty();
    }

    public boolean hasHiddenSameElementStructure() {

        return !hiddenSameElement
                .isEmpty();
    }

    public boolean hasAnyGeneratingStructure() {

        return hasVisibleGeneratingStructure()
                ||
                hasHiddenGeneratingStructure();
    }

    public boolean hasAnySameElementStructure() {

        return hasVisibleSameElementStructure()
                ||
                hasHiddenSameElementStructure();
    }

    /*
     * =================================================
     * 설기 구조
     *
     * 일간이 다른 오행을 생함.
     * =================================================
     */

    public boolean hasVisibleGeneratingOutflow() {

        return !visibleGeneratedByDayMaster
                .isEmpty();
    }

    public boolean hasHiddenGeneratingOutflow() {

        return !hiddenGeneratedByDayMaster
                .isEmpty();
    }

    /*
     * =================================================
     * 재성 방향
     *
     * 일간이 상대를 극함.
     * =================================================
     */

    public boolean hasVisibleControlledStructure() {

        return !visibleControlledByDayMaster
                .isEmpty();
    }

    public boolean hasHiddenControlledStructure() {

        return !hiddenControlledByDayMaster
                .isEmpty();
    }

    /*
     * =================================================
     * 관살 방향
     *
     * 상대가 일간을 극함.
     * =================================================
     */

    public boolean hasVisibleControllingStructure() {

        return !visibleControllingDayMaster
                .isEmpty();
    }

    public boolean hasHiddenControllingStructure() {

        return !hiddenControllingDayMaster
                .isEmpty();
    }

    /*
     * =================================================
     * 위치별 생조 조회
     * =================================================
     */

    public List<DayMasterHiddenStemRelation>
    hiddenGeneratingAt(
            PillarPosition position
    ) {

        Objects.requireNonNull(
                position,
                "기둥 위치는 필수입니다."
        );

        return hiddenGeneratingDayMaster
                .stream()
                .filter(
                        relation ->
                                relation.branchPosition()
                                        == position
                )
                .toList();
    }

    public List<DayMasterHiddenStemRelation>
    hiddenSameElementAt(
            PillarPosition position
    ) {

        Objects.requireNonNull(
                position,
                "기둥 위치는 필수입니다."
        );

        return hiddenSameElement
                .stream()
                .filter(
                        relation ->
                                relation.branchPosition()
                                        == position
                )
                .toList();
    }

    /*
     * =================================================
     * 월지 내부 구조 조회
     * =================================================
     */

    public List<DayMasterHiddenStemRelation>
    monthGeneratingDayMaster() {

        return hiddenGeneratingAt(
                PillarPosition.MONTH
        );
    }

    public List<DayMasterHiddenStemRelation>
    monthSameElement() {

        return hiddenSameElementAt(
                PillarPosition.MONTH
        );
    }

    private static <T> List<T> immutable(
            List<T> source
    ) {

        if (source == null) {

            return List.of();
        }

        return List.copyOf(
                source
        );
    }
}