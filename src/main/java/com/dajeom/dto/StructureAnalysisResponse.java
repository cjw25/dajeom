package com.dajeom.dto;

import com.dajeom.model.StructureAnalysis;
import com.dajeom.model.StructureResult;

import java.util.List;

/**
 * 주요 명리 구조 종합 분석 Response다.
 */
public record StructureAnalysisResponse(
        StructureResultResponse primaryStructure,
        List<StructureResultResponse> matchedStructures,
        List<StructureResultResponse> allStructures,
        int matchedCount,
        String ruleVersion
) {

    public StructureAnalysisResponse {
        matchedStructures =
                List.copyOf(matchedStructures);

        allStructures =
                List.copyOf(allStructures);
    }

    public static StructureAnalysisResponse from(
            StructureAnalysis analysis
    ) {
        StructureResult primary =
                analysis.primaryStructure();

        List<StructureResultResponse>
                matchedStructures =
                analysis.matchedResults()
                        .stream()
                        .map(
                                StructureResultResponse::from
                        )
                        .toList();

        List<StructureResultResponse>
                allStructures =
                analysis.results()
                        .stream()
                        .map(
                                StructureResultResponse::from
                        )
                        .toList();

        return new StructureAnalysisResponse(
                primary == null
                        ? null
                        : StructureResultResponse.from(
                        primary
                ),

                matchedStructures,

                allStructures,

                matchedStructures.size(),

                analysis.ruleVersion()
        );
    }
}