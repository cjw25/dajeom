package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.StructureUsefulGodCandidateRole;
import com.dajeom.type.UsefulGodMethodApplicability;
import com.dajeom.type.UsefulGodMethodType;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 7차-4.
 *
 * 격국용신법으로 木火土金水 전체를
 * 평가한 결과.
 *
 * =================================================
 *
 * 입력
 *
 * 7-1
 * UsefulGodMethodAnalysis
 *
 * 5차
 * StructureAnalysis
 *
 * 6-5
 * RemedyAnalysis
 *
 * 6-9
 * ClimateHealthCrossValidationAnalysis
 *
 * =================================================
 *
 * 일반 내격에서는:
 *
 * - 성격 요소
 * - 파격 요소
 * - 파격 복구 요소
 *
 * 를 구분한다.
 *
 * 특수격은 일반 내격 공식으로
 * 억지로 확정하지 않는다.
 *
 * 종격은 7-7이 담당한다.
 */
public record StructureUsefulGodAnalysis(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        EarthlyBranch monthBranch,

        UsefulGodMethodAnalysis methodAnalysis,

        UsefulGodMethodEvidence methodEvidence,

        StructureAnalysis structureAnalysis,

        RemedyAnalysis remedyAnalysis,

        ClimateHealthCrossValidationAnalysis crossValidationAnalysis,

        List<StructureUsefulGodCandidate> candidates,

        boolean followStructureSuppressed,

        boolean specialStructureReview,

        boolean completeChart

) {

    public StructureUsefulGodAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterElement,
                "일간 오행이 필요합니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지가 필요합니다."
        );

        Objects.requireNonNull(
                methodAnalysis,
                "7-1 용신법 분석이 필요합니다."
        );

        Objects.requireNonNull(
                methodEvidence,
                "격국용신법 Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                structureAnalysis,
                "5차 격국 분석이 필요합니다."
        );

        Objects.requireNonNull(
                remedyAnalysis,
                "6-5 병약 분석이 필요합니다."
        );

        Objects.requireNonNull(
                crossValidationAnalysis,
                "6-9 교차검증 분석이 필요합니다."
        );

        candidates =
                candidates == null
                        ? List.of()
                        : List.copyOf(
                        candidates
                );

        if (
                methodEvidence.methodType()
                        != UsefulGodMethodType.STRUCTURE
        ) {

            throw new IllegalArgumentException(
                    "7-4에는 STRUCTURE 용신법 Evidence가 필요합니다."
            );
        }

        if (
                candidates.size()
                        != FiveElement.values().length
        ) {

            throw new IllegalArgumentException(
                    "격국용신 후보에는 木火土金水 전체가 정확히 한 번씩 포함되어야 합니다."
            );
        }

        EnumSet<FiveElement> elements =
                EnumSet.noneOf(
                        FiveElement.class
                );

        for (
                StructureUsefulGodCandidate candidate
                : candidates
        ) {

            if (
                    !elements.add(
                            candidate.element()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일한 격국용신 후보 오행이 중복되었습니다: "
                                + candidate.element()
                );
            }

            if (
                    candidate.methodApplicability()
                            != methodEvidence.applicability()
            ) {

                throw new IllegalArgumentException(
                        "격국용신 후보의 방법 적용도가 7-1 결과와 일치하지 않습니다."
                );
            }
        }

        if (
                methodAnalysis.dayMaster()
                        != dayMaster

                        || structureAnalysis.dayMaster()
                        != dayMaster

                        || remedyAnalysis.dayMaster()
                        != dayMaster

                        || crossValidationAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "7-4 관련 분석의 일간이 서로 일치하지 않습니다."
            );
        }

        if (
                methodAnalysis.dayMasterElement()
                        != dayMasterElement

                        || crossValidationAnalysis.dayMasterElement()
                        != dayMasterElement
        ) {

            throw new IllegalArgumentException(
                    "7-4 관련 분석의 일간 오행이 서로 일치하지 않습니다."
            );
        }

        if (
                methodAnalysis.monthBranch()
                        != monthBranch

                        || structureAnalysis.monthBranch()
                        != monthBranch

                        || remedyAnalysis.monthBranch()
                        != monthBranch

                        || crossValidationAnalysis.monthBranch()
                        != monthBranch
        ) {

            throw new IllegalArgumentException(
                    "7-4 관련 분석의 월지가 서로 일치하지 않습니다."
            );
        }

        if (
                methodAnalysis.completeChart()
                        != completeChart

                        || structureAnalysis.completeChart()
                        != completeChart

                        || remedyAnalysis.completeChart()
                        != completeChart

                        || crossValidationAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "7-4 관련 분석의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }

        if (
                followStructureSuppressed
                        != methodEvidence.suppressedByFollowStructure()
        ) {

            throw new IllegalArgumentException(
                    "종격에 의한 격국용신법 억제 상태가 7-1 결과와 일치하지 않습니다."
            );
        }

        if (
                specialStructureReview
                        != structureAnalysis.primaryIsSpecial()
        ) {

            throw new IllegalArgumentException(
                    "특수격 검토 상태가 실제 주격 유형과 일치하지 않습니다."
            );
        }
    }

    public UsefulGodMethodApplicability methodApplicability() {

        return methodEvidence
                .applicability();
    }

    public List<StructureUsefulGodCandidate> primaryCandidates() {

        return candidates
                .stream()
                .filter(
                        StructureUsefulGodCandidate::primary
                )
                .toList();
    }

    public List<StructureUsefulGodCandidate> secondaryCandidates() {

        return candidates
                .stream()
                .filter(
                        StructureUsefulGodCandidate::secondary
                )
                .toList();
    }

    public List<StructureUsefulGodCandidate> conditionalCandidates() {

        return candidates
                .stream()
                .filter(
                        StructureUsefulGodCandidate::conditional
                )
                .toList();
    }

    public List<StructureUsefulGodCandidate> selectedCandidates() {

        return candidates
                .stream()
                .filter(
                        StructureUsefulGodCandidate::selected
                )
                .toList();
    }

    public List<StructureUsefulGodCandidate> repairCandidates() {

        return candidates
                .stream()
                .filter(
                        StructureUsefulGodCandidate::damageRepairMatch
                )
                .toList();
    }

    public List<StructureUsefulGodCandidate> formationSupportCandidates() {

        return candidates
                .stream()
                .filter(
                        StructureUsefulGodCandidate::formationSupportMatch
                )
                .toList();
    }

    public List<StructureUsefulGodCandidate> damageElements() {

        return candidates
                .stream()
                .filter(
                        StructureUsefulGodCandidate::damageElementMatch
                )
                .toList();
    }

    public Optional<StructureUsefulGodCandidate> candidateOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return Optional.empty();
        }

        return candidates
                .stream()
                .filter(
                        candidate ->
                                candidate.element() == element
                )
                .findFirst();
    }

    public boolean hasPrimaryCandidate() {

        return !primaryCandidates()
                .isEmpty();
    }

    public boolean hasMultiplePrimaryCandidates() {

        return primaryCandidates()
                .size() >= 2;
    }

    public boolean hasRepairCandidate() {

        return repairCandidates()
                .stream()
                .anyMatch(
                        StructureUsefulGodCandidate::candidate
                );
    }

    public boolean hasMixedStructureEvidence() {

        return candidates
                .stream()
                .anyMatch(
                        StructureUsefulGodCandidate::mixedStructureEvidence
                );
    }

    public boolean structureMethodCore() {

        return methodEvidence
                .applicability()
                .core();
    }

    public boolean structureMethodActive() {

        return methodEvidence
                .applicability()
                .applicable();
    }

    public boolean suppressedByFollowStructure() {

        return followStructureSuppressed;
    }

    public int countRole(
            StructureUsefulGodCandidateRole role
    ) {

        if (
                role == null
        ) {

            return 0;
        }

        return (int) candidates
                .stream()
                .filter(
                        candidate ->
                                candidate.role() == role
                )
                .count();
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}