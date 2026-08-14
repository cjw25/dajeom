package com.dajeom.model;

import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.FiveElement;
import com.dajeom.type.StructureSupportLevel;
import com.dajeom.type.StructureSupportPatternType;

import java.util.Objects;

/**
 * 정밀화 5차-5.
 *
 * 하나의 성격(成格) 패턴에 대한
 * 실제 원국 Evidence.
 *
 * 예:
 *
 * 정재격 후보
 *
 * 식상생재 패턴
 * ->
 * 식상 오행 土
 * ->
 * 土 세력
 * ->
 * 천간/지장간 source
 * ->
 * StructureSupportLevel
 *
 * 이 과정을 보존한다.
 */
public record StructureSupportSignal(

        StructureSupportPatternType patternType,

        DayMasterElementRole supportingRole,

        FiveElement supportingElement,

        int elementFinalPower,

        int elementShareBasisPoints,

        int visibleSourceCount,

        int hiddenSourceCount,

        int exposedHiddenSourceCount,

        StructureSupportLevel level

) {

    public StructureSupportSignal {

        Objects.requireNonNull(
                patternType,
                "성격 패턴이 필요합니다."
        );

        Objects.requireNonNull(
                supportingRole,
                "성격 보조 십성 그룹이 필요합니다."
        );

        Objects.requireNonNull(
                supportingElement,
                "성격 보조 오행이 필요합니다."
        );

        Objects.requireNonNull(
                level,
                "성격 보조 수준이 필요합니다."
        );

        if (
                patternType.supportingRole()
                        != supportingRole
        ) {

            throw new IllegalArgumentException(
                    "성격 패턴과 보조 십성 그룹이 일치하지 않습니다."
            );
        }

        if (
                elementFinalPower < 0
                        || elementShareBasisPoints < 0
                        || elementShareBasisPoints > 10_000
                        || visibleSourceCount < 0
                        || hiddenSourceCount < 0
                        || exposedHiddenSourceCount < 0
        ) {

            throw new IllegalArgumentException(
                    "성격 보조 Evidence 값이 올바르지 않습니다."
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
                level.none()
                        && elementFinalPower > 0
        ) {

            throw new IllegalArgumentException(
                    "보조 오행 세력이 존재하는데 SupportLevel이 NONE일 수 없습니다."
            );
        }

        if (
                !level.none()
                        && elementFinalPower <= 0
        ) {

            throw new IllegalArgumentException(
                    "보조 오행 세력이 없는데 성격 요소가 존재할 수 없습니다."
            );
        }
    }

    public double elementSharePercent() {

        return elementShareBasisPoints
                / 100.0;
    }

    public int totalSourceCount() {

        return visibleSourceCount
                + hiddenSourceCount;
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
     * 천간에 직접 존재하거나
     * 지장간이 실제 투출한 source가 있는가.
     */
    public boolean expressed() {

        return hasVisibleSource()
                || hasExposedHiddenSource();
    }

    public boolean strong() {

        return level.strong();
    }

    public boolean meaningful() {

        return level.meaningful();
    }

    public boolean exists() {

        return level.exists();
    }
}