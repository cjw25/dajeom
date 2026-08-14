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
 * 3차 오행 세력 계산을 위한
 * 통근 활성도 분석 결과.
 *
 * 여기서 저장하는 rootWeight는
 * 최종 오행 세력에 그대로 더하는 값이 아니다.
 *
 * 의미:
 *
 * "표면에 드러난 해당 오행이
 * 지지 내부에서 얼마나 안정적인 뿌리를
 * 확보하고 있는가"
 *
 * 를 나타내는 내부 상대 지수다.
 *
 * 최종 반영 방식은 3-7에서 결정한다.
 */
public record RootElementPowerAnalysis(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        boolean dayMasterRooted,

        boolean dayMasterDirectRooted,

        boolean dayMasterMonthRooted,

        boolean dayMasterMainQiRooted,

        boolean dayMasterStrongMonthMainRoot,

        int dayMasterRootWeight,

        int evidenceCount,

        List<RootElementPowerEvidence> evidences,

        Map<FiveElement, Integer> directRootWeights,

        Map<FiveElement, Integer> supportingRootWeights,

        Map<FiveElement, Integer> totalRootWeights,

        Map<FiveElement, Integer> rootSourceCounts,

        Map<FiveElement, Integer> monthRootSourceCounts,

        Map<FiveElement, Integer> mainQiRootSourceCounts

) {

    public RootElementPowerAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterElement,
                "일간 오행이 필요합니다."
        );

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        if (
                evidenceCount
                        != evidences.size()
        ) {

            throw new IllegalArgumentException(
                    "통근 Evidence 개수와 실제 목록 크기가 일치하지 않습니다."
            );
        }

        if (
                dayMasterRootWeight < 0
        ) {

            throw new IllegalArgumentException(
                    "일간 통근 세력은 음수가 될 수 없습니다."
            );
        }

        directRootWeights =
                immutableElementMap(
                        directRootWeights
                );

        supportingRootWeights =
                immutableElementMap(
                        supportingRootWeights
                );

        totalRootWeights =
                immutableElementMap(
                        totalRootWeights
                );

        rootSourceCounts =
                immutableElementMap(
                        rootSourceCounts
                );

        monthRootSourceCounts =
                immutableElementMap(
                        monthRootSourceCounts
                );

        mainQiRootSourceCounts =
                immutableElementMap(
                        mainQiRootSourceCounts
                );

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            int direct =
                    directRootWeights.get(
                            element
                    );

            int supporting =
                    supportingRootWeights.get(
                            element
                    );

            int total =
                    totalRootWeights.get(
                            element
                    );

            if (
                    direct + supporting
                            != total
            ) {

                throw new IllegalArgumentException(
                        "오행별 통근 세력 합계가 일치하지 않습니다: "
                                + element
                );
            }
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

    public int directRootWeightOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 0;
        }

        return directRootWeights
                .getOrDefault(
                        element,
                        0
                );
    }

    public int supportingRootWeightOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 0;
        }

        return supportingRootWeights
                .getOrDefault(
                        element,
                        0
                );
    }

    /**
     * 해당 오행의 통근 활성 지수.
     *
     * 최종 오행 세력 자체가 아니다.
     */
    public int totalRootWeightOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 0;
        }

        return totalRootWeights
                .getOrDefault(
                        element,
                        0
                );
    }

    public int rootSourceCountOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 0;
        }

        return rootSourceCounts
                .getOrDefault(
                        element,
                        0
                );
    }

    public int monthRootSourceCountOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 0;
        }

        return monthRootSourceCounts
                .getOrDefault(
                        element,
                        0
                );
    }

    public int mainQiRootSourceCountOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 0;
        }

        return mainQiRootSourceCounts
                .getOrDefault(
                        element,
                        0
                );
    }

    public boolean rooted(
            FiveElement element
    ) {

        return totalRootWeightOf(
                element
        ) > 0;
    }

    /**
     * 특정 오행이 월지에
     * 실제 뿌리를 확보했는지.
     */
    public boolean monthRooted(
            FiveElement element
    ) {

        return monthRootSourceCountOf(
                element
        ) > 0;
    }

    /**
     * 특정 오행이 정기에
     * 실제 뿌리를 확보했는지.
     */
    public boolean mainQiRooted(
            FiveElement element
    ) {

        return mainQiRootSourceCountOf(
                element
        ) > 0;
    }

    /**
     * 해당 오행에서 가장 강한 통근 Evidence.
     */
    public Optional<RootElementPowerEvidence>
    strongestEvidenceOf(
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
                                evidence.targetElement()
                                        == element
                )
                .max(
                        (
                                first,
                                second
                        ) ->
                                Integer.compare(
                                        first.effectiveRootWeight(),
                                        second.effectiveRootWeight()
                                )
                );
    }
}