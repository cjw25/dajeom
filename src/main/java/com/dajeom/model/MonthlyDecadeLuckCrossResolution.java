package com.dajeom.model;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 15차-5.
 *
 * 하나의:
 *
 * MonthlyLuckPeriod
 * ×
 * DecadeLuckPeriod
 *
 * 조합에 대한 기존 8/9차 정규화 결과.
 */
public record MonthlyDecadeLuckCrossResolution(

        MonthlyLuckPeriod monthlyPeriod,

        DecadeLuckPeriod decadePeriod,

        List<MonthlyDecadeLuckRelationReference> relations,

        List<MonthlyDecadeLuckTransformationReference> transformations

) {

    public MonthlyDecadeLuckCrossResolution {

        Objects.requireNonNull(
                monthlyPeriod,
                "대운-월운 Cross Resolution에는 MonthlyLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                decadePeriod,
                "대운-월운 Cross Resolution에는 DecadeLuckPeriod가 필요합니다."
        );

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

        for (
                MonthlyDecadeLuckRelationReference relation
                : relations
        ) {

            Objects.requireNonNull(
                    relation,
                    "대운-월운 Relation은 null일 수 없습니다."
            );
        }

        for (
                MonthlyDecadeLuckTransformationReference transformation
                : transformations
        ) {

            Objects.requireNonNull(
                    transformation,
                    "대운-월운 Transformation은 null일 수 없습니다."
            );
        }

        /*
         * 동일 Relation 중복 방지.
         */
        for (
                int first = 0;
                first < relations.size();
                first++
        ) {

            for (
                    int second = first + 1;
                    second < relations.size();
                    second++
            ) {

                MonthlyDecadeLuckRelationReference a =
                        relations.get(
                                first
                        );

                MonthlyDecadeLuckRelationReference b =
                        relations.get(
                                second
                        );

                if (
                        a.layer()
                                == b.layer()

                                && a.relationCode()
                                .equals(
                                        b.relationCode()
                                )
                ) {

                    throw new IllegalArgumentException(
                            "동일 대운-월운 관계가 중복되었습니다: "
                                    + a.relationCode()
                    );
                }
            }
        }

        /*
         * 동일 Transformation 중복 방지.
         */
        for (
                int first = 0;
                first < transformations.size();
                first++
        ) {

            for (
                    int second = first + 1;
                    second < transformations.size();
                    second++
            ) {

                MonthlyDecadeLuckTransformationReference a =
                        transformations.get(
                                first
                        );

                MonthlyDecadeLuckTransformationReference b =
                        transformations.get(
                                second
                        );

                if (
                        a.transformationCode()
                                .equals(
                                        b.transformationCode()
                                )
                ) {

                    throw new IllegalArgumentException(
                            "동일 대운-월운 Transformation이 중복되었습니다: "
                                    + a.transformationCode()
                    );
                }
            }
        }
    }
}