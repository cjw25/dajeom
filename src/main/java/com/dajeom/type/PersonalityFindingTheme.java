package com.dajeom.type;

import java.util.Objects;

/**
 * 기본성향 Finding의 구조적 의미.
 *
 * =================================================
 *
 * 아직 사용자에게 보여줄
 * 한국어 성격문장이 아니다.
 *
 * =================================================
 *
 * 목적:
 *
 * OpenAI 또는 후속 Statement 계층이
 *
 * sourceCode 문자열을 파싱하지 않고
 * 구조화된 Theme만 보고
 * 문장을 선택할 수 있게 한다.
 */
public enum PersonalityFindingTheme {

    /*
     * =================================================
     * CoreSelf
     * =================================================
     */

    CORE_OPERATING_CAPACITY,

    /*
     * =================================================
     * 십성 5대 기능
     * =================================================
     */

    SELF_ASSERTION_FUNCTION,

    EXPRESSION_FUNCTION,

    RESOURCE_REALITY_FUNCTION,

    RESPONSIBILITY_FUNCTION,

    PROCESSING_FUNCTION,

    /*
     * =================================================
     * 전통 Cross Pattern
     * =================================================
     */

    PRESSURE_REGULATED_BY_EXPRESSION,

    EXPRESSION_STANDARD_CONFLICT,

    EXPRESSION_REGULATED_BY_PROCESSING,

    OUTPUT_RESTRAINED_BY_PROCESSING,

    REALITY_GENERATES_RESPONSIBILITY,

    RESPONSIBILITY_GENERATES_PROCESSING,

    PRESSURE_GENERATES_PROCESSING,

    REALITY_CONTROLS_PROCESSING,

    SELF_ASSERTION_COMPETES_FOR_RESOURCE,

    RESOURCE_LOADS_CORE_SELF,

    RESPONSIBILITY_INTERNAL_MIXTURE,

    /*
     * =================================================
     * 대인관계 Context
     * =================================================
     */

    INTERPERSONAL_RELATION_CONTEXT;

    /**
     * 십성 기능축 → Finding Theme.
     */
    public static PersonalityFindingTheme fromFunction(

            NatalTenGodFunctionType functionType

    ) {

        Objects.requireNonNull(
                functionType,
                "십성 기능축은 필수입니다."
        );

        return switch (
                functionType
                ) {

            case SELF_ASSERTION ->
                    SELF_ASSERTION_FUNCTION;

            case EXPRESSION ->
                    EXPRESSION_FUNCTION;

            case RESOURCE_REALITY ->
                    RESOURCE_REALITY_FUNCTION;

            case RESPONSIBILITY ->
                    RESPONSIBILITY_FUNCTION;

            case PROCESSING ->
                    PROCESSING_FUNCTION;
        };
    }

    /**
     * CrossFunctional Pattern
     * →
     * Personality Finding Theme.
     *
     * 기존 Pattern을
     * 새 심리이론으로 변환하는 것이 아니라
     * 구조적 의미 이름만 정규화한다.
     */
    public static PersonalityFindingTheme fromCrossPattern(

            NatalCrossFunctionalPatternType patternType

    ) {

        Objects.requireNonNull(
                patternType,
                "CrossFunctional Pattern은 필수입니다."
        );

        return switch (
                patternType
                ) {

            case EXPRESSION_CONTROLS_PRESSURE ->
                    PRESSURE_REGULATED_BY_EXPRESSION;

            case EXPRESSION_CONFLICTS_RESPONSIBILITY ->
                    EXPRESSION_STANDARD_CONFLICT;

            case PROCESSING_REGULATES_EXPRESSION ->
                    EXPRESSION_REGULATED_BY_PROCESSING;

            case PROCESSING_RESTRAINS_OUTPUT ->
                    OUTPUT_RESTRAINED_BY_PROCESSING;

            case RESOURCE_GENERATES_RESPONSIBILITY ->
                    REALITY_GENERATES_RESPONSIBILITY;

            case RESPONSIBILITY_GENERATES_PROCESSING ->
                    RESPONSIBILITY_GENERATES_PROCESSING;

            case PRESSURE_GENERATES_PROCESSING ->
                    PRESSURE_GENERATES_PROCESSING;

            case RESOURCE_CONTROLS_PROCESSING ->
                    REALITY_CONTROLS_PROCESSING;

            case SELF_ASSERTION_COMPETES_FOR_RESOURCE ->
                    SELF_ASSERTION_COMPETES_FOR_RESOURCE;

            case RESOURCE_OVERLOADS_CORE_SELF ->
                    RESOURCE_LOADS_CORE_SELF;

            case RESPONSIBILITY_INTERNAL_MIXTURE ->
                    RESPONSIBILITY_INTERNAL_MIXTURE;
        };
    }
}