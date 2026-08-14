package com.dajeom.type;

/**
 * 정밀화 8차-10.
 *
 * 하나의 지지 궁위 Pair에 존재하는
 * 관계 종류.
 *
 * =================================================
 *
 * SIX_COMBINATION
 * 六合
 *
 * THREE_COMBINATION
 * 三合 / 半合 / 부분 삼합
 *
 * DIRECTIONAL_COMBINATION
 * 方合 / 부분 방합
 *
 * CLASH
 * 沖
 *
 * PUNISHMENT
 * 刑
 *
 * BREAK
 * 破
 *
 * HARM
 * 害
 *
 * =================================================
 *
 * 관계들은 상호 배타적이지 않다.
 *
 * 하나의 동일 Pair에:
 *
 * 六合
 * 刑
 * 破
 *
 * 등이 동시에 존재할 수 있다.
 *
 * =================================================
 *
 * 이 enum은 관계망의 요약 분류다.
 *
 * 완전 삼합인지 반합인지,
 * 완전 삼형인지 부분 삼형인지 같은
 * 세부 Evidence는 각 원본 Analysis에 그대로 남아 있다.
 */
public enum EarthlyBranchRelationKind {

    SIX_COMBINATION(
            "육합",
            true
    ),

    THREE_COMBINATION(
            "삼합계",
            true
    ),

    DIRECTIONAL_COMBINATION(
            "방합계",
            true
    ),

    CLASH(
            "충",
            false
    ),

    PUNISHMENT(
            "형",
            false
    ),

    BREAK(
            "파",
            false
    ),

    HARM(
            "해",
            false
    );

    private final String koreanName;

    private final boolean combinationFamily;

    EarthlyBranchRelationKind(
            String koreanName,
            boolean combinationFamily
    ) {

        this.koreanName =
                koreanName;

        this.combinationFamily =
                combinationFamily;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean combinationFamily() {

        return combinationFamily;
    }

    public boolean nonCombinationFamily() {

        return !combinationFamily;
    }

    public boolean sixCombination() {

        return this == SIX_COMBINATION;
    }

    public boolean threeCombination() {

        return this == THREE_COMBINATION;
    }

    public boolean directionalCombination() {

        return this == DIRECTIONAL_COMBINATION;
    }

    public boolean clash() {

        return this == CLASH;
    }

    public boolean punishment() {

        return this == PUNISHMENT;
    }

    public boolean breakRelation() {

        return this == BREAK;
    }

    public boolean harm() {

        return this == HARM;
    }
}