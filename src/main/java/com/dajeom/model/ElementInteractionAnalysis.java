package com.dajeom.model;

import com.dajeom.type.ElementInteractionType;
import com.dajeom.type.FiveElement;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 원국의 오행 생극 관계 분석 결과.
 *
 * 오행별로 다음 네 가지를 분리해서 보존한다.
 *
 * 1. generationSupportReceived
 *    생조를 받은 정도
 *
 * 2. generationDrainGiven
 *    다른 오행을 생하며 설기된 정도
 *
 * 3. controlPressureReceived
 *    다른 오행에게 극을 받은 압력
 *
 * 4. controlExertionGiven
 *    다른 오행을 극하면서 힘을 사용한 정도
 *
 * 중요한 점:
 *
 * 이 값들은 최종 세력 점수가 아니라
 * 오행 사이의 상대 작용 지수다.
 *
 * 실제 최종 세력 반영은 3-7에서 한다.
 */
public record ElementInteractionAnalysis(

        Map<FiveElement, Integer> rawBasePowers,

        Map<FiveElement, Integer> preInteractionPowers,

        Map<FiveElement, Integer> generationSupportReceived,

        Map<FiveElement, Integer> generationDrainGiven,

        Map<FiveElement, Integer> controlPressureReceived,

        Map<FiveElement, Integer> controlExertionGiven,

        List<ElementInteractionEvidence> evidences

) {

    public ElementInteractionAnalysis {

        rawBasePowers =
                immutableElementMap(
                        rawBasePowers
                );

        preInteractionPowers =
                immutableElementMap(
                        preInteractionPowers
                );

        generationSupportReceived =
                immutableElementMap(
                        generationSupportReceived
                );

        generationDrainGiven =
                immutableElementMap(
                        generationDrainGiven
                );

        controlPressureReceived =
                immutableElementMap(
                        controlPressureReceived
                );

        controlExertionGiven =
                immutableElementMap(
                        controlExertionGiven
                );

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        /*
         * 5개의 상생 관계
         * +
         * 5개의 상극 관계
         *
         * 총 10개.
         */
        if (
                evidences.size() != 10
        ) {

            throw new IllegalArgumentException(
                    "오행 생극 Evidence는 총 10개여야 합니다."
            );
        }

        long generatingCount =
                evidences
                        .stream()
                        .filter(
                                ElementInteractionEvidence::generating
                        )
                        .count();

        long controllingCount =
                evidences
                        .stream()
                        .filter(
                                ElementInteractionEvidence::controlling
                        )
                        .count();

        if (
                generatingCount != 5
                        || controllingCount != 5
        ) {

            throw new IllegalArgumentException(
                    "상생 5개, 상극 5개의 Evidence가 필요합니다."
            );
        }
    }

    private static Map<FiveElement, Integer>
    immutableElementMap(
            Map<FiveElement, Integer> source
    ) {

        EnumMap<FiveElement, Integer> result =
                new EnumMap<>(
                        FiveElement.class
                );

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            int value =
                    source == null
                            ? 0
                            : source.getOrDefault(
                            element,
                            0
                    );

            result.put(
                    element,
                    Math.max(
                            value,
                            0
                    )
            );
        }

        return Collections.unmodifiableMap(
                result
        );
    }

    /**
     * 3-1 + 3-2 원시 세력.
     */
    public int rawBasePowerOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 0;
        }

        return rawBasePowers
                .getOrDefault(
                        element,
                        0
                );
    }

    /**
     * 계절까지 적용한
     * 생극 직전 세력.
     */
    public int preInteractionPowerOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 0;
        }

        return preInteractionPowers
                .getOrDefault(
                        element,
                        0
                );
    }

    /**
     * 해당 오행이 다른 오행으로부터
     * 생을 받은 작용량.
     */
    public int supportReceivedOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 0;
        }

        return generationSupportReceived
                .getOrDefault(
                        element,
                        0
                );
    }

    /**
     * 해당 오행이 다른 오행을 생하면서
     * 설기한 작용량.
     */
    public int generationDrainOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 0;
        }

        return generationDrainGiven
                .getOrDefault(
                        element,
                        0
                );
    }

    /**
     * 해당 오행이 다른 오행에게
     * 극을 받은 압력.
     */
    public int controlPressureOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 0;
        }

        return controlPressureReceived
                .getOrDefault(
                        element,
                        0
                );
    }

    /**
     * 해당 오행이 다른 오행을
     * 극하기 위해 사용하는 작용량.
     */
    public int controlExertionOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 0;
        }

        return controlExertionGiven
                .getOrDefault(
                        element,
                        0
                );
    }

    /**
     * 특정 source → target 상생 Evidence.
     */
    public Optional<ElementInteractionEvidence>
    findGeneratingEvidence(

            FiveElement source,

            FiveElement target

    ) {

        return findEvidence(
                ElementInteractionType.GENERATING,
                source,
                target
        );
    }

    /**
     * 특정 source → target 상극 Evidence.
     */
    public Optional<ElementInteractionEvidence>
    findControllingEvidence(

            FiveElement source,

            FiveElement target

    ) {

        return findEvidence(
                ElementInteractionType.CONTROLLING,
                source,
                target
        );
    }

    private Optional<ElementInteractionEvidence>
    findEvidence(

            ElementInteractionType interactionType,

            FiveElement source,

            FiveElement target

    ) {

        if (
                interactionType == null
                        || source == null
                        || target == null
        ) {

            return Optional.empty();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.interactionType()
                                        == interactionType
                                        && evidence.sourceElement()
                                        == source
                                        && evidence.targetElement()
                                        == target
                )
                .findFirst();
    }
}