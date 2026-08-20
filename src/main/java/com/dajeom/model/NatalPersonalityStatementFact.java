package com.dajeom.model;

import com.dajeom.type.NatalResponseSituationType;
import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.PersonalityFindingSynthesisType;
import com.dajeom.type.PersonalityFindingTheme;
import com.dajeom.type.PersonalityJudgmentGroundingLevel;
import com.dajeom.type.PersonalityStatementExpressionMode;
import com.dajeom.type.PersonalityStatementFactType;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 사용자용 Personality 설명의
 * 구조화된 의미 단위.
 *
 * =================================================
 *
 * 아직 최종 한국어 문장은 아니다.
 *
 * =================================================
 *
 * 예:
 *
 * type
 * =
 * SYNTHESIS
 *
 * synthesisType
 * =
 * EXPRESSION_STANDARD_TENSION_WITH_PROCESSING_REGULATION
 *
 * involvedFunctions
 * =
 * EXPRESSION
 * RESPONSIBILITY
 * PROCESSING
 *
 * expressionMode
 * =
 * STRONG_DIRECT_WITH_QUALIFICATION
 *
 * =================================================
 *
 * 이 정도까지 Java가 확정하고
 * OpenAI는 이 구조를 자연어로
 * 표현하는 역할만 맡는다.
 *
 * =================================================
 *
 * absorbedByDirectSynthesis
 *
 * 개별 Finding이
 * 더 높은 복합 Synthesis에 포함되어
 * 사용자 출력에서 중복을 피해야 하는지 나타낸다.
 *
 * 중요:
 *
 * true라도 Fact와 Source Evidence 자체는
 * 삭제되지 않는다.
 */
public record NatalPersonalityStatementFact(

        PersonalityStatementFactType factType,

        Optional<PersonalityFindingTheme> findingTheme,

        Optional<PersonalityFindingSynthesisType> synthesisType,

        List<NatalTenGodFunctionType> involvedFunctions,

        List<NatalResponseSituationType> situations,

        PersonalityJudgmentGroundingLevel groundingLevel,

        PersonalityStatementExpressionMode expressionMode,

        boolean absorbedByDirectSynthesis,

        List<NatalPersonalityFinding> sourceFindings,

        List<NatalPersonalityFindingSynthesis> sourceSyntheses,

        boolean completeChart

) {

    public NatalPersonalityStatementFact {

        Objects.requireNonNull(
                factType,
                "Personality Statement Fact 종류는 필수입니다."
        );

        findingTheme =
                findingTheme == null
                        ? Optional.empty()
                        : findingTheme;

        synthesisType =
                synthesisType == null
                        ? Optional.empty()
                        : synthesisType;

        involvedFunctions =
                involvedFunctions == null
                        ? List.of()
                        : List.copyOf(
                        involvedFunctions
                );

        situations =
                situations == null
                        ? List.of()
                        : List.copyOf(
                        situations
                );

        Objects.requireNonNull(
                groundingLevel,
                "Statement Fact Grounding Level은 필수입니다."
        );

        Objects.requireNonNull(
                expressionMode,
                "Statement Fact 표현방식은 필수입니다."
        );

        sourceFindings =
                sourceFindings == null
                        ? List.of()
                        : List.copyOf(
                        sourceFindings
                );

        sourceSyntheses =
                sourceSyntheses == null
                        ? List.of()
                        : List.copyOf(
                        sourceSyntheses
                );

        /*
         * =================================================
         * 기능축 중복 금지
         * =================================================
         */

        Set<NatalTenGodFunctionType> functionSet =
                new LinkedHashSet<>();

        for (
                NatalTenGodFunctionType functionType
                : involvedFunctions
        ) {

            Objects.requireNonNull(
                    functionType,
                    "Statement Fact 기능축에는 null을 사용할 수 없습니다."
            );

            if (
                    !functionSet.add(
                            functionType
                    )
            ) {

                throw new IllegalArgumentException(
                        "Statement Fact 기능축이 중복되었습니다: "
                                + functionType
                );
            }
        }

        /*
         * =================================================
         * 상황 중복 금지
         * =================================================
         */

        Set<NatalResponseSituationType> situationSet =
                new LinkedHashSet<>();

        for (
                NatalResponseSituationType situation
                : situations
        ) {

            Objects.requireNonNull(
                    situation,
                    "Statement Fact 상황에는 null을 사용할 수 없습니다."
            );

            if (
                    !situationSet.add(
                            situation
                    )
            ) {

                throw new IllegalArgumentException(
                        "Statement Fact 상황이 중복되었습니다: "
                                + situation
                );
            }
        }

        /*
         * =================================================
         * Fact Type별 구조 검증
         * =================================================
         */

        switch (
                factType
        ) {

            case CORE_SELF,
                 FUNCTION,
                 CROSS_PATTERN,
                 INTERPERSONAL_CONTEXT -> {

                if (
                        findingTheme.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            factType
                                    + " Statement Fact에는 "
                                    + "Finding Theme이 필요합니다."
                    );
                }

                if (
                        synthesisType.isPresent()
                ) {

                    throw new IllegalArgumentException(
                            factType
                                    + " Statement Fact에는 "
                                    + "Synthesis Type을 사용할 수 없습니다."
                    );
                }

                if (
                        sourceFindings.size()
                                != 1
                ) {

                    throw new IllegalArgumentException(
                            factType
                                    + " Statement Fact에는 "
                                    + "정확히 하나의 Source Finding이 필요합니다."
                    );
                }

                if (
                        !sourceSyntheses.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            factType
                                    + " Statement Fact에는 "
                                    + "Source Synthesis가 없어야 합니다."
                    );
                }
            }

            case SYNTHESIS -> {

                if (
                        findingTheme.isPresent()
                ) {

                    throw new IllegalArgumentException(
                            "SYNTHESIS Statement Fact에는 "
                                    + "단일 Finding Theme을 사용하지 않습니다."
                    );
                }

                if (
                        synthesisType.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            "SYNTHESIS Statement Fact에는 "
                                    + "Synthesis Type이 필요합니다."
                    );
                }

                if (
                        sourceFindings.size()
                                < 2
                ) {

                    throw new IllegalArgumentException(
                            "SYNTHESIS Statement Fact에는 "
                                    + "최소 두 개의 Source Finding이 필요합니다."
                    );
                }

                if (
                        sourceSyntheses.size()
                                != 1
                ) {

                    throw new IllegalArgumentException(
                            "SYNTHESIS Statement Fact에는 "
                                    + "정확히 하나의 Source Synthesis가 필요합니다."
                    );
                }
            }
        }

        /*
         * =================================================
         * 원국 완전성 검증
         * =================================================
         */

        for (
                NatalPersonalityFinding finding
                : sourceFindings
        ) {

            if (
                    finding.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Statement Fact와 Source Finding의 "
                                + "원국 완전성 상태가 일치하지 않습니다."
                );
            }
        }

        for (
                NatalPersonalityFindingSynthesis synthesis
                : sourceSyntheses
        ) {

            if (
                    synthesis.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Statement Fact와 Source Synthesis의 "
                                + "원국 완전성 상태가 일치하지 않습니다."
                );
            }
        }

        /*
         * =================================================
         * Expression Mode와 Grounding 검증
         * =================================================
         */

        if (
                expressionMode.userFacing()

                        && !groundingLevel
                        .directAssertionAllowed()
        ) {

            throw new IllegalArgumentException(
                    "REFERENCE/CONTEXTUAL Grounding을 "
                            + "사용자 직접 표현 Mode로 사용할 수 없습니다."
            );
        }

        if (
                expressionMode.strong()

                        && groundingLevel
                        != PersonalityJudgmentGroundingLevel
                        .STRONGLY_GROUNDED
        ) {

            throw new IllegalArgumentException(
                    "Strong Expression Mode는 "
                            + "STRONGLY_GROUNDED Fact에만 사용할 수 있습니다."
            );
        }

        if (
                groundingLevel
                        == PersonalityJudgmentGroundingLevel
                        .STRONGLY_GROUNDED

                        && !expressionMode
                        .strong()
        ) {

            throw new IllegalArgumentException(
                    "STRONGLY_GROUNDED Fact는 "
                            + "Strong Expression Mode를 사용해야 합니다."
            );
        }

        /*
         * =================================================
         * Absorption은 Finding Fact에만 허용
         * =================================================
         */

        if (
                absorbedByDirectSynthesis

                        && factType
                        == PersonalityStatementFactType
                        .SYNTHESIS
        ) {

            throw new IllegalArgumentException(
                    "SYNTHESIS Fact 자체를 "
                            + "다른 Synthesis에 흡수된 것으로 표시할 수 없습니다."
            );
        }
    }

    /**
     * 실제 최종 사용자 설명 후보.
     *
     * 직접표현 가능하면서
     * 상위 Synthesis에 흡수되지 않은 경우.
     */
    public boolean userFacingCandidate() {

        return expressionMode
                .userFacing()

                && !absorbedByDirectSynthesis;
    }

    /**
     * 사용자에게 강하게 표현할 수 있는 Fact.
     */
    public boolean strongUserFacingCandidate() {

        return userFacingCandidate()

                && expressionMode
                .strong();
    }

    /**
     * 설명할 때 반드시 조건을 같이
     * 표현해야 하는 Fact.
     */
    public boolean qualificationRequired() {

        return expressionMode
                .qualificationRequired();
    }

    /**
     * 내부 근거로만 사용되는가.
     */
    public boolean internalOnly() {

        return !userFacingCandidate();
    }
}