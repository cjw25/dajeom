package com.dajeom.analyzer;

import com.dajeom.constant.ConsultationGroundingPolicyRegistry;
import com.dajeom.model.ConsultationGroundingAnalysis;
import com.dajeom.model.ConsultationGroundingEvidence;
import com.dajeom.model.ConsultationGroundingPolicy;
import com.dajeom.model.ConsultationGroundingPolicyAnalysis;
import com.dajeom.model.ConsultationGroundingSection;
import com.dajeom.model.ConsultationGroundingSource;
import com.dajeom.model.ConsultationGroundingTimelineScenario;
import com.dajeom.model.ConsultationGroundingTimelineYear;
import com.dajeom.model.NatalPersonalityStatementContract;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.LifeDomain;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 20차 / 21차
 * +
 * Personality Direct-Natal Contract 확장.
 *
 * Grounding Policy 검증.
 *
 * =================================================
 *
 * 기존 검증:
 *
 * - 허용 Domain
 * - Timeline 허용 여부
 * - PAST/FUTURE Range 구조
 * - AUXILIARY 독립 Evidence 금지
 * - LEGAL_RISK Cross-Domain 구조
 *
 * =================================================
 *
 * PERSONALITY 추가 검증:
 *
 * - Contract는 PERSONALITY에만 존재
 * - 공식 PERSONALITY 실행선에서는 Contract 필수
 * - completeChart 정합성
 * - available / output contract 정합성
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * NatalPersonalityStatementContract는
 * LifeDomain Evidence가 아니다.
 *
 * 따라서:
 *
 * - allowsDomain()
 * - AUXILIARY 검증
 * - prominence
 *
 * 대상으로 변환하지 않는다.
 */
@Component
public class ConsultationGroundingPolicyAnalyzer {

    private static final Set<String>
            LEGAL_CAUTION_STATUS_NAMES =
            Set.of(
                    "PRESSURE",
                    "MIXED"
            );

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
                    policy,
                    groundingAnalysis.completeChart()
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
     * Section
     * =================================================
     */

    private void validateSection(

            ConsultationGroundingSection section,

            ConsultationGroundingPolicy policy,

            boolean completeChart

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
         * =================================================
         * Personality Contract는
         * unavailable 여부보다 먼저 검증한다.
         *
         * unavailable이어도 내부 Reference Contract는
         * 보존될 수 있기 때문이다.
         * =================================================
         */

        validatePersonalityContract(
                section,
                completeChart
        );

        if (
                section.unavailable()
        ) {

            return;
        }

        validateTimelineRangeShape(
                section
        );

        if (
                section.category()
                        == ConsultationCategory.LEGAL_RISK
        ) {

            validateLegalRiskSection(
                    section
            );
        }

        for (
                ConsultationGroundingEvidence evidence
                : section.natalEvidences()
        ) {

            validateEvidence(
                    evidence,
                    policy
            );
        }

        if (
                !policy.timelineAllowed()
                        && section.hasAnyTimeline()
        ) {

            throw new IllegalStateException(
                    "Timeline이 허용되지 않은 ConsultationCategory에 Timeline Grounding이 포함되었습니다: "
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

        for (
                ConsultationGroundingTimelineYear timelineYear
                : section.timelineYears()
        ) {

            for (
                    ConsultationGroundingEvidence evidence
                    : timelineYear.evidences()
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
     * PERSONALITY Direct-Natal Contract
     * =================================================
     */

    private void validatePersonalityContract(

            ConsultationGroundingSection section,

            boolean completeChart

    ) {

        /*
         * =================================================
         * 다른 Category에 Contract 금지.
         *
         * ConsultationGroundingSection에서도 검증하지만
         * Policy 계층에서도 명시적으로 방어한다.
         * =================================================
         */

        if (
                section.category()
                        != ConsultationCategory.PERSONALITY
        ) {

            if (
                    section.hasPersonalityStatementContract()
            ) {

                throw new IllegalStateException(
                        "PERSONALITY 이외의 Grounding Section에 "
                                + "Personality Statement Contract가 존재합니다: "
                                + section.category()
                );
            }

            return;
        }

        /*
         * =================================================
         * 공식 PERSONALITY 실행선에서는
         * Contract가 반드시 존재해야 한다.
         * =================================================
         */

        if (
                !section.hasPersonalityStatementContract()
        ) {

            throw new IllegalStateException(
                    "PERSONALITY Grounding Section에는 "
                            + "NatalPersonalityStatementContract가 필요합니다."
            );
        }

        NatalPersonalityStatementContract contract =
                section
                        .requirePersonalityStatementContract();

        /*
         * =================================================
         * 원국 완전성 Anchor 정합성.
         * =================================================
         */

        if (
                contract.completeChart()
                        != completeChart
        ) {

            throw new IllegalStateException(
                    "PERSONALITY Statement Contract와 "
                            + "ConsultationGroundingAnalysis의 "
                            + "completeChart 상태가 일치하지 않습니다."
            );
        }

        boolean hasOutput =
                contract
                        .bundleContracts()
                        .stream()
                        .anyMatch(
                                bundleContract ->
                                        !bundleContract
                                                .mandatorySelections()
                                                .isEmpty()

                                                || !bundleContract
                                                .supportingSelections()
                                                .isEmpty()
                        );

        /*
         * =================================================
         * available인데 Contract에
         * 실제 설명할 것이 없는 상태 금지.
         *
         * legacy LifeDomain Evidence만으로
         * PERSONALITY를 살리지 않는다.
         * =================================================
         */

        if (
                section.available()
                        && !hasOutput
        ) {

            throw new IllegalStateException(
                    "사용 가능한 PERSONALITY Section에는 "
                            + "직접 출력 가능한 Personality Contract가 필요합니다."
            );
        }

        /*
         * =================================================
         * unavailable인데 Contract에는
         * 실제 출력 내용이 있는 상태도 금지.
         * =================================================
         */

        if (
                section.unavailable()
                        && hasOutput
        ) {

            throw new IllegalStateException(
                    "출력 가능한 Personality Contract가 존재하는데 "
                            + "PERSONALITY Section이 unavailable입니다."
            );
        }

        /*
         * =================================================
         * 기본성향 자체에는
         * legacy Timeline을 섞지 않는다.
         *
         * 운에서 성향이 어떻게 달라지는지는
         * 향후 Personality Luck Functional Layer의
         * 별도 책임이다.
         * =================================================
         */

        if (
                section.hasAnyTimeline()
        ) {

            throw new IllegalStateException(
                    "PERSONALITY Direct-Natal Grounding에는 "
                            + "legacy Timeline Evidence를 함께 넣지 않습니다."
            );
        }
    }

    /*
     * =================================================
     * Timeline Range Shape
     * =================================================
     */

    private void validateTimelineRangeShape(
            ConsultationGroundingSection section
    ) {

        boolean rangeCategory =
                section.category()
                        == ConsultationCategory.PAST_LUCK

                        || section.category()
                        == ConsultationCategory.FUTURE_LUCK;

        if (
                !rangeCategory
                        && section.hasTimelineYears()
        ) {

            throw new IllegalStateException(
                    "PAST_LUCK/FUTURE_LUCK이 아닌 Category에 Timeline Year Range가 포함되었습니다: "
                            + section.category()
            );
        }

        if (
                !rangeCategory
        ) {

            return;
        }

        if (
                !section.hasTimelineYears()
        ) {

            throw new IllegalStateException(
                    "사용 가능한 Timeline Range Category에는 Timeline Year Grounding이 필요합니다: "
                            + section.category()
            );
        }

        if (
                section.hasTimelineScenarios()
        ) {

            throw new IllegalStateException(
                    "PAST_LUCK/FUTURE_LUCK에는 현재 Timeline Scenario를 함께 넣을 수 없습니다: "
                            + section.category()
            );
        }

        if (
                section.hasNatalEvidence()
        ) {

            throw new IllegalStateException(
                    "PAST_LUCK/FUTURE_LUCK에는 별도 Natal Evidence를 함께 넣지 않습니다: "
                            + section.category()
            );
        }
    }

    /*
     * =================================================
     * LEGAL_RISK
     * =================================================
     */

    private void validateLegalRiskSection(
            ConsultationGroundingSection section
    ) {

        if (
                section.hasTimelineYears()
        ) {

            throw new IllegalStateException(
                    "LEGAL_RISK에는 과거/미래 Timeline Year Range를 직접 넣지 않습니다."
            );
        }

        if (
                !section.hasAnyEvidence()
        ) {

            throw new IllegalStateException(
                    "사용 가능한 LEGAL_RISK Section에는 전용 Cross Evidence가 필요합니다."
            );
        }

        for (
                ConsultationGroundingEvidence evidence
                : section.natalEvidences()
        ) {

            validateLegalStatus(
                    evidence
            );
        }

        boolean natalCross =
                legalCrossEstablished(
                        section.natalEvidences()
                );

        boolean scenarioCross =
                false;

        for (
                ConsultationGroundingTimelineScenario scenario
                : section.timelineScenarios()
        ) {

            if (
                    scenario.evidences()
                            .isEmpty()
            ) {

                continue;
            }

            for (
                    ConsultationGroundingEvidence evidence
                    : scenario.evidences()
            ) {

                validateLegalStatus(
                        evidence
                );
            }

            List<ConsultationGroundingEvidence> effective =
                    new ArrayList<>(
                            section.natalEvidences()
                    );

            effective.addAll(
                    scenario.evidences()
            );

            if (
                    !legalCrossEstablished(
                            effective
                    )
            ) {

                throw new IllegalStateException(
                        "LEGAL_RISK Timeline Scenario의 Evidence가 복수 Domain/Source 교차 조건을 충족하지 않습니다. "
                                + "scenarioOrder="
                                + scenario.scenarioOrder()
                );
            }

            scenarioCross =
                    true;
        }

        if (
                !natalCross
                        && !scenarioCross
        ) {

            throw new IllegalStateException(
                    "LEGAL_RISK에 실제 Cross-Domain Evidence가 존재하지 않습니다."
            );
        }
    }

    private void validateLegalStatus(
            ConsultationGroundingEvidence evidence
    ) {

        if (
                !LEGAL_CAUTION_STATUS_NAMES
                        .contains(
                                evidence
                                        .status()
                                        .name()
                        )
        ) {

            throw new IllegalStateException(
                    "LEGAL_RISK에는 PRESSURE 또는 MIXED Evidence만 사용할 수 있습니다. "
                            + "domain="
                            + evidence.domain()
                            + ", status="
                            + evidence.status()
                            + ", rule="
                            + evidence.ruleCode()
            );
        }
    }

    private boolean legalCrossEstablished(
            List<ConsultationGroundingEvidence> evidences
    ) {

        if (
                evidences == null
                        || evidences.size() < 2
        ) {

            return false;
        }

        Set<LifeDomain> domains =
                EnumSet.noneOf(
                        LifeDomain.class
                );

        Set<String> sourceKeys =
                new LinkedHashSet<>();

        for (
                ConsultationGroundingEvidence evidence
                : evidences
        ) {

            if (
                    evidence == null
                            || !LEGAL_CAUTION_STATUS_NAMES
                            .contains(
                                    evidence
                                            .status()
                                            .name()
                            )
            ) {

                continue;
            }

            domains.add(
                    evidence.domain()
            );

            for (
                    ConsultationGroundingSource source
                    : evidence.sources()
            ) {

                if (
                        source.auxiliary()
                ) {

                    continue;
                }

                sourceKeys.add(
                        source.sourceKey()
                );
            }
        }

        return domains.size() >= 2
                && sourceKeys.size() >= 2;
    }

    /*
     * =================================================
     * 일반 Evidence 검증
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