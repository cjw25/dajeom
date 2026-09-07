package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 일간의 모든 통근 지점과
 * 원국 지지관계 연결 결과.
 *
 * 점수 없음.
 * 뿌리 개수 합산 없음.
 *
 * 이후 명리 규칙에서:
 *
 * - 충
 * - 합
 * - 형
 * - 파
 * - 해
 * - 삼합
 * - 방합
 *
 * 이 실제 통근 지점에 어떤 영향을 주는지를
 * 판단하기 위한 기반 자료다.
 */
public record DayMasterRootContextAnalysis(

        HeavenlyStem dayMaster,

        List<DayMasterRootRelationContext> exactStemRootContexts,

        List<DayMasterRootRelationContext> sameElementRootContexts

) {

    public DayMasterRootContextAnalysis {

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

    public boolean hasExactStemRoot() {

        return !exactStemRootContexts.isEmpty();
    }

    public boolean hasSameElementRoot() {

        return !sameElementRootContexts.isEmpty();
    }

    public boolean hasAnyRoot() {

        return hasExactStemRoot()
                ||
                hasSameElementRoot();
    }

    public List<DayMasterRootRelationContext> rootsAt(
            PillarPosition position
    ) {

        Objects.requireNonNull(
                position,
                "기둥 위치는 필수입니다."
        );

        return java.util.stream.Stream
                .concat(
                        exactStemRootContexts.stream(),
                        sameElementRootContexts.stream()
                )
                .filter(
                        context ->
                                context.rootPosition()
                                        == position
                )
                .toList();
    }

    public List<DayMasterRootRelationContext> monthRoots() {

        return rootsAt(
                PillarPosition.MONTH
        );
    }

    public List<DayMasterRootRelationContext> dayRoots() {

        return rootsAt(
                PillarPosition.DAY
        );
    }

    private static void validate(

            HeavenlyStem dayMaster,

            List<DayMasterRootRelationContext> contexts,

            StemGroundRelationType expectedType

    ) {

        for (
                DayMasterRootRelationContext context
                : contexts
        ) {

            Objects.requireNonNull(
                    context,
                    "통근 Context 목록에 null이 포함될 수 없습니다."
            );

            if (
                    context.root()
                            .targetStem()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "통근 대상 천간이 일간과 일치하지 않습니다."
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