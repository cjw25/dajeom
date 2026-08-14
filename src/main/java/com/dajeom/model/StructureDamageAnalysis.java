package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.StructureDamagePatternType;
import com.dajeom.type.StructureTenGodType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 5차-6.
 *
 * 전체 격 후보에 대한
 * 파격 요소 분석 결과.
 */
public record StructureDamageAnalysis(

        HeavenlyStem dayMaster,

        EarthlyBranch monthBranch,

        StructureDamageEvidence
        mainQiEvidence,

        List<StructureDamageEvidence>
        evidences

) {

    public StructureDamageAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지가 필요합니다."
        );

        Objects.requireNonNull(
                mainQiEvidence,
                "월령 정기 파격 Evidence가 필요합니다."
        );

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        if (
                evidences.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "파격 분석 Evidence가 하나 이상 필요합니다."
            );
        }

        if (
                !mainQiEvidence.mainQi()
        ) {

            throw new IllegalArgumentException(
                    "mainQiEvidence는 월령 정기여야 합니다."
            );
        }

        long mainQiCount =
                evidences
                        .stream()
                        .filter(
                                StructureDamageEvidence
                                        ::mainQi
                        )
                        .count();

        if (
                mainQiCount != 1
        ) {

            throw new IllegalArgumentException(
                    "월령 정기 파격 Evidence는 정확히 하나여야 합니다."
            );
        }
    }

    public List<StructureDamageEvidence>
    damagedEvidences() {

        return evidences
                .stream()
                .filter(
                        StructureDamageEvidence
                                ::hasMeaningfulDamage
                )
                .toList();
    }

    public List<StructureDamageEvidence>
    stronglyDamagedEvidences() {

        return evidences
                .stream()
                .filter(
                        StructureDamageEvidence
                                ::hasStrongDamage
                )
                .toList();
    }

    public List<StructureDamageEvidence>
    supportAndDamageCoexistingEvidences() {

        return evidences
                .stream()
                .filter(
                        StructureDamageEvidence
                                ::supportAndDamageCoexist
                )
                .toList();
    }

    public List<StructureDamageEvidence>
    specialStructureDeferredEvidences() {

        return evidences
                .stream()
                .filter(
                        StructureDamageEvidence
                                ::specialStructureDeferred
                )
                .toList();
    }

    public boolean mainQiHasMeaningfulDamage() {

        return mainQiEvidence
                .hasMeaningfulDamage();
    }

    public boolean mainQiHasStrongDamage() {

        return mainQiEvidence
                .hasStrongDamage();
    }

    public boolean mainQiSupportAndDamageCoexist() {

        return mainQiEvidence
                .supportAndDamageCoexist();
    }

    public boolean requiresSpecialStructureReview() {

        return !specialStructureDeferredEvidences()
                .isEmpty();
    }

    public Optional<StructureDamageEvidence>
    evidenceOf(
            StructureTenGodType tenGod
    ) {

        if (
                tenGod == null
        ) {

            return Optional.empty();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.tenGod()
                                        == tenGod
                )
                .findFirst();
    }

    public List<StructureDamageSignal>
    signalsOf(
            StructureDamagePatternType patternType
    ) {

        if (
                patternType == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .flatMap(
                        evidence ->
                                evidence
                                        .damageSignals()
                                        .stream()
                )
                .filter(
                        signal ->
                                signal.patternType()
                                        == patternType
                )
                .toList();
    }
}