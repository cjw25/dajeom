package com.dajeom.model;

import com.dajeom.type.PalacePosition;

import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 15차-4.
 *
 * 월운 유입으로 발생하거나 영향을 받는
 * 합화 / 기능 변화 Evidence.
 *
 * =================================================
 *
 * 단순 관계:
 *
 * 합이 존재한다.
 *
 * =================================================
 *
 * Transformation:
 *
 * 기존 9차 조건에 의해
 * 실제 化가 성립하거나
 * 기존 기능이 감소한다.
 *
 * =================================================
 *
 * 따라서:
 *
 * 합 존재 != 합화 성립
 */
public record MonthlyLuckNatalTransformationReference(

        Set<PalacePosition> natalPositions,

        String transformationCode,

        String transformationName,

        String targetElementName,

        boolean activated,

        boolean originalFunctionReduced

) {

    public MonthlyLuckNatalTransformationReference {

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
                    "월운 합화 Context에는 최소 하나의 원국 Palace가 필요합니다."
            );
        }

        for (
                PalacePosition position
                : natalPositions
        ) {

            Objects.requireNonNull(
                    position,
                    "월운 합화 PalacePosition은 null일 수 없습니다."
            );
        }

        if (
                transformationCode == null
                        || transformationCode.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "월운 합화 Code가 필요합니다."
            );
        }

        transformationCode =
                transformationCode.trim();

        if (
                transformationName == null
                        || transformationName.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "월운 합화 이름이 필요합니다."
            );
        }

        transformationName =
                transformationName.trim();

        targetElementName =
                targetElementName == null
                        ? ""
                        : targetElementName.trim();
    }

    public boolean involves(
            PalacePosition position
    ) {

        return position != null
                && natalPositions.contains(
                position
        );
    }

    public boolean hasTargetElement() {

        return !targetElementName
                .isBlank();
    }

    public boolean functionalChange() {

        return activated
                || originalFunctionReduced;
    }
}