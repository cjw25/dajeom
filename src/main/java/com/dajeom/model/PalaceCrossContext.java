package com.dajeom.model;

import com.dajeom.type.PalacePosition;
import com.dajeom.type.PalaceRelationLayer;

import java.util.List;

/**
 * 정밀화 12차-4.
 *
 * 8차 관계 분석과
 * 9차 합화 분석을
 * 12차 궁위 엔진에 전달하는 Boundary.
 *
 * =================================================
 *
 * 8차/9차 내부 Model을
 * PalaceContextAnalyzer가 직접 해석하지 않는다.
 *
 * 이렇게 해야:
 *
 * 8차 Model 수정
 * 또는
 * 9차 Model 수정
 *
 * 이 발생해도 Adapter만 수정하면 된다.
 */
public record PalaceCrossContext(

        List<PalaceRelationReference> relations,

        List<PalaceTransformationReference> transformations,

        boolean completeChart

) {

    public PalaceCrossContext {

        relations =
                relations == null
                        ? List.of()
                        : List.copyOf(
                        relations
                );

        transformations =
                transformations == null
                        ? List.of()
                        : List.copyOf(
                        transformations
                );

        /*
         * =================================================
         * 시간 미상 방어
         * =================================================
         *
         * completeChart=false인데
         * HOUR를 포함한 관계/합화가 들어오면
         * 잘못된 upstream mapping이다.
         */

        if (
                !completeChart
        ) {

            boolean hourRelation =
                    relations
                            .stream()
                            .anyMatch(
                                    relation ->
                                            relation.involves(
                                                    PalacePosition.HOUR
                                            )
                            );

            boolean hourTransformation =
                    transformations
                            .stream()
                            .anyMatch(
                                    transformation ->
                                            transformation.involves(
                                                    PalacePosition.HOUR
                                            )
                            );

            if (
                    hourRelation
                            || hourTransformation
            ) {

                throw new IllegalArgumentException(
                        "시간 미상 원국에는 HOUR Palace 관계/합화를 연결할 수 없습니다."
                );
            }
        }
    }

    public static PalaceCrossContext neutral(
            boolean completeChart
    ) {

        return new PalaceCrossContext(
                List.of(),
                List.of(),
                completeChart
        );
    }

    public List<PalaceRelationReference> relationsOf(
            PalacePosition position
    ) {

        if (
                position == null
        ) {

            return List.of();
        }

        return relations
                .stream()
                .filter(
                        relation ->
                                relation.involves(
                                        position
                                )
                )
                .toList();
    }

    public List<PalaceRelationReference> stemRelationsOf(
            PalacePosition position
    ) {

        return relationsOf(
                position
        )
                .stream()
                .filter(
                        relation ->
                                relation.layer()
                                        == PalaceRelationLayer.HEAVENLY_STEM
                )
                .toList();
    }

    public List<PalaceRelationReference> branchRelationsOf(
            PalacePosition position
    ) {

        return relationsOf(
                position
        )
                .stream()
                .filter(
                        relation ->
                                relation.layer()
                                        == PalaceRelationLayer.EARTHLY_BRANCH
                )
                .toList();
    }

    public List<PalaceTransformationReference>
    transformationsOf(
            PalacePosition position
    ) {

        if (
                position == null
        ) {

            return List.of();
        }

        return transformations
                .stream()
                .filter(
                        transformation ->
                                transformation.involves(
                                        position
                                )
                )
                .toList();
    }
}