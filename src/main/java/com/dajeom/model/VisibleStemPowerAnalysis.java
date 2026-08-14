package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 원국 천간의 표면 세력 분석.
 *
 * 이 값은 아직 최종 오행 세력이 아니다.
 *
 * 현재 포함:
 *
 * - 실제 보이는 천간
 * - 천간 위치
 * - 천간 오행
 * - 일간 식별
 * - 오행별 천간 기본 세력
 *
 * 아직 포함하지 않음:
 *
 * - 지장간
 * - 월령
 * - 통근
 * - 투간 활성도
 * - 합화
 * - 생조
 * - 설기
 * - 극제
 *
 * 위 요소들은 뒤 단계에서 차례대로 합성한다.
 */
public record VisibleStemPowerAnalysis(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        int visibleStemCount,

        int totalBaseWeight,

        List<VisibleStemPower> visibleStems,

        Map<FiveElement, Integer> elementBaseWeights,

        Map<FiveElement, Integer> elementVisibleCounts

) {

    public VisibleStemPowerAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterElement,
                "일간 오행이 필요합니다."
        );

        visibleStems =
                visibleStems == null
                        ? List.of()
                        : List.copyOf(
                        visibleStems
                );

        if (
                visibleStems.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "표면 천간 목록은 비어 있을 수 없습니다."
            );
        }

        if (
                visibleStemCount
                        != visibleStems.size()
        ) {

            throw new IllegalArgumentException(
                    "표면 천간 개수와 실제 목록 크기가 일치하지 않습니다."
            );
        }

        if (
                visibleStemCount < 3
                        || visibleStemCount > 4
        ) {

            throw new IllegalArgumentException(
                    "표면 천간은 출생시간 여부에 따라 3개 또는 4개여야 합니다."
            );
        }

        int calculatedTotalWeight =
                visibleStems
                        .stream()
                        .mapToInt(
                                VisibleStemPower::baseWeight
                        )
                        .sum();

        if (
                calculatedTotalWeight
                        != totalBaseWeight
        ) {

            throw new IllegalArgumentException(
                    "천간 전체 기본 세력이 실제 합계와 일치하지 않습니다."
            );
        }

        boolean validDayMaster =
                visibleStems
                        .stream()
                        .anyMatch(
                                visibleStem ->
                                        visibleStem.dayMaster()
                                                && visibleStem.stem()
                                                == dayMaster
                                                && visibleStem.element()
                                                == dayMasterElement
                        );

        if (
                !validDayMaster
        ) {

            throw new IllegalArgumentException(
                    "표면 천간 목록에서 일간을 확인할 수 없습니다."
            );
        }

        elementBaseWeights =
                immutableElementMap(
                        elementBaseWeights
                );

        elementVisibleCounts =
                immutableElementMap(
                        elementVisibleCounts
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
     * 특정 오행의 천간 기본 세력.
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
     * 특정 오행의 표면 천간 개수.
     */
    public int visibleCountOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 0;
        }

        return elementVisibleCounts
                .getOrDefault(
                        element,
                        0
                );
    }

    /**
     * 특정 오행이 천간에
     * 실제로 드러나 있는지.
     */
    public boolean containsElement(
            FiveElement element
    ) {

        return visibleCountOf(
                element
        ) > 0;
    }

    /**
     * 특정 천간이 원국 표면에
     * 존재하는지.
     */
    public boolean containsStem(
            HeavenlyStem stem
    ) {

        if (
                stem == null
        ) {

            return false;
        }

        return visibleStems
                .stream()
                .anyMatch(
                        visibleStem ->
                                visibleStem.stem()
                                        == stem
                );
    }

    /**
     * 특정 천간이 몇 번 드러났는지.
     *
     * 예:
     *
     * 년간 庚
     * 월간 庚
     *
     * 이면 2.
     */
    public int visibleCountOf(
            HeavenlyStem stem
    ) {

        if (
                stem == null
        ) {

            return 0;
        }

        return (int) visibleStems
                .stream()
                .filter(
                        visibleStem ->
                                visibleStem.stem()
                                        == stem
                )
                .count();
    }

    /**
     * 일간 데이터.
     */
    public Optional<VisibleStemPower>
    dayMasterPower() {

        return visibleStems
                .stream()
                .filter(
                        VisibleStemPower::dayMaster
                )
                .findFirst();
    }

    public boolean completeChart() {

        return visibleStemCount == 4;
    }

    public boolean hourUnknown() {

        return visibleStemCount == 3;
    }
}