package com.dajeom.analyzer;

import com.dajeom.calculator.AnnualLuckPeriodCalculator;
import com.dajeom.model.AnnualLuckCandidate;
import com.dajeom.model.AnnualLuckPeriod;
import com.dajeom.model.AnnualLuckSelectionAnalysis;
import com.dajeom.model.LuckReferenceContext;
import com.dajeom.type.LuckCandidateCertainty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 16차-3.
 *
 * 현재 조회 기준에서
 * 세운 Candidate를 선택한다.
 *
 * =================================================
 *
 * 세운은:
 *
 * [입춘(targetYear),
 *  입춘(targetYear + 1))
 *
 * 구간이다.
 *
 * =================================================
 *
 * civil year가 Y이면
 * 해당 civil date가 속할 수 있는 세운은:
 *
 * Y - 1 세운
 * 또는
 * Y 세운
 *
 * 두 개뿐이다.
 *
 * 따라서 두 AnnualLuckPeriod만 생성해서
 * 현재 LuckReferenceContext와 교차시킨다.
 *
 * =================================================
 *
 * EXACT_DATE_TIME:
 *
 * 정확한 하나의 세운
 * ->
 * DEFINITE
 *
 * =================================================
 *
 * DATE_ONLY:
 *
 * 일반 날짜
 * ->
 * 하나의 DEFINITE
 *
 * 입춘 당일
 * ->
 * 이전 세운 POSSIBLE
 * +
 * 다음 세운 POSSIBLE
 */
@Component
public class AnnualLuckCandidateAnalyzer {

    private final AnnualLuckPeriodCalculator
            annualLuckPeriodCalculator;

    public AnnualLuckCandidateAnalyzer(
            AnnualLuckPeriodCalculator
                    annualLuckPeriodCalculator
    ) {

        this.annualLuckPeriodCalculator =
                Objects.requireNonNull(
                        annualLuckPeriodCalculator,
                        "AnnualLuckPeriodCalculator가 필요합니다."
                );
    }

    /**
     * =================================================
     * 공식 16-3 API
     * =================================================
     */
    public AnnualLuckSelectionAnalysis analyze(
            LuckReferenceContext referenceContext
    ) {

        Objects.requireNonNull(
                referenceContext,
                "16-3에는 LuckReferenceContext가 필요합니다."
        );

        int civilYear =
                referenceContext
                        .referenceDate()
                        .getYear();

        /*
         * =================================================
         * 조회 civil year Y에서 가능한 명리연도:
         *
         * Y - 1
         * Y
         *
         * =================================================
         *
         * 예:
         *
         * 2027-01-15
         *
         * -> 2026 세운 가능
         *
         * 2027-08-15
         *
         * -> 2027 세운 가능
         *
         * 2027 입춘 당일 DATE_ONLY
         *
         * -> 2026 / 2027 둘 다 가능
         */

        AnnualLuckPeriod previousAnnualPeriod =
                annualLuckPeriodCalculator
                        .calculate(
                                civilYear - 1,
                                referenceContext.zoneId()
                        );

        AnnualLuckPeriod currentAnnualPeriod =
                annualLuckPeriodCalculator
                        .calculate(
                                civilYear,
                                referenceContext.zoneId()
                        );

        List<AnnualLuckPeriod> periods =
                List.of(
                        previousAnnualPeriod,
                        currentAnnualPeriod
                );

        List<AnnualLuckCandidate> candidates =
                new ArrayList<>(
                        2
                );

        for (
                AnnualLuckPeriod period
                : periods
        ) {

            /*
             * =================================================
             * 현재 조회 기준과 세운 Period가
             * 조금이라도 교차하는지 확인.
             * =================================================
             */

            boolean possible =
                    referenceContext
                            .possiblyIntersects(
                                    period.startDateTimeInclusive(),
                                    period.endDateTimeExclusive()
                            );

            if (
                    !possible
            ) {

                continue;
            }

            /*
             * =================================================
             * 조회 기준 전체가 해당 세운에
             * 완전히 포함되는지 확인.
             *
             * EXACT라면 정확한 시각 하나.
             *
             * DATE_ONLY라면 civil-day 전체.
             * =================================================
             */

            boolean definite =
                    referenceContext
                            .definitelyContainedIn(
                                    period.startDateTimeInclusive(),
                                    period.endDateTimeExclusive()
                            );

            LuckCandidateCertainty certainty =
                    definite
                            ? LuckCandidateCertainty.DEFINITE
                            : LuckCandidateCertainty.POSSIBLE;

            candidates.add(
                    new AnnualLuckCandidate(
                            period,
                            certainty
                    )
            );
        }

        candidates.sort(
                Comparator.comparingInt(
                        AnnualLuckCandidate
                                ::targetYear
                )
        );

        /*
         * =================================================
         * 세운 시간축에는 Gap이 없어야 한다.
         * =================================================
         */

        if (
                candidates.isEmpty()
        ) {

            throw new IllegalStateException(
                    "현재 조회 기준에 대응하는 세운 Candidate를 찾지 못했습니다."
            );
        }

        if (
                candidates.size() > 2
        ) {

            throw new IllegalStateException(
                    "현재 조회 기준에서 세운 Candidate가 2개를 초과했습니다."
            );
        }

        /*
         * =================================================
         * 정확한 시각이면 하나만 나와야 한다.
         * =================================================
         */

        if (
                referenceContext.exact()
                        && candidates.size() != 1
        ) {

            throw new IllegalStateException(
                    "정확한 조회시각에서 세운 Candidate가 하나로 결정되지 않았습니다."
            );
        }

        /*
         * =================================================
         * exact라면 반환 Candidate는 반드시
         * DEFINITE여야 한다.
         * =================================================
         */

        if (
                referenceContext.exact()
                        && !candidates
                        .get(0)
                        .definite()
        ) {

            throw new IllegalStateException(
                    "정확한 조회시각의 세운이 DEFINITE로 판정되지 않았습니다."
            );
        }

        /*
         * =================================================
         * Candidate 2개라면 둘 다 POSSIBLE.
         *
         * 하나가 DEFINITE라는 것은
         * 조회 기준 전체가 한 Period에 속한다는 뜻이므로
         * 다른 Candidate가 동시에 존재할 수 없다.
         * =================================================
         */

        if (
                candidates.size() == 2
        ) {

            boolean definiteExists =
                    candidates
                            .stream()
                            .anyMatch(
                                    AnnualLuckCandidate
                                            ::definite
                            );

            if (
                    definiteExists
            ) {

                throw new IllegalStateException(
                        "복수 세운 Candidate 중 DEFINITE Candidate가 존재합니다."
                );
            }
        }

        return new AnnualLuckSelectionAnalysis(
                referenceContext,
                candidates
        );
    }
}