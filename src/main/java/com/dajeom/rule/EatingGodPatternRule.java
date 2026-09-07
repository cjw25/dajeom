package com.dajeom.rule;

import com.dajeom.domain.DayMasterStrengthAnalysis;
import com.dajeom.domain.EatingGodPatternReason;
import com.dajeom.domain.EatingGodPatternStructuralSignals;
import com.dajeom.domain.MonthCommandPatternCandidate;
import com.dajeom.domain.MonthCommandPatternCandidateType;
import com.dajeom.domain.PatternAssessment;
import com.dajeom.domain.PatternInteractionAnalysis;
import com.dajeom.domain.PatternStatus;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Objects;

/**
 * 食神格 成敗 Rule.
 *
 * =================================================
 *
 * 주요 成格:
 *
 * 1. 食神生財
 *
 * 2. 食帶煞而無財
 *
 * 3. 棄食就煞而透印
 *
 * =================================================
 *
 * 주요 破格:
 *
 * 1. 食神逢梟
 *
 * 2. 生財露煞
 *
 * =================================================
 *
 * 救應:
 *
 * 1. 食逢梟
 *    → 生財護食
 *
 * 2. 食逢梟
 *    → 就煞成格
 *
 * 3. 食神制殺
 *
 * 4. 食神合殺存財
 *
 * =================================================
 *
 * 成中有敗:
 *
 * 食神帶煞印而又逢財
 *
 * =================================================
 *
 * 숫자 / 가중치 / 다수결 없음.
 */
@Component
public class EatingGodPatternRule {

    public PatternAssessment<EatingGodPatternReason> assess(

            MonthCommandPatternCandidate selectedPattern,

            PatternInteractionAnalysis interaction,

            DayMasterStrengthAnalysis strength,

            EatingGodPatternStructuralSignals signals

    ) {

        Objects.requireNonNull(
                selectedPattern,
                "선택된 격국 후보는 필수입니다."
        );

        Objects.requireNonNull(
                interaction,
                "격국 Interaction 분석은 필수입니다."
        );

        Objects.requireNonNull(
                strength,
                "일간 왕쇠 분석은 필수입니다."
        );

        Objects.requireNonNull(
                signals,
                "食神格 구조 신호는 필수입니다."
        );

        validate(
                selectedPattern,
                interaction,
                strength
        );

        EnumSet<EatingGodPatternReason> reasons =
                EnumSet.of(
                        EatingGodPatternReason
                                .EATING_GOD_PATTERN
                );

        /*
         * =================================================
         * 1. 食神 자체
         * =================================================
         */

        if (selectedPattern.exposed()) {

            reasons.add(
                    EatingGodPatternReason
                            .EATING_GOD_EXPOSED
            );

        } else {

            reasons.add(
                    EatingGodPatternReason
                            .EATING_GOD_NOT_EXPOSED
            );
        }

        if (signals.eatingGodFoundationStable()) {

            reasons.add(
                    EatingGodPatternReason
                            .EATING_GOD_FOUNDATION_STABLE
            );

        } else {

            reasons.add(
                    EatingGodPatternReason
                            .EATING_GOD_FOUNDATION_REQUIRES_REVIEW
            );
        }

        /*
         * =================================================
         * 2. 일간 왕쇠
         * =================================================
         */

        boolean strongSide =
                strength.clearlyStrong()
                        ||
                        strength.strongLeaning();

        boolean weakSide =
                strength.clearlyWeak()
                        ||
                        strength.weakLeaning();

        boolean mixedStrength =
                strength.mixed();

        if (strongSide) {

            reasons.add(
                    EatingGodPatternReason
                            .DAY_MASTER_STRONG_SIDE
            );
        }

        if (weakSide) {

            reasons.add(
                    EatingGodPatternReason
                            .DAY_MASTER_WEAK_SIDE
            );
        }

        if (mixedStrength) {

            reasons.add(
                    EatingGodPatternReason
                            .DAY_MASTER_MIXED_STRENGTH
            );
        }

        /*
         * =================================================
         * 3. 원국 십성
         * =================================================
         */

        boolean wealthPresent =
                interaction.hasDirectWealth()
                        ||
                        interaction.hasIndirectWealth();

        boolean wealthExposed =
                interaction.wealthExposed();

        boolean sevenKillingsPresent =
                interaction.hasSevenKillings();

        boolean sevenKillingsExposed =
                interaction.sevenKillingsExposed();

        boolean directOfficerExposed =
                interaction.directOfficerExposed();

        boolean resourceExposed =
                interaction.resourceExposed();

        boolean indirectResourcePresent =
                interaction.hasIndirectResource();

        boolean indirectResourceExposed =
                interaction.hasVisible(
                        MonthCommandPatternCandidateType
                                .INDIRECT_RESOURCE
                );

        if (wealthPresent) {

            reasons.add(
                    EatingGodPatternReason
                            .WEALTH_PRESENT
            );
        }

        if (wealthExposed) {

            reasons.add(
                    EatingGodPatternReason
                            .WEALTH_EXPOSED
            );
        }

        if (sevenKillingsPresent) {

            reasons.add(
                    EatingGodPatternReason
                            .SEVEN_KILLINGS_PRESENT
            );
        }

        if (sevenKillingsExposed) {

            reasons.add(
                    EatingGodPatternReason
                            .SEVEN_KILLINGS_EXPOSED
            );
        }

        if (directOfficerExposed) {

            reasons.add(
                    EatingGodPatternReason
                            .DIRECT_OFFICER_EXPOSED
            );
        }

        if (resourceExposed) {

            reasons.add(
                    EatingGodPatternReason
                            .RESOURCE_EXPOSED
            );
        }

        if (indirectResourcePresent) {

            reasons.add(
                    EatingGodPatternReason
                            .INDIRECT_RESOURCE_PRESENT
            );
        }

        if (indirectResourceExposed) {

            reasons.add(
                    EatingGodPatternReason
                            .INDIRECT_RESOURCE_EXPOSED
            );
        }

        /*
         * =================================================
         * 4. 食神生財
         * =================================================
         *
         * 財 존재만으로 확정하지 않는다.
         *
         * - 財 투출
         * - 財 기반
         * - 身이 감당 가능한 구조
         *
         * 를 함께 본다.
         */

        boolean wealthFoundationStable =
                wealthExposed
                        &&
                        signals
                                .wealthFoundationStable();

        if (wealthFoundationStable) {

            reasons.add(
                    EatingGodPatternReason
                            .WEALTH_FOUNDATION_STABLE
            );
        }

        if (
                wealthExposed
                        &&
                        !signals
                                .wealthFoundationStable()
        ) {

            reasons.add(
                    EatingGodPatternReason
                            .WEALTH_FOUNDATION_REQUIRES_REVIEW
            );
        }

        boolean foodGeneratesWealthRoute =
                wealthExposed
                        &&
                        wealthFoundationStable
                        &&
                        strongSide;

        if (foodGeneratesWealthRoute) {

            reasons.add(
                    EatingGodPatternReason
                            .EATING_GOD_GENERATES_WEALTH_ROUTE
            );
        }

        if (
                wealthExposed
                        &&
                        weakSide
        ) {

            reasons.add(
                    EatingGodPatternReason
                            .EATING_GOD_GENERATES_WEALTH_WITH_WEAK_DAY_MASTER
            );
        }

        /*
         * =================================================
         * 5. 食神制殺
         * =================================================
         */

        boolean killingControlledByFood =
                sevenKillingsExposed
                        &&
                        signals
                                .sevenKillingsControlledByEatingGod();

        if (killingControlledByFood) {

            reasons.add(
                    EatingGodPatternReason
                            .SEVEN_KILLINGS_CONTROLLED_BY_EATING_GOD
            );
        }

        boolean foodWithKillingNoWealth =
                sevenKillingsExposed
                        &&
                        !wealthExposed;

        if (foodWithKillingNoWealth) {

            reasons.add(
                    EatingGodPatternReason
                            .FOOD_WITH_KILLING_AND_NO_WEALTH
            );
        }

        /*
         * =================================================
         * 6. 棄食就殺而透印
         * =================================================
         */

        boolean abandonFoodForKillingResource =
                sevenKillingsExposed
                        &&
                        resourceExposed
                        &&
                        !wealthExposed
                        &&
                        signals
                                .abandoningEatingGodForKillingWithResource();

        if (abandonFoodForKillingResource) {

            reasons.add(
                    EatingGodPatternReason
                            .ABANDON_EATING_GOD_FOR_KILLING_WITH_RESOURCE
            );
        }

        /*
         * =================================================
         * 7. 梟神奪食
         * =================================================
         *
         * 偏印 존재
         * != 奪食
         */

        boolean owlSuppressesFood =
                indirectResourcePresent
                        &&
                        signals
                                .indirectResourceActuallySuppressesEatingGod();

        if (owlSuppressesFood) {

            reasons.add(
                    EatingGodPatternReason
                            .INDIRECT_RESOURCE_SUPPRESSES_EATING_GOD
            );
        }

        /*
         * 財를 통해 梟을 제어하여
         * 食神을 보호.
         */
        boolean wealthProtectsFood =
                owlSuppressesFood
                        &&
                        wealthExposed
                        &&
                        signals
                                .wealthProtectsEatingGodFromIndirectResource();

        if (wealthProtectsFood) {

            reasons.add(
                    EatingGodPatternReason
                            .WEALTH_PROTECTS_EATING_GOD_FROM_INDIRECT_RESOURCE
            );
        }

        /*
         * 梟이 食을 빼앗지만
         * 아예 食을 버리고 煞印 구조로 전환.
         */
        boolean owlResolvedByKillingResourceRoute =
                owlSuppressesFood
                        &&
                        abandonFoodForKillingResource;

        boolean owlSuppressionResolved =
                wealthProtectsFood
                        ||
                        owlResolvedByKillingResourceRoute;

        boolean owlSuppressionUnresolved =
                owlSuppressesFood
                        &&
                        !owlSuppressionResolved;

        if (owlSuppressionUnresolved) {

            reasons.add(
                    EatingGodPatternReason
                            .OWL_SUPPRESSION_UNRESOLVED
            );
        }

        /*
         * =================================================
         * 8. 生財露殺
         * =================================================
         */

        boolean wealthAndKillingExposed =
                wealthExposed
                        &&
                        sevenKillingsExposed;

        if (wealthAndKillingExposed) {

            reasons.add(
                    EatingGodPatternReason
                            .WEALTH_AND_KILLING_BOTH_EXPOSED
            );
        }

        boolean wealthStrengthensKilling =
                wealthAndKillingExposed
                        &&
                        signals
                                .wealthStrengthensSevenKillings();

        if (wealthStrengthensKilling) {

            reasons.add(
                    EatingGodPatternReason
                            .WEALTH_STRENGTHENS_SEVEN_KILLINGS
            );
        }

        /*
         * 財와 殺이 같이 있어도
         * 食神이 중간에 실제 작용하여
         * 財가 바로 殺로 흘러가지 않는 예외.
         */
        boolean foodSeparatesWealthKilling =
                wealthAndKillingExposed
                        &&
                        signals
                                .eatingGodSeparatesWealthAndKilling();

        if (foodSeparatesWealthKilling) {

            reasons.add(
                    EatingGodPatternReason
                            .EATING_GOD_SEPARATES_WEALTH_AND_KILLING
            );
        }

        boolean foodCombinesKillingPreservingWealth =
                wealthAndKillingExposed
                        &&
                        signals
                                .eatingGodCombinesKillingPreservingWealth();

        if (foodCombinesKillingPreservingWealth) {

            reasons.add(
                    EatingGodPatternReason
                            .EATING_GOD_COMBINES_KILLING_PRESERVING_WEALTH
            );
        }

        boolean wealthKillingProblemResolved =
                killingControlledByFood
                        ||
                        foodSeparatesWealthKilling
                        ||
                        foodCombinesKillingPreservingWealth;

        boolean wealthKillingProblemUnresolved =
                wealthStrengthensKilling
                        &&
                        !wealthKillingProblemResolved;

        if (wealthKillingProblemUnresolved) {

            reasons.add(
                    EatingGodPatternReason
                            .WEALTH_KILLING_PROBLEM_UNRESOLVED
            );
        }

        /*
         * =================================================
         * 9. 食神帶煞印而又逢財
         *
         * → 成中有敗
         * =================================================
         */

        boolean killingResourceRouteWithWealth =
                sevenKillingsExposed
                        &&
                        resourceExposed
                        &&
                        wealthExposed
                        &&
                        signals
                                .wealthDamagesKillingResourceRoute();

        if (killingResourceRouteWithWealth) {

            reasons.add(
                    EatingGodPatternReason
                            .KILLING_RESOURCE_ROUTE_WITH_WEALTH
            );

            reasons.add(
                    EatingGodPatternReason
                            .WEALTH_DAMAGES_KILLING_RESOURCE_ROUTE
            );
        }

        /*
         * =================================================
         * 10. 官殺競出
         * =================================================
         */

        boolean officerKillingsMixed =
                directOfficerExposed
                        &&
                        sevenKillingsExposed;

        if (officerKillingsMixed) {

            reasons.add(
                    EatingGodPatternReason
                            .OFFICER_AND_SEVEN_KILLINGS_MIXED
            );
        }

        if (
                officerKillingsMixed
                        &&
                        signals
                                .officerKillingsMixtureUsable()
        ) {

            reasons.add(
                    EatingGodPatternReason
                            .OFFICER_KILLINGS_MIXTURE_USABLE
            );
        }

        if (
                officerKillingsMixed
                        &&
                        !signals
                                .officerKillingsMixtureUsable()
        ) {

            reasons.add(
                    EatingGodPatternReason
                            .OFFICER_KILLINGS_MIXTURE_REQUIRES_REVIEW
            );
        }

        /*
         * =================================================
         * 11. 梟奪食 敗中有救
         * =================================================
         *
         * 食逢梟
         * → 生財護食
         *
         * 또는
         *
         * → 就煞成格
         */

        if (
                owlSuppressesFood
                        &&
                        owlSuppressionResolved
                        &&
                        !wealthKillingProblemUnresolved
        ) {

            reasons.add(
                    EatingGodPatternReason
                            .EATING_GOD_PATTERN_RESCUED
            );

            return result(
                    selectedPattern,
                    PatternStatus.BROKEN_WITH_RESCUE,
                    reasons
            );
        }

        /*
         * =================================================
         * 12. 梟奪食 미해결
         * =================================================
         */

        if (owlSuppressionUnresolved) {

            reasons.add(
                    EatingGodPatternReason
                            .EATING_GOD_PATTERN_BROKEN
            );

            return result(
                    selectedPattern,
                    PatternStatus.BROKEN,
                    reasons
            );
        }

        /*
         * =================================================
         * 13. 生財露殺 미해결
         * =================================================
         */

        if (wealthKillingProblemUnresolved) {

            reasons.add(
                    EatingGodPatternReason
                            .EATING_GOD_PATTERN_BROKEN
            );

            return result(
                    selectedPattern,
                    PatternStatus.BROKEN,
                    reasons
            );
        }

        /*
         * =================================================
         * 14. 食神帶煞印而又逢財
         *
         * → 成中有敗
         * =================================================
         */

        if (killingResourceRouteWithWealth) {

            reasons.add(
                    EatingGodPatternReason
                            .EATING_GOD_FORMATION_WITH_DEFECT
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED_WITH_DEFECT,
                    reasons
            );
        }

        /*
         * =================================================
         * 15. 食神生財
         * =================================================
         */

        if (
                foodGeneratesWealthRoute
                        &&
                        !sevenKillingsExposed
                        &&
                        !owlSuppressesFood
        ) {

            reasons.add(
                    EatingGodPatternReason
                            .CLEAN_EATING_GOD_FORMATION
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED,
                    reasons
            );
        }

        /*
         * =================================================
         * 16. 食帶煞而無財
         * =================================================
         *
         * 七殺이 실제 食神에 의해 제어되는
         * 경우에만 clean 成格으로 인정.
         */

        if (
                foodWithKillingNoWealth
                        &&
                        killingControlledByFood
                        &&
                        !owlSuppressesFood
        ) {

            reasons.add(
                    EatingGodPatternReason
                            .CLEAN_EATING_GOD_FORMATION
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED,
                    reasons
            );
        }

        /*
         * =================================================
         * 17. 棄食就煞而透印
         * =================================================
         */

        if (
                abandonFoodForKillingResource
                        &&
                        !wealthExposed
        ) {

            reasons.add(
                    EatingGodPatternReason
                            .CLEAN_EATING_GOD_FORMATION
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED,
                    reasons
            );
        }

        /*
         * =================================================
         * 18. 食神合殺存財
         * =================================================
         */

        if (
                foodCombinesKillingPreservingWealth
                        &&
                        !owlSuppressesFood
        ) {

            reasons.add(
                    EatingGodPatternReason
                            .CLEAN_EATING_GOD_FORMATION
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED,
                    reasons
            );
        }

        /*
         * =================================================
         * 19. 財 + 殺이 함께 있으나
         * 食이 중간에서 정리함
         * =================================================
         */

        if (
                wealthAndKillingExposed
                        &&
                        foodSeparatesWealthKilling
                        &&
                        !owlSuppressesFood
        ) {

            reasons.add(
                    EatingGodPatternReason
                            .CLEAN_EATING_GOD_FORMATION
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED,
                    reasons
            );
        }

        /*
         * =================================================
         * 20. 官殺競出
         * =================================================
         *
         * 食神章에는 官煞競出도
         * 成局 가능하다고 설명하지만,
         * 실제 배치 조건이 더 필요하다.
         *
         * 별도 구조 신호가 없으면
         * 억지로 成格하지 않는다.
         */

        if (officerKillingsMixed) {

            if (
                    signals
                            .officerKillingsMixtureUsable()
            ) {

                reasons.add(
                        EatingGodPatternReason
                                .CLEAN_EATING_GOD_FORMATION
                );

                return result(
                        selectedPattern,
                        PatternStatus.FORMED,
                        reasons
                );
            }

            return result(
                    selectedPattern,
                    PatternStatus.REVIEW_REQUIRED,
                    reasons
            );
        }

        /*
         * =================================================
         * 21. 食神生財 가능하지만 身이 약함
         * =================================================
         */

        if (
                wealthExposed
                        &&
                        !strongSide
        ) {

            reasons.add(
                    EatingGodPatternReason
                            .EATING_GOD_PATTERN_REQUIRES_REVIEW
            );

            return result(
                    selectedPattern,
                    PatternStatus.REVIEW_REQUIRED,
                    reasons
            );
        }

        /*
         * =================================================
         * 22. 食帶煞이지만 制殺 구조 미확정
         * =================================================
         */

        if (
                sevenKillingsExposed
                        &&
                        !wealthExposed
                        &&
                        !killingControlledByFood
                        &&
                        !abandonFoodForKillingResource
        ) {

            reasons.add(
                    EatingGodPatternReason
                            .EATING_GOD_PATTERN_REQUIRES_REVIEW
            );

            return result(
                    selectedPattern,
                    PatternStatus.REVIEW_REQUIRED,
                    reasons
            );
        }

        /*
         * =================================================
         * 23. 財가 있으나 根 여부 불명확
         * =================================================
         */

        if (
                wealthExposed
                        &&
                        !wealthFoundationStable
        ) {

            reasons.add(
                    EatingGodPatternReason
                            .EATING_GOD_PATTERN_REQUIRES_REVIEW
            );

            return result(
                    selectedPattern,
                    PatternStatus.REVIEW_REQUIRED,
                    reasons
            );
        }

        /*
         * =================================================
         * 24. 單用食神 등
         * =================================================
         *
         * 財/殺/印의 명확한 배합이 없다고
         * 자동 破格하지 않는다.
         */

        reasons.add(
                EatingGodPatternReason
                        .EATING_GOD_PATTERN_REQUIRES_REVIEW
        );

        return result(
                selectedPattern,
                PatternStatus.REVIEW_REQUIRED,
                reasons
        );
    }

    private PatternAssessment<EatingGodPatternReason> result(

            MonthCommandPatternCandidate selectedPattern,

            PatternStatus status,

            EnumSet<EatingGodPatternReason> reasons

    ) {

        return new PatternAssessment<>(
                selectedPattern.dayMaster(),
                selectedPattern,
                status,
                reasons
        );
    }

    private void validate(

            MonthCommandPatternCandidate selectedPattern,

            PatternInteractionAnalysis interaction,

            DayMasterStrengthAnalysis strength

    ) {

        if (
                selectedPattern.candidateType()
                        != MonthCommandPatternCandidateType
                        .EATING_GOD
        ) {

            throw new IllegalArgumentException(
                    "EatingGodPatternRule에는 EATING_GOD 후보만 전달할 수 있습니다."
            );
        }

        if (
                selectedPattern.dayMaster()
                        != interaction.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "食神格 후보와 Interaction 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                selectedPattern.dayMaster()
                        != strength.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "食神格 후보와 왕쇠 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                strength.requiresSpecialStructureReview()
        ) {

            throw new IllegalArgumentException(
                    "특수구조 검토 상태를 일반 食神格 Rule에 전달할 수 없습니다."
            );
        }
    }
}