package com.dajeom.model;

import com.dajeom.type.StrengthFactorType;

import java.util.Objects;

/**
 * 일간 강약 점수가 변한 개별 근거다.
 */
public record StrengthEvidence(
        String code,
        StrengthFactorType factorType,
        double scoreChange,
        String description
) {

    public StrengthEvidence {
        Objects.requireNonNull(
                code,
                "강약 근거 코드는 필수입니다."
        );

        Objects.requireNonNull(
                factorType,
                "강약 요인 유형은 필수입니다."
        );

        Objects.requireNonNull(
                description,
                "강약 근거 설명은 필수입니다."
        );
    }
}