package com.dajeom.model;

/**
 * 정밀화 15차-5.
 *
 * 대운과 월운 사이에서 발생하는
 * 합화 / 기능 변화 Evidence.
 *
 * =================================================
 *
 * relation
 * = 합 또는 기타 관계 존재
 *
 * transformation
 * = 기존 9차 조건에 따른 실제 변화
 *
 * =================================================
 *
 * 합 존재와 합화 성립을 분리한다.
 */
public record MonthlyDecadeLuckTransformationReference(

        String transformationCode,

        String transformationName,

        String targetElementName,

        boolean activated,

        boolean originalFunctionReduced

) {

    public MonthlyDecadeLuckTransformationReference {

        if (
                transformationCode == null
                        || transformationCode.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "대운-월운 합화 Code가 필요합니다."
            );
        }

        transformationCode =
                transformationCode.trim();

        if (
                transformationName == null
                        || transformationName.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "대운-월운 합화 이름이 필요합니다."
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