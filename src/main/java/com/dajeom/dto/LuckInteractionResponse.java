package com.dajeom.dto;

import com.dajeom.model.LuckInteraction;

public record LuckInteractionResponse(
        String source,
        String sourceKorean,
        String relationType,
        String relationTypeKorean,
        String relationTypeChinese,
        String firstLabel,
        String firstValue,
        String secondLabel,
        String secondValue,
        double scoreChange,
        boolean favorable,
        String description
) {

    public static LuckInteractionResponse from(
            LuckInteraction interaction
    ) {
        return new LuckInteractionResponse(
                interaction.source().name(),

                interaction
                        .source()
                        .getKoreanName(),

                interaction
                        .relationType()
                        .name(),

                interaction
                        .relationType()
                        .getKoreanName(),

                interaction
                        .relationType()
                        .getChineseName(),

                interaction.firstLabel(),

                interaction.firstValue(),

                interaction.secondLabel(),

                interaction.secondValue(),

                interaction.scoreChange(),

                interaction.favorable(),

                interaction.description()
        );
    }
}