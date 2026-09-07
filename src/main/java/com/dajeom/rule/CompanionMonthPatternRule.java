package com.dajeom.rule;

import com.dajeom.domain.CompanionMonthFormationRoute;
import com.dajeom.domain.CompanionMonthPatternAnalysis;
import com.dajeom.domain.CompanionMonthPatternReason;
import com.dajeom.domain.CompanionMonthPatternStatus;
import com.dajeom.domain.CompanionMonthPatternType;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.MonthCommandPatternCandidateType;
import com.dajeom.domain.MonthCommandPatternSelectionPath;
import com.dajeom.domain.PatternInteractionAnalysis;
import com.dajeom.domain.PatternTenGodFoundationProfile;
import com.dajeom.domain.PatternTenGodInteractionAnalysis;
import com.dajeom.domain.PatternTenGodLinkType;
import com.dajeom.domain.PatternTenGodNode;
import com.dajeom.domain.PatternYangBladeAnalysis;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

/**
 * 建祿 / 月劫 / 陽刃의 실제 성격 경로를 판정한다.
 *
 * =================================================
 *
 * 建祿 / 月劫
 *
 * 1. 透官而逢財印
 *
 * 2. 透財而逢食傷
 *
 * 3. 透煞而遇制伏
 *
 * =================================================
 *
 * 陽刃
 *
 * 官殺로 陽刃을 제복하는 경로를 중심으로 하되
 * 傷官 투출을 성격의 결함으로 본다.
 *
 * =================================================
 *
 * 숫자 / 점수 / 다수결 없음.
 */
@Component
public class CompanionMonthPatternRule {

    public CompanionMonthPatternAnalysis assess(

            PatternInteractionAnalysis interaction,

            PatternTenGodInteractionAnalysis tenGodInteractions,

            PatternTenGodFoundationProfile foundations,

            PatternYangBladeAnalysis yangBladeAnalysis

    ) {

        Objects.requireNonNull(
                interaction,
                "PatternInteractionAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                tenGodInteractions,
                "PatternTenGodInteractionAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                foundations,
                "PatternTenGodFoundationProfile은 필수입니다."
        );

        Objects.requireNonNull(
                yangBladeAnalysis,
                "PatternYangBladeAnalysis는 필수입니다."
        );

        validateDayMaster(
                interaction,
                tenGodInteractions,
                foundations,
                yangBladeAnalysis
        );

        MonthCommandPatternSelectionPath path =
                interaction
                        .patternSelection()
                        .path();

        return switch (path) {

            case JIANLU_REVIEW ->
                    assessJianluOrYuejie(
                            CompanionMonthPatternType.JIANLU,
                            interaction,
                            tenGodInteractions,
                            foundations
                    );

            case YUEJIE_REVIEW ->
                    assessJianluOrYuejie(
                            CompanionMonthPatternType.YUEJIE,
                            interaction,
                            tenGodInteractions,
                            foundations
                    );

            case YANG_BLADE_REVIEW ->
                    assessYangBlade(
                            interaction,
                            tenGodInteractions,
                            foundations,
                            yangBladeAnalysis
                    );

            default ->
                    throw new IllegalArgumentException(
                            "CompanionMonthPatternRule 대상이 아닌 월령 선택 경로입니다: "
                                    + path
                    );
        };
    }

    /*
     * =================================================
     * 建祿 / 月劫
     * =================================================
     */

    private CompanionMonthPatternAnalysis assessJianluOrYuejie(

            CompanionMonthPatternType patternType,

            PatternInteractionAnalysis interaction,

            PatternTenGodInteractionAnalysis tenGodInteractions,

            PatternTenGodFoundationProfile foundations

    ) {

        EnumSet<CompanionMonthFormationRoute> routes =
                EnumSet.noneOf(
                        CompanionMonthFormationRoute.class
                );

        EnumSet<CompanionMonthPatternReason> reasons =
                EnumSet.noneOf(
                        CompanionMonthPatternReason.class
                );

        if (patternType == CompanionMonthPatternType.JIANLU) {

            reasons.add(
                    CompanionMonthPatternReason
                            .JIANLU_MONTH_COMMAND
            );

        } else {

            reasons.add(
                    CompanionMonthPatternReason
                            .YUEJIE_MONTH_COMMAND
            );
        }

        /*
         * =================================================
         * 1. 透官而逢財印
         * =================================================
         */

        boolean officerExposed =
                hasVisible(
                        MonthCommandPatternCandidateType
                                .DIRECT_OFFICER,
                        tenGodInteractions
                );

        if (officerExposed) {

            reasons.add(
                    CompanionMonthPatternReason
                            .DIRECT_OFFICER_EXPOSED
            );
        }

        boolean officerStable =
                foundations
                        .directOfficerFoundationStable();

        if (officerStable) {

            reasons.add(
                    CompanionMonthPatternReason
                            .DIRECT_OFFICER_FOUNDATION_CONFIRMED
            );
        }

        boolean wealthFoundation =
                foundations
                        .wealthFoundationStable();

        if (wealthFoundation) {

            reasons.add(
                    CompanionMonthPatternReason
                            .WEALTH_PRESENT
            );

            reasons.add(
                    CompanionMonthPatternReason
                            .WEALTH_FOUNDATION_CONFIRMED
            );
        }

        boolean resourceFoundation =
                foundations
                        .resourceFoundationStable();

        if (resourceFoundation) {

            reasons.add(
                    CompanionMonthPatternReason
                            .RESOURCE_PRESENT
            );

            reasons.add(
                    CompanionMonthPatternReason
                            .RESOURCE_FOUNDATION_CONFIRMED
            );
        }

        boolean officerRoute =
                officerExposed
                        &&
                        officerStable
                        &&
                        (
                                wealthFoundation
                                        ||
                                        resourceFoundation
                        );

        if (officerRoute) {

            routes.add(
                    CompanionMonthFormationRoute
                            .DIRECT_OFFICER_WITH_WEALTH_OR_RESOURCE
            );

            reasons.add(
                    CompanionMonthPatternReason
                            .DIRECT_OFFICER_WITH_WEALTH_OR_RESOURCE_FORMED
            );
        }

        /*
         * =================================================
         * 2. 透財而逢食傷
         * =================================================
         */

        List<PatternTenGodNode> visibleWealth =
                visibleWealthNodes(
                        tenGodInteractions
                );

        if (!visibleWealth.isEmpty()) {

            reasons.add(
                    CompanionMonthPatternReason
                            .WEALTH_EXPOSED
            );
        }

        boolean outputFoundation =
                foundations.eatingGodFoundationStable()
                        ||
                        foundations.hurtingOfficerFoundationStable();

        if (outputFoundation) {

            reasons.add(
                    CompanionMonthPatternReason
                            .OUTPUT_PRESENT
            );

            reasons.add(
                    CompanionMonthPatternReason
                            .OUTPUT_FOUNDATION_CONFIRMED
            );
        }

        boolean outputGeneratesVisibleWealth =
                outputFoundation
                        &&
                        visibleWealth
                                .stream()
                                .anyMatch(
                                        wealth ->
                                                anyOutputGenerates(
                                                        wealth,
                                                        tenGodInteractions,
                                                        foundations
                                                )
                                );

        if (outputGeneratesVisibleWealth) {

            reasons.add(
                    CompanionMonthPatternReason
                            .OUTPUT_GENERATES_EXPOSED_WEALTH
            );
        }

        boolean wealthRoute =
                !visibleWealth.isEmpty()
                        &&
                        wealthFoundation
                        &&
                        outputGeneratesVisibleWealth;

        if (wealthRoute) {

            routes.add(
                    CompanionMonthFormationRoute
                            .WEALTH_WITH_OUTPUT
            );

            reasons.add(
                    CompanionMonthPatternReason
                            .WEALTH_WITH_OUTPUT_FORMED
            );
        }

        /*
         * =================================================
         * 3. 透煞而遇制伏
         * =================================================
         */

        List<PatternTenGodNode> visibleKillings =
                tenGodInteractions
                        .visibleNodesOf(
                                MonthCommandPatternCandidateType
                                        .SEVEN_KILLINGS
                        );

        if (!visibleKillings.isEmpty()) {

            reasons.add(
                    CompanionMonthPatternReason
                            .SEVEN_KILLINGS_EXPOSED
            );
        }

        boolean killingStable =
                foundations
                        .sevenKillingsFoundationStable();

        if (killingStable) {

            reasons.add(
                    CompanionMonthPatternReason
                            .SEVEN_KILLINGS_FOUNDATION_CONFIRMED
            );
        }

        boolean killingControlled =
                killingStable
                        &&
                        visibleKillings
                                .stream()
                                .allMatch(
                                        killing ->
                                                anyStableOutputControls(
                                                        killing,
                                                        tenGodInteractions,
                                                        foundations
                                                )
                                );

        if (
                !visibleKillings.isEmpty()
                        &&
                        killingControlled
        ) {

            reasons.add(
                    CompanionMonthPatternReason
                            .OUTPUT_CONTROLS_EXPOSED_SEVEN_KILLINGS
            );

            routes.add(
                    CompanionMonthFormationRoute
                            .SEVEN_KILLINGS_WITH_OUTPUT_CONTROL
            );

            reasons.add(
                    CompanionMonthPatternReason
                            .SEVEN_KILLINGS_WITH_CONTROL_FORMED
            );
        }

        /*
         * =================================================
         * 최종
         * =================================================
         */

        if (!routes.isEmpty()) {

            return new CompanionMonthPatternAnalysis(

                    interaction.dayMaster(),

                    patternType,

                    CompanionMonthPatternStatus
                            .FORMED,

                    routes,

                    reasons
            );
        }

        /*
         * 사용할 글자는 투출됐는데
         * foundation이나 실제 route가 부족한 경우.
         */
        boolean relevantStemExposed =
                officerExposed
                        ||
                        !visibleWealth.isEmpty()
                        ||
                        !visibleKillings.isEmpty();

        if (relevantStemExposed) {

            reasons.add(
                    CompanionMonthPatternReason
                            .RELEVANT_STEM_EXPOSED_WITHOUT_CONFIRMED_FOUNDATION
            );

            reasons.add(
                    CompanionMonthPatternReason
                            .ADDITIONAL_REVIEW_REQUIRED
            );

            return new CompanionMonthPatternAnalysis(

                    interaction.dayMaster(),

                    patternType,

                    CompanionMonthPatternStatus
                            .REVIEW_REQUIRED,

                    routes,

                    reasons
            );
        }

        reasons.add(
                CompanionMonthPatternReason
                        .USABLE_ROUTE_NOT_FOUND
        );

        return new CompanionMonthPatternAnalysis(

                interaction.dayMaster(),

                patternType,

                CompanionMonthPatternStatus
                        .UNFORMED,

                routes,

                reasons
        );
    }

    /*
     * =================================================
     * 陽刃
     * =================================================
     */

    private CompanionMonthPatternAnalysis assessYangBlade(

            PatternInteractionAnalysis interaction,

            PatternTenGodInteractionAnalysis tenGodInteractions,

            PatternTenGodFoundationProfile foundations,

            PatternYangBladeAnalysis yangBladeAnalysis

    ) {

        EnumSet<CompanionMonthFormationRoute> routes =
                EnumSet.noneOf(
                        CompanionMonthFormationRoute.class
                );

        EnumSet<CompanionMonthPatternReason> reasons =
                EnumSet.noneOf(
                        CompanionMonthPatternReason.class
                );

        reasons.add(
                CompanionMonthPatternReason
                        .YANG_BLADE_MONTH_COMMAND
        );

        reasons.add(
                CompanionMonthPatternReason
                        .YANG_DAY_MASTER
        );

        /*
         * 月令 선택이 YANG_BLADE_REVIEW인데
         * 실제 月支 陽刃이 아니면
         * 데이터 계층이 모순.
         */
        if (!yangBladeAnalysis.hasMonthBlade()) {

            reasons.add(
                    CompanionMonthPatternReason
                            .SELECTION_AND_YANG_BLADE_ANALYSIS_INCONSISTENT
            );

            reasons.add(
                    CompanionMonthPatternReason
                            .ADDITIONAL_REVIEW_REQUIRED
            );

            return new CompanionMonthPatternAnalysis(

                    interaction.dayMaster(),

                    CompanionMonthPatternType
                            .YANG_BLADE,

                    CompanionMonthPatternStatus
                            .REVIEW_REQUIRED,

                    routes,

                    reasons
            );
        }

        reasons.add(
                CompanionMonthPatternReason
                        .MONTH_YANG_BLADE_CONFIRMED
        );

        /*
         * =================================================
         * 官殺 투출
         * =================================================
         */

        boolean officerExposed =
                hasVisible(
                        MonthCommandPatternCandidateType
                                .DIRECT_OFFICER,
                        tenGodInteractions
                );

        boolean killingExposed =
                hasVisible(
                        MonthCommandPatternCandidateType
                                .SEVEN_KILLINGS,
                        tenGodInteractions
                );

        if (officerExposed) {

            reasons.add(
                    CompanionMonthPatternReason
                            .DIRECT_OFFICER_EXPOSED
            );
        }

        if (killingExposed) {

            reasons.add(
                    CompanionMonthPatternReason
                            .SEVEN_KILLINGS_EXPOSED
            );
        }

        if (
                !officerExposed
                        &&
                        !killingExposed
        ) {

            reasons.add(
                    CompanionMonthPatternReason
                            .YANG_BLADE_AUTHORITY_NOT_EXPOSED
            );

            reasons.add(
                    CompanionMonthPatternReason
                            .USABLE_ROUTE_NOT_FOUND
            );

            return new CompanionMonthPatternAnalysis(

                    interaction.dayMaster(),

                    CompanionMonthPatternType
                            .YANG_BLADE,

                    CompanionMonthPatternStatus
                            .UNFORMED,

                    routes,

                    reasons
            );
        }

        /*
         * 官殺 자신도 실제 foundation이 있어야
         * 刃을 제복하는 힘이 확인된 것으로 본다.
         */

        boolean officerStable =
                officerExposed
                        &&
                        foundations
                                .directOfficerFoundationStable();

        boolean killingStable =
                killingExposed
                        &&
                        foundations
                                .sevenKillingsFoundationStable();

        if (officerStable) {

            routes.add(
                    CompanionMonthFormationRoute
                            .YANG_BLADE_WITH_DIRECT_OFFICER
            );

            reasons.add(
                    CompanionMonthPatternReason
                            .YANG_BLADE_DIRECT_OFFICER_ROUTE
            );
        }

        if (killingStable) {

            routes.add(
                    CompanionMonthFormationRoute
                            .YANG_BLADE_WITH_SEVEN_KILLINGS
            );

            reasons.add(
                    CompanionMonthPatternReason
                            .YANG_BLADE_SEVEN_KILLINGS_ROUTE
            );
        }

        boolean authorityStable =
                officerStable
                        ||
                        killingStable;

        if (authorityStable) {

            reasons.add(
                    CompanionMonthPatternReason
                            .YANG_BLADE_AUTHORITY_FOUNDATION_CONFIRMED
            );

        } else {

            reasons.add(
                    CompanionMonthPatternReason
                            .YANG_BLADE_AUTHORITY_FOUNDATION_NOT_CONFIRMED
            );

            reasons.add(
                    CompanionMonthPatternReason
                            .ADDITIONAL_REVIEW_REQUIRED
            );

            return new CompanionMonthPatternAnalysis(

                    interaction.dayMaster(),

                    CompanionMonthPatternType
                            .YANG_BLADE,

                    CompanionMonthPatternStatus
                            .REVIEW_REQUIRED,

                    routes,

                    reasons
            );
        }

        /*
         * =================================================
         * 財印 보조
         * =================================================
         *
         * 여기서는 財와 印 둘을 반드시 동시에
         * 요구하지 않는다.
         *
         * 적어도 하나의 유의미한 財/印 보조가
         * 투출되어 있는지를 확인한다.
         */

        boolean visibleWealthSupport =
                visibleWealthNodes(
                        tenGodInteractions
                )
                        .stream()
                        .findAny()
                        .isPresent()
                        &&
                        foundations
                                .wealthFoundationStable();

        boolean visibleResourceSupport =
                !visibleResourceNodes(
                        tenGodInteractions
                )
                        .isEmpty()
                        &&
                        foundations
                                .resourceFoundationStable();

        boolean supportExposed =
                visibleWealthSupport
                        ||
                        visibleResourceSupport;

        if (supportExposed) {

            reasons.add(
                    CompanionMonthPatternReason
                            .YANG_BLADE_WEALTH_OR_RESOURCE_SUPPORT_EXPOSED
            );
        }

        /*
         * =================================================
         * 傷官
         * =================================================
         */

        boolean hurtingOfficerExposed =
                hasVisible(
                        MonthCommandPatternCandidateType
                                .HURTING_OFFICER,
                        tenGodInteractions
                );

        if (hurtingOfficerExposed) {

            reasons.add(
                    CompanionMonthPatternReason
                            .HURTING_OFFICER_EXPOSED
            );

            reasons.add(
                    CompanionMonthPatternReason
                            .YANG_BLADE_DAMAGED_BY_HURTING_OFFICER
            );

            return new CompanionMonthPatternAnalysis(

                    interaction.dayMaster(),

                    CompanionMonthPatternType
                            .YANG_BLADE,

                    CompanionMonthPatternStatus
                            .FORMED_WITH_DEFECT,

                    routes,

                    reasons
            );
        }

        /*
         * 官殺은 뿌리가 있으나
         * 財印 보조 투출이 전혀 없는 경우는
         * 첫 버전에서는 성격 확정까지 밀지 않는다.
         */
        if (!supportExposed) {

            reasons.add(
                    CompanionMonthPatternReason
                            .YANG_BLADE_SUPPORT_NOT_EXPOSED
            );

            reasons.add(
                    CompanionMonthPatternReason
                            .ADDITIONAL_REVIEW_REQUIRED
            );

            return new CompanionMonthPatternAnalysis(

                    interaction.dayMaster(),

                    CompanionMonthPatternType
                            .YANG_BLADE,

                    CompanionMonthPatternStatus
                            .REVIEW_REQUIRED,

                    routes,

                    reasons
            );
        }

        return new CompanionMonthPatternAnalysis(

                interaction.dayMaster(),

                CompanionMonthPatternType
                        .YANG_BLADE,

                CompanionMonthPatternStatus
                        .FORMED,

                routes,

                reasons
        );
    }

    /*
     * =================================================
     * 建祿/月劫 실제 route helpers
     * =================================================
     */

    private boolean anyOutputGenerates(

            PatternTenGodNode wealth,

            PatternTenGodInteractionAnalysis interactions,

            PatternTenGodFoundationProfile foundations

    ) {

        if (
                foundations.eatingGodFoundationStable()
                        &&
                        nodesOf(
                                MonthCommandPatternCandidateType
                                        .EATING_GOD,
                                interactions
                        )
                                .stream()
                                .anyMatch(
                                        output ->
                                                generates(
                                                        output,
                                                        wealth,
                                                        interactions
                                                )
                                )
        ) {

            return true;
        }

        return foundations.hurtingOfficerFoundationStable()
                &&
                nodesOf(
                        MonthCommandPatternCandidateType
                                .HURTING_OFFICER,
                        interactions
                )
                        .stream()
                        .anyMatch(
                                output ->
                                        generates(
                                                output,
                                                wealth,
                                                interactions
                                        )
                        );
    }

    private boolean anyStableOutputControls(

            PatternTenGodNode killing,

            PatternTenGodInteractionAnalysis interactions,

            PatternTenGodFoundationProfile foundations

    ) {

        if (
                foundations.eatingGodFoundationStable()
                        &&
                        nodesOf(
                                MonthCommandPatternCandidateType
                                        .EATING_GOD,
                                interactions
                        )
                                .stream()
                                .anyMatch(
                                        output ->
                                                controls(
                                                        output,
                                                        killing,
                                                        interactions
                                                )
                                )
        ) {

            return true;
        }

        return foundations.hurtingOfficerFoundationStable()
                &&
                nodesOf(
                        MonthCommandPatternCandidateType
                                .HURTING_OFFICER,
                        interactions
                )
                        .stream()
                        .anyMatch(
                                output ->
                                        controls(
                                                output,
                                                killing,
                                                interactions
                                        )
                        );
    }

    /*
     * hidden/visible 모두 포함.
     *
     * "逢食傷", "遇制伏"의 보조 십성은
     * 반드시 천간 투출만으로 제한하지 않고
     * 실제 node + foundation 존재를 본다.
     */
    private List<PatternTenGodNode> nodesOf(

            MonthCommandPatternCandidateType type,

            PatternTenGodInteractionAnalysis interactions

    ) {

        return interactions
                .nodes()
                .stream()
                .filter(
                        node ->
                                !node.dayMaster()
                )
                .filter(
                        node ->
                                node.hasTenGodType(type)
                )
                .toList();
    }

    private List<PatternTenGodNode> visibleWealthNodes(
            PatternTenGodInteractionAnalysis interactions
    ) {

        return interactions
                .nodes()
                .stream()
                .filter(
                        PatternTenGodNode
                                ::visible
                )
                .filter(
                        node ->
                                node.hasTenGodType(
                                        MonthCommandPatternCandidateType
                                                .DIRECT_WEALTH
                                )

                                        ||

                                        node.hasTenGodType(
                                                MonthCommandPatternCandidateType
                                                        .INDIRECT_WEALTH
                                        )
                )
                .toList();
    }

    private List<PatternTenGodNode> visibleResourceNodes(
            PatternTenGodInteractionAnalysis interactions
    ) {

        return interactions
                .nodes()
                .stream()
                .filter(
                        PatternTenGodNode
                                ::visible
                )
                .filter(
                        node ->
                                node.hasTenGodType(
                                        MonthCommandPatternCandidateType
                                                .DIRECT_RESOURCE
                                )

                                        ||

                                        node.hasTenGodType(
                                                MonthCommandPatternCandidateType
                                                        .INDIRECT_RESOURCE
                                        )
                )
                .toList();
    }

    private boolean hasVisible(

            MonthCommandPatternCandidateType type,

            PatternTenGodInteractionAnalysis interactions

    ) {

        return !interactions
                .visibleNodesOf(type)
                .isEmpty();
    }

    /*
     * =================================================
     * graph
     * =================================================
     */

    private boolean generates(

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
                                        == PatternTenGodLinkType.GENERATES

                                        &&

                                        link.source()
                                                .equals(source)

                                        &&

                                        link.target()
                                                .equals(target)
                );
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
     * validation
     * =================================================
     */

    private void validateDayMaster(

            PatternInteractionAnalysis interaction,

            PatternTenGodInteractionAnalysis tenGodInteractions,

            PatternTenGodFoundationProfile foundations,

            PatternYangBladeAnalysis yangBladeAnalysis

    ) {

        HeavenlyStem dayMaster =
                interaction.dayMaster();

        if (
                tenGodInteractions.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "PatternInteractionAnalysis와 TenGodInteraction의 일간이 일치하지 않습니다."
            );
        }

        if (
                foundations.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "PatternInteractionAnalysis와 FoundationProfile의 일간이 일치하지 않습니다."
            );
        }

        if (
                yangBladeAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "PatternInteractionAnalysis와 YangBladeAnalysis의 일간이 일치하지 않습니다."
            );
        }

        MonthCommandPatternSelectionPath path =
                interaction
                        .patternSelection()
                        .path();

        if (
                path == MonthCommandPatternSelectionPath
                        .YANG_BLADE_REVIEW

                        &&

                        !isYangDayMaster(dayMaster)
        ) {

            throw new IllegalArgumentException(
                    "陰日主가 YANG_BLADE_REVIEW 경로로 들어왔습니다."
            );
        }

        if (
                path == MonthCommandPatternSelectionPath
                        .YUEJIE_REVIEW

                        &&

                        isYangDayMaster(dayMaster)
        ) {

            throw new IllegalArgumentException(
                    "陽日主가 YUEJIE_REVIEW 경로로 들어왔습니다."
            );
        }
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