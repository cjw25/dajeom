package com.dajeom.model;

import com.dajeom.type.DayMasterSeasonalStatus;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.MonthCommandRelationType;
import com.dajeom.type.SeasonalPowerState;

import java.util.Objects;

/**
 * 정밀화 4차-2.
 *
 * 일간의 월령 상태 분석.
 *
 * 이 결과는 단순한:
 *
 * 득령 = true / false
 *
 * 로 끝내지 않는다.
 *
 * 다음 근거를 모두 보존한다.
 *
 * - 일간
 * - 일간 오행
 * - 월지
 * - 월령 정기
 * - 월령 중심 오행
 * - 월령과 일간 관계
 * - 旺相休囚死
 * - 계절 보정률
 * - 월지 내부의 일간 오행 존재량
 * - 월지 정기 투간 여부
 *
 * 신강/신약의 최종 판정은
 * 4-8 StrengthRule에서 한다.
 */
public record DayMasterSeasonalAnalysis(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        EarthlyBranch monthBranch,

        HeavenlyStem monthMainHiddenStem,

        FiveElement monthCommandElement,

        MonthCommandRelationType monthCommandRelation,

        SeasonalPowerState seasonalPowerState,

        DayMasterSeasonalStatus status,

        int seasonalRate,

        int dayMasterElementMonthBranchWeight,

        boolean dayMasterElementPresentInMonthBranch,

        boolean monthMainQiExposed

) {

    public DayMasterSeasonalAnalysis {

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
                monthMainHiddenStem,
                "월지 정기가 필요합니다."
        );

        Objects.requireNonNull(
                monthCommandElement,
                "월령 중심 오행이 필요합니다."
        );

        Objects.requireNonNull(
                monthCommandRelation,
                "월령과 일간의 관계가 필요합니다."
        );

        Objects.requireNonNull(
                seasonalPowerState,
                "일간의 계절 상태가 필요합니다."
        );

        Objects.requireNonNull(
                status,
                "일간 월령 상태가 필요합니다."
        );

        if (
                seasonalRate <= 0
        ) {

            throw new IllegalArgumentException(
                    "계절 보정률은 0보다 커야 합니다."
            );
        }

        if (
                seasonalRate
                        != seasonalPowerState.seasonalRate()
        ) {

            throw new IllegalArgumentException(
                    "계절 상태와 계절 보정률이 일치하지 않습니다."
            );
        }

        if (
                dayMasterElementMonthBranchWeight < 0
                        || dayMasterElementMonthBranchWeight > 100
        ) {

            throw new IllegalArgumentException(
                    "월지 내부 일간 오행 세력은 0~100 사이여야 합니다."
            );
        }

        if (
                dayMasterElementPresentInMonthBranch
                        != (
                        dayMasterElementMonthBranchWeight > 0
                )
        ) {

            throw new IllegalArgumentException(
                    "월지 내부 일간 오행의 존재 여부와 세력이 일치하지 않습니다."
            );
        }
    }

    /**
     * 엄격한 의미의 직접 득령.
     *
     * 일간 오행과 월령 중심 오행이
     * 동일한 경우만 true.
     */
    public boolean directlyGainsCommand() {

        return status.directCommandGain();
    }

    /**
     * 직접 득령 또는 월령 인성 생조.
     *
     * 즉 계절적으로 일간을 돕는 방향.
     */
    public boolean receivesSeasonalSupport() {

        return status.supportive();
    }

    /**
     * 직접 득령은 아니지만
     * 월령으로부터 생조를 받는 상태.
     */
    public boolean receivesResourceSupport() {

        return status.resourceSupport();
    }

    /**
     * 식상 방향으로 설기되는가.
     */
    public boolean drainedByOutput() {

        return status.outputDrain();
    }

    /**
     * 재성 방향으로 월령을 제어하느라
     * 힘을 사용하는가.
     */
    public boolean expendsOnWealth() {

        return status.wealthExertion();
    }

    /**
     * 월령 관살에게 극을 받는가.
     */
    public boolean controlledBySeason() {

        return status.officerControl();
    }

    /**
     * 월지 내부에 일간과 같은 오행이
     * 실제로 포함되어 있는가.
     *
     * 이것은 직접 득령과는 별개의 근거다.
     *
     * 이후 득지/통근 판단에 사용할 수 있다.
     */
    public boolean hasDayMasterElementInsideMonthBranch() {

        return dayMasterElementPresentInMonthBranch;
    }

    /**
     * 월지 정기가 천간으로 실제 투출했는가.
     *
     * 득령 자체의 조건은 아니지만
     * 월령의 실제 표현력을 확인하는
     * 보조 Evidence다.
     */
    public boolean hasExposedMonthMainQi() {

        return monthMainQiExposed;
    }

    /**
     * 旺 여부.
     */
    public boolean prosperous() {

        return seasonalPowerState.prosperous();
    }

    /**
     * 相 여부.
     */
    public boolean assisted() {

        return seasonalPowerState.assisted();
    }

    /**
     * 休 여부.
     */
    public boolean resting() {

        return seasonalPowerState.resting();
    }

    /**
     * 囚 여부.
     */
    public boolean confined() {

        return seasonalPowerState.confined();
    }

    /**
     * 死 여부.
     */
    public boolean declined() {

        return seasonalPowerState.declined();
    }
}