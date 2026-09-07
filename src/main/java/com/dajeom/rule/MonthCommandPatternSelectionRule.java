package com.dajeom.rule;

import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.MonthCommandPatternCandidate;
import com.dajeom.domain.MonthCommandPatternCandidateAnalysis;
import com.dajeom.domain.MonthCommandPatternCandidateType;
import com.dajeom.domain.MonthCommandPatternSelectionAnalysis;
import com.dajeom.domain.MonthCommandPatternSelectionPath;
import com.dajeom.domain.MonthCommandPatternSelectionReason;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 월령 격국 후보를
 * 다음 격국 판정 경로로 분기한다.
 *
 * =================================================
 *
 * 1. 일반 8격 MAIN
 *
 *    비MAIN 투출 없음
 *    → MAIN 유지 검토
 *
 *    비MAIN 하나 투출
 *    → 격국 변화 검토
 *
 *    비MAIN 복수 투출
 *    → 복수 변화/혼재 검토
 *
 * =================================================
 *
 * 2. 비견 월령
 *
 *    → 建祿 검토
 *
 * =================================================
 *
 * 3. 겁재 월령
 *
 *    양간
 *    → 陽刃 검토
 *
 *    음간
 *    → 月劫 검토
 *
 * =================================================
 *
 * 여기서도 成格 / 破格은 판정하지 않는다.
 */
@Component
public class MonthCommandPatternSelectionRule {

    public MonthCommandPatternSelectionAnalysis assess(
            MonthCommandPatternCandidateAnalysis analysis
    ) {

        Objects.requireNonNull(
                analysis,
                "월령 격국 후보 분석은 필수입니다."
        );

        HeavenlyStem dayMaster =
                analysis.dayMaster();

        MonthCommandPatternCandidate primary =
                analysis.primaryCandidate();

        List<MonthCommandPatternCandidate> alternatives =
                analysis.exposedAlternativeCandidates();

        EnumSet<MonthCommandPatternSelectionReason> reasons =
                EnumSet.noneOf(
                        MonthCommandPatternSelectionReason.class
                );

        /*
         * MAIN 투출 상태는
         * 경로와 별개로 항상 보존한다.
         */
        if (
                primary.exposed()
        ) {

            reasons.add(
                    MonthCommandPatternSelectionReason
                            .PRIMARY_MAIN_EXPOSED
            );

        } else {

            reasons.add(
                    MonthCommandPatternSelectionReason
                            .PRIMARY_MAIN_NOT_EXPOSED
            );
        }

        /*
         * =================================================
         * 1. 建祿
         * =================================================
         */

        if (
                primary.candidateType()
                        == MonthCommandPatternCandidateType
                        .MONTH_COMPANION
        ) {

            reasons.add(
                    MonthCommandPatternSelectionReason
                            .MONTH_MAIN_IS_COMPANION
            );

            appendAlternativeReasons(
                    reasons,
                    primary,
                    alternatives
            );

            return new MonthCommandPatternSelectionAnalysis(
                    dayMaster,
                    analysis.monthBranch(),
                    MonthCommandPatternSelectionPath
                            .JIANLU_REVIEW,
                    primary,
                    Optional.empty(),
                    alternatives,
                    reasons
            );
        }

        /*
         * =================================================
         * 2. 劫財月令
         *
         * 양간 → 陽刃
         * 음간 → 月劫
         * =================================================
         */

        if (
                primary.candidateType()
                        == MonthCommandPatternCandidateType
                        .MONTH_ROB_WEALTH
        ) {

            reasons.add(
                    MonthCommandPatternSelectionReason
                            .MONTH_MAIN_IS_ROB_WEALTH
            );

            appendAlternativeReasons(
                    reasons,
                    primary,
                    alternatives
            );

            if (
                    isYang(
                            dayMaster
                    )
            ) {

                reasons.add(
                        MonthCommandPatternSelectionReason
                                .YANG_DAY_MASTER_WITH_ROB_WEALTH_MONTH
                );

                return new MonthCommandPatternSelectionAnalysis(
                        dayMaster,
                        analysis.monthBranch(),
                        MonthCommandPatternSelectionPath
                                .YANG_BLADE_REVIEW,
                        primary,
                        Optional.empty(),
                        alternatives,
                        reasons
                );
            }

            reasons.add(
                    MonthCommandPatternSelectionReason
                            .YIN_DAY_MASTER_WITH_ROB_WEALTH_MONTH
            );

            return new MonthCommandPatternSelectionAnalysis(
                    dayMaster,
                    analysis.monthBranch(),
                    MonthCommandPatternSelectionPath
                            .YUEJIE_REVIEW,
                    primary,
                    Optional.empty(),
                    alternatives,
                    reasons
            );
        }

        /*
         * =================================================
         * 3. 일반 8격 MAIN
         * =================================================
         */

        if (
                !primary
                        .standardEightPatternCandidate()
        ) {

            throw new IllegalStateException(
                    "처리할 수 없는 월령 MAIN 격국 후보입니다: "
                            +
                            primary.candidateType()
            );
        }

        reasons.add(
                MonthCommandPatternSelectionReason
                        .MONTH_MAIN_IS_STANDARD_PATTERN
        );

        /*
         * 비MAIN 투출 없음.
         *
         * 이 경우에만 MAIN을
         * 현재 단계의 provisional 후보로 둔다.
         *
         * 아직 成格 확정은 아니다.
         */
        if (
                alternatives.isEmpty()
        ) {

            reasons.add(
                    MonthCommandPatternSelectionReason
                            .NO_EXPOSED_ALTERNATIVE
            );

            return new MonthCommandPatternSelectionAnalysis(
                    dayMaster,
                    analysis.monthBranch(),
                    MonthCommandPatternSelectionPath
                            .STANDARD_PRIMARY_REVIEW,
                    primary,
                    Optional.of(
                            primary
                    ),
                    List.of(),
                    reasons
            );
        }

        /*
         * 비MAIN 하나 투출.
         *
         * MAIN을 삭제하거나
         * 투출 후보를 즉시 새 격으로 선택하지 않는다.
         */
        if (
                alternatives.size()
                        == 1
        ) {

            reasons.add(
                    MonthCommandPatternSelectionReason
                            .SINGLE_EXPOSED_ALTERNATIVE
            );

            appendCandidateRelationReason(
                    reasons,
                    primary,
                    alternatives.get(0)
            );

            return new MonthCommandPatternSelectionAnalysis(
                    dayMaster,
                    analysis.monthBranch(),
                    MonthCommandPatternSelectionPath
                            .STANDARD_CHANGE_REVIEW,
                    primary,
                    Optional.empty(),
                    alternatives,
                    reasons
            );
        }

        /*
         * 비MAIN 복수 투출.
         *
         * 하나를 임의로 선택하지 않는다.
         */
        reasons.add(
                MonthCommandPatternSelectionReason
                        .MULTIPLE_EXPOSED_ALTERNATIVES
        );

        for (
                MonthCommandPatternCandidate alternative
                : alternatives
        ) {

            appendCandidateRelationReason(
                    reasons,
                    primary,
                    alternative
            );
        }

        return new MonthCommandPatternSelectionAnalysis(
                dayMaster,
                analysis.monthBranch(),
                MonthCommandPatternSelectionPath
                        .STANDARD_MULTIPLE_CHANGE_REVIEW,
                primary,
                Optional.empty(),
                alternatives,
                reasons
        );
    }

    private void appendAlternativeReasons(

            EnumSet<MonthCommandPatternSelectionReason> reasons,

            MonthCommandPatternCandidate primary,

            List<MonthCommandPatternCandidate> alternatives

    ) {

        if (
                alternatives.isEmpty()
        ) {

            reasons.add(
                    MonthCommandPatternSelectionReason
                            .NO_EXPOSED_ALTERNATIVE
            );

            return;
        }

        if (
                alternatives.size()
                        == 1
        ) {

            reasons.add(
                    MonthCommandPatternSelectionReason
                            .SINGLE_EXPOSED_ALTERNATIVE
            );

            appendCandidateRelationReason(
                    reasons,
                    primary,
                    alternatives.get(0)
            );

            return;
        }

        reasons.add(
                MonthCommandPatternSelectionReason
                        .MULTIPLE_EXPOSED_ALTERNATIVES
        );

        for (
                MonthCommandPatternCandidate alternative
                : alternatives
        ) {

            appendCandidateRelationReason(
                    reasons,
                    primary,
                    alternative
            );
        }
    }

    private void appendCandidateRelationReason(

            EnumSet<MonthCommandPatternSelectionReason> reasons,

            MonthCommandPatternCandidate primary,

            MonthCommandPatternCandidate alternative

    ) {

        if (
                primary.candidateType()
                        == alternative.candidateType()
        ) {

            reasons.add(
                    MonthCommandPatternSelectionReason
                            .ALTERNATIVE_PATTERN_SAME_AS_PRIMARY
            );

        } else {

            reasons.add(
                    MonthCommandPatternSelectionReason
                            .ALTERNATIVE_PATTERN_DIFFERS_FROM_PRIMARY
            );
        }
    }

    private boolean isYang(
            HeavenlyStem stem
    ) {

        return switch (stem) {

            case JIA,
                 BING,
                 WU,
                 GENG,
                 REN ->
                    true;

            case YI,
                 DING,
                 JI,
                 XIN,
                 GUI ->
                    false;
        };
    }
}