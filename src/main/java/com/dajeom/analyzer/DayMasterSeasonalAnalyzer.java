package com.dajeom.analyzer;

import com.dajeom.model.DayMasterSeasonalAnalysis;
import com.dajeom.model.ElementStrengthAnalysis;
import com.dajeom.model.MonthCommandAnalysis;
import com.dajeom.model.SeasonalElementPowerAnalysis;
import com.dajeom.type.DayMasterSeasonalStatus;
import com.dajeom.type.FiveElement;
import com.dajeom.type.MonthCommandRelationType;
import com.dajeom.type.SeasonalPowerState;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 4차-2.
 *
 * 일간의 득령 및 월령 상태를 분석한다.
 *
 * 중요한 설계 원칙:
 *
 * "득령"을 넓게 사용해서
 *
 * 같은 오행
 * +
 * 월령의 생조
 *
 * 를 전부 하나의 boolean으로
 * 뭉개지 않는다.
 *
 * 엄격한 직접 득령:
 *
 * 일간 오행 == 월령 중심 오행
 *
 * 은 별도로 보존한다.
 *
 * 월령이 일간을 생하는 경우는
 * 직접 득령이 아니라
 * RESOURCE_SUPPORT로 구분한다.
 *
 * 그리고:
 *
 * 旺 / 相 / 休 / 囚 / 死
 *
 * 상태 역시 3차 분석값을
 * 그대로 재사용한다.
 */
@Component
public class DayMasterSeasonalAnalyzer {

    public DayMasterSeasonalAnalysis analyze(
            ElementStrengthAnalysis elementStrength
    ) {

        Objects.requireNonNull(
                elementStrength,
                "오행 세력 분석이 필요합니다."
        );

        MonthCommandAnalysis monthCommand =
                elementStrength
                        .basePowerAnalysis()
                        .monthCommandAnalysis();

        SeasonalElementPowerAnalysis seasonal =
                elementStrength
                        .seasonalElementPowerAnalysis();

        FiveElement dayMasterElement =
                elementStrength
                        .dayMasterElement();

        /*
         * 3차에서 이미 계산한
         * 일간 오행의 旺相休囚死 상태.
         */
        SeasonalPowerState seasonalState =
                seasonal.stateOf(
                        dayMasterElement
                );

        int seasonalRate =
                seasonal.seasonalRateOf(
                        dayMasterElement
                );

        /*
         * 월지 지장간 전체 가운데
         * 일간과 같은 오행이 실제로
         * 얼마만큼 들어 있는지.
         *
         * 예:
         *
         * 丁火 일간 / 申월
         *
         * 申:
         * 庚 金
         * 壬 水
         * 戊 土
         *
         * 火 없음
         *
         * -> 0
         */
        int dayMasterElementMonthBranchWeight =
                seasonal
                        .monthBranchPresenceWeightOf(
                                dayMasterElement
                        );

        boolean dayMasterElementPresentInMonthBranch =
                dayMasterElementMonthBranchWeight > 0;

        MonthCommandRelationType relation =
                monthCommand
                        .relationType();

        DayMasterSeasonalStatus status =
                determineStatus(
                        relation
                );

        /*
         * MonthCommandRelationType와
         * 旺相休囚死가 서로 일관적인지 검증.
         *
         * 계산 단계가 잘못 연결되면
         * 조용히 넘어가지 않고 즉시 실패시킨다.
         */
        validateSeasonalConsistency(
                relation,
                seasonalState
        );

        return new DayMasterSeasonalAnalysis(
                elementStrength.dayMaster(),
                dayMasterElement,
                monthCommand.monthBranch(),
                monthCommand.mainHiddenStem(),
                monthCommand.monthCommandElement(),
                relation,
                seasonalState,
                status,
                seasonalRate,
                dayMasterElementMonthBranchWeight,
                dayMasterElementPresentInMonthBranch,
                elementStrength.monthMainQiExposed()
        );
    }

    /**
     * 기존 MonthCommandRelationType을
     * 4차 신강·신약용 의미로 변환한다.
     */
    private DayMasterSeasonalStatus determineStatus(
            MonthCommandRelationType relation
    ) {

        Objects.requireNonNull(
                relation,
                "월령 관계가 필요합니다."
        );

        return switch (
                relation
                ) {

            /*
             * 일간과 월령이 같은 오행.
             */
            case COMPANION ->
                    DayMasterSeasonalStatus
                            .DIRECT_COMMAND_GAIN;

            /*
             * 월령이 일간을 생함.
             */
            case RESOURCE ->
                    DayMasterSeasonalStatus
                            .RESOURCE_SUPPORT;

            /*
             * 일간이 월령을 생함.
             */
            case OUTPUT ->
                    DayMasterSeasonalStatus
                            .OUTPUT_DRAIN;

            /*
             * 일간이 월령을 극함.
             */
            case WEALTH ->
                    DayMasterSeasonalStatus
                            .WEALTH_EXERTION;

            /*
             * 월령이 일간을 극함.
             */
            case OFFICER ->
                    DayMasterSeasonalStatus
                            .OFFICER_CONTROL;
        };
    }

    /**
     * 월령 생극 관계와
     * 旺相休囚死 계산이 동일한 원리에 따라
     * 생성됐는지 검증한다.
     *
     * COMPANION -> 旺
     * RESOURCE  -> 相
     * OUTPUT    -> 休
     * WEALTH    -> 囚
     * OFFICER   -> 死
     */
    private void validateSeasonalConsistency(

            MonthCommandRelationType relation,

            SeasonalPowerState seasonalState

    ) {

        SeasonalPowerState expected =
                switch (
                        relation
                        ) {

                    case COMPANION ->
                            SeasonalPowerState
                                    .PROSPEROUS;

                    case RESOURCE ->
                            SeasonalPowerState
                                    .ASSISTED;

                    case OUTPUT ->
                            SeasonalPowerState
                                    .RESTING;

                    case WEALTH ->
                            SeasonalPowerState
                                    .CONFINED;

                    case OFFICER ->
                            SeasonalPowerState
                                    .DECLINED;
                };

        if (
                expected
                        != seasonalState
        ) {

            throw new IllegalStateException(
                    "월령 관계와 계절 상태가 일치하지 않습니다. "
                            + "relation="
                            + relation
                            + ", seasonalState="
                            + seasonalState
            );
        }
    }
}