package com.dajeom.type;

import java.util.List;
import java.util.Objects;

/**
 * 기존 Personality Finding들이
 * 함께 존재할 때 성립하는
 * 구조적 Synthesis 종류.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 여기에 정의된 조합은
 * 기존 Finding Theme의
 * 명시적인 구조 연결만 사용한다.
 *
 * =================================================
 *
 * 새로운:
 *
 * - 심리이론
 * - 점수
 * - 임의 Threshold
 *
 * 를 만들지 않는다.
 *
 * =================================================
 *
 * 또한 requiredThemes 중 하나가
 * REFERENCE라면
 *
 * Synthesis도 강한 판정으로
 * 임의 승격시키지 않는다.
 */
public enum PersonalityFindingSynthesisType {

    /**
     * 상관견관
     * +
     * 상관패인
     *
     * 표현 ↔ 기준 긴장
     * +
     * 인성 → 표현 조절
     */
    EXPRESSION_STANDARD_TENSION_WITH_PROCESSING_REGULATION(

            PersonalityFindingSynthesisKind
                    .TENSION_WITH_REGULATION,

            List.of(
                    PersonalityFindingTheme
                            .EXPRESSION_STANDARD_CONFLICT,

                    PersonalityFindingTheme
                            .EXPRESSION_REGULATED_BY_PROCESSING
            )
    ),

    /**
     * 재생관
     * +
     * 관인상생
     *
     * 재성
     * →
     * 관성
     * →
     * 인성
     */
    REALITY_RESPONSIBILITY_PROCESSING_CHAIN(

            PersonalityFindingSynthesisKind
                    .FUNCTIONAL_CHAIN,

            List.of(
                    PersonalityFindingTheme
                            .REALITY_GENERATES_RESPONSIBILITY,

                    PersonalityFindingTheme
                            .RESPONSIBILITY_GENERATES_PROCESSING
            )
    ),

    /**
     * 살인상생
     * +
     * 상관패인
     *
     * 압력/관살
     * →
     * 인성
     * →
     * 표현 조절
     */
    PRESSURE_PROCESSING_EXPRESSION_CHAIN(

            PersonalityFindingSynthesisKind
                    .FUNCTIONAL_CHAIN,

            List.of(
                    PersonalityFindingTheme
                            .PRESSURE_GENERATES_PROCESSING,

                    PersonalityFindingTheme
                            .EXPRESSION_REGULATED_BY_PROCESSING
            )
    ),

    /**
     * 관살혼재
     * +
     * 관인상생
     *
     * 책임/관성 내부 혼재 구조와
     * 인성 방향의 작용이
     * 동시에 존재.
     *
     * 관인상생이 혼재를
     * 자동으로 해결한다고
     * 판단하지 않는다.
     */
    RESPONSIBILITY_MIXTURE_WITH_PROCESSING_PATH(

            PersonalityFindingSynthesisKind
                    .MIXED_STRUCTURE_WITH_PROCESSING,

            List.of(
                    PersonalityFindingTheme
                            .RESPONSIBILITY_INTERNAL_MIXTURE,

                    PersonalityFindingTheme
                            .RESPONSIBILITY_GENERATES_PROCESSING
            )
    ),

    /**
     * 재극인
     * +
     * 관인상생
     *
     * 재성과 관성이
     * 서로 다른 방식으로
     * 인성 기능에 연결된다.
     *
     * 한쪽은 CONTROL,
     * 한쪽은 GENERATION이다.
     *
     * 따라서 단순 상생 Chain으로
     * 취급하지 않는다.
     */
    REALITY_AND_RESPONSIBILITY_CONVERGE_ON_PROCESSING(

            PersonalityFindingSynthesisKind
                    .FUNCTIONAL_CONVERGENCE,

            List.of(
                    PersonalityFindingTheme
                            .REALITY_CONTROLS_PROCESSING,

                    PersonalityFindingTheme
                            .RESPONSIBILITY_GENERATES_PROCESSING
            )
    ),

    /**
     * 비겁쟁재
     * +
     * 재다신약
     *
     * 재성 영역에서:
     *
     * 비겁의 경쟁/분산 구조
     *
     * +
     *
     * CoreSelf 수용력 부담 구조
     *
     * 가 함께 존재.
     *
     * 이것만으로 실제 돈 문제나
     * 인간관계 사건을 예측하지 않는다.
     */
    RESOURCE_LOAD_WITH_SELF_ASSERTION_COMPETITION(

            PersonalityFindingSynthesisKind
                    .COMPOUND_PRESSURE,

            List.of(
                    PersonalityFindingTheme
                            .SELF_ASSERTION_COMPETES_FOR_RESOURCE,

                    PersonalityFindingTheme
                            .RESOURCE_LOADS_CORE_SELF
            )
    );

    private final PersonalityFindingSynthesisKind kind;

    private final List<PersonalityFindingTheme>
            requiredThemes;

    PersonalityFindingSynthesisType(

            PersonalityFindingSynthesisKind kind,

            List<PersonalityFindingTheme> requiredThemes

    ) {

        this.kind =
                Objects.requireNonNull(
                        kind
                );

        this.requiredThemes =
                List.copyOf(
                        Objects.requireNonNull(
                                requiredThemes
                        )
                );

        if (
                this.requiredThemes.size()
                        < 2
        ) {

            throw new IllegalArgumentException(
                    "Personality Synthesis에는 "
                            + "최소 두 개의 Finding Theme이 필요합니다."
            );
        }
    }

    public PersonalityFindingSynthesisKind kind() {

        return kind;
    }

    public List<PersonalityFindingTheme>
    requiredThemes() {

        return requiredThemes;
    }
}