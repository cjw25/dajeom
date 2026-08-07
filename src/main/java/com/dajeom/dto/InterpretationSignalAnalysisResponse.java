package com.dajeom.dto;

import com.dajeom.model.InterpretationSignalAnalysis;

import java.util.List;

public record InterpretationSignalAnalysisResponse(
        List<CategoryInterpretationResponse> categories,
        int categoryCount,
        int totalSignalCount,
        String ruleVersion
) {

    public InterpretationSignalAnalysisResponse {
        categories = List.copyOf(categories);
    }

    public static InterpretationSignalAnalysisResponse from(
            InterpretationSignalAnalysis analysis
    ) {
        List<CategoryInterpretationResponse> categories =
                analysis.categories()
                        .stream()
                        .map(
                                CategoryInterpretationResponse::from
                        )
                        .toList();

        return new InterpretationSignalAnalysisResponse(
                categories,
                categories.size(),
                analysis.totalSignalCount(),
                analysis.ruleVersion()
        );
    }
}