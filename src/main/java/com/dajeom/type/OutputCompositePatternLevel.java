package com.dajeom.type;

/**
 * 정밀화 10차-3.
 *
 * 식상 복합 Pattern의 구조적 강도.
 *
 * =================================================
 *
 * REFERENCE
 *
 * 구조 단서는 존재하지만
 * 핵심 Pattern이라고 보기에는 약한 상태.
 *
 * =================================================
 *
 * MEANINGFUL
 *
 * 이후 복합구조 및 생활영역 분석에서
 * 실제 Evidence로 사용할 수준.
 *
 * =================================================
 *
 * STRONG
 *
 * 원국에서 식상 구조가
 * 명확하게 강하게 형성된 상태.
 *
 * =================================================
 *
 * 길흉 점수나 확률이 아니다.
 */
public enum OutputCompositePatternLevel {

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

    OutputCompositePatternLevel(
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