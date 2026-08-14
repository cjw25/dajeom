package com.dajeom.model;

import com.dajeom.type.PalacePosition;
import com.dajeom.type.PalaceRelationLayer;

import java.util.Set;

/**
 * 정밀화 12차-4.
 *
 * 8차 천간·지지 관계 결과를
 * 궁위 관점으로 연결하기 위한 Boundary Model.
 *
 * =================================================
 *
 * 예:
 *
 * YEAR 酉
 * HOUR 辰
 * 六合
 *
 * ->
 *
 * layer = EARTHLY_BRANCH
 * positions = [YEAR, HOUR]
 * relationCode = "LIU_HE"
 * relationName = "육합"
 *
 * =================================================
 *
 * relationCode / relationName은
 * 8차의 원래 관계 정보를 전달한다.
 *
 * 12차에서 합·충·형·파·해를
 * 다시 계산하지 않는다.
 *
 * =================================================
 *
 * 2궁 관계뿐 아니라
 * 삼합·방합처럼 3개 이상 궁위가
 * 참여하는 관계도 허용한다.
 */
public record PalaceRelationReference(

        PalaceRelationLayer layer,

        Set<PalacePosition> positions,

        String relationCode,

        String relationName

) {

    public PalaceRelationReference {

        if (
                layer == null
        ) {

            throw new IllegalArgumentException(
                    "궁위 관계 Layer가 필요합니다."
            );
        }

        positions =
                positions == null
                        ? Set.of()
                        : Set.copyOf(
                        positions
                );

        if (
                positions.size() < 2
        ) {

            throw new IllegalArgumentException(
                    "궁위 관계에는 최소 2개 Palace가 필요합니다."
            );
        }

        if (
                relationCode == null
                        || relationCode.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "궁위 관계 Code가 필요합니다."
            );
        }

        if (
                relationName == null
                        || relationName.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "궁위 관계 이름이 필요합니다."
            );
        }
    }

    public boolean involves(
            PalacePosition position
    ) {

        return position != null
                && positions.contains(
                position
        );
    }

    public boolean connects(

            PalacePosition first,

            PalacePosition second

    ) {

        if (
                first == null
                        || second == null
        ) {

            return false;
        }

        return positions.contains(
                first
        )
                && positions.contains(
                second
        );
    }

    public boolean stemRelation() {

        return layer.heavenlyStem();
    }

    public boolean branchRelation() {

        return layer.earthlyBranch();
    }
}