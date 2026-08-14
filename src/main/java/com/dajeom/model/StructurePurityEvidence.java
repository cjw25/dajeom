package com.dajeom.model;

import com.dajeom.type.StructurePurityLevel;
import com.dajeom.type.StructureTenGodType;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 5차-7.
 *
 * 하나의 격 후보에 대한
 * 순도·혼잡도 분석 결과.
 *
 * 중요한 원칙:
 *
 * 순도는 격의 길흉이 아니다.
 *
 * PURE
 * ->
 * 좋은 격
 *
 * HIGHLY_MIXED
 * ->
 * 나쁜 격
 *
 * 이라는 뜻이 아니다.
 *
 * 오직:
 *
 * 해당 격 후보가 원국에서
 * 얼마나 단일하고 명확하게
 * 표현되고 있는지를 나타낸다.
 */
public record StructurePurityEvidence(

        StructureDamageEvidence damageEvidence,

        List<StructureMixingSignal> mixingSignals,

        int moderateSignalCount,

        int highSignalCount,

        StructurePurityLevel purityLevel,

        boolean specialStructureDeferred

) {

    public StructurePurityEvidence {

        Objects.requireNonNull(
                damageEvidence,
                "파격 분석 Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                purityLevel,
                "격 순도 수준이 필요합니다."
        );

        mixingSignals =
                mixingSignals == null
                        ? List.of()
                        : List.copyOf(
                        mixingSignals
                );

        if (
                moderateSignalCount < 0
                        || highSignalCount < 0
        ) {

            throw new IllegalArgumentException(
                    "혼잡 신호 개수는 음수가 될 수 없습니다."
            );
        }

        int calculatedModerate =
                (int) mixingSignals
                        .stream()
                        .filter(
                                StructureMixingSignal
                                        ::moderate
                        )
                        .count();

        int calculatedHigh =
                (int) mixingSignals
                        .stream()
                        .filter(
                                StructureMixingSignal
                                        ::high
                        )
                        .count();

        if (
                moderateSignalCount
                        != calculatedModerate
        ) {

            throw new IllegalArgumentException(
                    "중간 혼잡 신호 개수가 실제 Signal과 일치하지 않습니다."
            );
        }

        if (
                highSignalCount
                        != calculatedHigh
        ) {

            throw new IllegalArgumentException(
                    "강한 혼잡 신호 개수가 실제 Signal과 일치하지 않습니다."
            );
        }

        if (
                specialStructureDeferred
                        != damageEvidence
                        .specialStructureDeferred()
        ) {

            throw new IllegalArgumentException(
                    "특수구조 보류 상태가 일치하지 않습니다."
            );
        }

        boolean evaluationApplicable =
                damageEvidence.activeCandidate()
                        && damageEvidence
                        .standardStructureCandidate();

        if (
                !evaluationApplicable
        ) {

            if (
                    !purityLevel.notApplicable()
            ) {

                throw new IllegalArgumentException(
                        "비활성 또는 특수구조 후보는 일반 격 순도 평가 대상이 아닙니다."
                );
            }

            if (
                    !mixingSignals.isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "평가 대상이 아닌 격 후보에는 혼잡 Signal을 생성할 수 없습니다."
                );
            }

        } else {

            if (
                    purityLevel.notApplicable()
            ) {

                throw new IllegalArgumentException(
                        "활성 일반 내격 후보에는 순도 분석 결과가 필요합니다."
                );
            }
        }
    }

    public StructureTenGodType tenGod() {

        return damageEvidence
                .tenGod();
    }

    public boolean mainQi() {

        return damageEvidence
                .mainQi();
    }

    public boolean activeCandidate() {

        return damageEvidence
                .activeCandidate();
    }

    public boolean standardStructureCandidate() {

        return damageEvidence
                .standardStructureCandidate();
    }

    public boolean evaluationApplicable() {

        return activeCandidate()
                && standardStructureCandidate();
    }

    public boolean pure() {

        return purityLevel
                .pure();
    }

    public boolean mostlyPure() {

        return purityLevel
                .mostlyPure();
    }

    public boolean mixed() {

        return purityLevel
                .mixed();
    }

    public boolean highlyMixed() {

        return purityLevel
                .highlyMixed();
    }

    public boolean relativelyPure() {

        return purityLevel
                .relativelyPure();
    }

    public boolean meaningfullyMixed() {

        return purityLevel
                .meaningfullyMixed();
    }

    public boolean hasHighMixingSignal() {

        return highSignalCount > 0;
    }

    public boolean hasModerateMixingSignal() {

        return moderateSignalCount > 0;
    }

    public boolean hasSignal(
            com.dajeom.type.StructureMixingSignalType type
    ) {

        if (
                type == null
        ) {

            return false;
        }

        return mixingSignals
                .stream()
                .anyMatch(
                        signal ->
                                signal.type()
                                        == type
                );
    }
}