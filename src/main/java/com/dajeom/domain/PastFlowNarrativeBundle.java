package com.dajeom.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 지나온 흐름 AI Prompt 직전의
 * 기간별 NarrativeInstruction 묶음.
 *
 * =================================================
 *
 * 이 Bundle에는:
 *
 * - 명리 raw 계산값
 * - 십성 enum
 * - 합충 enum
 * - PeriodLifeSignalCode
 * - PeriodMyeongriInfluenceCode
 *
 * 를 AI에 직접 전달하기 위한 정보가 없다.
 *
 * AI가 사용할 수 있는 것은
 * NarrativeDirective의 자연어 의미뿐이다.
 */
public record PastFlowNarrativeBundle(

        List<PeriodLifeClaimNarrativeInstruction> instructions

) {

    public PastFlowNarrativeBundle {

        instructions =
                instructions == null
                        ? List.of()
                        : List.copyOf(instructions);

        for (
                PeriodLifeClaimNarrativeInstruction instruction
                : instructions
        ) {

            Objects.requireNonNull(
                    instruction,
                    "PastFlowNarrativeBundle에 null instruction이 포함될 수 없습니다."
            );
        }
    }

    public boolean hasInstructions() {

        return !instructions.isEmpty();
    }

    public List<PeriodLifeClaimNarrativeInstruction>
    orderedInstructions() {

        List<PeriodLifeClaimNarrativeInstruction> ordered =
                new ArrayList<>(
                        instructions
                );

        ordered.sort(

                Comparator
                        .comparing(
                                (PeriodLifeClaimNarrativeInstruction instruction) ->
                                        instruction
                                                .period()
                                                .startInclusive()
                        )
                        .thenComparing(
                                instruction ->
                                        instruction
                                                .period()
                                                .type()
                                                .ordinal()
                        )
                        .thenComparing(
                                instruction ->
                                        instruction
                                                .period()
                                                .key()
                        )
                        .thenComparing(
                                instruction ->
                                        instruction
                                                .claim()
                                                .code()
                                                .ordinal()
                        )
        );

        return List.copyOf(
                ordered
        );
    }

    public List<PeriodLifeClaimNarrativeInstruction>
    instructionsOf(
            LifePeriodType type
    ) {

        Objects.requireNonNull(
                type,
                "LifePeriodType은 필수입니다."
        );

        return orderedInstructions()
                .stream()
                .filter(
                        instruction ->
                                instruction
                                        .periodType()
                                        == type
                )
                .toList();
    }

    public List<PeriodLifeClaimNarrativeInstruction>
    daewoonInstructions() {

        return instructionsOf(
                LifePeriodType.DAEWOON
        );
    }

    public List<PeriodLifeClaimNarrativeInstruction>
    sewoonInstructions() {

        return instructionsOf(
                LifePeriodType.SEWOON
        );
    }

    public List<PeriodLifeClaimNarrativeInstruction>
    wolwoonInstructions() {

        return instructionsOf(
                LifePeriodType.WOLWOON
        );
    }
}