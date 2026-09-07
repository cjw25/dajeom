package com.dajeom.rule;

import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.PatternOfficerKillingsPurificationAnalysis;
import com.dajeom.domain.PatternOfficerKillingsPurificationReason;
import com.dajeom.domain.PatternOfficerKillingsPurificationState;
import com.dajeom.domain.PatternTenGodNode;
import com.dajeom.domain.PatternYangBladeKillingCombinationAnalysis;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

/**
 * 기존 官殺取清 Rule에서
 * 陽日 때문에 REVIEW_REQUIRED로 남겨둔
 *
 * 傷官制官 + 羊刃合殺
 *
 * 구조를 완성한다.
 *
 * =================================================
 *
 * 조건:
 *
 * 陽日
 * +
 * 官殺混雜
 * +
 * 傷官이 모든 투출 正官을 제어
 * +
 * 원국 陽刃 존재
 * +
 * 刃中劫財藏干 ↔ 투출 七殺 active 五合
 *
 * →
 *
 * 去官留殺
 *
 * =================================================
 */
@Component
public class PatternOfficerKillingsYangBladeCompletionRule {

    public PatternOfficerKillingsPurificationAnalysis complete(

            PatternOfficerKillingsPurificationAnalysis base,

            PatternYangBladeKillingCombinationAnalysis yangBladeCombinations

    ) {

        Objects.requireNonNull(
                base,
                "기존 官殺取清 분석은 필수입니다."
        );

        Objects.requireNonNull(
                yangBladeCombinations,
                "陽刃合殺 분석은 필수입니다."
        );

        if (
                base.dayMaster()
                        != yangBladeCombinations.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "官殺取清 분석과 陽刃合殺 분석의 일간이 일치하지 않습니다."
            );
        }

        /*
         * 陰日 또는 이미 다른 경로로
         * 최종 판정된 경우는 그대로 둔다.
         */
        if (!isYangDayMaster(base.dayMaster())) {

            return base;
        }

        if (!base.mixed()) {

            return base;
        }

        if (base.mixtureCleared()) {

            return base;
        }

        /*
         * 앞 단계가 실제로
         * "陽日 傷官制官 후 羊刃 확인 필요"
         * 상태였는지 확인.
         */
        if (
                !base.reasons()
                        .contains(
                                PatternOfficerKillingsPurificationReason
                                        .YANG_DAY_OFFICER_CONTROL_REQUIRES_YANG_BLADE_RETENTION
                        )
        ) {

            return base;
        }

        boolean allOfficersControlled =
                !base.visibleOfficers().isEmpty()
                        &&
                        base.controlledOfficers()
                                .containsAll(
                                        base.visibleOfficers()
                                );

        if (!allOfficersControlled) {

            return base;
        }

        EnumSet<PatternOfficerKillingsPurificationReason> reasons =
                EnumSet.noneOf(
                        PatternOfficerKillingsPurificationReason.class
                );

        reasons.addAll(
                base.reasons()
        );

        if (
                yangBladeCombinations
                        .hasAnyCombination()
        ) {

            reasons.add(
                    PatternOfficerKillingsPurificationReason
                            .YANG_BLADE_PRESENT
            );
        }

        /*
         * =================================================
         * 1. active 羊刃合殺 존재
         * =================================================
         */

        List<PatternTenGodNode> activelyRetainedKillings =
                base.visibleKillings()
                        .stream()
                        .filter(
                                yangBladeCombinations
                                        ::hasActiveCombinationWith
                        )
                        .toList();

        if (!activelyRetainedKillings.isEmpty()) {

            reasons.add(
                    PatternOfficerKillingsPurificationReason
                            .YANG_BLADE_KILLING_COMBINATION_ACTIVE
            );

            reasons.add(
                    PatternOfficerKillingsPurificationReason
                            .YANG_DAY_OFFICER_CONTROL_COMPLETED_BY_YANG_BLADE
            );

            reasons.add(
                    PatternOfficerKillingsPurificationReason
                            .OFFICERS_CONTROLLED_KILLINGS_RETAINED
            );

            /*
             * 去官 후 남는 것은 七殺 계열이다.
             *
             * 陽刃합이 확인된 七殺이 최소 하나 있으므로
             * 留殺 구조 성립.
             *
             * 같은 七殺이 여러 개 투출된 경우에도
             * 官은 모두 제거되고 七殺 계열 자체는 남으므로
             * retainedKillings에는 전체 七殺을 보존한다.
             */
            return new PatternOfficerKillingsPurificationAnalysis(

                    base.dayMaster(),

                    PatternOfficerKillingsPurificationState
                            .OFFICERS_CONTROLLED_KILLINGS_RETAINED,

                    base.visibleOfficers(),

                    base.visibleKillings(),

                    base.controlledOfficers(),

                    base.controlledKillings(),

                    base.retainedOfficers(),

                    base.visibleKillings(),

                    reasons
            );
        }

        /*
         * =================================================
         * 2. 羊刃合殺 pair는 있으나
         *    刃支가 沖刑破害 중
         * =================================================
         *
         * 바로 去官留殺 확정하지 않는다.
         */

        if (
                yangBladeCombinations
                        .hasReviewRequiredCombination()
        ) {

            reasons.add(
                    PatternOfficerKillingsPurificationReason
                            .YANG_BLADE_KILLING_COMBINATION_DISRUPTED
            );

            reasons.add(
                    PatternOfficerKillingsPurificationReason
                            .ADDITIONAL_REVIEW_REQUIRED
            );

            return new PatternOfficerKillingsPurificationAnalysis(

                    base.dayMaster(),

                    PatternOfficerKillingsPurificationState
                            .REVIEW_REQUIRED,

                    base.visibleOfficers(),

                    base.visibleKillings(),

                    base.controlledOfficers(),

                    base.controlledKillings(),

                    base.retainedOfficers(),

                    base.retainedKillings(),

                    reasons
            );
        }

        /*
         * =================================================
         * 3. 傷官制官은 확인됐지만
         *    羊刃合殺이 실제로 없음
         * =================================================
         */

        reasons.add(
                PatternOfficerKillingsPurificationReason
                        .YANG_BLADE_KILLING_RETENTION_NOT_ESTABLISHED
        );

        reasons.add(
                PatternOfficerKillingsPurificationReason
                        .CONTROL_PRESENT_BUT_RETENTION_NOT_CONFIRMED
        );

        return new PatternOfficerKillingsPurificationAnalysis(

                base.dayMaster(),

                PatternOfficerKillingsPurificationState
                        .PARTIAL_CLEARING,

                base.visibleOfficers(),

                base.visibleKillings(),

                base.controlledOfficers(),

                base.controlledKillings(),

                base.retainedOfficers(),

                base.retainedKillings(),

                reasons
        );
    }

    private boolean isYangDayMaster(
            HeavenlyStem stem
    ) {

        return switch (stem) {

            case JIA,
                 BING,
                 WU,
                 GENG,
                 REN -> true;

            case YI,
                 DING,
                 JI,
                 XIN,
                 GUI -> false;
        };
    }
}