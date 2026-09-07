package com.dajeom.domain;

import java.util.Objects;
import java.util.Optional;

/**
 * AI 해석 직전의 원국 명리 통합 프로필.
 *
 * =================================================
 *
 * 중요:
 *
 * 이 객체는 최종 사용자에게 그대로 보여주는 DTO가 아니다.
 *
 * 화면에는:
 *
 * - 사주팔자 4기둥
 * - 오행분포
 * - 장문 풀이
 *
 * 만 보여주고,
 *
 * 이 객체 내부의:
 *
 * - 신강약
 * - 격국
 * - 십성
 * - 통근
 * - 천간합
 * - 합거/보존
 * - 沖刑破害
 * - 六合/三合/三會
 * - 官殺取清
 * - 陽刃
 *
 * 등은 AI 해석용 내부 사실로 사용한다.
 *
 * =================================================
 *
 * AI는 이 객체를 보고 다시 명리 계산을 하면 안 된다.
 *
 * Java가 계산한 결과를
 * 자연어로 설명하는 역할만 맡는다.
 */
public record NatalMyeongriProfile(

        HeavenlyStem dayMaster,

        /*
         * =================================================
         * 일간 강약 최종 routing
         * =================================================
         */
        DayMasterStrengthDecision strengthDecision,

        /*
         * =================================================
         * 격국 최종 routing
         * =================================================
         */
        NatalPatternResolution patternResolution,

        /*
         * =================================================
         * 월령 + 십성 기본 구조
         * =================================================
         */
        PatternInteractionAnalysis patternInteraction,

        PatternTenGodFoundationProfile foundations,

        PatternTenGodRouteAnalysis routes,

        /*
         * =================================================
         * 天干五合
         * =================================================
         */
        PatternStemCombinationActivationAnalysis stemCombinationActivation,

        PatternStemCombinationPreservationAnalysis stemCombinationPreservation,

        /*
         * =================================================
         * 地支 沖刑破害 / 合會
         * =================================================
         */
        PatternBranchDisruptionAnalysis branchDisruptions,

        PatternBranchCombinationAnalysis branchCombinations,

        PatternBranchDisruptionResolutionAnalysis branchDisruptionResolutions,

        PatternMonthCommandDisruptionResolutionAnalysis
        monthCommandDisruptionResolution,

        /*
         * =================================================
         * 財印
         * =================================================
         */
        PatternWealthResourceInteractionAnalysis wealthResourceInteraction,

        /*
         * =================================================
         * 강약 민감 구조
         * =================================================
         */
        PatternStrengthSensitiveContextAnalysis strengthSensitiveContext,

        /*
         * =================================================
         * 官殺取清
         * =================================================
         */
        PatternOfficerKillingsPurificationAnalysis
        officerKillingsPurification,

        /*
         * =================================================
         * 陽刃
         * =================================================
         */
        PatternYangBladeAnalysis yangBlade,

        PatternYangBladeKillingCombinationAnalysis
        yangBladeKillingCombination

) {

    public NatalMyeongriProfile {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                strengthDecision,
                "DayMasterStrengthDecision은 필수입니다."
        );

        Objects.requireNonNull(
                patternResolution,
                "NatalPatternResolution은 필수입니다."
        );

        Objects.requireNonNull(
                patternInteraction,
                "PatternInteractionAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                foundations,
                "PatternTenGodFoundationProfile은 필수입니다."
        );

        Objects.requireNonNull(
                routes,
                "PatternTenGodRouteAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                stemCombinationActivation,
                "PatternStemCombinationActivationAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                stemCombinationPreservation,
                "PatternStemCombinationPreservationAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                branchDisruptions,
                "PatternBranchDisruptionAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                branchCombinations,
                "PatternBranchCombinationAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                branchDisruptionResolutions,
                "PatternBranchDisruptionResolutionAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                monthCommandDisruptionResolution,
                "PatternMonthCommandDisruptionResolutionAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                wealthResourceInteraction,
                "PatternWealthResourceInteractionAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                strengthSensitiveContext,
                "PatternStrengthSensitiveContextAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                officerKillingsPurification,
                "PatternOfficerKillingsPurificationAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                yangBlade,
                "PatternYangBladeAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                yangBladeKillingCombination,
                "PatternYangBladeKillingCombinationAnalysis는 필수입니다."
        );

        /*
         * =================================================
         * 일간 일관성 검증
         * =================================================
         */

        validateDayMaster(
                dayMaster,
                strengthDecision.dayMaster(),
                "strengthDecision"
        );

        validateDayMaster(
                dayMaster,
                patternInteraction.dayMaster(),
                "patternInteraction"
        );

        validateDayMaster(
                dayMaster,
                foundations.dayMaster(),
                "foundations"
        );

        validateDayMaster(
                dayMaster,
                routes.dayMaster(),
                "routes"
        );

        validateDayMaster(
                dayMaster,
                stemCombinationActivation.dayMaster(),
                "stemCombinationActivation"
        );

        validateDayMaster(
                dayMaster,
                stemCombinationPreservation.dayMaster(),
                "stemCombinationPreservation"
        );

        validateDayMaster(
                dayMaster,
                wealthResourceInteraction.dayMaster(),
                "wealthResourceInteraction"
        );

        validateDayMaster(
                dayMaster,
                strengthSensitiveContext.dayMaster(),
                "strengthSensitiveContext"
        );

        validateDayMaster(
                dayMaster,
                officerKillingsPurification.dayMaster(),
                "officerKillingsPurification"
        );

        validateDayMaster(
                dayMaster,
                yangBlade.dayMaster(),
                "yangBlade"
        );

        validateDayMaster(
                dayMaster,
                yangBladeKillingCombination.dayMaster(),
                "yangBladeKillingCombination"
        );

        validatePatternResolutionDayMaster(
                dayMaster,
                patternResolution
        );

        /*
         * =================================================
         * 월령 일관성 검증
         * =================================================
         */

        EarthlyBranch monthBranch =
                patternInteraction
                        .patternSelection()
                        .monthBranch();

        if (
                branchDisruptions.monthBranch()
                        != monthBranch
        ) {

            throw new IllegalArgumentException(
                    "PatternInteractionAnalysis와 BranchDisruptionAnalysis의 월지가 일치하지 않습니다."
            );
        }

        if (
                branchCombinations.monthBranch()
                        != monthBranch
        ) {

            throw new IllegalArgumentException(
                    "PatternInteractionAnalysis와 BranchCombinationAnalysis의 월지가 일치하지 않습니다."
            );
        }

        if (
                monthCommandDisruptionResolution.monthBranch()
                        != monthBranch
        ) {

            throw new IllegalArgumentException(
                    "PatternInteractionAnalysis와 MonthCommandDisruptionResolution의 월지가 일치하지 않습니다."
            );
        }
    }

    /*
     * =================================================
     * 기본
     * =================================================
     */

    public EarthlyBranch monthBranch() {

        return patternInteraction
                .patternSelection()
                .monthBranch();
    }

    /*
     * =================================================
     * 강약 routing
     * =================================================
     */

    public DayMasterStrengthDecisionPath strengthDecisionPath() {

        return strengthDecision.path();
    }

    public boolean trueTransformationStructure() {

        return strengthDecision.path()
                == DayMasterStrengthDecisionPath
                .TRUE_TRANSFORMATION;
    }

    public boolean followingStructureEstablished() {

        return strengthDecision.path()
                == DayMasterStrengthDecisionPath
                .FOLLOWING_STRUCTURE_ESTABLISHED;
    }

    public boolean generalStrengthUsed() {

        return strengthDecision.path()
                == DayMasterStrengthDecisionPath
                .GENERAL_STRENGTH

                ||

                strengthDecision.path()
                        == DayMasterStrengthDecisionPath
                        .GENERAL_STRENGTH_WITH_SPECIAL_CONTEXT;
    }

    public boolean specialStrengthContextExists() {

        return strengthDecision.path()
                == DayMasterStrengthDecisionPath
                .GENERAL_STRENGTH_WITH_SPECIAL_CONTEXT;
    }

    /*
     * =================================================
     * 격국
     * =================================================
     */

    public boolean patternDecided() {

        return patternResolution.decided();
    }

    public Optional<NatalPatternDecision> patternDecision() {

        return patternResolution.decision();
    }

    public Optional<NatalPatternReview> patternReview() {

        return patternResolution.review();
    }

    public boolean patternReviewRequired() {

        return patternResolution.reviewRequired();
    }

    public Optional<NatalPatternType> decidedPatternType() {

        return patternResolution
                .decision()
                .map(
                        NatalPatternDecision
                                ::patternType
                );
    }

    public Optional<NatalPatternDecisionStatus> decidedPatternStatus() {

        return patternResolution
                .decision()
                .map(
                        NatalPatternDecision
                                ::status
                );
    }

    /*
     * =================================================
     * 월령 교란
     * =================================================
     */

    public boolean monthCommandDisrupted() {

        return branchDisruptions
                .monthCommandDisrupted();
    }

    public boolean monthCommandDisruptionResolved() {

        return monthCommandDisruptionResolution
                .usableAsResolvedByCombination();
    }

    public boolean monthCommandDisruptionReviewRequired() {

        return monthCommandDisruptionResolution
                .reviewRequired();
    }

    /*
     * =================================================
     * 天干合
     * =================================================
     */

    public boolean hasActiveNonDayMasterStemCombination() {

        return stemCombinationActivation
                .hasActiveNonDayMasterCombination();
    }

    public boolean officerKillingsClearedByStemCombination() {

        return stemCombinationPreservation
                .officerKillingsMixtureCleared();
    }

    public boolean sevenKillingsClearedPreservingWealth() {

        return stemCombinationPreservation
                .sevenKillingsClearedPreservingWealth();
    }

    public boolean wealthClearedPreservingResource() {

        return stemCombinationPreservation
                .wealthClearedPreservingResource();
    }

    /*
     * =================================================
     * 官殺
     * =================================================
     */

    public boolean officerKillingsMixed() {

        return officerKillingsPurification
                .mixed();
    }

    public boolean officerKillingsMixtureCleared() {

        return officerKillingsPurification
                .mixtureCleared()

                ||

                stemCombinationPreservation
                        .officerKillingsMixtureCleared();
    }

    public boolean officerKillingsReviewRequired() {

        return officerKillingsPurification
                .reviewRequired();
    }

    /*
     * =================================================
     * 財印
     * =================================================
     */

    public boolean wealthResourceDirectConflictActive() {

        return wealthResourceInteraction
                .resourcePatternConflictActive();
    }

    public boolean wealthResourceMutuallyNonObstructing() {

        return wealthResourceInteraction
                .visibleWealthAndResourceMutuallyNonObstructing();
    }

    /*
     * =================================================
     * 강약 민감 구조
     * =================================================
     */

    public boolean excessiveResourceContext() {

        return strengthSensitiveContext
                .excessiveResourceContext();
    }

    public boolean wealthModeratesExcessResource() {

        return strengthSensitiveContext
                .wealthModeratesExcessResource();
    }

    public boolean outputDrainsWeakStructure() {

        return strengthSensitiveContext
                .outputDrainWeakStructureActive();
    }

    public boolean hurtingOfficerTooLightForResourceRoute() {

        return strengthSensitiveContext
                .hurtingOfficerTooLightForResourceRoute();
    }

    /*
     * =================================================
     * 陽刃
     * =================================================
     */

    public boolean hasYangBlade() {

        return yangBlade
                .hasYangBlade();
    }

    public boolean hasMonthYangBlade() {

        return yangBlade
                .hasMonthBlade();
    }

    public boolean hasActiveYangBladeKillingCombination() {

        return yangBladeKillingCombination
                .hasActiveCombination();
    }

    /*
     * =================================================
     * validation
     * =================================================
     */

    private static void validateDayMaster(

            HeavenlyStem expected,

            HeavenlyStem actual,

            String sourceName

    ) {

        if (expected != actual) {

            throw new IllegalArgumentException(
                    sourceName
                            +
                            "의 일간이 NatalMyeongriProfile의 일간과 일치하지 않습니다."
            );
        }
    }

    private static void validatePatternResolutionDayMaster(

            HeavenlyStem expected,

            NatalPatternResolution resolution

    ) {

        resolution
                .decision()
                .ifPresent(
                        decision ->
                                validateDayMaster(
                                        expected,
                                        decision.dayMaster(),
                                        "NatalPatternDecision"
                                )
                );

        resolution
                .review()
                .ifPresent(
                        review ->
                                validateDayMaster(
                                        expected,
                                        review.dayMaster(),
                                        "NatalPatternReview"
                                )
                );
    }
}