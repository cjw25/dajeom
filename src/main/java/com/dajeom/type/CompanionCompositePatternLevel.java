package com.dajeom.type;

/**
 * 정밀화 10차-2.
 *
 * 비겁 복합 Pattern의 구조적 강도.
 *
 * =================================================
 *
 * REFERENCE
 *
 * Pattern의 단서가 있으나
 * 핵심 구조로 보기에는 약한 상태.
 *
 * =================================================
 *
 * MEANINGFUL
 *
 * 후속 십성 복합분석에서
 * 실제 Evidence로 사용할 수 있는 수준.
 *
 * =================================================
 *
 * STRONG
 *
 * 명식 내에서 무시하기 어려운
 * 강한 복합구조.
 *
 * =================================================
 *
 * 확률이나 길흉 점수가 아니다.
 */
public enum CompanionCompositePatternLevel {

    REFERENCE(
            "참고",
            1
    ),

    MEANINGFUL(
            "유의미",
            2
    ),

    STRONG(
            "강함",
            3
    );

    private final String koreanName;

    private final int rank;

    CompanionCompositePatternLevel(
            String koreanName,
            int rank
    ) {

        this.koreanName = koreanName;
        this.rank = rank;
    }

    public String koreanName() {

        return koreanName;
    }

    public int rank() {

        return rank;
    }

    public boolean reference() {

        return this == REFERENCE;
    }

    public boolean meaningful() {

        return this == MEANINGFUL;
    }

    public boolean strong() {

        return this == STRONG;
    }

    public boolean effective() {

        return meaningful()
                || strong();
    }
}