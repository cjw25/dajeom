package com.dajeom.type;

/**
 * 정밀화 10차-5.
 *
 * 관살 복합 Pattern의 구조적 강도.
 *
 * 확률이나 길흉 점수가 아니다.
 */
public enum OfficerCompositePatternLevel {

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

    OfficerCompositePatternLevel(
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