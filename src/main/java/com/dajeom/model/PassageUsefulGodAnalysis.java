package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PassageUsefulGodCandidateRole;
import com.dajeom.type.UsefulGodMethodApplicability;
import com.dajeom.type.UsefulGodMethodType;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 7차-6.
 *
 * 통관용신법으로 木火土金水 전체를
 * 재검증한 결과.
 *
 * =================================================
 *
 * 입력
 *
 * 7-1
 * UsefulGodMethodAnalysis
 *
 * 6-6
 * PassageNeedAnalysis
 *
 * 6-7
 * PassageCandidateAnalysis
 *
 * 6-9
 * ClimateHealthCrossValidationAnalysis
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 6-7 PassageCandidate를
 * 그대로 용신 후보로 복사하지 않는다.
 *
 * 실제:
 *
 * - 통관 필요도
 * - bridge 부족
 * - 일간 직접 불리
 * - 병약 수렴
 * - 조후 수렴
 * - 현재 오행 과다
 * - 신강신약
 * - 격국
 *
 * 을 다시 검증한다.
 */
public record PassageUsefulGodAnalysis(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        EarthlyBranch monthBranch,

        UsefulGodMethodAnalysis methodAnalysis,

        UsefulGodMethodEvidence methodEvidence,

        PassageNeedAnalysis passageNeedAnalysis,

        PassageCandidateAnalysis sourcePassageAnalysis,

        ClimateHealthCrossValidationAnalysis crossValidationAnalysis,

        List<PassageUsefulGodCandidate> candidates,

        boolean completeChart

) {

    public PassageUsefulGodAnalysis {

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
                "통관용신법 Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                passageNeedAnalysis,
                "6-6 통관 필요성 분석이 필요합니다."
        );

        Objects.requireNonNull(
                sourcePassageAnalysis,
                "6-7 통관 후보 분석이 필요합니다."
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
                        != UsefulGodMethodType.PASSAGE
        ) {

            throw new IllegalArgumentException(
                    "7-6에는 PASSAGE 용신법 Evidence가 필요합니다."
            );
        }

        if (
                candidates.size()
                        != FiveElement.values().length
        ) {

            throw new IllegalArgumentException(
                    "통관용신 후보에는 木火土金水 전체가 정확히 한 번씩 포함되어야 합니다."
            );
        }

        EnumSet<FiveElement> elements =
                EnumSet.noneOf(
                        FiveElement.class
                );

        for (
                PassageUsefulGodCandidate candidate
                : candidates
        ) {

            if (
                    !elements.add(
                            candidate.element()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일한 통관용신 후보 오행이 중복되었습니다: "
                                + candidate.element()
                );
            }

            if (
                    candidate.methodApplicability()
                            != methodEvidence.applicability()
            ) {

                throw new IllegalArgumentException(
                        "통관용신 후보의 방법 적용도가 7-1 결과와 일치하지 않습니다."
                );
            }
        }

        if (
                methodAnalysis.dayMaster()
                        != dayMaster

                        || passageNeedAnalysis.dayMaster()
                        != dayMaster

                        || sourcePassageAnalysis.dayMaster()
                        != dayMaster

                        || crossValidationAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "7-6 관련 분석의 일간이 서로 일치하지 않습니다."
            );
        }

        if (
                methodAnalysis.dayMasterElement()
                        != dayMasterElement

                        || passageNeedAnalysis.dayMasterElement()
                        != dayMasterElement

                        || sourcePassageAnalysis.dayMasterElement()
                        != dayMasterElement

                        || crossValidationAnalysis.dayMasterElement()
                        != dayMasterElement
        ) {

            throw new IllegalArgumentException(
                    "7-6 관련 분석의 일간 오행이 서로 일치하지 않습니다."
            );
        }

        if (
                methodAnalysis.monthBranch()
                        != monthBranch

                        || passageNeedAnalysis.monthBranch()
                        != monthBranch

                        || sourcePassageAnalysis.monthBranch()
                        != monthBranch

                        || crossValidationAnalysis.monthBranch()
                        != monthBranch
        ) {

            throw new IllegalArgumentException(
                    "7-6 관련 분석의 월지가 서로 일치하지 않습니다."
            );
        }

        if (
                methodAnalysis.completeChart()
                        != completeChart

                        || passageNeedAnalysis.completeChart()
                        != completeChart

                        || sourcePassageAnalysis.completeChart()
                        != completeChart

                        || crossValidationAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "7-6 관련 분석의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }

        if (
                sourcePassageAnalysis
                        .passageNeedAnalysis()
                        != passageNeedAnalysis
        ) {

            /*
             * 같은 객체가 아니어도 내용이 같을 수 있으나
             * 현재 6차 Engine은 동일 계산 결과를
             * 그대로 전달하도록 설계되어 있다.
             */
            if (
                    sourcePassageAnalysis
                            .passageNeedAnalysis()
                            .overallNeed()
                            != passageNeedAnalysis
                            .overallNeed()
            ) {

                throw new IllegalArgumentException(
                        "6-6과 6-7의 통관 필요성 분석 결과가 일치하지 않습니다."
                );
            }
        }
    }

    public UsefulGodMethodApplicability methodApplicability() {

        return methodEvidence
                .applicability();
    }

    public List<PassageUsefulGodCandidate> primaryCandidates() {

        return candidates
                .stream()
                .filter(
                        PassageUsefulGodCandidate::primary
                )
                .toList();
    }

    public List<PassageUsefulGodCandidate> secondaryCandidates() {

        return candidates
                .stream()
                .filter(
                        PassageUsefulGodCandidate::secondary
                )
                .toList();
    }

    public List<PassageUsefulGodCandidate> conditionalCandidates() {

        return candidates
                .stream()
                .filter(
                        PassageUsefulGodCandidate::conditional
                )
                .toList();
    }

    public List<PassageUsefulGodCandidate> selectedCandidates() {

        return candidates
                .stream()
                .filter(
                        PassageUsefulGodCandidate::selected
                )
                .toList();
    }

    public List<PassageUsefulGodCandidate> highPriorityCandidates() {

        return candidates
                .stream()
                .filter(
                        PassageUsefulGodCandidate::addressesHighPriorityConflict
                )
                .toList();
    }

    public List<PassageUsefulGodCandidate> dayMasterProtectionCandidates() {

        return candidates
                .stream()
                .filter(
                        PassageUsefulGodCandidate::dayMasterDisadvantaged
                )
                .toList();
    }

    public List<PassageUsefulGodCandidate> alignedCandidates() {

        return candidates
                .stream()
                .filter(
                        PassageUsefulGodCandidate::anyCrossMethodAlignment
                )
                .toList();
    }

    public Optional<PassageUsefulGodCandidate> candidateOf(
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

    public boolean passageMethodCore() {

        return methodEvidence
                .applicability()
                .core();
    }

    public boolean passageMethodActive() {

        return methodEvidence
                .applicability()
                .applicable();
    }

    public boolean hasMeaningfulPassageNeed() {

        return passageNeedAnalysis
                .meaningfulPassageRequired();
    }

    public boolean hasHighPriorityPassageNeed() {

        return passageNeedAnalysis
                .highPriorityPassageRequired();
    }

    public boolean hasCrossMethodAlignment() {

        return candidates
                .stream()
                .filter(
                        PassageUsefulGodCandidate::candidate
                )
                .anyMatch(
                        PassageUsefulGodCandidate
                                ::anyCrossMethodAlignment
                );
    }

    public boolean hasDualCrossMethodAlignment() {

        return candidates
                .stream()
                .filter(
                        PassageUsefulGodCandidate::candidate
                )
                .anyMatch(
                        PassageUsefulGodCandidate
                                ::dualCrossMethodAlignment
                );
    }

    /**
     * 실제 의미 있는 통관 필요는 있지만
     * 7-6에서 PRIMARY/SECONDARY가 하나도 없는 상태.
     *
     * 충돌 때문에 전부 CONDITIONAL로 내려간 경우 등이
     * 대표적이다.
     */
    public boolean unresolvedPassageSelection() {

        return hasMeaningfulPassageNeed()
                && selectedCandidates()
                .isEmpty();
    }

    public int countRole(
            PassageUsefulGodCandidateRole role
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