package com.dajeom.model;

import com.dajeom.type.NatalResponseSituationType;
import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.PersonalityJudgmentGroundingLevel;
import com.dajeom.type.PersonalityJudgmentKind;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 기본성향 Deterministic Judgment 전체 Aggregate.
 *
 * =================================================
 *
 * 흐름:
 *
 * NatalFunctionalAnalysis
 *
 * ↓
 *
 * NatalCrossFunctionalAnalysis
 *
 * ↓
 *
 * NatalConditionalResponseAnalysis
 *
 * ↓
 *
 * NatalPersonalityPatternAnalysis
 *
 * ↓
 *
 * NatalPersonalityDeterministicAnalysis
 *
 * =================================================
 *
 * 이 단계부터 후속 상담계층은:
 *
 * LifeDomain을 다시 재해석하지 않고
 * 이 결과를 직접 사용할 수 있다.
 */
public record NatalPersonalityDeterministicAnalysis(

        NatalPersonalityPatternAnalysis patternAnalysis,

        List<NatalPersonalityDeterministicJudgment> judgments,

        boolean completeChart

) {

    public NatalPersonalityDeterministicAnalysis {

        Objects.requireNonNull(
                patternAnalysis,
                "Personality Pattern Analysis는 필수입니다."
        );

        judgments =
                judgments == null
                        ? List.of()
                        : List.copyOf(
                        judgments
                );

        if (
                patternAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Personality Pattern Analysis와 "
                            + "Deterministic Analysis의 "
                            + "원국 완전성 상태가 일치하지 않습니다."
            );
        }

        for (
                NatalPersonalityDeterministicJudgment judgment
                : judgments
        ) {

            if (
                    judgment.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Personality Judgment의 "
                                + "원국 완전성 상태가 "
                                + "일치하지 않습니다."
                );
            }
        }
    }

    public Optional<NatalPersonalityDeterministicJudgment>
    coreSelfJudgment() {

        return judgments
                .stream()
                .filter(
                        judgment ->
                                judgment.kind()
                                        == PersonalityJudgmentKind
                                        .CORE_SELF
                )
                .findFirst();
    }

    public Optional<NatalPersonalityDeterministicJudgment>
    functionJudgment(

            NatalTenGodFunctionType functionType

    ) {

        if (
                functionType == null
        ) {

            return Optional.empty();
        }

        return judgments
                .stream()
                .filter(
                        judgment ->
                                judgment.kind()
                                        == PersonalityJudgmentKind
                                        .FUNCTION
                )
                .filter(
                        judgment ->
                                judgment.functionType()
                                        .map(
                                                type ->
                                                        type
                                                                == functionType
                                        )
                                        .orElse(
                                                false
                                        )
                )
                .findFirst();
    }

    public Optional<NatalPersonalityDeterministicJudgment>
    situationJudgment(

            NatalResponseSituationType situationType

    ) {

        if (
                situationType == null
        ) {

            return Optional.empty();
        }

        return judgments
                .stream()
                .filter(
                        judgment ->
                                judgment.kind()
                                        == PersonalityJudgmentKind
                                        .SITUATION
                )
                .filter(
                        judgment ->
                                judgment.situationType()
                                        .map(
                                                type ->
                                                        type
                                                                == situationType
                                        )
                                        .orElse(
                                                false
                                        )
                )
                .findFirst();
    }

    /**
     * 실제 사용자에게
     * 비교적 직접적으로 표현할 수 있는 판정.
     */
    public List<NatalPersonalityDeterministicJudgment>
    directlyAssertableJudgments() {

        return judgments
                .stream()
                .filter(
                        NatalPersonalityDeterministicJudgment
                                ::directAssertionAllowed
                )
                .toList();
    }

    /**
     * 가장 강하게 근거가 확보된 판정.
     */
    public List<NatalPersonalityDeterministicJudgment>
    stronglyGroundedJudgments() {

        return judgments
                .stream()
                .filter(
                        judgment ->
                                judgment.groundingLevel()
                                        == PersonalityJudgmentGroundingLevel
                                        .STRONGLY_GROUNDED
                )
                .toList();
    }

    /**
     * 사용자에게 말할 때
     * 반드시 조건/반대축을 같이 설명해야 하는 판정.
     */
    public List<NatalPersonalityDeterministicJudgment>
    qualifiedJudgments() {

        return judgments
                .stream()
                .filter(
                        NatalPersonalityDeterministicJudgment
                                ::qualificationRequired
                )
                .toList();
    }
}