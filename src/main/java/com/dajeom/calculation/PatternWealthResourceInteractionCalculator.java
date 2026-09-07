package com.dajeom.calculation;

import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.MonthCommandPatternCandidate;
import com.dajeom.domain.MonthCommandPatternCandidateType;
import com.dajeom.domain.PatternInteractionAnalysis;
import com.dajeom.domain.PatternTenGodInteractionAnalysis;
import com.dajeom.domain.PatternTenGodLinkType;
import com.dajeom.domain.PatternTenGodNode;
import com.dajeom.domain.PatternWealthResourceInteractionAnalysis;
import com.dajeom.domain.PatternWealthResourceRelationOccurrence;
import com.dajeom.domain.PatternWealthResourceRelationReason;
import com.dajeom.domain.PatternWealthResourceRelationState;
import com.dajeom.domain.PillarPosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 財印 관계를 단순 오행 克만으로 판단하지 않고
 * 실제 천간 위치와 官殺 통관을 함께 분석한다.
 *
 * =================================================
 *
 * 기본 판정:
 *
 * 財 ↔ 印 같은 위치/인접
 * → DIRECT_CONFLICT
 *
 * 財 → 官殺 → 印
 * 동일 chain 존재
 * → AUTHORITY_BRIDGED_NON_OBSTRUCTING
 *
 * 財와 印이 떨어져 있고
 * 직접 충돌 위치가 아님
 * → POSITIONALLY_SEPARATED_NON_OBSTRUCTING
 *
 * =================================================
 *
 * 印格에서는 별도로:
 *
 * 투출 財
 * →
 * 月令 MAIN 印
 *
 * 을 추적한다.
 *
 * =================================================
 *
 * 숫자 / 점수 / 가중치 없음.
 */
@Component
public class PatternWealthResourceInteractionCalculator {

    public PatternWealthResourceInteractionAnalysis analyze(

            PatternInteractionAnalysis interaction,

            PatternTenGodInteractionAnalysis tenGodInteractions

    ) {

        Objects.requireNonNull(
                interaction,
                "PatternInteractionAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                tenGodInteractions,
                "PatternTenGodInteractionAnalysis는 필수입니다."
        );

        validateDayMaster(
                interaction,
                tenGodInteractions
        );

        List<PatternTenGodNode> wealthNodes =
                visibleWealthNodes(
                        tenGodInteractions
                );

        List<PatternTenGodNode> visibleResourceNodes =
                visibleResourceNodes(
                        tenGodInteractions
                );

        List<PatternWealthResourceRelationOccurrence> visibleRelations =
                new ArrayList<>();

        for (
                PatternTenGodNode wealth
                : wealthNodes
        ) {

            for (
                    PatternTenGodNode resource
                    : visibleResourceNodes
            ) {

                if (
                        controls(
                                wealth,
                                resource,
                                tenGodInteractions
                        )
                ) {

                    visibleRelations.add(
                            classify(
                                    wealth,
                                    resource,
                                    tenGodInteractions
                            )
                    );
                }
            }
        }

        /*
         * =================================================
         * 印格의 月令 MAIN 印
         * =================================================
         */

        List<PatternWealthResourceRelationOccurrence>
                selectedMonthResourceRelations =
                new ArrayList<>();

        Optional<PatternTenGodNode> selectedMonthResource =
                findSelectedMonthResource(
                        interaction,
                        tenGodInteractions
                );

        if (selectedMonthResource.isPresent()) {

            PatternTenGodNode resource =
                    selectedMonthResource.get();

            for (
                    PatternTenGodNode wealth
                    : wealthNodes
            ) {

                if (
                        controls(
                                wealth,
                                resource,
                                tenGodInteractions
                        )
                ) {

                    selectedMonthResourceRelations.add(
                            classify(
                                    wealth,
                                    resource,
                                    tenGodInteractions
                            )
                    );
                }
            }
        }

        return new PatternWealthResourceInteractionAnalysis(

                interaction.dayMaster(),

                visibleRelations,

                selectedMonthResourceRelations
        );
    }

    /*
     * =================================================
     * 관계 분류
     * =================================================
     */

    private PatternWealthResourceRelationOccurrence classify(

            PatternTenGodNode wealth,

            PatternTenGodNode resource,

            PatternTenGodInteractionAnalysis interactions

    ) {

        EnumSet<PatternWealthResourceRelationReason> reasons =
                EnumSet.noneOf(
                        PatternWealthResourceRelationReason.class
                );

        reasons.add(
                PatternWealthResourceRelationReason
                        .WEALTH_CONTROLS_RESOURCE
        );

        /*
         * =================================================
         * 1. 財 → 官殺 → 印 통관부터 확인
         * =================================================
         *
         * 직접 克 관계가 있어도
         * 위치상 실제 통관 노드가 사이를 잇고 있으면
         * 財印不相礙 후보로 본다.
         */

        Optional<PatternTenGodNode> authorityBridge =
                findAuthorityBridge(
                        wealth,
                        resource,
                        interactions
                );

        if (authorityBridge.isPresent()) {

            reasons.add(
                    PatternWealthResourceRelationReason
                            .AUTHORITY_BRIDGE_EXISTS
            );

            reasons.add(
                    PatternWealthResourceRelationReason
                            .WEALTH_GENERATES_AUTHORITY
            );

            reasons.add(
                    PatternWealthResourceRelationReason
                            .AUTHORITY_GENERATES_RESOURCE
            );

            reasons.add(
                    PatternWealthResourceRelationReason
                            .WEALTH_RESOURCE_MUTUALLY_NON_OBSTRUCTING
            );

            return new PatternWealthResourceRelationOccurrence(

                    wealth,

                    resource,

                    authorityBridge,

                    PatternWealthResourceRelationState
                            .AUTHORITY_BRIDGED_NON_OBSTRUCTING,

                    reasons
            );
        }

        /*
         * =================================================
         * 2. 위치 거리
         * =================================================
         */

        int distance =
                positionDistance(
                        wealth.position(),
                        resource.position()
                );

        /*
         * 같은 pillar.
         *
         * 실제 가능한 대표 예:
         *
         * 月干 財
         * 月支 MAIN 印
         */
        if (distance == 0) {

            reasons.add(
                    PatternWealthResourceRelationReason
                            .SAME_PILLAR_RELATION
            );

            reasons.add(
                    PatternWealthResourceRelationReason
                            .DIRECT_WEALTH_RESOURCE_CONFLICT
            );

            return new PatternWealthResourceRelationOccurrence(

                    wealth,

                    resource,

                    Optional.empty(),

                    PatternWealthResourceRelationState
                            .DIRECT_CONFLICT,

                    reasons
            );
        }

        /*
         * 서로 바로 붙은 기둥.
         *
         * 年 ↔ 月
         * 月 ↔ 日
         * 日 ↔ 時
         */
        if (distance == 1) {

            reasons.add(
                    PatternWealthResourceRelationReason
                            .ADJACENT_POSITIONS
            );

            reasons.add(
                    PatternWealthResourceRelationReason
                            .DIRECT_WEALTH_RESOURCE_CONFLICT
            );

            return new PatternWealthResourceRelationOccurrence(

                    wealth,

                    resource,

                    Optional.empty(),

                    PatternWealthResourceRelationState
                            .DIRECT_CONFLICT,

                    reasons
            );
        }

        /*
         * =================================================
         * 3. 서로 떨어져 있음
         * =================================================
         */

        reasons.add(
                PatternWealthResourceRelationReason
                        .DISTANT_POSITIONS
        );

        reasons.add(
                PatternWealthResourceRelationReason
                        .WEALTH_RESOURCE_POSITIONALLY_SEPARATED
        );

        reasons.add(
                PatternWealthResourceRelationReason
                        .WEALTH_RESOURCE_MUTUALLY_NON_OBSTRUCTING
        );

        return new PatternWealthResourceRelationOccurrence(

                wealth,

                resource,

                Optional.empty(),

                PatternWealthResourceRelationState
                        .POSITIONALLY_SEPARATED_NON_OBSTRUCTING,

                reasons
        );
    }

    /*
     * =================================================
     * 財 → 官殺 → 印
     * =================================================
     */

    private Optional<PatternTenGodNode> findAuthorityBridge(

            PatternTenGodNode wealth,

            PatternTenGodNode resource,

            PatternTenGodInteractionAnalysis interactions

    ) {

        List<PatternTenGodNode> authorityNodes =
                visibleAuthorityNodes(
                        interactions
                );

        return authorityNodes
                .stream()
                .filter(
                        authority ->
                                authorityCanBridgePositions(
                                        wealth,
                                        resource,
                                        authority
                                )
                )
                .filter(
                        authority ->
                                generates(
                                        wealth,
                                        authority,
                                        interactions
                                )
                )
                .filter(
                        authority ->
                                generates(
                                        authority,
                                        resource,
                                        interactions
                                )
                )
                .findFirst();
    }

    /**
     * 官殺 위치가 財와 印 사이의 실제 통관 위치인가.
     *
     * =================================================
     *
     * 투출 印의 경우:
     *
     * 財 YEAR
     * 官 MONTH
     * 印 DAY
     *
     * 같은 구조.
     *
     * =================================================
     *
     * 月令 지장간 印의 경우:
     *
     * 財 YEAR
     * 官 MONTH
     * 月支 印
     *
     * 처럼 官이 印과 같은 pillar 천간에 있어도
     * 통관 후보로 허용한다.
     */
    private boolean authorityCanBridgePositions(

            PatternTenGodNode wealth,

            PatternTenGodNode resource,

            PatternTenGodNode authority

    ) {

        int wealthIndex =
                positionIndex(
                        wealth.position()
                );

        int resourceIndex =
                positionIndex(
                        resource.position()
                );

        int authorityIndex =
                positionIndex(
                        authority.position()
                );

        int min =
                Math.min(
                        wealthIndex,
                        resourceIndex
                );

        int max =
                Math.max(
                        wealthIndex,
                        resourceIndex
                );

        /*
         * visible resource인 경우
         * 官殺은 두 천간 사이에 있어야 한다.
         */
        if (resource.visible()) {

            return authorityIndex > min
                    &&
                    authorityIndex < max;
        }

        /*
         * 月令 hidden resource인 경우
         * 官殺이 月干에 있어
         * 같은 pillar에서 印을 생하는 구조를 허용.
         */
        return authorityIndex > min
                &&
                authorityIndex <= max;
    }

    /*
     * =================================================
     * 印格 月令 MAIN 印 찾기
     * =================================================
     */

    private Optional<PatternTenGodNode> findSelectedMonthResource(

            PatternInteractionAnalysis interaction,

            PatternTenGodInteractionAnalysis interactions

    ) {

        Optional<MonthCommandPatternCandidate> candidateOptional =
                interaction
                        .provisionalStandardCandidate();

        if (candidateOptional.isEmpty()) {

            return Optional.empty();
        }

        MonthCommandPatternCandidate candidate =
                candidateOptional.get();

        boolean resourcePattern =
                candidate.candidateType()
                        == MonthCommandPatternCandidateType
                        .DIRECT_RESOURCE

                        ||

                        candidate.candidateType()
                                == MonthCommandPatternCandidateType
                                .INDIRECT_RESOURCE;

        if (!resourcePattern) {

            return Optional.empty();
        }

        return interactions
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
                                        == candidate.hiddenStem()
                )
                .filter(
                        node ->
                                node.hiddenStemPhase()
                                        .orElse(null)
                                        == candidate.phase()
                )
                .filter(
                        node ->
                                node.hasTenGodType(
                                        candidate.candidateType()
                                )
                )
                .findFirst();
    }

    /*
     * =================================================
     * 그래프 helper
     * =================================================
     */

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

    private List<PatternTenGodNode> visibleWealthNodes(
            PatternTenGodInteractionAnalysis interactions
    ) {

        List<PatternTenGodNode> result =
                new ArrayList<>();

        result.addAll(
                interactions.visibleNodesOf(
                        MonthCommandPatternCandidateType
                                .DIRECT_WEALTH
                )
        );

        result.addAll(
                interactions.visibleNodesOf(
                        MonthCommandPatternCandidateType
                                .INDIRECT_WEALTH
                )
        );

        return List.copyOf(result);
    }

    private List<PatternTenGodNode> visibleResourceNodes(
            PatternTenGodInteractionAnalysis interactions
    ) {

        List<PatternTenGodNode> result =
                new ArrayList<>();

        result.addAll(
                interactions.visibleNodesOf(
                        MonthCommandPatternCandidateType
                                .DIRECT_RESOURCE
                )
        );

        result.addAll(
                interactions.visibleNodesOf(
                        MonthCommandPatternCandidateType
                                .INDIRECT_RESOURCE
                )
        );

        return List.copyOf(result);
    }

    private List<PatternTenGodNode> visibleAuthorityNodes(
            PatternTenGodInteractionAnalysis interactions
    ) {

        List<PatternTenGodNode> result =
                new ArrayList<>();

        result.addAll(
                interactions.visibleNodesOf(
                        MonthCommandPatternCandidateType
                                .DIRECT_OFFICER
                )
        );

        result.addAll(
                interactions.visibleNodesOf(
                        MonthCommandPatternCandidateType
                                .SEVEN_KILLINGS
                )
        );

        return List.copyOf(result);
    }

    /*
     * =================================================
     * 위치
     * =================================================
     */

    private int positionDistance(

            PillarPosition first,

            PillarPosition second

    ) {

        return Math.abs(
                positionIndex(first)
                        -
                        positionIndex(second)
        );
    }

    private int positionIndex(
            PillarPosition position
    ) {

        Objects.requireNonNull(
                position,
                "기둥 위치는 필수입니다."
        );

        return switch (position) {

            case YEAR -> 0;

            case MONTH -> 1;

            case DAY -> 2;

            case HOUR -> 3;
        };
    }

    private void validateDayMaster(

            PatternInteractionAnalysis interaction,

            PatternTenGodInteractionAnalysis interactions

    ) {

        HeavenlyStem dayMaster =
                interaction.dayMaster();

        if (
                interactions.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "PatternInteractionAnalysis와 "
                            +
                            "PatternTenGodInteractionAnalysis의 일간이 일치하지 않습니다."
            );
        }
    }
}