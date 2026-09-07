package com.dajeom.rule;

import com.dajeom.domain.DayMasterStrengthAnalysis;
import com.dajeom.domain.MonthCommandPatternCandidate;
import com.dajeom.domain.MonthCommandPatternCandidateType;
import com.dajeom.domain.PatternAssessment;
import com.dajeom.domain.PatternInteractionAnalysis;
import com.dajeom.domain.PatternStatus;
import com.dajeom.domain.WealthPatternReason;
import com.dajeom.domain.WealthPatternStructuralSignals;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Objects;

/**
 * 正財格 / 偏財格 공통 成敗 Rule.
 *
 * =================================================
 *
 * 子平真詮의 財格 주요 成格:
 *
 * 1. 財旺生官
 *
 * 2. 財逢食生而身強
 *
 * 3. 財格透印而位置妥貼
 *
 * =================================================
 *
 * 주요 破格:
 *
 * 1. 財輕比重
 *
 * 2. 財透七殺
 *
 * 3. 財旺生官인데 身弱透官
 *
 * =================================================
 *
 * 주요 救應:
 *
 * 1. 財逢劫而透食以化
 *
 * 2. 生官以制劫
 *
 * 3. 七殺에 食神制殺以生財
 *
 * 4. 存財而合殺
 *
 * =================================================
 *
 * 중요:
 *
 * 比劫이 있다는 이유만으로
 * 자동으로 奪財라 하지 않는다.
 *
 * 七殺은 "透" 여부를 구분한다.
 *
 * 財와 印이 함께 있다고
 * 자동으로 相剋이라 하지 않는다.
 *
 * =================================================
 *
 * 숫자 / 가중치 / 다수결 없음.
 */
@Component
public class WealthPatternRule {

    public PatternAssessment<WealthPatternReason> assess(

            MonthCommandPatternCandidate selectedPattern,

            PatternInteractionAnalysis interaction,

            DayMasterStrengthAnalysis strength,

            WealthPatternStructuralSignals signals

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
                "財格 구조 신호는 필수입니다."
        );

        validate(
                selectedPattern,
                interaction,
                strength
        );

        EnumSet<WealthPatternReason> reasons =
                EnumSet.of(
                        WealthPatternReason.WEALTH_PATTERN
                );

        /*
         * =================================================
         * 1. 正財 / 偏財
         * =================================================
         */

        if (
                selectedPattern.candidateType()
                        == MonthCommandPatternCandidateType
                        .DIRECT_WEALTH
        ) {

            reasons.add(
                    WealthPatternReason
                            .DIRECT_WEALTH_PATTERN
            );

        } else {

            reasons.add(
                    WealthPatternReason
                            .INDIRECT_WEALTH_PATTERN
            );
        }

        if (
                selectedPattern.exposed()
        ) {

            reasons.add(
                    WealthPatternReason
                            .WEALTH_EXPOSED
            );

        } else {

            reasons.add(
                    WealthPatternReason
                            .WEALTH_NOT_EXPOSED
            );
        }

        /*
         * =================================================
         * 2. 일간 왕쇠 Context
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
                    WealthPatternReason
                            .DAY_MASTER_STRONG_SIDE
            );
        }

        if (weakSide) {

            reasons.add(
                    WealthPatternReason
                            .DAY_MASTER_WEAK_SIDE
            );
        }

        if (mixedStrength) {

            reasons.add(
                    WealthPatternReason
                            .DAY_MASTER_MIXED_STRENGTH
            );
        }

        /*
         * =================================================
         * 3. 원국 주요 십성
         * =================================================
         */

        boolean directOfficerPresent =
                interaction
                        .hasDirectOfficer();

        boolean directOfficerExposed =
                interaction
                        .directOfficerExposed();

        boolean eatingGodExposed =
                interaction
                        .hasVisible(
                                MonthCommandPatternCandidateType
                                        .EATING_GOD
                        );

        boolean resourceExposed =
                interaction
                        .resourceExposed();

        boolean companionRobExposed =
                interaction
                        .companionOrRobWealthExposed();

        boolean sevenKillingsPresent =
                interaction
                        .hasSevenKillings();

        boolean sevenKillingsExposed =
                interaction
                        .sevenKillingsExposed();

        boolean hurtingOfficerExposed =
                interaction
                        .hasVisible(
                                MonthCommandPatternCandidateType
                                        .HURTING_OFFICER
                        );

        if (directOfficerPresent) {

            reasons.add(
                    WealthPatternReason
                            .DIRECT_OFFICER_PRESENT
            );
        }

        if (directOfficerExposed) {

            reasons.add(
                    WealthPatternReason
                            .DIRECT_OFFICER_EXPOSED
            );
        }

        if (eatingGodExposed) {

            reasons.add(
                    WealthPatternReason
                            .EATING_GOD_EXPOSED
            );
        }

        if (resourceExposed) {

            reasons.add(
                    WealthPatternReason
                            .RESOURCE_EXPOSED
            );
        }

        if (companionRobExposed) {

            reasons.add(
                    WealthPatternReason
                            .COMPANION_OR_ROB_WEALTH_EXPOSED
            );
        }

        if (sevenKillingsPresent) {

            reasons.add(
                    WealthPatternReason
                            .SEVEN_KILLINGS_PRESENT
            );
        }

        if (sevenKillingsExposed) {

            reasons.add(
                    WealthPatternReason
                            .SEVEN_KILLINGS_EXPOSED
            );
        }

        if (hurtingOfficerExposed) {

            reasons.add(
                    WealthPatternReason
                            .HURTING_OFFICER_EXPOSED
            );
        }

        /*
         * =================================================
         * 4. 比劫奪財 Context
         * =================================================
         *
         * 比劫 존재
         * != 奪財
         *
         * 실제 財를 다투는 배치가
         * 확인됐을 때만 破格 Context가 된다.
         */

        boolean robberyContext =
                signals
                        .wealthContestedByCompanionOrRobWealth();

        if (robberyContext) {

            reasons.add(
                    WealthPatternReason
                            .WEALTH_CONTESTED_BY_COMPANION_OR_ROB_WEALTH
            );
        }

        boolean robberyResolvedByEatingGod =
                robberyContext
                        &&
                        signals
                                .robberyResolvedByEatingGod();

        boolean robberyControlledByOfficer =
                robberyContext
                        &&
                        signals
                                .robberyControlledByOfficer();

        if (robberyResolvedByEatingGod) {

            reasons.add(
                    WealthPatternReason
                            .ROBBERY_RESOLVED_BY_EATING_GOD
            );
        }

        if (robberyControlledByOfficer) {

            reasons.add(
                    WealthPatternReason
                            .ROBBERY_CONTROLLED_BY_OFFICER
            );
        }

        boolean robberyResolved =
                robberyResolvedByEatingGod
                        ||
                        robberyControlledByOfficer;

        boolean robberyUnresolved =
                robberyContext
                        &&
                        !robberyResolved;

        if (robberyUnresolved) {

            reasons.add(
                    WealthPatternReason
                            .ROBBERY_UNRESOLVED
            );
        }

        /*
         * =================================================
         * 5. 財透七殺
         * =================================================
         *
         * 子平真詮에서 財格의 직접적인
         * 破格 조건으로 제시.
         */

        boolean sevenKillingsControlled =
                sevenKillingsExposed
                        &&
                        signals
                                .sevenKillingsControlledByEatingGod();

        boolean sevenKillingsCombinedAway =
                sevenKillingsExposed
                        &&
                        signals
                                .sevenKillingsCombinedAwayPreservingWealth();

        if (sevenKillingsControlled) {

            reasons.add(
                    WealthPatternReason
                            .SEVEN_KILLINGS_CONTROLLED_BY_EATING_GOD
            );
        }

        if (sevenKillingsCombinedAway) {

            reasons.add(
                    WealthPatternReason
                            .SEVEN_KILLINGS_COMBINED_AWAY_PRESERVING_WEALTH
            );
        }

        boolean sevenKillingsResolved =
                sevenKillingsControlled
                        ||
                        sevenKillingsCombinedAway;

        boolean sevenKillingsUnresolved =
                sevenKillingsExposed
                        &&
                        !sevenKillingsResolved;

        if (sevenKillingsUnresolved) {

            reasons.add(
                    WealthPatternReason
                            .SEVEN_KILLINGS_UNRESOLVED
            );
        }

        /*
         * =================================================
         * 6. 敗中有救
         * =================================================
         *
         * 財逢劫
         * → 食化 또는 官制
         *
         * 財逢殺
         * → 食神制殺 또는 合殺存財
         */

        boolean rescuedBreakingContext =
                (
                        robberyContext
                                &&
                                robberyResolved
                )
                        ||
                        (
                                sevenKillingsExposed
                                        &&
                                        sevenKillingsResolved
                        );

        boolean unresolvedBreakingContext =
                robberyUnresolved
                        ||
                        sevenKillingsUnresolved;

        if (
                rescuedBreakingContext
                        &&
                        !unresolvedBreakingContext
        ) {

            reasons.add(
                    WealthPatternReason
                            .WEALTH_PATTERN_RESCUED
            );

            return result(
                    selectedPattern,
                    PatternStatus.BROKEN_WITH_RESCUE,
                    reasons
            );
        }

        /*
         * =================================================
         * 7. 財透七殺 미해결
         * =================================================
         */

        if (sevenKillingsUnresolved) {

            reasons.add(
                    WealthPatternReason
                            .WEALTH_PATTERN_BROKEN
            );

            return result(
                    selectedPattern,
                    PatternStatus.BROKEN,
                    reasons
            );
        }

        /*
         * =================================================
         * 8. 比劫奪財 미해결
         * =================================================
         */

        if (robberyUnresolved) {

            reasons.add(
                    WealthPatternReason
                            .WEALTH_PATTERN_BROKEN
            );

            return result(
                    selectedPattern,
                    PatternStatus.BROKEN,
                    reasons
            );
        }

        /*
         * =================================================
         * 9. 財旺生官
         * =================================================
         *
         * 월령 財格이고 四柱에 正官이 있어
         * 財 → 官 흐름이 형성.
         *
         * 七殺 혼잡은 깨끗한 財生官으로
         * 바로 확정하지 않는다.
         */

        boolean wealthGeneratesOfficerRoute =
                directOfficerPresent
                        &&
                        !sevenKillingsPresent;

        if (wealthGeneratesOfficerRoute) {

            reasons.add(
                    WealthPatternReason
                            .WEALTH_GENERATES_OFFICER_ROUTE
            );
        }

        /*
         * 財旺生官인데
         * 身弱하고 官이 실제 천간에 투출.
         *
         * 子平真詮:
         *
         * 身弱透官
         * → 破格
         *
         * 다만 印까지 투출하여
         * 실제로 身을 돕는 복잡한 구조라면
         * 財印 위치까지 다시 봐야 하므로
         * 바로 破格 확정하지 않는다.
         */

        if (
                wealthGeneratesOfficerRoute
                        &&
                        weakSide
                        &&
                        directOfficerExposed
        ) {

            reasons.add(
                    WealthPatternReason
                            .DAY_MASTER_WEAK_WITH_EXPOSED_OFFICER
            );

            if (
                    resourceExposed
                            &&
                            signals
                                    .wealthAndResourceMutuallyNonObstructing()
            ) {

                reasons.add(
                        WealthPatternReason
                                .WEALTH_AND_RESOURCE_NON_OBSTRUCTING
                );

                return result(
                        selectedPattern,
                        PatternStatus.REVIEW_REQUIRED,
                        reasons
                );
            }

            reasons.add(
                    WealthPatternReason
                            .WEALTH_PATTERN_BROKEN
            );

            return result(
                    selectedPattern,
                    PatternStatus.BROKEN,
                    reasons
            );
        }

        /*
         * =================================================
         * 10. 財旺生官 成中有敗
         * =================================================
         *
         * 財旺生官이 형성됐으나
         *
         * - 傷官이 官을 실제 손상
         * - 官이 合을 당함
         *
         * 하는 경우.
         */

        boolean officerDamagedByHurtingOfficer =
                wealthGeneratesOfficerRoute
                        &&
                        signals
                                .generatedOfficerDamagedByHurtingOfficer();

        boolean officerCombined =
                wealthGeneratesOfficerRoute
                        &&
                        signals
                                .generatedOfficerCombined();

        if (officerDamagedByHurtingOfficer) {

            reasons.add(
                    WealthPatternReason
                            .GENERATED_OFFICER_DAMAGED_BY_HURTING_OFFICER
            );
        }

        if (officerCombined) {

            reasons.add(
                    WealthPatternReason
                            .GENERATED_OFFICER_COMBINED
            );
        }

        if (
                wealthGeneratesOfficerRoute
                        &&
                        (
                                officerDamagedByHurtingOfficer
                                        ||
                                        officerCombined
                        )
        ) {

            reasons.add(
                    WealthPatternReason
                            .WEALTH_FORMATION_WITH_DEFECT
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED_WITH_DEFECT,
                    reasons
            );
        }

        /*
         * =================================================
         * 11. 食神生財
         * =================================================
         *
         * 月令財星
         * +
         * 食神透
         * +
         * 身強
         *
         * 이 기본 구조.
         *
         * 財用食生에서 官이 천간에 드러나는
         * 경우는 깨끗한 食神生財 구조로
         * 바로 확정하지 않는다.
         */

        boolean foodGeneratesWealthRoute =
                eatingGodExposed
                        &&
                        strongSide
                        &&
                        !directOfficerExposed
                        &&
                        !sevenKillingsExposed;

        if (foodGeneratesWealthRoute) {

            reasons.add(
                    WealthPatternReason
                            .FOOD_GENERATES_WEALTH_ROUTE
            );
        }

        if (
                eatingGodExposed
                        &&
                        !strongSide
        ) {

            reasons.add(
                    WealthPatternReason
                            .FOOD_GENERATES_WEALTH_BUT_DAY_MASTER_NOT_STRONG
            );
        }

        if (
                eatingGodExposed
                        &&
                        directOfficerExposed
        ) {

            reasons.add(
                    WealthPatternReason
                            .DIRECT_OFFICER_BLOCKS_PURE_FOOD_WEALTH_ROUTE
            );
        }

        /*
         * =================================================
         * 12. 財格佩印
         * =================================================
         *
         * 財와 印이 같이 있어도
         * 둘이 직접 서로 치면 안 된다.
         *
         * 위치가 적절하여
         * 財印不相礙일 때만
         * clean route로 인정.
         */

        boolean wealthResourceRoute =
                resourceExposed
                        &&
                        signals
                                .wealthAndResourceMutuallyNonObstructing();

        if (resourceExposed) {

            if (
                    signals
                            .wealthAndResourceMutuallyNonObstructing()
            ) {

                reasons.add(
                        WealthPatternReason
                                .WEALTH_AND_RESOURCE_NON_OBSTRUCTING
                );

                reasons.add(
                        WealthPatternReason
                                .WEALTH_WITH_RESOURCE_ROUTE
                );

            } else {

                reasons.add(
                        WealthPatternReason
                                .WEALTH_RESOURCE_PLACEMENT_REQUIRES_REVIEW
                );
            }
        }

        /*
         * =================================================
         * 13. 七殺이 지장간에만 존재
         * =================================================
         *
         * 子平真詮의 직접 破格 표현은
         *
         * 財透七殺
         *
         * 이다.
         *
         * 따라서 七殺이 존재하되
         * 천간에 투출하지 않은 경우를
         * 동일한 破格으로 만들지 않는다.
         */

        boolean hiddenSevenKillingsContext =
                sevenKillingsPresent
                        &&
                        !sevenKillingsExposed;

        if (hiddenSevenKillingsContext) {

            reasons.add(
                    WealthPatternReason
                            .HIDDEN_SEVEN_KILLINGS_REQUIRES_REVIEW
            );
        }

        /*
         * =================================================
         * 14. 깨끗한 財格 成格
         * =================================================
         */

        boolean cleanFormationRoute =
                wealthGeneratesOfficerRoute
                        ||
                        foodGeneratesWealthRoute
                        ||
                        wealthResourceRoute;

        if (
                cleanFormationRoute
                        &&
                        !hiddenSevenKillingsContext
                        &&
                        !robberyContext
        ) {

            reasons.add(
                    WealthPatternReason
                            .CLEAN_WEALTH_FORMATION
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED,
                    reasons
            );
        }

        /*
         * =================================================
         * 15. 比劫은 보이지만
         *     실제 奪財 관계는 미확정
         * =================================================
         *
         * 比劫이 천간에 있다는 이유만으로
         * 바로 破格하지 않는다.
         */

        if (
                companionRobExposed
                        &&
                        !robberyContext
                        &&
                        !foodGeneratesWealthRoute
        ) {

            return result(
                    selectedPattern,
                    PatternStatus.REVIEW_REQUIRED,
                    reasons
            );
        }

        /*
         * =================================================
         * 16. 財格佩印 가능성은 있으나
         *     財印 위치가 아직 해결되지 않음
         * =================================================
         */

        if (
                resourceExposed
                        &&
                        !wealthResourceRoute
        ) {

            return result(
                    selectedPattern,
                    PatternStatus.REVIEW_REQUIRED,
                    reasons
            );
        }

        /*
         * =================================================
         * 17. 食神生財를 검토할 수 있으나
         *     身이 강하지 않거나 官이 개입
         * =================================================
         */

        if (
                eatingGodExposed
                        &&
                        !foodGeneratesWealthRoute
        ) {

            return result(
                    selectedPattern,
                    PatternStatus.REVIEW_REQUIRED,
                    reasons
            );
        }

        /*
         * =================================================
         * 18. 七殺이 암장되어 순수성 추가검토
         * =================================================
         */

        if (hiddenSevenKillingsContext) {

            return result(
                    selectedPattern,
                    PatternStatus.REVIEW_REQUIRED,
                    reasons
            );
        }

        /*
         * =================================================
         * 19. 孤財 / 아직 배합 미완
         * =================================================
         *
         * 財 하나만 있다고
         * 바로 成格도 破格도 하지 않는다.
         */

        reasons.add(
                WealthPatternReason
                        .ISOLATED_WEALTH_REQUIRES_REVIEW
        );

        return result(
                selectedPattern,
                PatternStatus.REVIEW_REQUIRED,
                reasons
        );
    }

    private PatternAssessment<WealthPatternReason> result(

            MonthCommandPatternCandidate selectedPattern,

            PatternStatus status,

            EnumSet<WealthPatternReason> reasons

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

        MonthCommandPatternCandidateType type =
                selectedPattern
                        .candidateType();

        if (
                type
                        != MonthCommandPatternCandidateType
                        .DIRECT_WEALTH

                        &&

                        type
                                != MonthCommandPatternCandidateType
                                .INDIRECT_WEALTH
        ) {

            throw new IllegalArgumentException(
                    "WealthPatternRule에는 DIRECT_WEALTH 또는 INDIRECT_WEALTH 후보만 전달할 수 있습니다."
            );
        }

        if (
                selectedPattern.dayMaster()
                        != interaction.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "財格 후보와 Interaction 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                selectedPattern.dayMaster()
                        != strength.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "財格 후보와 왕쇠 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                strength
                        .requiresSpecialStructureReview()
        ) {

            throw new IllegalArgumentException(
                    "특수구조 검토 상태를 일반 財格 Rule에 전달할 수 없습니다."
            );
        }
    }
}