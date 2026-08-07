package com.dajeom.dto;

import com.dajeom.model.CategoryInterpretation;
import com.dajeom.model.InterpretationSignal;

import java.util.List;

public record CategoryInterpretationResponse(
        String category,
        String categoryKorean,
        String categoryDescription,

        double favorableScore,
        double challengingScore,
        double netScore,

        InterpretationSignalResponse primarySignal,
        List<InterpretationSignalResponse> signals
) {

    public CategoryInterpretationResponse {
        signals = List.copyOf(signals);
    }

    public static CategoryInterpretationResponse from(
            CategoryInterpretation interpretation
    ) {
        InterpretationSignal primary =
                interpretation.primarySignal();

        return new CategoryInterpretationResponse(
                interpretation
                        .category()
                        .name(),

                interpretation
                        .category()
                        .getKoreanName(),

                interpretation
                        .category()
                        .getDescription(),

                interpretation.favorableScore(),

                interpretation.challengingScore(),

                interpretation.netScore(),

                primary == null
                        ? null
                        : InterpretationSignalResponse.from(
                        primary
                ),

                interpretation.signals()
                        .stream()
                        .map(
                                InterpretationSignalResponse::from
                        )
                        .toList()
        );
    }
}