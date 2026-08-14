package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.MonthlyLuckNatalRelationLayer;
import com.dajeom.type.PalacePosition;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 15차-4.
 *
 * 특정 월운과 원국 전체 사이의 관계 분석.
 *
 * =================================================
 *
 * 포함:
 *
 * - 월운 천간 × 원국 천간
 * - 월운 지지 × 원국 지지
 * - 원국 Palace별 관계
 * - 다중관계
 * - 합화 Context
 *
 * =================================================
 *
 * 포함하지 않음:
 *
 * - 월운 × 대운
 * - 월운 × 세운
 * - 길흉 점수
 * - 용신 평가
 * - 생활영역 해석
 *
 * 위 내용은 후속 단계 책임이다.
 */
public record MonthlyLuckNatalRelationAnalysis(

        HeavenlyStem dayMaster,

        PalaceAnalysis natalAnalysis,

        MonthlyLuckPeriod period,

        List<MonthlyLuckNatalRelationReference> relations,

        List<MonthlyLuckNatalTransformationReference> transformations

) {

    public MonthlyLuckNatalRelationAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "월운-원국 분석에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                natalAnalysis,
                "월운-원국 분석에는 PalaceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                period,
                "월운-원국 분석에는 MonthlyLuckPeriod가 필요합니다."
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
                    "원국과 월운 관계 분석의 일간이 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Relation 검증
         * =================================================
         */

        for (
                MonthlyLuckNatalRelationReference relation
                : relations
        ) {

            Objects.requireNonNull(
                    relation,
                    "월운 관계 Reference는 null일 수 없습니다."
            );

            validateNatalPositions(
                    natalAnalysis,
                    relation.natalPositions()
            );
        }

        /*
         * =================================================
         * Transformation 검증
         * =================================================
         */

        for (
                MonthlyLuckNatalTransformationReference transformation
                : transformations
        ) {

            Objects.requireNonNull(
                    transformation,
                    "월운 Transformation은 null일 수 없습니다."
            );

            validateNatalPositions(
                    natalAnalysis,
                    transformation.natalPositions()
            );
        }
    }

    /**
     * =================================================
     * 시간 미상 방어
     * =================================================
     *
     * 프로젝트 정책상 불완전 원국은
     * 시주가 존재하지 않는 상태다.
     *
     * 따라서 completeChart=false에서
     * HOUR Evidence를 허용하지 않는다.
     *
     * YEAR/MONTH/DAY는 항상 존재한다.
     */
    private static void validateNatalPositions(

            PalaceAnalysis natalAnalysis,

            java.util.Set<PalacePosition> positions

    ) {

        for (
                PalacePosition position
                : positions
        ) {

            Objects.requireNonNull(
                    position,
                    "PalacePosition은 null일 수 없습니다."
            );

            if (
                    position == PalacePosition.HOUR
                            && !natalAnalysis.completeChart()
            ) {

                throw new IllegalArgumentException(
                        "출생시간 미상 원국에는 HOUR 월운 관계를 생성할 수 없습니다."
                );
            }
        }
    }

    /*
     * =================================================
     * 기본
     * =================================================
     */

    public int sajuYear() {

        return period
                .sajuYear();
    }

    public int monthIndex() {

        return period
                .monthIndex();
    }

    public int monthOrder() {

        return period
                .monthOrder();
    }

    public SexagenaryPillar monthlyPillar() {

        return period
                .pillar();
    }

    public AnnualLuckPeriod annualPeriod() {

        return period
                .annualPeriod();
    }

    /*
     * =================================================
     * 천간 관계
     * =================================================
     */

    public List<MonthlyLuckNatalRelationReference>
    stemRelations() {

        return relations
                .stream()
                .filter(
                        relation ->
                                relation.layer()
                                        == MonthlyLuckNatalRelationLayer
                                        .HEAVENLY_STEM
                )
                .toList();
    }

    /*
     * =================================================
     * 지지 관계
     * =================================================
     */

    public List<MonthlyLuckNatalRelationReference>
    branchRelations() {

        return relations
                .stream()
                .filter(
                        relation ->
                                relation.layer()
                                        == MonthlyLuckNatalRelationLayer
                                        .EARTHLY_BRANCH
                )
                .toList();
    }

    /*
     * =================================================
     * Palace별
     * =================================================
     */

    public List<MonthlyLuckNatalRelationReference>
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

    public List<MonthlyLuckNatalTransformationReference>
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
     * 존재 여부
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
                        MonthlyLuckNatalTransformationReference
                                ::activated
                );
    }

    public boolean hasReducedOriginalFunction() {

        return transformations
                .stream()
                .anyMatch(
                        MonthlyLuckNatalTransformationReference
                                ::originalFunctionReduced
                );
    }

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