package com.dajeom.model;

import com.dajeom.type.MonthlyDecadeLuckRelationLayer;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 15차-5.
 *
 * 하나의 월운과
 * 실제로 겹치는 하나의 대운 사이의
 * 관계 분석 결과.
 */
public record MonthlyDecadeLuckInteraction(

        MonthlyLuckPeriod monthlyPeriod,

        MonthlyDecadeLuckOverlap overlap,

        List<MonthlyDecadeLuckRelationReference> relations,

        List<MonthlyDecadeLuckTransformationReference> transformations

) {

    public MonthlyDecadeLuckInteraction {

        Objects.requireNonNull(
                monthlyPeriod,
                "대운-월운 Interaction에는 MonthlyLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                overlap,
                "대운-월운 Interaction에는 Overlap이 필요합니다."
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
                    "대운-월운 관계는 null일 수 없습니다."
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
    }

    public DecadeLuckPeriod decadePeriod() {

        return overlap
                .decadePeriod();
    }

    public int decadeSequenceNumber() {

        return decadePeriod()
                .sequenceNumber();
    }

    public SexagenaryPillar decadePillar() {

        return decadePeriod()
                .pillar();
    }

    public SexagenaryPillar monthlyPillar() {

        return monthlyPeriod
                .pillar();
    }

    public int sajuYear() {

        return monthlyPeriod
                .sajuYear();
    }

    public int monthIndex() {

        return monthlyPeriod
                .monthIndex();
    }

    public List<MonthlyDecadeLuckRelationReference>
    stemRelations() {

        return relations
                .stream()
                .filter(
                        relation ->
                                relation.layer()
                                        == MonthlyDecadeLuckRelationLayer
                                        .HEAVENLY_STEM
                )
                .toList();
    }

    public List<MonthlyDecadeLuckRelationReference>
    branchRelations() {

        return relations
                .stream()
                .filter(
                        relation ->
                                relation.layer()
                                        == MonthlyDecadeLuckRelationLayer
                                        .EARTHLY_BRANCH
                )
                .toList();
    }

    public boolean hasRelations() {

        return !relations
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
                        MonthlyDecadeLuckTransformationReference
                                ::activated
                );
    }

    public boolean hasReducedOriginalFunction() {

        return transformations
                .stream()
                .anyMatch(
                        MonthlyDecadeLuckTransformationReference
                                ::originalFunctionReduced
                );
    }

    public boolean timingUncertain() {

        return overlap
                .uncertaintyExists();
    }
}