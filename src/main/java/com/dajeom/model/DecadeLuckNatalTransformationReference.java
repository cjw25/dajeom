package com.dajeom.model;

import com.dajeom.type.PalacePosition;

import java.util.Set;

/**
 * 정밀화 13차-5.
 *
 * 대운 유입에 의해 발생하거나
 * 영향을 받는 합화 Context.
 *
 * =================================================
 *
 * 중요:
 *
 * 단순히 합이 있다는 이유로
 * activated=true로 만들지 않는다.
 *
 * 실제 합화 성립 여부는
 * 기존 9차 합화 엔진에서 판단한다.
 *
 * =================================================
 *
 * 예:
 *
 * 대운 己
 * +
 * 원국 HOUR 甲
 *
 * 甲己合 자체가 있다고 하더라도
 *
 * ->
 *
 * relation은 8차 관계 Evidence.
 *
 * 실제 化土가 성립하는지는
 *
 * ->
 *
 * 9차 Transformation Evidence.
 *
 * 둘을 구분한다.
 */
public record DecadeLuckNatalTransformationReference(

        Set<PalacePosition> natalPositions,

        String transformationCode,

        String transformationName,

        String targetElementName,

        boolean activated,

        boolean originalFunctionReduced

) {

    public DecadeLuckNatalTransformationReference {

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
                    "대운 합화 Context에는 최소 하나의 원국 Palace가 필요합니다."
            );
        }

        if (
                transformationCode == null
                        || transformationCode.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "대운 합화 Code가 필요합니다."
            );
        }

        if (
                transformationName == null
                        || transformationName.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "대운 합화 이름이 필요합니다."
            );
        }

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