package com.dajeom.calculation;

import com.dajeom.domain.EatingGodPatternRelationContext;
import com.dajeom.domain.EatingGodPatternStructuralSignals;
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
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 食神格에 필요한
 * EatingGodPatternStructuralSignals를
 * 원국 십성 생극 그래프에서 자동 생성한다.
 *
 * =================================================
 *
 * 자동으로 확인:
 *
 * 食神 → 財
 *
 * 食神 → 七殺
 *
 * 偏印 → 食神
 *
 * 財 → 偏印
 *
 * 財 → 七殺
 *
 * 七殺 → 印 → 身
 *
 * 財 → 印
 *
 * =================================================
 *
 * 그래프만으로 확정하지 않는 것:
 *
 * - 食神/財의 실제 기반
 * - 梟奪食의 실제 작동
 * - 生財護食의 위치 적합성
 * - 棄食就殺
 * - 食神合殺
 * - 財殺 사이의 食神 위치
 * - 官殺混雜의 取清
 *
 * =================================================
 *
 * 숫자 / 점수 / 가중치 / 다수결 없음.
 */
@Component
public class EatingGodPatternStructuralSignalsCalculator {

    public EatingGodPatternStructuralSignals analyze(

            PatternInteractionAnalysis interaction,

            PatternTenGodInteractionAnalysis tenGodInteractions,

            EatingGodPatternRelationContext relationContext

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
                "EatingGodPatternRelationContext는 필수입니다."
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
                                                "食神格 StructuralSignals를 만들려면 "
                                                        +
                                                        "선택된 일반격 후보가 필요합니다."
                                        )
                        );

        validateEatingGodPattern(
                selectedPattern
        );

        /*
         * =================================================
         * 1. 食神 기반
         * =================================================
         */

        boolean eatingGodFoundationStable =
                relationContext
                        .eatingGodFoundationStable();

        /*
         * =================================================
         * 2. 財 기반
         * =================================================
         */

        boolean wealthFoundationStable =
                relationContext
                        .wealthFoundationStable();

        /*
         * =================================================
         * 3. 梟神奪食
         * =================================================
         *
         * 월령 MAIN 食神을
         * 투출 偏印이 실제 오행상 剋하는가.
         */

        List<PatternTenGodNode> suppressingIndirectResources =
                visibleIndirectResourcesControllingSelectedEatingGod(
                        tenGodInteractions,
                        selectedPattern
                );

        boolean rawIndirectResourceSuppressesEatingGod =
                !suppressingIndirectResources
                        .isEmpty();

        boolean indirectResourceActuallySuppressesEatingGod =
                rawIndirectResourceSuppressesEatingGod
                        &&
                        relationContext
                                .indirectResourceSuppressionActive();

        /*
         * =================================================
         * 4. 生財護食
         * =================================================
         *
         * 梟神奪食에 관여하는
         * 바로 그 偏印을
         *
         * 財
         *   ↓ 剋
         * 偏印
         *
         * 구조로 제어하는가.
         */

        boolean rawWealthControlsSuppressingIndirectResource =
                hasVisibleWealthControllingAny(
                        tenGodInteractions,
                        suppressingIndirectResources
                );

        boolean wealthProtectsEatingGodFromIndirectResource =
                indirectResourceActuallySuppressesEatingGod
                        &&
                        rawWealthControlsSuppressingIndirectResource
                        &&
                        relationContext
                                .wealthProtectionRouteUsable();

        /*
         * =================================================
         * 5. 食神制殺
         * =================================================
         *
         * 食神과 七殺이 실제 투출되어:
         *
         * 食神
         *   ↓ 剋
         * 七殺
         */

        boolean sevenKillingsControlledByEatingGod =
                tenGodInteractions
                        .hasVisibleEatingGodControlSevenKillings();

        /*
         * =================================================
         * 6. 棄食就殺而透印
         * =================================================
         *
         * 같은 印 노드를 중심으로:
         *
         * 七殺
         *   ↓ 生
         * 印
         *   ↓ 生
         * 日主
         *
         * 연결이 실제 투출 구조로 존재하는가.
         *
         * 그리고 RelationContext에서
         * 棄食就殺 사용 가능성이 확인되어야 한다.
         */

        boolean rawKillingResourceDayMasterChain =
                hasVisibleKillingResourceDayMasterChain(
                        tenGodInteractions
                );

        boolean abandoningEatingGodForKillingWithResource =
                rawKillingResourceDayMasterChain
                        &&
                        relationContext
                                .abandoningEatingGodForKillingWithResourceUsable();

        /*
         * =================================================
         * 7. 食神合殺存財
         * =================================================
         *
         * "합"은 raw 오행 생극 그래프가 아니라
         * 실제 StemRelation이 필요하다.
         */

        boolean eatingGodCombinesKillingPreservingWealth =
                relationContext
                        .eatingGodCombinesKillingPreservingWealth();

        /*
         * =================================================
         * 8. 生財露殺
         * =================================================
         *
         * 투출 財
         *   ↓ 生
         * 투출 七殺
         *
         * raw link가 존재하는가.
         *
         * 食神格 Rule에서는 이 신호와
         * 食神의 제살/분리 신호를 함께 본다.
         */

        boolean wealthStrengthensSevenKillings =
                hasVisibleWealthGenerateSevenKillings(
                        tenGodInteractions
                );

        /*
         * =================================================
         * 9. 財와殺 사이를 食神이 실제 조절
         * =================================================
         *
         * raw 생극만으로 위치를 판정할 수 없으므로
         * RelationContext에서 받는다.
         *
         * 단, 食神·財·殺의 raw 연결 자체가
         * 존재하는 경우에만 true로 올린다.
         */

        boolean rawFoodWealthKillingContext =
                hasVisibleEatingGodGenerateWealth(
                        tenGodInteractions
                )
                        &&
                        wealthStrengthensSevenKillings;

        boolean eatingGodSeparatesWealthAndKilling =
                rawFoodWealthKillingContext
                        &&
                        relationContext
                                .eatingGodSeparatesWealthAndKilling();

        /*
         * =================================================
         * 10. 食神帶煞印而又逢財
         * =================================================
         *
         * 같은 印 노드에:
         *
         * 七殺 → 印 → 身
         *
         * 이 있고
         *
         * 財 → 그 印
         *
         * 의 극 관계까지 존재하는가.
         *
         * 실제 파괴 여부는 RelationContext가 결정한다.
         */

        boolean rawWealthDamagesKillingResourceRoute =
                hasVisibleWealthDamagingKillingResourceChain(
                        tenGodInteractions
                );

        boolean wealthDamagesKillingResourceRoute =
                rawWealthDamagesKillingResourceRoute
                        &&
                        relationContext
                                .wealthDamageKillingResourceRouteActive();

        /*
         * =================================================
         * 11. 官殺競出
         * =================================================
         *
         * 실제 사용 가능 여부는
         * 단순 생극 그래프로 결정하지 않는다.
         */

        boolean officerKillingsMixtureUsable =
                interaction
                        .directOfficerExposed()

                        &&

                        interaction
                                .sevenKillingsExposed()

                        &&

                        relationContext
                                .officerKillingsMixtureUsable();

        return new EatingGodPatternStructuralSignals(

                eatingGodFoundationStable,

                wealthFoundationStable,

                indirectResourceActuallySuppressesEatingGod,

                wealthProtectsEatingGodFromIndirectResource,

                sevenKillingsControlledByEatingGod,

                abandoningEatingGodForKillingWithResource,

                eatingGodCombinesKillingPreservingWealth,

                wealthStrengthensSevenKillings,

                eatingGodSeparatesWealthAndKilling,

                wealthDamagesKillingResourceRoute,

                officerKillingsMixtureUsable
        );
    }

    /**
     * 월령 MAIN 食神을
     * 실제 투출 偏印이 剋하는지 확인한다.
     *
     * 반환값은 그 偏印 노드들.
     */
    private List<PatternTenGodNode>
    visibleIndirectResourcesControllingSelectedEatingGod(

            PatternTenGodInteractionAnalysis analysis,

            MonthCommandPatternCandidate selectedPattern

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
                                                        .INDIRECT_RESOURCE
                                        )
                )
                .filter(
                        link ->
                                matchesSelectedEatingGod(
                                        link.target(),
                                        selectedPattern
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
     * 현재 월령 食神格의
     * MAIN 지장간 노드인가.
     */
    private boolean matchesSelectedEatingGod(

            PatternTenGodNode node,

            MonthCommandPatternCandidate selectedPattern

    ) {

        if (!node.hidden()) {

            return false;
        }

        if (
                node.position()
                        != PillarPosition.MONTH
        ) {

            return false;
        }

        if (
                node.stem()
                        != selectedPattern.hiddenStem()
        ) {

            return false;
        }

        HiddenStemPhase phase =
                node.hiddenStemPhase()
                        .orElse(null);

        return phase
                == selectedPattern.phase();
    }

    /**
     * 梟奪食에 관여한 偏印 중 하나를
     * 투출 財가 실제 剋하는가.
     *
     * 財 → 梟.
     */
    private boolean hasVisibleWealthControllingAny(

            PatternTenGodInteractionAnalysis analysis,

            List<PatternTenGodNode> indirectResources

    ) {

        if (
                indirectResources == null
                        ||
                        indirectResources.isEmpty()
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

                                        indirectResources
                                                .contains(
                                                        link.target()
                                                )
                );
    }

    /**
     * 七殺 → 印 → 日主.
     *
     * 七殺과 印 모두 투출을 요구한다.
     *
     * "透印" Context이므로
     * 중간 印 노드는 visible이어야 한다.
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
     * 投出 財 → 投出 七殺.
     */
    private boolean hasVisibleWealthGenerateSevenKillings(
            PatternTenGodInteractionAnalysis analysis
    ) {

        return analysis
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

                                        isWealth(
                                                link.source()
                                        )

                                        &&

                                        link.target()
                                                .visible()

                                        &&

                                        link.target()
                                                .hasTenGodType(
                                                        MonthCommandPatternCandidateType
                                                                .SEVEN_KILLINGS
                                                )
                );
    }

    /**
     * 投出 食神 → 財.
     *
     * 食神은 반드시 투출.
     *
     * 財 역시 食神格의 生財 route에서는
     * 천간에 실제 드러난 것을 우선 확인한다.
     */
    private boolean hasVisibleEatingGodGenerateWealth(
            PatternTenGodInteractionAnalysis analysis
    ) {

        return analysis
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
                                                                .EATING_GOD
                                                )

                                        &&

                                        link.target()
                                                .visible()

                                        &&

                                        isWealth(
                                                link.target()
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
     * 가 성립하고,
     *
     * 투출 財
     *   ↓ 剋
     * 같은 印
     *
     * 까지 존재하는가.
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

    private void validateEatingGodPattern(
            MonthCommandPatternCandidate selectedPattern
    ) {

        if (
                selectedPattern
                        .candidateType()
                        != MonthCommandPatternCandidateType
                        .EATING_GOD
        ) {

            throw new IllegalArgumentException(
                    "EatingGodPatternStructuralSignalsCalculator에는 "
                            +
                            "EATING_GOD 격국 후보만 사용할 수 있습니다."
            );
        }
    }
}