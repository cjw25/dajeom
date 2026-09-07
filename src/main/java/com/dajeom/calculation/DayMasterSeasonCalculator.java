package com.dajeom.calculation;

import com.dajeom.domain.DayMasterSeasonAnalysis;
import com.dajeom.domain.EarthlyBranch;
import com.dajeom.domain.FiveElement;
import com.dajeom.domain.FiveElementSeasonState;
import com.dajeom.domain.HeavenlyStem;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 일간의 월령 旺相休囚死 계산기.
 *
 * 숫자 점수 없음.
 * 가중치 없음.
 * 신강/신약 판정 없음.
 *
 * 오직:
 *
 * 일간 오행
 * +
 * 월령 오행
 *
 * 의 전통적인 계절 관계를 계산한다.
 */
@Component
public class DayMasterSeasonCalculator {

    public DayMasterSeasonAnalysis analyze(

            HeavenlyStem dayMaster,

            EarthlyBranch monthBranch

    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지는 필수입니다."
        );

        FiveElement dayMasterElement =
                elementOf(
                        dayMaster
                );

        FiveElement monthCommandElement =
                monthCommandElementOf(
                        monthBranch
                );

        FiveElementSeasonState state =
                seasonStateOf(
                        dayMasterElement,
                        monthCommandElement
                );

        return new DayMasterSeasonAnalysis(
                dayMaster,
                dayMasterElement,
                monthBranch,
                monthCommandElement,
                state
        );
    }

    /**
     * 旺相休囚死 판정.
     *
     * target = 일간 오행
     * command = 월령 당령 오행
     */
    public FiveElementSeasonState seasonStateOf(

            FiveElement target,

            FiveElement command

    ) {

        Objects.requireNonNull(
                target,
                "대상 오행은 필수입니다."
        );

        Objects.requireNonNull(
                command,
                "월령 오행은 필수입니다."
        );

        /*
         * 당령과 같은 오행
         *
         * → 旺
         */
        if (target == command) {

            return FiveElementSeasonState.WANG;
        }

        /*
         * 월령이 대상을 생함
         *
         * → 相
         */
        if (
                generates(
                        command,
                        target
                )
        ) {

            return FiveElementSeasonState.XIANG;
        }

        /*
         * 대상이 월령을 생함
         *
         * → 休
         */
        if (
                generates(
                        target,
                        command
                )
        ) {

            return FiveElementSeasonState.XIU;
        }

        /*
         * 대상이 월령을 극함
         *
         * → 囚
         */
        if (
                controls(
                        target,
                        command
                )
        ) {

            return FiveElementSeasonState.QIU;
        }

        /*
         * 월령이 대상을 극함
         *
         * → 死
         */
        if (
                controls(
                        command,
                        target
                )
        ) {

            return FiveElementSeasonState.SI;
        }

        throw new IllegalStateException(
                "월령 계절 관계를 판정할 수 없습니다: "
                        + target
                        + " / "
                        + command
        );
    }

    /**
     * 월지의 계절 당령 오행.
     *
     * 寅卯 → 木
     * 巳午 → 火
     * 申酉 → 金
     * 亥子 → 水
     * 辰未戌丑 → 土
     *
     * 지장간 전체 구조는 기존 MonthCommandAnalysis에서
     * 별도로 보존한다.
     */
    public FiveElement monthCommandElementOf(
            EarthlyBranch branch
    ) {

        Objects.requireNonNull(
                branch,
                "월지는 필수입니다."
        );

        return switch (branch) {

            case YIN, MAO ->
                    FiveElement.WOOD;

            case SI, WU ->
                    FiveElement.FIRE;

            case SHEN, YOU ->
                    FiveElement.METAL;

            case HAI, ZI ->
                    FiveElement.WATER;

            case CHEN, WEI, XU, CHOU ->
                    FiveElement.EARTH;
        };
    }

    private boolean generates(

            FiveElement source,

            FiveElement target

    ) {

        return switch (source) {

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

    private boolean controls(

            FiveElement source,

            FiveElement target

    ) {

        return switch (source) {

            case WOOD ->
                    target
                            == FiveElement.EARTH;

            case EARTH ->
                    target
                            == FiveElement.WATER;

            case WATER ->
                    target
                            == FiveElement.FIRE;

            case FIRE ->
                    target
                            == FiveElement.METAL;

            case METAL ->
                    target
                            == FiveElement.WOOD;
        };
    }

    private FiveElement elementOf(
            HeavenlyStem stem
    ) {

        return switch (stem) {

            case JIA, YI ->
                    FiveElement.WOOD;

            case BING, DING ->
                    FiveElement.FIRE;

            case WU, JI ->
                    FiveElement.EARTH;

            case GENG, XIN ->
                    FiveElement.METAL;

            case REN, GUI ->
                    FiveElement.WATER;
        };
    }
}