package com.dajeom.type;

/**
 * 사주 네 기둥의 위치.
 */
public enum PillarPosition {

    YEAR(
            1,
            "년주"
    ),

    MONTH(
            2,
            "월주"
    ),

    DAY(
            3,
            "일주"
    ),

    HOUR(
            4,
            "시주"
    );

    private final int order;
    private final String koreanName;

    PillarPosition(
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