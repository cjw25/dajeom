package com.dajeom.model;

import com.dajeom.type.DecadeLuckNatalRelationLayer;
import com.dajeom.type.PalacePosition;

import java.util.Set;

/**
 * 정밀화 13차-5.
 *
 * 대운과 원국 사이에서 발생한
 * 하나의 관계 Reference.
 *
 * =================================================
 *
 * 예:
 *
 * 대운 未
 * +
 * 원국 DAY 丑
 *
 * ->
 *
 * EARTHLY_BRANCH
 * natalPositions = [DAY]
 * relationCode = "CHOU_WEI_CLASH"
 * relationName = "축미충"
 *
 * =================================================
 *
 * 다중 궁위도 허용한다.
 *
 * 예:
 *
 * 대운 지지 하나가 들어오면서
 * 원국의 두 지지와 함께 삼합 구조를 완성한다면:
 *
 * natalPositions = [YEAR, MONTH]
 *
 * 형태가 가능하다.
 *
 * =================================================
 *
 * relationCode / relationName의 실제 값은
 * 기존 8차 관계 엔진에서 가져온다.
 *
 * 13차에서 관계 공식을 다시 만들지 않는다.
 */
public record DecadeLuckNatalRelationReference(

        DecadeLuckNatalRelationLayer layer,

        Set<PalacePosition> natalPositions,

        String relationCode,

        String relationName

) {

    public DecadeLuckNatalRelationReference {

        if (
                layer == null
        ) {

            throw new IllegalArgumentException(
                    "대운-원국 관계 Layer가 필요합니다."
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

        if (
                relationCode == null
                        || relationCode.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "대운-원국 관계 Code가 필요합니다."
            );
        }

        if (
                relationName == null
                        || relationName.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "대운-원국 관계 이름이 필요합니다."
            );
        }
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
}