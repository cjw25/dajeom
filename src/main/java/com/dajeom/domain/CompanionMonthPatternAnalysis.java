package com.dajeom.domain;

import java.util.Objects;
import java.util.Set;

/**
 * 建祿 / 月劫 / 陽刃 최종 구조 분석.
 */
public record CompanionMonthPatternAnalysis(

        HeavenlyStem dayMaster,

        CompanionMonthPatternType patternType,

        CompanionMonthPatternStatus status,

        Set<CompanionMonthFormationRoute> formationRoutes,

        Set<CompanionMonthPatternReason> reasons

) {

    public CompanionMonthPatternAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                patternType,
                "CompanionMonthPatternType은 필수입니다."
        );

        Objects.requireNonNull(
                status,
                "CompanionMonthPatternStatus는 필수입니다."
        );

        formationRoutes =
                formationRoutes == null
                        ? Set.of()
                        : Set.copyOf(formationRoutes);

        reasons =
                reasons == null
                        ? Set.of()
                        : Set.copyOf(reasons);

        if (
                status == CompanionMonthPatternStatus.FORMED
                        &&
                        formationRoutes.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "FORMED 상태에는 최소 하나의 성격 경로가 필요합니다."
            );
        }

        if (
                status == CompanionMonthPatternStatus.FORMED_WITH_DEFECT
                        &&
                        formationRoutes.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "FORMED_WITH_DEFECT 상태에도 성격 경로가 필요합니다."
            );
        }
    }

    public boolean formed() {

        return status
                == CompanionMonthPatternStatus.FORMED;
    }

    public boolean formedWithDefect() {

        return status
                == CompanionMonthPatternStatus.FORMED_WITH_DEFECT;
    }

    public boolean usableFormation() {

        return formed()
                ||
                formedWithDefect();
    }

    public boolean reviewRequired() {

        return status
                == CompanionMonthPatternStatus.REVIEW_REQUIRED;
    }

    public boolean hasRoute(
            CompanionMonthFormationRoute route
    ) {

        return formationRoutes.contains(route);
    }
}