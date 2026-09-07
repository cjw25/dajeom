package com.dajeom.domain;

import java.util.Objects;
import java.util.Set;

/**
 * 일반 8격 Rule 실행 후의
 * 통합 판정 결과.
 *
 * =================================================
 *
 * 각 격 전용 Rule:
 *
 * PatternAssessment<DirectOfficerPatternReason>
 * PatternAssessment<WealthPatternReason>
 * PatternAssessment<ResourcePatternReason>
 * ...
 *
 * 를 이후 계층에서 공통으로 읽을 수 있도록
 * 변환한 결과다.
 *
 * =================================================
 *
 * reasonCodes 예:
 *
 * WEALTH_PATTERN
 * SEVEN_KILLINGS_EXPOSED
 * SEVEN_KILLINGS_UNRESOLVED
 * WEALTH_PATTERN_BROKEN
 *
 * =================================================
 *
 * reasonCode는 점수나 confidence가 아니다.
 * 명리 Rule이 실제로 어떤 조건을 밟았는지
 * 추적하기 위한 코드다.
 */
public record StandardPatternAssessment(

        HeavenlyStem dayMaster,

        MonthCommandPatternCandidate patternCandidate,

        PatternStatus status,

        Set<String> reasonCodes

) {

    public StandardPatternAssessment {

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
                "격국 판정 상태는 필수입니다."
        );

        reasonCodes =
                reasonCodes == null
                        ? Set.of()
                        : Set.copyOf(
                        reasonCodes
                );

        if (
                patternCandidate.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "통합 격국 판정의 일간이 격국 후보와 일치하지 않습니다."
            );
        }

        if (
                !patternCandidate
                        .standardEightPatternCandidate()
        ) {

            throw new IllegalArgumentException(
                    "StandardPatternAssessment에는 일반 8격 후보만 들어갈 수 있습니다."
            );
        }

        if (reasonCodes.isEmpty()) {

            throw new IllegalArgumentException(
                    "통합 격국 판정에는 최소 하나의 reasonCode가 필요합니다."
            );
        }

        for (
                String reasonCode
                : reasonCodes
        ) {

            if (
                    reasonCode == null
                            ||
                            reasonCode.isBlank()
            ) {

                throw new IllegalArgumentException(
                        "격국 판정 reasonCode는 비어 있을 수 없습니다."
                );
            }
        }
    }

    public MonthCommandPatternCandidateType patternType() {

        return patternCandidate
                .candidateType();
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
}