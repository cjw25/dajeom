package com.dajeom.type;

/**
 * 정밀화 10차-4.
 *
 * 재성 복합구조의 구조적 강도.
 *
 * =================================================
 *
 * REFERENCE
 *
 * 재성 구조의 단서는 존재하지만
 * 핵심 구조라고 판단하기에는 약함.
 *
 * =================================================
 *
 * MEANINGFUL
 *
 * 이후 복합 Pattern 및 생활영역 분석에
 * 실제 Evidence로 사용할 수 있는 수준.
 *
 * =================================================
 *
 * STRONG
 *
 * 재성 구조가 원국에서
 * 명확하게 강하게 형성된 상태.
 *
 * =================================================
 *
 * 길흉 확률 또는 재물운 점수가 아니다.
 */
public enum WealthCompositePatternLevel {

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

    WealthCompositePatternLevel(
            String koreanName,
            int rank
    ) {

        this.koreanName =
                koreanName;

        this.rank =
                rank;
    }

    public String koreanName() {

        return koreanName;
    }

    public int rank() {

        return rank;
    }

    public boolean reference() {

        return this
                == REFERENCE;
    }

    public boolean meaningful() {

        return this
                == MEANINGFUL;
    }

    public boolean strong() {

        return this
                == STRONG;
    }

    public boolean effective() {

        return meaningful()
                || strong();
    }
}