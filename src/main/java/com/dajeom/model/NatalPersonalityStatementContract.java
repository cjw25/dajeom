package com.dajeom.model;

import com.dajeom.type.PersonalityStatementBundleObligation;
import com.dajeom.type.PersonalityStatementContractGuardrail;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 기본성향 문장생성을 위한
 * 최종 Java Contract.
 *
 * =================================================
 *
 * 이 객체가 OpenAI 직전의
 * Grounding Contract가 된다.
 *
 * =================================================
 *
 * 구성:
 *
 * bundleContracts
 *
 * → 각 성향 묶음에서
 *   무엇을 반드시/보조적으로 설명할지
 *
 * globalBackgroundContexts
 *
 * → 직접 성격판정으로 사용하면 안 되는
 *   전역 Context
 *
 * guardrails
 *
 * → OpenAI가 절대로 넘으면 안 되는
 *   의미적 경계
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 이 객체가 있더라도
 * OpenAI가 사주를 재계산하면 안 된다.
 *
 * OpenAI는:
 *
 * Java가 이미 결정한 Fact와 관계를
 * 자연스러운 한국어로 표현하는 역할만 한다.
 */
public record NatalPersonalityStatementContract(

        NatalPersonalityStatementBundleAnalysis
        bundleAnalysis,

        List<NatalPersonalityStatementBundleContract>
        bundleContracts,

        List<NatalPersonalityStatementSelection>
        globalBackgroundContexts,

        List<PersonalityStatementContractGuardrail>
        guardrails,

        boolean completeChart

) {

    public NatalPersonalityStatementContract {

        Objects.requireNonNull(
                bundleAnalysis,
                "Personality Statement Bundle Analysis는 필수입니다."
        );

        bundleContracts =
                bundleContracts == null
                        ? List.of()
                        : List.copyOf(
                        bundleContracts
                );

        globalBackgroundContexts =
                globalBackgroundContexts == null
                        ? List.of()
                        : List.copyOf(
                        globalBackgroundContexts
                );

        guardrails =
                guardrails == null
                        ? List.of()
                        : List.copyOf(
                        guardrails
                );

        /*
         * =================================================
         * 원국 완전성
         * =================================================
         */

        if (
                bundleAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Bundle Analysis와 Statement Contract의 "
                            + "원국 완전성 상태가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 모든 Bundle은
         * 정확히 하나의 Contract를 가져야 한다.
         * =================================================
         */

        if (
                bundleContracts.size()
                        != bundleAnalysis
                        .bundles()
                        .size()
        ) {

            throw new IllegalArgumentException(
                    "모든 Personality Bundle은 "
                            + "정확히 하나의 Contract를 가져야 합니다."
            );
        }

        Set<NatalPersonalityStatementBundle> actualBundles =
                new HashSet<>();

        for (
                NatalPersonalityStatementBundleContract contract
                : bundleContracts
        ) {

            if (
                    contract.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Bundle Contract의 "
                                + "원국 완전성 상태가 일치하지 않습니다."
                );
            }

            if (
                    !actualBundles.add(
                            contract.sourceBundle()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 Personality Bundle에 "
                                + "Contract가 중복되었습니다."
                );
            }
        }

        Set<NatalPersonalityStatementBundle> expectedBundles =
                new HashSet<>(
                        bundleAnalysis.bundles()
                );

        if (
                !actualBundles.equals(
                        expectedBundles
                )
        ) {

            throw new IllegalArgumentException(
                    "Statement Contract가 "
                            + "Bundle Analysis의 전체 Bundle을 "
                            + "정확히 보존하지 않습니다."
            );
        }

        /*
         * =================================================
         * Global Background Context 정확성
         * =================================================
         */

        Set<NatalPersonalityStatementSelection> actualContexts =
                new HashSet<>(
                        globalBackgroundContexts
                );

        Set<NatalPersonalityStatementSelection> expectedContexts =
                new HashSet<>(
                        bundleAnalysis.globalBackgroundContexts()
                );

        if (
                !actualContexts.equals(
                        expectedContexts
                )
        ) {

            throw new IllegalArgumentException(
                    "Statement Contract의 "
                            + "Global Background Context가 "
                            + "Bundle Analysis와 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Guardrail 중복 금지
         * =================================================
         */

        Set<PersonalityStatementContractGuardrail> guardrailSet =
                EnumSet.noneOf(
                        PersonalityStatementContractGuardrail.class
                );

        for (
                PersonalityStatementContractGuardrail guardrail
                : guardrails
        ) {

            Objects.requireNonNull(
                    guardrail,
                    "Statement Contract Guardrail에는 null을 사용할 수 없습니다."
            );

            if (
                    !guardrailSet.add(
                            guardrail
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 Statement Guardrail이 중복되었습니다: "
                                + guardrail
                );
            }
        }

        /*
         * =================================================
         * 현재 Personality Contract에서는
         * 정의된 Guardrail 전체를
         * 반드시 적용한다.
         *
         * 일부 Guardrail을 빠뜨린 Contract가
         * 생성되지 않도록 한다.
         * =================================================
         */

        Set<PersonalityStatementContractGuardrail> requiredGuardrails =
                EnumSet.allOf(
                        PersonalityStatementContractGuardrail.class
                );

        if (
                !guardrailSet.equals(
                        requiredGuardrails
                )
        ) {

            throw new IllegalArgumentException(
                    "Personality Statement Contract에는 "
                            + "정의된 모든 Guardrail이 필요합니다."
            );
        }
    }

    /**
     * 반드시 설명해야 하는
     * 중심 Bundle Contract.
     */
    public List<NatalPersonalityStatementBundleContract>
    requiredBundleContracts() {

        return bundleContracts
                .stream()
                .filter(
                        NatalPersonalityStatementBundleContract
                                ::required
                )
                .toList();
    }

    /**
     * 보조적으로 사용할 수 있는
     * Bundle Contract.
     */
    public List<NatalPersonalityStatementBundleContract>
    optionalSupportBundleContracts() {

        return bundleContracts
                .stream()
                .filter(
                        contract ->
                                contract.obligation()
                                        == PersonalityStatementBundleObligation
                                        .OPTIONAL_SUPPORT
                )
                .toList();
    }

    /**
     * 조건/상충/중재를
     * 반드시 함께 설명해야 하는 Contract.
     */
    public List<NatalPersonalityStatementBundleContract>
    qualifiedBundleContracts() {

        return bundleContracts
                .stream()
                .filter(
                        NatalPersonalityStatementBundleContract
                                ::qualificationRequired
                )
                .toList();
    }

    /**
     * 강한 표현이 허용된 Selection을
     * 포함한 Contract.
     *
     * Contract 전체가 강하다는 뜻은 아니다.
     */
    public List<NatalPersonalityStatementBundleContract>
    contractsWithStrongSelections() {

        return bundleContracts
                .stream()
                .filter(
                        NatalPersonalityStatementBundleContract
                                ::hasStrongSelection
                )
                .toList();
    }
}