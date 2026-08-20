package com.dajeom.type;

import java.util.Objects;

/**
 * 정밀화 14차-5 provenance 보강.
 *
 * 대운과 세운 사이에서 발생하는
 * 관계의 구조적 종류.
 *
 * =================================================
 *
 * 기존에는:
 *
 * relationCode
 *
 * 문자열만 남았기 때문에
 * 후속 Functional Layer가
 * 문자열을 다시 해석해야 했다.
 *
 * =================================================
 *
 * 특히 천간 극은 방향을 보존한다.
 *
 * CONTROL_DECADE_TO_ANNUAL
 *
 * =
 *
 * 대운 천간이 세운 천간을 극함.
 *
 * =================================================
 *
 * CONTROL_ANNUAL_TO_DECADE
 *
 * =
 *
 * 세운 천간이 대운 천간을 극함.
 *
 * =================================================
 *
 * 중요:
 *
 * 관계 종류 자체는 길흉이 아니다.
 *
 * CLASH / CONTROL / PUNISHMENT 등을
 * 곧바로 Function 약화나 흉으로
 * 변환하지 않는다.
 */
public enum AnnualDecadeLuckRelationKind {

    COMBINATION(
            true,
            false,
            true,
            false
    ),

    CONTROL_DECADE_TO_ANNUAL(
            true,
            false,
            false,
            true
    ),

    CONTROL_ANNUAL_TO_DECADE(
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

    BREAK(
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

    AnnualDecadeLuckRelationKind(

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
            AnnualDecadeLuckRelationLayer layer
    ) {

        Objects.requireNonNull(
                layer,
                "대운-세운 관계 Layer가 필요합니다."
        );

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
     * 제한/충돌 Context로 검토할 관계인가.
     *
     * 이것 자체가 실제 Function 감소를
     * 확정하는 것은 아니다.
     */
    public boolean restrictiveInteraction() {

        return restrictiveInteraction;
    }

    public boolean control() {

        return this == CONTROL_DECADE_TO_ANNUAL
                || this == CONTROL_ANNUAL_TO_DECADE;
    }

    public boolean decadeControlsAnnual() {

        return this == CONTROL_DECADE_TO_ANNUAL;
    }

    public boolean annualControlsDecade() {

        return this == CONTROL_ANNUAL_TO_DECADE;
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