package com.dajeom.analyzer;

import com.dajeom.constant.ConsultationGroundingPolicyRegistry;
import com.dajeom.model.ConsultationGroundingAnalysis;
import com.dajeom.model.ConsultationGroundingEvidence;
import com.dajeom.model.ConsultationGroundingPolicy;
import com.dajeom.model.ConsultationGroundingPolicyAnalysis;
import com.dajeom.model.ConsultationGroundingSection;
import com.dajeom.model.ConsultationGroundingSource;
import com.dajeom.model.ConsultationGroundingTimelineScenario;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 20차.
 *
 * ConsultationGroundingAnalysis에
 * ConsultationCategory별 Grounding Policy를 연결하고
 * deterministic 구조가 Policy를 위반하지 않는지 검증한다.
 *
 * =================================================
 *
 * 이 Analyzer는 자연어를 검사하지 않는다.
 *
 * 아직 LLM 문장이 생성되기 전이므로:
 *
 * - 의료 표현
 * - 법률 표현
 * - 사건 단정 표현
 *
 * 자체를 문자열 검사할 단계가 아니다.
 *
 * 대신 LLM 입력 전:
 *
 * - 허용 Domain
 * - Timeline 허용 여부
 * - AUXILIARY 독립 사용 여부
 *
 * 를 검증하고,
 *
 * 자연어 단계에서 지켜야 할 Constraint를
 * 구조화된 Policy로 전달한다.
 */
@Component
public class ConsultationGroundingPolicyAnalyzer {

    private final ConsultationGroundingPolicyRegistry
            policyRegistry;

    public ConsultationGroundingPolicyAnalyzer(
            ConsultationGroundingPolicyRegistry
                    policyRegistry
    ) {

        this.policyRegistry =
                Objects.requireNonNull(
                        policyRegistry,
                        "ConsultationGroundingPolicyRegistry가 필요합니다."
                );
    }

    public ConsultationGroundingPolicyAnalysis analyze(
            ConsultationGroundingAnalysis groundingAnalysis
    ) {

        Objects.requireNonNull(
                groundingAnalysis,
                "ConsultationGroundingPolicyAnalyzer에는 ConsultationGroundingAnalysis가 필요합니다."
        );

        List<ConsultationGroundingPolicy> policies =
                new ArrayList<>();

        for (
                ConsultationGroundingSection section
                : groundingAnalysis.sections()
        ) {

            ConsultationGroundingPolicy policy =
                    policyRegistry
                            .policyOf(
                                    section.category()
                            );

            validateSection(
                    section,
                    policy
            );

            policies.add(
                    policy
            );
        }

        return new ConsultationGroundingPolicyAnalysis(
                groundingAnalysis,
                List.copyOf(
                        policies
                )
        );
    }

    /*
     * =================================================
     * Section 검증
     * =================================================
     */

    private void validateSection(

            ConsultationGroundingSection section,

            ConsultationGroundingPolicy policy

    ) {

        if (
                section.category()
                        != policy.category()
        ) {

            throw new IllegalStateException(
                    "Grounding Section과 Policy의 ConsultationCategory가 일치하지 않습니다."
            );
        }

        /*
         * unavailable Section은
         * GroundingSection 생성 시점에서
         * Evidence가 비어 있음을 이미 보장한다.
         */

        if (
                section.unavailable()
        ) {

            return;
        }

        /*
         * =================================================
         * 원국 Evidence
         * =================================================
         */

        for (
                ConsultationGroundingEvidence evidence
                : section.natalEvidences()
        ) {

            validateEvidence(
                    evidence,
                    policy
            );
        }

        /*
         * =================================================
         * Timeline
         * =================================================
         */

        if (
                !policy.timelineAllowed()
                        && !section.timelineScenarios().isEmpty()
        ) {

            throw new IllegalStateException(
                    "Timeline이 허용되지 않은 ConsultationCategory에 Timeline Scenario가 포함되었습니다: "
                            + section.category()
            );
        }

        for (
                ConsultationGroundingTimelineScenario scenario
                : section.timelineScenarios()
        ) {

            for (
                    ConsultationGroundingEvidence evidence
                    : scenario.evidences()
            ) {

                validateEvidence(
                        evidence,
                        policy
                );
            }
        }
    }

    /*
     * =================================================
     * Evidence 검증
     * =================================================
     */

    private void validateEvidence(

            ConsultationGroundingEvidence evidence,

            ConsultationGroundingPolicy policy

    ) {

        if (
                !policy.allowsDomain(
                        evidence.domain()
                )
        ) {

            throw new IllegalStateException(
                    "ConsultationCategory에 허용되지 않은 LifeDomain Evidence가 포함되었습니다. "
                            + "category="
                            + policy.category()
                            + ", domain="
                            + evidence.domain()
                            + ", rule="
                            + evidence.ruleCode()
            );
        }

        /*
         * =================================================
         * AUXILIARY 독립 Evidence 금지
         * =================================================
         *
         * ConsultationGroundingEvidence 생성자에서도
         * 이미 검증하지만,
         * Policy 경계에서도 다시 확인한다.
         */

        boolean auxiliaryOnly =
                evidence
                        .sources()
                        .stream()
                        .allMatch(
                                ConsultationGroundingSource
                                        ::auxiliary
                        );

        if (
                auxiliaryOnly
        ) {

            throw new IllegalStateException(
                    "AUXILIARY Source만으로 Grounding Evidence를 구성할 수 없습니다. "
                            + "category="
                            + policy.category()
                            + ", rule="
                            + evidence.ruleCode()
            );
        }
    }
}