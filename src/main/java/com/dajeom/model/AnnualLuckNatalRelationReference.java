package com.dajeom.model;

import com.dajeom.type.AnnualLuckNatalRelationLayer;
import com.dajeom.type.PalacePosition;

import java.util.Set;

/**
 * 정밀화 14차-4.
 *
 * 세운과 원국 사이에서 발생하는
 * 하나의 관계 Evidence.
 *
 * =================================================
 *
 * 예:
 *
 * 2026 午
 * +
 * 원국 DAY 丑
 *
 * ->
 *
 * EARTHLY_BRANCH
 * natalPositions = [DAY]
 * relationCode = "CHOU_WU_HARM"
 * relationName = "축오해"
 *
 * =================================================
 *
 * natalPositions를 Set으로 둔 이유:
 *
 * 세운 지지 하나가 원국의 여러 지지와 함께
 *
 * - 삼합
 * - 방합
 * - 기타 다중 관계
 *
 * 를 구성할 수 있기 때문이다.
 *
 * =================================================
 *
 * relationCode / relationName은
 * 기존 8차 관계 엔진의 결과를
 * Adapter에서 정규화해서 전달한다.
 */
public record AnnualLuckNatalRelationReference(

        AnnualLuckNatalRelationLayer layer,

        Set<PalacePosition> natalPositions,

        String relationCode,

        String relationName

) {

    public AnnualLuckNatalRelationReference {

        if (
                layer == null
        ) {

            throw new IllegalArgumentException(
                    "세운-원국 관계 Layer가 필요합니다."
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
}