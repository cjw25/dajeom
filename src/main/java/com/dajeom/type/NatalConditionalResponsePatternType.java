package com.dajeom.type;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 원국에서 확인된 CrossFunctionalPattern을
 * 상황별 기능 작동순서로 변환한 타입.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 새로운 명리 Pattern을 만들지 않는다.
 *
 * 직전 단계의:
 *
 * NatalCrossFunctionalPatternType
 *
 * 11종과 정확히 1:1 대응한다.
 *
 * =================================================
 *
 * functionalSequence
 *
 * 실제 시간 순서를 의미하는
 * 심리학적 단계가 아니다.
 *
 * 명리 구조상:
 *
 * 어떤 기능이
 * 어떤 기능에 작용하는가
 *
 * 를 방향성 있게 보존한다.
 *
 * =================================================
 *
 * 예:
 *
 * 관인상생
 *
 * RESPONSIBILITY
 * →
 * PROCESSING
 *
 * =================================================
 *
 * 상관패인
 *
 * PROCESSING
 * →
 * EXPRESSION
 *
 * =================================================
 *
 * 재다신약
 *
 * RESOURCE_REALITY
 * →
 * CORE_SELF
 *
 * CORE_SELF는 십성 기능축이 아니므로
 * functionalSequence에는 재성만 두고
 * coreSelfIncluded=true로 별도 표시한다.
 */
public enum NatalConditionalResponsePatternType {

    /**
     * 식신제살.
     *
     * 식상 기능이
     * 칠살 압력 기능을 제어한다.
     */
    PRESSURE_REGULATED_BY_EXPRESSION(

            "압력에 대한 표현 기능의 제어",

            NatalCrossFunctionalPatternType
                    .EXPRESSION_CONTROLS_PRESSURE,

            NatalResponseSituationType
                    .PRESSURE_AND_RESPONSIBILITY,

            List.of(
                    NatalTenGodFunctionType
                            .EXPRESSION,

                    NatalTenGodFunctionType
                            .RESPONSIBILITY
            ),

            false
    ),

    /**
     * 상관견관.
     *
     * 표현 기능과
     * 관성 기능의 직접 충돌.
     */
    EXPRESSION_STANDARD_CONFLICT(

            "표현과 규범 기능의 직접 충돌",

            NatalCrossFunctionalPatternType
                    .EXPRESSION_CONFLICTS_RESPONSIBILITY,

            NatalResponseSituationType
                    .RULES_AND_EXPRESSION,

            List.of(
                    NatalTenGodFunctionType
                            .EXPRESSION,

                    NatalTenGodFunctionType
                            .RESPONSIBILITY
            ),

            false
    ),

    /**
     * 상관패인.
     *
     * 인성 기능이
     * 상관 표현 기능을 조절한다.
     */
    EXPRESSION_REGULATED_BY_PROCESSING(

            "수용·처리 기능에 의한 표현 조절",

            NatalCrossFunctionalPatternType
                    .PROCESSING_REGULATES_EXPRESSION,

            NatalResponseSituationType
                    .PROCESSING_AND_EXPRESSION,

            List.of(
                    NatalTenGodFunctionType
                            .PROCESSING,

                    NatalTenGodFunctionType
                            .EXPRESSION
            ),

            false
    ),

    /**
     * 편인도식 / 효신탈식.
     *
     * 인성 기능이
     * 식신 산출 기능을 제어한다.
     */
    OUTPUT_RESTRAINED_BY_PROCESSING(

            "수용·처리 기능에 의한 산출 억제",

            NatalCrossFunctionalPatternType
                    .PROCESSING_RESTRAINS_OUTPUT,

            NatalResponseSituationType
                    .PROCESSING_AND_EXPRESSION,

            List.of(
                    NatalTenGodFunctionType
                            .PROCESSING,

                    NatalTenGodFunctionType
                            .EXPRESSION
            ),

            false
    ),

    /**
     * 재생관.
     *
     * 재성 기능이
     * 관성 기능으로 연결된다.
     */
    REALITY_GENERATES_RESPONSIBILITY(

            "현실·자원 기능에서 책임 기능으로의 연결",

            NatalCrossFunctionalPatternType
                    .RESOURCE_GENERATES_RESPONSIBILITY,

            NatalResponseSituationType
                    .REALITY_AND_RESPONSIBILITY,

            List.of(
                    NatalTenGodFunctionType
                            .RESOURCE_REALITY,

                    NatalTenGodFunctionType
                            .RESPONSIBILITY
            ),

            false
    ),

    /**
     * 관인상생.
     *
     * 관성 기능이
     * 인성 기능으로 연결된다.
     */
    RESPONSIBILITY_GENERATES_PROCESSING(

            "책임 기능에서 수용·처리 기능으로의 연결",

            NatalCrossFunctionalPatternType
                    .RESPONSIBILITY_GENERATES_PROCESSING,

            NatalResponseSituationType
                    .PRESSURE_AND_RESPONSIBILITY,

            List.of(
                    NatalTenGodFunctionType
                            .RESPONSIBILITY,

                    NatalTenGodFunctionType
                            .PROCESSING
            ),

            false
    ),

    /**
     * 살인상생.
     *
     * 칠살 압력 기능이
     * 인성 기능으로 연결된다.
     *
     * Functional Family에서는
     * 칠살도 RESPONSIBILITY에 포함된다.
     */
    PRESSURE_GENERATES_PROCESSING(

            "압력 기능에서 수용·처리 기능으로의 연결",

            NatalCrossFunctionalPatternType
                    .PRESSURE_GENERATES_PROCESSING,

            NatalResponseSituationType
                    .PRESSURE_AND_RESPONSIBILITY,

            List.of(
                    NatalTenGodFunctionType
                            .RESPONSIBILITY,

                    NatalTenGodFunctionType
                            .PROCESSING
            ),

            false
    ),

    /**
     * 재극인.
     *
     * 현실·재성 기능이
     * 인성 기능을 제어한다.
     */
    REALITY_CONTROLS_PROCESSING(

            "현실·자원 기능의 수용·처리 기능 제어",

            NatalCrossFunctionalPatternType
                    .RESOURCE_CONTROLS_PROCESSING,

            NatalResponseSituationType
                    .REALITY_AND_PROCESSING,

            List.of(
                    NatalTenGodFunctionType
                            .RESOURCE_REALITY,

                    NatalTenGodFunctionType
                            .PROCESSING
            ),

            false
    ),

    /**
     * 비겁쟁재.
     *
     * 자기주도·경쟁 기능과
     * 현실·자원 기능이 경합한다.
     */
    SELF_ASSERTION_COMPETES_FOR_RESOURCE(

            "자기주도·경쟁 기능과 현실·자원 기능의 경합",

            NatalCrossFunctionalPatternType
                    .SELF_ASSERTION_COMPETES_FOR_RESOURCE,

            NatalResponseSituationType
                    .COMPETITION_AND_RESOURCE,

            List.of(
                    NatalTenGodFunctionType
                            .SELF_ASSERTION,

                    NatalTenGodFunctionType
                            .RESOURCE_REALITY
            ),

            false
    ),

    /**
     * 재다신약.
     *
     * 재성 기능과
     * 일간 자기기반의 수용 상태가
     * 직접 연결된다.
     */
    RESOURCE_LOADS_CORE_SELF(

            "현실·자원 기능의 자기기반 부담",

            NatalCrossFunctionalPatternType
                    .RESOURCE_OVERLOADS_CORE_SELF,

            NatalResponseSituationType
                    .RESOURCE_CAPACITY,

            List.of(
                    NatalTenGodFunctionType
                            .RESOURCE_REALITY
            ),

            true
    ),

    /**
     * 관살혼재.
     *
     * 책임·관살 Family 내부에서
     * 복수 기능이 동시에 작동한다.
     */
    RESPONSIBILITY_INTERNAL_MIXTURE(

            "책임·압력 기능 내부의 혼재",

            NatalCrossFunctionalPatternType
                    .RESPONSIBILITY_INTERNAL_MIXTURE,

            NatalResponseSituationType
                    .MIXED_RESPONSIBILITY,

            List.of(
                    NatalTenGodFunctionType
                            .RESPONSIBILITY
            ),

            false
    );

    private final String koreanName;

    private final NatalCrossFunctionalPatternType
            sourcePatternType;

    private final NatalResponseSituationType
            situationType;

    private final List<NatalTenGodFunctionType>
            functionalSequence;

    private final boolean coreSelfIncluded;

    NatalConditionalResponsePatternType(

            String koreanName,

            NatalCrossFunctionalPatternType
                    sourcePatternType,

            NatalResponseSituationType
                    situationType,

            List<NatalTenGodFunctionType>
                    functionalSequence,

            boolean coreSelfIncluded

    ) {

        this.koreanName =
                koreanName;

        this.sourcePatternType =
                sourcePatternType;

        this.situationType =
                situationType;

        this.functionalSequence =
                List.copyOf(
                        functionalSequence
                );

        this.coreSelfIncluded =
                coreSelfIncluded;
    }

    public String koreanName() {

        return koreanName;
    }

    public NatalCrossFunctionalPatternType
    sourcePatternType() {

        return sourcePatternType;
    }

    public NatalResponseSituationType
    situationType() {

        return situationType;
    }

    public List<NatalTenGodFunctionType>
    functionalSequence() {

        return functionalSequence;
    }

    public boolean coreSelfIncluded() {

        return coreSelfIncluded;
    }

    /**
     * 기존 Functional Cross Pattern을
     * Conditional Response Pattern으로
     * 정확히 1:1 변환한다.
     */
    public static NatalConditionalResponsePatternType from(

            NatalCrossFunctionalPatternType
                    sourcePatternType

    ) {

        Objects.requireNonNull(
                sourcePatternType,
                "원본 Functional Pattern은 필수입니다."
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
                                "Conditional Response로 "
                                        + "등록되지 않은 Functional Pattern입니다: "
                                        + sourcePatternType
                        )
                );
    }
}