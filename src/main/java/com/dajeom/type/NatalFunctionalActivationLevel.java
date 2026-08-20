package com.dajeom.type;

/**
 * 원국 기능축의 구조적 활성 수준.
 *
 * 이 값은 길흉 점수나 확률이 아니다.
 *
 * 기존 10차 십성 복합분석이 이미 판정한
 * 존재 / 유의미 / 강한 Pattern 수준을
 * 후속 Functional Analysis에서 사용하기 쉽게 정규화한다.
 *
 * 새로운 임의 점수나 threshold를 만들지 않는다.
 */
public enum NatalFunctionalActivationLevel {

    ABSENT(
            "부재",
            0
    ),

    PRESENT(
            "존재",
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

    NatalFunctionalActivationLevel(
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

    public boolean present() {

        return this
                != ABSENT;
    }

    public boolean meaningful() {

        return this
                == MEANINGFUL

                || this
                == STRONG;
    }

    public boolean strong() {

        return this
                == STRONG;
    }
}