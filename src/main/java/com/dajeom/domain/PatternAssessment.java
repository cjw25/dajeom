package com.dajeom.domain;

import java.util.Objects;
import java.util.Set;

/**
 * 개별 격국 Rule의 공통 판정 결과.
 *
 * R:
 * 해당 격국 전용 판정 이유 enum.
 *
 * 예:
 *
 * PatternAssessment<DirectOfficerPatternReason>
 */
public record PatternAssessment<R extends Enum<R>>(

        HeavenlyStem dayMaster,

        MonthCommandPatternCandidate patternCandidate,

        PatternStatus status,

        Set<R> reasons

) {

    public PatternAssessment {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                patternCandidate,
                "격국 후보는 필수입니다."
        );

        Objects.requireNonNull(
                status,
                "격국 상태는 필수입니다."
        );

        reasons =
                reasons == null
                        ? Set.of()
                        : Set.copyOf(reasons);

        if (
                patternCandidate.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "격국 후보의 일간이 분석 대상과 일치하지 않습니다."
            );
        }

        if (reasons.isEmpty()) {

            throw new IllegalArgumentException(
                    "격국 판정에는 최소 하나의 판정 이유가 필요합니다."
            );
        }
    }

    public boolean formed() {

        return status
                == PatternStatus.FORMED;
    }

    public boolean broken() {

        return status
                == PatternStatus.BROKEN;
    }

    public boolean formedWithDefect() {

        return status
                == PatternStatus.FORMED_WITH_DEFECT;
    }

    public boolean brokenWithRescue() {

        return status
                == PatternStatus.BROKEN_WITH_RESCUE;
    }

    public boolean reviewRequired() {

        return status
                == PatternStatus.REVIEW_REQUIRED;
    }

    /**
     * 깨끗한 成格뿐 아니라
     * 敗中有救까지 포함해
     * 실제 사용 가능한 격국 Context인지.
     *
     * 成中有敗는 흠이 남아 있으므로
     * 여기서는 false로 둔다.
     */
    public boolean usableFormation() {

        return formed()
                ||
                brokenWithRescue();
    }
}