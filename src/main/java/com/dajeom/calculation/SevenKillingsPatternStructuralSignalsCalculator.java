package com.dajeom.calculation;

import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.HiddenStemPhase;
import com.dajeom.domain.MonthCommandPatternCandidate;
import com.dajeom.domain.MonthCommandPatternCandidateType;
import com.dajeom.domain.PatternInteractionAnalysis;
import com.dajeom.domain.PatternTenGodInteractionAnalysis;
import com.dajeom.domain.PatternTenGodLink;
import com.dajeom.domain.PatternTenGodLinkType;
import com.dajeom.domain.PatternTenGodNode;
import com.dajeom.domain.PillarPosition;
import com.dajeom.domain.SevenKillingsPatternRelationContext;
import com.dajeom.domain.SevenKillingsPatternStructuralSignals;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 七殺格에 필요한 SevenKillingsPatternStructuralSignals를
 * 원국 십성 생극 그래프에서 자동 생성한다.
 *
 * =================================================
 *
 * 월령 MAIN 七殺 노드를 기준으로 추적:
 *
 * 食神 → 七殺
 *
 * 七殺 → 印 → 日主
 *
 * 財 → 七殺
 *
 * 財 → 七殺 → 印 → 日主
 *
 * 印 → 食神
 *
 * 財 → 印 → 食神
 *
 * =================================================
 *
 * 그래프만으로 확정하지 않는 것:
 *
 * - 食神制殺의 실제 작동
 * - 殺印相生의 실제 작동
 * - 財滋殺의 실제 작동
 * - 印奪食
 * - 財去印存食
 * - 官殺取清
 * - 七殺 천간합
 * - 月令의 실제 충형파해
 *
 * 위 항목은 RelationContext와 결합한다.
 *
 * =================================================
 *
 * 숫자 / 점수 / 가중치 / 다수결 없음.
 */
@Component
public class SevenKillingsPatternStructuralSignalsCalculator {

    public SevenKillingsPatternStructuralSignals analyze(

            PatternInteractionAnalysis interaction,

            PatternTenGodInteractionAnalysis tenGodInteractions,

            SevenKillingsPatternRelationContext relationContext

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
                "SevenKillingsPatternRelationContext는 필수입니다."
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
                                                "七殺格 StructuralSignals를 만들려면 "
                                                        +
                                                        "선택된 일반격 후보가 필요합니다."
                                        )
                        );

        validateSevenKillingsPattern(
                selectedPattern
        );

        /*
         * =================================================
         * 1. 월령 MAIN 七殺 노드
         * =================================================
         */

        PatternTenGodNode selectedSevenKillings =
                findSelectedSevenKillingsNode(
                        tenGodInteractions,
                        selectedPattern
                );

        /*
         * =================================================
         * 2. 七殺 기반
         * =================================================
         */

        boolean sevenKillingsFoundationStable =
                relationContext
                        .sevenKillingsFoundationStable();

        /*
         * =================================================
         * 3. 食神 → 월령 七殺
         * =================================================
         *
         * 월령 七殺을 실제 제하는
         * 투출 食神 노드를 찾는다.
         */

        List<PatternTenGodNode> controllingEatingGodNodes =
                visibleEatingGodControllingTarget(
                        tenGodInteractions,
                        selectedSevenKillings
                );

        boolean rawEatingGodControlsSevenKillings =
                !controllingEatingGodNodes
                        .isEmpty();

        boolean eatingGodActuallyControlsSevenKillings =
                rawEatingGodControlsSevenKillings
                        &&
                        relationContext
                                .eatingGodControlRouteUsable();

        boolean eatingGodFoundationStable =
                rawEatingGodControlsSevenKillings
                        &&
                        relationContext
                                .eatingGodFoundationStable();

        /*
         * =================================================
         * 4. 七殺 → 印 → 日主
         * =================================================
         *
         * 반드시 같은 印 노드를 통과한다.
         */

        List<PatternTenGodNode> killingResourceNodes =
                visibleResourcesInKillingResourceChain(
                        tenGodInteractions,
                        selectedSevenKillings
                );

        boolean rawKillingResourceChain =
                !killingResourceNodes
                        .isEmpty();

        boolean resourceFoundationStable =
                rawKillingResourceChain
                        &&
                        relationContext
                                .resourceFoundationStable();

        boolean killingGeneratesResourceToSupportDayMaster =
                rawKillingResourceChain
                        &&
                        resourceFoundationStable
                        &&
                        relationContext
                                .killingResourceChainUsable();

        /*
         * =================================================
         * 5. 印 → 食神
         * =================================================
         *
         * 食神制殺에 사용되는
         * 바로 그 食神을 印이 억누르는지를 본다.
         *
         * 아무 印
         * +
         * 아무 食神
         *
         * 이 아니다.
         */

        List<PatternTenGodNode> suppressingResourceNodes =
                visibleResourcesControllingAnyEatingGod(
                        tenGodInteractions,
                        controllingEatingGodNodes
                );

        boolean rawResourceSuppressesEatingGod =
                !suppressingResourceNodes
                        .isEmpty();

        boolean resourceActuallySuppressesEatingGod =
                eatingGodActuallyControlsSevenKillings
                        &&
                        rawResourceSuppressesEatingGod
                        &&
                        relationContext
                                .resourceSuppressionActive();

        /*
         * =================================================
         * 6. 財去印存食
         * =================================================
         *
         * 食神을 누르는 바로 그 印을
         * 財가 제어하는가.
         *
         * 財
         *   ↓ 剋
         * 印
         *   ↓ 剋
         * 食神
         *   ↓ 剋
         * 七殺
         */

        boolean rawWealthRemovesSuppressingResource =
                hasVisibleWealthControllingAny(
                        tenGodInteractions,
                        suppressingResourceNodes
                );

        boolean wealthRemovesResourcePreservingEatingGod =
                resourceActuallySuppressesEatingGod
                        &&
                        rawWealthRemovesSuppressingResource
                        &&
                        relationContext
                                .wealthRemovalRouteUsable();

        /*
         * =================================================
         * 7. 財 → 월령 七殺
         * =================================================
         */

        List<PatternTenGodNode> wealthNodesStrengtheningKilling =
                visibleWealthGeneratingTarget(
                        tenGodInteractions,
                        selectedSevenKillings
                );

        boolean rawWealthStrengthensSevenKillings =
                !wealthNodesStrengtheningKilling
                        .isEmpty();

        boolean wealthActuallyStrengthensSevenKillings =
                rawWealthStrengthensSevenKillings
                        &&
                        relationContext
                                .wealthStrengtheningRouteActive();

        /*
         * =================================================
         * 8. 財 → 七殺 → 印 → 日主
         * =================================================
         *
         * 같은 월령 七殺과
         * 같은 印 노드를 반드시 추적한다.
         */

        boolean rawWealthKillingResourceDayMasterChain =
                rawWealthStrengthensSevenKillings
                        &&
                        rawKillingResourceChain;

        boolean wealthKillingResourceChainUsable =
                rawWealthKillingResourceDayMasterChain
                        &&
                        relationContext
                                .wealthKillingResourceChainUsable();

        /*
         * =================================================
         * 9. 殺印相生의 印을 財가 손상
         * =================================================
         *
         * 七殺 → 印 → 身에 실제 사용되는
         * 바로 그 印 노드를 財가 剋하는가.
         */

        boolean rawWealthDamagesKillingResource =
                hasVisibleWealthControllingAny(
                        tenGodInteractions,
                        killingResourceNodes
                );

        boolean wealthDamagesResourceRoute =
                killingGeneratesResourceToSupportDayMaster
                        &&
                        rawWealthDamagesKillingResource
                        &&
                        relationContext
                                .wealthDamageResourceRouteActive();

        /*
         * =================================================
         * 10. 官殺取清
         * =================================================
         */

        boolean officerKillingsMixtureCleared =
                interaction
                        .directOfficerExposed()

                        &&

                        relationContext
                                .officerKillingsMixtureCleared();

        /*
         * =================================================
         * 11. 七殺 合
         * =================================================
         */

        boolean sevenKillingsStemCombined =
                relationContext
                        .sevenKillingsStemCombined();

        /*
         * =================================================
         * 12. 月令 교란
         * =================================================
         */

        boolean monthCommandDisrupted =
                relationContext
                        .monthCommandDisrupted();

        boolean monthCommandDisruptionResolved =
                monthCommandDisrupted
                        &&
                        relationContext
                                .monthCommandDisruptionResolved();

        return new SevenKillingsPatternStructuralSignals(

                sevenKillingsFoundationStable,

                eatingGodActuallyControlsSevenKillings,

                eatingGodFoundationStable,

                killingGeneratesResourceToSupportDayMaster,

                resourceFoundationStable,

                resourceActuallySuppressesEatingGod,

                wealthRemovesResourcePreservingEatingGod,

                wealthActuallyStrengthensSevenKillings,

                wealthKillingResourceChainUsable,

                wealthDamagesResourceRoute,

                officerKillingsMixtureCleared,

                sevenKillingsStemCombined,

                monthCommandDisrupted,

                monthCommandDisruptionResolved
        );
    }

    /**
     * 월령 MAIN 七殺 노드를 정확히 찾는다.
     */
    private PatternTenGodNode findSelectedSevenKillingsNode(

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
                                                .SEVEN_KILLINGS
                                )
                )
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "월령 MAIN 七殺 노드를 십성 그래프에서 찾을 수 없습니다."
                                )
                );
    }

    /**
     * 투출 食神 → 특정 七殺.
     *
     * target 七殺은 월령 MAIN 지장간이어도 된다.
     */
    private List<PatternTenGodNode>
    visibleEatingGodControllingTarget(

            PatternTenGodInteractionAnalysis analysis,

            PatternTenGodNode sevenKillings

    ) {

        return analysis
                .links()
                .stream()
                .filter(
                        link ->
                                link.type()
                                        == PatternTenGodLinkType.CONTROLS
                )
                .filter(
                        link ->
                                link.source()
                                        .visible()
                )
                .filter(
                        link ->
                                link.source()
                                        .hasTenGodType(
                                                MonthCommandPatternCandidateType
                                                        .EATING_GOD
                                        )
                )
                .filter(
                        link ->
                                link.target()
                                        .equals(
                                                sevenKillings
                                        )
                )
                .map(
                        PatternTenGodLink
                                ::source
                )
                .distinct()
                .toList();
    }

    /**
     * 월령 七殺 → 투출 印 → 日主.
     *
     * 같은 印 노드를 통해야 한다.
     */
    private List<PatternTenGodNode>
    visibleResourcesInKillingResourceChain(

            PatternTenGodInteractionAnalysis analysis,

            PatternTenGodNode sevenKillings

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
                .filter(
                        resource -> {

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
                                                                            .equals(
                                                                                    sevenKillings
                                                                            )

                                                                    &&

                                                                    link.target()
                                                                            .equals(
                                                                                    resource
                                                                            )
                                            );

                            if (!killingGeneratesResource) {

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

                                                            link.source()
                                                                    .equals(
                                                                            resource
                                                                    )

                                                            &&

                                                            link.target()
                                                                    .dayMaster()
                                    );
                        }
                )
                .toList();
    }

    /**
     * 食神制殺에 관여하는 食神들 중 하나를
     * 투출 印이 실제 剋하는가.
     *
     * 반환값은 그 印 노드들.
     */
    private List<PatternTenGodNode>
    visibleResourcesControllingAnyEatingGod(

            PatternTenGodInteractionAnalysis analysis,

            List<PatternTenGodNode> eatingGodNodes

    ) {

        if (
                eatingGodNodes == null
                        ||
                        eatingGodNodes.isEmpty()
        ) {

            return List.of();
        }

        return analysis
                .links()
                .stream()
                .filter(
                        link ->
                                link.type()
                                        == PatternTenGodLinkType.CONTROLS
                )
                .filter(
                        link ->
                                link.source()
                                        .visible()
                )
                .filter(
                        link ->
                                isResource(
                                        link.source()
                                )
                )
                .filter(
                        link ->
                                eatingGodNodes
                                        .contains(
                                                link.target()
                                        )
                )
                .map(
                        PatternTenGodLink
                                ::source
                )
                .distinct()
                .toList();
    }

    /**
     * 投出 財 → 특정 七殺.
     */
    private List<PatternTenGodNode>
    visibleWealthGeneratingTarget(

            PatternTenGodInteractionAnalysis analysis,

            PatternTenGodNode sevenKillings

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
                                        .visible()
                )
                .filter(
                        link ->
                                isWealth(
                                        link.source()
                                )
                )
                .filter(
                        link ->
                                link.target()
                                        .equals(
                                                sevenKillings
                                        )
                )
                .map(
                        PatternTenGodLink
                                ::source
                )
                .distinct()
                .toList();
    }

    /**
     * 투출 財가
     * 전달받은 대상 노드들 중 하나를
     * 실제 剋하는가.
     *
     * 사용 예:
     *
     * 財 → 印
     *
     * 財 → 食神을 억누르는 印
     */
    private boolean hasVisibleWealthControllingAny(

            PatternTenGodInteractionAnalysis analysis,

            List<PatternTenGodNode> targets

    ) {

        if (
                targets == null
                        ||
                        targets.isEmpty()
        ) {

            return false;
        }

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

                                        isWealth(
                                                link.source()
                                        )

                                        &&

                                        targets
                                                .contains(
                                                        link.target()
                                                )
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

    private void validateSevenKillingsPattern(
            MonthCommandPatternCandidate selectedPattern
    ) {

        if (
                selectedPattern
                        .candidateType()
                        != MonthCommandPatternCandidateType
                        .SEVEN_KILLINGS
        ) {

            throw new IllegalArgumentException(
                    "SevenKillingsPatternStructuralSignalsCalculator에는 "
                            +
                            "SEVEN_KILLINGS 격국 후보만 사용할 수 있습니다."
            );
        }
    }
}