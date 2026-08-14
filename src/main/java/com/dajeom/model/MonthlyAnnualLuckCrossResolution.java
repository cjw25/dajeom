package com.dajeom.model;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 15차-6.
 *
 * 하나의:
 *
 * AnnualLuckPeriod
 * ×
 * MonthlyLuckPeriod
 *
 * 조합에 대해 기존 8/9차 계층이
 * 해결한 정규화 결과.
 */
public record MonthlyAnnualLuckCrossResolution(

        AnnualLuckPeriod annualPeriod,

        MonthlyLuckPeriod monthlyPeriod,

        List<MonthlyAnnualLuckRelationReference> relations,

        List<MonthlyAnnualLuckTransformationReference> transformations

) {

    public MonthlyAnnualLuckCrossResolution {

        Objects.requireNonNull(
                annualPeriod,
                "세운-월운 Cross Resolution에는 AnnualLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                monthlyPeriod,
                "세운-월운 Cross Resolution에는 MonthlyLuckPeriod가 필요합니다."
        );

        /*
         * =================================================
         * 월운은 반드시 해당 세운의 Period여야 한다.
         * =================================================
         */

        if (
                monthlyPeriod.annualPeriod()
                        != annualPeriod
        ) {

            throw new IllegalArgumentException(
                    "MonthlyLuckPeriod가 현재 AnnualLuckPeriod에 속하지 않습니다."
            );
        }

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
                MonthlyAnnualLuckRelationReference relation
                : relations
        ) {

            Objects.requireNonNull(
                    relation,
                    "세운-월운 Relation은 null일 수 없습니다."
            );
        }

        for (
                MonthlyAnnualLuckTransformationReference transformation
                : transformations
        ) {

            Objects.requireNonNull(
                    transformation,
                    "세운-월운 Transformation은 null일 수 없습니다."
            );
        }

        /*
         * =================================================
         * 동일 관계 중복 방지
         * =================================================
         *
         * 같은 Layer라도 서로 다른 관계 Code라면
         * 동시에 보존할 수 있다.
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

                MonthlyAnnualLuckRelationReference a =
                        relations.get(
                                first
                        );

                MonthlyAnnualLuckRelationReference b =
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
                            "동일 세운-월운 관계가 중복되었습니다: "
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

                MonthlyAnnualLuckTransformationReference a =
                        transformations.get(
                                first
                        );

                MonthlyAnnualLuckTransformationReference b =
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
                            "동일 세운-월운 Transformation이 중복되었습니다: "
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