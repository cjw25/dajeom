package com.dajeom.analyzer;

import com.dajeom.calculator.MonthlyLuckPeriodCalculator;
import com.dajeom.model.AnnualLuckCandidate;
import com.dajeom.model.AnnualLuckSelectionAnalysis;
import com.dajeom.model.LuckReferenceContext;
import com.dajeom.model.MonthlyLuckCandidate;
import com.dajeom.model.MonthlyLuckPeriod;
import com.dajeom.model.MonthlyLuckSelectionAnalysis;
import com.dajeom.model.MonthlyLuckSequenceAnalysis;
import com.dajeom.type.LuckCandidateCertainty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 16차-4.
 *
 * 현재 조회 기준에서
 * 월운 Candidate를 선택하는 Analyzer.
 *
 * =================================================
 *
 * 입력:
 *
 * 16-3 AnnualLuckSelectionAnalysis
 *
 * 내부에는 이미:
 *
 * - LuckReferenceContext
 * - 가능한 AnnualLuckCandidate
 *
 * 가 존재한다.
 *
 * =================================================
 *
 * 처리:
 *
 * 각 AnnualLuckCandidate
 * ->
 * 해당 세운의 12개 월운 생성
 * ->
 * 조회 기준과 교차하는 월운 선택
 * ->
 * DEFINITE / POSSIBLE 결정
 *
 * =================================================
 *
 * 월운의 certainty는:
 *
 * 상위 세운 certainty
 * +
 * 월운 절입 boundary
 *
 * 두 조건을 함께 고려한다.
 */
@Component
public class MonthlyLuckCandidateAnalyzer {

    private final MonthlyLuckPeriodCalculator
            monthlyLuckPeriodCalculator;

    public MonthlyLuckCandidateAnalyzer(
            MonthlyLuckPeriodCalculator
                    monthlyLuckPeriodCalculator
    ) {

        this.monthlyLuckPeriodCalculator =
                Objects.requireNonNull(
                        monthlyLuckPeriodCalculator,
                        "MonthlyLuckPeriodCalculator가 필요합니다."
                );
    }

    /**
     * =================================================
     * 공식 16-4 API
     * =================================================
     */
    public MonthlyLuckSelectionAnalysis analyze(
            AnnualLuckSelectionAnalysis annualSelectionAnalysis
    ) {

        Objects.requireNonNull(
                annualSelectionAnalysis,
                "16-4에는 AnnualLuckSelectionAnalysis가 필요합니다."
        );

        LuckReferenceContext referenceContext =
                annualSelectionAnalysis
                        .referenceContext();

        List<MonthlyLuckCandidate> candidates =
                new ArrayList<>(2);

        /*
         * =================================================
         * 가능한 각 세운 안에서
         * 실제 월운 Candidate를 찾는다.
         * =================================================
         */

        for (
                AnnualLuckCandidate annualCandidate
                : annualSelectionAnalysis.candidates()
        ) {

            MonthlyLuckSequenceAnalysis monthlySequence =
                    monthlyLuckPeriodCalculator
                            .calculateAnnualPeriod(
                                    annualCandidate.period()
                            );

            /*
             * 15-2 Sequence는 반드시
             * 현재 세운 Candidate와 동일한
             * AnnualLuckPeriod를 사용해야 한다.
             */

            if (
                    monthlySequence.annualPeriod()
                            != annualCandidate.period()
            ) {

                throw new IllegalStateException(
                        "월운 Sequence가 현재 AnnualLuckCandidate와 다른 AnnualLuckPeriod를 참조합니다."
                );
            }

            for (
                    MonthlyLuckPeriod monthlyPeriod
                    : monthlySequence.periods()
            ) {

                /*
                 * =================================================
                 * 조회 기준과 월운 구간 교차?
                 * =================================================
                 */

                if (
                        !referenceContext
                                .possiblyIntersects(
                                        monthlyPeriod
                                                .startDateTimeInclusive(),
                                        monthlyPeriod
                                                .endDateTimeExclusive()
                                )
                ) {

                    continue;
                }

                /*
                 * =================================================
                 * DEFINITE 조건
                 * =================================================
                 *
                 * 1.
                 * 상위 세운도 DEFINITE
                 *
                 * 2.
                 * 현재 조회 기준 전체가
                 * 해당 월운 구간 안에 들어간다.
                 *
                 * 둘 다 만족해야 한다.
                 */

                boolean monthlyWindowDefinite =
                        referenceContext
                                .definitelyContainedIn(
                                        monthlyPeriod
                                                .startDateTimeInclusive(),
                                        monthlyPeriod
                                                .endDateTimeExclusive()
                                );

                boolean definite =
                        annualCandidate.definite()
                                && monthlyWindowDefinite;

                LuckCandidateCertainty certainty =
                        definite
                                ? LuckCandidateCertainty.DEFINITE
                                : LuckCandidateCertainty.POSSIBLE;

                candidates.add(
                        new MonthlyLuckCandidate(
                                annualCandidate,
                                monthlyPeriod,
                                certainty
                        )
                );
            }
        }

        /*
         * =================================================
         * 실제 시간순 정렬
         * =================================================
         */

        candidates.sort(
                Comparator.comparing(
                        candidate ->
                                candidate
                                        .startDateTimeInclusive()
                                        .toInstant()
                )
        );

        /*
         * =================================================
         * 월운 시간축은 연속적이므로
         * Candidate가 하나도 없으면 오류.
         * =================================================
         */

        if (
                candidates.isEmpty()
        ) {

            throw new IllegalStateException(
                    "현재 조회 기준에 대응하는 MonthlyLuckCandidate를 찾지 못했습니다."
            );
        }

        /*
         * =================================================
         * 하나의 civil date가
         * 세 개 월운에 걸릴 수는 없다.
         * =================================================
         */

        if (
                candidates.size() > 2
        ) {

            throw new IllegalStateException(
                    "현재 조회 기준에서 MonthlyLuckCandidate가 2개를 초과했습니다."
            );
        }

        /*
         * =================================================
         * EXACT_DATE_TIME
         * =================================================
         *
         * 세운/월운 경계는 deterministic하므로
         * 정확한 조회시각에서는
         * 월운 하나가 반드시 확정되어야 한다.
         */

        if (
                referenceContext.exact()
        ) {

            if (
                    candidates.size() != 1
            ) {

                throw new IllegalStateException(
                        "정확한 조회시각에서 MonthlyLuckCandidate가 하나로 결정되지 않았습니다."
                );
            }

            if (
                    !candidates
                            .get(0)
                            .definite()
            ) {

                throw new IllegalStateException(
                        "정확한 조회시각의 MonthlyLuckCandidate가 DEFINITE가 아닙니다."
                );
            }
        }

        /*
         * =================================================
         * 복수 Candidate면 둘 다 POSSIBLE
         * =================================================
         */

        if (
                candidates.size() == 2
        ) {

            boolean definiteExists =
                    candidates
                            .stream()
                            .anyMatch(
                                    MonthlyLuckCandidate
                                            ::definite
                            );

            if (
                    definiteExists
            ) {

                throw new IllegalStateException(
                        "복수 월운 Candidate 상태에서 DEFINITE Candidate가 생성되었습니다."
                );
            }
        }

        return new MonthlyLuckSelectionAnalysis(
                referenceContext,
                annualSelectionAnalysis,
                candidates
        );
    }
}