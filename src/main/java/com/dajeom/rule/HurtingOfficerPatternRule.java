package com.dajeom.rule;

import com.dajeom.domain.DayMasterStrengthAnalysis;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.HurtingOfficerPatternReason;
import com.dajeom.domain.HurtingOfficerPatternStructuralSignals;
import com.dajeom.domain.MonthCommandPatternCandidate;
import com.dajeom.domain.MonthCommandPatternCandidateType;
import com.dajeom.domain.PatternAssessment;
import com.dajeom.domain.PatternInteractionAnalysis;
import com.dajeom.domain.PatternStatus;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Objects;

/**
 * 傷官格 成敗 Rule.
 *
 * =================================================
 *
 * 子平真詮 주요 成格:
 *
 * 1. 傷官生財
 *
 * 2. 傷官佩印
 *    - 傷官旺
 *    - 印有根
 *
 * 3. 傷官旺
 *    身主弱
 *    透煞印
 *
 * 4. 傷官帶煞而無財
 *
 * =================================================
 *
 * 주요 破格:
 *
 * 1. 傷官非金水而見官
 *
 * 2. 生財而帶煞
 *
 * 3. 佩印而傷輕身旺
 *
 * =================================================
 *
 * 成中有敗:
 *
 * 1. 傷官生財
 *    財又逢合
 *
 * 2. 佩印
 *    印又遭傷
 *
 * 3. 煞印 구조에 財가 개입하여
 *    印을 손상
 *
 * =================================================
 *
 * 救應:
 *
 * 1. 傷官見官
 *    財의 위치가 적절하여
 *    傷官 → 財 → 官
 *
 * 2. 傷官生財透煞
 *    煞逢合
 *
 * =================================================
 *
 * 金水傷官見官은
 * 일반 傷官見官과 분리한다.
 *
 * =================================================
 *
 * 숫자 / 점수 / 가중치 / 다수결 없음.
 */
@Component
public class HurtingOfficerPatternRule {

    public PatternAssessment<HurtingOfficerPatternReason> assess(

            MonthCommandPatternCandidate selectedPattern,

            PatternInteractionAnalysis interaction,

            DayMasterStrengthAnalysis strength,

            HurtingOfficerPatternStructuralSignals signals

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
                "傷官格 구조 신호는 필수입니다."
        );

        validate(
                selectedPattern,
                interaction,
                strength
        );

        EnumSet<HurtingOfficerPatternReason> reasons =
                EnumSet.of(
                        HurtingOfficerPatternReason
                                .HURTING_OFFICER_PATTERN
                );

        /*
         * =================================================
         * 1. 傷官 자체
         * =================================================
         */

        if (selectedPattern.exposed()) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .HURTING_OFFICER_EXPOSED
            );

        } else {

            reasons.add(
                    HurtingOfficerPatternReason
                            .HURTING_OFFICER_NOT_EXPOSED
            );
        }

        if (
                signals
                        .hurtingOfficerFoundationStable()
        ) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .HURTING_OFFICER_FOUNDATION_STABLE
            );

        } else {

            reasons.add(
                    HurtingOfficerPatternReason
                            .HURTING_OFFICER_FOUNDATION_REQUIRES_REVIEW
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
                    HurtingOfficerPatternReason
                            .DAY_MASTER_STRONG_SIDE
            );
        }

        if (weakSide) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .DAY_MASTER_WEAK_SIDE
            );
        }

        if (mixedStrength) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .DAY_MASTER_MIXED_STRENGTH
            );
        }

        /*
         * =================================================
         * 3. 원국 십성 배치
         * =================================================
         */

        boolean wealthPresent =
                interaction.hasDirectWealth()
                        ||
                        interaction.hasIndirectWealth();

        boolean wealthExposed =
                interaction.wealthExposed();

        boolean resourcePresent =
                interaction.hasDirectResource()
                        ||
                        interaction.hasIndirectResource();

        boolean resourceExposed =
                interaction.resourceExposed();

        boolean sevenKillingsPresent =
                interaction.hasSevenKillings();

        boolean sevenKillingsExposed =
                interaction.sevenKillingsExposed();

        boolean directOfficerPresent =
                interaction.hasDirectOfficer();

        boolean directOfficerExposed =
                interaction.directOfficerExposed();

        if (wealthPresent) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .WEALTH_PRESENT
            );
        }

        if (wealthExposed) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .WEALTH_EXPOSED
            );
        }

        if (resourcePresent) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .RESOURCE_PRESENT
            );
        }

        if (resourceExposed) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .RESOURCE_EXPOSED
            );
        }

        if (sevenKillingsPresent) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .SEVEN_KILLINGS_PRESENT
            );
        }

        if (sevenKillingsExposed) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .SEVEN_KILLINGS_EXPOSED
            );
        }

        if (directOfficerPresent) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .DIRECT_OFFICER_PRESENT
            );
        }

        if (directOfficerExposed) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .DIRECT_OFFICER_EXPOSED
            );
        }

        /*
         * =================================================
         * 4. 傷官生財
         * =================================================
         */

        boolean wealthFoundationStable =
                wealthExposed
                        &&
                        signals
                                .wealthFoundationStable();

        if (wealthFoundationStable) {

            reasons.add(
                    HurtingOfficerPatternReason
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
                    HurtingOfficerPatternReason
                            .WEALTH_FOUNDATION_REQUIRES_REVIEW
            );
        }

        boolean wealthRoute =
                wealthExposed
                        &&
                        signals
                                .wealthRouteStable();

        if (wealthRoute) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .HURTING_OFFICER_GENERATES_WEALTH_ROUTE
            );
        }

        /*
         * =================================================
         * 5. 傷官佩印
         * =================================================
         */

        if (
                resourceExposed
                        &&
                        signals
                                .resourceFoundationStable()
        ) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .RESOURCE_FOUNDATION_STABLE
            );
        }

        if (
                resourceExposed
                        &&
                        signals
                                .resourceActuallyControlsHurtingOfficer()
        ) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .RESOURCE_CONTROLS_HURTING_OFFICER
            );
        }

        boolean resourceRoute =
                resourceExposed
                        &&
                        signals
                                .resourceRouteStable();

        if (resourceRoute) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .HURTING_OFFICER_WITH_RESOURCE_ROUTE
            );
        }

        boolean hurtingOfficerTooLight =
                resourceRoute
                        &&
                        signals
                                .hurtingOfficerTooLightForResourceRoute();

        if (hurtingOfficerTooLight) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .HURTING_OFFICER_TOO_LIGHT_FOR_RESOURCE_ROUTE
            );
        }

        /*
         * =================================================
         * 6. 七殺 / 煞印
         * =================================================
         */

        boolean killingResourceRoute =
                sevenKillingsExposed
                        &&
                        resourceExposed
                        &&
                        weakSide
                        &&
                        signals
                                .killingGeneratesResourceToSupportDayMaster();

        if (killingResourceRoute) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .KILLING_GENERATES_RESOURCE_TO_SUPPORT_DAY_MASTER
            );

            reasons.add(
                    HurtingOfficerPatternReason
                            .KILLING_RESOURCE_ROUTE
            );
        }

        boolean killingWithoutWealthRoute =
                sevenKillingsExposed
                        &&
                        !wealthExposed
                        &&
                        signals
                                .hurtingOfficerAndKillingUsableWithoutWealth();

        if (killingWithoutWealthRoute) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .HURTING_OFFICER_WITH_KILLING_NO_WEALTH
            );
        }

        /*
         * =================================================
         * 7. 金水傷官 여부
         * =================================================
         *
         * 월령 격 자체가 傷官이고
         * 일간이 庚/辛이면
         * 傷官 오행은 水다.
         *
         * 따라서 金水傷官 Context로 분리한다.
         *
         * 이것만으로 官을 쓸 수 있다고
         * 확정하지 않는다.
         */

        boolean metalWaterHurtingOfficer =
                isMetalDayMaster(
                        selectedPattern.dayMaster()
                );

        if (metalWaterHurtingOfficer) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .METAL_WATER_HURTING_OFFICER
            );
        }

        /*
         * =================================================
         * 8. 傷官見官
         * =================================================
         */

        boolean officerConflict =
                directOfficerExposed
                        &&
                        signals
                                .directOfficerActuallyConflictsWithHurtingOfficer();

        if (officerConflict) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .DIRECT_OFFICER_CONFLICTS_WITH_HURTING_OFFICER
            );
        }

        boolean officerResolvedByWealth =
                officerConflict
                        &&
                        signals
                                .officerConflictResolvedByWealthBridge();

        if (officerResolvedByWealth) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .OFFICER_CONFLICT_RESOLVED_BY_WEALTH_BRIDGE
            );
        }

        boolean metalWaterOfficerRoute =
                metalWaterHurtingOfficer
                        &&
                        directOfficerExposed
                        &&
                        signals
                                .metalWaterOfficerRouteUsable();

        if (metalWaterOfficerRoute) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .METAL_WATER_OFFICER_ROUTE_USABLE
            );
        }

        boolean nonMetalWaterOfficerConflict =
                officerConflict
                        &&
                        !metalWaterHurtingOfficer;

        if (nonMetalWaterOfficerConflict) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .NON_METAL_WATER_HURTING_OFFICER_SEES_OFFICER
            );
        }

        boolean officerConflictUnresolved =
                officerConflict
                        &&
                        !officerResolvedByWealth
                        &&
                        !metalWaterOfficerRoute;

        if (officerConflictUnresolved) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .OFFICER_CONFLICT_UNRESOLVED
            );
        }

        /*
         * =================================================
         * 9. 官殺混雜
         * =================================================
         */

        boolean officerKillingsMixed =
                directOfficerExposed
                        &&
                        sevenKillingsExposed;

        if (officerKillingsMixed) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .OFFICER_AND_SEVEN_KILLINGS_MIXED
            );
        }

        boolean officerKillingsCleared =
                officerKillingsMixed
                        &&
                        signals
                                .officerKillingsMixtureCleared();

        if (officerKillingsCleared) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .OFFICER_KILLINGS_MIXTURE_CLEARED
            );
        }

        boolean officerKillingsUnresolved =
                officerKillingsMixed
                        &&
                        !officerKillingsCleared;

        if (officerKillingsUnresolved) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .OFFICER_KILLINGS_MIXTURE_UNRESOLVED
            );
        }

        /*
         * =================================================
         * 10. 生財帶煞
         * =================================================
         */

        boolean wealthKillingContext =
                wealthRoute
                        &&
                        sevenKillingsExposed;

        if (wealthKillingContext) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .WEALTH_ROUTE_WITH_SEVEN_KILLINGS
            );
        }

        boolean killingDamagesWealthRoute =
                wealthKillingContext
                        &&
                        signals
                                .sevenKillingsDamagesWealthRoute();

        if (killingDamagesWealthRoute) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .SEVEN_KILLINGS_DAMAGES_WEALTH_ROUTE
            );
        }

        boolean killingCombinedAway =
                wealthKillingContext
                        &&
                        signals
                                .sevenKillingsCombinedAway();

        if (killingCombinedAway) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .SEVEN_KILLINGS_COMBINED_AWAY
            );
        }

        boolean killingDamageUnresolved =
                killingDamagesWealthRoute
                        &&
                        !killingCombinedAway;

        if (killingDamageUnresolved) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .SEVEN_KILLINGS_DAMAGE_UNRESOLVED
            );
        }

        /*
         * =================================================
         * 11. 成中有敗 Context
         * =================================================
         */

        boolean wealthCombined =
                wealthRoute
                        &&
                        signals
                                .wealthCombined();

        if (wealthCombined) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .WEALTH_COMBINED
            );
        }

        boolean resourceDamaged =
                resourceRoute
                        &&
                        signals
                                .resourceDamaged();

        if (resourceDamaged) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .RESOURCE_DAMAGED
            );
        }

        boolean killingResourceDamagedByWealth =
                killingResourceRoute
                        &&
                        wealthExposed
                        &&
                        signals
                                .wealthDamagesKillingResourceRoute();

        if (killingResourceDamagedByWealth) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .KILLING_RESOURCE_ROUTE_DAMAGED_BY_WEALTH
            );
        }

        /*
         * =================================================
         * 12. 敗中有救
         *
         * 傷官見官
         * → 財로 통관
         * =================================================
         */

        if (
                officerConflict
                        &&
                        officerResolvedByWealth
                        &&
                        !killingDamageUnresolved
        ) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .HURTING_OFFICER_PATTERN_RESCUED
            );

            return result(
                    selectedPattern,
                    PatternStatus.BROKEN_WITH_RESCUE,
                    reasons
            );
        }

        /*
         * =================================================
         * 13. 傷官生財透煞
         *     煞逢合
         *
         * → 敗中有救
         * =================================================
         */

        if (
                killingDamagesWealthRoute
                        &&
                        killingCombinedAway
        ) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .HURTING_OFFICER_PATTERN_RESCUED
            );

            return result(
                    selectedPattern,
                    PatternStatus.BROKEN_WITH_RESCUE,
                    reasons
            );
        }

        /*
         * =================================================
         * 14. 非金水傷官見官
         * =================================================
         */

        if (
                nonMetalWaterOfficerConflict
                        &&
                        !officerResolvedByWealth
        ) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .HURTING_OFFICER_PATTERN_BROKEN
            );

            return result(
                    selectedPattern,
                    PatternStatus.BROKEN,
                    reasons
            );
        }

        /*
         * =================================================
         * 15. 金水傷官見官
         * =================================================
         *
         * 金水傷官은 일반적인
         * "見官即破" Rule을 적용하지 않는다.
         *
         * 실제 조후/財印 보좌가 확인됐을 때만
         * 成格으로 인정한다.
         */

        if (
                metalWaterHurtingOfficer
                        &&
                        directOfficerExposed
        ) {

            if (metalWaterOfficerRoute) {

                reasons.add(
                        HurtingOfficerPatternReason
                                .CLEAN_HURTING_OFFICER_FORMATION
                );

                return result(
                        selectedPattern,
                        PatternStatus.FORMED,
                        reasons
                );
            }

            /*
             * 金水라는 이유만으로
             * 官을 자동 길하게 만들지 않는다.
             */
            return result(
                    selectedPattern,
                    PatternStatus.REVIEW_REQUIRED,
                    reasons
            );
        }

        /*
         * =================================================
         * 16. 生財帶煞 미해결
         * =================================================
         */

        if (killingDamageUnresolved) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .HURTING_OFFICER_PATTERN_BROKEN
            );

            return result(
                    selectedPattern,
                    PatternStatus.BROKEN,
                    reasons
            );
        }

        /*
         * =================================================
         * 17. 佩印而傷輕身旺
         * =================================================
         */

        if (
                resourceRoute
                        &&
                        strongSide
                        &&
                        hurtingOfficerTooLight
        ) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .HURTING_OFFICER_PATTERN_BROKEN
            );

            return result(
                    selectedPattern,
                    PatternStatus.BROKEN,
                    reasons
            );
        }

        /*
         * =================================================
         * 18. 傷官生財
         *     財又逢合
         *
         * → 成中有敗
         * =================================================
         */

        if (wealthCombined) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .HURTING_OFFICER_FORMATION_WITH_DEFECT
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED_WITH_DEFECT,
                    reasons
            );
        }

        /*
         * =================================================
         * 19. 佩印而印又遭傷
         *
         * → 成中有敗
         * =================================================
         */

        if (resourceDamaged) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .HURTING_OFFICER_FORMATION_WITH_DEFECT
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED_WITH_DEFECT,
                    reasons
            );
        }

        /*
         * =================================================
         * 20. 煞印 구조 + 財가 印 손상
         * =================================================
         */

        if (killingResourceDamagedByWealth) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .HURTING_OFFICER_FORMATION_WITH_DEFECT
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED_WITH_DEFECT,
                    reasons
            );
        }

        /*
         * =================================================
         * 21. 官殺並透
         * =================================================
         *
         * 傷官章에서는
         * 官煞並透도 干頭取清이 중요하다.
         */

        if (officerKillingsMixed) {

            if (officerKillingsCleared) {

                /*
                 * 혼잡이 정리되었다고
                 * 그것만으로 成格하지 않는다.
                 *
                 * 아래의 실제 成格 route가
                 * 존재하는지 계속 본다.
                 */

            } else {

                return result(
                        selectedPattern,
                        PatternStatus.REVIEW_REQUIRED,
                        reasons
                );
            }
        }

        /*
         * =================================================
         * 22. 傷官生財
         * =================================================
         */

        if (
                wealthRoute
                        &&
                        !sevenKillingsExposed
                        &&
                        !officerConflict
        ) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .CLEAN_HURTING_OFFICER_FORMATION
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED,
                    reasons
            );
        }

        /*
         * =================================================
         * 23. 傷官佩印
         * =================================================
         *
         * 傷官이 강하고 印에 根이 있으며
         * 印이 실제 傷官을 제하는 구조.
         *
         * 身旺 + 傷輕 Context는 위에서 탈락.
         */

        if (
                resourceRoute
                        &&
                        !hurtingOfficerTooLight
                        &&
                        !strongSide
                        &&
                        !wealthExposed
        ) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .CLEAN_HURTING_OFFICER_FORMATION
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED,
                    reasons
            );
        }

        /*
         * =================================================
         * 24. 傷官旺、身弱、透煞印
         * =================================================
         */

        if (
                killingResourceRoute
                        &&
                        !wealthExposed
        ) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .CLEAN_HURTING_OFFICER_FORMATION
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED,
                    reasons
            );
        }

        /*
         * =================================================
         * 25. 傷官帶煞而無財
         * =================================================
         */

        if (
                killingWithoutWealthRoute
                        &&
                        !directOfficerExposed
        ) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .CLEAN_HURTING_OFFICER_FORMATION
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED,
                    reasons
            );
        }

        /*
         * =================================================
         * 26. 生財 + 七殺 존재
         *
         * 실제 七殺이 財를 타고 강해지는지는
         * 아직 확인되지 않은 경우.
         * =================================================
         */

        if (
                wealthRoute
                        &&
                        sevenKillingsExposed
                        &&
                        !killingDamagesWealthRoute
        ) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .HURTING_OFFICER_FORMATION_WITH_DEFECT
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED_WITH_DEFECT,
                    reasons
            );
        }

        /*
         * =================================================
         * 27. 傷官佩印 가능하지만
         *     身이 강하거나 조건이 덜 정리됨
         * =================================================
         */

        if (resourceRoute) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .HURTING_OFFICER_PATTERN_REQUIRES_REVIEW
            );

            return result(
                    selectedPattern,
                    PatternStatus.REVIEW_REQUIRED,
                    reasons
            );
        }

        /*
         * =================================================
         * 28. 財는 있으나
         *     傷官生財의 실제 기반이 불분명
         * =================================================
         */

        if (
                wealthExposed
                        &&
                        !wealthRoute
        ) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .HURTING_OFFICER_PATTERN_REQUIRES_REVIEW
            );

            return result(
                    selectedPattern,
                    PatternStatus.REVIEW_REQUIRED,
                    reasons
            );
        }

        /*
         * =================================================
         * 29. 七殺은 있으나
         *     帶殺 / 煞印 구조가 정리되지 않음
         * =================================================
         */

        if (
                sevenKillingsExposed
                        &&
                        !killingResourceRoute
                        &&
                        !killingWithoutWealthRoute
        ) {

            reasons.add(
                    HurtingOfficerPatternReason
                            .HURTING_OFFICER_PATTERN_REQUIRES_REVIEW
            );

            return result(
                    selectedPattern,
                    PatternStatus.REVIEW_REQUIRED,
                    reasons
            );
        }

        /*
         * =================================================
         * 30. 나머지
         * =================================================
         */

        reasons.add(
                HurtingOfficerPatternReason
                        .HURTING_OFFICER_PATTERN_REQUIRES_REVIEW
        );

        return result(
                selectedPattern,
                PatternStatus.REVIEW_REQUIRED,
                reasons
        );
    }

    private PatternAssessment<HurtingOfficerPatternReason> result(

            MonthCommandPatternCandidate selectedPattern,

            PatternStatus status,

            EnumSet<HurtingOfficerPatternReason> reasons

    ) {

        return new PatternAssessment<>(
                selectedPattern.dayMaster(),
                selectedPattern,
                status,
                reasons
        );
    }

    private boolean isMetalDayMaster(
            HeavenlyStem dayMaster
    ) {

        return dayMaster
                == HeavenlyStem.GENG

                ||

                dayMaster
                        == HeavenlyStem.XIN;
    }

    private void validate(

            MonthCommandPatternCandidate selectedPattern,

            PatternInteractionAnalysis interaction,

            DayMasterStrengthAnalysis strength

    ) {

        if (
                selectedPattern.candidateType()
                        != MonthCommandPatternCandidateType
                        .HURTING_OFFICER
        ) {

            throw new IllegalArgumentException(
                    "HurtingOfficerPatternRule에는 HURTING_OFFICER 후보만 전달할 수 있습니다."
            );
        }

        if (
                selectedPattern.dayMaster()
                        != interaction.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "傷官格 후보와 Interaction 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                selectedPattern.dayMaster()
                        != strength.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "傷官格 후보와 왕쇠 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                strength.requiresSpecialStructureReview()
        ) {

            throw new IllegalArgumentException(
                    "특수구조 검토 상태를 일반 傷官格 Rule에 전달할 수 없습니다."
            );
        }
    }
}