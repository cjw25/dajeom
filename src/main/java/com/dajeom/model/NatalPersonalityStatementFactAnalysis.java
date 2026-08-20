package com.dajeom.model;

import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.PersonalityStatementFactType;

import java.util.List;
import java.util.Objects;

/**
 * Personality Statement Fact 전체 Aggregate.
 *
 * =================================================
 *
 * 여기에는:
 *
 * - 사용자에게 직접 말할 Fact
 * - 조건을 붙여 말할 Fact
 * - Synthesis에 흡수된 Fact
 * - 내부 Reference Fact
 *
 * 가 모두 보존된다.
 *
 * =================================================
 *
 * 즉:
 *
 * 출력에서 안 보인다고
 * Evidence가 삭제된 것은 아니다.
 */
public record NatalPersonalityStatementFactAnalysis(

        NatalPersonalityFindingSynthesisAnalysis
        synthesisAnalysis,

        List<NatalPersonalityStatementFact>
        facts,

        boolean completeChart

) {

    public NatalPersonalityStatementFactAnalysis {

        Objects.requireNonNull(
                synthesisAnalysis,
                "Personality Finding Synthesis Analysis는 필수입니다."
        );

        facts =
                facts == null
                        ? List.of()
                        : List.copyOf(
                        facts
                );

        if (
                synthesisAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Synthesis Analysis와 "
                            + "Statement Fact Analysis의 "
                            + "원국 완전성 상태가 일치하지 않습니다."
            );
        }

        for (
                NatalPersonalityStatementFact fact
                : facts
        ) {

            if (
                    fact.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Statement Fact의 원국 완전성 상태가 "
                                + "Analysis와 일치하지 않습니다."
                );
            }
        }
    }

    /**
     * 실제 사용자 설명 후보.
     *
     * Synthesis에 흡수된
     * 개별 Cross Pattern은 제외된다.
     */
    public List<NatalPersonalityStatementFact>
    userFacingFacts() {

        return facts
                .stream()
                .filter(
                        NatalPersonalityStatementFact
                                ::userFacingCandidate
                )
                .toList();
    }

    /**
     * 가장 강한 사용자 설명 후보.
     */
    public List<NatalPersonalityStatementFact>
    strongUserFacingFacts() {

        return facts
                .stream()
                .filter(
                        NatalPersonalityStatementFact
                                ::strongUserFacingCandidate
                )
                .toList();
    }

    /**
     * 사용자에게 직접 말할 수 있지만
     * 조건/상충/중재 설명이
     * 반드시 같이 필요한 Fact.
     */
    public List<NatalPersonalityStatementFact>
    qualifiedUserFacingFacts() {

        return userFacingFacts()
                .stream()
                .filter(
                        NatalPersonalityStatementFact
                                ::qualificationRequired
                )
                .toList();
    }

    /**
     * 상위 Synthesis에 흡수되어
     * 사용자에게 중복 출력하지 않는 Fact.
     *
     * Evidence는 그대로 남아 있다.
     */
    public List<NatalPersonalityStatementFact>
    absorbedFacts() {

        return facts
                .stream()
                .filter(
                        NatalPersonalityStatementFact
                                ::absorbedByDirectSynthesis
                )
                .toList();
    }

    /**
     * 사용자 출력에는 직접 사용하지 않는
     * 내부 Fact.
     */
    public List<NatalPersonalityStatementFact>
    internalFacts() {

        return facts
                .stream()
                .filter(
                        NatalPersonalityStatementFact
                                ::internalOnly
                )
                .toList();
    }

    /**
     * 특정 종류의 Fact.
     */
    public List<NatalPersonalityStatementFact>
    factsOfType(

            PersonalityStatementFactType factType

    ) {

        if (
                factType == null
        ) {

            return List.of();
        }

        return facts
                .stream()
                .filter(
                        fact ->
                                fact.factType()
                                        == factType
                )
                .toList();
    }

    /**
     * 특정 기능축과 관련된 Fact.
     */
    public List<NatalPersonalityStatementFact>
    factsInvolving(

            NatalTenGodFunctionType functionType

    ) {

        if (
                functionType == null
        ) {

            return List.of();
        }

        return facts
                .stream()
                .filter(
                        fact ->
                                fact
                                        .involvedFunctions()
                                        .contains(
                                                functionType
                                        )
                )
                .toList();
    }
}