package com.dajeom.model;

import com.dajeom.type.LuckCandidateCertainty;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 16차-3.
 *
 * 현재 조회 기준에서 가능한
 * 세운 Candidate 전체.
 *
 * =================================================
 *
 * 일반적인 EXACT_DATE_TIME:
 *
 * Candidate 1개
 * ->
 * DEFINITE
 *
 * =================================================
 *
 * 일반적인 DATE_ONLY:
 *
 * 하루 전체가 하나의 세운 안에 있으면
 *
 * Candidate 1개
 * ->
 * DEFINITE
 *
 * =================================================
 *
 * 입춘 당일 DATE_ONLY:
 *
 * 이전 세운 POSSIBLE
 * +
 * 새 세운 POSSIBLE
 *
 * ->
 * 확정 Candidate 없음.
 *
 * =================================================
 *
 * 세운은 연속적인 시간축이므로
 * 정상적인 조회라면 Candidate는
 * 반드시 1개 또는 2개다.
 */
public record AnnualLuckSelectionAnalysis(

        LuckReferenceContext referenceContext,

        List<AnnualLuckCandidate> candidates

) {

    public AnnualLuckSelectionAnalysis {

        Objects.requireNonNull(
                referenceContext,
                "세운 선택에는 LuckReferenceContext가 필요합니다."
        );

        candidates =
                candidates == null
                        ? List.of()
                        : candidates
                        .stream()
                        .peek(
                                candidate ->
                                        Objects.requireNonNull(
                                                candidate,
                                                "세운 Candidate는 null일 수 없습니다."
                                        )
                        )
                        .sorted(
                                Comparator.comparingInt(
                                        AnnualLuckCandidate
                                                ::targetYear
                                )
                        )
                        .toList();

        /*
         * =================================================
         * 세운 Candidate는 반드시 존재.
         * =================================================
         */

        if (
                candidates.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "현재 조회 기준에 대응하는 세운 Candidate가 필요합니다."
            );
        }

        /*
         * =================================================
         * 한 civil date가 걸칠 수 있는 세운은 최대 2개.
         * =================================================
         */

        if (
                candidates.size() > 2
        ) {

            throw new IllegalArgumentException(
                    "동일 조회 기준에서 세운 Candidate가 2개를 초과할 수 없습니다."
            );
        }

        /*
         * =================================================
         * ZoneId
         * =================================================
         */

        for (
                AnnualLuckCandidate candidate
                : candidates
        ) {

            if (
                    !candidate
                            .zoneId()
                            .equals(
                                    referenceContext.zoneId()
                            )
            ) {

                throw new IllegalArgumentException(
                        "세운 Candidate와 LuckReferenceContext의 ZoneId가 일치하지 않습니다."
                );
            }
        }

        /*
         * =================================================
         * targetYear 중복 금지.
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
                                .targetYear()
                                == candidates
                                .get(second)
                                .targetYear()
                ) {

                    throw new IllegalArgumentException(
                            "동일 명리연도의 세운 Candidate가 중복되었습니다."
                    );
                }
            }
        }

        /*
         * =================================================
         * 확정 Candidate는 최대 하나.
         * =================================================
         */

        long definiteCount =
                candidates
                        .stream()
                        .filter(
                                AnnualLuckCandidate
                                        ::definite
                        )
                        .count();

        if (
                definiteCount > 1
        ) {

            throw new IllegalArgumentException(
                    "동일 조회 기준에서 확정 세운 Candidate가 2개 이상 존재할 수 없습니다."
            );
        }

        /*
         * =================================================
         * 정확한 시각이면 반드시 하나의 세운이 확정된다.
         * =================================================
         */

        if (
                referenceContext.exact()
        ) {

            if (
                    candidates.size() != 1
            ) {

                throw new IllegalArgumentException(
                        "EXACT_DATE_TIME에서는 정확히 하나의 세운 Candidate가 필요합니다."
                );
            }

            if (
                    !candidates
                            .get(0)
                            .definite()
            ) {

                throw new IllegalArgumentException(
                        "EXACT_DATE_TIME의 세운 Candidate는 DEFINITE여야 합니다."
                );
            }
        }

        /*
         * =================================================
         * Candidate가 2개면 둘 중 하나를
         * definite로 강제할 수 없다.
         *
         * 현재 구조에서 2개가 나오는 이유는
         * DATE_ONLY 입춘 경계이기 때문이다.
         * =================================================
         */

        if (
                candidates.size() == 2
                        && definiteCount != 0
        ) {

            throw new IllegalArgumentException(
                    "복수 세운 Candidate 상태에서는 확정 Candidate가 존재하면 안 됩니다."
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
     * 확정 세운
     * =================================================
     */

    public Optional<AnnualLuckCandidate>
    definiteCandidate() {

        return candidates
                .stream()
                .filter(
                        AnnualLuckCandidate
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
     * 가능 Candidate
     * =================================================
     */

    public List<AnnualLuckCandidate>
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

    public boolean hasPossibleOnlyCandidate() {

        return !possibleOnlyCandidates()
                .isEmpty();
    }

    /*
     * =================================================
     * 특정 명리연도
     * =================================================
     */

    public Optional<AnnualLuckCandidate>
    candidateOfYear(
            int targetYear
    ) {

        return candidates
                .stream()
                .filter(
                        candidate ->
                                candidate.targetYear()
                                        == targetYear
                )
                .findFirst();
    }

    /*
     * =================================================
     * 불확실성
     * =================================================
     */

    public boolean uncertaintyExists() {

        return multipleCandidates()
                || hasPossibleOnlyCandidate();
    }

    /*
     * =================================================
     * 이전/다음 Candidate
     * =================================================
     *
     * 입춘일 DATE_ONLY처럼 Candidate가 2개일 때
     * 시간순으로 이전/다음 세운을 확인할 수 있다.
     */

    public Optional<AnnualLuckCandidate>
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

    public Optional<AnnualLuckCandidate>
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
}