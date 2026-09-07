package com.dajeom.domain;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 財 하나와 印 하나 사이의
 * 실제 위치 관계 분석 한 건.
 *
 * =================================================
 *
 * wealth
 * = 투출 正財 / 偏財
 *
 * resource
 * = 투출 正印 / 偏印
 *   또는 印格의 月令 MAIN 印
 *
 * =================================================
 */
public record PatternWealthResourceRelationOccurrence(

        PatternTenGodNode wealth,

        PatternTenGodNode resource,

        Optional<PatternTenGodNode> authorityBridge,

        PatternWealthResourceRelationState state,

        Set<PatternWealthResourceRelationReason> reasons

) {

    public PatternWealthResourceRelationOccurrence {

        Objects.requireNonNull(
                wealth,
                "財 노드는 필수입니다."
        );

        Objects.requireNonNull(
                resource,
                "印 노드는 필수입니다."
        );

        authorityBridge =
                authorityBridge == null
                        ? Optional.empty()
                        : authorityBridge;

        Objects.requireNonNull(
                state,
                "財印 관계 상태는 필수입니다."
        );

        reasons =
                reasons == null
                        ? Set.of()
                        : Set.copyOf(reasons);

        if (!wealth.visible()) {

            throw new IllegalArgumentException(
                    "財印 관계의 財는 투출 천간이어야 합니다."
            );
        }

        if (
                !wealth.hasTenGodType(
                        MonthCommandPatternCandidateType.DIRECT_WEALTH
                )
                        &&
                        !wealth.hasTenGodType(
                                MonthCommandPatternCandidateType.INDIRECT_WEALTH
                        )
        ) {

            throw new IllegalArgumentException(
                    "wealth 노드는 正財 또는 偏財여야 합니다."
            );
        }

        if (
                !resource.hasTenGodType(
                        MonthCommandPatternCandidateType.DIRECT_RESOURCE
                )
                        &&
                        !resource.hasTenGodType(
                                MonthCommandPatternCandidateType.INDIRECT_RESOURCE
                        )
        ) {

            throw new IllegalArgumentException(
                    "resource 노드는 正印 또는 偏印이어야 합니다."
            );
        }

        authorityBridge.ifPresent(
                bridge -> {

                    if (!bridge.visible()) {

                        throw new IllegalArgumentException(
                                "財印 통관의 官殺 노드는 투출 천간이어야 합니다."
                        );
                    }

                    boolean authority =
                            bridge.hasTenGodType(
                                    MonthCommandPatternCandidateType.DIRECT_OFFICER
                            )
                                    ||
                                    bridge.hasTenGodType(
                                            MonthCommandPatternCandidateType.SEVEN_KILLINGS
                                    );

                    if (!authority) {

                        throw new IllegalArgumentException(
                                "財印 통관 노드는 正官 또는 七殺이어야 합니다."
                        );
                    }
                }
        );

        if (
                state
                        == PatternWealthResourceRelationState
                        .AUTHORITY_BRIDGED_NON_OBSTRUCTING

                        &&

                        authorityBridge.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "官殺 통관 상태에는 authorityBridge가 필요합니다."
            );
        }
    }

    public boolean directConflict() {

        return state
                == PatternWealthResourceRelationState
                .DIRECT_CONFLICT;
    }

    public boolean authorityBridged() {

        return state
                == PatternWealthResourceRelationState
                .AUTHORITY_BRIDGED_NON_OBSTRUCTING;
    }

    public boolean positionallySeparated() {

        return state
                == PatternWealthResourceRelationState
                .POSITIONALLY_SEPARATED_NON_OBSTRUCTING;
    }

    public boolean mutuallyNonObstructing() {

        return authorityBridged()
                ||
                positionallySeparated();
    }

    public boolean resourceIsMonthCommandHiddenStem() {

        return resource.hidden()
                &&
                resource.position()
                        == PillarPosition.MONTH;
    }
}