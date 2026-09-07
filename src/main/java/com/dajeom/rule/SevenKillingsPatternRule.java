package com.dajeom.rule;

import com.dajeom.domain.DayMasterStrengthAnalysis;
import com.dajeom.domain.MonthCommandPatternCandidate;
import com.dajeom.domain.MonthCommandPatternCandidateType;
import com.dajeom.domain.PatternAssessment;
import com.dajeom.domain.PatternInteractionAnalysis;
import com.dajeom.domain.PatternStatus;
import com.dajeom.domain.SevenKillingsPatternReason;
import com.dajeom.domain.SevenKillingsPatternStructuralSignals;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Objects;

/**
 * 七殺格 成敗 Rule.
 *
 * =================================================
 *
 * 주요 成格:
 *
 * 1. 身強殺旺而食神有制
 *
 * 2. 食神制殺
 *
 * 3. 身弱而殺印相生
 *
 * 4. 財殺印이 실제 순환하여
 *    身으로 이어지는 구조
 *
 * =================================================
 *
 * 주요 破格:
 *
 * 1. 財滋七殺而無制化
 *
 * 2. 食神制殺인데 印이 食을 억누름
 *
 * 3. 殺印相生의 印이 財에 의해 손상
 *
 * 4. 월령 七殺 자체의 기반이 심하게 깨짐
 *
 * =================================================
 *
 * 成中有敗:
 *
 * 食神制殺
 * +
 * 印이 다시 食神을 억누름
 *
 * =================================================
 *
 * 敗中有救:
 *
 * 食制又逢印
 * +
 * 財去印存食
 *
 * =================================================
 *
 * 官殺混雜은
 * 단순 존재만으로 바로 破格 처리하지 않는다.
 *
 * 실제 取清 여부를 본다.
 *
 * =================================================
 *
 * 숫자 / 점수 / 가중치 / 다수결 없음.
 */
@Component
public class SevenKillingsPatternRule {

    public PatternAssessment<SevenKillingsPatternReason> assess(

            MonthCommandPatternCandidate selectedPattern,

            PatternInteractionAnalysis interaction,

            DayMasterStrengthAnalysis strength,

            SevenKillingsPatternStructuralSignals signals

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
                "七殺格 구조 신호는 필수입니다."
        );

        validate(
                selectedPattern,
                interaction,
                strength
        );

        EnumSet<SevenKillingsPatternReason> reasons =
                EnumSet.of(
                        SevenKillingsPatternReason
                                .SEVEN_KILLINGS_PATTERN
                );

        /*
         * =================================================
         * 1. 七殺 자체
         * =================================================
         */

        if (selectedPattern.exposed()) {

            reasons.add(
                    SevenKillingsPatternReason
                            .SEVEN_KILLINGS_EXPOSED
            );

        } else {

            reasons.add(
                    SevenKillingsPatternReason
                            .SEVEN_KILLINGS_NOT_EXPOSED
            );
        }

        if (
                signals
                        .sevenKillingsFoundationStable()
        ) {

            reasons.add(
                    SevenKillingsPatternReason
                            .SEVEN_KILLINGS_FOUNDATION_STABLE
            );

        } else {

            reasons.add(
                    SevenKillingsPatternReason
                            .SEVEN_KILLINGS_FOUNDATION_REQUIRES_REVIEW
            );
        }

        if (
                signals
                        .sevenKillingsStemCombined()
        ) {

            reasons.add(
                    SevenKillingsPatternReason
                            .SEVEN_KILLINGS_STEM_COMBINED
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
                    SevenKillingsPatternReason
                            .DAY_MASTER_STRONG_SIDE
            );
        }

        if (weakSide) {

            reasons.add(
                    SevenKillingsPatternReason
                            .DAY_MASTER_WEAK_SIDE
            );
        }

        if (mixedStrength) {

            reasons.add(
                    SevenKillingsPatternReason
                            .DAY_MASTER_MIXED_STRENGTH
            );
        }

        /*
         * =================================================
         * 3. 십성 배치
         * =================================================
         */

        boolean eatingGodPresent =
                interaction.hasEatingGod();

        boolean eatingGodExposed =
                interaction.hasVisible(
                        MonthCommandPatternCandidateType
                                .EATING_GOD
                );

        boolean resourcePresent =
                interaction.hasDirectResource()
                        ||
                        interaction.hasIndirectResource();

        boolean resourceExposed =
                interaction.resourceExposed();

        boolean wealthPresent =
                interaction.hasDirectWealth()
                        ||
                        interaction.hasIndirectWealth();

        boolean wealthExposed =
                interaction.wealthExposed();

        boolean directOfficerPresent =
                interaction.hasDirectOfficer();

        boolean directOfficerExposed =
                interaction.directOfficerExposed();

        if (eatingGodPresent) {

            reasons.add(
                    SevenKillingsPatternReason
                            .EATING_GOD_PRESENT
            );
        }

        if (eatingGodExposed) {

            reasons.add(
                    SevenKillingsPatternReason
                            .EATING_GOD_EXPOSED
            );
        }

        if (resourcePresent) {

            reasons.add(
                    SevenKillingsPatternReason
                            .RESOURCE_PRESENT
            );
        }

        if (resourceExposed) {

            reasons.add(
                    SevenKillingsPatternReason
                            .RESOURCE_EXPOSED
            );
        }

        if (wealthPresent) {

            reasons.add(
                    SevenKillingsPatternReason
                            .WEALTH_PRESENT
            );
        }

        if (wealthExposed) {

            reasons.add(
                    SevenKillingsPatternReason
                            .WEALTH_EXPOSED
            );
        }

        if (directOfficerPresent) {

            reasons.add(
                    SevenKillingsPatternReason
                            .DIRECT_OFFICER_PRESENT
            );
        }

        if (directOfficerExposed) {

            reasons.add(
                    SevenKillingsPatternReason
                            .DIRECT_OFFICER_EXPOSED
            );
        }

        /*
         * =================================================
         * 4. 食神制殺
         * =================================================
         */

        boolean eatingGodFoundationStable =
                eatingGodExposed
                        &&
                        signals
                                .eatingGodFoundationStable();

        if (eatingGodFoundationStable) {

            reasons.add(
                    SevenKillingsPatternReason
                            .EATING_GOD_FOUNDATION_STABLE
            );
        }

        boolean foodControlsKilling =
                eatingGodExposed
                        &&
                        signals
                                .eatingGodActuallyControlsSevenKillings();

        if (foodControlsKilling) {

            reasons.add(
                    SevenKillingsPatternReason
                            .EATING_GOD_CONTROLS_SEVEN_KILLINGS
            );
        }

        boolean foodControlRoute =
                eatingGodExposed
                        &&
                        signals
                                .foodControlRouteStable();

        if (foodControlRoute) {

            reasons.add(
                    SevenKillingsPatternReason
                            .FOOD_CONTROLS_KILLING_ROUTE
            );
        }

        /*
         * =================================================
         * 5. 殺印相生
         * =================================================
         */

        boolean resourceFoundationStable =
                resourceExposed
                        &&
                        signals
                                .resourceFoundationStable();

        if (resourceFoundationStable) {

            reasons.add(
                    SevenKillingsPatternReason
                            .RESOURCE_FOUNDATION_STABLE
            );
        }

        boolean killingGeneratesResource =
                resourceExposed
                        &&
                        signals
                                .killingGeneratesResourceToSupportDayMaster();

        if (killingGeneratesResource) {

            reasons.add(
                    SevenKillingsPatternReason
                            .KILLING_GENERATES_RESOURCE
            );
        }

        boolean killingResourceRoute =
                resourceExposed
                        &&
                        signals
                                .killingResourceRouteStable();

        if (killingResourceRoute) {

            reasons.add(
                    SevenKillingsPatternReason
                            .KILLING_RESOURCE_ROUTE
            );
        }

        /*
         * =================================================
         * 6. 食制又逢印
         * =================================================
         *
         * 印 존재만으로
         * 食神制殺을 깨지 않는다.
         *
         * 印이 실제 食神을 억누르는지를 본다.
         */

        boolean resourceSuppressesFood =
                foodControlRoute
                        &&
                        resourceExposed
                        &&
                        signals
                                .resourceActuallySuppressesEatingGod();

        if (resourceSuppressesFood) {

            reasons.add(
                    SevenKillingsPatternReason
                            .RESOURCE_SUPPRESSES_EATING_GOD
            );

            reasons.add(
                    SevenKillingsPatternReason
                            .FOOD_CONTROL_DAMAGED_BY_RESOURCE
            );
        }

        /*
         * =================================================
         * 7. 財去印存食
         * =================================================
         */

        boolean wealthRemovesResource =
                resourceSuppressesFood
                        &&
                        wealthExposed
                        &&
                        signals
                                .wealthRemovesResourcePreservingEatingGod();

        if (wealthRemovesResource) {

            reasons.add(
                    SevenKillingsPatternReason
                            .WEALTH_REMOVES_RESOURCE_PRESERVING_EATING_GOD
            );

            reasons.add(
                    SevenKillingsPatternReason
                            .FOOD_CONTROL_RESCUED_BY_WEALTH
            );
        }

        /*
         * =================================================
         * 8. 財滋殺
         * =================================================
         */

        boolean wealthStrengthensKilling =
                wealthExposed
                        &&
                        signals
                                .wealthActuallyStrengthensSevenKillings();

        if (wealthStrengthensKilling) {

            reasons.add(
                    SevenKillingsPatternReason
                            .WEALTH_STRENGTHENS_SEVEN_KILLINGS
            );
        }

        /*
         * 財 → 殺이라고 모두 파격은 아니다.
         *
         * 殺 → 印 → 身까지 실제로 이어지면
         * 별도의 순환 Context로 본다.
         */
        boolean wealthKillingResourceChain =
                wealthStrengthensKilling
                        &&
                        killingResourceRoute
                        &&
                        signals
                                .wealthKillingResourceChainUsable();

        if (wealthKillingResourceChain) {

            reasons.add(
                    SevenKillingsPatternReason
                            .WEALTH_KILLING_RESOURCE_CHAIN_USABLE
            );
        }

        boolean wealthStrengthensKillingWithoutControl =
                wealthStrengthensKilling
                        &&
                        !foodControlRoute
                        &&
                        !killingResourceRoute
                        &&
                        !wealthKillingResourceChain;

        if (wealthStrengthensKillingWithoutControl) {

            reasons.add(
                    SevenKillingsPatternReason
                            .WEALTH_STRENGTHENS_KILLING_WITHOUT_CONTROL
            );
        }

        /*
         * =================================================
         * 9. 殺印相生에 財가 印을 손상
         * =================================================
         */

        boolean wealthDamagesResourceRoute =
                killingResourceRoute
                        &&
                        wealthExposed
                        &&
                        signals
                                .wealthDamagesResourceRoute();

        if (wealthDamagesResourceRoute) {

            reasons.add(
                    SevenKillingsPatternReason
                            .WEALTH_DAMAGES_RESOURCE_ROUTE
            );
        }

        /*
         * =================================================
         * 10. 官殺混雜
         * =================================================
         */

        boolean officerKillingsMixed =
                directOfficerExposed;

        /*
         * 현재 월령 격 자체가 七殺이므로
         * 正官이 천간에 투출하면
         * 官殺並見 Context가 된다.
         */
        if (officerKillingsMixed) {

            reasons.add(
                    SevenKillingsPatternReason
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
                    SevenKillingsPatternReason
                            .OFFICER_KILLINGS_MIXTURE_CLEARED
            );
        }

        boolean officerKillingsUnresolved =
                officerKillingsMixed
                        &&
                        !officerKillingsCleared;

        if (officerKillingsUnresolved) {

            reasons.add(
                    SevenKillingsPatternReason
                            .OFFICER_KILLINGS_MIXTURE_UNRESOLVED
            );
        }

        /*
         * =================================================
         * 11. 月令 자체의 충형파해
         * =================================================
         */

        if (
                signals
                        .monthCommandDisrupted()
        ) {

            reasons.add(
                    SevenKillingsPatternReason
                            .MONTH_COMMAND_DISRUPTED
            );
        }

        if (
                signals
                        .monthCommandDisruptionResolved()
        ) {

            reasons.add(
                    SevenKillingsPatternReason
                            .MONTH_COMMAND_DISRUPTION_RESOLVED
            );
        }

        boolean monthCommandDisruptionUnresolved =
                signals
                        .unresolvedMonthCommandDisruption();

        if (monthCommandDisruptionUnresolved) {

            reasons.add(
                    SevenKillingsPatternReason
                            .MONTH_COMMAND_DISRUPTION_UNRESOLVED
            );
        }

        /*
         * =================================================
         * 12. 敗中有救
         *
         * 食神制殺
         * +
         * 印奪食
         * +
         * 財去印存食
         * =================================================
         */

        if (
                resourceSuppressesFood
                        &&
                        wealthRemovesResource
                        &&
                        !monthCommandDisruptionUnresolved
        ) {

            reasons.add(
                    SevenKillingsPatternReason
                            .SEVEN_KILLINGS_PATTERN_RESCUED
            );

            return result(
                    selectedPattern,
                    PatternStatus.BROKEN_WITH_RESCUE,
                    reasons
            );
        }

        /*
         * =================================================
         * 13. 財滋殺無制
         * =================================================
         */

        if (wealthStrengthensKillingWithoutControl) {

            reasons.add(
                    SevenKillingsPatternReason
                            .SEVEN_KILLINGS_PATTERN_BROKEN
            );

            return result(
                    selectedPattern,
                    PatternStatus.BROKEN,
                    reasons
            );
        }

        /*
         * =================================================
         * 14. 殺印相生인데 財가 印을 깨뜨림
         * =================================================
         */

        if (
                wealthDamagesResourceRoute
                        &&
                        !wealthKillingResourceChain
        ) {

            reasons.add(
                    SevenKillingsPatternReason
                            .SEVEN_KILLINGS_PATTERN_BROKEN
            );

            return result(
                    selectedPattern,
                    PatternStatus.BROKEN,
                    reasons
            );
        }

        /*
         * =================================================
         * 15. 月令 七殺 기반 자체가 미해결 교란
         * =================================================
         */

        if (monthCommandDisruptionUnresolved) {

            reasons.add(
                    SevenKillingsPatternReason
                            .SEVEN_KILLINGS_PATTERN_BROKEN
            );

            return result(
                    selectedPattern,
                    PatternStatus.BROKEN,
                    reasons
            );
        }

        /*
         * =================================================
         * 16. 食神制殺 + 印奪食
         *
         * 財去印存食까지는 없음
         *
         * → 成中有敗
         * =================================================
         */

        if (
                resourceSuppressesFood
                        &&
                        !wealthRemovesResource
        ) {

            reasons.add(
                    SevenKillingsPatternReason
                            .SEVEN_KILLINGS_FORMATION_WITH_DEFECT
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED_WITH_DEFECT,
                    reasons
            );
        }

        /*
         * =================================================
         * 17. 七殺 자체가 합됨
         * =================================================
         *
         * 七殺格의 用 자체가 묶인 경우.
         *
         * 무조건 破格으로 단정하기보다
         * 합거 후 다른 格으로 변하는지
         * 추가 검토가 필요하다.
         */

        if (
                signals
                        .sevenKillingsStemCombined()
        ) {

            return result(
                    selectedPattern,
                    PatternStatus.REVIEW_REQUIRED,
                    reasons
            );
        }

        /*
         * =================================================
         * 18. 官殺混雜
         * =================================================
         */

        if (
                officerKillingsMixed
                        &&
                        !officerKillingsCleared
        ) {

            return result(
                    selectedPattern,
                    PatternStatus.REVIEW_REQUIRED,
                    reasons
            );
        }

        /*
         * =================================================
         * 19. 食神制殺
         * =================================================
         *
         * 특히 身強에서 대표적인
         * 七殺格 사용 방식.
         *
         * 그러나 strongSide가 아니더라도
         * 실제 食神制殺 구조가 성립할 수 있으므로
         * strong 여부만으로 배제하지 않는다.
         */

        if (
                foodControlRoute
                        &&
                        !resourceSuppressesFood
                        &&
                        !wealthStrengthensKillingWithoutControl
        ) {

            reasons.add(
                    SevenKillingsPatternReason
                            .CLEAN_SEVEN_KILLINGS_FORMATION
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED,
                    reasons
            );
        }

        /*
         * =================================================
         * 20. 殺印相生
         * =================================================
         *
         * 특히 身弱에서 중요한 구조.
         */

        if (
                killingResourceRoute
                        &&
                        weakSide
                        &&
                        !wealthDamagesResourceRoute
        ) {

            reasons.add(
                    SevenKillingsPatternReason
                            .CLEAN_SEVEN_KILLINGS_FORMATION
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED,
                    reasons
            );
        }

        /*
         * =================================================
         * 21. 財 → 殺 → 印 → 身
         * =================================================
         *
         * 財가 七殺을 돕지만
         * 七殺이 다시 印을 생하고
         * 印이 身을 받치는 실제 순환이
         * 확인된 경우.
         */

        if (
                wealthKillingResourceChain
                        &&
                        !wealthDamagesResourceRoute
        ) {

            reasons.add(
                    SevenKillingsPatternReason
                            .CLEAN_SEVEN_KILLINGS_FORMATION
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED,
                    reasons
            );
        }

        /*
         * =================================================
         * 22. 身強 + 七殺 + 실제 制
         * =================================================
         */

        if (
                strongSide
                        &&
                        foodControlsKilling
                        &&
                        eatingGodFoundationStable
        ) {

            reasons.add(
                    SevenKillingsPatternReason
                            .CLEAN_SEVEN_KILLINGS_FORMATION
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED,
                    reasons
            );
        }

        /*
         * =================================================
         * 23. 殺印 구조가 있으나
         * 身이 강한 경우
         * =================================================
         *
         * 강한데 印을 더 받는다고
         * 무조건 좋은 구조로 확정하지 않는다.
         */

        if (
                killingResourceRoute
                        &&
                        strongSide
        ) {

            reasons.add(
                    SevenKillingsPatternReason
                            .SEVEN_KILLINGS_PATTERN_REQUIRES_REVIEW
            );

            return result(
                    selectedPattern,
                    PatternStatus.REVIEW_REQUIRED,
                    reasons
            );
        }

        /*
         * =================================================
         * 24. 七殺 + 食神은 있으나
         * 실제 制殺이 확인되지 않음
         * =================================================
         */

        if (
                eatingGodExposed
                        &&
                        !foodControlRoute
        ) {

            reasons.add(
                    SevenKillingsPatternReason
                            .SEVEN_KILLINGS_PATTERN_REQUIRES_REVIEW
            );

            return result(
                    selectedPattern,
                    PatternStatus.REVIEW_REQUIRED,
                    reasons
            );
        }

        /*
         * =================================================
         * 25. 七殺 + 印은 있으나
         * 실제 殺印相生이 확인되지 않음
         * =================================================
         */

        if (
                resourceExposed
                        &&
                        !killingResourceRoute
        ) {

            reasons.add(
                    SevenKillingsPatternReason
                            .SEVEN_KILLINGS_PATTERN_REQUIRES_REVIEW
            );

            return result(
                    selectedPattern,
                    PatternStatus.REVIEW_REQUIRED,
                    reasons
            );
        }

        /*
         * =================================================
         * 26. 財는 있지만
         * 실제 財滋殺 여부 미확정
         * =================================================
         */

        if (
                wealthExposed
                        &&
                        !wealthStrengthensKilling
        ) {

            reasons.add(
                    SevenKillingsPatternReason
                            .SEVEN_KILLINGS_PATTERN_REQUIRES_REVIEW
            );

            return result(
                    selectedPattern,
                    PatternStatus.REVIEW_REQUIRED,
                    reasons
            );
        }

        /*
         * =================================================
         * 27. 七殺 단독
         * =================================================
         *
         * 七殺이 월령이라는 이유만으로
         * 자동 成格 / 破格하지 않는다.
         */

        reasons.add(
                SevenKillingsPatternReason
                        .SEVEN_KILLINGS_PATTERN_REQUIRES_REVIEW
        );

        return result(
                selectedPattern,
                PatternStatus.REVIEW_REQUIRED,
                reasons
        );
    }

    private PatternAssessment<SevenKillingsPatternReason> result(

            MonthCommandPatternCandidate selectedPattern,

            PatternStatus status,

            EnumSet<SevenKillingsPatternReason> reasons

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
                        .SEVEN_KILLINGS
        ) {

            throw new IllegalArgumentException(
                    "SevenKillingsPatternRule에는 SEVEN_KILLINGS 후보만 전달할 수 있습니다."
            );
        }

        if (
                selectedPattern.dayMaster()
                        != interaction.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "七殺格 후보와 Interaction 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                selectedPattern.dayMaster()
                        != strength.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "七殺格 후보와 왕쇠 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                strength.requiresSpecialStructureReview()
        ) {

            throw new IllegalArgumentException(
                    "특수구조 검토 상태를 일반 七殺格 Rule에 전달할 수 없습니다."
            );
        }
    }
}