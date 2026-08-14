package com.dajeom.model;

import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.TenGodFamilyChainReasonType;
import com.dajeom.type.TenGodFamilyInteractionLevel;
import com.dajeom.type.TenGodFamilyInteractionType;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 10차-7.
 *
 * 두 십성 Family 사이의
 * 하나의 방향성 관계 Evidence.
 *
 * =================================================
 *
 * 예:
 *
 * OUTPUT -> WEALTH
 *
 * type = GENERATION
 *
 * =================================================
 *
 * WEALTH -> RESOURCE
 *
 * type = CONTROL
 *
 * =================================================
 *
 * sourceShareBasisPoints /
 * targetShareBasisPoints
 *
 * 전체 외부 십성 power 대비
 * 각 Family의 상대 점유율.
 *
 * =================================================
 *
 * 이 객체만으로 특정 고전 Pattern을
 * 확정하지 않는다.
 */
public record TenGodFamilyInteractionEvidence(

        DayMasterElementRole sourceRole,

        DayMasterElementRole targetRole,

        TenGodFamilyInteractionType interactionType,

        TenGodFamilyInteractionLevel level,

        int sourcePower,

        int targetPower,

        int sourceShareBasisPoints,

        int targetShareBasisPoints,

        boolean sourceVisible,

        boolean targetVisible,

        boolean sourceMonthPresent,

        boolean targetMonthPresent,

        List<TenGodFamilyChainReasonType> reasons

) {

    public TenGodFamilyInteractionEvidence {

        Objects.requireNonNull(
                sourceRole,
                "출발 십성 Family가 필요합니다."
        );

        Objects.requireNonNull(
                targetRole,
                "대상 십성 Family가 필요합니다."
        );

        Objects.requireNonNull(
                interactionType,
                "Family 상호작용 종류가 필요합니다."
        );

        Objects.requireNonNull(
                level,
                "Family 상호작용 수준이 필요합니다."
        );

        if (
                sourceRole == targetRole
        ) {

            throw new IllegalArgumentException(
                    "동일 Family 사이에는 10-7 방향성 상호작용을 만들 수 없습니다."
            );
        }

        if (
                sourcePower <= 0
                        || targetPower <= 0
        ) {

            throw new IllegalArgumentException(
                    "Family 상호작용 Evidence는 양쪽 Family가 모두 실제 존재해야 합니다."
            );
        }

        if (
                sourceShareBasisPoints < 0
                        || sourceShareBasisPoints > 10_000

                        || targetShareBasisPoints < 0
                        || targetShareBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "Family 점유율은 0~10000bp 범위여야 합니다."
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
                    "Family 상호작용에는 하나 이상의 근거가 필요합니다."
            );
        }
    }

    public boolean generation() {

        return interactionType
                .generation();
    }

    public boolean control() {

        return interactionType
                .control();
    }

    public boolean meaningful() {

        return level
                .effective();
    }

    public boolean strong() {

        return level
                .strong();
    }

    public boolean sourceDominant() {

        return sourcePower
                > targetPower;
    }

    public boolean targetDominant() {

        return targetPower
                > sourcePower;
    }

    public boolean similarPower() {

        if (
                sourcePower <= 0
                        || targetPower <= 0
        ) {

            return false;
        }

        int larger =
                Math.max(
                        sourcePower,
                        targetPower
                );

        int smaller =
                Math.min(
                        sourcePower,
                        targetPower
                );

        return smaller
                * 100L

                >= larger
                * 80L;
    }

    public boolean involves(
            DayMasterElementRole role
    ) {

        return role != null
                && (
                sourceRole == role
                        || targetRole == role
        );
    }

    public boolean matches(

            DayMasterElementRole source,

            DayMasterElementRole target,

            TenGodFamilyInteractionType type

    ) {

        return sourceRole == source
                && targetRole == target
                && interactionType == type;
    }

    public boolean hasReason(
            TenGodFamilyChainReasonType reason
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