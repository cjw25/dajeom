package com.dajeom.domain;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * AI Prompt Builder 직전의 7개 카테고리별
 * 서술 Instruction 묶음.
 */
public record LifeClaimNarrativeBundle(

        Map<
                LifeInterpretationCategory,
                List<LifeClaimNarrativeInstruction>
                > instructionsByCategory

) {

    public LifeClaimNarrativeBundle {

        Objects.requireNonNull(
                instructionsByCategory,
                "카테고리별 narrative instruction은 필수입니다."
        );

        EnumMap<
                LifeInterpretationCategory,
                List<LifeClaimNarrativeInstruction>
                > normalized =
                new EnumMap<>(
                        LifeInterpretationCategory.class
                );

        for (
                LifeInterpretationCategory category
                : LifeInterpretationCategory.values()
        ) {

            List<LifeClaimNarrativeInstruction> instructions =
                    instructionsByCategory
                            .getOrDefault(
                                    category,
                                    List.of()
                            );

            List<LifeClaimNarrativeInstruction> copied =
                    List.copyOf(
                            instructions
                    );

            for (
                    LifeClaimNarrativeInstruction instruction
                    : copied
            ) {

                Objects.requireNonNull(
                        instruction,
                        "NarrativeInstruction에 null이 포함될 수 없습니다."
                );

                if (
                        instruction.category()
                                != category
                ) {

                    throw new IllegalArgumentException(
                            "카테고리 Map과 NarrativeInstruction의 category가 일치하지 않습니다."
                    );
                }
            }

            normalized.put(
                    category,
                    copied
            );
        }

        instructionsByCategory =
                Map.copyOf(
                        normalized
                );
    }

    public List<LifeClaimNarrativeInstruction> instructionsFor(
            LifeInterpretationCategory category
    ) {

        Objects.requireNonNull(
                category,
                "카테고리는 필수입니다."
        );

        return instructionsByCategory
                .getOrDefault(
                        category,
                        List.of()
                );
    }
}