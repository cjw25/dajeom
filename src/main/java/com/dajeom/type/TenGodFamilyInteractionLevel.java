package com.dajeom.type;

/**
 * 정밀화 10차-7.
 *
 * 두 십성 Family 사이의
 * 상호작용 구조 강도.
 *
 * =================================================
 *
 * REFERENCE
 *
 * 두 Family가 모두 존재하지만
 * 한쪽 또는 양쪽의 세력이 약해
 * 참고 수준의 관계.
 *
 * =================================================
 *
 * MEANINGFUL
 *
 * 실제 복합 Pattern 판정에서
 * 사용할 수 있는 수준.
 *
 * =================================================
 *
 * STRONG
 *
 * 양쪽 Family가 모두 원국에서
 * 의미 있게 형성된 강한 상호작용.
 *
 * =================================================
 *
 * 확률이나 길흉 점수가 아니다.
 */
public enum TenGodFamilyInteractionLevel {

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

    TenGodFamilyInteractionLevel(
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