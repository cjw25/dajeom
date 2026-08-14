package com.dajeom.model;

/**
 * 정밀화 15차-6.
 *
 * 세운과 월운 사이에서 발생하는
 * 합화 / 기능 변화 Evidence.
 *
 * =================================================
 *
 * 관계:
 *
 * 丙 + 辛
 * -> 丙辛合 존재
 *
 * =================================================
 *
 * Transformation:
 *
 * 丙 + 辛
 * +
 * 기존 9차 합화 조건
 *
 * ->
 *
 * 실제 化 성립 여부
 *
 * =================================================
 *
 * 따라서:
 *
 * 합 존재
 * !=
 * 실제 합화 성립
 */
public record MonthlyAnnualLuckTransformationReference(

        String transformationCode,

        String transformationName,

        String targetElementName,

        boolean activated,

        boolean originalFunctionReduced

) {

    public MonthlyAnnualLuckTransformationReference {

        if (
                transformationCode == null
                        || transformationCode.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "세운-월운 Transformation Code가 필요합니다."
            );
        }

        transformationCode =
                transformationCode.trim();

        if (
                transformationName == null
                        || transformationName.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "세운-월운 Transformation 이름이 필요합니다."
            );
        }

        transformationName =
                transformationName.trim();

        targetElementName =
                targetElementName == null
                        ? ""
                        : targetElementName.trim();
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