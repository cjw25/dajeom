package com.dajeom.model;

import com.dajeom.type.ResourceCompositePatternLevel;
import com.dajeom.type.ResourceCompositePatternType;
import com.dajeom.type.ResourceCompositeReasonType;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 10차-6.
 *
 * 하나의 인성 복합 Pattern에 대한 Evidence.
 */
public record ResourceCompositeSignal(

        ResourceCompositePatternType patternType,

        ResourceCompositePatternLevel level,

        int directResourcePower,

        int indirectResourcePower,

        int resourceFamilyPower,

        int outputFamilyPower,

        int officerFamilyPower,

        int wealthFamilyPower,

        int resourceFamilyShareBasisPoints,

        int directResourceOccurrenceCount,

        int indirectResourceOccurrenceCount,

        boolean directResourceVisible,

        boolean indirectResourceVisible,

        boolean monthDirectResourcePresent,

        boolean monthIndirectResourcePresent,

        List<ResourceCompositeReasonType> reasons

) {

    public ResourceCompositeSignal {

        Objects.requireNonNull(
                patternType,
                "인성 Pattern 종류가 필요합니다."
        );

        Objects.requireNonNull(
                level,
                "인성 Pattern 수준이 필요합니다."
        );

        if (
                directResourcePower < 0
                        || indirectResourcePower < 0
                        || resourceFamilyPower < 0
                        || outputFamilyPower < 0
                        || officerFamilyPower < 0
                        || wealthFamilyPower < 0
        ) {

            throw new IllegalArgumentException(
                    "십성 세력은 음수일 수 없습니다."
            );
        }

        if (
                resourceFamilyPower
                        != directResourcePower
                        + indirectResourcePower
        ) {

            throw new IllegalArgumentException(
                    "인성 전체 세력이 정인+편인 세력과 일치하지 않습니다."
            );
        }

        if (
                resourceFamilyShareBasisPoints < 0
                        || resourceFamilyShareBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "인성 점유율은 0~10000bp 범위여야 합니다."
            );
        }

        if (
                directResourceOccurrenceCount < 0
                        || indirectResourceOccurrenceCount < 0
        ) {

            throw new IllegalArgumentException(
                    "인성 출현 횟수는 음수일 수 없습니다."
            );
        }

        reasons =
                reasons == null
                        ? List.of()
                        : List.copyOf(
                        reasons
                );

        if (
                reasons.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "인성 Pattern에는 하나 이상의 판정 근거가 필요합니다."
            );
        }
    }

    public boolean meaningful() {

        return level
                .effective();
    }

    public boolean strong() {

        return level
                .strong();
    }

    public int totalResourceOccurrenceCount() {

        return directResourceOccurrenceCount
                + indirectResourceOccurrenceCount;
    }

    public boolean directResourcePresent() {

        return directResourceOccurrenceCount > 0;
    }

    public boolean indirectResourcePresent() {

        return indirectResourceOccurrenceCount > 0;
    }

    public boolean outputPresent() {

        return outputFamilyPower > 0;
    }

    public boolean officerPresent() {

        return officerFamilyPower > 0;
    }

    public boolean wealthPresent() {

        return wealthFamilyPower > 0;
    }

    public boolean directResourceDominant() {

        return directResourcePower
                > indirectResourcePower;
    }

    public boolean indirectResourceDominant() {

        return indirectResourcePower
                > directResourcePower;
    }

    public boolean balancedDirectAndIndirectResource() {

        return directResourcePower
                == indirectResourcePower;
    }

    public double resourceFamilySharePercent() {

        return resourceFamilyShareBasisPoints
                / 100.0;
    }

    public boolean hasReason(
            ResourceCompositeReasonType reason
    ) {

        if (
                reason == null
        ) {

            return false;
        }

        return reasons.contains(
                reason
        );
    }
}