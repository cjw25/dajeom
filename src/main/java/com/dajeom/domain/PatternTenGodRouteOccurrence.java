package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 실제 동일 십성 노드를 따라 연결된
 * 하나의 생극 route.
 *
 * nodes 순서는 route 흐름 순서다.
 *
 * 예:
 *
 * 財 → 七殺 → 印 → 日主
 *
 * nodes:
 *
 * [財, 七殺, 印, 日主]
 */
public record PatternTenGodRouteOccurrence(

        PatternTenGodRouteType type,

        List<PatternTenGodNode> nodes

) {

    public PatternTenGodRouteOccurrence {

        Objects.requireNonNull(
                type,
                "route type은 필수입니다."
        );

        nodes =
                nodes == null
                        ? List.of()
                        : List.copyOf(nodes);

        if (nodes.size() < 2) {

            throw new IllegalArgumentException(
                    "십성 route에는 최소 두 노드가 필요합니다."
            );
        }

        for (PatternTenGodNode node : nodes) {

            Objects.requireNonNull(
                    node,
                    "route nodes에 null이 포함될 수 없습니다."
            );
        }
    }

    public PatternTenGodNode first() {

        return nodes.get(0);
    }

    public PatternTenGodNode last() {

        return nodes.get(
                nodes.size() - 1
        );
    }

    public boolean involves(
            PatternTenGodNode node
    ) {

        return nodes.contains(node);
    }
}