package com.dajeom.model;

import com.dajeom.type.StructureTenGodType;
import com.dajeom.type.TenGodPowerLevel;

import java.util.Objects;

/**
 * 정밀화 10차-1.
 *
 * 하나의 십성에 대한
 * 원국 분포·세력 요약.
 *
 * =================================================
 *
 * occurrenceCount
 *
 * 실제 source 개수.
 *
 * =================================================
 *
 * allocatedPower
 *
 * 3차 오행 최종 세력을 source 단위로
 * 배분한 뒤 해당 십성에 모은 값.
 *
 * =================================================
 *
 * shareBasisPoints
 *
 * 일간 자기 자신을 제외한
 * 전체 십성 power 중 이 십성이
 * 차지하는 상대 비율.
 *
 * 10000bp = 100%
 */
public record TenGodPowerSummary(

        StructureTenGodType tenGodType,

        TenGodPowerLevel powerLevel,

        int occurrenceCount,

        int visibleOccurrenceCount,

        int hiddenOccurrenceCount,

        int totalRawWeight,

        int allocatedPower,

        int visibleAllocatedPower,

        int hiddenAllocatedPower,

        int monthAllocatedPower,

        int monthBranchAllocatedPower,

        int shareBasisPoints

) {

    public TenGodPowerSummary {

        Objects.requireNonNull(
                tenGodType,
                "십성 종류가 필요합니다."
        );

        Objects.requireNonNull(
                powerLevel,
                "십성 세력 수준이 필요합니다."
        );

        if (
                occurrenceCount < 0
                        || visibleOccurrenceCount < 0
                        || hiddenOccurrenceCount < 0
        ) {

            throw new IllegalArgumentException(
                    "십성 출현 횟수는 음수일 수 없습니다."
            );
        }

        if (
                occurrenceCount
                        != visibleOccurrenceCount
                        + hiddenOccurrenceCount
        ) {

            throw new IllegalArgumentException(
                    "십성 전체 출현 횟수와 천간/지장간 출현 횟수 합이 일치하지 않습니다."
            );
        }

        if (
                totalRawWeight < 0
                        || allocatedPower < 0
                        || visibleAllocatedPower < 0
                        || hiddenAllocatedPower < 0
                        || monthAllocatedPower < 0
                        || monthBranchAllocatedPower < 0
        ) {

            throw new IllegalArgumentException(
                    "십성 세력 값은 음수일 수 없습니다."
            );
        }

        if (
                allocatedPower
                        != visibleAllocatedPower
                        + hiddenAllocatedPower
        ) {

            throw new IllegalArgumentException(
                    "십성 전체 배분 세력과 천간/지장간 세력 합이 일치하지 않습니다."
            );
        }

        if (
                monthBranchAllocatedPower
                        > monthAllocatedPower
        ) {

            throw new IllegalArgumentException(
                    "월지 지장간 세력은 전체 월주 source 세력보다 클 수 없습니다."
            );
        }

        if (
                shareBasisPoints < 0
                        || shareBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "십성 점유율은 0~10000bp 범위여야 합니다."
            );
        }

        if (
                occurrenceCount == 0
                        && powerLevel
                        != TenGodPowerLevel.ABSENT
        ) {

            throw new IllegalArgumentException(
                    "출현하지 않은 십성은 ABSENT여야 합니다."
            );
        }

        if (
                occurrenceCount > 0
                        && powerLevel
                        == TenGodPowerLevel.ABSENT
        ) {

            throw new IllegalArgumentException(
                    "출현한 십성을 ABSENT로 처리할 수 없습니다."
            );
        }
    }

    public String koreanName() {

        return tenGodType
                .koreanName();
    }

    public boolean present() {

        return occurrenceCount > 0;
    }

    public boolean visible() {

        return visibleOccurrenceCount > 0;
    }

    public boolean hiddenOnly() {

        return occurrenceCount > 0
                && visibleOccurrenceCount == 0;
    }

    public boolean monthPresent() {

        return monthAllocatedPower > 0;
    }

    public boolean monthBranchPresent() {

        return monthBranchAllocatedPower > 0;
    }

    public boolean dominant() {

        return powerLevel.dominant();
    }

    public boolean strongSide() {

        return powerLevel.strongSide();
    }

    public boolean meaningful() {

        return powerLevel.meaningful();
    }

    public double sharePercent() {

        return shareBasisPoints / 100.0;
    }
}