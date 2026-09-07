package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 특정 십성 타입의 투출 천간들이
 * active 天干合에서 어떻게 처리되는지 분석한 결과.
 */
public record PatternStemTenGodBindingAnalysis(

        MonthCommandPatternCandidateType tenGodType,

        List<PatternTenGodNode> visibleNodes,

        List<PatternTenGodNode> activelyBoundNodes,

        List<PatternTenGodNode> reviewNodes,

        PatternStemTenGodBindingState state

) {

    public PatternStemTenGodBindingAnalysis {

        Objects.requireNonNull(
                tenGodType,
                "십성 타입은 필수입니다."
        );

        visibleNodes =
                visibleNodes == null
                        ? List.of()
                        : List.copyOf(visibleNodes);

        activelyBoundNodes =
                activelyBoundNodes == null
                        ? List.of()
                        : List.copyOf(activelyBoundNodes);

        reviewNodes =
                reviewNodes == null
                        ? List.of()
                        : List.copyOf(reviewNodes);

        Objects.requireNonNull(
                state,
                "십성 Binding 상태는 필수입니다."
        );

        for (PatternTenGodNode node : visibleNodes) {

            Objects.requireNonNull(
                    node,
                    "visibleNodes에 null이 포함될 수 없습니다."
            );

            if (!node.visible()) {

                throw new IllegalArgumentException(
                        "십성 Binding 분석에는 투출 천간만 들어갈 수 있습니다."
                );
            }

            if (!node.hasTenGodType(tenGodType)) {

                throw new IllegalArgumentException(
                        "visibleNode의 십성 타입이 분석 대상과 일치하지 않습니다."
                );
            }
        }

        for (PatternTenGodNode node : activelyBoundNodes) {

            if (!visibleNodes.contains(node)) {

                throw new IllegalArgumentException(
                        "activelyBoundNodes는 visibleNodes의 부분집합이어야 합니다."
                );
            }
        }

        for (PatternTenGodNode node : reviewNodes) {

            if (!visibleNodes.contains(node)) {

                throw new IllegalArgumentException(
                        "reviewNodes는 visibleNodes의 부분집합이어야 합니다."
                );
            }
        }

        if (
                state == PatternStemTenGodBindingState.ABSENT
                        &&
                        !visibleNodes.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "ABSENT 상태인데 투출 십성 노드가 존재합니다."
            );
        }

        if (
                state != PatternStemTenGodBindingState.ABSENT
                        &&
                        visibleNodes.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "투출 십성 노드가 없으면 ABSENT 상태여야 합니다."
            );
        }
    }

    public boolean absent() {

        return state
                == PatternStemTenGodBindingState.ABSENT;
    }

    public boolean retained() {

        return state
                == PatternStemTenGodBindingState.RETAINED;
    }

    public boolean partiallyBound() {

        return state
                == PatternStemTenGodBindingState.PARTIALLY_BOUND;
    }

    public boolean fullyBound() {

        return state
                == PatternStemTenGodBindingState.FULLY_BOUND;
    }

    public boolean reviewRequired() {

        return state
                == PatternStemTenGodBindingState.REVIEW_REQUIRED;
    }

    /**
     * 합에 묶이지 않고 실제로 남아 있는
     * 투출 노드가 하나 이상 있는가.
     *
     * REVIEW_REQUIRED 상태에서는
     * 보존됐다고 확정하지 않는다.
     */
    public boolean hasRetainedVisibleNode() {

        if (
                absent()
                        ||
                        reviewRequired()
        ) {

            return false;
        }

        return visibleNodes
                .stream()
                .anyMatch(
                        node ->
                                !activelyBoundNodes
                                        .contains(node)
                );
    }
}