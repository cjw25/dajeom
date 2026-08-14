package com.dajeom.analyzer;

import com.dajeom.model.BasePowerAnalysis;
import com.dajeom.model.HiddenElementPowerAnalysis;
import com.dajeom.model.MonthCommandAnalysis;
import com.dajeom.model.SeasonalElementPowerAnalysis;
import com.dajeom.model.SeasonalElementPowerEvidence;
import com.dajeom.type.FiveElement;
import com.dajeom.type.SeasonalPowerState;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 3차-3.
 *
 * 월령을 기준으로
 * 木火土金水의 계절 상태를 계산한다.
 *
 * 기본 원리:
 *
 * 월령과 같은 오행
 * -> 旺
 *
 * 월령이 생하는 오행
 * -> 相
 *
 * 월령을 생하는 오행
 * -> 休
 *
 * 월령을 극하는 오행
 * -> 囚
 *
 * 월령에게 극을 받는 오행
 * -> 死
 *
 * 중요한 점:
 *
 * 월지 지장간 세력은 3-2에서
 * 이미 원시 세력으로 계산됐다.
 *
 * 따라서 이 단계에서는
 * 월지 지장간 값을 다시 더하지 않는다.
 *
 * 월지 내부 구성은 계절 판단의
 * Evidence로만 보존한다.
 */
@Component
public class SeasonalElementPowerAnalyzer {

    private final HiddenElementPowerAnalyzer
            hiddenElementPowerAnalyzer;

    public SeasonalElementPowerAnalyzer(
            HiddenElementPowerAnalyzer hiddenElementPowerAnalyzer
    ) {

        this.hiddenElementPowerAnalyzer =
                Objects.requireNonNull(
                        hiddenElementPowerAnalyzer,
                        "HiddenElementPowerAnalyzer가 필요합니다."
                );
    }

    /**
     * BasePowerAnalysis에서 직접
     * 월령 계절 세력을 계산한다.
     */
    public SeasonalElementPowerAnalysis analyze(
            BasePowerAnalysis basePowerAnalysis
    ) {

        Objects.requireNonNull(
                basePowerAnalysis,
                "기초 세력 분석이 필요합니다."
        );

        HiddenElementPowerAnalysis
                hiddenElementPowerAnalysis =
                hiddenElementPowerAnalyzer.analyze(
                        basePowerAnalysis
                );

        return analyze(
                basePowerAnalysis,
                hiddenElementPowerAnalysis
        );
    }

    /**
     * 이미 계산된 3-2 결과가 있을 경우
     * 중복 계산 없이 사용할 수 있다.
     */
    public SeasonalElementPowerAnalysis analyze(

            BasePowerAnalysis basePowerAnalysis,

            HiddenElementPowerAnalysis
                    hiddenElementPowerAnalysis

    ) {

        Objects.requireNonNull(
                basePowerAnalysis,
                "기초 세력 분석이 필요합니다."
        );

        Objects.requireNonNull(
                hiddenElementPowerAnalysis,
                "지장간 오행 세력 분석이 필요합니다."
        );

        MonthCommandAnalysis monthCommand =
                basePowerAnalysis
                        .monthCommandAnalysis();

        FiveElement monthCommandElement =
                monthCommand
                        .monthCommandElement();

        List<SeasonalElementPowerEvidence> evidences =
                new ArrayList<>();

        Map<FiveElement, SeasonalPowerState> states =
                new EnumMap<>(
                        FiveElement.class
                );

        Map<FiveElement, Integer> seasonalRates =
                new EnumMap<>(
                        FiveElement.class
                );

        Map<FiveElement, Integer>
                monthBranchPresenceWeights =
                new EnumMap<>(
                        FiveElement.class
                );

        for (
                FiveElement targetElement
                : FiveElement.values()
        ) {

            SeasonalPowerState state =
                    determineSeasonalState(
                            monthCommandElement,
                            targetElement
                    );

            int monthBranchPresenceWeight =
                    hiddenElementPowerAnalysis
                            .monthBranchWeightOf(
                                    targetElement
                            );

            boolean presentInMonthBranch =
                    monthBranchPresenceWeight > 0;

            boolean monthCommandElementMatch =
                    targetElement
                            == monthCommandElement;

            SeasonalElementPowerEvidence evidence =
                    new SeasonalElementPowerEvidence(
                            targetElement,
                            monthCommandElement,
                            state,
                            state.seasonalRate(),
                            monthBranchPresenceWeight,
                            presentInMonthBranch,
                            monthCommandElementMatch
                    );

            evidences.add(
                    evidence
            );

            states.put(
                    targetElement,
                    state
            );

            seasonalRates.put(
                    targetElement,
                    state.seasonalRate()
            );

            monthBranchPresenceWeights.put(
                    targetElement,
                    monthBranchPresenceWeight
            );
        }

        return new SeasonalElementPowerAnalysis(
                monthCommand.monthBranch(),
                monthCommandElement,
                evidences,
                states,
                seasonalRates,
                monthBranchPresenceWeights
        );
    }

    /**
     * 旺相休囚死 판정.
     *
     * 월령 중심 오행을 기준으로
     * 일반화한다.
     */
    private SeasonalPowerState determineSeasonalState(

            FiveElement monthCommandElement,

            FiveElement targetElement

    ) {

        /*
         * 같은 오행.
         *
         * 旺
         */
        if (
                monthCommandElement
                        == targetElement
        ) {

            return SeasonalPowerState
                    .PROSPEROUS;
        }

        /*
         * 월령이 대상 오행을 생한다.
         *
         * 相
         *
         * 예:
         *
         * 金월
         * ↓
         * 金生水
         * ↓
         * 水 = 相
         */
        if (
                produces(
                        monthCommandElement,
                        targetElement
                )
        ) {

            return SeasonalPowerState
                    .ASSISTED;
        }

        /*
         * 대상 오행이 월령을 생한다.
         *
         * 休
         *
         * 예:
         *
         * 金월
         * ↓
         * 土生金
         * ↓
         * 土 = 休
         */
        if (
                produces(
                        targetElement,
                        monthCommandElement
                )
        ) {

            return SeasonalPowerState
                    .RESTING;
        }

        /*
         * 대상 오행이 월령을 극한다.
         *
         * 囚
         *
         * 예:
         *
         * 金월
         * ↓
         * 火克金
         * ↓
         * 火 = 囚
         */
        if (
                controls(
                        targetElement,
                        monthCommandElement
                )
        ) {

            return SeasonalPowerState
                    .CONFINED;
        }

        /*
         * 월령이 대상 오행을 극한다.
         *
         * 死
         *
         * 예:
         *
         * 金月
         * ↓
         * 金克木
         * ↓
         * 木 = 死
         */
        if (
                controls(
                        monthCommandElement,
                        targetElement
                )
        ) {

            return SeasonalPowerState
                    .DECLINED;
        }

        throw new IllegalStateException(
                "계절 오행 관계를 판정할 수 없습니다: "
                        + monthCommandElement
                        + " / "
                        + targetElement
        );
    }

    /**
     * 오행 상생.
     *
     * 木生火
     * 火生土
     * 土生金
     * 金生水
     * 水生木
     */
    private boolean produces(

            FiveElement source,

            FiveElement target

    ) {

        return switch (
                source
                ) {

            case WOOD ->
                    target
                            == FiveElement.FIRE;

            case FIRE ->
                    target
                            == FiveElement.EARTH;

            case EARTH ->
                    target
                            == FiveElement.METAL;

            case METAL ->
                    target
                            == FiveElement.WATER;

            case WATER ->
                    target
                            == FiveElement.WOOD;
        };
    }

    /**
     * 오행 상극.
     *
     * 木克土
     * 土克水
     * 水克火
     * 火克金
     * 金克木
     */
    private boolean controls(

            FiveElement source,

            FiveElement target

    ) {

        return switch (
                source
                ) {

            case WOOD ->
                    target
                            == FiveElement.EARTH;

            case FIRE ->
                    target
                            == FiveElement.METAL;

            case EARTH ->
                    target
                            == FiveElement.WATER;

            case METAL ->
                    target
                            == FiveElement.WOOD;

            case WATER ->
                    target
                            == FiveElement.FIRE;
        };
    }
}