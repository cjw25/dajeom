package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PathologySeverity;
import com.dajeom.type.RemedyUsefulGodCandidateRole;
import com.dajeom.type.UsefulGodMethodApplicability;
import com.dajeom.type.UsefulGodMethodType;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 7차-5.
 *
 * 병약용신법으로 木火土金水 전체를
 * 재검증한 결과.
 *
 * =================================================
 *
 * 입력
 *
 * 7-1
 * UsefulGodMethodAnalysis
 *
 * 6-4
 * PathologyAnalysis
 *
 * 6-5
 * RemedyAnalysis
 *
 * 6-9
 * ClimateHealthCrossValidationAnalysis
 *
 * =================================================
 *
 * 6-5의 약 후보를 그대로 복사하지 않는다.
 *
 * 반드시:
 *
 * - 전체 병 심각도
 * - 의미 있게 해결하는 병 수
 * - 강한 병 직접 대응
 * - 다른 병과의 충돌
 * - 현재 오행 과다
 * - 신강신약
 * - 격국
 * - 미해결 병 구조
 *
 * 를 다시 검증한다.
 */
public record RemedyUsefulGodAnalysis(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        EarthlyBranch monthBranch,

        UsefulGodMethodAnalysis methodAnalysis,

        UsefulGodMethodEvidence methodEvidence,

        PathologyAnalysis pathologyAnalysis,

        RemedyAnalysis sourceRemedyAnalysis,

        ClimateHealthCrossValidationAnalysis crossValidationAnalysis,

        List<RemedyUsefulGodCandidate> candidates,

        boolean unresolvedMeaningfulPathologyPresent,

        boolean completeChart

) {

    public RemedyUsefulGodAnalysis {

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
                "병약용신법 Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                pathologyAnalysis,
                "6-4 병 구조 분석이 필요합니다."
        );

        Objects.requireNonNull(
                sourceRemedyAnalysis,
                "6-5 약 분석이 필요합니다."
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
                        != UsefulGodMethodType.REMEDY
        ) {

            throw new IllegalArgumentException(
                    "7-5에는 REMEDY 용신법 Evidence가 필요합니다."
            );
        }

        if (
                candidates.size()
                        != FiveElement.values().length
        ) {

            throw new IllegalArgumentException(
                    "병약용신 후보에는 木火土金水 전체가 정확히 한 번씩 포함되어야 합니다."
            );
        }

        EnumSet<FiveElement> elements =
                EnumSet.noneOf(
                        FiveElement.class
                );

        for (
                RemedyUsefulGodCandidate candidate
                : candidates
        ) {

            if (
                    !elements.add(
                            candidate.element()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일한 병약용신 후보 오행이 중복되었습니다: "
                                + candidate.element()
                );
            }

            if (
                    candidate.methodApplicability()
                            != methodEvidence.applicability()
            ) {

                throw new IllegalArgumentException(
                        "병약용신 후보의 방법 적용도가 7-1 결과와 일치하지 않습니다."
                );
            }

            if (
                    candidate.overallPathologySeverity()
                            != pathologyAnalysis.overallSeverity()
            ) {

                throw new IllegalArgumentException(
                        "병약용신 후보의 전체 병 심각도가 6-4 결과와 일치하지 않습니다."
                );
            }

            if (
                    candidate.unresolvedMeaningfulPathologyPresent()
                            != unresolvedMeaningfulPathologyPresent
            ) {

                throw new IllegalArgumentException(
                        "미해결 병 구조 상태가 후보 간 일치하지 않습니다."
                );
            }
        }

        if (
                methodAnalysis.dayMaster()
                        != dayMaster

                        || pathologyAnalysis.dayMaster()
                        != dayMaster

                        || sourceRemedyAnalysis.dayMaster()
                        != dayMaster

                        || crossValidationAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "7-5 관련 분석의 일간이 서로 일치하지 않습니다."
            );
        }

        if (
                methodAnalysis.dayMasterElement()
                        != dayMasterElement

                        || crossValidationAnalysis.dayMasterElement()
                        != dayMasterElement
        ) {

            throw new IllegalArgumentException(
                    "7-5 관련 분석의 일간 오행이 서로 일치하지 않습니다."
            );
        }

        if (
                methodAnalysis.monthBranch()
                        != monthBranch

                        || pathologyAnalysis.monthBranch()
                        != monthBranch

                        || sourceRemedyAnalysis.monthBranch()
                        != monthBranch

                        || crossValidationAnalysis.monthBranch()
                        != monthBranch
        ) {

            throw new IllegalArgumentException(
                    "7-5 관련 분석의 월지가 서로 일치하지 않습니다."
            );
        }

        if (
                methodAnalysis.completeChart()
                        != completeChart

                        || pathologyAnalysis.completeChart()
                        != completeChart

                        || sourceRemedyAnalysis.completeChart()
                        != completeChart

                        || crossValidationAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "7-5 관련 분석의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }

        if (
                unresolvedMeaningfulPathologyPresent
                        != sourceRemedyAnalysis
                        .hasUnresolvedMeaningfulPathology()
        ) {

            throw new IllegalArgumentException(
                    "미해결 의미 있는 병 구조 상태가 6-5 결과와 일치하지 않습니다."
            );
        }
    }

    public UsefulGodMethodApplicability methodApplicability() {

        return methodEvidence
                .applicability();
    }

    public PathologySeverity overallPathologySeverity() {

        return pathologyAnalysis
                .overallSeverity();
    }

    public List<RemedyUsefulGodCandidate> primaryCandidates() {

        return candidates
                .stream()
                .filter(
                        RemedyUsefulGodCandidate::primary
                )
                .toList();
    }

    public List<RemedyUsefulGodCandidate> secondaryCandidates() {

        return candidates
                .stream()
                .filter(
                        RemedyUsefulGodCandidate::secondary
                )
                .toList();
    }

    public List<RemedyUsefulGodCandidate> conditionalCandidates() {

        return candidates
                .stream()
                .filter(
                        RemedyUsefulGodCandidate::conditional
                )
                .toList();
    }

    public List<RemedyUsefulGodCandidate> selectedCandidates() {

        return candidates
                .stream()
                .filter(
                        RemedyUsefulGodCandidate::selected
                )
                .toList();
    }

    public List<RemedyUsefulGodCandidate> severePathologyCandidates() {

        return candidates
                .stream()
                .filter(
                        RemedyUsefulGodCandidate::addressesSeverePathology
                )
                .toList();
    }

    public List<RemedyUsefulGodCandidate> multiPathologyCandidates() {

        return candidates
                .stream()
                .filter(
                        RemedyUsefulGodCandidate
                                ::addressesMultipleMeaningfulPathologies
                )
                .toList();
    }

    public Optional<RemedyUsefulGodCandidate> candidateOf(
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

    public boolean hasSeverePathologyCandidate() {

        return severePathologyCandidates()
                .stream()
                .anyMatch(
                        RemedyUsefulGodCandidate::candidate
                );
    }

    public boolean hasMultiPathologyCandidate() {

        return multiPathologyCandidates()
                .stream()
                .anyMatch(
                        RemedyUsefulGodCandidate::candidate
                );
    }

    public boolean hasCandidateConflict() {

        return candidates
                .stream()
                .filter(
                        RemedyUsefulGodCandidate::candidate
                )
                .anyMatch(
                        candidate ->
                                candidate.sourceConflict()
                                        || candidate.crossValidationConflict()
                );
    }

    public boolean remedyMethodCore() {

        return methodEvidence
                .applicability()
                .core();
    }

    public boolean remedyMethodActive() {

        return methodEvidence
                .applicability()
                .applicable();
    }

    public boolean hasUnresolvedMeaningfulPathology() {

        return unresolvedMeaningfulPathologyPresent;
    }

    public int countRole(
            RemedyUsefulGodCandidateRole role
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