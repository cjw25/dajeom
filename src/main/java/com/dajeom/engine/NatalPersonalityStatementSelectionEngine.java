package com.dajeom.engine;

import com.dajeom.model.NatalPersonalityStatementFact;
import com.dajeom.model.NatalPersonalityStatementFactAnalysis;
import com.dajeom.model.NatalPersonalityStatementSelection;
import com.dajeom.model.NatalPersonalityStatementSelectionAnalysis;
import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.PersonalityStatementFactType;
import com.dajeom.type.PersonalityStatementSelectionReason;
import com.dajeom.type.PersonalityStatementSelectionRole;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Personality Statement Fact를
 *
 * PRIMARY
 * SUPPORTING
 * BACKGROUND_CONTEXT
 * INTERNAL_REFERENCE
 *
 * 로 선택한다.
 *
 * =================================================
 *
 * 핵심 원칙:
 *
 * 1.
 * 점수 없음.
 *
 * 2.
 * Fact 개수 다수결 없음.
 *
 * 3.
 * Grounding의 기존 강도를
 * 임의 재계산하지 않는다.
 *
 * 4.
 * 직접 Synthesis는
 * 개별 Pattern보다
 * 완성된 구조 설명을 제공하므로
 * 중심 Fact로 사용한다.
 *
 * 5.
 * Synthesis에 참여한 Function은
 * 삭제하지 않는다.
 *
 * 다만 동일 내용을
 * 별도 중심문장으로 반복하지 않도록
 * SUPPORTING으로 사용한다.
 *
 * 6.
 * 주격 중심 Function은
 * Synthesis에 덮이지 않은 경우
 * 중심 Personality Fact가 될 수 있다.
 *
 * 7.
 * CoreSelf는:
 *
 * 신강 = 고집
 * 신약 = 소심
 *
 * 같은 잘못된 변환을 막기 위해
 * BACKGROUND_CONTEXT로 사용한다.
 *
 * 8.
 * 원국 관계 Context도
 * 직접 심리판정으로 확대하지 않는다.
 *
 * 9.
 * qualificationRequired는
 * 삭제하지 않고 Selection Reason에 보존한다.
 *
 * 다음 Bundle 단계에서
 * 반대축/중재축과 함께 묶는다.
 */
@Component
public class NatalPersonalityStatementSelectionEngine {

    public NatalPersonalityStatementSelectionAnalysis analyze(

            NatalPersonalityStatementFactAnalysis
                    statementFactAnalysis

    ) {

        Objects.requireNonNull(
                statementFactAnalysis,
                "Personality Statement Fact Analysis는 필수입니다."
        );

        List<NatalPersonalityStatementFact>
                directSynthesisFacts =
                statementFactAnalysis
                        .factsOfType(
                                PersonalityStatementFactType
                                        .SYNTHESIS
                        )
                        .stream()
                        .filter(
                                NatalPersonalityStatementFact
                                        ::userFacingCandidate
                        )
                        .toList();

        List<NatalPersonalityStatementSelection>
                selections =
                new ArrayList<>();

        /*
         * =================================================
         * 모든 Fact를 정확히 하나의 Role로 분류
         * =================================================
         */

        for (
                NatalPersonalityStatementFact fact
                : statementFactAnalysis.facts()
        ) {

            selections.add(
                    select(
                            statementFactAnalysis,
                            fact,
                            directSynthesisFacts
                    )
            );
        }

        return new NatalPersonalityStatementSelectionAnalysis(

                statementFactAnalysis,

                selections,

                statementFactAnalysis.completeChart()
        );
    }

    /**
     * Fact 하나의 Role 결정.
     */
    private NatalPersonalityStatementSelection select(

            NatalPersonalityStatementFactAnalysis
                    statementFactAnalysis,

            NatalPersonalityStatementFact fact,

            List<NatalPersonalityStatementFact>
                    directSynthesisFacts

    ) {

        /*
         * =================================================
         * 1.
         * 실제 Synthesis에 흡수된 Cross Fact
         * =================================================
         */

        if (
                fact.absorbedByDirectSynthesis()
        ) {

            return createSelection(

                    fact,

                    PersonalityStatementSelectionRole
                            .INTERNAL_REFERENCE,

                    false,

                    PersonalityStatementSelectionReason
                            .ABSORBED_BY_SYNTHESIS
            );
        }

        /*
         * =================================================
         * 2.
         * CoreSelf
         *
         * 명리적 운용 Context이지
         * 직접 심리문장이 아니다.
         * =================================================
         */

        if (
                fact.factType()
                        == PersonalityStatementFactType
                        .CORE_SELF
        ) {

            return createSelection(

                    fact,

                    PersonalityStatementSelectionRole
                            .BACKGROUND_CONTEXT,

                    false,

                    PersonalityStatementSelectionReason
                            .CORE_SELF_AS_CONTEXT
            );
        }

        /*
         * =================================================
         * 3.
         * 대인관계 원국 Context
         *
         * 합충이 있다고:
         *
         * 사람을 못 믿는다
         * 사교적이다
         * 관계에 집착한다
         *
         * 로 바로 변환하지 않는다.
         * =================================================
         */

        if (
                fact.factType()
                        == PersonalityStatementFactType
                        .INTERPERSONAL_CONTEXT
        ) {

            List<PersonalityStatementSelectionReason> reasons =
                    new ArrayList<>();

            reasons.add(
                    PersonalityStatementSelectionReason
                            .INTERPERSONAL_AS_CONTEXT
            );

            if (
                    fact.qualificationRequired()
            ) {

                reasons.add(
                        PersonalityStatementSelectionReason
                                .QUALIFICATION_REQUIRED
                );
            }

            return new NatalPersonalityStatementSelection(

                    fact,

                    PersonalityStatementSelectionRole
                            .BACKGROUND_CONTEXT,

                    reasons,

                    false,

                    fact.completeChart()
            );
        }

        /*
         * =================================================
         * 4.
         * 직접 사용자 설명을 허용하지 않는 Fact
         * =================================================
         */

        if (
                !fact.userFacingCandidate()
        ) {

            List<PersonalityStatementSelectionReason> reasons =
                    new ArrayList<>();

            reasons.add(
                    PersonalityStatementSelectionReason
                            .NOT_DIRECTLY_ASSERTABLE
            );

            if (
                    fact.qualificationRequired()
            ) {

                reasons.add(
                        PersonalityStatementSelectionReason
                                .QUALIFICATION_REQUIRED
                );
            }

            return new NatalPersonalityStatementSelection(

                    fact,

                    PersonalityStatementSelectionRole
                            .INTERNAL_REFERENCE,

                    reasons,

                    false,

                    fact.completeChart()
            );
        }

        /*
         * =================================================
         * 5.
         * 직접 Synthesis
         *
         * 복합 구조 자체를
         * 중심 설명으로 선택한다.
         * =================================================
         */

        if (
                fact.factType()
                        == PersonalityStatementFactType
                        .SYNTHESIS
        ) {

            List<PersonalityStatementSelectionReason> reasons =
                    new ArrayList<>();

            if (
                    fact.strongUserFacingCandidate()
            ) {

                reasons.add(
                        PersonalityStatementSelectionReason
                                .STRONG_SYNTHESIS
                );

            } else {

                reasons.add(
                        PersonalityStatementSelectionReason
                                .DIRECT_SYNTHESIS
                );
            }

            addQualificationReason(
                    fact,
                    reasons
            );

            return new NatalPersonalityStatementSelection(

                    fact,

                    PersonalityStatementSelectionRole
                            .PRIMARY,

                    reasons,

                    false,

                    fact.completeChart()
            );
        }

        /*
         * =================================================
         * 6.
         * Function
         * =================================================
         */

        if (
                fact.factType()
                        == PersonalityStatementFactType
                        .FUNCTION
        ) {

            return selectFunction(

                    statementFactAnalysis,

                    fact,

                    directSynthesisFacts
            );
        }

        /*
         * =================================================
         * 7.
         * Synthesis에 흡수되지 않은 Cross Pattern
         * =================================================
         */

        if (
                fact.factType()
                        == PersonalityStatementFactType
                        .CROSS_PATTERN
        ) {

            return selectStandaloneCrossPattern(
                    fact
            );
        }

        throw new IllegalStateException(
                "처리되지 않은 Personality Statement Fact Type입니다: "
                        + fact.factType()
        );
    }

    /**
     * =================================================
     * Function 선택
     * =================================================
     */
    private NatalPersonalityStatementSelection
    selectFunction(

            NatalPersonalityStatementFactAnalysis
                    statementFactAnalysis,

            NatalPersonalityStatementFact fact,

            List<NatalPersonalityStatementFact>
                    directSynthesisFacts

    ) {

        NatalTenGodFunctionType functionType =
                requireSingleFunction(
                        fact
                );

        boolean structurallyCentered =
                statementFactAnalysis
                        .synthesisAnalysis()
                        .findingAnalysis()
                        .deterministicAnalysis()
                        .patternAnalysis()
                        .requireFunction(
                                functionType
                        )
                        .structuralCenter();

        boolean coveredByDirectSynthesis =
                directSynthesisFacts
                        .stream()
                        .anyMatch(
                                synthesisFact ->
                                        synthesisFact
                                                .involvedFunctions()
                                                .contains(
                                                        functionType
                                                )
                        );

        List<PersonalityStatementSelectionReason> reasons =
                new ArrayList<>();

        /*
         * =================================================
         * 이미 강한 복합설명 안에 들어간 Function.
         *
         * 삭제하지 않고 SUPPORTING.
         *
         * 예:
         *
         * EXPRESSION Function
         *
         * +
         *
         * EXPRESSION ↔ RESPONSIBILITY
         * +
         * PROCESSING → EXPRESSION
         *
         * Synthesis가 있으면
         *
         * Function 자체는 그 Synthesis의
         * 설명 배경/보강으로 붙는다.
         * =================================================
         */

        if (
                coveredByDirectSynthesis
        ) {

            reasons.add(
                    PersonalityStatementSelectionReason
                            .FUNCTION_COVERED_BY_DIRECT_SYNTHESIS
            );

            if (
                    structurallyCentered
            ) {

                reasons.add(
                        PersonalityStatementSelectionReason
                                .STRUCTURAL_CENTER_FUNCTION
                );
            }

            if (
                    fact.strongUserFacingCandidate()
            ) {

                reasons.add(
                        PersonalityStatementSelectionReason
                                .STRONG_FUNCTION
                );

            } else {

                reasons.add(
                        PersonalityStatementSelectionReason
                                .DIRECT_FUNCTION
                );
            }

            addQualificationReason(
                    fact,
                    reasons
            );

            return new NatalPersonalityStatementSelection(

                    fact,

                    PersonalityStatementSelectionRole
                            .SUPPORTING,

                    reasons,

                    true,

                    fact.completeChart()
            );
        }

        /*
         * =================================================
         * Synthesis에 덮이지 않은 Function.
         *
         * 주격 중심 또는 강한 Function이면
         * 자체가 PRIMARY가 될 수 있다.
         * =================================================
         */

        if (
                structurallyCentered
                        || fact.strongUserFacingCandidate()
        ) {

            if (
                    structurallyCentered
            ) {

                reasons.add(
                        PersonalityStatementSelectionReason
                                .STRUCTURAL_CENTER_FUNCTION
                );
            }

            if (
                    fact.strongUserFacingCandidate()
            ) {

                reasons.add(
                        PersonalityStatementSelectionReason
                                .STRONG_FUNCTION
                );

            } else {

                reasons.add(
                        PersonalityStatementSelectionReason
                                .DIRECT_FUNCTION
                );
            }

            addQualificationReason(
                    fact,
                    reasons
            );

            return new NatalPersonalityStatementSelection(

                    fact,

                    PersonalityStatementSelectionRole
                            .PRIMARY,

                    reasons,

                    false,

                    fact.completeChart()
            );
        }

        /*
         * =================================================
         * 일반 GROUNDED Function.
         *
         * 의미는 분명하지만
         * 중심 구조까지는 아니므로 SUPPORTING.
         * =================================================
         */

        reasons.add(
                PersonalityStatementSelectionReason
                        .DIRECT_FUNCTION
        );

        addQualificationReason(
                fact,
                reasons
        );

        return new NatalPersonalityStatementSelection(

                fact,

                PersonalityStatementSelectionRole
                        .SUPPORTING,

                reasons,

                false,

                fact.completeChart()
        );
    }

    /**
     * =================================================
     * 단독 Cross Pattern
     * =================================================
     *
     * 직접 Synthesis에 흡수되지 않은
     * 실제 Pattern만 여기까지 온다.
     */
    private NatalPersonalityStatementSelection
    selectStandaloneCrossPattern(

            NatalPersonalityStatementFact fact

    ) {

        List<PersonalityStatementSelectionReason> reasons =
                new ArrayList<>();

        PersonalityStatementSelectionRole role;

        if (
                fact.strongUserFacingCandidate()
        ) {

            role =
                    PersonalityStatementSelectionRole
                            .PRIMARY;

            reasons.add(
                    PersonalityStatementSelectionReason
                            .STANDALONE_STRONG_CROSS_PATTERN
            );

        } else {

            role =
                    PersonalityStatementSelectionRole
                            .SUPPORTING;

            reasons.add(
                    PersonalityStatementSelectionReason
                            .STANDALONE_CROSS_PATTERN
            );
        }

        addQualificationReason(
                fact,
                reasons
        );

        return new NatalPersonalityStatementSelection(

                fact,

                role,

                reasons,

                false,

                fact.completeChart()
        );
    }

    /**
     * FUNCTION Fact는
     * 정확히 하나의 기능축을 가져야 한다.
     */
    private NatalTenGodFunctionType
    requireSingleFunction(

            NatalPersonalityStatementFact fact

    ) {

        if (
                fact.involvedFunctions()
                        .size()
                        != 1
        ) {

            throw new IllegalStateException(
                    "FUNCTION Statement Fact에는 "
                            + "정확히 하나의 기능축이 필요합니다."
            );
        }

        return fact
                .involvedFunctions()
                .get(0);
    }

    /**
     * Qualification을 Selection에도
     * 명시적으로 보존한다.
     */
    private void addQualificationReason(

            NatalPersonalityStatementFact fact,

            List<PersonalityStatementSelectionReason> reasons

    ) {

        if (
                fact.qualificationRequired()
        ) {

            reasons.add(
                    PersonalityStatementSelectionReason
                            .QUALIFICATION_REQUIRED
            );
        }
    }

    /**
     * 단일 이유 Selection 편의 메서드.
     */
    private NatalPersonalityStatementSelection
    createSelection(

            NatalPersonalityStatementFact fact,

            PersonalityStatementSelectionRole role,

            boolean coveredByDirectSynthesis,

            PersonalityStatementSelectionReason reason

    ) {

        return new NatalPersonalityStatementSelection(

                fact,

                role,

                List.of(
                        reason
                ),

                coveredByDirectSynthesis,

                fact.completeChart()
        );
    }
}