package com.dajeom.calculation;

import com.dajeom.domain.OfficerKillingsNodeRef;
import com.dajeom.domain.PatternOfficerKillingsYangBladeCompletionAnalysis;
import com.dajeom.domain.PatternOfficerKillingsYangBladeCompletionStatus;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * retained 七殺 각각에 대해
 * 羊刃合殺 완료 여부를 판정한다.
 *
 * =================================================
 *
 * 핵심 오류 방지
 * =================================================
 *
 * retained 七殺:
 *
 * K1
 * K2
 *
 * 실제 羊刃合殺:
 *
 * K1만 성립
 *
 * 과거 잘못된 방식:
 *
 * "羊刃合殺이 하나 존재한다"
 * →
 * 전체 완료
 *
 * 잘못된 결과.
 *
 * =================================================
 *
 * 현재 방식:
 *
 * K1 = resolved
 * K2 = unresolved
 *
 * →
 *
 * PARTIAL
 *
 * =================================================
 *
 * COMPLETE는:
 *
 * retainedKillingNodes
 * ==
 * yangBladeResolvedKillingNodes
 *
 * 일 때만 가능하다.
 */
@Component
public class PatternOfficerKillingsYangBladeCompletionCalculator {

    /**
     * @param retainedKillingNodes
     * 官殺 정리 과정 이후 실제 남아 있는 七殺 노드.
     *
     * @param yangBladeResolvedKillingNodes
     * 개별적으로 유효한 羊刃合殺이 최종 확인된 七殺 노드.
     *
     * raw 天干五合이나 지지 합 자체를 넣으면 안 된다.
     *
     * hidden 劫財 stem ↔ visible 七殺의
     * 羊刃合殺 전용 규칙을 통과한 노드만 넣는다.
     *
     * @param reviewKillingNodes
     * 결속 경쟁·차단·중복 대상 등으로
     * 최종 확정할 수 없는 七殺 노드.
     */
    public PatternOfficerKillingsYangBladeCompletionAnalysis calculate(

            Set<OfficerKillingsNodeRef> retainedKillingNodes,

            Set<OfficerKillingsNodeRef> yangBladeResolvedKillingNodes,

            Set<OfficerKillingsNodeRef> reviewKillingNodes

    ) {

        Set<OfficerKillingsNodeRef> retained =
                copy(
                        retainedKillingNodes,
                        "retainedKillingNodes"
                );

        Set<OfficerKillingsNodeRef> resolved =
                copy(
                        yangBladeResolvedKillingNodes,
                        "yangBladeResolvedKillingNodes"
                );

        Set<OfficerKillingsNodeRef> review =
                copy(
                        reviewKillingNodes,
                        "reviewKillingNodes"
                );

        /*
         * 羊刃合殺 처리 대상으로 들어온 노드는
         * 반드시 retained 七殺이어야 한다.
         */
        if (
                !retained.containsAll(
                        resolved
                )
        ) {

            throw new IllegalArgumentException(
                    "羊刃合殺 resolved 노드에는 retained 七殺이 아닌 노드가 포함될 수 없습니다."
            );
        }

        if (
                !retained.containsAll(
                        review
                )
        ) {

            throw new IllegalArgumentException(
                    "羊刃合殺 review 노드에는 retained 七殺이 아닌 노드가 포함될 수 없습니다."
            );
        }

        /*
         * resolved와 review는 동시에 될 수 없다.
         */
        for (
                OfficerKillingsNodeRef node
                : resolved
        ) {

            if (
                    review.contains(
                            node
                    )
            ) {

                throw new IllegalArgumentException(
                        "같은 七殺 노드를 羊刃合殺 resolved와 review로 동시에 분류할 수 없습니다: "
                                + node.nodeKey()
                );
            }
        }

        /*
         * 미처리 노드는:
         *
         * retained
         * -
         * resolved
         * -
         * review
         */
        LinkedHashSet<OfficerKillingsNodeRef> unresolved =
                new LinkedHashSet<>(
                        retained
                );

        unresolved.removeAll(
                resolved
        );

        unresolved.removeAll(
                review
        );

        PatternOfficerKillingsYangBladeCompletionStatus status =
                decideStatus(
                        retained,
                        resolved,
                        unresolved,
                        review
                );

        return new PatternOfficerKillingsYangBladeCompletionAnalysis(
                status,
                retained,
                resolved,
                unresolved,
                review
        );
    }

    /**
     * 검토 노드가 없는 일반적인 경우.
     */
    public PatternOfficerKillingsYangBladeCompletionAnalysis calculate(

            Set<OfficerKillingsNodeRef> retainedKillingNodes,

            Set<OfficerKillingsNodeRef> yangBladeResolvedKillingNodes

    ) {

        return calculate(
                retainedKillingNodes,
                yangBladeResolvedKillingNodes,
                Set.of()
        );
    }

    private PatternOfficerKillingsYangBladeCompletionStatus decideStatus(

            Set<OfficerKillingsNodeRef> retained,

            Set<OfficerKillingsNodeRef> resolved,

            Set<OfficerKillingsNodeRef> unresolved,

            Set<OfficerKillingsNodeRef> review

    ) {

        /*
         * 처리할 七殺 없음.
         */
        if (
                retained.isEmpty()
        ) {

            return PatternOfficerKillingsYangBladeCompletionStatus
                    .NO_RETAINED_KILLINGS;
        }

        /*
         * 하나라도 확정 불가라면
         * 전체 완료 판정을 내리지 않는다.
         */
        if (
                !review.isEmpty()
        ) {

            return PatternOfficerKillingsYangBladeCompletionStatus
                    .REVIEW_REQUIRED;
        }

        /*
         * 핵심.
         *
         * resolved 하나가 존재하는지만 보는 것이 아니다.
         *
         * retained 전체가 resolved인지 본다.
         */
        if (
                resolved.equals(
                        retained
                )

                        &&

                        unresolved.isEmpty()
        ) {

            return PatternOfficerKillingsYangBladeCompletionStatus
                    .COMPLETE;
        }

        /*
         * 일부 七殺만 처리.
         */
        if (
                !resolved.isEmpty()
        ) {

            return PatternOfficerKillingsYangBladeCompletionStatus
                    .PARTIAL;
        }

        /*
         * 처리된 七殺 없음.
         */
        return PatternOfficerKillingsYangBladeCompletionStatus
                .UNRESOLVED;
    }

    private Set<OfficerKillingsNodeRef> copy(

            Set<OfficerKillingsNodeRef> source,

            String name

    ) {

        if (
                source == null
        ) {

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