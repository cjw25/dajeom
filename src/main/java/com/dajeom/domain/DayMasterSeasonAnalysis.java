package com.dajeom.domain;

import java.util.Objects;

/**
 * 일간이 출생 월령에서
 * 旺 / 相 / 休 / 囚 / 死 중
 * 어떤 계절 상태에 있는지 나타낸다.
 *
 * 이것만으로 신강/신약을 결정하지 않는다.
 *
 * 예:
 *
 * 丁 일간
 * 申 월령
 *
 * 申 = 金 당령
 * 丁 = 火
 *
 * 火克金
 *
 * → 丁火는 申월에서 囚
 */
public record DayMasterSeasonAnalysis(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        EarthlyBranch monthBranch,

        FiveElement monthCommandElement,

        FiveElementSeasonState seasonState

) {

    public DayMasterSeasonAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                dayMasterElement,
                "일간 오행은 필수입니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지는 필수입니다."
        );

        Objects.requireNonNull(
                monthCommandElement,
                "월령 오행은 필수입니다."
        );

        Objects.requireNonNull(
                seasonState,
                "월령 계절 상태는 필수입니다."
        );
    }

    public boolean wang() {
        return seasonState
                == FiveElementSeasonState.WANG;
    }

    public boolean xiang() {
        return seasonState
                == FiveElementSeasonState.XIANG;
    }

    public boolean xiu() {
        return seasonState
                == FiveElementSeasonState.XIU;
    }

    public boolean qiu() {
        return seasonState
                == FiveElementSeasonState.QIU;
    }

    public boolean si() {
        return seasonState
                == FiveElementSeasonState.SI;
    }
}