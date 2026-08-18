package com.dajeom.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 21차-1.
 *
 * PAST_LUCK / FUTURE_LUCK용
 * 실제 Timeline Range 분석 Anchor.
 *
 * =================================================
 *
 * currentReferenceDate
 *
 * 현재 상담 기준 날짜.
 *
 * =================================================
 *
 * pastAnalyses
 *
 * 현재 기준연도 이전의
 * 실제 16 -> 17 -> 18차 LifeDomainAnalysis.
 *
 * =================================================
 *
 * futureAnalyses
 *
 * 현재 기준연도 이후의
 * 실제 16 -> 17 -> 18차 LifeDomainAnalysis.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * - 과거/미래를 YEAR/HOUR 궁위로 대체하지 않는다.
 * - 현재 Segment를 복사하지 않는다.
 * - 새로운 LifeDomain Rule을 만들지 않는다.
 * - 기존 16~18차 deterministic 계산을 재사용한다.
 */
public record ConsultationTimelineRangeAnalysis(

        LocalDate currentReferenceDate,

        List<LifeDomainAnalysis> pastAnalyses,

        List<LifeDomainAnalysis> futureAnalyses

) {

    public ConsultationTimelineRangeAnalysis {

        Objects.requireNonNull(
                currentReferenceDate,
                "ConsultationTimelineRangeAnalysis에는 현재 기준날짜가 필요합니다."
        );

        pastAnalyses =
                normalize(
                        pastAnalyses,
                        "과거"
                );

        futureAnalyses =
                normalize(
                        futureAnalyses,
                        "미래"
                );
    }

    public boolean hasPastRange() {

        return !pastAnalyses.isEmpty();
    }

    public boolean hasFutureRange() {

        return !futureAnalyses.isEmpty();
    }

    public int pastAnalysisCount() {

        return pastAnalyses.size();
    }

    public int futureAnalysisCount() {

        return futureAnalyses.size();
    }

    private static List<LifeDomainAnalysis> normalize(

            List<LifeDomainAnalysis> source,

            String label

    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return List.of();
        }

        List<LifeDomainAnalysis> copied =
                List.copyOf(
                        source
                );

        for (
                LifeDomainAnalysis analysis
                : copied
        ) {

            Objects.requireNonNull(
                    analysis,
                    label
                            + " Timeline Range의 LifeDomainAnalysis는 null일 수 없습니다."
            );
        }

        return copied;
    }
}