package com.dajeom.type;

/**
 * 대운 × 원국 관계의 구조적 종류.
 *
 * =================================================
 *
 * 목적:
 *
 * 13-5 이후 계층에서:
 *
 * relationCode
 * relationName
 *
 * 문자열을 파싱하지 않고
 * 관계 종류를 구조적으로 판단하기 위해 사용한다.
 *
 * =================================================
 *
 * 중요:
 *
 * 이 enum은 길흉 점수가 아니다.
 *
 * 예:
 *
 * CLASH
 * =
 * 무조건 나쁨
 *
 * COMBINATION
 * =
 * 무조건 좋음
 *
 * 으로 해석하지 않는다.
 *
 * 실제 기능 변화는:
 *
 * - 합화 활성 여부
 * - 원기능 감소
 * - 원국 기능구조
 * - 대운 기능입력
 *
 * 을 별도로 결합해서 판단한다.
 */
public enum DecadeLuckNatalRelationKind {

    COMBINATION(
            true,
            false,
            true,
            false
    ),

    CONTROL(
            true,
            false,
            false,
            true
    ),

    CLASH(
            true,
            true,
            false,
            true
    ),

    SIX_COMBINATION(
            false,
            true,
            true,
            false
    ),

    PUNISHMENT(
            false,
            true,
            false,
            true
    ),

    SELF_PUNISHMENT(
            false,
            true,
            false,
            true
    ),

    HARM(
            false,
            true,
            false,
            true
    ),

    BREAK(
            false,
            true,
            false,
            true
    ),

    THREE_COMBINATION(
            false,
            true,
            true,
            false
    ),

    DIRECTIONAL_COMBINATION(
            false,
            true,
            true,
            false
    );

    private final boolean heavenlyStemAllowed;

    private final boolean earthlyBranchAllowed;

    private final boolean combinationFamily;

    private final boolean restrictiveInteraction;

    DecadeLuckNatalRelationKind(

            boolean heavenlyStemAllowed,

            boolean earthlyBranchAllowed,

            boolean combinationFamily,

            boolean restrictiveInteraction

    ) {

        this.heavenlyStemAllowed =
                heavenlyStemAllowed;

        this.earthlyBranchAllowed =
                earthlyBranchAllowed;

        this.combinationFamily =
                combinationFamily;

        this.restrictiveInteraction =
                restrictiveInteraction;
    }

    public boolean allows(
            DecadeLuckNatalRelationLayer layer
    ) {

        if (
                layer == null
        ) {

            return false;
        }

        return switch (
                layer
                ) {

            case HEAVENLY_STEM ->
                    heavenlyStemAllowed;

            case EARTHLY_BRANCH ->
                    earthlyBranchAllowed;
        };
    }

    /**
     * 합/결합 계열 관계인가.
     *
     * 실제 합화 활성 여부와는 다르다.
     */
    public boolean combinationFamily() {

        return combinationFamily;
    }

    /**
     * 구조적으로 간섭 또는 충돌성이 있는
     * 관계 Family인가.
     *
     * 길흉 판정이 아니다.
     */
    public boolean restrictiveInteraction() {

        return restrictiveInteraction;
    }

    public boolean clash() {

        return this == CLASH;
    }

    public boolean control() {

        return this == CONTROL;
    }

    public boolean punishment() {

        return this == PUNISHMENT
                || this == SELF_PUNISHMENT;
    }

    public boolean harm() {

        return this == HARM;
    }

    public boolean breakRelation() {

        return this == BREAK;
    }
}