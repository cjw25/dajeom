package com.dajeom.calculation;

import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.MonthCommandPatternCandidateType;
import com.dajeom.domain.PatternBranchDisruptionAnalysis;
import com.dajeom.domain.PatternBranchDisruptionOccurrence;
import com.dajeom.domain.PatternTenGodInteractionAnalysis;
import com.dajeom.domain.PatternTenGodNode;
import com.dajeom.domain.PatternYangBladeAnalysis;
import com.dajeom.domain.PatternYangBladeKillingCombinationAnalysis;
import com.dajeom.domain.PatternYangBladeKillingCombinationOccurrence;
import com.dajeom.domain.PatternYangBladeKillingCombinationReason;
import com.dajeom.domain.PatternYangBladeKillingCombinationState;
import com.dajeom.domain.PatternYangBladeOccurrence;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

/**
 * 陽刃 속 劫財藏干과
 * 투출 七殺의 天干五合을 계산한다.
 *
 * =================================================
 *
 * 일반 PatternStemCombinationCalculator는
 * hidden stem을 합 대상에 포함하지 않는다.
 *
 * 따라서 羊刃合殺은
 * 이 전용 Calculator에서 계산한다.
 *
 * =================================================
 */
@Component
public class PatternYangBladeKillingCombinationCalculator {

    public PatternYangBladeKillingCombinationAnalysis analyze(

            PatternYangBladeAnalysis yangBladeAnalysis,

            PatternTenGodInteractionAnalysis interactions,

            PatternBranchDisruptionAnalysis branchDisruptions

    ) {

        Objects.requireNonNull(
                yangBladeAnalysis,
                "PatternYangBladeAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                interactions,
                "PatternTenGodInteractionAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                branchDisruptions,
                "PatternBranchDisruptionAnalysis는 필수입니다."
        );

        validateDayMaster(
                yangBladeAnalysis,
                interactions
        );

        List<PatternTenGodNode> killings =
                interactions.visibleNodesOf(
                        MonthCommandPatternCandidateType
                                .SEVEN_KILLINGS
                );

        List<PatternYangBladeKillingCombinationOccurrence> results =
                new ArrayList<>();

        for (
                PatternYangBladeOccurrence blade
                : yangBladeAnalysis.occurrences()
        ) {

            for (
                    PatternTenGodNode killing
                    : killings
            ) {

                if (
                        !fiveHarmony(
                                blade.bladeRobStem(),
                                killing.stem()
                        )
                ) {

                    continue;
                }

                List<PatternBranchDisruptionOccurrence> disruptions =
                        branchDisruptions
                                .occurrences()
                                .stream()
                                .filter(
                                        disruption ->
                                                disruption.involvesPosition(
                                                        blade.position()
                                                )
                                )
                                .toList();

                EnumSet<PatternYangBladeKillingCombinationReason> reasons =
                        EnumSet.noneOf(
                                PatternYangBladeKillingCombinationReason.class
                        );

                reasons.add(
                        PatternYangBladeKillingCombinationReason
                                .YANG_BLADE_PRESENT
                );

                reasons.add(
                        PatternYangBladeKillingCombinationReason
                                .ROB_WEALTH_HIDDEN_STEM_PRESENT
                );

                reasons.add(
                        PatternYangBladeKillingCombinationReason
                                .VISIBLE_SEVEN_KILLINGS_PRESENT
                );

                reasons.add(
                        PatternYangBladeKillingCombinationReason
                                .ROB_WEALTH_HIDDEN_STEM_COMBINES_SEVEN_KILLINGS
                );

                PatternYangBladeKillingCombinationState state;

                if (disruptions.isEmpty()) {

                    state =
                            PatternYangBladeKillingCombinationState
                                    .ACTIVE;

                    reasons.add(
                            PatternYangBladeKillingCombinationReason
                                    .YANG_BLADE_BRANCH_NOT_DISRUPTED
                    );

                    reasons.add(
                            PatternYangBladeKillingCombinationReason
                                    .ACTIVE_YANG_BLADE_KILLING_COMBINATION
                    );

                } else {

                    state =
                            PatternYangBladeKillingCombinationState
                                    .BLADE_BRANCH_DISRUPTED_REVIEW_REQUIRED;

                    reasons.add(
                            PatternYangBladeKillingCombinationReason
                                    .YANG_BLADE_BRANCH_DISRUPTED
                    );

                    reasons.add(
                            PatternYangBladeKillingCombinationReason
                                    .ADDITIONAL_REVIEW_REQUIRED
                    );
                }

                results.add(
                        new PatternYangBladeKillingCombinationOccurrence(

                                blade,

                                killing,

                                state,

                                disruptions,

                                reasons
                        )
                );
            }
        }

        return new PatternYangBladeKillingCombinationAnalysis(

                interactions.dayMaster(),

                results
        );
    }

    /**
     * 五合 pair 존재 여부.
     *
     * 合化 여부가 아니다.
     */
    private boolean fiveHarmony(

            HeavenlyStem first,

            HeavenlyStem second

    ) {

        return pair(
                first,
                second,
                HeavenlyStem.JIA,
                HeavenlyStem.JI
        )
                ||
                pair(
                        first,
                        second,
                        HeavenlyStem.YI,
                        HeavenlyStem.GENG
                )
                ||
                pair(
                        first,
                        second,
                        HeavenlyStem.BING,
                        HeavenlyStem.XIN
                )
                ||
                pair(
                        first,
                        second,
                        HeavenlyStem.DING,
                        HeavenlyStem.REN
                )
                ||
                pair(
                        first,
                        second,
                        HeavenlyStem.WU,
                        HeavenlyStem.GUI
                );
    }

    private boolean pair(

            HeavenlyStem first,

            HeavenlyStem second,

            HeavenlyStem expectedFirst,

            HeavenlyStem expectedSecond

    ) {

        return (
                first == expectedFirst
                        &&
                        second == expectedSecond
        )
                ||
                (
                        first == expectedSecond
                                &&
                                second == expectedFirst
                );
    }

    private void validateDayMaster(

            PatternYangBladeAnalysis yangBladeAnalysis,

            PatternTenGodInteractionAnalysis interactions

    ) {

        if (
                yangBladeAnalysis.dayMaster()
                        != interactions.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "陽刃 분석과 십성 Interaction의 일간이 일치하지 않습니다."
            );
        }
    }
}