package com.dajeom.rule;

import com.dajeom.domain.DirectOfficerPatternReason;
import com.dajeom.domain.DirectOfficerStructuralSignals;
import com.dajeom.domain.MonthCommandPatternCandidate;
import com.dajeom.domain.MonthCommandPatternCandidateType;
import com.dajeom.domain.PatternAssessment;
import com.dajeom.domain.PatternInteractionAnalysis;
import com.dajeom.domain.PatternStatus;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Objects;

/**
 * 正官格의 成格 / 破格 / 成中有敗 / 敗中有救 판정.
 *
 * =================================================
 *
 * 子平真詮의 기본 구조:
 *
 * 官逢財印
 * 無刑沖破害
 * → 官格成
 *
 * 官逢傷克刑沖
 * → 官格敗
 *
 * 正官逢財而又逢傷
 * → 成中有敗
 *
 * 透官而又逢合
 * → 成中有敗
 *
 * 官逢傷而透印以解
 * → 敗中有救
 *
 * 雜煞而合煞以清
 * → 敗中有救
 *
 * 刑沖而會合以解
 * → 敗中有救
 *
 * =================================================
 *
 * 중요:
 *
 * 단순 십성 존재만으로
 * 모든 구조를 확정하지 않는다.
 *
 * 합/충/형/파/해 등의 관계는
 * DirectOfficerStructuralSignals를 통해 받는다.
 *
 * =================================================
 *
 * 숫자 / 가중치 / 다수결 없음.
 */
@Component
public class DirectOfficerPatternRule {

    public PatternAssessment<DirectOfficerPatternReason> assess(

            MonthCommandPatternCandidate selectedPattern,

            PatternInteractionAnalysis interaction,

            DirectOfficerStructuralSignals signals

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
                signals,
                "正官格 구조 신호는 필수입니다."
        );

        if (
                selectedPattern.candidateType()
                        != MonthCommandPatternCandidateType
                        .DIRECT_OFFICER
        ) {

            throw new IllegalArgumentException(
                    "DirectOfficerPatternRule에는 DIRECT_OFFICER 후보만 전달할 수 있습니다."
            );
        }

        if (
                selectedPattern.dayMaster()
                        != interaction.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "격국 후보와 Interaction 분석의 일간이 일치하지 않습니다."
            );
        }

        EnumSet<DirectOfficerPatternReason> reasons =
                EnumSet.of(
                        DirectOfficerPatternReason
                                .DIRECT_OFFICER_PATTERN
                );

        /*
         * =================================================
         * 1. 正官 투출
         * =================================================
         */

        boolean directOfficerExposed =
                interaction
                        .directOfficerExposed();

        if (directOfficerExposed) {

            reasons.add(
                    DirectOfficerPatternReason
                            .DIRECT_OFFICER_EXPOSED
            );

        } else {

            reasons.add(
                    DirectOfficerPatternReason
                            .DIRECT_OFFICER_NOT_EXPOSED
            );
        }

        /*
         * =================================================
         * 2. 財 / 印
         * =================================================
         */

        boolean wealthExposed =
                interaction
                        .wealthExposed();

        boolean resourceExposed =
                interaction
                        .resourceExposed();

        if (wealthExposed) {

            reasons.add(
                    DirectOfficerPatternReason
                            .WEALTH_SUPPORT_PRESENT
            );
        }

        if (resourceExposed) {

            reasons.add(
                    DirectOfficerPatternReason
                            .RESOURCE_SUPPORT_PRESENT
            );
        }

        boolean wealthAndResourceTogether =
                wealthExposed
                        &&
                        resourceExposed;

        boolean wealthResourcePlacementResolved =
                !wealthAndResourceTogether
                        ||
                        signals
                                .wealthAndResourceMutuallyNonObstructing();

        if (
                wealthAndResourceTogether
        ) {

            if (
                    signals
                            .wealthAndResourceMutuallyNonObstructing()
            ) {

                reasons.add(
                        DirectOfficerPatternReason
                                .WEALTH_AND_RESOURCE_NON_OBSTRUCTING
                );

            } else {

                reasons.add(
                        DirectOfficerPatternReason
                                .WEALTH_AND_RESOURCE_PLACEMENT_REQUIRES_REVIEW
                );
            }
        }

        /*
         * =================================================
         * 3. 傷官
         * =================================================
         */

        boolean hurtingOfficerPresent =
                interaction
                        .hasHurtingOfficer();

        boolean hurtingOfficerExposed =
                interaction
                        .hasVisible(
                                MonthCommandPatternCandidateType
                                        .HURTING_OFFICER
                        );

        if (hurtingOfficerPresent) {

            reasons.add(
                    DirectOfficerPatternReason
                            .HURTING_OFFICER_PRESENT
            );
        }

        if (hurtingOfficerExposed) {

            reasons.add(
                    DirectOfficerPatternReason
                            .HURTING_OFFICER_EXPOSED
            );
        }

        boolean hurtingOfficerResolved =
                hurtingOfficerPresent
                        &&
                        signals
                                .hurtingOfficerResolvedByResource();

        if (hurtingOfficerResolved) {

            reasons.add(
                    DirectOfficerPatternReason
                            .HURTING_OFFICER_RESOLVED_BY_RESOURCE
            );
        }

        boolean hurtingOfficerUnresolved =
                hurtingOfficerPresent
                        &&
                        !hurtingOfficerResolved;

        if (hurtingOfficerUnresolved) {

            reasons.add(
                    DirectOfficerPatternReason
                            .HURTING_OFFICER_UNRESOLVED
            );
        }

        /*
         * =================================================
         * 4. 官殺混雜
         * =================================================
         */

        boolean sevenKillingsPresent =
                interaction
                        .hasSevenKillings();

        boolean sevenKillingsExposed =
                interaction
                        .sevenKillingsExposed();

        if (sevenKillingsPresent) {

            reasons.add(
                    DirectOfficerPatternReason
                            .SEVEN_KILLINGS_PRESENT
            );
        }

        if (sevenKillingsExposed) {

            reasons.add(
                    DirectOfficerPatternReason
                            .SEVEN_KILLINGS_EXPOSED
            );
        }

        boolean sevenKillingsCleared =
                sevenKillingsPresent
                        &&
                        signals
                                .mixedSevenKillingsClearedByCombination();

        if (sevenKillingsCleared) {

            reasons.add(
                    DirectOfficerPatternReason
                            .SEVEN_KILLINGS_CLEARED_BY_COMBINATION
            );
        }

        boolean sevenKillingsUnresolved =
                sevenKillingsPresent
                        &&
                        !sevenKillingsCleared;

        if (sevenKillingsUnresolved) {

            reasons.add(
                    DirectOfficerPatternReason
                            .SEVEN_KILLINGS_MIXTURE_UNRESOLVED
            );
        }

        /*
         * =================================================
         * 5. 官星 본체의 천간 합
         * =================================================
         */

        if (
                signals.officerStemCombined()
        ) {

            reasons.add(
                    DirectOfficerPatternReason
                            .DIRECT_OFFICER_STEM_COMBINED
            );
        }

        /*
         * =================================================
         * 6. 月令 형충파해
         * =================================================
         */

        if (
                signals.monthCommandClashed()
        ) {

            reasons.add(
                    DirectOfficerPatternReason
                            .MONTH_COMMAND_CLASHED
            );
        }

        if (
                signals.monthCommandPunished()
        ) {

            reasons.add(
                    DirectOfficerPatternReason
                            .MONTH_COMMAND_PUNISHED
            );
        }

        if (
                signals.monthCommandBroken()
        ) {

            reasons.add(
                    DirectOfficerPatternReason
                            .MONTH_COMMAND_BROKEN
            );
        }

        if (
                signals.monthCommandHarmed()
        ) {

            reasons.add(
                    DirectOfficerPatternReason
                            .MONTH_COMMAND_HARMED
            );
        }

        boolean monthDisruption =
                signals
                        .hasMonthCommandDisruption();

        boolean monthDisruptionResolved =
                monthDisruption
                        &&
                        signals
                                .monthCommandDisruptionResolvedByCombination();

        if (monthDisruptionResolved) {

            reasons.add(
                    DirectOfficerPatternReason
                            .MONTH_COMMAND_DISRUPTION_RESOLVED
            );
        }

        boolean monthDisruptionUnresolved =
                monthDisruption
                        &&
                        !monthDisruptionResolved;

        if (monthDisruptionUnresolved) {

            reasons.add(
                    DirectOfficerPatternReason
                            .MONTH_COMMAND_DISRUPTION_UNRESOLVED
            );
        }

        /*
         * =================================================
         * 7. 敗中有救
         * =================================================
         *
         * 官逢傷而透印以解
         *
         * 雜煞而合煞以清
         *
         * 刑沖而會合以解
         *
         * 단:
         * 다른 미해결 파격 요소가 남아 있으면
         * 敗中有救로 섣불리 확정하지 않는다.
         */

        boolean rescuedBreakingContext =
                hurtingOfficerResolved
                        ||
                        sevenKillingsCleared
                        ||
                        monthDisruptionResolved;

        boolean unresolvedMajorBreakingContext =
                hurtingOfficerUnresolved
                        ||
                        monthDisruptionUnresolved;

        if (
                rescuedBreakingContext
                        &&
                        !unresolvedMajorBreakingContext
                        &&
                        !sevenKillingsUnresolved
                        &&
                        !signals.officerStemCombined()
                        &&
                        wealthResourcePlacementResolved
        ) {

            reasons.add(
                    DirectOfficerPatternReason
                            .DIRECT_OFFICER_RESCUED
            );

            return result(
                    selectedPattern,
                    PatternStatus.BROKEN_WITH_RESCUE,
                    reasons
            );
        }

        /*
         * =================================================
         * 8. 月令 형충파해가 미해결
         * =================================================
         *
         * 官格에서 월령 官의 기반 자체가
         * 흔들리는 경우.
         */

        if (
                monthDisruptionUnresolved
        ) {

            reasons.add(
                    DirectOfficerPatternReason
                            .DIRECT_OFFICER_BROKEN
            );

            return result(
                    selectedPattern,
                    PatternStatus.BROKEN,
                    reasons
            );
        }

        /*
         * =================================================
         * 9. 正官逢財而又逢傷
         *
         * → 成中有敗
         * =================================================
         */

        if (
                wealthExposed
                        &&
                        hurtingOfficerUnresolved
        ) {

            reasons.add(
                    DirectOfficerPatternReason
                            .DIRECT_OFFICER_WITH_DEFECT
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED_WITH_DEFECT,
                    reasons
            );
        }

        /*
         * =================================================
         * 10. 傷官 미해결
         * =================================================
         *
         * 財로 이루어진 成中有敗 Context도 아니고
         * 印의 실제 구조적 해결도 없는 경우.
         */

        if (
                hurtingOfficerUnresolved
        ) {

            /*
             * 印도 있고 財도 함께 있는 경우에는
             * 財가 印을 해치는지,
             * 위치상 서로 간섭하지 않는지를
             * 추가로 봐야 한다.
             */
            if (
                    resourceExposed
                            &&
                            wealthExposed
                            &&
                            !wealthResourcePlacementResolved
            ) {

                return result(
                        selectedPattern,
                        PatternStatus.REVIEW_REQUIRED,
                        reasons
                );
            }

            reasons.add(
                    DirectOfficerPatternReason
                            .DIRECT_OFFICER_BROKEN
            );

            return result(
                    selectedPattern,
                    PatternStatus.BROKEN,
                    reasons
            );
        }

        /*
         * =================================================
         * 11. 透官而又逢合
         *
         * → 成中有敗
         * =================================================
         */

        if (
                directOfficerExposed
                        &&
                        signals.officerStemCombined()
        ) {

            reasons.add(
                    DirectOfficerPatternReason
                            .DIRECT_OFFICER_WITH_DEFECT
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED_WITH_DEFECT,
                    reasons
            );
        }

        /*
         * =================================================
         * 12. 官殺混雜 미해결
         * =================================================
         *
         * 바로 破格으로 단정하지 않고
         * 取清 여부를 더 검토해야 한다.
         *
         * 合殺留官 여부는 위에서 이미 확인.
         */

        if (
                sevenKillingsUnresolved
        ) {

            if (
                    wealthExposed
                            ||
                            resourceExposed
            ) {

                reasons.add(
                        DirectOfficerPatternReason
                                .DIRECT_OFFICER_WITH_DEFECT
                );

                return result(
                        selectedPattern,
                        PatternStatus.FORMED_WITH_DEFECT,
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
         * 13. 財印 동시 투출인데
         *     위치 관계 미확정
         * =================================================
         */

        if (
                wealthAndResourceTogether
                        &&
                        !wealthResourcePlacementResolved
        ) {

            return result(
                    selectedPattern,
                    PatternStatus.REVIEW_REQUIRED,
                    reasons
            );
        }

        /*
         * =================================================
         * 14. 깨끗한 正官格
         * =================================================
         *
         * 官逢財印
         * +
         * 無刑沖破害
         * +
         * 傷官 없음
         * +
         * 官殺 혼잡 없음
         * +
         * 官 자체 합 없음
         *
         * 財 또는 印 중 하나 이상의
         * 실제 보좌가 있는 경우.
         */

        boolean supportingStructure =
                wealthExposed
                        ||
                        resourceExposed;

        if (
                supportingStructure
                        &&
                        !monthDisruption
                        &&
                        !hurtingOfficerPresent
                        &&
                        !sevenKillingsPresent
                        &&
                        !signals.officerStemCombined()
        ) {

            reasons.add(
                    DirectOfficerPatternReason
                            .CLEAN_DIRECT_OFFICER_FORMATION
            );

            return result(
                    selectedPattern,
                    PatternStatus.FORMED,
                    reasons
            );
        }

        /*
         * =================================================
         * 15. 孤官 등
         * =================================================
         *
         * 財印 보좌가 전혀 없다고
         * 바로 破格으로 하지 않는다.
         *
         * 子平真詮에서도
         * 孤官無輔를 별도로 논하므로
         * 추가 검토 상태로 남긴다.
         */

        reasons.add(
                DirectOfficerPatternReason
                        .LONE_DIRECT_OFFICER_REQUIRES_REVIEW
        );

        return result(
                selectedPattern,
                PatternStatus.REVIEW_REQUIRED,
                reasons
        );
    }

    private PatternAssessment<DirectOfficerPatternReason> result(

            MonthCommandPatternCandidate selectedPattern,

            PatternStatus status,

            EnumSet<DirectOfficerPatternReason> reasons

    ) {

        return new PatternAssessment<>(
                selectedPattern.dayMaster(),
                selectedPattern,
                status,
                reasons
        );
    }
}