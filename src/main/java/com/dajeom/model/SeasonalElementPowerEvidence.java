package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.SeasonalPowerState;

import java.util.Objects;

/**
 * 하나의 오행이 월령에서
 * 어떤 계절 상태에 놓이는지를 나타낸다.
 *
 * 예:
 *
 * 申월 = 金
 *
 * 金 -> 旺
 * 水 -> 相
 * 土 -> 休
 * 火 -> 囚
 * 木 -> 死
 *
 * monthBranchPresenceWeight는
 * 해당 오행이 월지 지장간 내부에
 * 실제로 얼마나 존재하는지 나타낸다.
 *
 * 이 값은 이미 지장간 세력에 포함되므로
 * 여기서 다시 더하지 않는다.
 *
 * 오직 계절 판단의 근거로 보존한다.
 */
public record SeasonalElementPowerEvidence(

        FiveElement targetElement,

        FiveElement monthCommandElement,

        SeasonalPowerState state,

        int seasonalRate,

        int monthBranchPresenceWeight,

        boolean presentInMonthBranch,

        boolean monthCommandElementMatch

) {

    public SeasonalElementPowerEvidence {

        Objects.requireNonNull(
                targetElement,
                "계절 분석 대상 오행이 필요합니다."
        );

        Objects.requireNonNull(
                monthCommandElement,
                "월령 중심 오행이 필요합니다."
        );

        Objects.requireNonNull(
                state,
                "계절 상태가 필요합니다."
        );

        if (
                seasonalRate <= 0
        ) {

            throw new IllegalArgumentException(
                    "계절 보정률은 0보다 커야 합니다."
            );
        }

        if (
                monthBranchPresenceWeight < 0
                        || monthBranchPresenceWeight > 100
        ) {

            throw new IllegalArgumentException(
                    "월지 내부 오행 세력은 0~100 사이여야 합니다."
            );
        }

        if (
                seasonalRate
                        != state.seasonalRate()
        ) {

            throw new IllegalArgumentException(
                    "계절 상태와 계절 보정률이 일치하지 않습니다."
            );
        }

        if (
                presentInMonthBranch
                        != (
                        monthBranchPresenceWeight > 0
                )
        ) {

            throw new IllegalArgumentException(
                    "월지 오행 존재 여부와 내부 세력이 일치하지 않습니다."
            );
        }

        if (
                monthCommandElementMatch
                        != (
                        targetElement
                                == monthCommandElement
                )
        ) {

            throw new IllegalArgumentException(
                    "월령 중심 오행 여부가 실제 오행과 일치하지 않습니다."
            );
        }
    }

    public boolean prosperous() {

        return state.prosperous();
    }

    public boolean assisted() {

        return state.assisted();
    }

    public boolean resting() {

        return state.resting();
    }

    public boolean confined() {

        return state.confined();
    }

    public boolean declined() {

        return state.declined();
    }

    /**
     * 월령과 같으면서 월지 내부에도
     * 실제 해당 오행이 존재하는지.
     */
    public boolean directMonthCommandSupport() {

        return monthCommandElementMatch
                && presentInMonthBranch;
    }
}