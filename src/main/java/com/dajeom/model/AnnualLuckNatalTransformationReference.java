package com.dajeom.model;

import com.dajeom.type.PalacePosition;

import java.util.Set;

/**
 * 정밀화 14차-4.
 *
 * 세운 유입으로 인해 발생하거나
 * 영향을 받는 합화 Context.
 *
 * =================================================
 *
 * 단순 관계:
 *
 * 甲 + 己
 * -> 천간합 존재
 *
 * =================================================
 *
 * 실제 합화:
 *
 * 甲 + 己
 * -> 기존 9차 조건 검증
 * -> 化土 활성 여부
 *
 * =================================================
 *
 * 둘은 반드시 분리한다.
 *
 * 합이 있다고 해서
 * activated=true로 만들지 않는다.
 */
public record AnnualLuckNatalTransformationReference(

        Set<PalacePosition> natalPositions,

        String transformationCode,

        String transformationName,

        String targetElementName,

        boolean activated,

        boolean originalFunctionReduced

) {

    public AnnualLuckNatalTransformationReference {

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
                    "세운 합화 Context에는 최소 하나의 원국 Palace가 필요합니다."
            );
        }

        if (
                transformationCode == null
                        || transformationCode.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "세운 합화 Code가 필요합니다."
            );
        }

        transformationCode =
                transformationCode.trim();

        if (
                transformationName == null
                        || transformationName.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "세운 합화 이름이 필요합니다."
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

    /**
     * 단순 관계 존재가 아니라
     * 기능적으로 실제 변화가 발생했는지.
     */
    public boolean functionalChange() {

        return activated
                || originalFunctionReduced;
    }
}