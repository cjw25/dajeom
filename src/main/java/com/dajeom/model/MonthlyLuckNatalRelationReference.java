package com.dajeom.model;

import com.dajeom.type.MonthlyLuckNatalRelationLayer;
import com.dajeom.type.PalacePosition;

import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 15차-4.
 *
 * 월운과 원국 사이에 발생한 하나의 관계 Evidence.
 *
 * 예:
 *
 * 월운 寅
 * +
 * 원국 MONTH 申
 *
 * ->
 *
 * EARTHLY_BRANCH
 * natalPositions = [MONTH]
 * relationCode = ...
 * relationName = 인신충
 *
 * =================================================
 *
 * natalPositions를 Set으로 유지하는 이유:
 *
 * 월운 하나가 원국 여러 Palace와 함께
 *
 * - 삼합
 * - 방합
 * - 기타 다중관계
 *
 * 를 형성할 수 있기 때문이다.
 */
public record MonthlyLuckNatalRelationReference(

        MonthlyLuckNatalRelationLayer layer,

        Set<PalacePosition> natalPositions,

        String relationCode,

        String relationName

) {

    public MonthlyLuckNatalRelationReference {

        Objects.requireNonNull(
                layer,
                "월운-원국 관계 Layer가 필요합니다."
        );

        natalPositions =
                natalPositions == null
                        ? Set.of()
                        : Set.copyOf(
                        natalPositions
                );

        if (
                natalPositions.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "월운-원국 관계에는 최소 하나의 원국 Palace가 필요합니다."
            );
        }

        for (
                PalacePosition position
                : natalPositions
        ) {

            Objects.requireNonNull(
                    position,
                    "월운 관계의 PalacePosition은 null일 수 없습니다."
            );
        }

        if (
                relationCode == null
                        || relationCode.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "월운-원국 관계 Code가 필요합니다."
            );
        }

        relationCode =
                relationCode.trim();

        if (
                relationName == null
                        || relationName.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "월운-원국 관계 이름이 필요합니다."
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

    public boolean stemRelation() {

        return layer
                .heavenlyStem();
    }

    public boolean branchRelation() {

        return layer
                .earthlyBranch();
    }

    public boolean singleNatalPosition() {

        return natalPositions.size()
                == 1;
    }

    public boolean multipleNatalPositions() {

        return natalPositions.size()
                > 1;
    }
}