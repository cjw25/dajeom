package com.dajeom.model;

/**
 * 정밀화 14차-5.
 *
 * 대운과 세운 사이에서 발생하는
 * 합화 / 기능 변화 Reference.
 *
 * =================================================
 *
 * 단순 합:
 *
 * relation
 *
 * =================================================
 *
 * 실제 합화:
 *
 * transformation
 *
 * =================================================
 *
 * 반드시 분리한다.
 *
 * 합이 존재한다고 해서
 * 자동으로 activated=true가 되지 않는다.
 */
public record AnnualDecadeLuckTransformationReference(

        String transformationCode,

        String transformationName,

        String targetElementName,

        boolean activated,

        boolean originalFunctionReduced

) {

    public AnnualDecadeLuckTransformationReference {

        if (
                transformationCode == null
                        || transformationCode.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "대운-세운 합화 Code가 필요합니다."
            );
        }

        transformationCode =
                transformationCode.trim();

        if (
                transformationName == null
                        || transformationName.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "대운-세운 합화 이름이 필요합니다."
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