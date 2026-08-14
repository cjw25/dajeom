package com.dajeom.model;

import com.dajeom.type.OutputCompositePatternLevel;
import com.dajeom.type.OutputCompositePatternType;
import com.dajeom.type.OutputCompositeReasonType;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 10차-3.
 *
 * 하나의 식상 복합 Pattern에 대한 Evidence.
 *
 * =================================================
 *
 * eatingGodPower
 *
 * 식신 세력.
 *
 * hurtingOfficerPower
 *
 * 상관 세력.
 *
 * outputFamilyPower
 *
 * 식신 + 상관.
 *
 * resourceFamilyPower
 *
 * 편인 + 정인.
 *
 * officerFamilyPower
 *
 * 편관 + 정관.
 *
 * =================================================
 *
 * outputFamilyShareBasisPoints
 *
 * 전체 외부 십성 power 중
 * 식상이 차지하는 상대 비율.
 */
public record OutputCompositeSignal(

        OutputCompositePatternType patternType,

        OutputCompositePatternLevel level,

        int eatingGodPower,

        int hurtingOfficerPower,

        int outputFamilyPower,

        int resourceFamilyPower,

        int officerFamilyPower,

        int outputFamilyShareBasisPoints,

        int eatingGodOccurrenceCount,

        int hurtingOfficerOccurrenceCount,

        boolean eatingGodVisible,

        boolean hurtingOfficerVisible,

        boolean monthEatingGodPresent,

        boolean monthHurtingOfficerPresent,

        List<OutputCompositeReasonType> reasons

) {

    public OutputCompositeSignal {

        Objects.requireNonNull(
                patternType,
                "식상 Pattern 종류가 필요합니다."
        );

        Objects.requireNonNull(
                level,
                "식상 Pattern 수준이 필요합니다."
        );

        if (
                eatingGodPower < 0
                        || hurtingOfficerPower < 0
                        || outputFamilyPower < 0
                        || resourceFamilyPower < 0
                        || officerFamilyPower < 0
        ) {

            throw new IllegalArgumentException(
                    "십성 세력은 음수일 수 없습니다."
            );
        }

        if (
                outputFamilyPower
                        != eatingGodPower
                        + hurtingOfficerPower
        ) {

            throw new IllegalArgumentException(
                    "식상 전체 세력이 식신+상관 세력과 일치하지 않습니다."
            );
        }

        if (
                outputFamilyShareBasisPoints < 0
                        || outputFamilyShareBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "식상 점유율은 0~10000bp 범위여야 합니다."
            );
        }

        if (
                eatingGodOccurrenceCount < 0
                        || hurtingOfficerOccurrenceCount < 0
        ) {

            throw new IllegalArgumentException(
                    "식상 출현 횟수는 음수일 수 없습니다."
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
                    "식상 Pattern에는 하나 이상의 판정 근거가 필요합니다."
            );
        }
    }

    public boolean meaningful() {

        return level.effective();
    }

    public boolean strong() {

        return level.strong();
    }

    public int totalOutputOccurrenceCount() {

        return eatingGodOccurrenceCount
                + hurtingOfficerOccurrenceCount;
    }

    public boolean eatingGodPresent() {

        return eatingGodOccurrenceCount > 0;
    }

    public boolean hurtingOfficerPresent() {

        return hurtingOfficerOccurrenceCount > 0;
    }

    public boolean resourcePresent() {

        return resourceFamilyPower > 0;
    }

    public boolean officerPresent() {

        return officerFamilyPower > 0;
    }

    public boolean eatingGodDominant() {

        return eatingGodPower
                > hurtingOfficerPower;
    }

    public boolean hurtingOfficerDominant() {

        return hurtingOfficerPower
                > eatingGodPower;
    }

    public boolean balancedEatingGodAndHurtingOfficer() {

        return eatingGodPower
                == hurtingOfficerPower;
    }

    public double outputFamilySharePercent() {

        return outputFamilyShareBasisPoints
                / 100.0;
    }

    public boolean hasReason(
            OutputCompositeReasonType reason
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