package com.dajeom.model;

import com.dajeom.type.PalacePosition;

import java.util.Set;

/**
 * 정밀화 12차-4.
 *
 * 9차 합화 결과를
 * 궁위 관점으로 연결하기 위한 Boundary Model.
 *
 * =================================================
 *
 * 12차에서는 합화 조건을 다시 계산하지 않는다.
 *
 * 9차가 이미 판단한:
 *
 * - 어떤 위치가 참여했는지
 * - 합화가 실제 활성됐는지
 * - 원래 기능 감소가 있는지
 *
 * 를 받아서 궁위 Context에 연결한다.
 *
 * =================================================
 *
 * transformationCode:
 *
 * 기존 9차 내부 식별값.
 *
 * transformationName:
 *
 * 사용자/개발자 확인용 이름.
 *
 * targetElementName:
 *
 * 합화 목표 오행이 있을 경우의 이름.
 *
 * 정확한 FiveElement enum에
 * 12차를 강결합하지 않기 위해
 * Boundary에서는 문자열로 보존한다.
 */
public record PalaceTransformationReference(

        Set<PalacePosition> positions,

        String transformationCode,

        String transformationName,

        String targetElementName,

        boolean activated,

        boolean originalFunctionReduced

) {

    public PalaceTransformationReference {

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
                    "합화 Context에는 최소 2개 Palace가 필요합니다."
            );
        }

        if (
                transformationCode == null
                        || transformationCode.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "합화 Code가 필요합니다."
            );
        }

        if (
                transformationName == null
                        || transformationName.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "합화 이름이 필요합니다."
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
                && positions.contains(
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