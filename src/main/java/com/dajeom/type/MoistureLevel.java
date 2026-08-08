package com.dajeom.type;

/**
 * 원국의 조습 상태.
 *
 * DRY:
 * 건조한 방향
 *
 * BALANCED:
 * 특별히 한쪽으로 치우치지 않은 상태
 *
 * WET:
 * 습한 방향
 */
public enum MoistureLevel {

    VERY_DRY(
            "매우 건조"
    ),

    DRY(
            "건조"
    ),

    BALANCED(
            "조습 균형"
    ),

    WET(
            "습윤"
    ),

    VERY_WET(
            "매우 습윤"
    );

    private final String koreanName;

    MoistureLevel(
            String koreanName
    ) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}