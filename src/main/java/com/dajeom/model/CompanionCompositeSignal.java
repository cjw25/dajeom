package com.dajeom.model;

import com.dajeom.type.CompanionCompositePatternLevel;
import com.dajeom.type.CompanionCompositePatternType;
import com.dajeom.type.CompanionCompositeReasonType;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 10차-2.
 *
 * 비겁 복합구조 하나의 Pattern Evidence.
 *
 * =================================================
 *
 * 모든 숫자는 해당 Pattern이 만들어질 당시의
 * 원국 십성 세력 Evidence를 보존한다.
 *
 * =================================================
 *
 * companionPower
 *
 * 비견 세력.
 *
 * robWealthPower
 *
 * 겁재 세력.
 *
 * companionFamilyPower
 *
 * 비견 + 겁재.
 *
 * wealthFamilyPower
 *
 * 편재 + 정재.
 *
 * =================================================
 *
 * familyShareBasisPoints
 *
 * 전체 외부 십성 power 중
 * 비겁이 차지하는 상대 비중.
 */
public record CompanionCompositeSignal(

        CompanionCompositePatternType patternType,

        CompanionCompositePatternLevel level,

        int companionPower,

        int robWealthPower,

        int companionFamilyPower,

        int wealthFamilyPower,

        int familyShareBasisPoints,

        int companionOccurrenceCount,

        int robWealthOccurrenceCount,

        boolean companionVisible,

        boolean robWealthVisible,

        boolean monthCompanionPresent,

        boolean monthRobWealthPresent,

        List<CompanionCompositeReasonType> reasons

) {

    public CompanionCompositeSignal {

        Objects.requireNonNull(
                patternType,
                "비겁 Pattern 종류가 필요합니다."
        );

        Objects.requireNonNull(
                level,
                "비겁 Pattern 수준이 필요합니다."
        );

        if (
                companionPower < 0
                        || robWealthPower < 0
                        || companionFamilyPower < 0
                        || wealthFamilyPower < 0
        ) {

            throw new IllegalArgumentException(
                    "비겁·재성 세력은 음수일 수 없습니다."
            );
        }

        if (
                companionFamilyPower
                        != companionPower
                        + robWealthPower
        ) {

            throw new IllegalArgumentException(
                    "비겁 전체 세력이 비견+겁재 세력과 일치하지 않습니다."
            );
        }

        if (
                familyShareBasisPoints < 0
                        || familyShareBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "비겁 점유율은 0~10000bp 범위여야 합니다."
            );
        }

        if (
                companionOccurrenceCount < 0
                        || robWealthOccurrenceCount < 0
        ) {

            throw new IllegalArgumentException(
                    "십성 출현 횟수는 음수일 수 없습니다."
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
                    "비겁 Pattern에는 하나 이상의 판정 근거가 필요합니다."
            );
        }
    }

    public boolean meaningful() {

        return level.effective();
    }

    public boolean strong() {

        return level.strong();
    }

    public int totalCompanionOccurrenceCount() {

        return companionOccurrenceCount
                + robWealthOccurrenceCount;
    }

    public boolean wealthPresent() {

        return wealthFamilyPower > 0;
    }

    public boolean companionDominatesRobWealth() {

        return companionPower
                > robWealthPower;
    }

    public boolean robWealthDominatesCompanion() {

        return robWealthPower
                > companionPower;
    }

    public boolean balancedCompanionAndRobWealth() {

        return companionPower
                == robWealthPower;
    }

    public double familySharePercent() {

        return familyShareBasisPoints
                / 100.0;
    }

    public boolean hasReason(
            CompanionCompositeReasonType reason
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