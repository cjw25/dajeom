package com.dajeom.model;

import com.dajeom.type.ClimateHealthCandidateRole;
import com.dajeom.type.ClimateHealthStatus;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 6차 최종 분석 결과.
 *
 * =================================================
 *
 * 포함되는 전체 분석
 *
 * 6-1 ClimateEnvironmentAnalysis
 *
 * 6-2 ClimateAdjustmentNeedAnalysis
 *
 * 6-3 ClimateElementCandidateAnalysis
 *
 * 6-4 PathologyAnalysis
 *
 * 6-5 RemedyAnalysis
 *
 * 6-6 PassageNeedAnalysis
 *
 * 6-7 PassageCandidateAnalysis
 *
 * 6-8 ClimateHealthIntegrationAnalysis
 *
 * 6-9 ClimateHealthCrossValidationAnalysis
 *
 * 6-10 ClimateHealthStatus
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 이 객체의 PRIMARY 후보는
 * "6차에서 살아남은 핵심 오행 후보"다.
 *
 * 아직 최종 용신이 아니다.
 *
 * 실제 용신·희신·기신 결정은
 * 정밀화 7차의 책임이다.
 */
public record ClimateHealthAnalysis(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        EarthlyBranch monthBranch,

        ClimateHealthStatus status,

        ClimateEnvironmentAnalysis
        climateEnvironmentAnalysis,

        ClimateAdjustmentNeedAnalysis
        climateAdjustmentNeedAnalysis,

        ClimateElementCandidateAnalysis
        climateElementCandidateAnalysis,

        PathologyAnalysis
        pathologyAnalysis,

        RemedyAnalysis
        remedyAnalysis,

        PassageNeedAnalysis
        passageNeedAnalysis,

        PassageCandidateAnalysis
        passageCandidateAnalysis,

        ClimateHealthIntegrationAnalysis
        integrationAnalysis,

        ClimateHealthCrossValidationAnalysis
        crossValidationAnalysis,

        boolean completeChart

) {

    public ClimateHealthAnalysis {

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
                status,
                "6차 최종 상태가 필요합니다."
        );

        Objects.requireNonNull(
                climateEnvironmentAnalysis,
                "6-1 한난조습 환경 분석이 필요합니다."
        );

        Objects.requireNonNull(
                climateAdjustmentNeedAnalysis,
                "6-2 조후 필요성 분석이 필요합니다."
        );

        Objects.requireNonNull(
                climateElementCandidateAnalysis,
                "6-3 조후 오행 후보 분석이 필요합니다."
        );

        Objects.requireNonNull(
                pathologyAnalysis,
                "6-4 병 구조 분석이 필요합니다."
        );

        Objects.requireNonNull(
                remedyAnalysis,
                "6-5 약 후보 분석이 필요합니다."
        );

        Objects.requireNonNull(
                passageNeedAnalysis,
                "6-6 통관 필요성 분석이 필요합니다."
        );

        Objects.requireNonNull(
                passageCandidateAnalysis,
                "6-7 통관 후보 분석이 필요합니다."
        );

        Objects.requireNonNull(
                integrationAnalysis,
                "6-8 통합 분석이 필요합니다."
        );

        Objects.requireNonNull(
                crossValidationAnalysis,
                "6-9 교차검증 분석이 필요합니다."
        );

        validateIdentityConsistency(
                dayMaster,
                dayMasterElement,
                monthBranch,
                climateEnvironmentAnalysis,
                climateAdjustmentNeedAnalysis,
                climateElementCandidateAnalysis,
                pathologyAnalysis,
                remedyAnalysis,
                passageNeedAnalysis,
                passageCandidateAnalysis,
                integrationAnalysis,
                crossValidationAnalysis
        );

        validateCompleteChartConsistency(
                completeChart,
                climateEnvironmentAnalysis,
                climateAdjustmentNeedAnalysis,
                climateElementCandidateAnalysis,
                pathologyAnalysis,
                remedyAnalysis,
                passageNeedAnalysis,
                passageCandidateAnalysis,
                integrationAnalysis,
                crossValidationAnalysis
        );
    }

    private static void validateIdentityConsistency(

            HeavenlyStem dayMaster,

            FiveElement dayMasterElement,

            EarthlyBranch monthBranch,

            ClimateEnvironmentAnalysis climateEnvironment,

            ClimateAdjustmentNeedAnalysis climateNeed,

            ClimateElementCandidateAnalysis climateCandidates,

            PathologyAnalysis pathology,

            RemedyAnalysis remedy,

            PassageNeedAnalysis passageNeed,

            PassageCandidateAnalysis passageCandidates,

            ClimateHealthIntegrationAnalysis integration,

            ClimateHealthCrossValidationAnalysis validation

    ) {

        /*
         * =================================================
         * 일간
         * =================================================
         */
        if (
                climateEnvironment.dayMaster()
                        != dayMaster

                        || climateNeed.dayMaster()
                        != dayMaster

                        || climateCandidates.dayMaster()
                        != dayMaster

                        || pathology.dayMaster()
                        != dayMaster

                        || remedy.dayMaster()
                        != dayMaster

                        || passageNeed.dayMaster()
                        != dayMaster

                        || passageCandidates.dayMaster()
                        != dayMaster

                        || integration.dayMaster()
                        != dayMaster

                        || validation.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "6차 분석 단계들의 일간이 서로 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 일간 오행
         * =================================================
         */
        if (
                passageNeed.dayMasterElement()
                        != dayMasterElement

                        || passageCandidates.dayMasterElement()
                        != dayMasterElement

                        || integration.dayMasterElement()
                        != dayMasterElement

                        || validation.dayMasterElement()
                        != dayMasterElement
        ) {

            throw new IllegalArgumentException(
                    "6차 분석 단계들의 일간 오행이 서로 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 월지
         * =================================================
         */
        if (
                climateEnvironment.monthBranch()
                        != monthBranch

                        || climateNeed.monthBranch()
                        != monthBranch

                        || climateCandidates.monthBranch()
                        != monthBranch

                        || pathology.monthBranch()
                        != monthBranch

                        || remedy.monthBranch()
                        != monthBranch

                        || passageNeed.monthBranch()
                        != monthBranch

                        || passageCandidates.monthBranch()
                        != monthBranch

                        || integration.monthBranch()
                        != monthBranch

                        || validation.monthBranch()
                        != monthBranch
        ) {

            throw new IllegalArgumentException(
                    "6차 분석 단계들의 월지가 서로 일치하지 않습니다."
            );
        }
    }

    private static void validateCompleteChartConsistency(

            boolean completeChart,

            ClimateEnvironmentAnalysis climateEnvironment,

            ClimateAdjustmentNeedAnalysis climateNeed,

            ClimateElementCandidateAnalysis climateCandidates,

            PathologyAnalysis pathology,

            RemedyAnalysis remedy,

            PassageNeedAnalysis passageNeed,

            PassageCandidateAnalysis passageCandidates,

            ClimateHealthIntegrationAnalysis integration,

            ClimateHealthCrossValidationAnalysis validation

    ) {

        if (
                climateEnvironment.completeChart()
                        != completeChart

                        || climateNeed.completeChart()
                        != completeChart

                        || climateCandidates.completeChart()
                        != completeChart

                        || pathology.completeChart()
                        != completeChart

                        || remedy.completeChart()
                        != completeChart

                        || passageNeed.completeChart()
                        != completeChart

                        || passageCandidates.completeChart()
                        != completeChart

                        || integration.completeChart()
                        != completeChart

                        || validation.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "6차 분석 단계들의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }
    }

    /**
     * 5차 격국 분석.
     *
     * 6-9 안에 이미 보존되어 있으므로
     * 중복 필드로 다시 저장하지 않는다.
     */
    public StructureAnalysis structureAnalysis() {

        return crossValidationAnalysis
                .structureAnalysis();
    }

    public List<ClimateHealthValidatedCandidate>
    candidates() {

        return crossValidationAnalysis
                .candidates();
    }

    public List<ClimateHealthValidatedCandidate>
    primaryCandidates() {

        return crossValidationAnalysis
                .primaryCandidates();
    }

    public List<ClimateHealthValidatedCandidate>
    secondaryCandidates() {

        return crossValidationAnalysis
                .secondaryCandidates();
    }

    public List<ClimateHealthValidatedCandidate>
    deferredCandidates() {

        return crossValidationAnalysis
                .deferredCandidates();
    }

    public List<ClimateHealthValidatedCandidate>
    rejectedCandidates() {

        return crossValidationAnalysis
                .rejectedCandidates();
    }

    public List<ClimateHealthValidatedCandidate>
    selectedCandidates() {

        return candidates()
                .stream()
                .filter(
                        ClimateHealthValidatedCandidate
                                ::selected
                )
                .toList();
    }

    public Optional<ClimateHealthValidatedCandidate>
    candidateOf(
            FiveElement element
    ) {

        return crossValidationAnalysis
                .candidateOf(
                        element
                );
    }

    /**
     * PRIMARY가 정확히 하나일 때만 반환.
     *
     * 복수 PRIMARY를 억지로 하나로 고르지 않는다.
     */
    public Optional<ClimateHealthValidatedCandidate>
    singlePrimaryCandidate() {

        List<ClimateHealthValidatedCandidate> primary =
                primaryCandidates();

        if (
                primary.size() != 1
        ) {

            return Optional.empty();
        }

        return Optional.of(
                primary.get(
                        0
                )
        );
    }

    public Optional<FiveElement>
    singlePrimaryElement() {

        return singlePrimaryCandidate()
                .map(
                        ClimateHealthValidatedCandidate
                                ::element
                );
    }

    public boolean hasSinglePrimary() {

        return status
                .singlePrimary();
    }

    public boolean hasMultiplePrimary() {

        return status
                .multiplePrimary();
    }

    public boolean hasPrimaryCandidate() {

        return status
                .hasPrimary();
    }

    public boolean hasSecondaryOnly() {

        return status
                .secondaryOnly();
    }

    public boolean deferredOnly() {

        return status
                .deferredOnly();
    }

    public boolean inactive() {

        return status
                .inactive();
    }

    /**
     * 7차에서 후보 간 추가 선택 작업이
     * 필요한 상태인가.
     */
    public boolean requiresFurtherSelection() {

        return status
                .requiresFurtherSelection();
    }

    public boolean hasCrossValidationContradiction() {

        return crossValidationAnalysis
                .hasContradiction();
    }

    public boolean hasDowngradedCandidate() {

        return crossValidationAnalysis
                .hasDowngrade();
    }

    public boolean primaryLostAfterValidation() {

        return crossValidationAnalysis
                .primaryLostAfterValidation();
    }

    public boolean hasPathology() {

        return pathologyAnalysis
                .hasPathology();
    }

    public boolean hasMeaningfulPathology() {

        return pathologyAnalysis
                .hasMeaningfulPathology();
    }

    public boolean hasUnresolvedPathology() {

        return remedyAnalysis
                .hasUnresolvedPathology();
    }

    public boolean hasUnresolvedMeaningfulPathology() {

        return remedyAnalysis
                .hasUnresolvedMeaningfulPathology();
    }

    public boolean climateAdjustmentRequired() {

        return climateAdjustmentNeedAnalysis
                .adjustmentRequired();
    }

    public boolean passageRequired() {

        return passageNeedAnalysis
                .passageRequired();
    }

    public boolean meaningfulPassageRequired() {

        return passageNeedAnalysis
                .meaningfulPassageRequired();
    }

    public boolean integrationConflict() {

        return integrationAnalysis
                .hasConflict();
    }

    /**
     * 일반 내격이 아닌 구조에 대한
     * 추가 검토가 필요한가.
     */
    public boolean requiresSpecialStructureReview() {

        return structureAnalysis()
                .requiresSpecialReview();
    }

    /**
     * 7차에서 특별히 주의해서
     * 후보를 결정해야 하는 상태.
     */
    public boolean requiresReview() {

        return requiresFurtherSelection()
                || hasCrossValidationContradiction()
                || integrationConflict()
                || hasUnresolvedMeaningfulPathology()
                || requiresSpecialStructureReview();
    }

    public int countRole(
            ClimateHealthCandidateRole role
    ) {

        return crossValidationAnalysis
                .countRole(
                        role
                );
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}