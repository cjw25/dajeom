package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.StructurePurityLevel;
import com.dajeom.type.StructureTenGodType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 5차-7.
 *
 * 전체 월령 격 후보에 대한
 * 순도·혼잡도 분석 결과.
 *
 * global fields:
 *
 * meaningfulStandardCandidateCount
 * ->
 * 5-3에서 의미 있는 일반 내격 후보 수
 *
 * establishedStandardCandidateCount
 * ->
 * ESTABLISHED 일반 내격 후보 수
 *
 * exposedStandardCandidateCount
 * ->
 * 실제 천간으로 투출한 일반 내격 후보 수
 *
 * secondaryExposedWhileMainHidden
 * ->
 * 정기는 미투출인데
 * 중기/여기 일반 내격 후보가 투출한 구조
 */
public record StructurePurityAnalysis(

        HeavenlyStem dayMaster,

        EarthlyBranch monthBranch,

        int meaningfulStandardCandidateCount,

        int establishedStandardCandidateCount,

        int exposedStandardCandidateCount,

        boolean secondaryExposedWhileMainHidden,

        StructurePurityEvidence mainQiEvidence,

        List<StructurePurityEvidence> evidences

) {

    public StructurePurityAnalysis {

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
                "월령 정기 순도 Evidence가 필요합니다."
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
                    "순도 분석 Evidence가 하나 이상 필요합니다."
            );
        }

        if (
                meaningfulStandardCandidateCount < 0
                        || establishedStandardCandidateCount < 0
                        || exposedStandardCandidateCount < 0
        ) {

            throw new IllegalArgumentException(
                    "격 후보 개수는 음수가 될 수 없습니다."
            );
        }

        if (
                establishedStandardCandidateCount
                        > meaningfulStandardCandidateCount
        ) {

            throw new IllegalArgumentException(
                    "ESTABLISHED 후보 수가 의미 있는 후보 수보다 클 수 없습니다."
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
                                StructurePurityEvidence
                                        ::mainQi
                        )
                        .count();

        if (
                mainQiCount != 1
        ) {

            throw new IllegalArgumentException(
                    "월령 정기 순도 Evidence는 정확히 하나여야 합니다."
            );
        }

        int calculatedMeaningfulCount =
                (int) evidences
                        .stream()
                        .filter(
                                evidence ->
                                        evidence
                                                .damageEvidence()
                                                .standardStructureCandidate()
                        )
                        .filter(
                                evidence ->
                                        evidence
                                                .damageEvidence()
                                                .establishmentLevel()
                                                .meaningfulCandidate()
                        )
                        .count();

        int calculatedEstablishedCount =
                (int) evidences
                        .stream()
                        .filter(
                                evidence ->
                                        evidence
                                                .damageEvidence()
                                                .standardStructureCandidate()
                        )
                        .filter(
                                evidence ->
                                        evidence
                                                .damageEvidence()
                                                .establishmentLevel()
                                                .established()
                        )
                        .count();

        int calculatedExposedCount =
                (int) evidences
                        .stream()
                        .filter(
                                evidence ->
                                        evidence
                                                .damageEvidence()
                                                .standardStructureCandidate()
                        )
                        .filter(
                                StructurePurityAnalysis
                                        ::isCandidateExposed
                        )
                        .count();

        if (
                meaningfulStandardCandidateCount
                        != calculatedMeaningfulCount
        ) {

            throw new IllegalArgumentException(
                    "의미 있는 일반 내격 후보 수가 Evidence와 일치하지 않습니다."
            );
        }

        if (
                establishedStandardCandidateCount
                        != calculatedEstablishedCount
        ) {

            throw new IllegalArgumentException(
                    "ESTABLISHED 일반 내격 후보 수가 Evidence와 일치하지 않습니다."
            );
        }

        if (
                exposedStandardCandidateCount
                        != calculatedExposedCount
        ) {

            throw new IllegalArgumentException(
                    "투출 일반 내격 후보 수가 Evidence와 일치하지 않습니다."
            );
        }
    }

    private static boolean isCandidateExposed(
            StructurePurityEvidence evidence
    ) {

        return evidence
                .damageEvidence()
                .supportEvidence()
                .balanceEvidence()
                .establishmentEvidence()
                .exposed();
    }

    public List<StructurePurityEvidence>
    pureEvidences() {

        return evidences
                .stream()
                .filter(
                        StructurePurityEvidence
                                ::pure
                )
                .toList();
    }

    public List<StructurePurityEvidence>
    mostlyPureEvidences() {

        return evidences
                .stream()
                .filter(
                        StructurePurityEvidence
                                ::mostlyPure
                )
                .toList();
    }

    public List<StructurePurityEvidence>
    mixedEvidences() {

        return evidences
                .stream()
                .filter(
                        StructurePurityEvidence
                                ::meaningfullyMixed
                )
                .toList();
    }

    public List<StructurePurityEvidence>
    highlyMixedEvidences() {

        return evidences
                .stream()
                .filter(
                        StructurePurityEvidence
                                ::highlyMixed
                )
                .toList();
    }

    public List<StructurePurityEvidence>
    evaluatedEvidences() {

        return evidences
                .stream()
                .filter(
                        StructurePurityEvidence
                                ::evaluationApplicable
                )
                .toList();
    }

    public List<StructurePurityEvidence>
    specialStructureDeferredEvidences() {

        return evidences
                .stream()
                .filter(
                        StructurePurityEvidence
                                ::specialStructureDeferred
                )
                .toList();
    }

    public boolean hasMultipleMeaningfulCandidates() {

        return meaningfulStandardCandidateCount >= 2;
    }

    public boolean hasMultipleEstablishedCandidates() {

        return establishedStandardCandidateCount >= 2;
    }

    public boolean hasMultipleExposedCandidates() {

        return exposedStandardCandidateCount >= 2;
    }

    public boolean mainQiRelativelyPure() {

        return mainQiEvidence
                .relativelyPure();
    }

    public boolean mainQiMeaningfullyMixed() {

        return mainQiEvidence
                .meaningfullyMixed();
    }

    public Optional<StructurePurityEvidence>
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

    public int countLevel(
            StructurePurityLevel level
    ) {

        if (
                level == null
        ) {

            return 0;
        }

        return (int) evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.purityLevel()
                                        == level
                )
                .count();
    }

    public boolean requiresSpecialStructureReview() {

        return !specialStructureDeferredEvidences()
                .isEmpty();
    }
}