package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 원국 전체 지장간의
 * 오행별 내부 원시 세력 분석.
 *
 * 이 결과는 아직 최종 오행 세력이 아니다.
 *
 * 포함:
 *
 * - 모든 지장간
 * - 정기 / 중기 / 여기
 * - 년지 / 월지 / 일지 / 시지
 * - 오행별 원시 내부 세력
 * - 월지 내부 세력
 *
 * 미포함:
 *
 * - 계절 가중
 * - 통근 보정
 * - 투간 활성
 * - 생극
 * - 합충
 */
public record HiddenElementPowerAnalysis(

        int sourceBranchCount,

        int evidenceCount,

        int totalBaseWeight,

        List<HiddenElementPowerEvidence> evidences,

        Map<FiveElement, Integer> elementBaseWeights,

        Map<FiveElement, Integer> elementEvidenceCounts,

        Map<FiveElement, Integer> monthBranchElementWeights

) {

    public HiddenElementPowerAnalysis {

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        if (
                sourceBranchCount < 3
                        || sourceBranchCount > 4
        ) {

            throw new IllegalArgumentException(
                    "지장간 분석 지지는 3개 또는 4개여야 합니다."
            );
        }

        if (
                evidenceCount
                        != evidences.size()
        ) {

            throw new IllegalArgumentException(
                    "지장간 Evidence 개수와 실제 목록 크기가 일치하지 않습니다."
            );
        }

        int calculatedWeight =
                evidences
                        .stream()
                        .mapToInt(
                                HiddenElementPowerEvidence::baseWeight
                        )
                        .sum();

        if (
                calculatedWeight
                        != totalBaseWeight
        ) {

            throw new IllegalArgumentException(
                    "지장간 전체 세력과 Evidence 합계가 일치하지 않습니다."
            );
        }

        elementBaseWeights =
                immutableElementMap(
                        elementBaseWeights
                );

        elementEvidenceCounts =
                immutableElementMap(
                        elementEvidenceCounts
                );

        monthBranchElementWeights =
                immutableElementMap(
                        monthBranchElementWeights
                );
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
     * 특정 오행의 전체 지장간 원시 세력.
     */
    public int baseWeightOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 0;
        }

        return elementBaseWeights
                .getOrDefault(
                        element,
                        0
                );
    }

    /**
     * 특정 오행이 지장간에
     * 몇 번 등장하는지.
     */
    public int evidenceCountOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 0;
        }

        return elementEvidenceCounts
                .getOrDefault(
                        element,
                        0
                );
    }

    /**
     * 특정 오행의 월지 내부 원시 세력.
     */
    public int monthBranchWeightOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 0;
        }

        return monthBranchElementWeights
                .getOrDefault(
                        element,
                        0
                );
    }

    /**
     * 특정 천간이 지장간 전체에서
     * 확보하고 있는 원시 세력.
     */
    public int baseWeightOf(
            HeavenlyStem stem
    ) {

        if (
                stem == null
        ) {

            return 0;
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.hiddenStem()
                                        == stem
                )
                .mapToInt(
                        HiddenElementPowerEvidence::baseWeight
                )
                .sum();
    }

    /**
     * 특정 오행이 지장간에
     * 존재하는지.
     */
    public boolean containsElement(
            FiveElement element
    ) {

        return baseWeightOf(
                element
        ) > 0;
    }

    /**
     * 특정 천간이 지장간에
     * 존재하는지.
     */
    public boolean containsStem(
            HeavenlyStem stem
    ) {

        return baseWeightOf(
                stem
        ) > 0;
    }

    /**
     * 원국 지장간에서 가장 강한 오행.
     *
     * 아직 계절보정 전이므로
     * 최종적인 가장 강한 오행을 뜻하지 않는다.
     */
    public Optional<FiveElement>
    strongestRawElement() {

        return elementBaseWeights
                .entrySet()
                .stream()
                .filter(
                        entry ->
                                entry.getValue() > 0
                )
                .max(
                        Comparator.comparingInt(
                                Map.Entry::getValue
                        )
                )
                .map(
                        Map.Entry::getKey
                );
    }

    /**
     * 월지 정기 Evidence.
     */
    public Optional<HiddenElementPowerEvidence>
    monthMainQiEvidence() {

        return evidences
                .stream()
                .filter(
                        HiddenElementPowerEvidence
                                ::monthMainQi
                )
                .findFirst();
    }

    public boolean completeChart() {

        return sourceBranchCount == 4;
    }

    public boolean hourUnknown() {

        return sourceBranchCount == 3;
    }
}