package com.dajeom.model;

import com.dajeom.type.ClimateUsefulGodCandidateRole;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.UsefulGodMethodApplicability;
import com.dajeom.type.UsefulGodMethodType;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 7차-3.
 *
 * 조후용신법으로 木火土金水 전체를
 * 재검증한 결과.
 *
 * =================================================
 *
 * 입력 근거
 *
 * 7-1
 * UsefulGodMethodAnalysis
 *
 * 6-2
 * ClimateAdjustmentNeedAnalysis
 *
 * 6-3
 * ClimateElementCandidateAnalysis
 *
 * 6-9
 * ClimateHealthCrossValidationAnalysis
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 6-3 PRIMARY를
 * 그대로 7-3 PRIMARY로 복사하지 않는다.
 *
 * 반드시:
 *
 * - 조후용신법 적용도
 * - 조후 충돌
 * - 기존 오행량
 * - 신강신약
 * - 격국
 *
 * 을 다시 확인한다.
 */
public record ClimateUsefulGodAnalysis(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        EarthlyBranch monthBranch,

        UsefulGodMethodAnalysis methodAnalysis,

        UsefulGodMethodEvidence methodEvidence,

        ClimateAdjustmentNeedAnalysis climateNeedAnalysis,

        ClimateElementCandidateAnalysis sourceClimateAnalysis,

        ClimateHealthCrossValidationAnalysis crossValidationAnalysis,

        List<ClimateUsefulGodCandidate> candidates,

        boolean completeChart

) {

    public ClimateUsefulGodAnalysis {

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
                "조후용신법 Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                climateNeedAnalysis,
                "6-2 조후 필요성 분석이 필요합니다."
        );

        Objects.requireNonNull(
                sourceClimateAnalysis,
                "6-3 조후 후보 분석이 필요합니다."
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
                        != UsefulGodMethodType.CLIMATE_CONTROL
        ) {

            throw new IllegalArgumentException(
                    "7-3에는 CLIMATE_CONTROL 용신법 Evidence가 필요합니다."
            );
        }

        if (
                candidates.size()
                        != FiveElement
                        .values()
                        .length
        ) {

            throw new IllegalArgumentException(
                    "조후용신 후보에는 木火土金水 전체가 정확히 한 번씩 포함되어야 합니다."
            );
        }

        EnumSet<FiveElement> elements =
                EnumSet.noneOf(
                        FiveElement.class
                );

        for (
                ClimateUsefulGodCandidate candidate
                : candidates
        ) {

            if (
                    !elements.add(
                            candidate.element()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일한 조후용신 후보 오행이 중복되었습니다: "
                                + candidate.element()
                );
            }

            if (
                    candidate.methodApplicability()
                            != methodEvidence.applicability()
            ) {

                throw new IllegalArgumentException(
                        "조후용신 후보의 방법 적용도가 7-1 결과와 일치하지 않습니다."
                );
            }
        }

        if (
                methodAnalysis.dayMaster()
                        != dayMaster

                        || climateNeedAnalysis.dayMaster()
                        != dayMaster

                        || sourceClimateAnalysis.dayMaster()
                        != dayMaster

                        || crossValidationAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "7-3 관련 분석의 일간이 서로 일치하지 않습니다."
            );
        }

        if (
                methodAnalysis.dayMasterElement()
                        != dayMasterElement

                        || crossValidationAnalysis.dayMasterElement()
                        != dayMasterElement
        ) {

            throw new IllegalArgumentException(
                    "7-3 관련 분석의 일간 오행이 서로 일치하지 않습니다."
            );
        }

        if (
                methodAnalysis.monthBranch()
                        != monthBranch

                        || climateNeedAnalysis.monthBranch()
                        != monthBranch

                        || sourceClimateAnalysis.monthBranch()
                        != monthBranch

                        || crossValidationAnalysis.monthBranch()
                        != monthBranch
        ) {

            throw new IllegalArgumentException(
                    "7-3 관련 분석의 월지가 서로 일치하지 않습니다."
            );
        }

        if (
                methodAnalysis.completeChart()
                        != completeChart

                        || climateNeedAnalysis.completeChart()
                        != completeChart

                        || sourceClimateAnalysis.completeChart()
                        != completeChart

                        || crossValidationAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "7-3 관련 분석의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }
    }

    public UsefulGodMethodApplicability
    methodApplicability() {

        return methodEvidence
                .applicability();
    }

    public List<ClimateUsefulGodCandidate>
    primaryCandidates() {

        return candidates
                .stream()
                .filter(
                        ClimateUsefulGodCandidate
                                ::primary
                )
                .toList();
    }

    public List<ClimateUsefulGodCandidate>
    secondaryCandidates() {

        return candidates
                .stream()
                .filter(
                        ClimateUsefulGodCandidate
                                ::secondary
                )
                .toList();
    }

    public List<ClimateUsefulGodCandidate>
    conditionalCandidates() {

        return candidates
                .stream()
                .filter(
                        ClimateUsefulGodCandidate
                                ::conditional
                )
                .toList();
    }

    public List<ClimateUsefulGodCandidate>
    selectedCandidates() {

        return candidates
                .stream()
                .filter(
                        ClimateUsefulGodCandidate
                                ::selected
                )
                .toList();
    }

    public Optional<ClimateUsefulGodCandidate>
    candidateOf(
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
                                candidate.element()
                                        == element
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

    public boolean competingClimate() {

        return climateNeedAnalysis
                .correctionConflict();
    }

    public boolean alignedClimate() {

        return climateNeedAnalysis
                .correctionAligned();
    }

    public boolean climateMethodCore() {

        return methodEvidence
                .applicability()
                .core();
    }

    public boolean climateMethodActive() {

        return methodEvidence
                .applicability()
                .applicable();
    }

    public boolean hasCrossValidationConflict() {

        return candidates
                .stream()
                .filter(
                        ClimateUsefulGodCandidate
                                ::candidate
                )
                .anyMatch(
                        ClimateUsefulGodCandidate
                                ::crossValidationConflict
                );
    }

    public int countRole(
            ClimateUsefulGodCandidateRole role
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
                                candidate.role()
                                        == role
                )
                .count();
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}