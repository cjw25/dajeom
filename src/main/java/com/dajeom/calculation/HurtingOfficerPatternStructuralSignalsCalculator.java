package com.dajeom.calculation;

import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.HiddenStemPhase;
import com.dajeom.domain.HurtingOfficerPatternRelationContext;
import com.dajeom.domain.HurtingOfficerPatternStructuralSignals;
import com.dajeom.domain.MonthCommandPatternCandidate;
import com.dajeom.domain.MonthCommandPatternCandidateType;
import com.dajeom.domain.PatternInteractionAnalysis;
import com.dajeom.domain.PatternTenGodInteractionAnalysis;
import com.dajeom.domain.PatternTenGodLink;
import com.dajeom.domain.PatternTenGodLinkType;
import com.dajeom.domain.PatternTenGodNode;
import com.dajeom.domain.PillarPosition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 傷官格에 필요한
 * HurtingOfficerPatternStructuralSignals를
 * 원국 십성 그래프에서 자동 생성한다.
 *
 * =================================================
 *
 * 자동으로 추적하는 주요 구조:
 *
 * 傷官 → 財
 *
 * 印 → 傷官
 *
 * 傷官 → 正官
 *
 * 傷官 → 財 → 正官
 *
 * 七殺 → 印 → 日主
 *
 * 傷官 → 七殺
 *
 * 傷官 → 財 → 七殺
 *
 * 財 → 印
 *
 * =================================================
 *
 * raw 그래프만으로 확정하지 않는 것:
 *
 * - 傷官生財 최종 성립
 * - 傷官佩印 최종 성립
 * - 실제 傷官見官
 * - 金水傷官 조후
 * - 財合
 * - 印 손상
 * - 合殺
 * - 官殺取清
 *
 * 이 부분은 RelationContext와 결합한다.
 *
 * =================================================
 *
 * 숫자 / 점수 / 가중치 / 다수결 없음.
 */
@Component
public class HurtingOfficerPatternStructuralSignalsCalculator {

    public HurtingOfficerPatternStructuralSignals analyze(

            PatternInteractionAnalysis interaction,

            PatternTenGodInteractionAnalysis tenGodInteractions,

            HurtingOfficerPatternRelationContext relationContext

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
                relationContext,
                "HurtingOfficerPatternRelationContext는 필수입니다."
        );

        validateDayMaster(
                interaction,
                tenGodInteractions
        );

        MonthCommandPatternCandidate selectedPattern =
                interaction
                        .provisionalStandardCandidate()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "傷官格 StructuralSignals를 만들려면 "
                                                        +
                                                        "선택된 일반격 후보가 필요합니다."
                                        )
                        );

        validateHurtingOfficerPattern(
                selectedPattern
        );

        PatternTenGodNode selectedHurtingOfficer =
                findSelectedHurtingOfficerNode(
                        tenGodInteractions,
                        selectedPattern
                );

        /*
         * =================================================
         * 1. 傷官 기반
         * =================================================
         */

        boolean hurtingOfficerFoundationStable =
                relationContext
                        .hurtingOfficerFoundationStable();

        /*
         * =================================================
         * 2. 傷官 → 財
         * =================================================
         */

        List<PatternTenGodNode> generatedVisibleWealth =
                visibleWealthGeneratedBy(
                        tenGodInteractions,
                        selectedHurtingOfficer
                );

        boolean rawHurtingOfficerGeneratesWealth =
                !generatedVisibleWealth
                        .isEmpty();

        boolean wealthFoundationStable =
                rawHurtingOfficerGeneratesWealth
                        &&
                        relationContext
                                .wealthFoundationStable();

        boolean hurtingOfficerGeneratesWealthIntact =
                rawHurtingOfficerGeneratesWealth
                        &&
                        wealthFoundationStable
                        &&
                        relationContext
                                .wealthGenerationRouteUsable();

        /*
         * =================================================
         * 3. 印 → 傷官
         * =================================================
         *
         * 반드시 월령 MAIN 傷官을
         * 투출 印이 직접 剋해야 한다.
         */

        boolean rawResourceControlsHurtingOfficer =
                hasVisibleResourceControlTarget(
                        tenGodInteractions,
                        selectedHurtingOfficer
                );

        boolean resourceFoundationStable =
                rawResourceControlsHurtingOfficer
                        &&
                        relationContext
                                .resourceFoundationStable();

        boolean resourceActuallyControlsHurtingOfficer =
                rawResourceControlsHurtingOfficer
                        &&
                        resourceFoundationStable
                        &&
                        relationContext
                                .resourceControlRouteUsable();

        /*
         * =================================================
         * 4. 佩印而傷輕身旺
         * =================================================
         */

        boolean hurtingOfficerTooLightForResourceRoute =
                resourceActuallyControlsHurtingOfficer
                        &&
                        relationContext
                                .hurtingOfficerTooLightForResourceRoute();

        /*
         * =================================================
         * 5. 七殺 → 印 → 日主
         * =================================================
         */

        boolean rawKillingResourceDayMasterChain =
                hasVisibleKillingResourceDayMasterChain(
                        tenGodInteractions
                );

        boolean killingGeneratesResourceToSupportDayMaster =
                rawKillingResourceDayMasterChain
                        &&
                        relationContext
                                .killingResourceChainUsable();

        /*
         * =================================================
         * 6. 傷官 → 七殺
         * =================================================
         *
         * 傷官帶煞에서
         * 월령 傷官과 투출 七殺 사이의
         * 직접 raw 관계를 확인한다.
         */

        boolean rawHurtingOfficerControlsSevenKillings =
                hasTargetControlVisibleType(
                        tenGodInteractions,
                        selectedHurtingOfficer,
                        MonthCommandPatternCandidateType
                                .SEVEN_KILLINGS
                );

        boolean hurtingOfficerAndKillingUsableWithoutWealth =
                rawHurtingOfficerControlsSevenKillings
                        &&
                        relationContext
                                .hurtingOfficerKillingRouteUsableWithoutWealth();

        /*
         * =================================================
         * 7. 傷官見官
         * =================================================
         */

        boolean rawHurtingOfficerControlsDirectOfficer =
                hasTargetControlVisibleType(
                        tenGodInteractions,
                        selectedHurtingOfficer,
                        MonthCommandPatternCandidateType
                                .DIRECT_OFFICER
                );

        boolean directOfficerActuallyConflictsWithHurtingOfficer =
                rawHurtingOfficerControlsDirectOfficer
                        &&
                        relationContext
                                .directOfficerConflictActive();

        /*
         * =================================================
         * 8. 傷官 → 財 → 正官
         * =================================================
         *
         * 동일 財 노드를 반드시 추적한다.
         *
         * 傷官
         *   ↓ 生
         * 財
         *   ↓ 生
         * 正官
         */

        boolean rawHurtingOfficerWealthOfficerChain =
                hasGeneratedWealthGeneratingVisibleType(
                        tenGodInteractions,
                        generatedVisibleWealth,
                        MonthCommandPatternCandidateType
                                .DIRECT_OFFICER
                );

        boolean officerConflictResolvedByWealthBridge =
                directOfficerActuallyConflictsWithHurtingOfficer
                        &&
                        rawHurtingOfficerWealthOfficerChain
                        &&
                        relationContext
                                .officerConflictWealthBridgeUsable();

        /*
         * =================================================
         * 9. 金水傷官
         * =================================================
         *
         * 金日主라는 사실만으로 true가 아니다.
         *
         * 실제 正官이 존재하고
         * 조후/財印 조건까지 확인되어야 한다.
         */

        boolean metalWaterOfficerRouteUsable =
                isMetalDayMaster(
                        interaction.dayMaster()
                )
                        &&
                        interaction
                                .directOfficerExposed()
                        &&
                        relationContext
                                .metalWaterOfficerRouteUsable();

        /*
         * =================================================
         * 10. 傷官生財而財又逢合
         * =================================================
         */

        boolean wealthCombined =
                hurtingOfficerGeneratesWealthIntact
                        &&
                        relationContext
                                .wealthCombined();

        /*
         * =================================================
         * 11. 佩印而印又遭傷
         * =================================================
         */

        boolean resourceDamaged =
                resourceActuallyControlsHurtingOfficer
                        &&
                        relationContext
                                .resourceDamaged();

        /*
         * =================================================
         * 12. 傷官 → 財 → 七殺
         * =================================================
         *
         * 역시 동일 財 노드를 추적한다.
         */

        boolean rawHurtingOfficerWealthKillingChain =
                hasGeneratedWealthGeneratingVisibleType(
                        tenGodInteractions,
                        generatedVisibleWealth,
                        MonthCommandPatternCandidateType
                                .SEVEN_KILLINGS
                );

        boolean sevenKillingsDamagesWealthRoute =
                hurtingOfficerGeneratesWealthIntact
                        &&
                        rawHurtingOfficerWealthKillingChain
                        &&
                        relationContext
                                .wealthKillingRouteDamaging();

        /*
         * =================================================
         * 13. 合殺
         * =================================================
         */

        boolean sevenKillingsCombinedAway =
                sevenKillingsDamagesWealthRoute
                        &&
                        relationContext
                                .sevenKillingsCombinedAway();

        /*
         * =================================================
         * 14. 七殺 → 印 → 身
         *     + 財 → 같은 印
         * =================================================
         */

        boolean rawWealthDamagesKillingResourceRoute =
                hasVisibleWealthDamagingKillingResourceChain(
                        tenGodInteractions
                );

        boolean wealthDamagesKillingResourceRoute =
                killingGeneratesResourceToSupportDayMaster
                        &&
                        rawWealthDamagesKillingResourceRoute
                        &&
                        relationContext
                                .wealthDamageKillingResourceRouteActive();

        /*
         * =================================================
         * 15. 官殺混雜 取清
         * =================================================
         */

        boolean officerKillingsMixtureCleared =
                interaction
                        .directOfficerExposed()

                        &&

                        interaction
                                .sevenKillingsExposed()

                        &&

                        relationContext
                                .officerKillingsMixtureCleared();

        return new HurtingOfficerPatternStructuralSignals(

                hurtingOfficerFoundationStable,

                wealthFoundationStable,

                hurtingOfficerGeneratesWealthIntact,

                resourceFoundationStable,

                resourceActuallyControlsHurtingOfficer,

                hurtingOfficerTooLightForResourceRoute,

                killingGeneratesResourceToSupportDayMaster,

                hurtingOfficerAndKillingUsableWithoutWealth,

                directOfficerActuallyConflictsWithHurtingOfficer,

                officerConflictResolvedByWealthBridge,

                metalWaterOfficerRouteUsable,

                wealthCombined,

                resourceDamaged,

                sevenKillingsDamagesWealthRoute,

                sevenKillingsCombinedAway,

                wealthDamagesKillingResourceRoute,

                officerKillingsMixtureCleared
        );
    }

    /**
     * 월령 MAIN 傷官 노드를 정확히 찾는다.
     */
    private PatternTenGodNode findSelectedHurtingOfficerNode(

            PatternTenGodInteractionAnalysis analysis,

            MonthCommandPatternCandidate selectedPattern

    ) {

        return analysis
                .nodes()
                .stream()
                .filter(
                        PatternTenGodNode
                                ::hidden
                )
                .filter(
                        node ->
                                node.position()
                                        == PillarPosition.MONTH
                )
                .filter(
                        node ->
                                node.stem()
                                        == selectedPattern.hiddenStem()
                )
                .filter(
                        node ->
                                node.hiddenStemPhase()
                                        .orElse(null)
                                        == selectedPattern.phase()
                )
                .filter(
                        node ->
                                node.hasTenGodType(
                                        MonthCommandPatternCandidateType
                                                .HURTING_OFFICER
                                )
                )
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "월령 MAIN 傷官 노드를 십성 그래프에서 찾을 수 없습니다."
                                )
                );
    }

    /**
     * 특정 傷官 노드가 生하는
     * 투출 財 노드들.
     */
    private List<PatternTenGodNode> visibleWealthGeneratedBy(

            PatternTenGodInteractionAnalysis analysis,

            PatternTenGodNode hurtingOfficer

    ) {

        return analysis
                .links()
                .stream()
                .filter(
                        link ->
                                link.type()
                                        == PatternTenGodLinkType.GENERATES
                )
                .filter(
                        link ->
                                link.source()
                                        .equals(
                                                hurtingOfficer
                                        )
                )
                .filter(
                        link ->
                                link.target()
                                        .visible()
                )
                .filter(
                        link ->
                                isWealth(
                                        link.target()
                                )
                )
                .map(
                        PatternTenGodLink
                                ::target
                )
                .distinct()
                .toList();
    }

    /**
     * 투출 正印/偏印이
     * 월령 傷官을 직접 剋하는가.
     */
    private boolean hasVisibleResourceControlTarget(

            PatternTenGodInteractionAnalysis analysis,

            PatternTenGodNode target

    ) {

        return analysis
                .links()
                .stream()
                .anyMatch(
                        link ->
                                link.type()
                                        == PatternTenGodLinkType.CONTROLS

                                        &&

                                        link.source()
                                                .visible()

                                        &&

                                        isResource(
                                                link.source()
                                        )

                                        &&

                                        link.target()
                                                .equals(
                                                        target
                                                )
                );
    }

    /**
     * 七殺 → 印 → 日主.
     *
     * 같은 印 노드를 통과해야 한다.
     */
    private boolean hasVisibleKillingResourceDayMasterChain(
            PatternTenGodInteractionAnalysis analysis
    ) {

        for (
                PatternTenGodNode resource
                : visibleResourceNodes(
                analysis
        )
        ) {

            boolean killingGeneratesResource =
                    analysis
                            .links()
                            .stream()
                            .anyMatch(
                                    link ->
                                            link.type()
                                                    == PatternTenGodLinkType.GENERATES

                                                    &&

                                                    link.source()
                                                            .visible()

                                                    &&

                                                    link.source()
                                                            .hasTenGodType(
                                                                    MonthCommandPatternCandidateType
                                                                            .SEVEN_KILLINGS
                                                            )

                                                    &&

                                                    link.target()
                                                            .equals(
                                                                    resource
                                                            )
                            );

            if (!killingGeneratesResource) {

                continue;
            }

            boolean resourceGeneratesDayMaster =
                    analysis
                            .links()
                            .stream()
                            .anyMatch(
                                    link ->
                                            link.type()
                                                    == PatternTenGodLinkType.GENERATES

                                                    &&

                                                    link.source()
                                                            .equals(
                                                                    resource
                                                            )

                                                    &&

                                                    link.target()
                                                            .dayMaster()
                            );

            if (resourceGeneratesDayMaster) {

                return true;
            }
        }

        return false;
    }

    /**
     * 특정 source가
     * 투출 targetType을 직접 剋하는가.
     */
    private boolean hasTargetControlVisibleType(

            PatternTenGodInteractionAnalysis analysis,

            PatternTenGodNode source,

            MonthCommandPatternCandidateType targetType

    ) {

        return analysis
                .links()
                .stream()
                .anyMatch(
                        link ->
                                link.type()
                                        == PatternTenGodLinkType.CONTROLS

                                        &&

                                        link.source()
                                                .equals(
                                                        source
                                                )

                                        &&

                                        link.target()
                                                .visible()

                                        &&

                                        link.target()
                                                .hasTenGodType(
                                                        targetType
                                                )
                );
    }

    /**
     * 傷官이 生한 동일 財 노드들 중 하나가
     * 다시 targetType을 生하는가.
     *
     * 예:
     *
     * 傷官 → 財 → 正官
     *
     * 傷官 → 財 → 七殺
     */
    private boolean hasGeneratedWealthGeneratingVisibleType(

            PatternTenGodInteractionAnalysis analysis,

            List<PatternTenGodNode> wealthNodes,

            MonthCommandPatternCandidateType targetType

    ) {

        if (
                wealthNodes == null
                        ||
                        wealthNodes.isEmpty()
        ) {

            return false;
        }

        return analysis
                .links()
                .stream()
                .anyMatch(
                        link ->
                                link.type()
                                        == PatternTenGodLinkType.GENERATES

                                        &&

                                        wealthNodes.contains(
                                                link.source()
                                        )

                                        &&

                                        link.target()
                                                .visible()

                                        &&

                                        link.target()
                                                .hasTenGodType(
                                                        targetType
                                                )
                );
    }

    /**
     * 같은 印 노드를 중심으로:
     *
     * 七殺
     *   ↓ 生
     * 印
     *   ↓ 生
     * 日主
     *
     * 그리고
     *
     * 財
     *   ↓ 剋
     * 같은 印
     */
    private boolean hasVisibleWealthDamagingKillingResourceChain(
            PatternTenGodInteractionAnalysis analysis
    ) {

        for (
                PatternTenGodNode resource
                : visibleResourceNodes(
                analysis
        )
        ) {

            boolean killingGeneratesResource =
                    analysis
                            .links()
                            .stream()
                            .anyMatch(
                                    link ->
                                            link.type()
                                                    == PatternTenGodLinkType.GENERATES

                                                    &&

                                                    link.source()
                                                            .visible()

                                                    &&

                                                    link.source()
                                                            .hasTenGodType(
                                                                    MonthCommandPatternCandidateType
                                                                            .SEVEN_KILLINGS
                                                            )

                                                    &&

                                                    link.target()
                                                            .equals(
                                                                    resource
                                                            )
                            );

            if (!killingGeneratesResource) {

                continue;
            }

            boolean resourceGeneratesDayMaster =
                    analysis
                            .links()
                            .stream()
                            .anyMatch(
                                    link ->
                                            link.type()
                                                    == PatternTenGodLinkType.GENERATES

                                                    &&

                                                    link.source()
                                                            .equals(
                                                                    resource
                                                            )

                                                    &&

                                                    link.target()
                                                            .dayMaster()
                            );

            if (!resourceGeneratesDayMaster) {

                continue;
            }

            boolean wealthControlsSameResource =
                    analysis
                            .links()
                            .stream()
                            .anyMatch(
                                    link ->
                                            link.type()
                                                    == PatternTenGodLinkType.CONTROLS

                                                    &&

                                                    link.source()
                                                            .visible()

                                                    &&

                                                    isWealth(
                                                            link.source()
                                                    )

                                                    &&

                                                    link.target()
                                                            .equals(
                                                                    resource
                                                            )
                            );

            if (wealthControlsSameResource) {

                return true;
            }
        }

        return false;
    }

    private List<PatternTenGodNode> visibleResourceNodes(
            PatternTenGodInteractionAnalysis analysis
    ) {

        return analysis
                .nodes()
                .stream()
                .filter(
                        PatternTenGodNode
                                ::visible
                )
                .filter(
                        this::isResource
                )
                .toList();
    }

    private boolean isWealth(
            PatternTenGodNode node
    ) {

        return node.hasTenGodType(
                MonthCommandPatternCandidateType
                        .DIRECT_WEALTH
        )
                ||
                node.hasTenGodType(
                        MonthCommandPatternCandidateType
                                .INDIRECT_WEALTH
                );
    }

    private boolean isResource(
            PatternTenGodNode node
    ) {

        return node.hasTenGodType(
                MonthCommandPatternCandidateType
                        .DIRECT_RESOURCE
        )
                ||
                node.hasTenGodType(
                        MonthCommandPatternCandidateType
                                .INDIRECT_RESOURCE
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

    private void validateDayMaster(

            PatternInteractionAnalysis interaction,

            PatternTenGodInteractionAnalysis tenGodInteractions

    ) {

        HeavenlyStem dayMaster =
                interaction.dayMaster();

        if (
                tenGodInteractions.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "PatternInteractionAnalysis와 "
                            +
                            "PatternTenGodInteractionAnalysis의 일간이 일치하지 않습니다."
            );
        }
    }

    private void validateHurtingOfficerPattern(
            MonthCommandPatternCandidate selectedPattern
    ) {

        if (
                selectedPattern
                        .candidateType()
                        != MonthCommandPatternCandidateType
                        .HURTING_OFFICER
        ) {

            throw new IllegalArgumentException(
                    "HurtingOfficerPatternStructuralSignalsCalculator에는 "
                            +
                            "HURTING_OFFICER 격국 후보만 사용할 수 있습니다."
            );
        }
    }
}