package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 원국 전체 십성 생극 연결 분석.
 *
 * =================================================
 *
 * 노드:
 *
 * - 일간
 * - 투출 천간
 * - 모든 지장간
 *
 * =================================================
 *
 * Link:
 *
 * - 生
 * - 剋
 *
 * 방향성을 그대로 보존.
 *
 * =================================================
 *
 * 위치와 투출/암장을 잃지 않기 때문에
 *
 * 이후:
 *
 * - 食神制殺
 * - 傷官生財
 * - 財生官
 * - 財滋殺
 * - 殺印相生
 * - 財殺印身
 * - 印制食傷
 * - 比劫奪財
 *
 * 등을 판정하는 공통 기반으로 사용한다.
 *
 * =================================================
 *
 * 아직 成格/破格 판단은 하지 않는다.
 */
public record PatternTenGodInteractionAnalysis(

        HeavenlyStem dayMaster,

        List<PatternTenGodNode> nodes,

        List<PatternTenGodLink> links

) {

    public PatternTenGodInteractionAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        nodes =
                nodes == null
                        ? List.of()
                        : List.copyOf(
                        nodes
                );

        links =
                links == null
                        ? List.of()
                        : List.copyOf(
                        links
                );

        long dayMasterNodeCount =
                nodes
                        .stream()
                        .filter(
                                PatternTenGodNode
                                        ::dayMaster
                        )
                        .count();

        if (
                dayMasterNodeCount != 1
        ) {

            throw new IllegalArgumentException(
                    "십성 Interaction 그래프에는 정확히 하나의 일간 노드가 필요합니다."
            );
        }

        PatternTenGodNode dmNode =
                nodes
                        .stream()
                        .filter(
                                PatternTenGodNode
                                        ::dayMaster
                        )
                        .findFirst()
                        .orElseThrow();

        if (
                dmNode.stem()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "그래프 일간 노드와 분석 대상 일간이 일치하지 않습니다."
            );
        }

        for (
                PatternTenGodNode node
                : nodes
        ) {

            Objects.requireNonNull(
                    node,
                    "십성 노드 목록에 null이 포함될 수 없습니다."
            );
        }

        for (
                PatternTenGodLink link
                : links
        ) {

            Objects.requireNonNull(
                    link,
                    "십성 Link 목록에 null이 포함될 수 없습니다."
            );

            if (
                    !nodes.contains(
                            link.source()
                    )
                            ||
                            !nodes.contains(
                                    link.target()
                            )
            ) {

                throw new IllegalArgumentException(
                        "Link가 그래프에 존재하지 않는 노드를 참조하고 있습니다."
                );
            }
        }
    }

    public PatternTenGodNode dayMasterNode() {

        return nodes
                .stream()
                .filter(
                        PatternTenGodNode
                                ::dayMaster
                )
                .findFirst()
                .orElseThrow();
    }

    /*
     * =================================================
     * 노드 조회
     * =================================================
     */

    public List<PatternTenGodNode> nodesOf(
            MonthCommandPatternCandidateType type
    ) {

        Objects.requireNonNull(
                type,
                "십성 타입은 필수입니다."
        );

        return nodes
                .stream()
                .filter(
                        node ->
                                node.hasTenGodType(
                                        type
                                )
                )
                .toList();
    }

    public List<PatternTenGodNode> visibleNodesOf(
            MonthCommandPatternCandidateType type
    ) {

        return nodesOf(
                type
        )
                .stream()
                .filter(
                        PatternTenGodNode
                                ::visible
                )
                .toList();
    }

    public List<PatternTenGodNode> hiddenNodesOf(
            MonthCommandPatternCandidateType type
    ) {

        return nodesOf(
                type
        )
                .stream()
                .filter(
                        PatternTenGodNode
                                ::hidden
                )
                .toList();
    }

    /*
     * =================================================
     * Generic Link 조회
     * =================================================
     */

    public List<PatternTenGodLink> links(

            MonthCommandPatternCandidateType sourceType,

            MonthCommandPatternCandidateType targetType,

            PatternTenGodLinkType linkType

    ) {

        Objects.requireNonNull(
                sourceType,
                "출발 십성 타입은 필수입니다."
        );

        Objects.requireNonNull(
                targetType,
                "도착 십성 타입은 필수입니다."
        );

        Objects.requireNonNull(
                linkType,
                "Link 타입은 필수입니다."
        );

        return links
                .stream()
                .filter(
                        link ->
                                link.matches(
                                        sourceType,
                                        targetType,
                                        linkType
                                )
                )
                .toList();
    }

    public List<PatternTenGodLink> visibleLinks(

            MonthCommandPatternCandidateType sourceType,

            MonthCommandPatternCandidateType targetType,

            PatternTenGodLinkType linkType

    ) {

        return links(
                sourceType,
                targetType,
                linkType
        )
                .stream()
                .filter(
                        PatternTenGodLink
                                ::bothVisible
                )
                .toList();
    }

    public boolean hasVisibleLink(

            MonthCommandPatternCandidateType sourceType,

            MonthCommandPatternCandidateType targetType,

            PatternTenGodLinkType linkType

    ) {

        return !visibleLinks(
                sourceType,
                targetType,
                linkType
        ).isEmpty();
    }

    /*
     * =================================================
     * 격국 Rule에서 자주 사용하는
     * 원시 구조 연결
     * =================================================
     */

    /**
     * 傷官 → 財.
     */
    public boolean hasVisibleHurtingOfficerToWealthGeneration() {

        return hasVisibleGenerationToWealth(
                MonthCommandPatternCandidateType
                        .HURTING_OFFICER
        );
    }

    /**
     * 食神 → 財.
     */
    public boolean hasVisibleEatingGodToWealthGeneration() {

        return hasVisibleGenerationToWealth(
                MonthCommandPatternCandidateType
                        .EATING_GOD
        );
    }

    /**
     * 財 → 正官.
     */
    public boolean hasVisibleWealthToDirectOfficerGeneration() {

        return hasVisibleWealthGenerationTo(
                MonthCommandPatternCandidateType
                        .DIRECT_OFFICER
        );
    }

    /**
     * 財 → 七殺.
     */
    public boolean hasVisibleWealthToSevenKillingsGeneration() {

        return hasVisibleWealthGenerationTo(
                MonthCommandPatternCandidateType
                        .SEVEN_KILLINGS
        );
    }

    /**
     * 食神 → 七殺.
     */
    public boolean hasVisibleEatingGodControlSevenKillings() {

        return hasVisibleLink(
                MonthCommandPatternCandidateType
                        .EATING_GOD,
                MonthCommandPatternCandidateType
                        .SEVEN_KILLINGS,
                PatternTenGodLinkType
                        .CONTROLS
        );
    }

    /**
     * 印 → 食神.
     */
    public boolean hasVisibleResourceControlEatingGod() {

        return hasVisibleResourceControl(
                MonthCommandPatternCandidateType
                        .EATING_GOD
        );
    }

    /**
     * 印 → 傷官.
     */
    public boolean hasVisibleResourceControlHurtingOfficer() {

        return hasVisibleResourceControl(
                MonthCommandPatternCandidateType
                        .HURTING_OFFICER
        );
    }

    /**
     * 七殺 → 印.
     */
    public boolean hasVisibleSevenKillingsToResourceGeneration() {

        return hasVisibleGenerationFromSevenKillingsToResource();
    }

    /**
     * 印 → 日主.
     *
     * 십성 정의상 印은 일간을 생하지만,
     * 실제 어느 투출 印이 일간과 연결되는지도
     * 위치를 가진 Link로 확인할 수 있다.
     */
    public boolean hasVisibleResourceToDayMasterGeneration() {

        return links
                .stream()
                .anyMatch(
                        link ->
                                link.type()
                                        == PatternTenGodLinkType.GENERATES

                                        &&

                                        link.source().visible()

                                        &&

                                        isResource(
                                                link.source()
                                                        .tenGodType()
                                                        .orElse(null)
                                        )

                                        &&

                                        link.targetDayMaster()
                );
    }

    /**
     * 比肩/劫財 → 財.
     *
     * 오행상 剋 연결이 있는가.
     *
     * 이것 자체가 奪財 확정은 아니다.
     */
    public boolean hasVisibleCompanionControlWealth() {

        return links
                .stream()
                .anyMatch(
                        link ->
                                link.type()
                                        == PatternTenGodLinkType.CONTROLS

                                        &&

                                        link.bothVisible()

                                        &&

                                        isCompanion(
                                                link.source()
                                                        .tenGodType()
                                                        .orElse(null)
                                        )

                                        &&

                                        isWealth(
                                                link.target()
                                                        .tenGodType()
                                                        .orElse(null)
                                        )
                );
    }

    /*
     * =================================================
     * raw chain 확인
     *
     * 아직 成格 확정이 아니다.
     * =================================================
     */

    /**
     * 財 → 七殺 → 印 → 身
     *
     * 의 투출 오행 연결이 존재하는가.
     */
    public boolean hasVisibleWealthKillingResourceDayMasterChain() {

        boolean wealthToKilling =
                hasVisibleWealthToSevenKillingsGeneration();

        boolean killingToResource =
                hasVisibleSevenKillingsToResourceGeneration();

        boolean resourceToDayMaster =
                hasVisibleResourceToDayMasterGeneration();

        return wealthToKilling
                &&
                killingToResource
                &&
                resourceToDayMaster;
    }

    /**
     * 傷官 → 財 → 正官
     *
     * 의 투출 연결이 존재하는가.
     */
    public boolean hasVisibleHurtingOfficerWealthOfficerChain() {

        return hasVisibleHurtingOfficerToWealthGeneration()
                &&
                hasVisibleWealthToDirectOfficerGeneration();
    }

    private boolean hasVisibleGenerationToWealth(
            MonthCommandPatternCandidateType sourceType
    ) {

        return hasVisibleLink(
                sourceType,
                MonthCommandPatternCandidateType
                        .DIRECT_WEALTH,
                PatternTenGodLinkType.GENERATES
        )
                ||
                hasVisibleLink(
                        sourceType,
                        MonthCommandPatternCandidateType
                                .INDIRECT_WEALTH,
                        PatternTenGodLinkType.GENERATES
                );
    }

    private boolean hasVisibleWealthGenerationTo(
            MonthCommandPatternCandidateType targetType
    ) {

        return hasVisibleLink(
                MonthCommandPatternCandidateType
                        .DIRECT_WEALTH,
                targetType,
                PatternTenGodLinkType.GENERATES
        )
                ||
                hasVisibleLink(
                        MonthCommandPatternCandidateType
                                .INDIRECT_WEALTH,
                        targetType,
                        PatternTenGodLinkType.GENERATES
                );
    }

    private boolean hasVisibleGenerationFromSevenKillingsToResource() {

        return hasVisibleLink(
                MonthCommandPatternCandidateType
                        .SEVEN_KILLINGS,
                MonthCommandPatternCandidateType
                        .DIRECT_RESOURCE,
                PatternTenGodLinkType.GENERATES
        )
                ||
                hasVisibleLink(
                        MonthCommandPatternCandidateType
                                .SEVEN_KILLINGS,
                        MonthCommandPatternCandidateType
                                .INDIRECT_RESOURCE,
                        PatternTenGodLinkType.GENERATES
                );
    }

    private boolean hasVisibleResourceControl(
            MonthCommandPatternCandidateType targetType
    ) {

        return hasVisibleLink(
                MonthCommandPatternCandidateType
                        .DIRECT_RESOURCE,
                targetType,
                PatternTenGodLinkType.CONTROLS
        )
                ||
                hasVisibleLink(
                        MonthCommandPatternCandidateType
                                .INDIRECT_RESOURCE,
                        targetType,
                        PatternTenGodLinkType.CONTROLS
                );
    }

    private boolean isWealth(
            MonthCommandPatternCandidateType type
    ) {

        return type
                == MonthCommandPatternCandidateType.DIRECT_WEALTH

                ||

                type
                        == MonthCommandPatternCandidateType
                        .INDIRECT_WEALTH;
    }

    private boolean isResource(
            MonthCommandPatternCandidateType type
    ) {

        return type
                == MonthCommandPatternCandidateType.DIRECT_RESOURCE

                ||

                type
                        == MonthCommandPatternCandidateType
                        .INDIRECT_RESOURCE;
    }

    private boolean isCompanion(
            MonthCommandPatternCandidateType type
    ) {

        return type
                == MonthCommandPatternCandidateType.MONTH_COMPANION

                ||

                type
                        == MonthCommandPatternCandidateType
                        .MONTH_ROB_WEALTH;
    }
}