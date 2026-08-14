package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.StructureSupportPatternType;
import com.dajeom.type.StructureTenGodType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 5차-5.
 *
 * 전체 격 후보의 성격(成格) 요소 분석 결과.
 *
 * 주의:
 *
 * 이번 단계의 support는
 * 격을 살리는 구조적인 보조 Evidence다.
 *
 * 이것만으로:
 *
 * - 성격 확정
 * - 파격 여부
 * - 주격
 *
 * 을 결정하지 않는다.
 *
 * 다음 5-6에서 파격 요소를
 * 별도로 분석해야 한다.
 */
public record StructureFormationSupportAnalysis(

        HeavenlyStem dayMaster,

        EarthlyBranch monthBranch,

        StructureFormationSupportEvidence
        mainQiEvidence,

        List<StructureFormationSupportEvidence>
        evidences

) {

    public StructureFormationSupportAnalysis {

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
                "월령 정기 성격 Evidence가 필요합니다."
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
                    "성격 분석 Evidence가 하나 이상 필요합니다."
            );
        }

        if (
                !mainQiEvidence.mainQi()
        ) {

            throw new IllegalArgumentException(
                    "mainQiEvidence는 월령 정기 후보여야 합니다."
            );
        }

        long mainQiCount =
                evidences
                        .stream()
                        .filter(
                                StructureFormationSupportEvidence
                                        ::mainQi
                        )
                        .count();

        if (
                mainQiCount != 1
        ) {

            throw new IllegalArgumentException(
                    "월령 정기 성격 Evidence는 정확히 하나여야 합니다."
            );
        }
    }

    public List<StructureFormationSupportEvidence>
    meaningfulSupportEvidences() {

        return evidences
                .stream()
                .filter(
                        StructureFormationSupportEvidence
                                ::hasMeaningfulSupport
                )
                .toList();
    }

    public List<StructureFormationSupportEvidence>
    stronglySupportedEvidences() {

        return evidences
                .stream()
                .filter(
                        StructureFormationSupportEvidence
                                ::hasStrongSupport
                )
                .toList();
    }

    public List<StructureFormationSupportEvidence>
    formationSupportedEvidences() {

        return evidences
                .stream()
                .filter(
                        StructureFormationSupportEvidence
                                ::formationSupportPresent
                )
                .toList();
    }

    public List<StructureFormationSupportEvidence>
    specialStructureDeferredEvidences() {

        return evidences
                .stream()
                .filter(
                        StructureFormationSupportEvidence
                                ::specialStructureDeferred
                )
                .toList();
    }

    public boolean mainQiHasMeaningfulSupport() {

        return mainQiEvidence
                .hasMeaningfulSupport();
    }

    public boolean mainQiHasStrongSupport() {

        return mainQiEvidence
                .hasStrongSupport();
    }

    public boolean mainQiFormationSupportPresent() {

        return mainQiEvidence
                .formationSupportPresent();
    }

    public boolean requiresSpecialStructureReview() {

        return !specialStructureDeferredEvidences()
                .isEmpty();
    }

    public Optional<StructureFormationSupportEvidence>
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

    public List<StructureSupportSignal>
    signalsOf(
            StructureSupportPatternType patternType
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
                                        .supportSignals()
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