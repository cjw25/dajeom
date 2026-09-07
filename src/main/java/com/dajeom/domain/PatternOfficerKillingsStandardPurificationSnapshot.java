package com.dajeom.domain;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 기존 官殺 전용 정리 규칙의 결과를
 * 최종 purification 계층에 넘기기 위한 정규화 Snapshot.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * 기존 프로젝트의 실제:
 *
 * PatternOfficerKillingsPurificationAnalysis
 *
 * 내부 구조가 어떻게 되어 있든
 * 최종 계층에서는 아래 정보만 알면 된다.
 *
 * - 官殺混雜인가
 * - 표준 규칙으로 전체 정리가 끝났는가
 * - 표준 규칙 자체가 검토 필요한가
 * - 표준 정리 이후 남은 七殺 노드는 무엇인가
 *
 * =================================================
 * 중요
 * =================================================
 *
 * retainedKillingNodes는
 * "원국에 존재하는 모든 七殺"이 아니다.
 *
 * 반드시:
 *
 * 표준 官殺 정리
 *      ↓
 * 아직 제거되지 않고 남은 七殺
 *
 * 만 들어와야 한다.
 */
public record PatternOfficerKillingsStandardPurificationSnapshot(

        boolean mixed,

        boolean cleared,

        boolean reviewRequired,

        Set<OfficerKillingsNodeRef> retainedKillingNodes

) {

    public PatternOfficerKillingsStandardPurificationSnapshot {

        retainedKillingNodes =
                copy(
                        retainedKillingNodes
                );

        /*
         * =================================================
         * 상태 모순 방지
         * =================================================
         */

        if (
                cleared
                        &&
                        reviewRequired
        ) {

            throw new IllegalArgumentException(
                    "官殺 표준 정리 완료와 REVIEW_REQUIRED는 동시에 성립할 수 없습니다."
            );
        }

        /*
         * 官殺混雜이 아니면
         * 정리 결과나 retained 七殺이 존재하면 안 된다.
         */
        if (!mixed) {

            if (
                    cleared
                            ||
                            reviewRequired
                            ||
                            !retainedKillingNodes.isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "官殺混雜이 아닌 경우 purification 상태나 retained 七殺을 가질 수 없습니다."
                );
            }
        }

        /*
         * 전체 정리가 끝났는데
         * 七殺이 남아 있으면 모순.
         */
        if (
                cleared
                        &&
                        !retainedKillingNodes.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "官殺 표준 정리가 완료되었다면 retained 七殺이 남을 수 없습니다."
            );
        }
    }

    /**
     * 羊刃合殺 후속 검토가 필요한 상태인가.
     */
    public boolean requiresYangBladeReview() {

        return mixed
                &&
                !cleared
                &&
                !reviewRequired
                &&
                !retainedKillingNodes.isEmpty();
    }

    private static Set<OfficerKillingsNodeRef> copy(
            Set<OfficerKillingsNodeRef> source
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
                            "retainedKillingNodes에 null이 포함될 수 없습니다."
                    )
            );
        }

        return Set.copyOf(
                copied
        );
    }
}