package com.dajeom.model;

import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.FiveElement;

import java.util.Objects;

/**
 * 정밀화 4차-6.
 *
 * 식상·재성·관살 가운데
 * 하나의 압력 계산 근거.
 *
 * OUTPUT
 * -> 설기
 *
 * WEALTH
 * -> 일간이 극하면서 발생하는 소모
 *
 * OFFICER
 * -> 일간이 직접 극을 받는 압력
 *
 * pressureCapacity:
 * 두 오행이 실제 관계를 형성할 수 있는
 * 상대적인 관계 가능량.
 *
 * effectivePressurePower:
 * 다점 내부 Rule에 따라 실제
 * 신강·신약 비교에 사용할 압력값.
 */
public record DayMasterPressureEvidence(

        DayMasterElementRole role,

        FiveElement element,

        int rawBasePower,

        int finalElementPower,

        int pressureCapacity,

        int pressureRate,

        int effectivePressurePower,

        int visibleSourceCount,

        int hiddenSourceCount,

        int exposedHiddenSourceCount

) {

    public DayMasterPressureEvidence {

        Objects.requireNonNull(
                role,
                "압력 역할이 필요합니다."
        );

        Objects.requireNonNull(
                element,
                "압력 오행이 필요합니다."
        );

        if (
                !role.output()
                        && !role.wealth()
                        && !role.officer()
        ) {

            throw new IllegalArgumentException(
                    "압력 Evidence에는 식상·재성·관살만 사용할 수 있습니다."
            );
        }

        if (
                rawBasePower < 0
                        || finalElementPower < 0
                        || pressureCapacity < 0
                        || effectivePressurePower < 0
                        || visibleSourceCount < 0
                        || hiddenSourceCount < 0
                        || exposedHiddenSourceCount < 0
        ) {

            throw new IllegalArgumentException(
                    "압력 계산값은 음수가 될 수 없습니다."
            );
        }

        if (
                pressureRate < 0
                        || pressureRate > 100
        ) {

            throw new IllegalArgumentException(
                    "압력 반영률은 0~100 사이여야 합니다."
            );
        }

        if (
                exposedHiddenSourceCount
                        > hiddenSourceCount
        ) {

            throw new IllegalArgumentException(
                    "투출 지장간 source 수가 전체 지장간 source 수보다 클 수 없습니다."
            );
        }

        if (
                effectivePressurePower
                        > pressureCapacity
        ) {

            throw new IllegalArgumentException(
                    "유효 압력이 관계 capacity보다 클 수 없습니다."
            );
        }
    }

    public boolean output() {

        return role.output();
    }

    public boolean wealth() {

        return role.wealth();
    }

    public boolean officer() {

        return role.officer();
    }

    /**
     * 식상에 의한 설기 Evidence인가.
     */
    public boolean outputDrain() {

        return output();
    }

    /**
     * 재성을 다루면서 힘을 사용하는 Evidence인가.
     */
    public boolean wealthExertion() {

        return wealth();
    }

    /**
     * 관살에게 직접 극을 받는 Evidence인가.
     */
    public boolean officerControl() {

        return officer();
    }

    public boolean exists() {

        return rawBasePower > 0;
    }

    public boolean active() {

        return effectivePressurePower > 0;
    }

    public boolean hasVisibleSource() {

        return visibleSourceCount > 0;
    }

    public boolean hasHiddenSource() {

        return hiddenSourceCount > 0;
    }

    public boolean hasExposedHiddenSource() {

        return exposedHiddenSourceCount > 0;
    }

    public int totalSourceCount() {

        return visibleSourceCount
                + hiddenSourceCount;
    }
}