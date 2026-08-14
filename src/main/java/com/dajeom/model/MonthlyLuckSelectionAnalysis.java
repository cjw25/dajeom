package com.dajeom.model;

import com.dajeom.type.LuckCandidateCertainty;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 16차-4.
 *
 * 현재 조회 기준에서 가능한
 * 월운 Candidate 전체.
 *
 * =================================================
 *
 * 일반 EXACT_DATE_TIME:
 *
 * 월운 Candidate 1개
 * -> DEFINITE
 *
 * =================================================
 *
 * 일반 DATE_ONLY:
 *
 * 하루 전체가 하나의 절입 월운에 속하면:
 *
 * 월운 Candidate 1개
 * -> DEFINITE
 *
 * =================================================
 *
 * 절입일 DATE_ONLY:
 *
 * 이전 월운 POSSIBLE
 * +
 * 다음 월운 POSSIBLE
 *
 * =================================================
 *
 * 입춘일 DATE_ONLY:
 *
 * 이전 명리연도 마지막 丑월 POSSIBLE
 * +
 * 새 명리연도 첫 寅월 POSSIBLE
 *
 * =================================================
 *
 * 정상적인 civil-day 하나가
 * 월운 Candidate 3개 이상에 걸릴 수는 없다.
 */
public record MonthlyLuckSelectionAnalysis(

        LuckReferenceContext referenceContext,

        AnnualLuckSelectionAnalysis annualSelectionAnalysis,

        List<MonthlyLuckCandidate> candidates

) {

    public MonthlyLuckSelectionAnalysis {

        Objects.requireNonNull(
                referenceContext,
                "월운 선택에는 LuckReferenceContext가 필요합니다."
        );

        Objects.requireNonNull(
                annualSelectionAnalysis,
                "월운 선택에는 AnnualLuckSelectionAnalysis가 필요합니다."
        );

        /*
         * =================================================
         * 동일 Reference Context
         * =================================================
         */

        if (
                annualSelectionAnalysis.referenceContext()
                        != referenceContext
        ) {

            throw new IllegalArgumentException(
                    "세운 선택과 월운 선택이 서로 다른 LuckReferenceContext를 참조합니다."
            );
        }

        candidates =
                candidates == null
                        ? List.of()
                        : candidates
                        .stream()
                        .peek(
                                candidate ->
                                        Objects.requireNonNull(
                                                candidate,
                                                "월운 Candidate는 null일 수 없습니다."
                                        )
                        )
                        .sorted(
                                Comparator.comparing(
                                        candidate ->
                                                candidate
                                                        .startDateTimeInclusive()
                                                        .toInstant()
                                )
                        )
                        .toList();

        /*
         * =================================================
         * 월운 시간축에는 Gap이 없어야 한다.
         * =================================================
         */

        if (
                candidates.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "현재 조회 기준에 대응하는 월운 Candidate가 필요합니다."
            );
        }

        /*
         * =================================================
         * 하나의 날짜/시각은 최대 두 월운에 걸친다.
         * =================================================
         */

        if (
                candidates.size() > 2
        ) {

            throw new IllegalArgumentException(
                    "동일 조회 기준에서 월운 Candidate가 2개를 초과할 수 없습니다."
            );
        }

        /*
         * =================================================
         * Candidate의 상위 세운 검증
         * =================================================
         */

        for (
                MonthlyLuckCandidate candidate
                : candidates
        ) {

            boolean knownAnnualCandidate =
                    annualSelectionAnalysis
                            .candidates()
                            .stream()
                            .anyMatch(
                                    annualCandidate ->
                                            annualCandidate
                                                    == candidate
                                                    .annualCandidate()
                            );

            if (
                    !knownAnnualCandidate
            ) {

                throw new IllegalArgumentException(
                        "16-3 세운 선택에 존재하지 않는 AnnualLuckCandidate가 월운 Candidate에 포함되었습니다."
                );
            }

            if (
                    !candidate
                            .zoneId()
                            .equals(
                                    referenceContext
                                            .zoneId()
                            )
            ) {

                throw new IllegalArgumentException(
                        "월운 Candidate와 LuckReferenceContext의 ZoneId가 일치하지 않습니다."
                );
            }

            /*
             * Candidate라면 반드시 조회 기준과
             * 실제 월운 구간이 교차해야 한다.
             */

            if (
                    !referenceContext
                            .possiblyIntersects(
                                    candidate.startDateTimeInclusive(),
                                    candidate.endDateTimeExclusive()
                            )
            ) {

                throw new IllegalArgumentException(
                        "조회 기준과 교차하지 않는 월운이 Candidate에 포함되었습니다."
                );
            }

            /*
             * DEFINITE라면:
             *
             * 1. 상위 세운도 DEFINITE
             * 2. 조회 기준 전체가 월운 안에 존재
             */

            if (
                    candidate.definite()
            ) {

                if (
                        !candidate
                                .annualCandidate()
                                .definite()
                ) {

                    throw new IllegalArgumentException(
                            "확정 월운의 상위 세운 Candidate도 DEFINITE여야 합니다."
                    );
                }

                if (
                        !referenceContext
                                .definitelyContainedIn(
                                        candidate.startDateTimeInclusive(),
                                        candidate.endDateTimeExclusive()
                                )
                ) {

                    throw new IllegalArgumentException(
                            "DEFINITE 월운 Candidate가 조회 기준 전체를 포함하지 않습니다."
                    );
                }
            }
        }

        /*
         * =================================================
         * 동일 MonthlyLuckPeriod 중복 금지
         * =================================================
         */

        for (
                int first = 0;
                first < candidates.size();
                first++
        ) {

            for (
                    int second = first + 1;
                    second < candidates.size();
                    second++
            ) {

                if (
                        candidates
                                .get(first)
                                .period()
                                == candidates
                                .get(second)
                                .period()
                ) {

                    throw new IllegalArgumentException(
                            "동일 MonthlyLuckPeriod가 Candidate에 중복되었습니다."
                    );
                }
            }
        }

        /*
         * =================================================
         * 확정 월운은 최대 하나
         * =================================================
         */

        long definiteCount =
                candidates
                        .stream()
                        .filter(
                                MonthlyLuckCandidate
                                        ::definite
                        )
                        .count();

        if (
                definiteCount > 1
        ) {

            throw new IllegalArgumentException(
                    "동일 조회 기준에서 확정 월운 Candidate가 2개 이상 존재할 수 없습니다."
            );
        }

        /*
         * =================================================
         * EXACT_DATE_TIME
         * =================================================
         *
         * 월운 경계 자체는 deterministic하므로
         * 정확한 시각이면 반드시 월운 하나가 확정된다.
         */

        if (
                referenceContext.exact()
        ) {

            if (
                    candidates.size() != 1
            ) {

                throw new IllegalArgumentException(
                        "EXACT_DATE_TIME에서는 정확히 하나의 월운 Candidate가 필요합니다."
                );
            }

            if (
                    !candidates
                            .get(0)
                            .definite()
            ) {

                throw new IllegalArgumentException(
                        "EXACT_DATE_TIME의 월운 Candidate는 DEFINITE여야 합니다."
                );
            }
        }

        /*
         * =================================================
         * 복수 Candidate 상태
         * =================================================
         *
         * 두 월운이 나온다는 것은
         * DATE_ONLY가 절입 경계를 포함했다는 뜻이다.
         *
         * 따라서 둘 중 하나를 DEFINITE로
         * 강제할 수 없다.
         */

        if (
                candidates.size() == 2
                        && definiteCount != 0
        ) {

            throw new IllegalArgumentException(
                    "복수 월운 Candidate 상태에서는 DEFINITE Candidate가 존재하면 안 됩니다."
            );
        }

        /*
         * =================================================
         * 두 Candidate가 시간적으로 연속하는지 확인
         * =================================================
         */

        if (
                candidates.size() == 2
        ) {

            MonthlyLuckCandidate earlier =
                    candidates.get(0);

            MonthlyLuckCandidate later =
                    candidates.get(1);

            if (
                    !earlier
                            .endDateTimeExclusive()
                            .toInstant()
                            .equals(
                                    later
                                            .startDateTimeInclusive()
                                            .toInstant()
                            )
            ) {

                throw new IllegalArgumentException(
                        "복수 월운 Candidate가 하나의 절입 경계를 공유하지 않습니다."
                );
            }

            validateCandidateContinuity(
                    earlier,
                    later
            );
        }
    }

    /**
     * =================================================
     * 월운 순서 연속성
     * =================================================
     *
     * 같은 세운 안:
     *
     * monthIndex N
     * ->
     * N + 1
     *
     * =================================================
     *
     * 입춘 경계:
     *
     * 이전 세운 monthIndex 11
     * ->
     * 새 세운 monthIndex 0
     */
    private static void validateCandidateContinuity(

            MonthlyLuckCandidate earlier,

            MonthlyLuckCandidate later

    ) {

        if (
                earlier.sajuYear()
                        == later.sajuYear()
        ) {

            if (
                    later.monthIndex()
                            != earlier.monthIndex() + 1
            ) {

                throw new IllegalArgumentException(
                        "같은 세운 내부의 월운 Candidate 순서가 연속되지 않습니다."
                );
            }

            return;
        }

        /*
         * 세운 경계를 넘는 경우.
         */

        if (
                later.sajuYear()
                        != earlier.sajuYear() + 1
        ) {

            throw new IllegalArgumentException(
                    "월운 Candidate의 명리연도가 연속되지 않습니다."
            );
        }

        if (
                earlier.monthIndex() != 11
                        || later.monthIndex() != 0
        ) {

            throw new IllegalArgumentException(
                    "입춘 경계에서는 이전 세운 丑월(monthIndex=11)에서 새 세운 寅월(monthIndex=0)로 이어져야 합니다."
            );
        }
    }

    /*
     * =================================================
     * 기본
     * =================================================
     */

    public int candidateCount() {

        return candidates
                .size();
    }

    public boolean singleCandidate() {

        return candidateCount()
                == 1;
    }

    public boolean multipleCandidates() {

        return candidateCount()
                > 1;
    }

    /*
     * =================================================
     * 확정 월운
     * =================================================
     */

    public Optional<MonthlyLuckCandidate>
    definiteCandidate() {

        return candidates
                .stream()
                .filter(
                        MonthlyLuckCandidate
                                ::definite
                )
                .findFirst();
    }

    public boolean hasDefiniteCandidate() {

        return definiteCandidate()
                .isPresent();
    }

    /*
     * =================================================
     * 가능성만 있는 Candidate
     * =================================================
     */

    public List<MonthlyLuckCandidate>
    possibleOnlyCandidates() {

        return candidates
                .stream()
                .filter(
                        candidate ->
                                candidate.certainty()
                                        == LuckCandidateCertainty
                                        .POSSIBLE
                )
                .toList();
    }

    /*
     * =================================================
     * 불확실성
     * =================================================
     */

    public boolean uncertaintyExists() {

        return multipleCandidates()
                || !possibleOnlyCandidates()
                .isEmpty();
    }

    /*
     * =================================================
     * 특정 세운 + monthIndex 조회
     * =================================================
     */

    public Optional<MonthlyLuckCandidate>
    candidateOf(

            int sajuYear,

            int monthIndex

    ) {

        if (
                monthIndex < 0
                        || monthIndex > 11
        ) {

            return Optional.empty();
        }

        return candidates
                .stream()
                .filter(
                        candidate ->
                                candidate.sajuYear()
                                        == sajuYear

                                        && candidate.monthIndex()
                                        == monthIndex
                )
                .findFirst();
    }

    /*
     * =================================================
     * 시간순 Candidate
     * =================================================
     */

    public Optional<MonthlyLuckCandidate>
    earlierCandidate() {

        if (
                candidates.isEmpty()
        ) {

            return Optional.empty();
        }

        return Optional.of(
                candidates.get(0)
        );
    }

    public Optional<MonthlyLuckCandidate>
    laterCandidate() {

        if (
                candidates.isEmpty()
        ) {

            return Optional.empty();
        }

        return Optional.of(
                candidates.get(
                        candidates.size() - 1
                )
        );
    }

    /*
     * =================================================
     * 입춘 경계를 넘는가?
     * =================================================
     */

    public boolean crossesSajuYearBoundary() {

        if (
                candidates.size() != 2
        ) {

            return false;
        }

        return candidates
                .get(0)
                .sajuYear()
                != candidates
                .get(1)
                .sajuYear();
    }

    /*
     * =================================================
     * 같은 세운 안의 일반 절입 경계인가?
     * =================================================
     */

    public boolean crossesMonthlyBoundaryWithinSameSajuYear() {

        return candidates.size() == 2
                && !crossesSajuYearBoundary();
    }
}