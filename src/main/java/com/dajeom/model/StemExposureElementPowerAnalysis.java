package com.dajeom.model;

import com.dajeom.type.FiveElement;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 3차 오행 세력 계산에 사용할
 * 투간 활성도 분석 결과.
 *
 * 핵심 값:
 *
 * totalHiddenBaseWeights
 *
 * 해당 오행이 지장간 전체에
 * 가지고 있는 원시 세력.
 *
 * exposedBaseWeights
 *
 * 그중 실제 같은 천간이
 * 표면으로 드러난 세력.
 *
 * activationRates
 *
 * exposed / totalHidden * 100
 *
 * 예:
 *
 * 金 지장간 전체 = 170
 * 투간된 金 = 60
 *
 * 활성비율 = 약 35%
 *
 * 이 값은 확률이 아니다.
 *
 * 숨은 세력 중 실제 표면과 연결된
 * 비율을 의미한다.
 */
public record StemExposureElementPowerAnalysis(

        int evidenceCount,

        List<StemExposureElementPowerEvidence> evidences,

        Map<FiveElement, Integer> totalHiddenBaseWeights,

        Map<FiveElement, Integer> exposedBaseWeights,

        Map<FiveElement, Integer> weightedActivationIndexes,

        Map<FiveElement, Integer> activationRates,

        Map<FiveElement, Integer> exposedSourceCounts,

        Map<FiveElement, Integer> visibleStemCounts,

        Map<FiveElement, Integer> monthExposedSourceCounts,

        Map<FiveElement, Integer> mainQiExposedSourceCounts,

        Map<FiveElement, Integer> repeatedExposureSourceCounts

) {

    public StemExposureElementPowerAnalysis {

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
                    "투간 활성 Evidence 개수와 실제 목록 크기가 일치하지 않습니다."
            );
        }

        totalHiddenBaseWeights =
                immutableElementMap(
                        totalHiddenBaseWeights
                );

        exposedBaseWeights =
                immutableElementMap(
                        exposedBaseWeights
                );

        weightedActivationIndexes =
                immutableElementMap(
                        weightedActivationIndexes
                );

        activationRates =
                immutableElementMap(
                        activationRates
                );

        exposedSourceCounts =
                immutableElementMap(
                        exposedSourceCounts
                );

        visibleStemCounts =
                immutableElementMap(
                        visibleStemCounts
                );

        monthExposedSourceCounts =
                immutableElementMap(
                        monthExposedSourceCounts
                );

        mainQiExposedSourceCounts =
                immutableElementMap(
                        mainQiExposedSourceCounts
                );

        repeatedExposureSourceCounts =
                immutableElementMap(
                        repeatedExposureSourceCounts
                );

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            int totalHidden =
                    totalHiddenBaseWeights
                            .get(
                                    element
                            );

            int exposed =
                    exposedBaseWeights
                            .get(
                                    element
                            );

            int rate =
                    activationRates
                            .get(
                                    element
                            );

            if (
                    exposed > totalHidden
            ) {

                throw new IllegalArgumentException(
                        "투간 세력이 전체 지장간 세력보다 클 수 없습니다: "
                                + element
                );
            }

            if (
                    rate < 0
                            || rate > 100
            ) {

                throw new IllegalArgumentException(
                        "투간 활성비율은 0~100 사이여야 합니다: "
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

    /**
     * 해당 오행의 전체 지장간 세력.
     */
    public int totalHiddenBaseWeightOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 0;
        }

        return totalHiddenBaseWeights
                .getOrDefault(
                        element,
                        0
                );
    }

    /**
     * 실제 천간으로 투출한
     * 지장간 원시 세력.
     */
    public int exposedBaseWeightOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 0;
        }

        return exposedBaseWeights
                .getOrDefault(
                        element,
                        0
                );
    }

    /**
     * 투간 source 위치까지 반영한
     * 내부 활성 지수.
     *
     * 최종 오행 세력에 그대로
     * 더하는 값은 아니다.
     */
    public int weightedActivationIndexOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 0;
        }

        return weightedActivationIndexes
                .getOrDefault(
                        element,
                        0
                );
    }

    /**
     * 전체 지장간 세력 중
     * 실제 투출된 세력의 비율.
     *
     * 0~100.
     */
    public int activationRateOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 0;
        }

        return activationRates
                .getOrDefault(
                        element,
                        0
                );
    }

    public int exposedSourceCountOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 0;
        }

        return exposedSourceCounts
                .getOrDefault(
                        element,
                        0
                );
    }

    public int visibleStemCountOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 0;
        }

        return visibleStemCounts
                .getOrDefault(
                        element,
                        0
                );
    }

    public int monthExposedSourceCountOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 0;
        }

        return monthExposedSourceCounts
                .getOrDefault(
                        element,
                        0
                );
    }

    public int mainQiExposedSourceCountOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 0;
        }

        return mainQiExposedSourceCounts
                .getOrDefault(
                        element,
                        0
                );
    }

    public int repeatedExposureSourceCountOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 0;
        }

        return repeatedExposureSourceCounts
                .getOrDefault(
                        element,
                        0
                );
    }

    /**
     * 해당 오행에서 투간이 하나라도
     * 성립했는지.
     */
    public boolean exposed(
            FiveElement element
    ) {

        return exposedSourceCountOf(
                element
        ) > 0;
    }

    /**
     * 월지에서 투간된 source가 있는지.
     */
    public boolean exposedFromMonthBranch(
            FiveElement element
    ) {

        return monthExposedSourceCountOf(
                element
        ) > 0;
    }

    /**
     * 해당 오행에서 가장 강한
     * 투간 source.
     */
    public Optional<StemExposureElementPowerEvidence>
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
                                evidence.element()
                                        == element
                )
                .max(
                        Comparator.comparingInt(
                                StemExposureElementPowerEvidence
                                        ::effectiveSourceWeight
                        )
                );
    }

    /**
     * 월지 정기가 실제 투간된 Evidence.
     */
    public Optional<StemExposureElementPowerEvidence>
    monthMainQiEvidence() {

        return evidences
                .stream()
                .filter(
                        StemExposureElementPowerEvidence
                                ::monthMainQiExposure
                )
                .findFirst();
    }
}