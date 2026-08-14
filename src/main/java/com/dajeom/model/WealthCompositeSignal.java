package com.dajeom.model;

import com.dajeom.type.WealthCompositePatternLevel;
import com.dajeom.type.WealthCompositePatternType;
import com.dajeom.type.WealthCompositeReasonType;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 10차-4.
 *
 * 하나의 재성 복합 Pattern에 대한 Evidence.
 *
 * =================================================
 *
 * directWealthPower
 *
 * 정재 세력.
 *
 * indirectWealthPower
 *
 * 편재 세력.
 *
 * wealthFamilyPower
 *
 * 정재 + 편재.
 *
 * companionFamilyPower
 *
 * 비견 + 겁재.
 *
 * officerFamilyPower
 *
 * 편관 + 정관.
 *
 * resourceFamilyPower
 *
 * 편인 + 정인.
 *
 * =================================================
 *
 * wealthFamilyShareBasisPoints
 *
 * 전체 외부 십성 세력 중
 * 재성이 차지하는 상대 점유율.
 */
public record WealthCompositeSignal(

        WealthCompositePatternType patternType,

        WealthCompositePatternLevel level,

        int directWealthPower,

        int indirectWealthPower,

        int wealthFamilyPower,

        int companionFamilyPower,

        int officerFamilyPower,

        int resourceFamilyPower,

        int wealthFamilyShareBasisPoints,

        int directWealthOccurrenceCount,

        int indirectWealthOccurrenceCount,

        boolean directWealthVisible,

        boolean indirectWealthVisible,

        boolean monthDirectWealthPresent,

        boolean monthIndirectWealthPresent,

        List<WealthCompositeReasonType> reasons

) {

    public WealthCompositeSignal {

        Objects.requireNonNull(
                patternType,
                "재성 Pattern 종류가 필요합니다."
        );

        Objects.requireNonNull(
                level,
                "재성 Pattern 수준이 필요합니다."
        );

        if (
                directWealthPower < 0
                        || indirectWealthPower < 0
                        || wealthFamilyPower < 0
                        || companionFamilyPower < 0
                        || officerFamilyPower < 0
                        || resourceFamilyPower < 0
        ) {

            throw new IllegalArgumentException(
                    "십성 세력은 음수일 수 없습니다."
            );
        }

        if (
                wealthFamilyPower
                        != directWealthPower
                        + indirectWealthPower
        ) {

            throw new IllegalArgumentException(
                    "재성 전체 세력이 정재+편재 세력과 일치하지 않습니다."
            );
        }

        if (
                wealthFamilyShareBasisPoints < 0
                        || wealthFamilyShareBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "재성 점유율은 0~10000bp 범위여야 합니다."
            );
        }

        if (
                directWealthOccurrenceCount < 0
                        || indirectWealthOccurrenceCount < 0
        ) {

            throw new IllegalArgumentException(
                    "재성 출현 횟수는 음수일 수 없습니다."
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
                    "재성 Pattern에는 하나 이상의 판정 근거가 필요합니다."
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

    public int totalWealthOccurrenceCount() {

        return directWealthOccurrenceCount
                + indirectWealthOccurrenceCount;
    }

    public boolean directWealthPresent() {

        return directWealthOccurrenceCount > 0;
    }

    public boolean indirectWealthPresent() {

        return indirectWealthOccurrenceCount > 0;
    }

    public boolean companionPresent() {

        return companionFamilyPower > 0;
    }

    public boolean officerPresent() {

        return officerFamilyPower > 0;
    }

    public boolean resourcePresent() {

        return resourceFamilyPower > 0;
    }

    public boolean directWealthDominant() {

        return directWealthPower
                > indirectWealthPower;
    }

    public boolean indirectWealthDominant() {

        return indirectWealthPower
                > directWealthPower;
    }

    public boolean balancedDirectAndIndirectWealth() {

        return directWealthPower
                == indirectWealthPower;
    }

    public double wealthFamilySharePercent() {

        return wealthFamilyShareBasisPoints
                / 100.0;
    }

    public boolean hasReason(
            WealthCompositeReasonType reason
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