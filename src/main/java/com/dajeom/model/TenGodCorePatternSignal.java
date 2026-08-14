package com.dajeom.model;

import com.dajeom.type.StructureTenGodType;
import com.dajeom.type.TenGodCorePatternLevel;
import com.dajeom.type.TenGodCorePatternReasonType;
import com.dajeom.type.TenGodCorePatternType;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 10차-8.
 *
 * 하나의 핵심 십성 Pattern에 대한 Evidence.
 *
 * =================================================
 *
 * primaryPower / secondaryPower
 *
 * Pattern의 핵심 양쪽 세력.
 *
 * Pattern에 따라 개별 십성 power일 수도 있고
 * Family power일 수도 있다.
 *
 * 예:
 *
 * 식신제살
 *
 * primary = 식신
 * secondary = 칠살
 *
 * =================================================
 *
 * 비겁쟁재
 *
 * primary = 비겁 Family
 * secondary = 재성 Family
 *
 * =================================================
 *
 * 재다신약
 *
 * primary = 재성 Family
 * secondary = 0
 *
 * 일간 신약 상태는 Reason과
 * upstream DayMasterStrengthAnalysis에서 보존한다.
 */
public record TenGodCorePatternSignal(

        TenGodCorePatternType patternType,

        TenGodCorePatternLevel level,

        List<StructureTenGodType> involvedTenGodTypes,

        int primaryPower,

        int secondaryPower,

        int primaryShareBasisPoints,

        int secondaryShareBasisPoints,

        boolean primaryVisible,

        boolean secondaryVisible,

        boolean primaryMonthPresent,

        boolean secondaryMonthPresent,

        boolean directionalInteractionPresent,

        boolean directionalInteractionMeaningful,

        List<TenGodCorePatternReasonType> reasons

) {

    public TenGodCorePatternSignal {

        Objects.requireNonNull(
                patternType,
                "핵심 십성 Pattern 종류가 필요합니다."
        );

        Objects.requireNonNull(
                level,
                "핵심 십성 Pattern 수준이 필요합니다."
        );

        involvedTenGodTypes =
                involvedTenGodTypes == null
                        ? List.of()
                        : List.copyOf(
                        involvedTenGodTypes
                );

        if (
                involvedTenGodTypes.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "핵심 십성 Pattern에는 하나 이상의 관련 십성이 필요합니다."
            );
        }

        Set<StructureTenGodType> unique =
                new HashSet<>(
                        involvedTenGodTypes
                );

        if (
                unique.size()
                        != involvedTenGodTypes.size()
        ) {

            throw new IllegalArgumentException(
                    "핵심 십성 Pattern의 involvedTenGodTypes가 중복되었습니다."
            );
        }

        if (
                primaryPower < 0
                        || secondaryPower < 0
        ) {

            throw new IllegalArgumentException(
                    "십성 Pattern 세력은 음수일 수 없습니다."
            );
        }

        if (
                primaryShareBasisPoints < 0
                        || primaryShareBasisPoints > 10_000

                        || secondaryShareBasisPoints < 0
                        || secondaryShareBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "십성 Pattern 점유율은 0~10000bp 범위여야 합니다."
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
                    "핵심 십성 Pattern에는 판정 근거가 필요합니다."
            );
        }
    }

    public String koreanName() {

        return patternType
                .koreanName();
    }

    public boolean reference() {

        return level
                .reference();
    }

    public boolean meaningful() {

        return level
                .effective();
    }

    public boolean strong() {

        return level
                .strong();
    }

    public boolean involves(
            StructureTenGodType tenGodType
    ) {

        return tenGodType != null
                && involvedTenGodTypes.contains(
                tenGodType
        );
    }

    public boolean hasDirectionalInteraction() {

        return directionalInteractionPresent;
    }

    public boolean hasMeaningfulDirectionalInteraction() {

        return directionalInteractionMeaningful;
    }

    public boolean primaryDominant() {

        return primaryPower
                > secondaryPower;
    }

    public boolean secondaryDominant() {

        return secondaryPower
                > primaryPower;
    }

    public boolean bothVisible() {

        return primaryVisible
                && secondaryVisible;
    }

    public boolean eitherVisible() {

        return primaryVisible
                || secondaryVisible;
    }

    public boolean eitherMonthAnchored() {

        return primaryMonthPresent
                || secondaryMonthPresent;
    }

    public double primarySharePercent() {

        return primaryShareBasisPoints
                / 100.0;
    }

    public double secondarySharePercent() {

        return secondaryShareBasisPoints
                / 100.0;
    }

    public boolean hasReason(
            TenGodCorePatternReasonType reason
    ) {

        return reason != null
                && reasons.contains(
                reason
        );
    }
}