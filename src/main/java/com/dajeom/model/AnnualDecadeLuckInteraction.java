package com.dajeom.model;

import com.dajeom.type.AnnualDecadeLuckRelationLayer;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 14차-5.
 *
 * 특정 세운과
 * 실제로 겹치는 특정 대운의
 * 관계 분석 결과.
 */
public record AnnualDecadeLuckInteraction(

        AnnualLuckPeriod annualPeriod,

        AnnualDecadeLuckOverlap overlap,

        List<AnnualDecadeLuckRelationReference> relations,

        List<AnnualDecadeLuckTransformationReference> transformations

) {

    public AnnualDecadeLuckInteraction {

        Objects.requireNonNull(
                annualPeriod,
                "대운-세운 Interaction에는 세운 Period가 필요합니다."
        );

        Objects.requireNonNull(
                overlap,
                "대운-세운 Interaction에는 시간 Overlap이 필요합니다."
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
                    "대운-세운 관계 Evidence는 null일 수 없습니다."
            );
        }

        for (
                AnnualDecadeLuckTransformationReference transformation
                : transformations
        ) {

            Objects.requireNonNull(
                    transformation,
                    "대운-세운 Transformation은 null일 수 없습니다."
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

    public SexagenaryPillar annualPillar() {

        return annualPeriod
                .pillar();
    }

    public int targetYear() {

        return annualPeriod
                .targetYear();
    }

    public List<AnnualDecadeLuckRelationReference>
    stemRelations() {

        return relations
                .stream()
                .filter(
                        relation ->
                                relation.layer()
                                        == AnnualDecadeLuckRelationLayer
                                        .HEAVENLY_STEM
                )
                .toList();
    }

    public List<AnnualDecadeLuckRelationReference>
    branchRelations() {

        return relations
                .stream()
                .filter(
                        relation ->
                                relation.layer()
                                        == AnnualDecadeLuckRelationLayer
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
                        AnnualDecadeLuckTransformationReference
                                ::activated
                );
    }

    public boolean hasReducedOriginalFunction() {

        return transformations
                .stream()
                .anyMatch(
                        AnnualDecadeLuckTransformationReference
                                ::originalFunctionReduced
                );
    }

    public boolean timingUncertain() {

        return overlap
                .uncertaintyExists();
    }
}