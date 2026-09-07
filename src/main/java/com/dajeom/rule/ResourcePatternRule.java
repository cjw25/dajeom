package com.dajeom.rule;

import com.dajeom.domain.DayMasterStrengthAnalysis;
import com.dajeom.domain.MonthCommandPatternCandidate;
import com.dajeom.domain.MonthCommandPatternCandidateType;
import com.dajeom.domain.PatternAssessment;
import com.dajeom.domain.PatternInteractionAnalysis;
import com.dajeom.domain.PatternStatus;
import com.dajeom.domain.ResourcePatternReason;
import com.dajeom.domain.ResourcePatternStructuralSignals;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Objects;

/**
 * 正印格 / 偏印格 공통 成敗 Rule.
 *
 * =================================================
 *
 * 子平真詮의 印格 주요 成格:
 *
 * 1. 印輕逢煞
 *
 * 2. 官印雙全
 *
 * 3. 身印兩旺而用食傷洩氣
 *
 * 4. 印多逢財而財透根輕
 *
 * =================================================
 *
 * 주요 破格:
 *
 * 1. 印輕逢財
 *
 * 2. 身強印重而透煞
 *
 * =================================================
 *
 * 成中有敗:
 *
 * 1. 印透食以洩氣
 *    又遇財露
 *
 * 2. 透煞以生印
 *    又透財
 *
 * 3. 食神洩秀
 *    又逢梟奪食
 *
 * =================================================
 *
 * 敗中有救:
 *
 * 1. 印逢財
 *    劫財以解
 *
 * 2. 合財而存印
 *
 * 3. 官殺混雜
 *    合煞 / 有制
 *
 * =================================================
 *
 * 숫자 / 개수 다수결 / 퍼센트 없음.
 */
@Component
public class ResourcePatternRule {

    public PatternAssessment<ResourcePatternReason> assess(

            MonthCommandPatternCandidate selectedPattern,

            PatternInteractionAnalysis interaction,

            DayMasterStrengthAnalysis strength,

            ResourcePatternStructuralSignals signals

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
                "印格 구조 신호는 필수입니다."
        );

        validate(
                selectedPattern,
                interaction,
                strength
        );

        EnumSet<ResourcePatternReason> reasons =
                EnumSet.of(
                        ResourcePatternReason
                                .RESOURCE_PATTERN
                );

        /*
         * =================================================
         * 1. 正印 / 偏印
         * =================================================
         */

        if (
                selectedPattern.candidateType()
                        == MonthCommandPatternCandidateType
                        .DIRECT_RESOURCE
        ) {

            reasons.add(
                    ResourcePatternReason
                            .DIRECT_RESOURCE_PATTERN
            );

        } else {

            reasons.add(
                    ResourcePatternReason
                            .INDIRECT_RESOURCE_PATTERN
            );
        }

        if (
                selectedPattern.exposed()
        ) {

            reasons.add(
                    ResourcePatternReason
                            .RESOURCE_EXPOSED
            );

        } else {

            reasons.add(
                    ResourcePatternReason
                            .RESOURCE_NOT_EXPOSED
            );
        }

        if (
                signals.resourceFoundationStable()
        ) {

            reasons.add(
                    ResourcePatternReason
                            .RESOURCE_FOUNDATION_STABLE
            );

        } else {

            reasons.add(
                    ResourcePatternReason
                            .RESOURCE_FOUNDATION_REQUIRES_REVIEW
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
                    ResourcePatternReason
                            .DAY_MASTER_STRONG_SIDE
            );
        }

        if (weakSide) {

            reasons.add(
                    ResourcePatternReason
                            .DAY_MASTER_WEAK_SIDE
            );
        }

        if (mixedStrength) {

            reasons.add(
                    ResourcePatternReason
                            .DAY_MASTER_MIXED_STRENGTH
            );
        }

        /*
         * =================================================
         * 3. 십성 배치
         * =================================================
         */

        boolean directOfficerPresent =
                interaction
                        .hasDirectOfficer();

        boolean directOfficerExposed =
                interaction
                        .directOfficerExposed();

        boolean sevenKillingsPresent =
                interaction
                        .hasSevenKillings();

        boolean sevenKillingsExposed =
                interaction
                        .sevenKillingsExposed();

        boolean wealthExposed =
                interaction
                        .wealthExposed();

        boolean outputExposed =
                interaction
                        .outputExposed();

        boolean eatingGodExposed =
                interaction
                        .hasVisible(
                                MonthCommandPatternCandidateType
                                        .EATING_GOD
                        );

        boolean hurtingOfficerExposed =
                interaction
                        .hasVisible(
                                MonthCommandPatternCandidateType
                                        .HURTING_OFFICER
                        );

        boolean indirectResourceExposed =
                interaction
                        .hasVisible(
                                MonthCommandPatternCandidateType
                                        .INDIRECT_RESOURCE
                        );

        if (directOfficerPresent) {

            reasons.add(
                    ResourcePatternReason
                            .DIRECT_OFFICER_PRESENT
            );
        }

        if (directOfficerExposed) {

            reasons.add(
                    ResourcePatternReason
                            .DIRECT_OFFICER_EXPOSED
            );
        }

        if (sevenKillingsPresent) {

            reasons.add(
                    ResourcePatternReason
                            .SEVEN_KILLINGS_PRESENT
            );
        }

        if (sevenKillingsExposed) {

            reasons.add(
                    ResourcePatternReason
                            .SEVEN_KILLINGS_EXPOSED
            );
        }

        if (wealthExposed) {

            reasons.add(
                    ResourcePatternReason
                            .WEALTH_EXPOSED
            );
        }

        if (outputExposed) {

            reasons.add(
                    ResourcePatternReason
                            .OUTPUT_EXPOSED
            );
        }

        if (eatingGodExposed) {

            reasons.add(
                    ResourcePatternReason
                            .EATING_GOD_EXPOSED
            );
        }

        if (hurtingOfficerExposed) {

            reasons.add(
                    ResourcePatternReason
                            .HURTING_OFFICER_EXPOSED
            );
        }

        if (indirectResourceExposed) {

            reasons.add(
                    ResourcePatternReason
                            .INDIRECT_RESOURCE_EXPOSED
            );
        }

        /*
         * =================================================
         * 4. 官印雙全
         * =================================================
         */

        boolean officerResourceRoute =
                directOfficerExposed
                        &&
                        signals
                                .officerResourceChainIntact();

        if (
                signals
                        .officerResourceChainIntact()
        ) {

            reasons.add(
                    ResourcePatternReason
                            .OFFICER_RESOURCE_CHAIN_INTACT
            );
        }

        if (officerResourceRoute) {

            reasons.add(
                    ResourcePatternReason
                            .OFFICER_RESOURCE_ROUTE
            );
        }

        /*
         * =================================================
         * 5. 七殺生印
         * =================================================
         *
         * 七殺 존재만으로 인정하지 않는다.
         *
         * 실제 印을 적절히 생하는 구조라는
         * 별도 신호가 필요하다.
         */

        boolean killingResourceRoute =
                sevenKillingsExposed
                        &&
                        signals
                                .sevenKillingsAppropriatelySupportsResource();

        if (killingResourceRoute) {

            reasons.add(
                    ResourcePatternReason
                            .SEVEN_KILLINGS_SUPPORTS_RESOURCE
            );
        }

        /*
         * 身強印重而透煞
         *
         * 현재 강약 결과 하나만으로
         * 印重을 추측하지 않는다.
         *
         * 실제 과다 Context가 확인된 경우만
         * 파격 신호를 사용한다.
         */

        boolean excessiveKillingContext =
                sevenKillingsExposed
                        &&
                        signals
                                .strongResourceWithExcessKilling();

        if (excessiveKillingContext) {

            reasons.add(
                    ResourcePatternReason
                            .STRONG_RESOURCE_WITH_EXCESS_KILLING
            );
        }

        boolean killingControlledByOutput =
                sevenKillingsExposed
                        &&
                        signals
                                .sevenKillingsControlledByOutput();

        if (killingControlledByOutput) {

            reasons.add(
                    ResourcePatternReason
                            .SEVEN_KILLINGS_CONTROLLED_BY_OUTPUT
            );
        }

        /*
         * =================================================
         * 6. 官殺混雜
         * =================================================
         */

        boolean officerKillingsMixed =
                directOfficerExposed
                        &&
                        sevenKillingsExposed;

        if (officerKillingsMixed) {

            reasons.add(
                    ResourcePatternReason
                            .OFFICER_AND_SEVEN_KILLINGS_MIXED
            );
        }

        boolean mixedKillingsCleared =
                officerKillingsMixed
                        &&
                        signals
                                .mixedSevenKillingsClearedByCombination();

        boolean mixedKillingsControlled =
                officerKillingsMixed
                        &&
                        signals
                                .mixedOfficerKillingsControlled();

        if (mixedKillingsCleared) {

            reasons.add(
                    ResourcePatternReason
                            .SEVEN_KILLINGS_CLEARED_BY_COMBINATION
            );
        }

        if (mixedKillingsControlled) {

            reasons.add(
                    ResourcePatternReason
                            .OFFICER_KILLINGS_MIXTURE_CONTROLLED
            );
        }

        boolean mixedKillingsResolved =
                mixedKillingsCleared
                        ||
                        mixedKillingsControlled;

        boolean mixedKillingsUnresolved =
                officerKillingsMixed
                        &&
                        !mixedKillingsResolved;

        if (mixedKillingsUnresolved) {

            reasons.add(
                    ResourcePatternReason
                            .OFFICER_KILLINGS_MIXTURE_UNRESOLVED
            );
        }

        /*
         * =================================================
         * 7. 身印兩旺而用食傷
         * =================================================
         */

        boolean outputVentRoute =
                strongSide
                        &&
                        outputExposed;

        if (outputVentRoute) {

            reasons.add(
                    ResourcePatternReason
                            .STRONG_BODY_RESOURCE_USES_OUTPUT
            );
        }

        boolean excessiveOutputDrain =
                outputExposed
                        &&
                        signals
                                .outputExcessivelyDrainsWeakStructure();

        if (excessiveOutputDrain) {

            reasons.add(
                    ResourcePatternReason
                            .OUTPUT_EXCESSIVELY_DRAINS_WEAK_STRUCTURE
            );
        }

        /*
         * =================================================
         * 8. 梟奪食
         * =================================================
         *
         * 偏印이 보인다는 이유만으로
         * 食神을 빼앗았다고 하지 않는다.
         */

        boolean indirectResourceSuppressesFood =
                eatingGodExposed
                        &&
                        indirectResourceExposed
                        &&
                        signals
                                .indirectResourceActuallySuppressesEatingGod();

        if (indirectResourceSuppressesFood) {

            reasons.add(
                    ResourcePatternReason
                            .INDIRECT_RESOURCE_SUPPRESSES_EATING_GOD
            );
        }

        /*
         * =================================================
         * 9. 印多用財
         * =================================================
         *
         * strongSide 하나만으로
         * 印多를 확정하지 않는다.
         *
         * 실제 財가 印의 과다를
         * 적절히 조절하는 구조라는
         * 신호가 필요하다.
         */

        boolean wealthModerationRoute =
                wealthExposed
                        &&
                        strongSide
                        &&
                        signals
                                .resourceFoundationStable()
                        &&
                        signals
                                .wealthModeratesExcessResource();

        if (wealthModerationRoute) {

            reasons.add(
                    ResourcePatternReason
                            .WEALTH_MODERATES_EXCESS_RESOURCE
            );
        }

        /*
         * =================================================
         * 10. 財破印
         * =================================================
         */

        boolean wealthDamage =
                wealthExposed
                        &&
                        signals
                                .wealthActuallyDamagesResource();

        if (wealthDamage) {

            reasons.add(
                    ResourcePatternReason
                            .WEALTH_ACTUALLY_DAMAGES_RESOURCE
            );
        }

        boolean wealthDamageResolvedByRob =
                wealthDamage
                        &&
                        signals
                                .wealthDamageResolvedByRobWealth();

        boolean wealthCombinedAway =
                wealthDamage
                        &&
                        signals
                                .wealthCombinedAwayPreservingResource();

        if (wealthDamageResolvedByRob) {

            reasons.add(
                    ResourcePatternReason
                            .WEALTH_DAMAGE_RESOLVED_BY_ROB_WEALTH
            );
        }

        if (wealthCombinedAway) {

            reasons.add(
                    ResourcePatternReason
                            .WEALTH_COMBINED_AWAY_PRESERVING_RESOURCE
            );
        }

        boolean wealthDamageResolved =
                wealthDamageResolvedByRob
                        ||
                        wealthCombinedAway;

        boolean wealthDamageUnresolved =
                wealthDamage
                        &&
                        !wealthDamageResolved;

        if (wealthDamageUnresolved) {

            reasons.add(
                    ResourcePatternReason
                            .WEALTH_DAMAGE_UNRESOLVED
            );
        }

        /*
         * =================================================
         * 11. 財破印 敗中有救
         * =================================================
         *
         * 印逢財
         * → 劫財以解
         *
         * 또는
         *
         * → 合財存印
         */

        if (
                wealthDamage
                        &&
                        wealthDamageResolved
        ) {

            reasons.add(
                    ResourcePatternReason
                            .RESOURCE_PATTERN_RESCUED
            );

            return result(
                    selectedPattern,
                    PatternStatus.BROKEN_WITH_RESCUE,
                    reasons
            );
        }

        /*
         * =================================================
         * 12. 財破印 미해결
         * =================================================
         */

        if (wealthDamageUnresolved) {

            reasons.add(
                    ResourcePatternReason
                            .RESOURCE_PATTERN_BROKEN
            );

            return result(
                    selectedPattern,
                    PatternStatus.BROKEN,
                    reasons
            );
        }

        /*
         * =================================================
         * 13. 身弱/印淺 구조에서 과도한 食傷
         * =================================================
         */

        if (excessiveOutputDrain) {

            reasons.add(
                    ResourcePatternReason
                            .RESOURCE_PATTERN_BROKEN
            );

            return result(
                    selectedPattern,
                    PatternStatus.BROKEN,
                    reasons
            );
        }

        /*
         * =================================================
         * 14. 身強印重而透煞
         * =================================================
         *
         * 다만 七殺이 食傷에 의해 실제로 조절되면
         * 별도 救應 Context를 인정한다.
         */

        if (
                excessiveKillingContext
                        &&
                        !killingControlledByOutput
        ) {

            reasons.add(
                    ResourcePatternReason
                            .RESOURCE_PATTERN_BROKEN
            );

            return result(
                    selectedPattern,
                    PatternStatus.BROKEN,
                    reasons
            );
        }

        if (
                excessiveKillingContext
                        &&
                        killingControlledByOutput
        ) {

            reasons.add(
                    ResourcePatternReason
                            .RESOURCE_PATTERN_RESCUED
            );

            return result(
                    selectedPattern,
                    PatternStatus.BROKEN_WITH_RESCUE,
                    reasons
            );
        }

        /*
         * =================================================
         * 15. 官殺混雜 후 정리됨
         * =================================================
         *
         * 印兼透官煞
         * → 合煞
         * 또는 有制
         */

        if (
                officerKillingsMixed
                        &&
                        mixedKillingsResolved
        ) {

            reasons.add(
                    ResourcePatternReason
                            .RESOURCE_PATTERN_RESCUED
            );

            return result(
                    selectedPattern,
                    PatternStatus.BROKEN_WITH_RESCUE,
                    reasons
            );
        }

        /*
         * 官殺 혼잡은 있으나
         * 아직 取清 여부가 해결되지 않음.
         */

        if (mixedKillingsUnresolved) {

            return result(
                    selectedPattern,
                    PatternStatus.REVIEW_REQUIRED,
                    reasons
            );
        }

        /*
         * =================================================
         * 16. 印透食洩氣 + 財露
         *
         * → 成中有敗
         * =================================================
         */

        if (
                outputVentRoute
                        &&
                        wealthExposed
                        &&
                        !wealthModerationRoute
        ) {

            reasons.add(
                    ResourcePatternReason
                            .OUTPUT_ROUTE_WITH_WEALTH_DEFECT
            );

            reasons.add(
                    ResourcePatternReason
                            .RESOURCE_FORMATION_WITH_DEFECT
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED_WITH_DEFECT,
                    reasons
            );
        }

        /*
         * =================================================
         * 17. 透煞生印 + 又透財
         *
         * → 成中有敗
         * =================================================
         */

        if (
                killingResourceRoute
                        &&
                        wealthExposed
                        &&
                        !wealthModerationRoute
        ) {

            reasons.add(
                    ResourcePatternReason
                            .KILLING_RESOURCE_ROUTE_WITH_WEALTH_DEFECT
            );

            reasons.add(
                    ResourcePatternReason
                            .RESOURCE_FORMATION_WITH_DEFECT
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED_WITH_DEFECT,
                    reasons
            );
        }

        /*
         * =================================================
         * 18. 食神洩秀 + 梟奪食
         *
         * → 成中有敗
         * =================================================
         */

        if (
                outputVentRoute
                        &&
                        eatingGodExposed
                        &&
                        indirectResourceSuppressesFood
        ) {

            reasons.add(
                    ResourcePatternReason
                            .OUTPUT_ROUTE_WITH_INDIRECT_RESOURCE_DEFECT
            );

            reasons.add(
                    ResourcePatternReason
                            .RESOURCE_FORMATION_WITH_DEFECT
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED_WITH_DEFECT,
                    reasons
            );
        }

        /*
         * =================================================
         * 19. 官印雙全
         * =================================================
         */

        if (
                officerResourceRoute
                        &&
                        !mixedKillingsUnresolved
        ) {

            reasons.add(
                    ResourcePatternReason
                            .CLEAN_RESOURCE_FORMATION
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED,
                    reasons
            );
        }

        /*
         * =================================================
         * 20. 印輕逢煞 계열
         * =================================================
         *
         * 실제 七殺이 印을 적절히 생하는 구조가
         * 확인된 경우만 成格.
         */

        if (
                killingResourceRoute
                        &&
                        !excessiveKillingContext
        ) {

            reasons.add(
                    ResourcePatternReason
                            .CLEAN_RESOURCE_FORMATION
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED,
                    reasons
            );
        }

        /*
         * =================================================
         * 21. 身印兩旺用食傷
         * =================================================
         */

        if (
                outputVentRoute
                        &&
                        !indirectResourceSuppressesFood
                        &&
                        !wealthExposed
        ) {

            reasons.add(
                    ResourcePatternReason
                            .CLEAN_RESOURCE_FORMATION
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED,
                    reasons
            );
        }

        /*
         * =================================================
         * 22. 印多用財
         * =================================================
         */

        if (wealthModerationRoute) {

            reasons.add(
                    ResourcePatternReason
                            .CLEAN_RESOURCE_FORMATION
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED,
                    reasons
            );
        }

        /*
         * =================================================
         * 23. 七殺 + 食傷
         * =================================================
         *
         * 七殺이 있고 食傷이 실제로 制하면
         * 生身과 洩身 구조를 함께 갖는
         * 별도 成格 Context.
         */

        if (
                sevenKillingsExposed
                        &&
                        outputExposed
                        &&
                        killingControlledByOutput
                        &&
                        !wealthDamage
        ) {

            reasons.add(
                    ResourcePatternReason
                            .CLEAN_RESOURCE_FORMATION
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED,
                    reasons
            );
        }

        /*
         * =================================================
         * 24. 구조 미확정
         * =================================================
         *
         * 印格이라는 이유만으로
         * 자동 成格하지 않는다.
         */

        reasons.add(
                ResourcePatternReason
                        .RESOURCE_PATTERN_REQUIRES_REVIEW
        );

        return result(
                selectedPattern,
                PatternStatus.REVIEW_REQUIRED,
                reasons
        );
    }

    private PatternAssessment<ResourcePatternReason> result(

            MonthCommandPatternCandidate selectedPattern,

            PatternStatus status,

            EnumSet<ResourcePatternReason> reasons

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
                        .DIRECT_RESOURCE

                        &&

                        type
                                != MonthCommandPatternCandidateType
                                .INDIRECT_RESOURCE
        ) {

            throw new IllegalArgumentException(
                    "ResourcePatternRule에는 DIRECT_RESOURCE 또는 INDIRECT_RESOURCE 후보만 전달할 수 있습니다."
            );
        }

        if (
                selectedPattern.dayMaster()
                        != interaction.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "印格 후보와 Interaction 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                selectedPattern.dayMaster()
                        != strength.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "印格 후보와 왕쇠 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                strength.requiresSpecialStructureReview()
        ) {

            throw new IllegalArgumentException(
                    "특수구조 검토 상태를 일반 印格 Rule에 전달할 수 없습니다."
            );
        }
    }
}