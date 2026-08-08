package com.dajeom.type;

/**
 * 사용자에게 제공하는 최종 사주 상담 분야.
 *
 * 내부 명리 계산 용어와
 * 사용자 화면의 표현을 분리하기 위한 타입이다.
 */
public enum ConsultationCategory {

    PERSONALITY(
            1,
            "나의 기본 성향"
    ),

    PAST_LUCK(
            2,
            "지나온 흐름"
    ),

    SOCIAL_RELATIONSHIP(
            3,
            "주변 사람·대인관계"
    ),

    LOVE_STYLE(
            4,
            "연애 스타일"
    ),

    PARTNER_LUCK(
            5,
            "인연·배우자운"
    ),

    WEALTH(
            6,
            "재물운"
    ),

    CAREER(
            7,
            "직업·사업운"
    ),

    LEGAL_RISK(
            8,
            "분쟁·주의운"
    ),

    HEALTH_TENDENCY(
            9,
            "건강 관리 경향"
    ),

    CURRENT_YEAR_LUCK(
            10,
            "현재와 올해의 흐름"
    ),

    FUTURE_LUCK(
            11,
            "앞으로의 흐름"
    );

    private final int order;
    private final String koreanName;

    ConsultationCategory(
            int order,
            String koreanName
    ) {
        this.order = order;
        this.koreanName = koreanName;
    }

    public int getOrder() {
        return order;
    }

    public String getKoreanName() {
        return koreanName;
    }
}