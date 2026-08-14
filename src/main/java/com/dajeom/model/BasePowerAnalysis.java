package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 원국의 기초 세력 분석 데이터.
 *
 * 2차 정밀화에서 계산한:
 *
 * - 지장간
 * - 월령
 * - 통근
 * - 투간
 *
 * 을 하나로 통합한다.
 *
 * 중요:
 *
 * 이 모델 자체가 신강/신약을
 * 최종 판정하지 않는다.
 *
 * 신강/신약은 이후:
 *
 * 3차 오행 세력
 * 4차 신강·신약
 *
 * 단계에서 판단한다.
 */
public record BasePowerAnalysis(

        FourPillars fourPillars,

        HeavenlyStem dayMaster,

        MonthCommandAnalysis monthCommandAnalysis,

        List<PillarHiddenStemAnalysis> pillarHiddenStemAnalyses,

        RootAnalysis dayMasterRootAnalysis,

        Map<HeavenlyStem, RootAnalysis> visibleStemRootAnalyses,

        Map<HeavenlyStem, StemExposureAnalysis> hiddenStemExposureAnalyses,

        Map<HeavenlyStem, StemExposureAnalysis> monthBranchExposureAnalyses,

        boolean dayMasterRooted,

        boolean dayMasterMonthRooted,

        boolean dayMasterMainQiRooted,

        boolean monthMainQiExposed,

        int dayMasterRootWeight,

        int knownPillarCount

) {

    public BasePowerAnalysis {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국이 필요합니다."
        );

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                monthCommandAnalysis,
                "월령 분석이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterRootAnalysis,
                "일간 통근 분석이 필요합니다."
        );

        pillarHiddenStemAnalyses =
                pillarHiddenStemAnalyses == null
                        ? List.of()
                        : List.copyOf(
                        pillarHiddenStemAnalyses
                );

        visibleStemRootAnalyses =
                immutableMap(
                        visibleStemRootAnalyses
                );

        hiddenStemExposureAnalyses =
                immutableMap(
                        hiddenStemExposureAnalyses
                );

        monthBranchExposureAnalyses =
                immutableMap(
                        monthBranchExposureAnalyses
                );

        if (
                pillarHiddenStemAnalyses.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "지지별 지장간 분석이 필요합니다."
            );
        }

        if (
                dayMasterRootWeight < 0
        ) {

            throw new IllegalArgumentException(
                    "일간 통근 세력은 음수가 될 수 없습니다."
            );
        }

        if (
                knownPillarCount < 3
                        || knownPillarCount > 4
        ) {

            throw new IllegalArgumentException(
                    "확정 기둥 수는 3 또는 4여야 합니다."
            );
        }

        if (
                knownPillarCount
                        != fourPillars.knownPillarCount()
        ) {

            throw new IllegalArgumentException(
                    "원국 기둥 수와 통합 분석 기둥 수가 일치하지 않습니다."
            );
        }

        if (
                dayMaster
                        != fourPillars
                        .dayPillar()
                        .stem()
        ) {

            throw new IllegalArgumentException(
                    "일간과 일주의 천간이 일치하지 않습니다."
            );
        }
    }

    private static <K, V> Map<K, V> immutableMap(
            Map<K, V> source
    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return Map.of();
        }

        return Collections.unmodifiableMap(
                new LinkedHashMap<>(
                        source
                )
        );
    }

    /**
     * 출생시간까지 있어서
     * 4주 8자가 모두 확정된 경우.
     */
    public boolean completeChart() {

        return knownPillarCount == 4;
    }

    /**
     * 출생시간 미상으로
     * 3주만 확정된 경우.
     */
    public boolean hourUnknown() {

        return knownPillarCount == 3;
    }

    /**
     * 일간이 월령의 계절 지원을 받는지.
     */
    public boolean dayMasterSeasonallySupported() {

        return monthCommandAnalysis
                .seasonallySupported();
    }

    /**
     * 일간이 계절적으로 설기·소모되는지.
     */
    public boolean dayMasterSeasonallyDrained() {

        return monthCommandAnalysis
                .draining();
    }

    /**
     * 일간이 월령으로부터 직접 극을 받는지.
     */
    public boolean dayMasterSeasonallyControlled() {

        return monthCommandAnalysis
                .controlled();
    }

    /**
     * 월지 정기가 실제 천간으로
     * 투출된 경우.
     *
     * 이후 격국 분석에서 중요한 근거.
     */
    public boolean hasMonthMainQiExposure() {

        return monthMainQiExposed;
    }

    /**
     * 특정 천간의 통근 분석.
     */
    public Optional<RootAnalysis> rootAnalysisOf(
            HeavenlyStem stem
    ) {

        if (
                stem == null
        ) {

            return Optional.empty();
        }

        return Optional.ofNullable(
                visibleStemRootAnalyses.get(
                        stem
                )
        );
    }

    /**
     * 특정 지장간의 투간 분석.
     */
    public Optional<StemExposureAnalysis>
    exposureAnalysisOf(
            HeavenlyStem stem
    ) {

        if (
                stem == null
        ) {

            return Optional.empty();
        }

        return Optional.ofNullable(
                hiddenStemExposureAnalyses.get(
                        stem
                )
        );
    }

    /**
     * 월지 지장간의 특정 천간에 대한
     * 투간 분석.
     */
    public Optional<StemExposureAnalysis>
    monthExposureAnalysisOf(
            HeavenlyStem stem
    ) {

        if (
                stem == null
        ) {

            return Optional.empty();
        }

        return Optional.ofNullable(
                monthBranchExposureAnalyses.get(
                        stem
                )
        );
    }

    /**
     * 특정 위치의 지장간 분석.
     */
    public Optional<PillarHiddenStemAnalysis>
    hiddenStemAnalysisOfMonthBranch() {

        return pillarHiddenStemAnalyses
                .stream()
                .filter(
                        PillarHiddenStemAnalysis
                                ::monthBranch
                )
                .findFirst();
    }

    /**
     * 일간이 월지에 직접 또는 동기로
     * 뿌리를 확보하고 있는지.
     */
    public boolean hasDayMasterMonthRoot() {

        return dayMasterMonthRooted;
    }

    /**
     * 일간 통근이 전혀 없는지.
     */
    public boolean dayMasterRootless() {

        return !dayMasterRooted;
    }
}