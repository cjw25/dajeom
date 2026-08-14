package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.SeasonalPowerState;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 원국 전체의 월령 계절 세력 분석.
 *
 * 木火土金水 5개 오행에 대해
 * 모두 하나씩 SeasonalElementPowerEvidence를
 * 생성한다.
 *
 * 이 모델은 아직 최종 오행 세력이 아니다.
 *
 * 계절 보정률을 계산하여
 * 이후 ElementPower 계산에 전달한다.
 */
public record SeasonalElementPowerAnalysis(

        EarthlyBranch monthBranch,

        FiveElement monthCommandElement,

        List<SeasonalElementPowerEvidence> evidences,

        Map<FiveElement, SeasonalPowerState> states,

        Map<FiveElement, Integer> seasonalRates,

        Map<FiveElement, Integer> monthBranchPresenceWeights

) {

    public SeasonalElementPowerAnalysis {

        Objects.requireNonNull(
                monthBranch,
                "월지가 필요합니다."
        );

        Objects.requireNonNull(
                monthCommandElement,
                "월령 중심 오행이 필요합니다."
        );

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
                    "월령 계절 분석은 오행 5개를 모두 포함해야 합니다."
            );
        }

        states =
                immutableStateMap(
                        states
                );

        seasonalRates =
                immutableIntegerMap(
                        seasonalRates
                );

        monthBranchPresenceWeights =
                immutableIntegerMap(
                        monthBranchPresenceWeights
                );
    }

    private static Map<FiveElement, SeasonalPowerState>
    immutableStateMap(
            Map<FiveElement, SeasonalPowerState> source
    ) {

        EnumMap<FiveElement, SeasonalPowerState> result =
                new EnumMap<>(
                        FiveElement.class
                );

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            SeasonalPowerState state =
                    source == null
                            ? null
                            : source.get(
                            element
                    );

            if (
                    state == null
            ) {

                throw new IllegalArgumentException(
                        "오행 계절 상태가 누락되었습니다: "
                                + element
                );
            }

            result.put(
                    element,
                    state
            );
        }

        return Collections.unmodifiableMap(
                result
        );
    }

    private static Map<FiveElement, Integer>
    immutableIntegerMap(
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

    public SeasonalPowerState stateOf(
            FiveElement element
    ) {

        Objects.requireNonNull(
                element,
                "오행이 필요합니다."
        );

        return states.get(
                element
        );
    }

    /**
     * 최종 오행 세력 계산에서 사용할
     * 계절 상대 보정률.
     */
    public int seasonalRateOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 100;
        }

        return seasonalRates
                .getOrDefault(
                        element,
                        100
                );
    }

    public int monthBranchPresenceWeightOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 0;
        }

        return monthBranchPresenceWeights
                .getOrDefault(
                        element,
                        0
                );
    }

    public Optional<SeasonalElementPowerEvidence>
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
                                evidence.targetElement()
                                        == element
                )
                .findFirst();
    }

    /**
     * 계절상 가장 강한 오행.
     *
     * 정상적인 경우 월령 중심 오행이다.
     */
    public Optional<FiveElement>
    strongestSeasonalElement() {

        return evidences
                .stream()
                .max(
                        Comparator.comparingInt(
                                evidence ->
                                        evidence.state()
                                                .rank()
                        )
                )
                .map(
                        SeasonalElementPowerEvidence
                                ::targetElement
                );
    }

    /**
     * 월령 중심 오행 Evidence.
     */
    public Optional<SeasonalElementPowerEvidence>
    monthCommandEvidence() {

        return evidenceOf(
                monthCommandElement
        );
    }
}