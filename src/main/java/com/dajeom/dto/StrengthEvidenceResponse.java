package com.dajeom.dto;

import com.dajeom.model.StrengthEvidence;

/**
 * 신강·신약 점수에 반영된 개별 근거다.
 */
public record StrengthEvidenceResponse(
        String code,
        String factorType,
        String factorTypeKorean,
        double scoreChange,
        String description
) {

    public static StrengthEvidenceResponse from(
            StrengthEvidence evidence
    ) {
        return new StrengthEvidenceResponse(
                evidence.code(),

                evidence
                        .factorType()
                        .name(),

                evidence
                        .factorType()
                        .getKoreanName(),

                evidence.scoreChange(),

                evidence.description()
        );
    }
}