package com.dajeom.model;

import com.dajeom.type.FiveElement;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 木火土金水의 최종 상대 세력 분석.
 *
 * 이것이 3차에서 완성하는
 * 실제 오행 세력 결과다.
 *
 * 단,
 *
 * 이것만으로 신강·신약을
 * 바로 확정하지 않는다.
 *
 * 4차에서는 이 결과와 함께:
 *
 * - 일간
 * - 득령
 * - 통근
 * - 득지
 * - 득세
 * - 인성
 * - 비겁
 * - 식상
 * - 재성
 * - 관살
 *
 * 등을 일간 관점으로 다시 평가한다.
 */
public record ElementPowerAnalysis(

        int totalFinalPower,

        List<ElementPowerEvidence> evidences,

        Map<FiveElement, Integer> finalPowers,

        Map<FiveElement, Integer> shareBasisPoints

) {

    public ElementPowerAnalysis {

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        if (
                evidences.size() != 5
        ) {

            throw new IllegalArgumentException(
                    "오행 최종 세력은 木火土金水 5개를 모두 포함해야 합니다."
            );
        }

        finalPowers =
                immutableElementMap(
                        finalPowers
                );

        shareBasisPoints =
                immutableElementMap(
                        shareBasisPoints
                );

        int calculatedTotal =
                evidences
                        .stream()
                        .mapToInt(
                                ElementPowerEvidence::finalPower
                        )
                        .sum();

        if (
                calculatedTotal
                        != totalFinalPower
        ) {

            throw new IllegalArgumentException(
                    "전체 오행 세력과 세부 합계가 일치하지 않습니다."
            );
        }

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            ElementPowerEvidence evidence =
                    evidences
                            .stream()
                            .filter(
                                    item ->
                                            item.element()
                                                    == element
                            )
                            .findFirst()
                            .orElseThrow(
                                    () ->
                                            new IllegalArgumentException(
                                                    "오행 Evidence가 누락되었습니다: "
                                                            + element
                                            )
                            );

            if (
                    finalPowers.get(
                            element
                    )
                            != evidence.finalPower()
            ) {

                throw new IllegalArgumentException(
                        "오행 최종 세력이 Evidence와 일치하지 않습니다: "
                                + element
                );
            }

            if (
                    shareBasisPoints.get(
                            element
                    )
                            != evidence.shareBasisPoints()
            ) {

                throw new IllegalArgumentException(
                        "오행 점유율이 Evidence와 일치하지 않습니다: "
                                + element
                );
            }
        }

        int shareTotal =
                shareBasisPoints
                        .values()
                        .stream()
                        .mapToInt(
                                Integer::intValue
                        )
                        .sum();

        if (
                totalFinalPower > 0
                        && shareTotal != 10_000
        ) {

            throw new IllegalArgumentException(
                    "오행 점유율 합계는 10000bp여야 합니다."
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

    public int powerOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 0;
        }

        return finalPowers
                .getOrDefault(
                        element,
                        0
                );
    }

    public int shareBasisPointsOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 0;
        }

        return shareBasisPoints
                .getOrDefault(
                        element,
                        0
                );
    }

    public double sharePercentOf(
            FiveElement element
    ) {

        return shareBasisPointsOf(
                element
        )
                / 100.0;
    }

    public Optional<ElementPowerEvidence>
    evidenceOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return Optional.empty();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.element()
                                        == element
                )
                .findFirst();
    }

    /**
     * 최종적으로 가장 강한 오행.
     */
    public Optional<FiveElement>
    strongestElement() {

        return evidences
                .stream()
                .max(
                        Comparator.comparingInt(
                                ElementPowerEvidence
                                        ::finalPower
                        )
                )
                .map(
                        ElementPowerEvidence::element
                );
    }

    /**
     * 원국에 존재하는 오행 중
     * 가장 약한 오행.
     *
     * 원래부터 0인 오행은 제외한다.
     */
    public Optional<FiveElement>
    weakestExistingElement() {

        return evidences
                .stream()
                .filter(
                        ElementPowerEvidence::exists
                )
                .min(
                        Comparator.comparingInt(
                                ElementPowerEvidence
                                        ::finalPower
                        )
                )
                .map(
                        ElementPowerEvidence::element
                );
    }

    /**
     * 세력이 강한 순서.
     */
    public List<ElementPowerEvidence>
    orderedByPowerDescending() {

        return evidences
                .stream()
                .sorted(
                        Comparator
                                .comparingInt(
                                        ElementPowerEvidence
                                                ::finalPower
                                )
                                .reversed()
                )
                .toList();
    }
}