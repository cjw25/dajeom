package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 월령 기반 격국 후보 전체.
 *
 * =================================================
 *
 * primaryCandidate
 *
 * → 월령 MAIN을 기준으로 한 본래 후보
 *
 * =================================================
 *
 * exposedAlternativeCandidates
 *
 * → 월령 EARLY/MIDDLE 중
 *   실제 천간에 투출한 후보
 *
 * → 격국 변화 검토용
 *
 * =================================================
 *
 * 아직:
 *
 * - 격국 확정
 * - 성격
 * - 파격
 * - 용신
 *
 * 을 결정하지 않는다.
 */
public record MonthCommandPatternCandidateAnalysis(

        HeavenlyStem dayMaster,

        EarthlyBranch monthBranch,

        MonthCommandPatternCandidate primaryCandidate,

        List<MonthCommandPatternCandidate>
        exposedAlternativeCandidates

) {

    public MonthCommandPatternCandidateAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지는 필수입니다."
        );

        Objects.requireNonNull(
                primaryCandidate,
                "월령 MAIN 격국 후보는 필수입니다."
        );

        exposedAlternativeCandidates =
                exposedAlternativeCandidates == null
                        ? List.of()
                        : List.copyOf(
                        exposedAlternativeCandidates
                );

        if (
                primaryCandidate.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "MAIN 격국 후보의 일간이 일치하지 않습니다."
            );
        }

        if (
                primaryCandidate.monthBranch()
                        != monthBranch
        ) {

            throw new IllegalArgumentException(
                    "MAIN 격국 후보의 월지가 일치하지 않습니다."
            );
        }

        if (
                !primaryCandidate.main()
        ) {

            throw new IllegalArgumentException(
                    "primaryCandidate는 MAIN 지장간이어야 합니다."
            );
        }

        for (
                MonthCommandPatternCandidate candidate
                : exposedAlternativeCandidates
        ) {

            Objects.requireNonNull(
                    candidate,
                    "변화 격국 후보 목록에 null이 포함될 수 없습니다."
            );

            if (
                    candidate.dayMaster()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "변화 격국 후보의 일간이 일치하지 않습니다."
                );
            }

            if (
                    candidate.monthBranch()
                            != monthBranch
            ) {

                throw new IllegalArgumentException(
                        "변화 격국 후보의 월지가 일치하지 않습니다."
                );
            }

            if (
                    candidate.main()
            ) {

                throw new IllegalArgumentException(
                        "변화 격국 후보에는 MAIN 후보를 중복 포함하지 않습니다."
                );
            }

            if (
                    !candidate.exposed()
            ) {

                throw new IllegalArgumentException(
                        "MAIN이 아닌 변화 후보는 실제 천간 투출이 필요합니다."
                );
            }
        }
    }

    public boolean primaryExposed() {

        return primaryCandidate
                .exposed();
    }

    public boolean hasExposedAlternativeCandidate() {

        return !exposedAlternativeCandidates
                .isEmpty();
    }

    /**
     * 격국 변화 가능성을 검토해야 하는가.
     *
     * 이것이 true라고
     * 본래 격이 실제로 변경된 것은 아니다.
     */
    public boolean requiresPatternChangeReview() {

        return hasExposedAlternativeCandidate();
    }

    /**
     * 현재 단계에서 검토해야 하는 후보 전체.
     *
     * MAIN
     * +
     * 투출된 비MAIN 지장간.
     */
    public List<MonthCommandPatternCandidate>
    allReviewCandidates() {

        return java.util.stream.Stream
                .concat(
                        java.util.stream.Stream.of(
                                primaryCandidate
                        ),
                        exposedAlternativeCandidates
                                .stream()
                )
                .toList();
    }

    public boolean companionMonthCommand() {

        return primaryCandidate
                .candidateType()
                .companionMonthCommand();
    }
}