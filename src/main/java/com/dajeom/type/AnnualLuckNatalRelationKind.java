package com.dajeom.type;

/**
 * 세운과 원국 사이에서 발생하는
 * 관계의 구조적 종류.
 *
 * =================================================
 *
 * 기존 14-4에서는:
 *
 * relationCode
 * relationName
 *
 * 문자열만 보존했기 때문에
 * 후속 Functional Analysis에서
 * 관계 종류를 다시 문자열로 해석해야 하는
 * 문제가 있었다.
 *
 * =================================================
 *
 * 이제:
 *
 * AnnualLuckNatalRelationKind
 *
 * 를 공식 구조 Source로 사용하고,
 *
 * relationCode / relationName은
 * 설명 및 추적용 메타데이터로만 유지한다.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * - COMBINATION = 강화 아님
 * - CONTROL = 약화 아님
 * - CLASH = 흉 아님
 *
 * 관계 종류와
 * 실제 Function 작동 방향은
 * 후속 Functional Layer에서 별도로 판단한다.
 */
public enum AnnualLuckNatalRelationKind {

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

    AnnualLuckNatalRelationKind(

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
            AnnualLuckNatalRelationLayer layer
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

    public boolean combinationFamily() {

        return combinationFamily;
    }

    /**
     * 후속 Functional Layer에서
     * 원기능 제한 여부를 추가 검토해야 하는
     * 관계 종류인가.
     *
     * 이것 자체가 실제 Function 약화를
     * 확정하는 것은 아니다.
     */
    public boolean restrictiveInteraction() {

        return restrictiveInteraction;
    }

    public boolean control() {

        return this == CONTROL;
    }

    public boolean clash() {

        return this == CLASH;
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