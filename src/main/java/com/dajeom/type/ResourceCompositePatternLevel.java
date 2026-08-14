package com.dajeom.type;

/**
 * 정밀화 10차-6.
 *
 * 인성 복합 Pattern의 구조적 강도.
 *
 * =================================================
 *
 * REFERENCE
 *
 * 구조적 단서는 있으나
 * 핵심 Pattern으로 보기에는 약한 수준.
 *
 * =================================================
 *
 * MEANINGFUL
 *
 * 후속 복합분석에서 실제 Evidence로
 * 사용할 수 있는 수준.
 *
 * =================================================
 *
 * STRONG
 *
 * 원국에서 인성 구조가
 * 강하게 형성된 상태.
 *
 * =================================================
 *
 * 길흉 점수나 확률이 아니다.
 */
public enum ResourceCompositePatternLevel {

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

    ResourceCompositePatternLevel(
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