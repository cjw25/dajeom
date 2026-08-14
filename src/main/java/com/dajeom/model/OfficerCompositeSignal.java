package com.dajeom.model;

import com.dajeom.type.OfficerCompositePatternLevel;
import com.dajeom.type.OfficerCompositePatternType;
import com.dajeom.type.OfficerCompositeReasonType;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 10차-5.
 *
 * 하나의 관살 복합 Pattern에 대한 Evidence.
 */
public record OfficerCompositeSignal(

        OfficerCompositePatternType patternType,

        OfficerCompositePatternLevel level,

        int directOfficerPower,

        int sevenKillingsPower,

        int officerFamilyPower,

        int outputFamilyPower,

        int resourceFamilyPower,

        int wealthFamilyPower,

        int officerFamilyShareBasisPoints,

        int directOfficerOccurrenceCount,

        int sevenKillingsOccurrenceCount,

        boolean directOfficerVisible,

        boolean sevenKillingsVisible,

        boolean monthDirectOfficerPresent,

        boolean monthSevenKillingsPresent,

        List<OfficerCompositeReasonType> reasons

) {

    public OfficerCompositeSignal {

        Objects.requireNonNull(
                patternType,
                "관살 Pattern 종류가 필요합니다."
        );

        Objects.requireNonNull(
                level,
                "관살 Pattern 수준이 필요합니다."
        );

        if (
                directOfficerPower < 0
                        || sevenKillingsPower < 0
                        || officerFamilyPower < 0
                        || outputFamilyPower < 0
                        || resourceFamilyPower < 0
                        || wealthFamilyPower < 0
        ) {

            throw new IllegalArgumentException(
                    "십성 세력은 음수일 수 없습니다."
            );
        }

        if (
                officerFamilyPower
                        != directOfficerPower
                        + sevenKillingsPower
        ) {

            throw new IllegalArgumentException(
                    "관살 전체 세력이 정관+칠살 세력과 일치하지 않습니다."
            );
        }

        if (
                officerFamilyShareBasisPoints < 0
                        || officerFamilyShareBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "관살 점유율은 0~10000bp 범위여야 합니다."
            );
        }

        if (
                directOfficerOccurrenceCount < 0
                        || sevenKillingsOccurrenceCount < 0
        ) {

            throw new IllegalArgumentException(
                    "관살 출현 횟수는 음수일 수 없습니다."
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
                    "관살 Pattern에는 하나 이상의 판정 근거가 필요합니다."
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

    public int totalOfficerOccurrenceCount() {

        return directOfficerOccurrenceCount
                + sevenKillingsOccurrenceCount;
    }

    public boolean directOfficerPresent() {

        return directOfficerOccurrenceCount > 0;
    }

    public boolean sevenKillingsPresent() {

        return sevenKillingsOccurrenceCount > 0;
    }

    public boolean outputPresent() {

        return outputFamilyPower > 0;
    }

    public boolean resourcePresent() {

        return resourceFamilyPower > 0;
    }

    public boolean wealthPresent() {

        return wealthFamilyPower > 0;
    }

    public boolean directOfficerDominant() {

        return directOfficerPower
                > sevenKillingsPower;
    }

    public boolean sevenKillingsDominant() {

        return sevenKillingsPower
                > directOfficerPower;
    }

    public boolean balancedOfficerAndKillings() {

        return directOfficerPower
                == sevenKillingsPower;
    }

    public double officerFamilySharePercent() {

        return officerFamilyShareBasisPoints
                / 100.0;
    }

    public boolean hasReason(
            OfficerCompositeReasonType reason
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