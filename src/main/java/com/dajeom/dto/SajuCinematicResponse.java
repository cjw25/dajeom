package com.dajeom.dto;

import java.time.YearMonth;
import java.util.List;

/**
 * 영상형 사주 결과 응답 DTO.
 *
 * 실제 영상 파일을 생성하는 것이 아니라
 * 프론트엔드가 영상처럼 재생할 수 있는
 * 장면별 데이터를 반환한다.
 */
public record SajuCinematicResponse(

        /**
         * 분석 ID
         */
        Long analysisId,

        /**
         * 사주 프로필 ID
         */
        Long profileId,

        /**
         * 영상형 결과 제목
         */
        String title,

        /**
         * 영상형 결과 부제목
         */
        String subtitle,

        /**
         * 전체 예상 재생시간
         */
        int totalDurationSeconds,

        /**
         * 영상 장면 목록
         */
        List<CinematicScene> scenes,

        /**
         * 사용자 안내 문구
         */
        String disclaimer

) {

    public SajuCinematicResponse {
        totalDurationSeconds = Math.max(
                totalDurationSeconds,
                0
        );

        scenes = scenes == null
                ? List.of()
                : List.copyOf(scenes);

        disclaimer = disclaimer == null
                ? "이 해설은 만세력과 명리학적 계산을 바탕으로 한 참고 분석이며, 미래의 특정 사건을 확정하지 않습니다."
                : disclaimer;
    }

    /**
     * 영상형 사주 결과의 개별 장면.
     */
    public record CinematicScene(

            /**
             * 장면 순서
             */
            int order,

            /**
             * 장면 유형
             */
            SceneType sceneType,

            /**
             * 프론트엔드에서 사용할 배경 키
             *
             * 예:
             * DESTINY_GATE
             * FOUR_PILLARS
             * INNER_NATURE
             * YEAR_FLOW
             * MONTHLY_TIMELINE
             */
            String visualKey,

            /**
             * 캐릭터 표정 키
             *
             * 예:
             * CALM
             * SERIOUS
             * SMILE
             * WARNING
             */
            String characterExpressionKey,

            /**
             * 장면 전환 효과 키
             *
             * 예:
             * FADE
             * SLIDE_UP
             * ZOOM_IN
             * LIGHT_FLASH
             */
            String transitionKey,

            /**
             * 장면 재생시간
             */
            int durationSeconds,

            /**
             * 장면 제목
             */
            String title,

            /**
             * 음성 또는 화면 낭독용 문장
             */
            String narration,

            /**
             * 화면에 짧게 표시할 자막
             */
            String subtitle,

            /**
             * 미래 기간 장면에서 사용하는 월 단위 기간
             */
            CinematicPeriod period,

            /**
             * 화면에 강조해서 보여줄 핵심 내용
             */
            List<String> highlights,

            /**
             * 해당 장면의 명리학적 근거
             */
            List<String> evidences

    ) {

        public CinematicScene {
            order = Math.max(
                    order,
                    0
            );

            sceneType = sceneType == null
                    ? SceneType.OPENING
                    : sceneType;

            durationSeconds = Math.max(
                    durationSeconds,
                    1
            );

            highlights = highlights == null
                    ? List.of()
                    : List.copyOf(highlights);

            evidences = evidences == null
                    ? List.of()
                    : List.copyOf(evidences);
        }
    }

    /**
     * 영상 장면 유형.
     */
    public enum SceneType {

        /**
         * 영상 시작
         */
        OPENING,

        /**
         * 만세력 원국 공개
         */
        MANSE_CALENDAR,

        /**
         * 타고난 기본 기운
         */
        BASIC_ENERGY,

        /**
         * 겉으로 보이는 성격
         */
        PERSONALITY_OUTER,

        /**
         * 실제 속마음
         */
        PERSONALITY_INNER,

        /**
         * 장점과 능력
         */
        STRENGTHS,

        /**
         * 약점과 보완점
         */
        WEAKNESSES,

        /**
         * 재물운
         */
        WEALTH,

        /**
         * 직업과 사업운
         */
        CAREER_BUSINESS,

        /**
         * 사람복과 인간관계
         */
        RELATIONSHIPS,

        /**
         * 연애와 배우자운
         */
        ROMANCE,

        /**
         * 현재 대운
         */
        CURRENT_DAEUN,

        /**
         * 앞으로의 큰 흐름
         */
        FUTURE_OVERVIEW,

        /**
         * 특정 연도의 흐름
         */
        YEAR_FORECAST,

        /**
         * 월별 영향 기간
         */
        MONTH_PERIOD,

        /**
         * 조심해야 할 기간
         */
        CAUTION_PERIOD,

        /**
         * 기회를 활용할 기간
         */
        OPPORTUNITY_PERIOD,

        /**
         * 현실적인 최종 조언
         */
        ADVICE,

        /**
         * 만세력과 계산 근거
         */
        EVIDENCE
    }

    /**
     * 월 단위 영향 기간.
     */
    public record CinematicPeriod(

            /**
             * 전체 영향 시작월
             */
            YearMonth startYearMonth,

            /**
             * 집중 작용 시작월
             */
            YearMonth peakStartYearMonth,

            /**
             * 집중 작용 종료월
             */
            YearMonth peakEndYearMonth,

            /**
             * 전체 영향 종료월
             */
            YearMonth endYearMonth,

            /**
             * 기간 표시용 문장
             *
             * 예:
             * 2027년 4월부터 7월까지
             */
            String displayText

    ) {

        /**
         * 기간 정보가 없는 장면에서 사용하는 기본 객체.
         */
        public static CinematicPeriod empty() {
            return new CinematicPeriod(
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }
    }
}