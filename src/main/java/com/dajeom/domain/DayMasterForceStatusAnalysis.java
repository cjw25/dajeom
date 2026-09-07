package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 일간의 得勢 영역 구조 분석.
 *
 * =================================================
 *
 * 생조:
 *
 * - 일간을 생함
 * - 일간과 같은 오행
 *
 * =================================================
 *
 * 설기:
 *
 * - 일간이 다른 오행을 생함
 *
 * =================================================
 *
 * 재성 방향:
 *
 * - 일간이 다른 오행을 극함
 *
 * =================================================
 *
 * 관살 방향:
 *
 * - 다른 오행이 일간을 극함
 *
 * =================================================
 *
 * 천간에 드러난 구조와
 * 지장간에 깔린 구조를 분리해서 보존한다.
 *
 * 하지 않는 것:
 *
 * - 몇 개가 더 많은지 비교
 * - 점수 계산
 * - 得勢 true/false
 * - 신강/신약 결정
 */
public record DayMasterForceStatusAnalysis(

        HeavenlyStem dayMaster,

        DayMasterForceSupportPattern visibleSupportPattern,

        List<DayMasterVisibleStemRelation>
        visibleGeneratingSupport,

        List<DayMasterVisibleStemRelation>
        visibleSameElementSupport,

        List<DayMasterHiddenStemRelation>
        hiddenGeneratingSupport,

        List<DayMasterHiddenStemRelation>
        hiddenSameElementSupport,

        List<DayMasterVisibleStemRelation>
        visibleGeneratingOutflow,

        List<DayMasterHiddenStemRelation>
        hiddenGeneratingOutflow,

        List<DayMasterVisibleStemRelation>
        visibleWealthDirection,

        List<DayMasterHiddenStemRelation>
        hiddenWealthDirection,

        List<DayMasterVisibleStemRelation>
        visibleControlPressure,

        List<DayMasterHiddenStemRelation>
        hiddenControlPressure

) {

    public DayMasterForceStatusAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                visibleSupportPattern,
                "천간 생조 형태는 필수입니다."
        );

        visibleGeneratingSupport =
                immutableVisible(
                        visibleGeneratingSupport
                );

        visibleSameElementSupport =
                immutableVisible(
                        visibleSameElementSupport
                );

        hiddenGeneratingSupport =
                immutableHidden(
                        hiddenGeneratingSupport
                );

        hiddenSameElementSupport =
                immutableHidden(
                        hiddenSameElementSupport
                );

        visibleGeneratingOutflow =
                immutableVisible(
                        visibleGeneratingOutflow
                );

        hiddenGeneratingOutflow =
                immutableHidden(
                        hiddenGeneratingOutflow
                );

        visibleWealthDirection =
                immutableVisible(
                        visibleWealthDirection
                );

        hiddenWealthDirection =
                immutableHidden(
                        hiddenWealthDirection
                );

        visibleControlPressure =
                immutableVisible(
                        visibleControlPressure
                );

        hiddenControlPressure =
                immutableHidden(
                        hiddenControlPressure
                );

        validateVisible(
                dayMaster,
                visibleGeneratingSupport,
                DayMasterRelationType
                        .GENERATES_DAY_MASTER
        );

        validateVisible(
                dayMaster,
                visibleSameElementSupport,
                DayMasterRelationType
                        .SAME_ELEMENT
        );

        validateHidden(
                dayMaster,
                hiddenGeneratingSupport,
                DayMasterRelationType
                        .GENERATES_DAY_MASTER
        );

        validateHidden(
                dayMaster,
                hiddenSameElementSupport,
                DayMasterRelationType
                        .SAME_ELEMENT
        );

        validateVisible(
                dayMaster,
                visibleGeneratingOutflow,
                DayMasterRelationType
                        .GENERATED_BY_DAY_MASTER
        );

        validateHidden(
                dayMaster,
                hiddenGeneratingOutflow,
                DayMasterRelationType
                        .GENERATED_BY_DAY_MASTER
        );

        validateVisible(
                dayMaster,
                visibleWealthDirection,
                DayMasterRelationType
                        .CONTROLLED_BY_DAY_MASTER
        );

        validateHidden(
                dayMaster,
                hiddenWealthDirection,
                DayMasterRelationType
                        .CONTROLLED_BY_DAY_MASTER
        );

        validateVisible(
                dayMaster,
                visibleControlPressure,
                DayMasterRelationType
                        .CONTROLS_DAY_MASTER
        );

        validateHidden(
                dayMaster,
                hiddenControlPressure,
                DayMasterRelationType
                        .CONTROLS_DAY_MASTER
        );
    }

    /*
     * =================================================
     * 천간 생조
     * =================================================
     */

    public boolean hasVisibleGeneratingSupport() {

        return !visibleGeneratingSupport
                .isEmpty();
    }

    public boolean hasVisibleSameElementSupport() {

        return !visibleSameElementSupport
                .isEmpty();
    }

    public boolean hasAnyVisibleSupport() {

        return hasVisibleGeneratingSupport()
                ||
                hasVisibleSameElementSupport();
    }

    /*
     * =================================================
     * 지장간 생조 기반
     * =================================================
     */

    public boolean hasHiddenGeneratingSupport() {

        return !hiddenGeneratingSupport
                .isEmpty();
    }

    public boolean hasHiddenSameElementSupport() {

        return !hiddenSameElementSupport
                .isEmpty();
    }

    public boolean hasAnyHiddenSupport() {

        return hasHiddenGeneratingSupport()
                ||
                hasHiddenSameElementSupport();
    }

    /*
     * =================================================
     * 천간 + 지장간 전체 생조 존재
     *
     * 개수를 합산하지 않는다.
     * 존재 구조만 확인한다.
     * =================================================
     */

    public boolean hasAnyGeneratingSupport() {

        return hasVisibleGeneratingSupport()
                ||
                hasHiddenGeneratingSupport();
    }

    public boolean hasAnySameElementSupport() {

        return hasVisibleSameElementSupport()
                ||
                hasHiddenSameElementSupport();
    }

    /*
     * =================================================
     * 설기
     *
     * 일간이 다른 오행을 생함.
     * =================================================
     */

    public boolean hasVisibleGeneratingOutflow() {

        return !visibleGeneratingOutflow
                .isEmpty();
    }

    public boolean hasHiddenGeneratingOutflow() {

        return !hiddenGeneratingOutflow
                .isEmpty();
    }

    public boolean hasGeneratingOutflowStructure() {

        return hasVisibleGeneratingOutflow()
                ||
                hasHiddenGeneratingOutflow();
    }

    /*
     * =================================================
     * 재성 방향
     *
     * 일간이 상대를 극함.
     *
     * 이를 단순히 "일간을 극하는 적"으로
     * 취급하지 않는다.
     * =================================================
     */

    public boolean hasVisibleWealthDirection() {

        return !visibleWealthDirection
                .isEmpty();
    }

    public boolean hasHiddenWealthDirection() {

        return !hiddenWealthDirection
                .isEmpty();
    }

    public boolean hasWealthDirectionStructure() {

        return hasVisibleWealthDirection()
                ||
                hasHiddenWealthDirection();
    }

    /*
     * =================================================
     * 관살 방향
     *
     * 상대 오행이 일간을 극함.
     * =================================================
     */

    public boolean hasVisibleControlPressure() {

        return !visibleControlPressure
                .isEmpty();
    }

    public boolean hasHiddenControlPressure() {

        return !hiddenControlPressure
                .isEmpty();
    }

    public boolean hasControlPressureStructure() {

        return hasVisibleControlPressure()
                ||
                hasHiddenControlPressure();
    }

    /*
     * =================================================
     * 생조와 설극 구조가 동시에 존재하는가.
     *
     * 이것은 "상쇄" 계산이 아니다.
     *
     * 단순히 양쪽 구조가 함께 존재한다는
     * 명리적 Context를 표시한다.
     * =================================================
     */

    public boolean hasSupportAndOutflowTogether() {

        return (
                hasAnyGeneratingSupport()
                        ||
                        hasAnySameElementSupport()
        )
                &&
                hasGeneratingOutflowStructure();
    }

    public boolean hasSupportAndWealthTogether() {

        return (
                hasAnyGeneratingSupport()
                        ||
                        hasAnySameElementSupport()
        )
                &&
                hasWealthDirectionStructure();
    }

    public boolean hasSupportAndControlTogether() {

        return (
                hasAnyGeneratingSupport()
                        ||
                        hasAnySameElementSupport()
        )
                &&
                hasControlPressureStructure();
    }

    /**
     * 생조가 존재하면서
     * 설기/재성/관살 방향도 함께 존재.
     *
     * 어느 쪽이 강하다는 뜻이 아니다.
     */
    public boolean mixedForceContext() {

        boolean support =
                hasAnyGeneratingSupport()
                        ||
                        hasAnySameElementSupport();

        boolean otherDirection =
                hasGeneratingOutflowStructure()
                        ||
                        hasWealthDirectionStructure()
                        ||
                        hasControlPressureStructure();

        return support
                &&
                otherDirection;
    }

    /*
     * =================================================
     * 위치별 조회
     * =================================================
     */

    public List<DayMasterHiddenStemRelation>
    hiddenSupportAt(
            PillarPosition position
    ) {

        Objects.requireNonNull(
                position,
                "기둥 위치는 필수입니다."
        );

        return java.util.stream.Stream
                .concat(
                        hiddenGeneratingSupport.stream(),
                        hiddenSameElementSupport.stream()
                )
                .filter(
                        relation ->
                                relation.branchPosition()
                                        == position
                )
                .toList();
    }

    public List<DayMasterHiddenStemRelation>
    monthHiddenSupport() {

        return hiddenSupportAt(
                PillarPosition.MONTH
        );
    }

    public List<DayMasterHiddenStemRelation>
    dayHiddenSupport() {

        return hiddenSupportAt(
                PillarPosition.DAY
        );
    }

    private static List<DayMasterVisibleStemRelation>
    immutableVisible(
            List<DayMasterVisibleStemRelation> source
    ) {

        if (source == null) {

            return List.of();
        }

        return List.copyOf(
                source
        );
    }

    private static List<DayMasterHiddenStemRelation>
    immutableHidden(
            List<DayMasterHiddenStemRelation> source
    ) {

        if (source == null) {

            return List.of();
        }

        return List.copyOf(
                source
        );
    }

    private static void validateVisible(

            HeavenlyStem dayMaster,

            List<DayMasterVisibleStemRelation> relations,

            DayMasterRelationType expectedType

    ) {

        for (
                DayMasterVisibleStemRelation relation
                : relations
        ) {

            Objects.requireNonNull(
                    relation,
                    "천간 관계 목록에 null이 포함될 수 없습니다."
            );

            if (
                    relation.dayMaster()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "천간 관계의 일간이 분석 대상과 일치하지 않습니다."
                );
            }

            if (
                    relation.relationType()
                            != expectedType
            ) {

                throw new IllegalArgumentException(
                        "천간 관계 종류가 올바르지 않습니다."
                );
            }
        }
    }

    private static void validateHidden(

            HeavenlyStem dayMaster,

            List<DayMasterHiddenStemRelation> relations,

            DayMasterRelationType expectedType

    ) {

        for (
                DayMasterHiddenStemRelation relation
                : relations
        ) {

            Objects.requireNonNull(
                    relation,
                    "지장간 관계 목록에 null이 포함될 수 없습니다."
            );

            if (
                    relation.dayMaster()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "지장간 관계의 일간이 분석 대상과 일치하지 않습니다."
                );
            }

            if (
                    relation.relationType()
                            != expectedType
            ) {

                throw new IllegalArgumentException(
                        "지장간 관계 종류가 올바르지 않습니다."
                );
            }
        }
    }
}