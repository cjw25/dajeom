package com.dajeom.type;

import java.util.List;
import java.util.Objects;

/**
 * Legacy Personality Finding Synthesis 식별자.
 *
 * =================================================
 * 현재 공식 정책
 * =================================================
 *
 * 아래 식별자는 과거 모델/API 호환을 위해 유지한다.
 *
 * 그러나 공식 Personality Pipeline에서는
 * 이 조합들로 새로운 사용자 Meaning을 생성하지 않는다.
 *
 * 각각의 requiredThemes가
 * 실제 전통 Pattern으로 독립 성립했다면
 * 각 Pattern은 각각의 Finding으로만 해석한다.
 *
 * 즉:
 *
 * A가 성립하고
 * B가 성립한다
 *
 * !=
 *
 * 프로젝트가
 * A + B = C
 *
 * 라는 새로운 성격 의미를 만든다.
 */
public enum PersonalityFindingSynthesisType {

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

    /**
     * 현재 공식 사용자 Meaning 생성에는
     * 사용하지 않는다.
     */
    public boolean userMeaningEnabled() {

        return false;
    }
}