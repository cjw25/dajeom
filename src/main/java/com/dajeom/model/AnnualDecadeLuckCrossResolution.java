package com.dajeom.model;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 14차-5.
 *
 * 하나의:
 *
 * AnnualLuckPeriod
 * ×
 * DecadeLuckPeriod
 *
 * 조합에 대해 8/9차 Resolver가
 * 해결한 정규화 결과.
 */
public record AnnualDecadeLuckCrossResolution(

        AnnualLuckPeriod annualPeriod,

        DecadeLuckPeriod decadePeriod,

        List<AnnualDecadeLuckRelationReference> relations,

        List<AnnualDecadeLuckTransformationReference> transformations

) {

    public AnnualDecadeLuckCrossResolution {

        Objects.requireNonNull(
                annualPeriod,
                "세운 Period가 필요합니다."
        );

        Objects.requireNonNull(
                decadePeriod,
                "대운 Period가 필요합니다."
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
                AnnualDecadeLuckRelationReference relation
                : relations
        ) {

            Objects.requireNonNull(
                    relation,
                    "대운-세운 관계는 null일 수 없습니다."
            );
        }

        for (
                AnnualDecadeLuckTransformationReference transformation
                : transformations
        ) {

            Objects.requireNonNull(
                    transformation,
                    "대운-세운 합화 Reference는 null일 수 없습니다."
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

                AnnualDecadeLuckRelationReference a =
                        relations.get(
                                first
                        );

                AnnualDecadeLuckRelationReference b =
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
                            "동일 대운-세운 관계가 중복되었습니다: "
                                    + a.relationCode()
                    );
                }
            }
        }

        /*
         * =================================================
         * Transformation 중복 방지
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

                AnnualDecadeLuckTransformationReference a =
                        transformations.get(
                                first
                        );

                AnnualDecadeLuckTransformationReference b =
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
                            "동일 대운-세운 Transformation이 중복되었습니다: "
                                    + a.transformationCode()
                    );
                }
            }
        }
    }
}