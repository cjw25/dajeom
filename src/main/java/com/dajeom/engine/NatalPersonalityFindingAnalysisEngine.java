package com.dajeom.engine;

import com.dajeom.model.NatalConditionalResponsePattern;
import com.dajeom.model.NatalCrossFunctionalPattern;
import com.dajeom.model.NatalPersonalityDeterministicAnalysis;
import com.dajeom.model.NatalPersonalityDeterministicJudgment;
import com.dajeom.model.NatalPersonalityFinding;
import com.dajeom.model.NatalPersonalityFindingAnalysis;
import com.dajeom.model.NatalPersonalityJudgmentEvidence;
import com.dajeom.type.NatalCrossFunctionalPatternType;
import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.PersonalityFindingOriginType;
import com.dajeom.type.PersonalityFindingTheme;
import com.dajeom.type.PersonalityJudgmentEvidenceType;
import com.dajeom.type.PersonalityJudgmentGroundingLevel;
import com.dajeom.type.PersonalityJudgmentKind;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Personality Deterministic Judgment와
 * 기존 Cross Pattern을
 *
 * 최종 구조화 Finding으로 변환한다.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 1.
 * REFERENCE Pattern도 버리지 않는다.
 *
 * 2.
 * REFERENCE Pattern은
 * user-facing direct assertion으로
 * 승격하지 않는다.
 *
 * 3.
 * MEANINGFUL / STRONG Pattern은
 * ConditionalResponse 상태를 연결한다.
 *
 * 4.
 * 새로운 점수 없음.
 *
 * 5.
 * Pattern 개수 다수결 없음.
 *
 * 6.
 * 문자열 parsing 없음.
 *
 * 7.
 * 기존:
 *
 * STRUCTURAL EFFECT
 * CROSS VALIDATION
 * CONDITIONAL RESPONSE
 *
 * 결과만 사용한다.
 */
@Component
public class NatalPersonalityFindingAnalysisEngine {

    public NatalPersonalityFindingAnalysis analyze(

            NatalPersonalityDeterministicAnalysis
                    deterministicAnalysis

    ) {

        Objects.requireNonNull(
                deterministicAnalysis,
                "Personality Deterministic Analysis는 필수입니다."
        );

        List<NatalPersonalityFinding> findings =
                new ArrayList<>();

        /*
         * =================================================
         * 1.
         * CoreSelf Finding
         * =================================================
         */

        deterministicAnalysis
                .coreSelfJudgment()
                .ifPresent(
                        judgment ->
                                findings.add(
                                        createCoreSelfFinding(
                                                judgment
                                        )
                                )
                );

        /*
         * =================================================
         * 2.
         * 십성 5대 기능 Finding
         *
         * REFERENCE / CONTEXTUAL도
         * 그대로 Finding으로 만든다.
         * =================================================
         */

        for (
                NatalTenGodFunctionType functionType
                : NatalTenGodFunctionType.values()
        ) {

            deterministicAnalysis
                    .functionJudgment(
                            functionType
                    )
                    .ifPresent(
                            judgment ->
                                    findings.add(
                                            createFunctionFinding(
                                                    functionType,
                                                    judgment
                                            )
                                    )
                    );
        }

        /*
         * =================================================
         * 3.
         * 모든 CrossFunctional Pattern
         *
         * 중요:
         *
         * effectivePatterns()가 아니라
         * patterns() 전체를 사용한다.
         *
         * 따라서 REFERENCE Pattern도
         * 최종 Finding에 보존된다.
         * =================================================
         */

        for (
                NatalCrossFunctionalPattern crossPattern
                : deterministicAnalysis
                .patternAnalysis()
                .crossFunctionalAnalysis()
                .patterns()
        ) {

            findings.add(
                    createCrossPatternFinding(
                            deterministicAnalysis,
                            crossPattern
                    )
            );
        }

        /*
         * =================================================
         * 4.
         * 대인관계 Context
         * =================================================
         */

        deterministicAnalysis
                .judgments()
                .stream()
                .filter(
                        judgment ->
                                judgment.kind()
                                        == PersonalityJudgmentKind
                                        .INTERPERSONAL_CONTEXT
                )
                .findFirst()
                .ifPresent(
                        judgment ->
                                findings.add(
                                        createInterpersonalFinding(
                                                judgment
                                        )
                                )
                );

        return new NatalPersonalityFindingAnalysis(

                deterministicAnalysis,

                findings,

                deterministicAnalysis.completeChart()
        );
    }

    /**
     * =================================================
     * CoreSelf
     * =================================================
     */
    private NatalPersonalityFinding
    createCoreSelfFinding(

            NatalPersonalityDeterministicJudgment judgment

    ) {

        return new NatalPersonalityFinding(

                PersonalityFindingTheme
                        .CORE_OPERATING_CAPACITY,

                PersonalityFindingOriginType
                        .CORE_SELF,

                List.of(),

                Optional.empty(),

                Optional.empty(),

                judgment.groundingLevel(),

                judgment.evidences(),

                judgment.directAssertionAllowed(),

                judgment.qualificationRequired(),

                judgment.completeChart()
        );
    }

    /**
     * =================================================
     * 십성 기능축
     * =================================================
     */
    private NatalPersonalityFinding
    createFunctionFinding(

            NatalTenGodFunctionType functionType,

            NatalPersonalityDeterministicJudgment judgment

    ) {

        return new NatalPersonalityFinding(

                PersonalityFindingTheme
                        .fromFunction(
                                functionType
                        ),

                PersonalityFindingOriginType
                        .FUNCTION,

                List.of(
                        functionType
                ),

                Optional.empty(),

                Optional.empty(),

                judgment.groundingLevel(),

                judgment.evidences(),

                judgment.directAssertionAllowed(),

                judgment.qualificationRequired(),

                judgment.completeChart()
        );
    }

    /**
     * =================================================
     * Cross Pattern
     * =================================================
     *
     * REFERENCE Pattern도 여기 들어온다.
     */
    private NatalPersonalityFinding
    createCrossPatternFinding(

            NatalPersonalityDeterministicAnalysis
                    deterministicAnalysis,

            NatalCrossFunctionalPattern crossPattern

    ) {

        /*
         * =================================================
         * 같은 Cross Pattern에서 생성된
         * ConditionalResponse가 있는지 찾는다.
         *
         * REFERENCE Pattern이면
         * 존재하지 않을 수 있다.
         * =================================================
         */

        Optional<NatalConditionalResponsePattern>
                conditionalPattern =
                deterministicAnalysis
                        .patternAnalysis()
                        .conditionalResponseAnalysis()
                        .patterns()
                        .stream()
                        .filter(
                                pattern ->
                                        pattern
                                                .sourcePattern()
                                                .patternType()

                                                == crossPattern
                                                .patternType()
                        )
                        .findFirst();

        /*
         * =================================================
         * Finding Grounding 결정
         *
         * 새 점수 없이
         * 기존 상태만 사용한다.
         * =================================================
         */

        PersonalityJudgmentGroundingLevel groundingLevel =
                determineCrossPatternGrounding(
                        crossPattern,
                        conditionalPattern,
                        deterministicAnalysis.completeChart()
                );

        /*
         * =================================================
         * Evidence
         * =================================================
         */

        List<NatalPersonalityJudgmentEvidence> evidences =
                new ArrayList<>();

        /*
         * CrossFunctional 원본 Evidence.
         */
        evidences.add(
                new NatalPersonalityJudgmentEvidence(

                        PersonalityJudgmentEvidenceType
                                .CROSS_FUNCTIONAL_PATTERN,

                        crossPattern
                                .patternType()
                                .name(),

                        crossPattern
                                .strongSourcePattern()

                                && crossPattern
                                .supported(),

                        crossPattern
                                .structurallyEffective()

                                && crossPattern
                                .supported(),

                        crossPattern
                                .structurallyEffective()

                                && (
                                crossPattern.qualified()

                                        || crossPattern.weakened()

                                        || crossPattern.conflicted()
                        )
                )
        );

        /*
         * ConditionalResponse가 존재하면
         * 별도 Evidence Family로 연결.
         */
        conditionalPattern
                .ifPresent(
                        pattern ->
                                evidences.add(
                                        new NatalPersonalityJudgmentEvidence(

                                                PersonalityJudgmentEvidenceType
                                                        .CONDITIONAL_RESPONSE,

                                                pattern
                                                        .patternType()
                                                        .name(),

                                                pattern
                                                        .stronglyGrounded(),

                                                pattern
                                                        .directlyAssertable(),

                                                pattern
                                                        .requiresQualification()
                                        )
                                )
                );

        /*
         * =================================================
         * 참여 기능축
         * =================================================
         */

        List<NatalTenGodFunctionType> involvedFunctions =
                createInvolvedFunctions(
                        crossPattern
                                .patternType()
                );

        /*
         * =================================================
         * 상황
         * =================================================
         */

        Optional<com.dajeom.type.NatalResponseSituationType>
                situationType =
                conditionalPattern
                        .map(
                                NatalConditionalResponsePattern
                                        ::situationType
                        );

        boolean qualificationRequired =
                crossPattern
                        .structurallyEffective()

                        && (
                        crossPattern.qualified()

                                || crossPattern.weakened()

                                || crossPattern.conflicted()
                );

        if (
                conditionalPattern
                        .map(
                                NatalConditionalResponsePattern
                                        ::requiresQualification
                        )
                        .orElse(
                                false
                        )
        ) {

            qualificationRequired =
                    true;
        }

        return new NatalPersonalityFinding(

                PersonalityFindingTheme
                        .fromCrossPattern(
                                crossPattern.patternType()
                        ),

                PersonalityFindingOriginType
                        .CROSS_PATTERN,

                involvedFunctions,

                situationType,

                Optional.of(
                        crossPattern.patternType()
                ),

                groundingLevel,

                evidences,

                groundingLevel
                        .directAssertionAllowed(),

                qualificationRequired,

                deterministicAnalysis.completeChart()
        );
    }

    /**
     * Cross Pattern Grounding.
     *
     * =================================================
     *
     * REFERENCE
     *
     * 10-8에서 아직 실제 Pattern으로
     * 승격되지 않은 상태.
     *
     * =================================================
     *
     * CONTEXTUAL
     *
     * 실제 Pattern이지만
     * 10-9에서 조건/약화/충돌이 존재.
     *
     * =================================================
     *
     * GROUNDED
     *
     * 10-9 SUPPORTED.
     *
     * =================================================
     *
     * STRONGLY_GROUNDED
     *
     * 10-8 STRONG
     * +
     * 10-9 SUPPORTED.
     */
    private PersonalityJudgmentGroundingLevel
    determineCrossPatternGrounding(

            NatalCrossFunctionalPattern crossPattern,

            Optional<NatalConditionalResponsePattern>
                    conditionalPattern,

            boolean completeChart

    ) {

        /*
         * 10-8 REFERENCE.
         *
         * 삭제하지 않고 그대로 보존한다.
         */
        if (
                !crossPattern
                        .structurallyEffective()
        ) {

            return PersonalityJudgmentGroundingLevel
                    .REFERENCE;
        }

        /*
         * MEANINGFUL/STRONG이지만
         * ConditionalResponse가 없다는 것은
         * 현재 분석 chain의 이상 상태다.
         *
         * 강제로 추정하지 않고
         * CONTEXTUAL로 제한한다.
         */
        if (
                conditionalPattern.isEmpty()
        ) {

            return PersonalityJudgmentGroundingLevel
                    .CONTEXTUAL;
        }

        NatalConditionalResponsePattern conditional =
                conditionalPattern.get();

        if (
                conditional
                        .stronglyGrounded()
        ) {

            return completeChart

                    ? PersonalityJudgmentGroundingLevel
                    .STRONGLY_GROUNDED

                    : PersonalityJudgmentGroundingLevel
                    .GROUNDED;
        }

        if (
                conditional
                        .directlyAssertable()
        ) {

            return PersonalityJudgmentGroundingLevel
                    .GROUNDED;
        }

        return PersonalityJudgmentGroundingLevel
                .CONTEXTUAL;
    }

    /**
     * Cross Pattern에 참여하는
     * 기능축 목록.
     */
    private List<NatalTenGodFunctionType>
    createInvolvedFunctions(

            NatalCrossFunctionalPatternType patternType

    ) {

        List<NatalTenGodFunctionType> result =
                new ArrayList<>();

        result.add(
                patternType.primaryFunction()
        );

        patternType
                .secondaryFunction()
                .ifPresent(
                        secondary -> {

                            if (
                                    !result.contains(
                                            secondary
                                    )
                            ) {

                                result.add(
                                        secondary
                                );
                            }
                        }
                );

        return List.copyOf(
                result
        );
    }

    /**
     * =================================================
     * 대인관계 Context
     * =================================================
     */
    private NatalPersonalityFinding
    createInterpersonalFinding(

            NatalPersonalityDeterministicJudgment judgment

    ) {

        return new NatalPersonalityFinding(

                PersonalityFindingTheme
                        .INTERPERSONAL_RELATION_CONTEXT,

                PersonalityFindingOriginType
                        .INTERPERSONAL_CONTEXT,

                List.of(),

                Optional.empty(),

                Optional.empty(),

                judgment.groundingLevel(),

                judgment.evidences(),

                judgment.directAssertionAllowed(),

                judgment.qualificationRequired(),

                judgment.completeChart()
        );
    }
}