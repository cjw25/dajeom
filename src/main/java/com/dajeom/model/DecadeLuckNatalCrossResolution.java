package com.dajeom.model;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 13차-5.
 *
 * 하나의 대운에 대해
 * 기존 관계/합화 엔진이 해결한 결과.
 *
 * =================================================
 *
 * relations
 *
 * 8차 기반.
 *
 * =================================================
 *
 * transformations
 *
 * 9차 기반.
 *
 * =================================================
 *
 * period를 함께 보존해서
 * 다른 대운 결과가 잘못 연결되는 것을 방지한다.
 */
public record DecadeLuckNatalCrossResolution(

        DecadeLuckPeriod period,

        List<DecadeLuckNatalRelationReference> relations,

        List<DecadeLuckNatalTransformationReference> transformations

) {

    public DecadeLuckNatalCrossResolution {

        Objects.requireNonNull(
                period,
                "대운 Cross Resolution에는 Period가 필요합니다."
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
                DecadeLuckNatalRelationReference relation
                : relations
        ) {

            Objects.requireNonNull(
                    relation,
                    "대운 관계 Reference는 null일 수 없습니다."
            );
        }

        for (
                DecadeLuckNatalTransformationReference transformation
                : transformations
        ) {

            Objects.requireNonNull(
                    transformation,
                    "대운 합화 Reference는 null일 수 없습니다."
            );
        }

        /*
         * =================================================
         * 관계 중복 방지
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

                DecadeLuckNatalRelationReference a =
                        relations.get(
                                first
                        );

                DecadeLuckNatalRelationReference b =
                        relations.get(
                                second
                        );

                if (
                        a.layer() == b.layer()
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
                            "동일한 대운-원국 관계가 중복되었습니다: "
                                    + a.relationCode()
                    );
                }
            }
        }

        /*
         * =================================================
         * 합화 중복 방지
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

                DecadeLuckNatalTransformationReference a =
                        transformations.get(
                                first
                        );

                DecadeLuckNatalTransformationReference b =
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
                            "동일한 대운 합화 Context가 중복되었습니다: "
                                    + a.transformationCode()
                    );
                }
            }
        }
    }
}