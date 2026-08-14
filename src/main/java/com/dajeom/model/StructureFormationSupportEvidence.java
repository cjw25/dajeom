package com.dajeom.model;

import com.dajeom.type.StructureBalanceLevel;
import com.dajeom.type.StructureEstablishmentLevel;
import com.dajeom.type.StructureSupportLevel;
import com.dajeom.type.StructureTenGodType;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 5차-5.
 *
 * 하나의 격 후보에 대해
 * 해당 격을 살리는 성격(成格) 요소들을
 * 묶어서 보존한다.
 *
 * 예:
 *
 * 정관 후보:
 *
 * - 재생관
 * - 관인상생
 *
 * 두 패턴을 동시에 가질 수 있다.
 *
 * 아직:
 *
 * - 최종 성격
 * - 파격
 * - 주격
 *
 * 을 확정하지 않는다.
 */
public record StructureFormationSupportEvidence(

        StructureBalanceEvidence balanceEvidence,

        List<StructureSupportSignal> supportSignals,

        StructureSupportLevel strongestSupportLevel,

        int meaningfulSupportCount,

        boolean specialStructureDeferred

) {

    public StructureFormationSupportEvidence {

        Objects.requireNonNull(
                balanceEvidence,
                "격국 강약 Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                strongestSupportLevel,
                "가장 강한 성격 요소 수준이 필요합니다."
        );

        supportSignals =
                supportSignals == null
                        ? List.of()
                        : List.copyOf(
                        supportSignals
                );

        if (
                meaningfulSupportCount < 0
                        || meaningfulSupportCount
                        > supportSignals.size()
        ) {

            throw new IllegalArgumentException(
                    "유효 성격 요소 개수가 올바르지 않습니다."
            );
        }

        int calculatedMeaningfulCount =
                (int) supportSignals
                        .stream()
                        .filter(
                                StructureSupportSignal
                                        ::meaningful
                        )
                        .count();

        if (
                meaningfulSupportCount
                        != calculatedMeaningfulCount
        ) {

            throw new IllegalArgumentException(
                    "유효 성격 요소 개수가 Signal과 일치하지 않습니다."
            );
        }

        StructureSupportLevel calculatedStrongest =
                supportSignals
                        .stream()
                        .map(
                                StructureSupportSignal
                                        ::level
                        )
                        .max(
                                Comparator.comparingInt(
                                        StructureSupportLevel
                                                ::rank
                                )
                        )
                        .orElse(
                                StructureSupportLevel.NONE
                        );

        if (
                calculatedStrongest
                        != strongestSupportLevel
        ) {

            throw new IllegalArgumentException(
                    "가장 강한 성격 요소 수준이 Signal과 일치하지 않습니다."
            );
        }

        if (
                specialStructureDeferred
                        != balanceEvidence
                        .specialStructureCandidate()
        ) {

            throw new IllegalArgumentException(
                    "특수구조 보류 여부가 격 후보 정보와 일치하지 않습니다."
            );
        }

        if (
                specialStructureDeferred
                        && !supportSignals.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "특수구조 보류 후보에는 일반 내격 성격 패턴을 적용할 수 없습니다."
            );
        }
    }

    public StructureTenGodType tenGod() {

        return balanceEvidence
                .tenGod();
    }

    public StructureEstablishmentLevel
    establishmentLevel() {

        return balanceEvidence
                .establishmentEvidence()
                .level();
    }

    public StructureBalanceLevel balanceLevel() {

        return balanceEvidence
                .balanceLevel();
    }

    public boolean mainQi() {

        return balanceEvidence
                .mainQi();
    }

    public boolean activeCandidate() {

        return balanceEvidence
                .activeCandidate();
    }

    public boolean standardStructureCandidate() {

        return balanceEvidence
                .standardStructureCandidate();
    }

    public boolean hasAnySupportPattern() {

        return supportSignals
                .stream()
                .anyMatch(
                        StructureSupportSignal
                                ::exists
                );
    }

    public boolean hasMeaningfulSupport() {

        return meaningfulSupportCount > 0;
    }

    public boolean hasStrongSupport() {

        return strongestSupportLevel
                .strong();
    }

    public Optional<StructureSupportSignal>
    strongestSignal() {

        return supportSignals
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
     * 격 후보가:
     *
     * - 실제 활성 후보이고
     * - 일반 내격 대상이며
     * - 적어도 하나의 의미 있는 성격 요소가 있고
     *
     * 현재 단계에서 성격 기반을
     * 확보한 것으로 볼 수 있는가.
     *
     * 이것도 최종 成格 확정은 아니다.
     */
    public boolean formationSupportPresent() {

        return activeCandidate()
                && standardStructureCandidate()
                && hasMeaningfulSupport();
    }
}