package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationGroundingConstraint;
import com.dajeom.type.LifeDomain;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 20차.
 *
 * 하나의 ConsultationCategory를
 * LLM이 설명할 때 적용할 Grounding Policy.
 *
 * =================================================
 *
 * allowedDomains
 *
 * 해당 상담 View가 소비할 수 있는
 * 18차 LifeDomain.
 *
 * =================================================
 *
 * timelineAllowed
 *
 * 대운·세운·월운 Timeline Scenario를
 * 해당 상담 View에서 사용할 수 있는지.
 *
 * =================================================
 *
 * constraints
 *
 * 자연어 설명 과정에서 반드시 지켜야 할
 * 안전/정밀화 제약조건.
 *
 * =================================================
 *
 * 이 객체는:
 *
 * - 점수를 계산하지 않는다.
 * - 명리 결과를 다시 판정하지 않는다.
 * - 사용자 문장을 생성하지 않는다.
 */
public record ConsultationGroundingPolicy(

        ConsultationCategory category,

        Set<LifeDomain> allowedDomains,

        boolean timelineAllowed,

        Set<ConsultationGroundingConstraint> constraints

) {

    public ConsultationGroundingPolicy {

        Objects.requireNonNull(
                category,
                "Grounding Policy의 ConsultationCategory는 필수입니다."
        );

        allowedDomains =
                immutableDomains(
                        allowedDomains
                );

        constraints =
                immutableConstraints(
                        constraints
                );

        /*
         * =================================================
         * 모든 상담 분야에 필요한 기본 제약
         * =================================================
         */

        requireConstraint(
                constraints,
                ConsultationGroundingConstraint.NO_NEW_SAJU_CALCULATION
        );

        requireConstraint(
                constraints,
                ConsultationGroundingConstraint.NO_DIRECT_EVENT_ASSERTION
        );

        requireConstraint(
                constraints,
                ConsultationGroundingConstraint.PRESERVE_MIXED_EVIDENCE
        );

        requireConstraint(
                constraints,
                ConsultationGroundingConstraint.PRESERVE_TIMELINE_UNCERTAINTY
        );

        requireConstraint(
                constraints,
                ConsultationGroundingConstraint.AUXILIARY_NOT_STANDALONE
        );
    }

    public boolean allowsDomain(
            LifeDomain domain
    ) {

        return domain != null
                && allowedDomains.contains(
                domain
        );
    }

    public boolean hasConstraint(
            ConsultationGroundingConstraint constraint
    ) {

        return constraint != null
                && constraints.contains(
                constraint
        );
    }

    private static Set<LifeDomain>
    immutableDomains(
            Set<LifeDomain> source
    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return Set.of();
        }

        EnumSet<LifeDomain> copied =
                EnumSet.noneOf(
                        LifeDomain.class
                );

        for (
                LifeDomain domain
                : source
        ) {

            copied.add(
                    Objects.requireNonNull(
                            domain,
                            "Grounding Policy의 LifeDomain은 null일 수 없습니다."
                    )
            );
        }

        return Collections.unmodifiableSet(
                copied
        );
    }

    private static Set<ConsultationGroundingConstraint>
    immutableConstraints(
            Set<ConsultationGroundingConstraint> source
    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return Set.of();
        }

        EnumSet<ConsultationGroundingConstraint> copied =
                EnumSet.noneOf(
                        ConsultationGroundingConstraint.class
                );

        for (
                ConsultationGroundingConstraint constraint
                : source
        ) {

            copied.add(
                    Objects.requireNonNull(
                            constraint,
                            "Grounding Constraint는 null일 수 없습니다."
                    )
            );
        }

        return Collections.unmodifiableSet(
                copied
        );
    }

    private static void requireConstraint(

            Set<ConsultationGroundingConstraint> constraints,

            ConsultationGroundingConstraint required

    ) {

        if (
                !constraints.contains(
                        required
                )
        ) {

            throw new IllegalArgumentException(
                    "필수 Grounding Constraint가 누락되었습니다: "
                            + required
            );
        }
    }
}