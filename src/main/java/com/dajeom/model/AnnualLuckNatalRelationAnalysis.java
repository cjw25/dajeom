package com.dajeom.model;

import com.dajeom.type.AnnualLuckNatalRelationLayer;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PalacePosition;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 14차-4.
 *
 * 특정 세운과 원국 전체의 관계 분석.
 *
 * =================================================
 *
 * 포함:
 *
 * - 세운 천간 × 원국 천간
 * - 세운 지지 × 원국 지지
 * - 다중 관계
 * - 합화 Context
 *
 * =================================================
 *
 * 포함하지 않는 것:
 *
 * - 길흉 판정
 * - 점수
 * - 용신 평가
 * - 격국 평가
 * - 대운과의 관계
 *
 * =================================================
 *
 * 대운 × 세운은 14-5,
 * 교차 Context는 14-6에서 처리한다.
 */
public record AnnualLuckNatalRelationAnalysis(

        HeavenlyStem dayMaster,

        PalaceAnalysis natalAnalysis,

        AnnualLuckPeriod period,

        List<AnnualLuckNatalRelationReference> relations,

        List<AnnualLuckNatalTransformationReference> transformations

) {

    public AnnualLuckNatalRelationAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "세운-원국 관계 분석에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                natalAnalysis,
                "세운-원국 관계 분석에는 PalaceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                period,
                "세운-원국 관계 분석에는 AnnualLuckPeriod가 필요합니다."
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

        if (
                natalAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "원국과 세운 관계 분석의 일간이 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 존재하지 않는 Palace 차단
         * =================================================
         *
         * 시간 미상 원국에서는 HOUR가 존재하지 않기 때문에
         * Resolver가 HOUR 관계를 반환해도 여기서 차단한다.
         */

        for (
                AnnualLuckNatalRelationReference relation
                : relations
        ) {

            Objects.requireNonNull(
                    relation,
                    "세운 관계 Reference는 null일 수 없습니다."
            );

            for (
                    PalacePosition position
                    : relation.natalPositions()
            ) {

                if (
                        !natalAnalysis
                                .knownPositions()
                                .contains(
                                        position
                                )
                ) {

                    throw new IllegalArgumentException(
                            "존재하지 않는 원국 Palace가 세운 관계에 포함되었습니다: "
                                    + position
                    );
                }
            }
        }

        for (
                AnnualLuckNatalTransformationReference transformation
                : transformations
        ) {

            Objects.requireNonNull(
                    transformation,
                    "세운 합화 Reference는 null일 수 없습니다."
            );

            for (
                    PalacePosition position
                    : transformation.natalPositions()
            ) {

                if (
                        !natalAnalysis
                                .knownPositions()
                                .contains(
                                        position
                                )
                ) {

                    throw new IllegalArgumentException(
                            "존재하지 않는 원국 Palace가 세운 합화 Context에 포함되었습니다: "
                                    + position
                    );
                }
            }
        }
    }

    /*
     * =================================================
     * 세운
     * =================================================
     */

    public int targetYear() {

        return period
                .targetYear();
    }

    public SexagenaryPillar annualPillar() {

        return period
                .pillar();
    }

    /*
     * =================================================
     * 천간 관계
     * =================================================
     */

    public List<AnnualLuckNatalRelationReference>
    stemRelations() {

        return relations
                .stream()
                .filter(
                        relation ->
                                relation.layer()
                                        == AnnualLuckNatalRelationLayer
                                        .HEAVENLY_STEM
                )
                .toList();
    }

    /*
     * =================================================
     * 지지 관계
     * =================================================
     */

    public List<AnnualLuckNatalRelationReference>
    branchRelations() {

        return relations
                .stream()
                .filter(
                        relation ->
                                relation.layer()
                                        == AnnualLuckNatalRelationLayer
                                        .EARTHLY_BRANCH
                )
                .toList();
    }

    /*
     * =================================================
     * Palace별 조회
     * =================================================
     */

    public List<AnnualLuckNatalRelationReference>
    relationsOf(
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

    public List<AnnualLuckNatalTransformationReference>
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

    /*
     * =================================================
     * 관계 존재 여부
     * =================================================
     */

    public boolean hasRelations() {

        return !relations
                .isEmpty();
    }

    public boolean hasStemRelations() {

        return !stemRelations()
                .isEmpty();
    }

    public boolean hasBranchRelations() {

        return !branchRelations()
                .isEmpty();
    }

    public boolean hasTransformations() {

        return !transformations
                .isEmpty();
    }

    /*
     * =================================================
     * 합화
     * =================================================
     */

    public boolean hasActivatedTransformation() {

        return transformations
                .stream()
                .anyMatch(
                        AnnualLuckNatalTransformationReference
                                ::activated
                );
    }

    public boolean hasReducedOriginalFunction() {

        return transformations
                .stream()
                .anyMatch(
                        AnnualLuckNatalTransformationReference
                                ::originalFunctionReduced
                );
    }

    /*
     * =================================================
     * 특정 Palace 활성 여부
     * =================================================
     */

    public boolean involves(
            PalacePosition position
    ) {

        if (
                position == null
        ) {

            return false;
        }

        return !relationsOf(
                position
        )
                .isEmpty()

                || !transformationsOf(
                position
        )
                .isEmpty();
    }
}