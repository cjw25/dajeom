package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 일간의 得地 영역 구조 분석.
 *
 * =================================================
 *
 * 입력 기반:
 *
 * DayMasterEffectiveRootAnalysis
 *
 * =================================================
 *
 * 보존:
 *
 * - 전체 근
 * - 관계 간섭 없는 근
 * - 충/형/파/해 Context가 있는 근
 * - 합화가 일간을 생조하는 근 Context
 * - 합화가 생조 방향이 아닌 근 Context
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * - 점수 계산
 * - 근 개수 비교
 * - 得地 true/false
 * - 신강/신약 확정
 */
public record DayMasterGroundStatusAnalysis(

        HeavenlyStem dayMaster,

        DayMasterGroundStatus status,

        List<DayMasterEffectiveRootContext> allRoots,

        List<DayMasterEffectiveRootContext> plainRoots,

        List<DayMasterEffectiveRootContext> disturbedRoots,

        List<DayMasterEffectiveRootContext>
        supportiveTransformationRoots,

        List<DayMasterEffectiveRootContext>
        nonSupportiveTransformationRoots

) {

    public DayMasterGroundStatusAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                status,
                "득지 구조 상태는 필수입니다."
        );

        allRoots =
                immutable(
                        allRoots
                );

        plainRoots =
                immutable(
                        plainRoots
                );

        disturbedRoots =
                immutable(
                        disturbedRoots
                );

        supportiveTransformationRoots =
                immutable(
                        supportiveTransformationRoots
                );

        nonSupportiveTransformationRoots =
                immutable(
                        nonSupportiveTransformationRoots
                );

        validateDayMaster(
                dayMaster,
                allRoots
        );

        validateDayMaster(
                dayMaster,
                plainRoots
        );

        validateDayMaster(
                dayMaster,
                disturbedRoots
        );

        validateDayMaster(
                dayMaster,
                supportiveTransformationRoots
        );

        validateDayMaster(
                dayMaster,
                nonSupportiveTransformationRoots
        );

        if (
                status
                        == DayMasterGroundStatus.NO_ROOT
                        &&
                        !allRoots.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "NO_ROOT 상태에는 통근 Context가 존재할 수 없습니다."
            );
        }

        if (
                status
                        != DayMasterGroundStatus.NO_ROOT
                        &&
                        allRoots.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "근이 존재하는 상태인데 통근 Context가 없습니다."
            );
        }
    }

    /**
     * 원국에 어떤 형태로든
     * 일간의 지지 연결이 존재하는가.
     *
     * 이 메서드는 得地 확정이 아니다.
     */
    public boolean hasRootContext() {

        return !allRoots.isEmpty();
    }

    /**
     * 동일 천간으로 직접 통근한 Context.
     */
    public List<DayMasterEffectiveRootContext>
    exactStemRoots() {

        return allRoots
                .stream()
                .filter(
                        DayMasterEffectiveRootContext
                                ::exactStemRoot
                )
                .toList();
    }

    /**
     * 같은 오행의 다른 천간 연결.
     *
     * 동일 천간 근과 별도로 유지한다.
     */
    public List<DayMasterEffectiveRootContext>
    sameElementOtherStemRoots() {

        return allRoots
                .stream()
                .filter(
                        DayMasterEffectiveRootContext
                                ::sameElementOtherStemRoot
                )
                .toList();
    }

    public List<DayMasterEffectiveRootContext>
    rootsAt(
            PillarPosition position
    ) {

        Objects.requireNonNull(
                position,
                "기둥 위치는 필수입니다."
        );

        return allRoots
                .stream()
                .filter(
                        root ->
                                root.rootPosition()
                                        == position
                )
                .toList();
    }

    public List<DayMasterEffectiveRootContext>
    monthRoots() {

        return rootsAt(
                PillarPosition.MONTH
        );
    }

    public List<DayMasterEffectiveRootContext>
    dayRoots() {

        return rootsAt(
                PillarPosition.DAY
        );
    }

    public List<DayMasterEffectiveRootContext>
    yearRoots() {

        return rootsAt(
                PillarPosition.YEAR
        );
    }

    public List<DayMasterEffectiveRootContext>
    hourRoots() {

        return rootsAt(
                PillarPosition.HOUR
        );
    }

    /**
     * 충/형/파/해 등 직접적인
     * 관계 간섭이 확인된 근 Context.
     */
    public boolean hasDisturbedRoot() {

        return !disturbedRoots
                .isEmpty();
    }

    /**
     * ACCEPTED 합화 작용이
     * 일간을 생하거나 동류로 작용하는
     * 근 Context가 존재하는가.
     */
    public boolean hasSupportiveTransformationRoot() {

        return !supportiveTransformationRoots
                .isEmpty();
    }

    /**
     * ACCEPTED 합화 작용이 있지만
     * 일간 생조 방향은 아닌 근 Context.
     */
    public boolean hasNonSupportiveTransformationRoot() {

        return !nonSupportiveTransformationRoots
                .isEmpty();
    }

    /**
     * 다양한 근 상태가 함께 존재하는지.
     *
     * 상태를 숫자로 세는 것이 아니라
     * 종류가 복합적으로 존재하는지 확인한다.
     */
    public boolean mixedContext() {

        return status
                == DayMasterGroundStatus
                .MIXED_ROOT_CONTEXT;
    }

    private static List<DayMasterEffectiveRootContext>
    immutable(
            List<DayMasterEffectiveRootContext> source
    ) {

        if (source == null) {

            return List.of();
        }

        return List.copyOf(
                source
        );
    }

    private static void validateDayMaster(

            HeavenlyStem dayMaster,

            List<DayMasterEffectiveRootContext> roots

    ) {

        for (
                DayMasterEffectiveRootContext root
                : roots
        ) {

            Objects.requireNonNull(
                    root,
                    "통근 Context 목록에 null이 포함될 수 없습니다."
            );

            if (
                    root.dayMaster()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "통근 Context의 일간이 분석 대상 일간과 일치하지 않습니다."
                );
            }
        }
    }
}