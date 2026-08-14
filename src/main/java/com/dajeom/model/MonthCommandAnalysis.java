package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.MonthCommandRelationType;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 월령(月令) 분석 결과.
 *
 * 월지를 단순히 하나의 오행으로만 보지 않고
 * 월지 내부 지장간까지 포함해서 관리한다.
 *
 * 예:
 *
 * 申월
 *
 * 庚 정기
 * 壬 중기
 * 戊 여기
 *
 * 월령 중심 기운 = 庚金
 *
 * 이후:
 *
 * - 통근
 * - 투간
 * - 오행 세력
 * - 신강/신약
 * - 격국
 * - 용신
 *
 * 분석의 근거로 사용한다.
 */
public record MonthCommandAnalysis(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        EarthlyBranch monthBranch,

        HeavenlyStem mainHiddenStem,

        FiveElement monthCommandElement,

        MonthCommandRelationType relationType,

        boolean seasonallySupported,

        boolean sameElementSupport,

        boolean resourceSupport,

        boolean draining,

        boolean controlled,

        int mainHiddenStemWeight,

        int hiddenStemCount,

        List<HiddenStem> hiddenStems,

        Map<FiveElement, Integer> hiddenStemElementWeights

) {

    public MonthCommandAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterElement,
                "일간 오행이 필요합니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지가 필요합니다."
        );

        Objects.requireNonNull(
                mainHiddenStem,
                "월지 정기가 필요합니다."
        );

        Objects.requireNonNull(
                monthCommandElement,
                "월령 오행이 필요합니다."
        );

        Objects.requireNonNull(
                relationType,
                "월령과 일간의 관계가 필요합니다."
        );

        hiddenStems =
                hiddenStems == null
                        ? List.of()
                        : List.copyOf(
                        hiddenStems
                );

        if (
                mainHiddenStemWeight <= 0
        ) {

            throw new IllegalArgumentException(
                    "월지 정기 가중치는 0보다 커야 합니다."
            );
        }

        if (
                hiddenStemCount
                        != hiddenStems.size()
        ) {

            throw new IllegalArgumentException(
                    "월지 지장간 개수와 실제 지장간 개수가 일치하지 않습니다."
            );
        }

        EnumMap<FiveElement, Integer> weightMap =
                new EnumMap<>(
                        FiveElement.class
                );

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            weightMap.put(
                    element,
                    0
            );
        }

        if (
                hiddenStemElementWeights != null
        ) {

            hiddenStemElementWeights
                    .forEach(
                            (
                                    element,
                                    weight
                            ) -> {

                                if (
                                        element != null
                                                && weight != null
                                ) {

                                    weightMap.put(
                                            element,
                                            Math.max(
                                                    weight,
                                                    0
                                            )
                                    );
                                }
                            }
                    );
        }

        hiddenStemElementWeights =
                Collections.unmodifiableMap(
                        weightMap
                );
    }

    /**
     * 동기 또는 인성 방향의 계절 지원.
     */
    public boolean gainsSeasonalSupport() {

        return seasonallySupported;
    }

    /**
     * 같은 오행 자체가 월령을 잡은 경우.
     */
    public boolean directSeasonalSupport() {

        return sameElementSupport;
    }

    /**
     * 월령이 일간을 생하는 경우.
     */
    public boolean generatedBySeason() {

        return resourceSupport;
    }

    /**
     * 월령 때문에 일간이 기운을
     * 밖으로 사용하는 구조인지.
     */
    public boolean losesEnergyToSeason() {

        return draining;
    }

    /**
     * 월령에게 직접 극을 받는 구조인지.
     */
    public boolean pressuredBySeason() {

        return controlled;
    }

    /**
     * 특정 오행이 월지 지장간 안에
     * 어느 정도 존재하는지 조회한다.
     *
     * 이 값은 상대 가중치이며
     * 확률이 아니다.
     */
    public int hiddenWeightOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return 0;
        }

        return hiddenStemElementWeights
                .getOrDefault(
                        element,
                        0
                );
    }
}