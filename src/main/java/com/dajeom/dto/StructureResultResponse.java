package com.dajeom.dto;

import com.dajeom.model.StructureResult;

import java.util.List;

/**
 * 주요 명리 구조 하나의 판정 결과 Response다.
 */
public record StructureResultResponse(
        String structureType,
        String structureTypeKorean,
        String structureDescription,
        boolean matched,
        double score,
        String confidenceLevel,
        String confidenceLevelKorean,
        List<String> evidenceCodes,
        List<String> evidenceDescriptions
) {

    public StructureResultResponse {
        evidenceCodes =
                List.copyOf(evidenceCodes);

        evidenceDescriptions =
                List.copyOf(
                        evidenceDescriptions
                );
    }

    public static StructureResultResponse from(
            StructureResult result
    ) {
        return new StructureResultResponse(
                result
                        .structureType()
                        .name(),

                result
                        .structureType()
                        .getKoreanName(),

                result
                        .structureType()
                        .getDescription(),

                result.matched(),

                result.score(),

                result
                        .confidenceLevel()
                        .name(),

                result
                        .confidenceLevel()
                        .getKoreanName(),

                result.evidenceCodes(),

                result.evidenceDescriptions()
        );
    }
}