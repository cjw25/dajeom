package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 일간의 모든 통근과
 * ACCEPTED 합화 작용 연결 결과.
 *
 * =================================================
 *
 * exactStemRootContexts
 *
 * → 일간과 동일 천간으로 통근한 지점
 *
 * =================================================
 *
 * sameElementRootContexts
 *
 * → 같은 오행의 다른 천간으로 연결된 지점
 *
 * =================================================
 *
 * 합화 작용이 없는 통근도 삭제하지 않고
 * Context 안에 그대로 포함한다.
 */
public record DayMasterRootTransformationAnalysis(

        HeavenlyStem dayMaster,

        List<DayMasterRootTransformationContext>
        exactStemRootContexts,

        List<DayMasterRootTransformationContext>
        sameElementRootContexts

) {

    public DayMasterRootTransformationAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        exactStemRootContexts =
                exactStemRootContexts == null
                        ? List.of()
                        : List.copyOf(
                        exactStemRootContexts
                );

        sameElementRootContexts =
                sameElementRootContexts == null
                        ? List.of()
                        : List.copyOf(
                        sameElementRootContexts
                );

        validate(
                dayMaster,
                exactStemRootContexts,
                StemGroundRelationType.EXACT_STEM
        );

        validate(
                dayMaster,
                sameElementRootContexts,
                StemGroundRelationType.SAME_ELEMENT_OTHER_STEM
        );
    }

    public boolean hasAnyRoot() {

        return !exactStemRootContexts.isEmpty()
                ||
                !sameElementRootContexts.isEmpty();
    }

    /**
     * 합화 작용이 걸린 통근만 조회.
     */
    public List<DayMasterRootTransformationContext>
    transformedRootContexts() {

        return allRootContexts()
                .stream()
                .filter(
                        DayMasterRootTransformationContext
                                ::hasTransformationEffect
                )
                .toList();
    }

    /**
     * 합화 작용이 없는 통근.
     */
    public List<DayMasterRootTransformationContext>
    originalOnlyRootContexts() {

        return allRootContexts()
                .stream()
                .filter(
                        context ->
                                !context
                                        .hasTransformationEffect()
                )
                .toList();
    }

    public boolean hasTransformedRootContext() {

        return !transformedRootContexts()
                .isEmpty();
    }

    public List<DayMasterRootTransformationContext>
    rootsAt(
            PillarPosition position
    ) {

        Objects.requireNonNull(
                position,
                "기둥 위치는 필수입니다."
        );

        return allRootContexts()
                .stream()
                .filter(
                        context ->
                                context.rootPosition()
                                        == position
                )
                .toList();
    }

    public List<DayMasterRootTransformationContext>
    monthRootContexts() {

        return rootsAt(
                PillarPosition.MONTH
        );
    }

    public List<DayMasterRootTransformationContext>
    dayRootContexts() {

        return rootsAt(
                PillarPosition.DAY
        );
    }

    private List<DayMasterRootTransformationContext>
    allRootContexts() {

        return java.util.stream.Stream
                .concat(
                        exactStemRootContexts.stream(),
                        sameElementRootContexts.stream()
                )
                .toList();
    }

    private static void validate(

            HeavenlyStem dayMaster,

            List<DayMasterRootTransformationContext> contexts,

            StemGroundRelationType expectedType

    ) {

        for (
                DayMasterRootTransformationContext context
                : contexts
        ) {

            Objects.requireNonNull(
                    context,
                    "통근 합화 Context 목록에 null이 포함될 수 없습니다."
            );

            if (
                    context.dayMaster()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "통근 합화 Context의 일간이 일치하지 않습니다."
                );
            }

            if (
                    context.rootRelationType()
                            != expectedType
            ) {

                throw new IllegalArgumentException(
                        "통근 관계 종류가 올바르지 않습니다."
                );
            }
        }
    }
}