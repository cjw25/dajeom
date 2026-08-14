package com.dajeom.model;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 14차-4.
 *
 * 하나의 세운에 대해
 * 기존 8차/9차 엔진이 해결한
 * 정규화된 Cross 결과.
 *
 * =================================================
 *
 * relations
 *
 * 기존 8차 관계 결과.
 *
 * =================================================
 *
 * transformations
 *
 * 기존 9차 합화 결과.
 */
public record AnnualLuckNatalCrossResolution(

        AnnualLuckPeriod period,

        List<AnnualLuckNatalRelationReference> relations,

        List<AnnualLuckNatalTransformationReference> transformations

) {

    public AnnualLuckNatalCrossResolution {

        Objects.requireNonNull(
                period,
                "세운 Cross Resolution에는 AnnualLuckPeriod가 필요합니다."
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
                AnnualLuckNatalRelationReference relation
                : relations
        ) {

            Objects.requireNonNull(
                    relation,
                    "세운 관계 Reference는 null일 수 없습니다."
            );
        }

        for (
                AnnualLuckNatalTransformationReference transformation
                : transformations
        ) {

            Objects.requireNonNull(
                    transformation,
                    "세운 합화 Reference는 null일 수 없습니다."
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

                AnnualLuckNatalRelationReference a =
                        relations.get(
                                first
                        );

                AnnualLuckNatalRelationReference b =
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
                            "동일 세운-원국 관계가 중복되었습니다: "
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

                AnnualLuckNatalTransformationReference a =
                        transformations.get(
                                first
                        );

                AnnualLuckNatalTransformationReference b =
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
                            "동일 세운 합화 Context가 중복되었습니다: "
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