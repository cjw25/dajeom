package com.dajeom.model;

import com.dajeom.type.MonthlyAnnualLuckRelationLayer;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 15차-6.
 *
 * 하나의 세운과 하나의 월운 사이
 * 관계 분석 결과.
 *
 * =================================================
 *
 * 포함:
 *
 * - 세운 천간 × 월운 천간
 * - 세운 지지 × 월운 지지
 * - 관계 Evidence
 * - Transformation Evidence
 *
 * =================================================
 *
 * 포함하지 않음:
 *
 * - 원국 × 월운
 * - 대운 × 월운
 * - 원국 + 대운 + 세운 + 월운
 * - 용신 Context
 * - 격국 Context
 * - 길흉 점수
 */
public record MonthlyAnnualLuckRelationAnalysis(

        AnnualLuckPeriod annualPeriod,

        MonthlyLuckPeriod monthlyPeriod,

        List<MonthlyAnnualLuckRelationReference> relations,

        List<MonthlyAnnualLuckTransformationReference> transformations

) {

    public MonthlyAnnualLuckRelationAnalysis {

        Objects.requireNonNull(
                annualPeriod,
                "세운-월운 관계 분석에는 AnnualLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                monthlyPeriod,
                "세운-월운 관계 분석에는 MonthlyLuckPeriod가 필요합니다."
        );

        if (
                monthlyPeriod.annualPeriod()
                        != annualPeriod
        ) {

            throw new IllegalArgumentException(
                    "월운이 현재 세운의 AnnualLuckPeriod에 속하지 않습니다."
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
                    "세운-월운 관계 Evidence는 null일 수 없습니다."
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
    }

    /*
     * =================================================
     * 기본
     * =================================================
     */

    public int sajuYear() {

        return annualPeriod
                .targetYear();
    }

    public int monthIndex() {

        return monthlyPeriod
                .monthIndex();
    }

    public int monthOrder() {

        return monthlyPeriod
                .monthOrder();
    }

    public SexagenaryPillar annualPillar() {

        return annualPeriod
                .pillar();
    }

    public SexagenaryPillar monthlyPillar() {

        return monthlyPeriod
                .pillar();
    }

    /*
     * =================================================
     * 천간 관계
     * =================================================
     */

    public List<MonthlyAnnualLuckRelationReference>
    stemRelations() {

        return relations
                .stream()
                .filter(
                        relation ->
                                relation.layer()
                                        == MonthlyAnnualLuckRelationLayer
                                        .HEAVENLY_STEM
                )
                .toList();
    }

    /*
     * =================================================
     * 지지 관계
     * =================================================
     */

    public List<MonthlyAnnualLuckRelationReference>
    branchRelations() {

        return relations
                .stream()
                .filter(
                        relation ->
                                relation.layer()
                                        == MonthlyAnnualLuckRelationLayer
                                        .EARTHLY_BRANCH
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
                        MonthlyAnnualLuckTransformationReference
                                ::activated
                );
    }

    public boolean hasReducedOriginalFunction() {

        return transformations
                .stream()
                .anyMatch(
                        MonthlyAnnualLuckTransformationReference
                                ::originalFunctionReduced
                );
    }
}