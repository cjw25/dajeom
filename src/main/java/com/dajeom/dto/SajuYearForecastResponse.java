package com.dajeom.dto;

import java.time.YearMonth;
import java.util.List;

/**
 * 특정 연도의 종합 운세 응답 DTO.
 *
 * 연도의 큰 흐름과 12개월 월운,
 * 전조월·집중월·잔여월을 함께 반환한다.
 */
public record SajuYearForecastResponse(

        /**
         * 조회 연도
         */
        int year,

        /**
         * 해당 연도의 간지
         *
         * 예: 병오, 정미
         */
        String ganji,

        /**
         * 연도 운세 제목
         */
        String title,

        /**
         * 한 줄 핵심 풀이
         */
        String headline,

        /**
         * 해당 연도의 핵심 주제
         *
         * 예:
         * 직업 변화
         * 재물과 계약
         * 인간관계 재편
         */
        String theme,

        /**
         * 일반인용 연도 종합 설명
         */
        String summary,

        /**
         * 해당 연도에 나타날 수 있는 주요 작용
         */
        List<String> possibleEffects,

        /**
         * 기회를 활용하는 방법
         */
        List<String> opportunities,

        /**
         * 조심해야 할 행동
         */
        List<String> cautions,

        /**
         * 월 단위 전체 영향 기간
         */
        InfluencePeriod influencePeriod,

        /**
         * 1월부터 12월까지의 월별 운세
         */
        List<SajuMonthForecastResponse> months,

        /**
         * 분야별 연간 작용 점수
         */
        SajuMonthForecastResponse.ForecastScores scores,

        /**
         * 해석 신뢰도
         */
        SajuMonthForecastResponse.ConfidenceLevel confidence,

        /**
         * 해석 신뢰 점수
         */
        int confidenceScore,

        /**
         * 해석 근거 개수
         */
        int evidenceCount,

        /**
         * 명리학적 해석 근거
         */
        List<SajuMonthForecastResponse.ForecastEvidence> evidences,

        /**
         * 사용자 안내 문구
         */
        String disclaimer

) {

    /**
     * null 컬렉션과 기본값을 정리한다.
     */
    public SajuYearForecastResponse {
        possibleEffects = safeList(
                possibleEffects
        );

        opportunities = safeList(
                opportunities
        );

        cautions = safeList(
                cautions
        );

        months = safeList(
                months
        );

        scores = scores == null
                ? SajuMonthForecastResponse
                .ForecastScores
                .empty()
                : scores;

        confidence = confidence == null
                ? SajuMonthForecastResponse
                .ConfidenceLevel
                .REFERENCE
                : confidence;

        confidenceScore = normalizeScore(
                confidenceScore
        );

        evidenceCount = Math.max(
                evidenceCount,
                0
        );

        evidences = safeList(
                evidences
        );

        disclaimer = disclaimer == null
                ? "연도와 월의 흐름은 특정 사건을 확정하는 예언이 아니라, 관련 작용이 강해질 수 있는 시기를 설명합니다."
                : disclaimer;
    }

    /**
     * 하나의 작용이 이어지는 월 단위 기간.
     *
     * 예:
     * 전체 영향 기간: 2027년 4월부터 7월
     * 집중 작용 기간: 2027년 5월부터 6월
     */
    public record InfluencePeriod(

            /**
             * 영향이 시작되는 월
             */
            YearMonth startYearMonth,

            /**
             * 작용이 가장 강해지기 시작하는 월
             */
            YearMonth peakStartYearMonth,

            /**
             * 작용이 가장 강한 구간의 마지막 월
             */
            YearMonth peakEndYearMonth,

            /**
             * 잔여 작용이 끝나는 월
             */
            YearMonth endYearMonth,

            /**
             * 기간의 핵심 작용
             */
            String theme,

            /**
             * 기간에 대한 쉬운 설명
             */
            String description

    ) {

        /**
         * 영향 기간이 아직 계산되지 않았을 때 사용하는 기본 객체.
         */
        public static InfluencePeriod empty() {
            return new InfluencePeriod(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }
    }

    private static int normalizeScore(
            int score
    ) {
        return Math.max(
                0,
                Math.min(
                        score,
                        100
                )
        );
    }

    private static <T> List<T> safeList(
            List<T> values
    ) {
        return values == null
                ? List.of()
                : List.copyOf(values);
    }
}