package com.dajeom.model;

import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.FiveElement;

import java.util.Objects;

/**
 * 정밀화 4차-5.
 *
 * 비겁 또는 인성 하나의
 * 지원 세력 계산 근거.
 *
 * COMPANION:
 *
 * 일간과 같은 오행이지만
 * 일간 자기 자신을 제외한
 * 외부 세력만 지원력으로 계산한다.
 *
 * RESOURCE:
 *
 * 일간을 생하는 오행으로,
 * 일간 오행과 실제 형성 가능한
 * 생조 capacity를 기준으로 계산한다.
 */
public record DayMasterSupportEvidence(

        DayMasterElementRole role,

        FiveElement element,

        int rawBasePower,

        int externalRawPower,

        int finalElementPower,

        int supportCapacity,

        int supportRate,

        int effectiveSupportPower,

        int visibleSourceCount,

        int hiddenSourceCount,

        int exposedHiddenSourceCount

) {

    public DayMasterSupportEvidence {

        Objects.requireNonNull(
                role,
                "지원 역할이 필요합니다."
        );

        Objects.requireNonNull(
                element,
                "지원 오행이 필요합니다."
        );

        if (
                !role.companion()
                        && !role.resource()
        ) {

            throw new IllegalArgumentException(
                    "지원 Evidence에는 비겁 또는 인성만 사용할 수 있습니다."
            );
        }

        if (
                rawBasePower < 0
                        || externalRawPower < 0
                        || finalElementPower < 0
                        || supportCapacity < 0
                        || effectiveSupportPower < 0
                        || visibleSourceCount < 0
                        || hiddenSourceCount < 0
                        || exposedHiddenSourceCount < 0
        ) {

            throw new IllegalArgumentException(
                    "지원 세력 값은 음수가 될 수 없습니다."
            );
        }

        if (
                externalRawPower
                        > rawBasePower
        ) {

            throw new IllegalArgumentException(
                    "외부 source의 raw 세력이 전체 raw 세력보다 클 수 없습니다."
            );
        }

        if (
                supportRate < 0
                        || supportRate > 100
        ) {

            throw new IllegalArgumentException(
                    "지원 반영률은 0~100 사이여야 합니다."
            );
        }

        if (
                exposedHiddenSourceCount
                        > hiddenSourceCount
        ) {

            throw new IllegalArgumentException(
                    "투출 지장간 수가 전체 지장간 source 수보다 클 수 없습니다."
            );
        }

        if (
                effectiveSupportPower
                        > supportCapacity
        ) {

            throw new IllegalArgumentException(
                    "유효 지원력이 지원 capacity보다 클 수 없습니다."
            );
        }
    }

    public boolean companion() {

        return role.companion();
    }

    public boolean resource() {

        return role.resource();
    }

    public boolean exists() {

        return rawBasePower > 0;
    }

    /**
     * 외부 source가 실제로 존재하는가.
     *
     * 비겁의 경우 일간 자신 외에
     * 다른 火/木 등의 source가 있는지를
     * 확인하는 데 중요하다.
     */
    public boolean hasExternalSource() {

        return externalRawPower > 0;
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

    /**
     * 전체 raw 세력 가운데
     * 일간 자신을 제외한 외부 source 비율.
     *
     * 10000bp = 100%.
     */
    public int externalRawShareBasisPoints() {

        if (
                rawBasePower <= 0
        ) {

            return 0;
        }

        return Math.max(
                0,
                Math.min(
                        10_000,
                        (int) Math.round(
                                externalRawPower
                                        * 10_000.0
                                        / rawBasePower
                        )
                )
        );
    }

    public double externalRawSharePercent() {

        return externalRawShareBasisPoints()
                / 100.0;
    }
}