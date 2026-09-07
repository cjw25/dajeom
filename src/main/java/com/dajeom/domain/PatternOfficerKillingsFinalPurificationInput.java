package com.dajeom.domain;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 官殺混雜 최종 판정 Pipeline의 입력.
 *
 * =================================================
 * 전체 의미
 * =================================================
 *
 * 표준 官殺 정리 규칙이 먼저 실행된 뒤,
 * 남아 있는 七殺 각각에 대해 羊刃合殺 여부를
 * 추적한 결과를 하나로 모은다.
 *
 * =================================================
 * mixed
 * =================================================
 *
 * 官殺混雜 자체가 실제 성립하는가.
 *
 * =================================================
 * standardPurificationCleared
 * =================================================
 *
 * 羊刃合殺을 사용하기 전에 이미
 * 官殺 전용 표준 규칙으로 전체 정리가 완료되었는가.
 *
 * 예:
 *
 * - 去官留殺
 * - 去殺留官
 * - 食神制殺 계열
 * - 傷官去官 계열
 *
 * generic 天干합의 FULLY_BOUND는
 * 여기에 넣으면 안 된다.
 *
 * =================================================
 * standardPurificationReviewRequired
 * =================================================
 *
 * 표준 官殺 정리 단계 자체에서
 * 최종 판정을 확정할 수 없는 상태인가.
 *
 * =================================================
 * retainedKillingNodes
 * =================================================
 *
 * 표준 정리 이후에도 실제 남아 있는
 * 개별 七殺 노드.
 *
 * =================================================
 * yangBladeResolvedKillingNodes
 * =================================================
 *
 * retained 七殺 중
 *
 * hidden 劫財 stem
 * ↔
 * visible 七殺
 *
 * 전용 羊刃合殺 규칙을 실제 통과한 노드.
 *
 * =================================================
 * yangBladeReviewKillingNodes
 * =================================================
 *
 * 경쟁·차단·중복 등의 이유로
 * 羊刃合殺 최종 성립 여부를 확정할 수 없는 七殺.
 */
public record PatternOfficerKillingsFinalPurificationInput(

        boolean mixed,

        boolean standardPurificationCleared,

        boolean standardPurificationReviewRequired,

        Set<OfficerKillingsNodeRef> retainedKillingNodes,

        Set<OfficerKillingsNodeRef> yangBladeResolvedKillingNodes,

        Set<OfficerKillingsNodeRef> yangBladeReviewKillingNodes

) {

    public PatternOfficerKillingsFinalPurificationInput {

        retainedKillingNodes =
                copy(
                        retainedKillingNodes,
                        "retainedKillingNodes"
                );

        yangBladeResolvedKillingNodes =
                copy(
                        yangBladeResolvedKillingNodes,
                        "yangBladeResolvedKillingNodes"
                );

        yangBladeReviewKillingNodes =
                copy(
                        yangBladeReviewKillingNodes,
                        "yangBladeReviewKillingNodes"
                );

        /*
         * =================================================
         * 기본 모순 방지
         * =================================================
         */

        if (
                standardPurificationCleared
                        &&
                        standardPurificationReviewRequired
        ) {

            throw new IllegalArgumentException(
                    "표준 官殺 정리가 완료된 상태와 REVIEW_REQUIRED 상태는 동시에 성립할 수 없습니다."
            );
        }

        /*
         * 官殺混雜 자체가 아니면
         * 정리 작업도 존재할 수 없다.
         */
        if (!mixed) {

            if (
                    standardPurificationCleared
                            ||
                            standardPurificationReviewRequired
                            ||
                            !retainedKillingNodes.isEmpty()
                            ||
                            !yangBladeResolvedKillingNodes.isEmpty()
                            ||
                            !yangBladeReviewKillingNodes.isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "官殺混雜이 아닌 경우 purification 관련 결과를 가질 수 없습니다."
                );
            }
        }

        /*
         * 표준 단계에서 이미 전체 정리가 끝났다면
         * retained 七殺이 남아 있을 수 없다.
         */
        if (
                standardPurificationCleared
                        &&
                        !retainedKillingNodes.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "표준 官殺 정리가 완료되었다면 retained 七殺이 남을 수 없습니다."
            );
        }

        if (
                standardPurificationCleared
                        &&
                        (
                                !yangBladeResolvedKillingNodes.isEmpty()
                                        ||
                                        !yangBladeReviewKillingNodes.isEmpty()
                        )
        ) {

            throw new IllegalArgumentException(
                    "표준 官殺 정리가 이미 완료된 경우 羊刃合殺 후속 처리 노드를 가질 수 없습니다."
            );
        }

        /*
         * 羊刃合殺 대상은 반드시
         * retained 七殺이어야 한다.
         */
        if (
                !retainedKillingNodes.containsAll(
                        yangBladeResolvedKillingNodes
                )
        ) {

            throw new IllegalArgumentException(
                    "羊刃合殺 resolved 노드는 반드시 retained 七殺에 포함되어야 합니다."
            );
        }

        if (
                !retainedKillingNodes.containsAll(
                        yangBladeReviewKillingNodes
                )
        ) {

            throw new IllegalArgumentException(
                    "羊刃合殺 review 노드는 반드시 retained 七殺에 포함되어야 합니다."
            );
        }

        /*
         * 한 七殺이 resolved와 review에
         * 동시에 들어갈 수 없다.
         */
        for (
                OfficerKillingsNodeRef node
                : yangBladeResolvedKillingNodes
        ) {

            if (
                    yangBladeReviewKillingNodes.contains(
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

    /**
     * 표준 정리 이후
     * 羊刃合殺 검토가 실제 필요한가.
     */
    public boolean requiresYangBladeCompletion() {

        return mixed
                &&
                !standardPurificationCleared
                &&
                !standardPurificationReviewRequired
                &&
                !retainedKillingNodes.isEmpty();
    }

    /**
     * 표준 단계에서 정리가 끝난 경우.
     */
    public boolean standardCleared() {

        return standardPurificationCleared;
    }

    /**
     * 표준 단계에서부터 확정 불가.
     */
    public boolean standardReviewRequired() {

        return standardPurificationReviewRequired;
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
                            name + "에 null 노드가 포함될 수 없습니다."
                    )
            );
        }

        return Set.copyOf(
                copied
        );
    }
}