package com.dajeom.domain;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 羊刃合殺 전용 규칙에서 확인한
 * 七殺 개별 노드 결과 Snapshot.
 *
 * =================================================
 * resolvedKillingNodes
 * =================================================
 *
 * 실제 전용 규칙을 통과해
 * 羊刃合殺이 성립한 七殺 노드.
 *
 * =================================================
 * reviewKillingNodes
 * =================================================
 *
 * 다음과 같은 이유로 확정하지 못한 七殺 노드.
 *
 * - 경쟁하는 합
 * - 중복 결속
 * - 중간 차단
 * - 적용 우선순위 충돌
 * - 기타 rule review
 *
 * =================================================
 * 중요
 * =================================================
 *
 * 이 Snapshot 자체는
 * retained 七殺 전체를 알지 못한다.
 *
 * 최종 Adapter 단계에서
 *
 * resolved/review
 * ⊆
 * retained
 *
 * 인지를 검증한다.
 */
public record PatternOfficerKillingsYangBladeNodeSnapshot(

        Set<OfficerKillingsNodeRef> resolvedKillingNodes,

        Set<OfficerKillingsNodeRef> reviewKillingNodes

) {

    public PatternOfficerKillingsYangBladeNodeSnapshot {

        resolvedKillingNodes =
                copy(
                        resolvedKillingNodes,
                        "resolvedKillingNodes"
                );

        reviewKillingNodes =
                copy(
                        reviewKillingNodes,
                        "reviewKillingNodes"
                );

        /*
         * 같은 七殺이
         * resolved와 review일 수 없다.
         */
        for (
                OfficerKillingsNodeRef node
                : resolvedKillingNodes
        ) {

            if (
                    reviewKillingNodes.contains(
                            node
                    )
            ) {

                throw new IllegalArgumentException(
                        "같은 七殺 노드를 羊刃合殺 resolved와 review로 동시에 분류할 수 없습니다: "
                                + node.nodeKey()
                );
            }
        }
    }

    public boolean empty() {

        return resolvedKillingNodes.isEmpty()
                &&
                reviewKillingNodes.isEmpty();
    }

    private static Set<OfficerKillingsNodeRef> copy(

            Set<OfficerKillingsNodeRef> source,

            String name

    ) {

        if (source == null) {

            return Set.of();
        }

        LinkedHashSet<OfficerKillingsNodeRef> copied =
                new LinkedHashSet<>();

        for (
                OfficerKillingsNodeRef node
                : source
        ) {

            copied.add(
                    Objects.requireNonNull(
                            node,
                            name + "에 null이 포함될 수 없습니다."
                    )
            );
        }

        return Set.copyOf(
                copied
        );
    }
}