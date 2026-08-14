package com.dajeom.analyzer;

import com.dajeom.model.DecadeLuckAnalysis;
import com.dajeom.model.DecadeLuckCandidate;
import com.dajeom.model.DecadeLuckPeriod;
import com.dajeom.model.DecadeLuckSelectionAnalysis;
import com.dajeom.model.LuckReferenceContext;
import com.dajeom.type.LuckCandidateCertainty;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 16차-2.
 *
 * 현재 조회 기준에서
 * 대운 Candidate 전체를 선택하는 Analyzer.
 *
 * =================================================
 *
 * 대운 possible window:
 *
 * [earliestStart, latestEnd)
 *
 * =================================================
 *
 * 대운 definite window:
 *
 * [latestStart, earliestEnd)
 *
 * =================================================
 *
 * EXACT_DATE_TIME:
 *
 * 정확한 시각이 possible 안에 있으면 Candidate.
 *
 * 정확한 시각이 definite 안에도 있으면 DEFINITE.
 *
 * =================================================
 *
 * DATE_ONLY:
 *
 * 해당 날짜 전체와 possible window가
 * 일부라도 겹치면 Candidate.
 *
 * 해당 날짜 전체가 definite window 안에
 * 완전히 포함되어야 DEFINITE.
 *
 * =================================================
 *
 * 시스템은 이전/다음 대운 중
 * 하나를 임의 선택하지 않는다.
 */
@Component
public class DecadeLuckCandidateAnalyzer {

    /**
     * =================================================
     * 공식 16-2 API
     * =================================================
     */
    public DecadeLuckSelectionAnalysis analyze(

            LuckReferenceContext referenceContext,

            DecadeLuckAnalysis decadeLuckAnalysis

    ) {

        Objects.requireNonNull(
                referenceContext,
                "16-2에는 LuckReferenceContext가 필요합니다."
        );

        Objects.requireNonNull(
                decadeLuckAnalysis,
                "16-2에는 DecadeLuckAnalysis가 필요합니다."
        );

        /*
         * =================================================
         * 대운 Sequence 사용 불가
         * =================================================
         */

        if (
                !decadeLuckAnalysis
                        .sequenceAvailable()
        ) {

            return new DecadeLuckSelectionAnalysis(
                    referenceContext,
                    decadeLuckAnalysis,
                    List.of(),
                    false,
                    resolveUnavailableReason(
                            decadeLuckAnalysis
                    )
            );
        }

        List<DecadeLuckPeriod> periods =
                decadeLuckAnalysis
                        .periods();

        if (
                periods.isEmpty()
        ) {

            throw new IllegalStateException(
                    "대운 Sequence가 available인데 DecadeLuckPeriod가 존재하지 않습니다."
            );
        }

        List<DecadeLuckCandidate> candidates =
                new ArrayList<>();

        for (
                DecadeLuckPeriod period
                : periods
        ) {

            /*
             * =================================================
             * POSSIBLE WINDOW
             * =================================================
             */

            ZonedDateTime possibleStart =
                    period
                            .earliestStartDateTime()
                            .withZoneSameInstant(
                                    referenceContext
                                            .zoneId()
                            );

            ZonedDateTime possibleEnd =
                    period
                            .latestEndDateTimeExclusive()
                            .withZoneSameInstant(
                                    referenceContext
                                            .zoneId()
                            );

            if (
                    !possibleEnd
                            .isAfter(
                                    possibleStart
                            )
            ) {

                throw new IllegalStateException(
                        "대운 possible window가 유효하지 않습니다: "
                                + period.sequenceNumber()
                );
            }

            /*
             * 조회 기준과 possible window가
             * 전혀 겹치지 않으면 Candidate가 아니다.
             */

            if (
                    !referenceContext
                            .possiblyIntersects(
                                    possibleStart,
                                    possibleEnd
                            )
            ) {

                continue;
            }

            /*
             * =================================================
             * DEFINITE WINDOW
             * =================================================
             */

            ZonedDateTime definiteStart =
                    period
                            .latestStartDateTime()
                            .withZoneSameInstant(
                                    referenceContext
                                            .zoneId()
                            );

            ZonedDateTime definiteEnd =
                    period
                            .earliestEndDateTimeExclusive()
                            .withZoneSameInstant(
                                    referenceContext
                                            .zoneId()
                            );

            boolean hasDefiniteWindow =
                    definiteEnd
                            .isAfter(
                                    definiteStart
                            );

            LuckCandidateCertainty certainty =
                    LuckCandidateCertainty.POSSIBLE;

            /*
             * definite window가 존재하고
             * 현재 조회 기준 전체가 그 안에 들어갈 때만
             * DEFINITE.
             */

            if (
                    hasDefiniteWindow
                            && referenceContext
                            .definitelyContainedIn(
                                    definiteStart,
                                    definiteEnd
                            )
            ) {

                certainty =
                        LuckCandidateCertainty.DEFINITE;
            }

            candidates.add(
                    new DecadeLuckCandidate(
                            period,
                            certainty
                    )
            );
        }

        candidates.sort(
                Comparator.comparingInt(
                        DecadeLuckCandidate
                                ::sequenceNumber
                )
        );

        /*
         * =================================================
         * Candidate가 없는 경우
         * =================================================
         *
         * 정상적인 상태는:
         *
         * - 첫 대운 시작 전
         * - 생성한 대운 Sequence 이후
         *
         * 둘 중 하나다.
         *
         * 그 사이인데 Candidate가 없다면
         * 대운 Interval 모델에 Gap이 있다는 뜻이다.
         */

        if (
                candidates.isEmpty()
        ) {

            validateNoCandidateState(
                    referenceContext,
                    periods
            );
        }

        /*
         * =================================================
         * DEFINITE는 최대 하나
         * =================================================
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

            throw new IllegalStateException(
                    "동일 조회 기준에서 확정 대운 Candidate가 2개 이상 생성되었습니다."
            );
        }

        return new DecadeLuckSelectionAnalysis(
                referenceContext,
                decadeLuckAnalysis,
                candidates,
                true,
                ""
        );
    }

    /*
     * =================================================
     * Candidate 0개 상태 검증
     * =================================================
     */

    private void validateNoCandidateState(

            LuckReferenceContext referenceContext,

            List<DecadeLuckPeriod> periods

    ) {

        DecadeLuckPeriod first =
                periods.get(0);

        DecadeLuckPeriod last =
                periods.get(
                        periods.size() - 1
                );

        ZonedDateTime firstPossibleStart =
                first
                        .earliestStartDateTime()
                        .withZoneSameInstant(
                                referenceContext.zoneId()
                        );

        ZonedDateTime lastPossibleEnd =
                last
                        .latestEndDateTimeExclusive()
                        .withZoneSameInstant(
                                referenceContext.zoneId()
                        );

        boolean beforeFirst;

        boolean afterLast;

        if (
                referenceContext.exact()
        ) {

            ZonedDateTime exact =
                    referenceContext
                            .requireExactDateTime();

            beforeFirst =
                    exact.isBefore(
                            firstPossibleStart
                    );

            afterLast =
                    !exact.isBefore(
                            lastPossibleEnd
                    );

        } else {

            /*
             * DATE_ONLY 전체가 첫 대운 이전.
             */
            beforeFirst =
                    !referenceContext
                            .possibleEndExclusive()
                            .isAfter(
                                    firstPossibleStart
                            );

            /*
             * DATE_ONLY 전체가 마지막 생성 대운 이후.
             */
            afterLast =
                    !referenceContext
                            .possibleStartInclusive()
                            .isBefore(
                                    lastPossibleEnd
                            );
        }

        if (
                beforeFirst
                        || afterLast
        ) {

            return;
        }

        throw new IllegalStateException(
                "조회 기준이 생성된 대운 범위 안에 있으나 대응하는 DecadeLuckCandidate를 찾지 못했습니다."
        );
    }

    /*
     * =================================================
     * 대운 분석 불가 사유
     * =================================================
     */

    private String resolveUnavailableReason(
            DecadeLuckAnalysis decadeLuckAnalysis
    ) {

        return decadeLuckAnalysis
                .unavailableReason()
                .orElse(
                        "대운 Sequence가 확정되지 않아 현재 대운 Candidate를 선택할 수 없습니다."
                );
    }
}