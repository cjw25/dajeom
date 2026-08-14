package com.dajeom.model;

import com.dajeom.type.StructureMixingSeverity;
import com.dajeom.type.StructureMixingSignalType;
import com.dajeom.type.StructureTenGodType;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 5차-7.
 *
 * 하나의 격 혼잡 Evidence.
 *
 * relatedTenGods:
 *
 * 이 혼잡 신호에 실제로 관련된
 * 정확한 십성들을 보존한다.
 *
 * 예:
 *
 * 관살혼잡
 * ->
 * DIRECT_OFFICER
 * SEVEN_KILLINGS
 *
 * evidenceCount:
 *
 * 해당 혼잡 신호를 뒷받침하는
 * source 또는 구조 Evidence의 개수.
 *
 * 확률이나 점수가 아니다.
 */
public record StructureMixingSignal(

        StructureMixingSignalType type,

        StructureMixingSeverity severity,

        List<StructureTenGodType> relatedTenGods,

        int evidenceCount

) {

    public StructureMixingSignal {

        Objects.requireNonNull(
                type,
                "혼잡 신호 유형이 필요합니다."
        );

        Objects.requireNonNull(
                severity,
                "혼잡 수준이 필요합니다."
        );

        relatedTenGods =
                relatedTenGods == null
                        ? List.of()
                        : List.copyOf(
                        relatedTenGods
                );

        if (
                relatedTenGods.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "혼잡 신호에는 관련 십성이 하나 이상 필요합니다."
            );
        }

        if (
                relatedTenGods
                        .stream()
                        .distinct()
                        .count()
                        != relatedTenGods.size()
        ) {

            throw new IllegalArgumentException(
                    "혼잡 신호의 관련 십성이 중복되었습니다."
            );
        }

        if (
                evidenceCount <= 0
        ) {

            throw new IllegalArgumentException(
                    "혼잡 Evidence 개수는 1 이상이어야 합니다."
            );
        }
    }

    public boolean high() {

        return severity.high();
    }

    public boolean moderate() {

        return severity.moderate();
    }

    public boolean involves(
            StructureTenGodType tenGod
    ) {

        if (
                tenGod == null
        ) {

            return false;
        }

        return relatedTenGods
                .contains(
                        tenGod
                );
    }
}