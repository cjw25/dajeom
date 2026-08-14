package com.dajeom.model;

import com.dajeom.type.StructureDamageLevel;
import com.dajeom.type.StructureEstablishmentLevel;
import com.dajeom.type.StructureTenGodType;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 5차-6.
 *
 * 하나의 격 후보에 대해
 * 파격 요소들을 묶어서 보존한다.
 *
 * 주의:
 *
 * strongDamagePresent()
 *
 * 이 true라고 해서
 * 최종적으로 "파격 확정"하는 것은 아니다.
 *
 * 5-5 성격 요소,
 * 5-7 순도·혼잡도,
 * 5-9 주격 결정
 *
 * 과정이 더 남아 있다.
 */
public record StructureDamageEvidence(

        StructureFormationSupportEvidence
        supportEvidence,

        List<StructureDamageSignal>
        damageSignals,

        StructureDamageLevel
        strongestDamageLevel,

        int meaningfulDamageCount,

        boolean specialStructureDeferred

) {

    public StructureDamageEvidence {

        Objects.requireNonNull(
                supportEvidence,
                "성격 분석 Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                strongestDamageLevel,
                "가장 강한 파격 수준이 필요합니다."
        );

        damageSignals =
                damageSignals == null
                        ? List.of()
                        : List.copyOf(
                        damageSignals
                );

        if (
                meaningfulDamageCount < 0
                        || meaningfulDamageCount
                        > damageSignals.size()
        ) {

            throw new IllegalArgumentException(
                    "유효 파격 요소 개수가 올바르지 않습니다."
            );
        }

        int calculatedMeaningfulCount =
                (int) damageSignals
                        .stream()
                        .filter(
                                StructureDamageSignal
                                        ::meaningful
                        )
                        .count();

        if (
                meaningfulDamageCount
                        != calculatedMeaningfulCount
        ) {

            throw new IllegalArgumentException(
                    "유효 파격 요소 개수가 Signal과 일치하지 않습니다."
            );
        }

        StructureDamageLevel calculatedStrongest =
                damageSignals
                        .stream()
                        .map(
                                StructureDamageSignal
                                        ::level
                        )
                        .max(
                                Comparator.comparingInt(
                                        StructureDamageLevel
                                                ::rank
                                )
                        )
                        .orElse(
                                StructureDamageLevel.NONE
                        );

        if (
                strongestDamageLevel
                        != calculatedStrongest
        ) {

            throw new IllegalArgumentException(
                    "가장 강한 파격 수준이 Signal과 일치하지 않습니다."
            );
        }

        if (
                specialStructureDeferred
                        != supportEvidence
                        .specialStructureDeferred()
        ) {

            throw new IllegalArgumentException(
                    "특수구조 보류 상태가 일치하지 않습니다."
            );
        }

        if (
                specialStructureDeferred
                        && !damageSignals.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "특수구조 후보에는 일반 내격 파격 Rule을 적용할 수 없습니다."
            );
        }
    }

    public StructureTenGodType tenGod() {

        return supportEvidence
                .tenGod();
    }

    public StructureEstablishmentLevel
    establishmentLevel() {

        return supportEvidence
                .establishmentLevel();
    }

    public boolean mainQi() {

        return supportEvidence
                .mainQi();
    }

    public boolean activeCandidate() {

        return supportEvidence
                .activeCandidate();
    }

    public boolean standardStructureCandidate() {

        return supportEvidence
                .standardStructureCandidate();
    }

    public boolean hasAnyDamage() {

        return damageSignals
                .stream()
                .anyMatch(
                        StructureDamageSignal
                                ::exists
                );
    }

    public boolean hasMeaningfulDamage() {

        return meaningfulDamageCount > 0;
    }

    public boolean hasStrongDamage() {

        return strongestDamageLevel
                .strong();
    }

    public Optional<StructureDamageSignal>
    strongestSignal() {

        return damageSignals
                .stream()
                .max(
                        Comparator.comparingInt(
                                signal ->
                                        signal
                                                .level()
                                                .rank()
                        )
                );
    }

    /**
     * 일반 내격 후보이고
     * 실제 활성 상태이며
     * 의미 있는 파격 요소까지
     * 존재하는가.
     */
    public boolean damagePresent() {

        return activeCandidate()
                && standardStructureCandidate()
                && hasMeaningfulDamage();
    }

    /**
     * 5-5 성격 요소도 있고
     * 5-6 파격 요소도 동시에 있는 구조.
     *
     * 5-7 혼잡도/순도 분석에서
     * 중요한 Evidence가 된다.
     */
    public boolean supportAndDamageCoexist() {

        return supportEvidence
                .hasMeaningfulSupport()

                && hasMeaningfulDamage();
    }
}