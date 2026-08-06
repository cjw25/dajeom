package com.dajeom.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 특정 연도와 월의 상세 운세 응답 DTO.
 *
 * 일반 달력의 월뿐만 아니라
 * 명리학 절기 기준 적용 기간도 함께 반환한다.
 */
public record SajuMonthForecastResponse(

        /**
         * 조회 연도
         */
        int year,

        /**
         * 조회 월
         */
        int month,

        /**
         * 해당 월의 간지
         *
         * 예: 갑진, 을사
         */
        String ganji,

        /**
         * 월운 시작 절기 이름
         *
         * 예: 입춘, 경칩, 청명
         */
        String solarTermName,

        /**
         * 절기 기준 월운 시작 시각
         */
        LocalDateTime periodStart,

        /**
         * 절기 기준 월운 종료 시각
         */
        LocalDateTime periodEnd,

        /**
         * 전체 영향 기간에서 이 월이 담당하는 단계
         */
        MonthPhase phase,

        /**
         * 월운 제목
         */
        String title,

        /**
         * 한 줄 핵심 풀이
         */
        String headline,

        /**
         * 일반인용 상세 설명
         */
        String summary,

        /**
         * 나타날 수 있는 작용
         */
        List<String> possibleEffects,

        /**
         * 좋은 방향으로 활용하는 방법
         */
        List<String> goodUses,

        /**
         * 조심해야 할 행동
         */
        List<String> cautions,

        /**
         * 분야별 작용 점수
         */
        ForecastScores scores,

        /**
         * 해석 신뢰도
         */
        ConfidenceLevel confidence,

        /**
         * 해석 신뢰 점수
         *
         * 0점부터 100점까지 사용한다.
         * 미래 사건의 발생 확률이 아니라
         * 명리학적 근거가 얼마나 반복되는지를 나타낸다.
         */
        int confidenceScore,

        /**
         * 해석에 사용된 근거 개수
         */
        int evidenceCount,

        /**
         * 명리학적 해석 근거
         */
        List<ForecastEvidence> evidences,

        /**
         * 사용자 안내 문구
         */
        String disclaimer

) {

    /**
     * null 컬렉션이 프론트엔드로 전달되지 않도록 정리한다.
     */
    public SajuMonthForecastResponse {
        phase = phase == null
                ? MonthPhase.NORMAL
                : phase;

        possibleEffects = safeList(
                possibleEffects
        );

        goodUses = safeList(
                goodUses
        );

        cautions = safeList(
                cautions
        );

        confidence = confidence == null
                ? ConfidenceLevel.REFERENCE
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
                ? "특정 사건이 반드시 발생한다는 뜻이 아니라, 관련 변화 가능성이 평소보다 강해질 수 있는 시기입니다."
                : disclaimer;
    }

    /**
     * 월별 흐름 단계.
     */
    public enum MonthPhase {

        /**
         * 작용이 서서히 시작되는 달
         */
        PRELUDE,

        /**
         * 작용이 가장 강한 달
         */
        PEAK,

        /**
         * 앞선 작용의 결과나 후속 흐름이 이어지는 달
         */
        AFTERMATH,

        /**
         * 특별한 집중 작용이 없는 일반적인 달
         */
        NORMAL
    }

    /**
     * 해석 신뢰도.
     */
    public enum ConfidenceLevel {

        /**
         * 원국·대운·세운·월운에서
         * 같은 작용이 여러 번 반복되는 경우
         */
        HIGH,

        /**
         * 주요 작용이 두 가지 이상 겹치는 경우
         */
        MEDIUM,

        /**
         * 월운이나 신살 등 보조적인 근거 위주인 경우
         */
        REFERENCE
    }

    /**
     * 분야별 작용 점수.
     *
     * 점수는 좋고 나쁨의 절대 점수가 아니라
     * 해당 분야의 움직임이 얼마나 강한지를 의미한다.
     */
    public record ForecastScores(

            /**
             * 직업과 업무 변화 작용
             */
            int career,

            /**
             * 재물 움직임
             */
            int wealth,

            /**
             * 사업과 독립 활동
             */
            int business,

            /**
             * 대인관계 작용
             */
            int relationship,

            /**
             * 연애와 배우자 관계 작용
             */
            int romance,

            /**
             * 이동과 변화 작용
             */
            int change,

            /**
             * 주의가 필요한 작용
             */
            int caution

    ) {

        public ForecastScores {
            career = normalizeScore(
                    career
            );

            wealth = normalizeScore(
                    wealth
            );

            business = normalizeScore(
                    business
            );

            relationship = normalizeScore(
                    relationship
            );

            romance = normalizeScore(
                    romance
            );

            change = normalizeScore(
                    change
            );

            caution = normalizeScore(
                    caution
            );
        }

        /**
         * 모든 점수가 0인 기본 객체.
         */
        public static ForecastScores empty() {
            return new ForecastScores(
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0
            );
        }
    }

    /**
     * 풀이 근거.
     */
    public record ForecastEvidence(

            /**
             * 근거 유형
             *
             * 예:
             * DAEUN_SEUN_REPEAT
             * MONTHLY_ACTIVATION
             * BRANCH_CLASH
             * ELEMENT_REPEAT
             * TEN_GOD_ACTIVATION
             */
            String type,

            /**
             * 일반인용 근거 설명
             */
            String description

    ) {
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