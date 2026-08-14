package com.dajeom.model;

import com.dajeom.type.LuckCandidateCertainty;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 16차-2.
 *
 * 현재 조회 기준에서 가능한 대운 Candidate 전체.
 *
 * =================================================
 *
 * 예 1.
 *
 * 정확한 시각
 * +
 * 정확한 교운
 *
 * ->
 *
 * Candidate 1개
 * DEFINITE
 *
 * =================================================
 *
 * 예 2.
 *
 * 출생시간 미상 교운 구간
 *
 * ->
 *
 * 이전 대운 POSSIBLE
 * 다음 대운 POSSIBLE
 *
 * =================================================
 *
 * 예 3.
 *
 * 정확한 교운 날짜이지만
 * 사용자가 날짜만 입력
 *
 * ->
 *
 * 이전 대운 POSSIBLE
 * 다음 대운 POSSIBLE
 *
 * =================================================
 *
 * Candidate가 0개라고 반드시 오류는 아니다.
 *
 * 첫 대운 시작 전일 수도 있고,
 * 생성된 대운 Sequence 이후일 수도 있다.
 */
public record DecadeLuckSelectionAnalysis(

        LuckReferenceContext referenceContext,

        DecadeLuckAnalysis decadeLuckAnalysis,

        List<DecadeLuckCandidate> candidates,

        boolean selectionAvailable,

        String unavailableReason

) {

    public DecadeLuckSelectionAnalysis {

        Objects.requireNonNull(
                referenceContext,
                "대운 선택에는 LuckReferenceContext가 필요합니다."
        );

        Objects.requireNonNull(
                decadeLuckAnalysis,
                "대운 선택에는 DecadeLuckAnalysis가 필요합니다."
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
                                                "대운 Candidate는 null일 수 없습니다."
                                        )
                        )
                        .sorted(
                                Comparator.comparingInt(
                                        DecadeLuckCandidate
                                                ::sequenceNumber
                                )
                        )
                        .toList();

        unavailableReason =
                unavailableReason == null
                        ? ""
                        : unavailableReason.trim();

        /*
         * =================================================
         * 선택 가능
         * =================================================
         */

        if (
                selectionAvailable
        ) {

            if (
                    !decadeLuckAnalysis
                            .sequenceAvailable()
            ) {

                throw new IllegalArgumentException(
                        "대운 Sequence가 사용 불가능한데 대운 선택을 available로 만들 수 없습니다."
                );
            }

            if (
                    !unavailableReason
                            .isBlank()
            ) {

                throw new IllegalArgumentException(
                        "대운 선택 가능 상태에는 unavailableReason이 없어야 합니다."
                );
            }

            if (
                    decadeLuckAnalysis
                            .periods()
                            .isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "대운 Sequence가 available인데 DecadeLuckPeriod가 존재하지 않습니다."
                );
            }

            /*
             * Candidate는 반드시
             * 현재 DecadeLuckAnalysis에 존재하는
             * 동일 Period 객체여야 한다.
             */
            for (
                    DecadeLuckCandidate candidate
                    : candidates
            ) {

                boolean known =
                        decadeLuckAnalysis
                                .periods()
                                .stream()
                                .anyMatch(
                                        period ->
                                                period
                                                        == candidate.period()
                                );

                if (
                        !known
                ) {

                    throw new IllegalArgumentException(
                            "현재 대운 Sequence에 존재하지 않는 Candidate가 포함되었습니다: "
                                    + candidate.sequenceNumber()
                    );
                }
            }

            /*
             * 동일 대운 중복 금지.
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
                                "동일 DecadeLuckPeriod가 Candidate에 중복되었습니다."
                        );
                    }
                }
            }

            /*
             * 확정 대운은 최대 하나.
             */
            long definiteCount =
                    candidates
                            .stream()
                            .filter(
                                    DecadeLuckCandidate
                                            ::definite
                            )
                            .count();

            if (
                    definiteCount > 1
            ) {

                throw new IllegalArgumentException(
                        "동일 조회 기준에서 확정 대운 Candidate가 2개 이상 존재할 수 없습니다."
                );
            }
        }

        /*
         * =================================================
         * 선택 불가
         * =================================================
         */

        if (
                !selectionAvailable
        ) {

            if (
                    !candidates
                            .isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "대운 선택 불가 상태에서는 Candidate를 생성할 수 없습니다."
                );
            }

            if (
                    unavailableReason
                            .isBlank()
            ) {

                throw new IllegalArgumentException(
                        "대운 선택 불가 사유가 필요합니다."
                );
            }
        }
    }

    /*
     * =================================================
     * 기본
     * =================================================
     */

    public boolean hasCandidates() {

        return !candidates
                .isEmpty();
    }

    public int candidateCount() {

        return candidates
                .size();
    }

    public boolean multipleCandidates() {

        return candidateCount()
                > 1;
    }

    /*
     * =================================================
     * 확정 대운
     * =================================================
     */

    public Optional<DecadeLuckCandidate>
    definiteCandidate() {

        if (
                !selectionAvailable
        ) {

            return Optional.empty();
        }

        return candidates
                .stream()
                .filter(
                        DecadeLuckCandidate
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

    public List<DecadeLuckCandidate>
    possibleOnlyCandidates() {

        if (
                !selectionAvailable
        ) {

            return List.of();
        }

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
     * Candidate 검색
     * =================================================
     */

    public Optional<DecadeLuckCandidate>
    candidateOfSequence(
            int sequenceNumber
    ) {

        if (
                sequenceNumber < 1
                        || !selectionAvailable
        ) {

            return Optional.empty();
        }

        return candidates
                .stream()
                .filter(
                        candidate ->
                                candidate
                                        .sequenceNumber()
                                        == sequenceNumber
                )
                .findFirst();
    }

    /*
     * =================================================
     * 불확실성
     * =================================================
     *
     * POSSIBLE Candidate가 하나라도 있으면
     * 현재 정보만으로 대운 상태가 완전히 확정된 것이 아니다.
     */

    public boolean uncertaintyExists() {

        return selectionAvailable
                && hasPossibleOnlyCandidate();
    }

    /*
     * =================================================
     * 첫 대운 이전
     * =================================================
     */

    public boolean beforeFirstDecadeLuck() {

        if (
                !selectionAvailable
                        || hasCandidates()
        ) {

            return false;
        }

        DecadeLuckPeriod first =
                decadeLuckAnalysis
                        .periods()
                        .get(0);

        ZonedDateTime firstPossibleStart =
                first
                        .earliestStartDateTime()
                        .withZoneSameInstant(
                                referenceContext.zoneId()
                        );

        if (
                referenceContext.exact()
        ) {

            return referenceContext
                    .requireExactDateTime()
                    .isBefore(
                            firstPossibleStart
                    );
        }

        /*
         * DATE_ONLY 전체가
         * 첫 대운 possible start 이전이어야 한다.
         */

        return !referenceContext
                .possibleEndExclusive()
                .isAfter(
                        firstPossibleStart
                );
    }

    /*
     * =================================================
     * 생성 대운 범위 이후
     * =================================================
     */

    public boolean afterGeneratedDecadeRange() {

        if (
                !selectionAvailable
                        || hasCandidates()
        ) {

            return false;
        }

        List<DecadeLuckPeriod> periods =
                decadeLuckAnalysis
                        .periods();

        DecadeLuckPeriod last =
                periods.get(
                        periods.size() - 1
                );

        ZonedDateTime lastPossibleEnd =
                last
                        .latestEndDateTimeExclusive()
                        .withZoneSameInstant(
                                referenceContext.zoneId()
                        );

        if (
                referenceContext.exact()
        ) {

            return !referenceContext
                    .requireExactDateTime()
                    .isBefore(
                            lastPossibleEnd
                    );
        }

        return !referenceContext
                .possibleStartInclusive()
                .isBefore(
                        lastPossibleEnd
                );
    }

    /*
     * =================================================
     * 선택 불가 사유 Optional
     * =================================================
     */

    public Optional<String>
    unavailableReasonOptional() {

        if (
                selectionAvailable
                        || unavailableReason
                        .isBlank()
        ) {

            return Optional.empty();
        }

        return Optional.of(
                unavailableReason
        );
    }
}