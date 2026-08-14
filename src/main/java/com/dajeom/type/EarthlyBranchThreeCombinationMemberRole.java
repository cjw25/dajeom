package com.dajeom.type;

/**
 * 정밀화 8차-4.
 *
 * 삼합국을 구성하는 세 지지의 역할.
 *
 * =================================================
 *
 * BIRTH
 * 생지(生支)
 *
 * 기운이 시작되는 지지.
 *
 * =================================================
 *
 * CARDINAL
 * 왕지(旺支)
 *
 * 삼합 오행의 중심이 되는 지지.
 *
 * 반합 여부를 구분할 때 매우 중요하다.
 *
 * =================================================
 *
 * STORAGE
 * 고지/묘지(庫支)
 *
 * 기운이 저장·마무리되는 지지.
 *
 * =================================================
 *
 * 예:
 *
 * 申子辰 水局
 *
 * 申 = 생지
 * 子 = 왕지
 * 辰 = 고지
 */
public enum EarthlyBranchThreeCombinationMemberRole {

    BIRTH(
            "생지"
    ),

    CARDINAL(
            "왕지"
    ),

    STORAGE(
            "고지"
    );

    private final String koreanName;

    EarthlyBranchThreeCombinationMemberRole(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean birth() {

        return this == BIRTH;
    }

    public boolean cardinal() {

        return this == CARDINAL;
    }

    public boolean storage() {

        return this == STORAGE;
    }
}