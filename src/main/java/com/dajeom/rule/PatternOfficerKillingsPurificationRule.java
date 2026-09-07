package com.dajeom.rule;

import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.MonthCommandPatternCandidateType;
import com.dajeom.domain.PatternOfficerKillingsPurificationAnalysis;
import com.dajeom.domain.PatternOfficerKillingsPurificationReason;
import com.dajeom.domain.PatternOfficerKillingsPurificationState;
import com.dajeom.domain.PatternStemCombinationActivationAnalysis;
import com.dajeom.domain.PatternStemCombinationActivationOccurrence;
import com.dajeom.domain.PatternTenGodFoundationProfile;
import com.dajeom.domain.PatternTenGodInteractionAnalysis;
import com.dajeom.domain.PatternTenGodLinkType;
import com.dajeom.domain.PatternTenGodNode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

/**
 * 투출 官殺混雜을
 * 食神 / 傷官의 制와 合을 이용해 정리한다.
 *
 * =================================================
 *
 * 고전적 기본 흐름:
 *
 * 陽日:
 *
 * 食神制殺
 * +
 * 食神合官
 * → 去殺留官
 *
 * 傷官制官만으로는
 * 去官留殺을 자동 확정하지 않음.
 * 羊刃合殺 추가 필요.
 *
 * =================================================
 *
 * 陰日:
 *
 * 食神制殺
 * +
 * 日主合官
 * → 去殺留官
 *
 * 傷官制官
 * +
 * 傷官合殺
 * → 去官留殺
 *
 * =================================================
 *
 * 주의:
 *
 * 오행 graph에서는 食神이 官과 殺을
 * 모두 克할 수 있다.
 *
 * 따라서 단순 CONTROLS link만 보고
 * 官까지 제거됐다고 하지 않는다.
 *
 * 官 또는 殺의 "留"는
 * 위의 일간 음양별 合 구조까지 확인한다.
 */
@Component
public class PatternOfficerKillingsPurificationRule {

    public PatternOfficerKillingsPurificationAnalysis assess(

            PatternTenGodInteractionAnalysis interactions,

            PatternTenGodFoundationProfile foundations,

            PatternStemCombinationActivationAnalysis combinations

    ) {

        Objects.requireNonNull(
                interactions,
                "PatternTenGodInteractionAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                foundations,
                "PatternTenGodFoundationProfile은 필수입니다."
        );

        Objects.requireNonNull(
                combinations,
                "PatternStemCombinationActivationAnalysis는 필수입니다."
        );

        validateDayMaster(
                interactions,
                foundations,
                combinations
        );

        List<PatternTenGodNode> officers =
                interactions.visibleNodesOf(
                        MonthCommandPatternCandidateType
                                .DIRECT_OFFICER
                );

        List<PatternTenGodNode> killings =
                interactions.visibleNodesOf(
                        MonthCommandPatternCandidateType
                                .SEVEN_KILLINGS
                );

        EnumSet<PatternOfficerKillingsPurificationReason> reasons =
                EnumSet.noneOf(
                        PatternOfficerKillingsPurificationReason.class
                );

        if (
                officers.isEmpty()
                        ||
                        killings.isEmpty()
        ) {

            reasons.add(
                    PatternOfficerKillingsPurificationReason
                            .OFFICER_KILLINGS_NOT_MIXED
            );

            return new PatternOfficerKillingsPurificationAnalysis(

                    interactions.dayMaster(),

                    PatternOfficerKillingsPurificationState
                            .NOT_MIXED,

                    officers,

                    killings,

                    List.of(),

                    List.of(),

                    List.of(),

                    List.of(),

                    reasons
            );
        }

        reasons.add(
                PatternOfficerKillingsPurificationReason
                        .OFFICER_KILLINGS_MIXED
        );

        boolean yangDay =
                isYangDayMaster(
                        interactions.dayMaster()
                );

        if (yangDay) {

            reasons.add(
                    PatternOfficerKillingsPurificationReason
                            .YANG_DAY_MASTER
            );

        } else {

            reasons.add(
                    PatternOfficerKillingsPurificationReason
                            .YIN_DAY_MASTER
            );
        }

        boolean eatingGodFoundation =
                foundations
                        .eatingGodFoundationStable();

        boolean hurtingOfficerFoundation =
                foundations
                        .hurtingOfficerFoundationStable();

        if (eatingGodFoundation) {

            reasons.add(
                    PatternOfficerKillingsPurificationReason
                            .EATING_GOD_FOUNDATION_CONFIRMED
            );
        }

        if (hurtingOfficerFoundation) {

            reasons.add(
                    PatternOfficerKillingsPurificationReason
                            .HURTING_OFFICER_FOUNDATION_CONFIRMED
            );
        }

        /*
         * =================================================
         * 食神 → 七殺
         * =================================================
         */

        List<PatternTenGodNode> controlledKillings =
                controlledTargets(

                        killings,

                        MonthCommandPatternCandidateType
                                .EATING_GOD,

                        interactions
                );

        boolean allKillingsControlled =
                eatingGodFoundation
                        &&
                        !killings.isEmpty()
                        &&
                        controlledKillings.size()
                                == killings.size();

        if (allKillingsControlled) {

            reasons.add(
                    PatternOfficerKillingsPurificationReason
                            .ALL_VISIBLE_KILLINGS_CONTROLLED_BY_EATING_GOD
            );

        } else if (!controlledKillings.isEmpty()) {

            reasons.add(
                    PatternOfficerKillingsPurificationReason
                            .SOME_VISIBLE_KILLINGS_REMAIN_UNCONTROLLED
            );
        }

        /*
         * =================================================
         * 傷官 → 正官
         * =================================================
         */

        List<PatternTenGodNode> controlledOfficers =
                controlledTargets(

                        officers,

                        MonthCommandPatternCandidateType
                                .HURTING_OFFICER,

                        interactions
                );

        boolean allOfficersControlled =
                hurtingOfficerFoundation
                        &&
                        !officers.isEmpty()
                        &&
                        controlledOfficers.size()
                                == officers.size();

        if (allOfficersControlled) {

            reasons.add(
                    PatternOfficerKillingsPurificationReason
                            .ALL_VISIBLE_OFFICERS_CONTROLLED_BY_HURTING_OFFICER
            );

        } else if (!controlledOfficers.isEmpty()) {

            reasons.add(
                    PatternOfficerKillingsPurificationReason
                            .SOME_VISIBLE_OFFICERS_REMAIN_UNCONTROLLED
            );
        }

        /*
         * =================================================
         * 去殺留官의 "留官"
         * =================================================
         */

        List<PatternTenGodNode> retainedOfficers =
                new ArrayList<>();

        if (yangDay) {

            /*
             * 陽日:
             *
             * 食神制殺
             * 食神合官
             */
            for (
                    PatternTenGodNode officer
                    : officers
            ) {

                if (
                        hasActiveCombinationBetweenTypeAndNode(

                                MonthCommandPatternCandidateType
                                        .EATING_GOD,

                                officer,

                                combinations
                        )
                ) {

                    retainedOfficers.add(
                            officer
                    );
                }
            }

            if (!retainedOfficers.isEmpty()) {

                reasons.add(
                        PatternOfficerKillingsPurificationReason
                                .YANG_DAY_EATING_GOD_COMBINES_OFFICER
                );
            }

        } else {

            /*
             * 陰日:
             *
             * 日主 자체가 官과 합하여 留官.
             */
            PatternTenGodNode dayMaster =
                    interactions.dayMasterNode();

            for (
                    PatternTenGodNode officer
                    : officers
            ) {

                if (
                        hasSelfCombination(

                                dayMaster,

                                officer,

                                combinations
                        )
                ) {

                    retainedOfficers.add(
                            officer
                    );
                }
            }

            if (!retainedOfficers.isEmpty()) {

                reasons.add(
                        PatternOfficerKillingsPurificationReason
                                .YIN_DAY_MASTER_COMBINES_OFFICER
                );
            }
        }

        boolean officerRetentionEstablished =
                !retainedOfficers.isEmpty();

        /*
         * =================================================
         * 去官留殺의 "留殺"
         * =================================================
         */

        List<PatternTenGodNode> retainedKillings =
                new ArrayList<>();

        boolean killingRetentionEstablished =
                false;

        if (!yangDay) {

            /*
             * 陰日:
             *
             * 傷官制官
             * +
             * 傷官合殺
             */
            for (
                    PatternTenGodNode killing
                    : killings
            ) {

                if (
                        hasActiveCombinationBetweenTypeAndNode(

                                MonthCommandPatternCandidateType
                                        .HURTING_OFFICER,

                                killing,

                                combinations
                        )
                ) {

                    retainedKillings.add(
                            killing
                    );
                }
            }

            killingRetentionEstablished =
                    !retainedKillings.isEmpty();

            if (killingRetentionEstablished) {

                reasons.add(
                        PatternOfficerKillingsPurificationReason
                                .YIN_DAY_HURTING_OFFICER_COMBINES_KILLING
                );
            }

        } else if (allOfficersControlled) {

            /*
             * 陽日:
             *
             * 傷官制官만으로
             * 留殺을 자동 확정하지 않는다.
             *
             * 羊刃合殺 검토가 필요.
             */
            reasons.add(
                    PatternOfficerKillingsPurificationReason
                            .YANG_DAY_OFFICER_CONTROL_REQUIRES_YANG_BLADE_RETENTION
            );
        }

        boolean killingClearingPath =
                allKillingsControlled
                        &&
                        officerRetentionEstablished;

        boolean officerClearingPath =
                allOfficersControlled
                        &&
                        killingRetentionEstablished;

        /*
         * 두 방향이 동시에 성립하면
         * 임의로 하나를 선택하지 않는다.
         */
        if (
                killingClearingPath
                        &&
                        officerClearingPath
        ) {

            reasons.add(
                    PatternOfficerKillingsPurificationReason
                            .BOTH_CLEARING_DIRECTIONS_PRESENT
            );

            reasons.add(
                    PatternOfficerKillingsPurificationReason
                            .ADDITIONAL_REVIEW_REQUIRED
            );

            return result(

                    interactions,

                    PatternOfficerKillingsPurificationState
                            .REVIEW_REQUIRED,

                    officers,
                    killings,

                    controlledOfficers,
                    controlledKillings,

                    retainedOfficers,
                    retainedKillings,

                    reasons
            );
        }

        if (killingClearingPath) {

            reasons.add(
                    PatternOfficerKillingsPurificationReason
                            .KILLINGS_CONTROLLED_OFFICER_RETAINED
            );

            return result(

                    interactions,

                    PatternOfficerKillingsPurificationState
                            .KILLINGS_CONTROLLED_OFFICER_RETAINED,

                    officers,
                    killings,

                    controlledOfficers,
                    controlledKillings,

                    retainedOfficers,
                    retainedKillings,

                    reasons
            );
        }

        if (officerClearingPath) {

            reasons.add(
                    PatternOfficerKillingsPurificationReason
                            .OFFICERS_CONTROLLED_KILLINGS_RETAINED
            );

            return result(

                    interactions,

                    PatternOfficerKillingsPurificationState
                            .OFFICERS_CONTROLLED_KILLINGS_RETAINED,

                    officers,
                    killings,

                    controlledOfficers,
                    controlledKillings,

                    retainedOfficers,
                    retainedKillings,

                    reasons
            );
        }

        /*
         * 陽日 傷官制官인데
         * 羊刃合殺 여부가 아직 없음.
         */
        if (
                yangDay
                        &&
                        allOfficersControlled
        ) {

            reasons.add(
                    PatternOfficerKillingsPurificationReason
                            .ADDITIONAL_REVIEW_REQUIRED
            );

            return result(

                    interactions,

                    PatternOfficerKillingsPurificationState
                            .REVIEW_REQUIRED,

                    officers,
                    killings,

                    controlledOfficers,
                    controlledKillings,

                    retainedOfficers,
                    retainedKillings,

                    reasons
            );
        }

        /*
         * 制는 있으나 留가 확정되지 않음.
         */
        if (
                allKillingsControlled
                        ||
                        allOfficersControlled
                        ||
                        !controlledKillings.isEmpty()
                        ||
                        !controlledOfficers.isEmpty()
        ) {

            reasons.add(
                    PatternOfficerKillingsPurificationReason
                            .CONTROL_PRESENT_BUT_RETENTION_NOT_CONFIRMED
            );

            return result(

                    interactions,

                    PatternOfficerKillingsPurificationState
                            .PARTIAL_CLEARING,

                    officers,
                    killings,

                    controlledOfficers,
                    controlledKillings,

                    retainedOfficers,
                    retainedKillings,

                    reasons
            );
        }

        if (
                !eatingGodFoundation
                        &&
                        !hurtingOfficerFoundation
        ) {

            reasons.add(
                    PatternOfficerKillingsPurificationReason
                            .OUTPUT_FOUNDATION_NOT_CONFIRMED
            );
        }

        return result(

                interactions,

                PatternOfficerKillingsPurificationState
                        .UNCLEARED,

                officers,
                killings,

                controlledOfficers,
                controlledKillings,

                retainedOfficers,
                retainedKillings,

                reasons
        );
    }

    /*
     * =================================================
     * 制
     * =================================================
     */

    private List<PatternTenGodNode> controlledTargets(

            List<PatternTenGodNode> targets,

            MonthCommandPatternCandidateType controllerType,

            PatternTenGodInteractionAnalysis interactions

    ) {

        return targets
                .stream()
                .filter(
                        target ->
                                interactions
                                        .visibleNodesOf(
                                                controllerType
                                        )
                                        .stream()
                                        .anyMatch(
                                                source ->
                                                        controls(
                                                                source,
                                                                target,
                                                                interactions
                                                        )
                                        )
                )
                .toList();
    }

    private boolean controls(

            PatternTenGodNode source,

            PatternTenGodNode target,

            PatternTenGodInteractionAnalysis interactions

    ) {

        return interactions
                .links()
                .stream()
                .anyMatch(
                        link ->
                                link.type()
                                        == PatternTenGodLinkType.CONTROLS

                                        &&

                                        link.source()
                                                .equals(source)

                                        &&

                                        link.target()
                                                .equals(target)
                );
    }

    /*
     * =================================================
     * 留 - 비일간 合
     * =================================================
     */

    private boolean hasActiveCombinationBetweenTypeAndNode(

            MonthCommandPatternCandidateType sourceType,

            PatternTenGodNode target,

            PatternStemCombinationActivationAnalysis combinations

    ) {

        return combinations
                .activations()
                .stream()
                .filter(
                        PatternStemCombinationActivationOccurrence
                                ::activeNonDayMasterCombination
                )
                .anyMatch(
                        activation -> {

                            PatternTenGodNode left =
                                    activation
                                            .combination()
                                            .left();

                            PatternTenGodNode right =
                                    activation
                                            .combination()
                                            .right();

                            return (
                                    left.hasTenGodType(sourceType)
                                            &&
                                            right.equals(target)
                            )
                                    ||
                                    (
                                            right.hasTenGodType(sourceType)
                                                    &&
                                                    left.equals(target)
                                    );
                        }
                );
    }

    /*
     * =================================================
     * 留 - 日主 本身之合
     * =================================================
     */

    private boolean hasSelfCombination(

            PatternTenGodNode dayMaster,

            PatternTenGodNode target,

            PatternStemCombinationActivationAnalysis combinations

    ) {

        return combinations
                .activations()
                .stream()
                .filter(
                        PatternStemCombinationActivationOccurrence
                                ::selfCombination
                )
                .anyMatch(
                        activation -> {

                            PatternTenGodNode left =
                                    activation
                                            .combination()
                                            .left();

                            PatternTenGodNode right =
                                    activation
                                            .combination()
                                            .right();

                            return (
                                    left.equals(dayMaster)
                                            &&
                                            right.equals(target)
                            )
                                    ||
                                    (
                                            right.equals(dayMaster)
                                                    &&
                                                    left.equals(target)
                                    );
                        }
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

    private PatternOfficerKillingsPurificationAnalysis result(

            PatternTenGodInteractionAnalysis interactions,

            PatternOfficerKillingsPurificationState state,

            List<PatternTenGodNode> officers,

            List<PatternTenGodNode> killings,

            List<PatternTenGodNode> controlledOfficers,

            List<PatternTenGodNode> controlledKillings,

            List<PatternTenGodNode> retainedOfficers,

            List<PatternTenGodNode> retainedKillings,

            EnumSet<PatternOfficerKillingsPurificationReason> reasons

    ) {

        return new PatternOfficerKillingsPurificationAnalysis(

                interactions.dayMaster(),

                state,

                officers,

                killings,

                controlledOfficers,

                controlledKillings,

                retainedOfficers,

                retainedKillings,

                reasons
        );
    }

    private void validateDayMaster(

            PatternTenGodInteractionAnalysis interactions,

            PatternTenGodFoundationProfile foundations,

            PatternStemCombinationActivationAnalysis combinations

    ) {

        if (
                interactions.dayMaster()
                        != foundations.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "십성 Interaction과 FoundationProfile의 일간이 일치하지 않습니다."
            );
        }

        if (
                interactions.dayMaster()
                        != combinations.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "십성 Interaction과 천간합 Activation의 일간이 일치하지 않습니다."
            );
        }
    }
}