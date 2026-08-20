package com.dajeom.model;

import com.dajeom.type.AnnualDecadeLuckRelationKind;
import com.dajeom.type.AnnualDecadeLuckRelationLayer;

import java.util.Objects;

/**
 * 정밀화 14차-5.
 *
 * 특정 세운과 특정 대운 사이에서
 * 발생하는 하나의 관계 Evidence.
 *
 * =================================================
 *
 * 기존:
 *
 * layer
 * relationCode
 * relationName
 *
 * =================================================
 *
 * 보강:
 *
 * relationKind
 *
 * =================================================
 *
 * 이제 후속 Functional Layer는
 * relationCode 문자열을 파싱하지 않고
 *
 * AnnualDecadeLuckRelationKind
 *
 * 를 공식 구조 Source로 사용한다.
 *
 * =================================================
 *
 * relationCode / relationName은
 * 추적성과 설명 호환성을 위해
 * 그대로 보존한다.
 */
public record AnnualDecadeLuckRelationReference(

        AnnualDecadeLuckRelationLayer layer,

        AnnualDecadeLuckRelationKind relationKind,

        String relationCode,

        String relationName

) {

    public AnnualDecadeLuckRelationReference {

        Objects.requireNonNull(
                layer,
                "대운-세운 관계 Layer가 필요합니다."
        );

        Objects.requireNonNull(
                relationKind,
                "대운-세운 관계 Kind가 필요합니다."
        );

        if (
                !relationKind
                        .allows(
                                layer
                        )
        ) {

            throw new IllegalArgumentException(
                    "대운-세운 관계 Kind와 Layer가 일치하지 않습니다. "
                            + "kind="
                            + relationKind
                            + ", layer="
                            + layer
            );
        }

        if (
                relationCode == null
                        || relationCode.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "대운-세운 관계 Code가 필요합니다."
            );
        }

        relationCode =
                relationCode.trim();

        if (
                relationName == null
                        || relationName.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "대운-세운 관계 이름이 필요합니다."
            );
        }

        relationName =
                relationName.trim();
    }

    public boolean stemRelation() {

        return layer
                .heavenlyStem();
    }

    public boolean branchRelation() {

        return layer
                .earthlyBranch();
    }

    /*
     * =================================================
     * Typed Relation
     * =================================================
     */

    public boolean combinationFamily() {

        return relationKind
                .combinationFamily();
    }

    public boolean restrictiveInteraction() {

        return relationKind
                .restrictiveInteraction();
    }

    public boolean controlRelation() {

        return relationKind
                .control();
    }

    public boolean decadeControlsAnnual() {

        return relationKind
                .decadeControlsAnnual();
    }

    public boolean annualControlsDecade() {

        return relationKind
                .annualControlsDecade();
    }

    public boolean clashRelation() {

        return relationKind
                .clash();
    }

    public boolean punishmentRelation() {

        return relationKind
                .punishment();
    }

    public boolean harmRelation() {

        return relationKind
                .harm();
    }

    public boolean breakRelation() {

        return relationKind
                .breakRelation();
    }
}