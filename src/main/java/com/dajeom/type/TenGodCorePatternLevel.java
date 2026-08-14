package com.dajeom.type;

/**
 * 정밀화 10차-8.
 *
 * 핵심 십성 Pattern의 구조적 성립 수준.
 *
 * =================================================
 *
 * REFERENCE
 *
 * 필요한 십성이 존재하여
 * Pattern의 형태는 확인되지만
 * 실제 세력이 약한 상태.
 *
 * =================================================
 *
 * MEANINGFUL
 *
 * 십성 세력과 Family 관계가
 * 실제 Pattern으로 다룰 수 있는 수준.
 *
 * =================================================
 *
 * STRONG
 *
 * 개별 십성과 Family 관계가 모두
 * 강하게 뒷받침하는 구조.
 *
 * =================================================
 *
 * 주의:
 *
 * STRONG = 길함
 *
 * 이 아니다.
 *
 * 구조적 성립도가 강하다는 뜻이다.
 */
public enum TenGodCorePatternLevel {

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

    TenGodCorePatternLevel(
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