package com.dajeom.model;

import com.dajeom.type.AnnualLuckNatalRelationKind;
import com.dajeom.type.AnnualLuckNatalRelationLayer;
import com.dajeom.type.PalacePosition;

import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 14차-4.
 *
 * 세운과 원국 사이에서 발생하는
 * 하나의 관계 Evidence.
 *
 * =================================================
 *
 * 기존:
 *
 * layer
 * natalPositions
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
 * AnnualLuckNatalRelationKind
 *
 * 를 공식 구조 Source로 사용한다.
 *
 * =================================================
 *
 * relationCode / relationName은
 * 기존 추적성과 설명 호환성을 위해
 * 그대로 보존한다.
 */
public record AnnualLuckNatalRelationReference(

        AnnualLuckNatalRelationLayer layer,

        AnnualLuckNatalRelationKind relationKind,

        Set<PalacePosition> natalPositions,

        String relationCode,

        String relationName

) {

    public AnnualLuckNatalRelationReference {

        Objects.requireNonNull(
                layer,
                "세운-원국 관계 Layer가 필요합니다."
        );

        Objects.requireNonNull(
                relationKind,
                "세운-원국 관계 Kind가 필요합니다."
        );

        if (
                !relationKind
                        .allows(
                                layer
                        )
        ) {

            throw new IllegalArgumentException(
                    "관계 Kind와 Layer가 일치하지 않습니다. "
                            + "kind="
                            + relationKind
                            + ", layer="
                            + layer
            );
        }

        natalPositions =
                natalPositions == null
                        ? Set.of()
                        : Set.copyOf(
                        natalPositions
                );

        /*
         * 세운 글자 자체가 하나의 Participant이므로
         * 원국에서는 최소 한 Palace만 있어도 된다.
         */
        if (
                natalPositions.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "세운-원국 관계에는 최소 하나의 원국 Palace가 필요합니다."
            );
        }

        if (
                relationCode == null
                        || relationCode.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "세운-원국 관계 Code가 필요합니다."
            );
        }

        relationCode =
                relationCode.trim();

        if (
                relationName == null
                        || relationName.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "세운-원국 관계 이름이 필요합니다."
            );
        }

        relationName =
                relationName.trim();
    }

    public boolean involves(
            PalacePosition position
    ) {

        return position != null
                && natalPositions.contains(
                position
        );
    }

    public boolean singleNatalPosition() {

        return natalPositions.size()
                == 1;
    }

    public boolean multipleNatalPositions() {

        return natalPositions.size()
                > 1;
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
     * Typed Relation Helpers
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