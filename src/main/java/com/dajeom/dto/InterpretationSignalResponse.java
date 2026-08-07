package com.dajeom.dto;

import com.dajeom.model.InterpretationSignal;

import java.util.List;

public record InterpretationSignalResponse(
        String signalCode,

        String category,
        String categoryKorean,

        String title,
        String summary,

        String polarity,
        String polarityKorean,

        String strength,
        String strengthKorean,

        double score,

        List<String> evidenceCodes,
        List<String> evidenceDescriptions
) {

    public InterpretationSignalResponse {
        evidenceCodes =
                List.copyOf(evidenceCodes);

        evidenceDescriptions =
                List.copyOf(evidenceDescriptions);
    }

    public static InterpretationSignalResponse from(
            InterpretationSignal signal
    ) {
        return new InterpretationSignalResponse(
                signal.signalCode(),

                signal.category().name(),

                signal
                        .category()
                        .getKoreanName(),

                signal.title(),

                signal.summary(),

                signal.polarity().name(),

                signal
                        .polarity()
                        .getKoreanName(),

                signal.strength().name(),

                signal
                        .strength()
                        .getKoreanName(),

                signal.score(),

                signal.evidenceCodes(),

                signal.evidenceDescriptions()
        );
    }
}