package com.dajeom.type;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 기존 10-8 / 10-9에서 이미 계산된
 * 전통 십성 복합 Pattern을
 *
 * 원국 Functional Analysis의
 * 기능축 관계로 변환한 타입.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 새로운 Pattern을 발명하지 않는다.
 *
 * 기존:
 *
 * TenGodCorePatternType
 *
 * 에 존재하는 11개 Pattern을
 * 정확히 1:1로 연결한다.
 *
 * =================================================
 *
 * 예:
 *
 * 식신제살
 *
 * EXPRESSION
 * 식상
 *
 * →
 *
 * RESPONSIBILITY
 * 관살
 *
 * CONTROL
 *
 * =================================================
 *
 * 상관견관
 *
 * EXPRESSION
 *
 * ↔
 *
 * RESPONSIBILITY
 *
 * CONFLICT
 *
 * =================================================
 *
 * 재다신약
 *
 * RESOURCE_REALITY
 *
 * ↔
 *
 * CORE_SELF
 *
 * OVERLOAD
 *
 * =================================================
 *
 * 이 enum 자체는
 * 사람의 성격이나 길흉을 결정하지 않는다.
 */
public enum NatalCrossFunctionalPatternType {

    /**
     * 식신제살.
     *
     * 표현·산출 기능이
     * 칠살 압력 기능을 제어하는 구조.
     */
    EXPRESSION_CONTROLS_PRESSURE(

            "표현 기능의 관살 제어",

            TenGodCorePatternType
                    .EATING_GOD_CONTROLS_SEVEN_KILLINGS,

            NatalCrossFunctionalScope
                    .BETWEEN_FUNCTIONS,

            NatalCrossFunctionalInteractionType
                    .CONTROL,

            NatalTenGodFunctionType
                    .EXPRESSION,

            NatalTenGodFunctionType
                    .RESPONSIBILITY
    ),

    /**
     * 상관견관.
     *
     * 표현 기능과
     * 관성·규범 기능이 직접 충돌하는 구조.
     */
    EXPRESSION_CONFLICTS_RESPONSIBILITY(

            "표현 기능과 관성 기능의 충돌",

            TenGodCorePatternType
                    .HURTING_OFFICER_MEETS_DIRECT_OFFICER,

            NatalCrossFunctionalScope
                    .BETWEEN_FUNCTIONS,

            NatalCrossFunctionalInteractionType
                    .CONFLICT,

            NatalTenGodFunctionType
                    .EXPRESSION,

            NatalTenGodFunctionType
                    .RESPONSIBILITY
    ),

    /**
     * 상관패인.
     *
     * 인성 기능이
     * 상관 표현 기능을 조절하는 구조.
     */
    PROCESSING_REGULATES_EXPRESSION(

            "인성 기능의 표현 조절",

            TenGodCorePatternType
                    .HURTING_OFFICER_WITH_RESOURCE,

            NatalCrossFunctionalScope
                    .BETWEEN_FUNCTIONS,

            NatalCrossFunctionalInteractionType
                    .CONTROL,

            NatalTenGodFunctionType
                    .PROCESSING,

            NatalTenGodFunctionType
                    .EXPRESSION
    ),

    /**
     * 편인도식 / 효신탈식.
     *
     * 인성 기능이
     * 식신 산출 기능을 제어하는 구조.
     */
    PROCESSING_RESTRAINS_OUTPUT(

            "인성 기능의 식신 억제",

            TenGodCorePatternType
                    .INDIRECT_RESOURCE_SEIZES_EATING_GOD,

            NatalCrossFunctionalScope
                    .BETWEEN_FUNCTIONS,

            NatalCrossFunctionalInteractionType
                    .CONTROL,

            NatalTenGodFunctionType
                    .PROCESSING,

            NatalTenGodFunctionType
                    .EXPRESSION
    ),

    /**
     * 재생관.
     *
     * 현실·자원 기능이
     * 관성·책임 기능으로 연결되는 구조.
     */
    RESOURCE_GENERATES_RESPONSIBILITY(

            "재성 기능의 관성 생성",

            TenGodCorePatternType
                    .WEALTH_GENERATES_DIRECT_OFFICER,

            NatalCrossFunctionalScope
                    .BETWEEN_FUNCTIONS,

            NatalCrossFunctionalInteractionType
                    .GENERATION,

            NatalTenGodFunctionType
                    .RESOURCE_REALITY,

            NatalTenGodFunctionType
                    .RESPONSIBILITY
    ),

    /**
     * 관인상생.
     *
     * 관성·책임 기능이
     * 인성·수용 기능으로 연결되는 구조.
     */
    RESPONSIBILITY_GENERATES_PROCESSING(

            "관성 기능의 인성 생성",

            TenGodCorePatternType
                    .DIRECT_OFFICER_GENERATES_RESOURCE,

            NatalCrossFunctionalScope
                    .BETWEEN_FUNCTIONS,

            NatalCrossFunctionalInteractionType
                    .GENERATION,

            NatalTenGodFunctionType
                    .RESPONSIBILITY,

            NatalTenGodFunctionType
                    .PROCESSING
    ),

    /**
     * 살인상생.
     *
     * 칠살 압력 기능이
     * 인성 기능으로 연결되는 구조.
     */
    PRESSURE_GENERATES_PROCESSING(

            "칠살 기능의 인성 생성",

            TenGodCorePatternType
                    .SEVEN_KILLINGS_GENERATES_RESOURCE,

            NatalCrossFunctionalScope
                    .BETWEEN_FUNCTIONS,

            NatalCrossFunctionalInteractionType
                    .GENERATION,

            NatalTenGodFunctionType
                    .RESPONSIBILITY,

            NatalTenGodFunctionType
                    .PROCESSING
    ),

    /**
     * 재극인.
     *
     * 현실·자원 기능이
     * 인성·수용 기능을 제어하는 구조.
     */
    RESOURCE_CONTROLS_PROCESSING(

            "재성 기능의 인성 제어",

            TenGodCorePatternType
                    .WEALTH_CONTROLS_RESOURCE,

            NatalCrossFunctionalScope
                    .BETWEEN_FUNCTIONS,

            NatalCrossFunctionalInteractionType
                    .CONTROL,

            NatalTenGodFunctionType
                    .RESOURCE_REALITY,

            NatalTenGodFunctionType
                    .PROCESSING
    ),

    /**
     * 비겁쟁재.
     *
     * 자기주도·경쟁 기능과
     * 현실·자원 기능이 경합하는 구조.
     */
    SELF_ASSERTION_COMPETES_FOR_RESOURCE(

            "비겁 기능과 재성 기능의 경합",

            TenGodCorePatternType
                    .COMPANION_CONTESTS_WEALTH,

            NatalCrossFunctionalScope
                    .BETWEEN_FUNCTIONS,

            NatalCrossFunctionalInteractionType
                    .COMPETITION,

            NatalTenGodFunctionType
                    .SELF_ASSERTION,

            NatalTenGodFunctionType
                    .RESOURCE_REALITY
    ),

    /**
     * 재다신약.
     *
     * 재성·현실 기능의 요구가
     * 일간 자기기반의 수용 상태와
     * 직접 연결되는 구조.
     */
    RESOURCE_OVERLOADS_CORE_SELF(

            "재성 기능의 자기기반 과중",

            TenGodCorePatternType
                    .WEALTH_HEAVY_WEAK_DAY_MASTER,

            NatalCrossFunctionalScope
                    .FUNCTION_TO_CORE_SELF,

            NatalCrossFunctionalInteractionType
                    .OVERLOAD,

            NatalTenGodFunctionType
                    .RESOURCE_REALITY,

            null
    ),

    /**
     * 관살혼재.
     *
     * RESPONSIBILITY Family 내부에서
     * 정관과 칠살이 함께 작동하는 구조.
     */
    RESPONSIBILITY_INTERNAL_MIXTURE(

            "관살 기능 내부 혼재",

            TenGodCorePatternType
                    .MIXED_DIRECT_OFFICER_AND_SEVEN_KILLINGS,

            NatalCrossFunctionalScope
                    .WITHIN_FUNCTION,

            NatalCrossFunctionalInteractionType
                    .MIXTURE,

            NatalTenGodFunctionType
                    .RESPONSIBILITY,

            null
    );

    private final String koreanName;

    private final TenGodCorePatternType
            sourcePatternType;

    private final NatalCrossFunctionalScope
            scope;

    private final NatalCrossFunctionalInteractionType
            interactionType;

    private final NatalTenGodFunctionType
            primaryFunction;

    private final NatalTenGodFunctionType
            secondaryFunction;

    NatalCrossFunctionalPatternType(

            String koreanName,

            TenGodCorePatternType
                    sourcePatternType,

            NatalCrossFunctionalScope
                    scope,

            NatalCrossFunctionalInteractionType
                    interactionType,

            NatalTenGodFunctionType
                    primaryFunction,

            NatalTenGodFunctionType
                    secondaryFunction

    ) {

        this.koreanName =
                koreanName;

        this.sourcePatternType =
                sourcePatternType;

        this.scope =
                scope;

        this.interactionType =
                interactionType;

        this.primaryFunction =
                primaryFunction;

        this.secondaryFunction =
                secondaryFunction;
    }

    public String koreanName() {

        return koreanName;
    }

    public TenGodCorePatternType sourcePatternType() {

        return sourcePatternType;
    }

    public NatalCrossFunctionalScope scope() {

        return scope;
    }

    public NatalCrossFunctionalInteractionType
    interactionType() {

        return interactionType;
    }

    public NatalTenGodFunctionType primaryFunction() {

        return primaryFunction;
    }

    public Optional<NatalTenGodFunctionType>
    secondaryFunction() {

        return Optional.ofNullable(
                secondaryFunction
        );
    }

    public boolean betweenFunctions() {

        return scope
                == NatalCrossFunctionalScope
                .BETWEEN_FUNCTIONS;
    }

    public boolean functionToCoreSelf() {

        return scope
                == NatalCrossFunctionalScope
                .FUNCTION_TO_CORE_SELF;
    }

    public boolean withinFunction() {

        return scope
                == NatalCrossFunctionalScope
                .WITHIN_FUNCTION;
    }

    /**
     * 기존 10차 Pattern을
     * Functional Pattern으로 1:1 변환한다.
     */
    public static NatalCrossFunctionalPatternType from(

            TenGodCorePatternType
                    sourcePatternType

    ) {

        Objects.requireNonNull(
                sourcePatternType,
                "원본 십성 Pattern 종류는 필수입니다."
        );

        return Arrays
                .stream(
                        values()
                )
                .filter(
                        type ->
                                type.sourcePatternType
                                        == sourcePatternType
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Functional Pattern으로 등록되지 않은 "
                                        + "십성 Pattern입니다: "
                                        + sourcePatternType
                        )
                );
    }
}