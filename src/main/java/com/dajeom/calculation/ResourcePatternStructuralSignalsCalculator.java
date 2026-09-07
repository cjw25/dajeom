package com.dajeom.calculation;

import com.dajeom.domain.DayMasterStrengthAnalysis;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.HiddenStemPhase;
import com.dajeom.domain.MonthCommandPatternCandidate;
import com.dajeom.domain.MonthCommandPatternCandidateType;
import com.dajeom.domain.PatternInteractionAnalysis;
import com.dajeom.domain.PatternTenGodInteractionAnalysis;
import com.dajeom.domain.PatternTenGodLink;
import com.dajeom.domain.PatternTenGodLinkType;
import com.dajeom.domain.PatternTenGodNode;
import com.dajeom.domain.ResourcePatternRelationContext;
import com.dajeom.domain.ResourcePatternStructuralSignals;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 印格에 필요한 ResourcePatternStructuralSignals를
 * 원국 십성 생극 그래프에서 자동 생성한다.
 *
 * =================================================
 *
 * 자동 확인:
 *
 * 正官 → 印 → 日主
 *
 * 七殺 → 印 → 日主
 *
 * 食傷 → 七殺
 *
 * 財 → 印
 *
 * 劫財 → 財
 *
 * 偏印 → 食神
 *
 * 日主 → 食傷
 *
 * =================================================
 *
 * raw 연결만으로 확정하지 않는 것:
 *
 * - 官印雙全 최종 성립
 * - 殺印相生 최종 성립
 * - 印重
 * - 財破印
 * - 印多用財
 * - 梟奪食
 * - 과도한 食傷 설기
 *
 * 이런 구조는 RelationContext까지 함께 본다.
 *
 * =================================================
 *
 * 숫자 / 점수 / 가중치 / 다수결 없음.
 */
@Component
public class ResourcePatternStructuralSignalsCalculator {

    public ResourcePatternStructuralSignals analyze(

            PatternInteractionAnalysis interaction,

            PatternTenGodInteractionAnalysis tenGodInteractions,

            DayMasterStrengthAnalysis strength,

            ResourcePatternRelationContext relationContext

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
                strength,
                "일간 왕쇠 분석은 필수입니다."
        );

        Objects.requireNonNull(
                relationContext,
                "ResourcePatternRelationContext는 필수입니다."
        );

        validate(
                interaction,
                tenGodInteractions,
                strength
        );

        MonthCommandPatternCandidate selectedPattern =
                interaction
                        .provisionalStandardCandidate()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "印格 StructuralSignals를 만들려면 "
                                                        +
                                                        "선택된 일반격 후보가 필요합니다."
                                        )
                        );

        validateResourcePattern(
                selectedPattern
        );

        /*
         * =================================================
         * 1. 印의 기반
         * =================================================
         */

        boolean resourceFoundationStable =
                relationContext
                        .resourceFoundationStable();

        /*
         * =================================================
         * 2. 官 → 印 → 身
         * =================================================
         *
         * 같은 印 노드를 중심으로:
         *
         * 正官
         *   ↓ 生
         * 印
         *   ↓ 生
         * 日主
         *
         * 연결이 있어야 한다.
         */

        boolean rawOfficerResourceChain =
                hasVisibleOfficerResourceDayMasterChain(
                        tenGodInteractions
                );

        boolean officerResourceChainIntact =
                rawOfficerResourceChain
                        &&
                        resourceFoundationStable
                        &&
                        relationContext
                                .officerResourceChainUsable();

        /*
         * =================================================
         * 3. 七殺 → 印 → 身
         * =================================================
         */

        boolean rawKillingResourceChain =
                hasVisibleKillingResourceDayMasterChain(
                        tenGodInteractions
                );

        boolean sevenKillingsAppropriatelySupportsResource =
                rawKillingResourceChain
                        &&
                        resourceFoundationStable
                        &&
                        relationContext
                                .killingResourceChainUsable();

        /*
         * =================================================
         * 4. 身强印重而透煞
         * =================================================
         *
         * strongSide 하나만으로
         * 印重을 만들지 않는다.
         */

        boolean strongSide =
                strength.clearlyStrong()
                        ||
                        strength.strongLeaning();

        boolean strongResourceWithExcessKilling =
                strongSide
                        &&
                        interaction.sevenKillingsExposed()
                        &&
                        rawKillingResourceChain
                        &&
                        relationContext
                                .excessiveResourceContext();

        /*
         * =================================================
         * 5. 食傷制殺
         * =================================================
         *
         * 食神 또는 傷官이 투출하여
         * 七殺을 실제 오행상 剋하는가.
         */

        boolean sevenKillingsControlledByOutput =
                hasVisibleOutputControlSevenKillings(
                        tenGodInteractions
                );

        /*
         * =================================================
         * 6. 官殺混雜의 정리
         * =================================================
         */

        boolean mixedSevenKillingsClearedByCombination =
                relationContext
                        .mixedSevenKillingsClearedByCombination();

        boolean mixedOfficerKillingsControlled =
                relationContext
                        .mixedOfficerKillingsControlled();

        /*
         * =================================================
         * 7. 財 → 印
         * =================================================
         *
         * 먼저 raw 극 관계를 찾는다.
         *
         * 가능하면 월령 MAIN 印 자체가
         * 財에게 극을 받는지를 우선 본다.
         */

        List<PatternTenGodNode> damagingWealthNodes =
                visibleWealthControllingSelectedResource(
                        tenGodInteractions,
                        selectedPattern
                );

        boolean rawWealthControlsResource =
                !damagingWealthNodes
                        .isEmpty();

        /*
         * =================================================
         * 8. 印多用財
         * =================================================
         *
         * 같은 財 → 印 관계라도
         *
         * 財破印
         *
         * 과
         *
         * 印多用財
         *
         * 는 다른 명리 구조다.
         */

        boolean wealthModeratesExcessResource =
                strongSide
                        &&
                        rawWealthControlsResource
                        &&
                        relationContext
                                .excessiveResourceContext()
                        &&
                        relationContext
                                .wealthModeratesExcessResource();

        /*
         * =================================================
         * 9. 財破印
         * =================================================
         */

        boolean wealthActuallyDamagesResource =
                rawWealthControlsResource
                        &&
                        relationContext
                                .wealthResourceConflictActive()
                        &&
                        !wealthModeratesExcessResource;

        /*
         * =================================================
         * 10. 劫財救印
         * =================================================
         *
         * 실제 印을 공격하는 바로 그 財를
         * 劫財가 제어하는 raw Link가 있는가.
         */

        boolean wealthDamageResolvedByRobWealth =
                wealthActuallyDamagesResource
                        &&
                        hasVisibleRobWealthControllingAny(
                                tenGodInteractions,
                                damagingWealthNodes
                        );

        /*
         * =================================================
         * 11. 合財存印
         * =================================================
         */

        boolean wealthCombinedAwayPreservingResource =
                wealthActuallyDamagesResource
                        &&
                        relationContext
                                .wealthCombinedAwayPreservingResource();

        /*
         * =================================================
         * 12. 梟神奪食
         * =================================================
         *
         * 偏印
         *   ↓ 剋
         * 食神
         *
         * raw 연결
         *
         * +
         *
         * 실제 배치상 梟奪食 작동 여부
         */

        boolean rawIndirectResourceControlsEatingGod =
                hasVisibleIndirectResourceControlEatingGod(
                        tenGodInteractions
                );

        boolean indirectResourceActuallySuppressesEatingGod =
                rawIndirectResourceControlsEatingGod
                        &&
                        relationContext
                                .indirectResourceSuppressionActive();

        /*
         * =================================================
         * 13. 신약 상태의 과도한 食傷 설기
         * =================================================
         *
         * 日主
         *   ↓ 生
         * 食神/傷官
         *
         * raw 연결이 있으며
         *
         * 실제 신약 + 과다설기가
         * RelationContext에서 확인된 경우.
         */

        boolean weakSide =
                strength.clearlyWeak()
                        ||
                        strength.weakLeaning();

        boolean rawDayMasterGeneratesVisibleOutput =
                hasDayMasterGenerateVisibleOutput(
                        tenGodInteractions
                );

        boolean outputExcessivelyDrainsWeakStructure =
                weakSide
                        &&
                        rawDayMasterGeneratesVisibleOutput
                        &&
                        relationContext
                                .outputDrainWeakStructureActive();

        return new ResourcePatternStructuralSignals(

                resourceFoundationStable,

                officerResourceChainIntact,

                sevenKillingsAppropriatelySupportsResource,

                strongResourceWithExcessKilling,

                sevenKillingsControlledByOutput,

                mixedSevenKillingsClearedByCombination,

                mixedOfficerKillingsControlled,

                wealthModeratesExcessResource,

                wealthActuallyDamagesResource,

                wealthDamageResolvedByRobWealth,

                wealthCombinedAwayPreservingResource,

                indirectResourceActuallySuppressesEatingGod,

                outputExcessivelyDrainsWeakStructure
        );
    }

    /**
     * 正官 → 印 → 日主.
     *
     * 반드시 동일한 印 노드가
     * 두 Link의 중간 노드여야 한다.
     */
    private boolean hasVisibleOfficerResourceDayMasterChain(
            PatternTenGodInteractionAnalysis analysis
    ) {

        for (
                PatternTenGodNode resource
                : resourceNodes(
                analysis
        )
        ) {

            boolean officerGeneratesResource =
                    analysis
                            .links()
                            .stream()
                            .anyMatch(
                                    link ->
                                            link.type()
                                                    == PatternTenGodLinkType.GENERATES

                                                    &&

                                                    link.source().visible()

                                                    &&

                                                    link.source()
                                                            .hasTenGodType(
                                                                    MonthCommandPatternCandidateType
                                                                            .DIRECT_OFFICER
                                                            )

                                                    &&

                                                    link.target()
                                                            .equals(
                                                                    resource
                                                            )
                            );

            if (!officerGeneratesResource) {

                continue;
            }

            if (
                    resourceGeneratesDayMaster(
                            analysis,
                            resource
                    )
            ) {

                return true;
            }
        }

        return false;
    }

    /**
     * 七殺 → 印 → 日主.
     */
    private boolean hasVisibleKillingResourceDayMasterChain(
            PatternTenGodInteractionAnalysis analysis
    ) {

        for (
                PatternTenGodNode resource
                : resourceNodes(
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

                                                    link.source().visible()

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

            if (
                    resourceGeneratesDayMaster(
                            analysis,
                            resource
                    )
            ) {

                return true;
            }
        }

        return false;
    }

    /**
     * 해당 印 노드가
     * 日主를 실제로 生하는 raw Link.
     */
    private boolean resourceGeneratesDayMaster(

            PatternTenGodInteractionAnalysis analysis,

            PatternTenGodNode resource

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
                                                .equals(
                                                        resource
                                                )

                                        &&

                                        link.target()
                                                .dayMaster()
                );
    }

    /**
     * 食神/傷官 → 七殺.
     */
    private boolean hasVisibleOutputControlSevenKillings(
            PatternTenGodInteractionAnalysis analysis
    ) {

        return analysis
                .links()
                .stream()
                .anyMatch(
                        link ->
                                link.type()
                                        == PatternTenGodLinkType.CONTROLS

                                        &&

                                        link.source().visible()

                                        &&

                                        isOutput(
                                                link.source()
                                        )

                                        &&

                                        link.target().visible()

                                        &&

                                        link.target()
                                                .hasTenGodType(
                                                        MonthCommandPatternCandidateType
                                                                .SEVEN_KILLINGS
                                                )
                );
    }

    /**
     * 財 → 월령 MAIN 印.
     *
     * 財는 실제 투출된 천간이어야 한다.
     */
    private List<PatternTenGodNode>
    visibleWealthControllingSelectedResource(

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
                                link.source().visible()
                )
                .filter(
                        link ->
                                isWealth(
                                        link.source()
                                )
                )
                .filter(
                        link ->
                                matchesSelectedResource(
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
     * 현재 월령 印격 후보와
     * 그래프의 hidden resource node가
     * 같은 지장간인지 확인.
     */
    private boolean matchesSelectedResource(

            PatternTenGodNode node,

            MonthCommandPatternCandidate selectedPattern

    ) {

        if (!node.hidden()) {

            return false;
        }

        if (
                node.position()
                        != com.dajeom.domain.PillarPosition.MONTH
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
     * 劫財 → 財.
     *
     * 印을 공격하는 財들 중 하나를
     * 투출 劫財가 실제 剋하는가.
     */
    private boolean hasVisibleRobWealthControllingAny(

            PatternTenGodInteractionAnalysis analysis,

            List<PatternTenGodNode> wealthNodes

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
                                        == PatternTenGodLinkType.CONTROLS

                                        &&

                                        link.source().visible()

                                        &&

                                        link.source()
                                                .hasTenGodType(
                                                        MonthCommandPatternCandidateType
                                                                .MONTH_ROB_WEALTH
                                                )

                                        &&

                                        wealthNodes.contains(
                                                link.target()
                                        )
                );
    }

    /**
     * 偏印 → 食神.
     */
    private boolean hasVisibleIndirectResourceControlEatingGod(
            PatternTenGodInteractionAnalysis analysis
    ) {

        return analysis
                .links()
                .stream()
                .anyMatch(
                        link ->
                                link.type()
                                        == PatternTenGodLinkType.CONTROLS

                                        &&

                                        link.source().visible()

                                        &&

                                        link.source()
                                                .hasTenGodType(
                                                        MonthCommandPatternCandidateType
                                                                .INDIRECT_RESOURCE
                                                )

                                        &&

                                        link.target()
                                                .hasTenGodType(
                                                        MonthCommandPatternCandidateType
                                                                .EATING_GOD
                                                )
                );
    }

    /**
     * 日主 → 투출 食神/傷官.
     *
     * 오행상 日主가 실제 食傷을 生하는
     * raw 설기 구조.
     */
    private boolean hasDayMasterGenerateVisibleOutput(
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
                                                .dayMaster()

                                        &&

                                        link.target().visible()

                                        &&

                                        isOutput(
                                                link.target()
                                        )
                );
    }

    private List<PatternTenGodNode> resourceNodes(
            PatternTenGodInteractionAnalysis analysis
    ) {

        return analysis
                .nodes()
                .stream()
                .filter(
                        this::isResource
                )
                .toList();
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

    private boolean isOutput(
            PatternTenGodNode node
    ) {

        return node.hasTenGodType(
                MonthCommandPatternCandidateType
                        .EATING_GOD
        )
                ||
                node.hasTenGodType(
                        MonthCommandPatternCandidateType
                                .HURTING_OFFICER
                );
    }

    private void validate(

            PatternInteractionAnalysis interaction,

            PatternTenGodInteractionAnalysis tenGodInteractions,

            DayMasterStrengthAnalysis strength

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

        if (
                strength.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "PatternInteractionAnalysis와 왕쇠 분석의 일간이 일치하지 않습니다."
            );
        }
    }

    private void validateResourcePattern(
            MonthCommandPatternCandidate selectedPattern
    ) {

        MonthCommandPatternCandidateType type =
                selectedPattern
                        .candidateType();

        if (
                type
                        != MonthCommandPatternCandidateType
                        .DIRECT_RESOURCE

                        &&

                        type
                                != MonthCommandPatternCandidateType
                                .INDIRECT_RESOURCE
        ) {

            throw new IllegalArgumentException(
                    "ResourcePatternStructuralSignalsCalculator에는 "
                            +
                            "正印/偏印 격국 후보만 사용할 수 있습니다."
            );
        }
    }
}