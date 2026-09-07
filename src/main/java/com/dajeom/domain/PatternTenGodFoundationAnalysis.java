package com.dajeom.domain;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 특정 십성 하나의 foundation 분석.
 */
public record PatternTenGodFoundationAnalysis(

        MonthCommandPatternCandidateType tenGodType,

        List<PatternTenGodNode> visibleNodes,

        List<PatternTenGodNode> hiddenFamilyNodes,

        List<PatternTenGodRootHit> rootHits,

        boolean monthCommandMain,

        PatternTenGodFoundationState state,

        Set<PatternTenGodFoundationReason> reasons

) {

    public PatternTenGodFoundationAnalysis {

        Objects.requireNonNull(
                tenGodType,
                "십성 타입은 필수입니다."
        );

        visibleNodes =
                visibleNodes == null
                        ? List.of()
                        : List.copyOf(visibleNodes);

        hiddenFamilyNodes =
                hiddenFamilyNodes == null
                        ? List.of()
                        : List.copyOf(hiddenFamilyNodes);

        rootHits =
                rootHits == null
                        ? List.of()
                        : List.copyOf(rootHits);

        Objects.requireNonNull(
                state,
                "foundation 상태는 필수입니다."
        );

        reasons =
                reasons == null
                        ? Set.of()
                        : Set.copyOf(reasons);

        for (PatternTenGodNode node : visibleNodes) {

            Objects.requireNonNull(
                    node,
                    "visibleNodes에 null이 포함될 수 없습니다."
            );

            if (!node.visible()) {

                throw new IllegalArgumentException(
                        "visibleNodes에는 투출 천간만 들어갈 수 있습니다."
                );
            }

            if (!node.hasTenGodType(tenGodType)) {

                throw new IllegalArgumentException(
                        "visibleNode의 십성이 분석 대상과 일치하지 않습니다."
                );
            }
        }

        for (PatternTenGodNode node : hiddenFamilyNodes) {

            Objects.requireNonNull(
                    node,
                    "hiddenFamilyNodes에 null이 포함될 수 없습니다."
            );

            if (!node.hidden()) {

                throw new IllegalArgumentException(
                        "hiddenFamilyNodes에는 지장간만 들어갈 수 있습니다."
                );
            }
        }

        for (PatternTenGodRootHit hit : rootHits) {

            Objects.requireNonNull(
                    hit,
                    "rootHits에 null이 포함될 수 없습니다."
            );

            if (!visibleNodes.contains(hit.visibleNode())) {

                throw new IllegalArgumentException(
                        "rootHit의 visibleNode가 분석 대상 visibleNodes에 없습니다."
                );
            }

            if (!hiddenFamilyNodes.contains(hit.hiddenNode())) {

                throw new IllegalArgumentException(
                        "rootHit의 hiddenNode가 분석 대상 hiddenFamilyNodes에 없습니다."
                );
            }
        }
    }

    public boolean foundationStable() {

        return state
                == PatternTenGodFoundationState
                .FOUNDATION_CONFIRMED;
    }

    public boolean absent() {

        return state
                == PatternTenGodFoundationState
                .ABSENT;
    }

    public boolean visible() {

        return !visibleNodes.isEmpty();
    }

    public boolean rootedVisibleStemExists() {

        return !rootHits.isEmpty();
    }

    public boolean hasExactStemRoot() {

        return rootHits
                .stream()
                .anyMatch(
                        PatternTenGodRootHit
                                ::exactStem
                );
    }

    public boolean hasMonthRoot() {

        return rootHits
                .stream()
                .anyMatch(
                        PatternTenGodRootHit
                                ::monthRoot
                );
    }
}