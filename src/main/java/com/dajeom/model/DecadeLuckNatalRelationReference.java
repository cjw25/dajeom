package com.dajeom.model;

import com.dajeom.type.DecadeLuckNatalRelationKind;
import com.dajeom.type.DecadeLuckNatalRelationLayer;
import com.dajeom.type.PalacePosition;

import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 13차-5.
 *
 * 대운과 원국 사이에서 발생한
 * 하나의 관계 Reference.
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
 * 이유:
 *
 * 이후 Functional Layer에서:
 *
 * relationCode.contains("CLASH")
 * relationName.equals("충")
 *
 * 같은 문자열 파싱 없이
 * 관계 종류를 구조적으로 사용할 수 있어야 한다.
 *
 * =================================================
 *
 * 중요:
 *
 * relationKind는:
 *
 * - 좋음
 * - 나쁨
 * - 성공
 * - 실패
 *
 * 를 의미하지 않는다.
 *
 * 관계의 구조 종류만 보존한다.
 */
public record DecadeLuckNatalRelationReference(

        DecadeLuckNatalRelationLayer layer,

        DecadeLuckNatalRelationKind relationKind,

        Set<PalacePosition> natalPositions,

        String relationCode,

        String relationName

) {

    public DecadeLuckNatalRelationReference {

        Objects.requireNonNull(
                layer,
                "대운-원국 관계 Layer가 필요합니다."
        );

        Objects.requireNonNull(
                relationKind,
                "대운-원국 관계 Kind가 필요합니다."
        );

        if (
                !relationKind.allows(
                        layer
                )
        ) {

            throw new IllegalArgumentException(
                    "현재 관계 Kind를 해당 Layer에 사용할 수 없습니다. "
                            + "layer="
                            + layer
                            + ", kind="
                            + relationKind
            );
        }

        natalPositions =
                natalPositions == null
                        ? Set.of()
                        : Set.copyOf(
                        natalPositions
                );

        /*
         * 대운 글자 자체가 한 Participant이므로
         * 원국 Palace는 최소 1개면 된다.
         */
        if (
                natalPositions.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "대운 관계에는 최소 하나의 원국 Palace가 필요합니다."
            );
        }

        relationCode =
                requireText(
                        relationCode,
                        "대운-원국 관계 Code가 필요합니다."
                );

        relationName =
                requireText(
                        relationName,
                        "대운-원국 관계 이름이 필요합니다."
                );
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
                == DecadeLuckNatalRelationLayer.HEAVENLY_STEM;
    }

    public boolean branchRelation() {

        return layer
                == DecadeLuckNatalRelationLayer.EARTHLY_BRANCH;
    }

    /*
     * =================================================
     * 관계 Family
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

    public boolean clash() {

        return relationKind
                .clash();
    }

    public boolean control() {

        return relationKind
                .control();
    }

    public boolean punishment() {

        return relationKind
                .punishment();
    }

    public boolean harm() {

        return relationKind
                .harm();
    }

    public boolean breakRelation() {

        return relationKind
                .breakRelation();
    }

    private static String requireText(

            String value,

            String message

    ) {

        Objects.requireNonNull(
                value,
                message
        );

        String normalized =
                value.trim();

        if (
                normalized.isBlank()
        ) {

            throw new IllegalArgumentException(
                    message
            );
        }

        return normalized;
    }
}