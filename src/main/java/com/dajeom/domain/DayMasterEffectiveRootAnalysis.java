package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 일간 전체 통근의
 * 원국 관계 + 합화 작용 통합 분석.
 *
 * =================================================
 *
 * exactStemRoots
 *
 * → 일간과 동일 천간으로 연결된 실제 근
 *
 * =================================================
 *
 * sameElementRoots
 *
 * → 같은 오행의 다른 천간으로 연결된 Context
 *
 * =================================================
 *
 * 여전히 둘을 같은 강도의 근으로
 * 취급하지 않는다.
 *
 * 숫자 계산 없음.
 */
public record DayMasterEffectiveRootAnalysis(

        HeavenlyStem dayMaster,

        List<DayMasterEffectiveRootContext>
        exactStemRoots,

        List<DayMasterEffectiveRootContext>
        sameElementRoots

) {

    public DayMasterEffectiveRootAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        exactStemRoots =
                exactStemRoots == null
                        ? List.of()
                        : List.copyOf(
                        exactStemRoots
                );

        sameElementRoots =
                sameElementRoots == null
                        ? List.of()
                        : List.copyOf(
                        sameElementRoots
                );

        validate(
                dayMaster,
                exactStemRoots,
                StemGroundRelationType.EXACT_STEM
        );

        validate(
                dayMaster,
                sameElementRoots,
                StemGroundRelationType
                        .SAME_ELEMENT_OTHER_STEM
        );
    }

    public boolean hasExactStemRoot() {

        return !exactStemRoots
                .isEmpty();
    }

    public boolean hasSameElementRoot() {

        return !sameElementRoots
                .isEmpty();
    }

    public boolean hasAnyRootContext() {

        return hasExactStemRoot()
                ||
                hasSameElementRoot();
    }

    public List<DayMasterEffectiveRootContext>
    allRoots() {

        return java.util.stream.Stream
                .concat(
                        exactStemRoots.stream(),
                        sameElementRoots.stream()
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

        return allRoots()
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
    rootsWithDisturbance() {

        return allRoots()
                .stream()
                .filter(
                        DayMasterEffectiveRootContext
                                ::hasDisturbanceContext
                )
                .toList();
    }

    public List<DayMasterEffectiveRootContext>
    rootsWithTransformation() {

        return allRoots()
                .stream()
                .filter(
                        DayMasterEffectiveRootContext
                                ::hasTransformationEffect
                )
                .toList();
    }

    public List<DayMasterEffectiveRootContext>
    transformationSupportingRoots() {

        return allRoots()
                .stream()
                .filter(
                        DayMasterEffectiveRootContext
                                ::transformationSupportsDayMaster
                )
                .toList();
    }

    public List<DayMasterEffectiveRootContext>
    transformationControllingRoots() {

        return allRoots()
                .stream()
                .filter(
                        DayMasterEffectiveRootContext
                                ::transformationControlsDayMaster
                )
                .toList();
    }

    private static void validate(

            HeavenlyStem dayMaster,

            List<DayMasterEffectiveRootContext> roots,

            StemGroundRelationType expectedType

    ) {

        for (
                DayMasterEffectiveRootContext root
                : roots
        ) {

            Objects.requireNonNull(
                    root,
                    "통근 목록에 null이 포함될 수 없습니다."
            );

            if (
                    root.dayMaster()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "통근 Context의 일간이 일치하지 않습니다."
                );
            }

            if (
                    root.rootRelationType()
                            != expectedType
            ) {

                throw new IllegalArgumentException(
                        "통근 관계 종류가 일치하지 않습니다."
                );
            }
        }
    }
}