package com.dajeom.model;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 15차-4.
 *
 * 특정 MonthlyLuckPeriod에 대해
 * 8차 관계 / 9차 합화 계층이 해결한
 * 정규화 결과.
 *
 * =================================================
 *
 * relations
 * = 관계 존재
 *
 * transformations
 * = 실제 합화·기능 변화
 */
public record MonthlyLuckNatalCrossResolution(

        MonthlyLuckPeriod period,

        List<MonthlyLuckNatalRelationReference> relations,

        List<MonthlyLuckNatalTransformationReference> transformations

) {

    public MonthlyLuckNatalCrossResolution {

        Objects.requireNonNull(
                period,
                "월운 Cross Resolution에는 MonthlyLuckPeriod가 필요합니다."
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
                MonthlyLuckNatalRelationReference relation
                : relations
        ) {

            Objects.requireNonNull(
                    relation,
                    "월운 관계 Reference는 null일 수 없습니다."
            );
        }

        for (
                MonthlyLuckNatalTransformationReference transformation
                : transformations
        ) {

            Objects.requireNonNull(
                    transformation,
                    "월운 Transformation Reference는 null일 수 없습니다."
            );
        }

        /*
         * =================================================
         * 동일 관계 중복 방지
         * =================================================
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

                MonthlyLuckNatalRelationReference a =
                        relations.get(
                                first
                        );

                MonthlyLuckNatalRelationReference b =
                        relations.get(
                                second
                        );

                if (
                        a.layer()
                                == b.layer()

                                && a.natalPositions()
                                .equals(
                                        b.natalPositions()
                                )

                                && a.relationCode()
                                .equals(
                                        b.relationCode()
                                )
                ) {

                    throw new IllegalArgumentException(
                            "동일 월운-원국 관계가 중복되었습니다: "
                                    + a.relationCode()
                    );
                }
            }
        }

        /*
         * =================================================
         * 동일 Transformation 중복 방지
         * =================================================
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

                MonthlyLuckNatalTransformationReference a =
                        transformations.get(
                                first
                        );

                MonthlyLuckNatalTransformationReference b =
                        transformations.get(
                                second
                        );

                if (
                        a.natalPositions()
                                .equals(
                                        b.natalPositions()
                                )

                                && a.transformationCode()
                                .equals(
                                        b.transformationCode()
                                )
                ) {

                    throw new IllegalArgumentException(
                            "동일 월운 Transformation이 중복되었습니다: "
                                    + a.transformationCode()
                    );
                }
            }
        }
    }

    public boolean hasRelations() {

        return !relations
                .isEmpty();
    }

    public boolean hasTransformations() {

        return !transformations
                .isEmpty();
    }
}