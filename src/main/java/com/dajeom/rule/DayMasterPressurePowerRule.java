package com.dajeom.rule;

import org.springframework.stereotype.Component;

/**
 * 정밀화 4차-6.
 *
 * 식상·재성·관살이 일간에 만드는
 * 소모/극제 압력을 계산하는 규칙.
 *
 * 이 값들은 자연과학적 수치나
 * 모든 명리 유파에 공통되는 절대 공식이 아니다.
 *
 * 다점 엔진 내부에서:
 *
 * - 식상 설기
 * - 재성 제어 소모
 * - 관살 극제
 *
 * 를 서로 다른 작용으로 비교하기 위한
 * 상대적인 분석 정책이다.
 *
 * 최종 신강/신약은
 * 이 Rule에서 판정하지 않는다.
 */
@Component
public class DayMasterPressurePowerRule {

    /**
     * 식상.
     *
     * 일간이 식상을 생하면서
     * 자신의 기운을 밖으로 내보내는 설기.
     *
     * 관계 capacity의 80%를
     * 신강·신약 분석용 설기 압력으로 사용한다.
     */
    private static final int
            OUTPUT_DRAIN_RATE =
            80;

    /**
     * 재성.
     *
     * 일간이 재성을 극하고 관리하는 과정에서
     * 힘을 사용하는 구조.
     *
     * 식상처럼 직접 생해서 내보내는 것보다는
     * 소모율을 조금 낮게 둔다.
     */
    private static final int
            WEALTH_EXERTION_RATE =
            70;

    /**
     * 관살.
     *
     * 외부 오행이 일간을 직접 극하는 구조.
     *
     * 세 종류 중 가장 직접적인 압력으로
     * 관계 capacity를 그대로 반영한다.
     */
    private static final int
            OFFICER_CONTROL_RATE =
            100;

    /**
     * 식상 관계 capacity.
     *
     * 일간 세력이 아무리 커도
     * 식상 세력 이상으로 설기 관계가
     * 형성될 수 없고,
     *
     * 식상이 아무리 커도
     * 현재 일간 오행 세력 이상을
     * 한 번에 설기시키는 것으로 계산하지 않는다.
     */
    public int calculateOutputDrainCapacity(

            int dayMasterElementFinalPower,

            int outputFinalPower

    ) {

        return calculateSharedCapacity(
                dayMasterElementFinalPower,
                outputFinalPower
        );
    }

    /**
     * 재성 관계 capacity.
     *
     * 일간이 재성을 극하는 관계이므로
     * 일간과 재성 양쪽의 현재 세력을
     * 함께 제한 조건으로 사용한다.
     */
    public int calculateWealthExertionCapacity(

            int dayMasterElementFinalPower,

            int wealthFinalPower

    ) {

        return calculateSharedCapacity(
                dayMasterElementFinalPower,
                wealthFinalPower
        );
    }

    /**
     * 관살 관계 capacity.
     *
     * 관살이 일간을 극하므로:
     *
     * 관살 세력
     * VS
     * 일간 오행 세력
     *
     * 중 더 작은 범위까지만
     * 실제 극제 관계가 형성된다고 본다.
     */
    public int calculateOfficerControlCapacity(

            int officerFinalPower,

            int dayMasterElementFinalPower

    ) {

        return calculateSharedCapacity(
                officerFinalPower,
                dayMasterElementFinalPower
        );
    }

    public int calculateOutputDrainPower(
            int capacity
    ) {

        return applyRate(
                capacity,
                OUTPUT_DRAIN_RATE
        );
    }

    public int calculateWealthExertionPower(
            int capacity
    ) {

        return applyRate(
                capacity,
                WEALTH_EXERTION_RATE
        );
    }

    public int calculateOfficerControlPower(
            int capacity
    ) {

        return applyRate(
                capacity,
                OFFICER_CONTROL_RATE
        );
    }

    public int outputDrainRate() {

        return OUTPUT_DRAIN_RATE;
    }

    public int wealthExertionRate() {

        return WEALTH_EXERTION_RATE;
    }

    public int officerControlRate() {

        return OFFICER_CONTROL_RATE;
    }

    /**
     * 두 오행의 실제 관계 가능 범위.
     *
     * 최종 세력 자체를 더하거나 빼는 값이 아니라
     * 신강·신약 비교용 관계 capacity다.
     */
    private int calculateSharedCapacity(

            int firstPower,

            int secondPower

    ) {

        if (
                firstPower <= 0
                        || secondPower <= 0
        ) {

            return 0;
        }

        return Math.min(
                firstPower,
                secondPower
        );
    }

    private int applyRate(

            int value,

            int rate

    ) {

        if (
                value <= 0
                        || rate <= 0
        ) {

            return 0;
        }

        return Math.max(
                0,
                (int) Math.round(
                        value
                                * (
                                rate / 100.0
                        )
                )
        );
    }
}