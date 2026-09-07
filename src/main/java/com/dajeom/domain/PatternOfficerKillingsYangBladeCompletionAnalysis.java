package com.dajeom.domain;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 羊刃合殺의 node-level 완료 분석.
 *
 * =================================================
 *
 * retainedKillingNodes
 *
 * 앞선 官殺取清 과정 이후에도
 * 실제로 남아 있어서 검토 대상이 되는 七殺 노드.
 *
 * =================================================
 *
 * resolvedKillingNodes
 *
 * 개별적으로 유효한 羊刃合殺이 확인된 七殺.
 *
 * =================================================
 *
 * unresolvedKillingNodes
 *
 * 羊刃合殺이 성립하지 않은 七殺.
 *
 * =================================================
 *
 * reviewKillingNodes
 *
 * 경쟁·중복·차단 등의 이유로
 * 성립 여부를 확정할 수 없는 七殺.
 *
 * =================================================
 *
 * 가장 중요한 불변식:
 *
 * retained
 * =
 * resolved
 * + unresolved
 * + review
 *
 * 이며 세 결과 집합은 서로 겹치지 않는다.
 */
public record PatternOfficerKillingsYangBladeCompletionAnalysis(

        PatternOfficerKillingsYangBladeCompletionStatus status,

        Set<OfficerKillingsNodeRef> retainedKillingNodes,

        Set<OfficerKillingsNodeRef> resolvedKillingNodes,

        Set<OfficerKillingsNodeRef> unresolvedKillingNodes,

        Set<OfficerKillingsNodeRef> reviewKillingNodes

) {

    public PatternOfficerKillingsYangBladeCompletionAnalysis {

        Objects.requireNonNull(
                status,
                "羊刃合殺 completion status는 필수입니다."
        );

        retainedKillingNodes =
                immutableSet(
                        retainedKillingNodes,
                        "retainedKillingNodes"
                );

        resolvedKillingNodes =
                immutableSet(
                        resolvedKillingNodes,
                        "resolvedKillingNodes"
                );

        unresolvedKillingNodes =
                immutableSet(
                        unresolvedKillingNodes,
                        "unresolvedKillingNodes"
                );

        reviewKillingNodes =
                immutableSet(
                        reviewKillingNodes,
                        "reviewKillingNodes"
                );

        validateSubset(
                retainedKillingNodes,
                resolvedKillingNodes,
                "resolvedKillingNodes"
        );

        validateSubset(
                retainedKillingNodes,
                unresolvedKillingNodes,
                "unresolvedKillingNodes"
        );

        validateSubset(
                retainedKillingNodes,
                reviewKillingNodes,
                "reviewKillingNodes"
        );

        validateDisjoint(
                resolvedKillingNodes,
                unresolvedKillingNodes,
                "resolved",
                "unresolved"
        );

        validateDisjoint(
                resolvedKillingNodes,
                reviewKillingNodes,
                "resolved",
                "review"
        );

        validateDisjoint(
                unresolvedKillingNodes,
                reviewKillingNodes,
                "unresolved",
                "review"
        );

        LinkedHashSet<OfficerKillingsNodeRef> classified =
                new LinkedHashSet<>();

        classified.addAll(
                resolvedKillingNodes
        );

        classified.addAll(
                unresolvedKillingNodes
        );

        classified.addAll(
                reviewKillingNodes
        );

        if (
                !classified.equals(
                        retainedKillingNodes
                )
        ) {

            throw new IllegalArgumentException(
                    "모든 retained 七殺 노드는 resolved/unresolved/review 중 하나로 분류되어야 합니다."
            );
        }

        validateStatus(
                status,
                retainedKillingNodes,
                resolvedKillingNodes,
                unresolvedKillingNodes,
                reviewKillingNodes
        );
    }

    /**
     * retained 七殺 전체가
     * 개별적으로 羊刃合殺 처리되었는가.
     *
     * 七殺 하나만 처리되었더라도
     * 다른 retained 七殺이 남아 있으면 false다.
     */
    public boolean complete() {

        return status
                == PatternOfficerKillingsYangBladeCompletionStatus.COMPLETE;
    }

    /**
     * 일부만 처리된 상태.
     */
    public boolean partial() {

        return status
                == PatternOfficerKillingsYangBladeCompletionStatus.PARTIAL;
    }

    public boolean reviewRequired() {

        return status
                == PatternOfficerKillingsYangBladeCompletionStatus.REVIEW_REQUIRED;
    }

    public boolean unresolved() {

        return status
                == PatternOfficerKillingsYangBladeCompletionStatus.UNRESOLVED;
    }

    public boolean hasRetainedKillings() {

        return !retainedKillingNodes.isEmpty();
    }

    /**
     * 특정 七殺 노드가
     * 실제 羊刃合殺 완료 대상인지 확인.
     */
    public boolean resolved(
            OfficerKillingsNodeRef node
    ) {

        Objects.requireNonNull(
                node,
                "확인할 官殺 노드는 필수입니다."
        );

        return resolvedKillingNodes.contains(
                node
        );
    }

    private static Set<OfficerKillingsNodeRef> immutableSet(

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

    private static void validateSubset(

            Set<OfficerKillingsNodeRef> retained,

            Set<OfficerKillingsNodeRef> subset,

            String name

    ) {

        if (
                !retained.containsAll(
                        subset
                )
        ) {

            throw new IllegalArgumentException(
                    name + "에는 retained 七殺이 아닌 노드가 포함될 수 없습니다."
            );
        }
    }

    private static void validateDisjoint(

            Set<OfficerKillingsNodeRef> left,

            Set<OfficerKillingsNodeRef> right,

            String leftName,

            String rightName

    ) {

        for (
                OfficerKillingsNodeRef node
                : left
        ) {

            if (
                    right.contains(
                            node
                    )
            ) {

                throw new IllegalArgumentException(
                        "같은 七殺 노드가 "
                                + leftName
                                + "와 "
                                + rightName
                                + "에 동시에 포함될 수 없습니다: "
                                + node.nodeKey()
                );
            }
        }
    }

    private static void validateStatus(

            PatternOfficerKillingsYangBladeCompletionStatus status,

            Set<OfficerKillingsNodeRef> retained,

            Set<OfficerKillingsNodeRef> resolved,

            Set<OfficerKillingsNodeRef> unresolved,

            Set<OfficerKillingsNodeRef> review

    ) {

        PatternOfficerKillingsYangBladeCompletionStatus expected;

        if (
                retained.isEmpty()
        ) {

            expected =
                    PatternOfficerKillingsYangBladeCompletionStatus
                            .NO_RETAINED_KILLINGS;

        } else if (
                !review.isEmpty()
        ) {

            expected =
                    PatternOfficerKillingsYangBladeCompletionStatus
                            .REVIEW_REQUIRED;

        } else if (
                resolved.equals(
                        retained
                )
        ) {

            expected =
                    PatternOfficerKillingsYangBladeCompletionStatus
                            .COMPLETE;

        } else if (
                !resolved.isEmpty()
        ) {

            expected =
                    PatternOfficerKillingsYangBladeCompletionStatus
                            .PARTIAL;

        } else {

            expected =
                    PatternOfficerKillingsYangBladeCompletionStatus
                            .UNRESOLVED;
        }

        if (
                status != expected
        ) {

            throw new IllegalArgumentException(
                    "羊刃合殺 completion status가 노드 분류와 일치하지 않습니다. expected="
                            + expected
                            + ", actual="
                            + status
            );
        }

        /*
         * COMPLETE에서는 unresolved/review가
         * 절대 남아 있을 수 없다.
         */
        if (
                status
                        == PatternOfficerKillingsYangBladeCompletionStatus.COMPLETE

                        &&

                        (
                                !unresolved.isEmpty()
                                        ||
                                        !review.isEmpty()
                        )
        ) {

            throw new IllegalArgumentException(
                    "COMPLETE 상태에서는 미처리 또는 검토 七殺 노드가 남을 수 없습니다."
            );
        }
    }
}