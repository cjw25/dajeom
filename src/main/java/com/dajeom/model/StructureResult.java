package com.dajeom.model;

import com.dajeom.type.ConfidenceLevel;
import com.dajeom.type.StructureType;

import java.util.List;
import java.util.Objects;

/**
 * 주요 명리 구조 하나의 판정 결과다.
 */
public record StructureResult(
        StructureType structureType,
        boolean matched,
        double score,
        ConfidenceLevel confidenceLevel,
        List<String> evidenceCodes,
        List<String> evidenceDescriptions
) {

    public StructureResult {
        Objects.requireNonNull(
                structureType,
                "명리 구조 유형은 필수입니다."
        );

        Objects.requireNonNull(
                confidenceLevel,
                "신뢰도는 필수입니다."
        );

        Objects.requireNonNull(
                evidenceCodes,
                "판정 근거 코드는 필수입니다."
        );

        Objects.requireNonNull(
                evidenceDescriptions,
                "판정 근거 설명은 필수입니다."
        );

        evidenceCodes =
                List.copyOf(evidenceCodes);

        evidenceDescriptions =
                List.copyOf(evidenceDescriptions);
    }

    public static StructureResult matched(
            StructureType structureType,
            double score,
            List<String> evidenceCodes,
            List<String> evidenceDescriptions
    ) {
        double normalizedScore =
                normalizeScore(score);

        return new StructureResult(
                structureType,
                true,
                normalizedScore,
                ConfidenceLevel.fromScore(
                        normalizedScore
                ),
                evidenceCodes,
                evidenceDescriptions
        );
    }

    public static StructureResult notMatched(
            StructureType structureType,
            double score,
            List<String> evidenceCodes,
            List<String> evidenceDescriptions
    ) {
        double normalizedScore =
                normalizeScore(score);

        return new StructureResult(
                structureType,
                false,
                normalizedScore,
                ConfidenceLevel.fromScore(
                        normalizedScore
                ),
                evidenceCodes,
                evidenceDescriptions
        );
    }

    private static double normalizeScore(
            double score
    ) {
        double clamped =
                Math.max(
                        0.0,
                        Math.min(100.0, score)
                );

        return Math.round(clamped * 100.0)
                / 100.0;
    }
}