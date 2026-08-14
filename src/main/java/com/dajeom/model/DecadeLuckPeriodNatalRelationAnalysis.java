package com.dajeom.model;

import com.dajeom.type.DecadeLuckNatalRelationLayer;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PalacePosition;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 13차-5.
 *
 * 하나의 대운 Period와
 * 원국 사이의 관계 분석.
 *
 * =================================================
 *
 * 예:
 *
 * 1대운 己未
 *
 * ↓
 *
 * 천간:
 *
 * 己 ↔ 원국 甲
 *
 * 지지:
 *
 * 未 ↔ 원국 丑
 *
 * 다중:
 *
 * 대운 지지가 원국의 여러 지지와
 * 삼합/방합 구조를 구성
 *
 * 합화:
 *
 * 9차 판정 결과
 *
 * 를 한 Period에 묶는다.
 */
public record DecadeLuckPeriodNatalRelationAnalysis(

        HeavenlyStem dayMaster,

        PalaceAnalysis natalAnalysis,

        DecadeLuckPeriod period,

        List<DecadeLuckNatalRelationReference> relations,

        List<DecadeLuckNatalTransformationReference> transformations

) {

    public DecadeLuckPeriodNatalRelationAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "대운-원국 관계 분석에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                natalAnalysis,
                "원국 PalaceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                period,
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

        if (
                natalAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "원국 PalaceAnalysis와 대운 관계 분석의 일간이 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Palace 존재 여부
         * =================================================
         *
         * 시간 미상 원국에서
         * Resolver가 HOUR 관계를 반환하는 것도
         * 여기서 차단된다.
         */

        for (
                DecadeLuckNatalRelationReference relation
                : relations
        ) {

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
                            "존재하지 않는 원국 Palace가 대운 관계에 포함되었습니다: "
                                    + position
                    );
                }
            }
        }

        for (
                DecadeLuckNatalTransformationReference transformation
                : transformations
        ) {

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
                            "존재하지 않는 원국 Palace가 대운 합화 Context에 포함되었습니다: "
                                    + position
                    );
                }
            }
        }
    }

    public int sequenceNumber() {

        return period
                .sequenceNumber();
    }

    public SexagenaryPillar luckPillar() {

        return period
                .pillar();
    }

    /*
     * =================================================
     * 관계 Layer
     * =================================================
     */

    public List<DecadeLuckNatalRelationReference>
    stemRelations() {

        return relations
                .stream()
                .filter(
                        relation ->
                                relation.layer()
                                        == DecadeLuckNatalRelationLayer
                                        .HEAVENLY_STEM
                )
                .toList();
    }

    public List<DecadeLuckNatalRelationReference>
    branchRelations() {

        return relations
                .stream()
                .filter(
                        relation ->
                                relation.layer()
                                        == DecadeLuckNatalRelationLayer
                                        .EARTHLY_BRANCH
                )
                .toList();
    }

    /*
     * =================================================
     * Palace별 관계
     * =================================================
     */

    public List<DecadeLuckNatalRelationReference>
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

    public List<DecadeLuckNatalTransformationReference>
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
     * 상태 Shortcut
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

    public boolean hasActivatedTransformation() {

        return transformations
                .stream()
                .anyMatch(
                        DecadeLuckNatalTransformationReference
                                ::activated
                );
    }

    public boolean hasReducedOriginalFunction() {

        return transformations
                .stream()
                .anyMatch(
                        DecadeLuckNatalTransformationReference
                                ::originalFunctionReduced
                );
    }
}