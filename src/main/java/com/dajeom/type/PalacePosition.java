package com.dajeom.type;

/**
 * 정밀화 12차-1.
 *
 * 사주 원국의 4개 궁위 위치.
 *
 * =================================================
 *
 * YEAR
 *
 * 원국의 바깥쪽 배경과
 * 초기 기반에 가까운 위치.
 *
 * =================================================
 *
 * MONTH
 *
 * 계절·환경·사회적 작동 기반과
 * 가까운 위치.
 *
 * =================================================
 *
 * DAY
 *
 * 일간과 직접 연결되는
 * 핵심 생활축 위치.
 *
 * =================================================
 *
 * HOUR
 *
 * 원국에서 앞으로 확장되는 방향과
 * 후반 전개 Context에 가까운 위치.
 *
 * =================================================
 *
 * 중요:
 *
 * 이 enum 자체에서는:
 *
 * YEAR  = 부모
 * MONTH = 형제
 * DAY   = 배우자
 * HOUR  = 자녀
 *
 * 같은 가족관계 1:1 대응을 확정하지 않는다.
 *
 * 가족·관계·생활영역 해석은
 * 이후 십성·궁위·운 흐름을 함께 사용한다.
 */
public enum PalacePosition {

    YEAR(
            "년궁",
            "외부 배경·초기 기반",
            0
    ),

    MONTH(
            "월궁",
            "환경·사회적 작동 기반",
            1
    ),

    DAY(
            "일궁",
            "핵심 생활축",
            2
    ),

    HOUR(
            "시궁",
            "확장·후반 전개",
            3
    );

    private final String koreanName;

    private final String contextName;

    private final int order;

    PalacePosition(
            String koreanName,
            String contextName,
            int order
    ) {

        this.koreanName =
                koreanName;

        this.contextName =
                contextName;

        this.order =
                order;
    }

    public String koreanName() {

        return koreanName;
    }

    public String contextName() {

        return contextName;
    }

    /**
     * 원국 표시 순서.
     *
     * 중요도나 길흉 순위가 아니다.
     */
    public int order() {

        return order;
    }

    public boolean year() {

        return this == YEAR;
    }

    public boolean month() {

        return this == MONTH;
    }

    public boolean day() {

        return this == DAY;
    }

    public boolean hour() {

        return this == HOUR;
    }
}