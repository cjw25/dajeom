package com.dajeom.dto;

import com.dajeom.model.UsefulGodCandidate;

import java.util.List;

public record UsefulGodCandidateResponse(
        String element,
        String elementKorean,
        String elementChinese,
        String type,
        String typeKorean,
        String typeDescription,
        double score,
        String confidenceLevel,
        String confidenceLevelKorean,
        List<String> methods,
        List<String> methodsKorean,
        List<String> evidenceCodes,
        List<String> evidenceDescriptions
) {

    public UsefulGodCandidateResponse {
        methods = List.copyOf(methods);
        methodsKorean =
                List.copyOf(methodsKorean);
        evidenceCodes =
                List.copyOf(evidenceCodes);
        evidenceDescriptions =
                List.copyOf(evidenceDescriptions);
    }

    public static UsefulGodCandidateResponse from(
            UsefulGodCandidate candidate
    ) {
        return new UsefulGodCandidateResponse(
                candidate.element().name(),

                candidate
                        .element()
                        .getKoreanName(),

                candidate
                        .element()
                        .getChineseCharacter(),

                candidate.type().name(),

                candidate
                        .type()
                        .getKoreanName(),

                candidate
                        .type()
                        .getDescription(),

                candidate.score(),

                candidate
                        .confidenceLevel()
                        .name(),

                candidate
                        .confidenceLevel()
                        .getKoreanName(),

                candidate.methods()
                        .stream()
                        .map(Enum::name)
                        .toList(),

                candidate.methods()
                        .stream()
                        .map(method ->
                                method.getKoreanName()
                        )
                        .toList(),

                candidate.evidenceCodes(),

                candidate.evidenceDescriptions()
        );
    }
}