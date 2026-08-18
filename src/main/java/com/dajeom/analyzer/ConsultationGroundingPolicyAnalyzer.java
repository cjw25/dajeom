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
 * 정밀화 20차 / 21차 확장.
 *
 * Grounding Policy 검증.
 *
 * =================================================
 *
 * 검증:
 *
 * - 허용 Domain
 * - Timeline 허용 여부
 * - PAST/FUTURE Range 구조
 * - AUXILIARY 독립 Evidence 금지
 * - LEGAL_RISK Cross-Domain 구조
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
     * Section
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

        if (
                section.unavailable()
        ) {

            return;
        }

        validateTimelineRangeShape(
                section
        );

        /*
         * LEGAL_RISK는 일반 Evidence 검증 전에
         * 전용 교차 구조도 확인한다.
         */
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

        /*
         * 현재 구현에서는 Legal View가 available이라면
         * 실제 교차 Evidence가 반드시 존재한다.
         */
        if (
                !section.hasAnyEvidence()
        ) {

            throw new IllegalStateException(
                    "사용 가능한 LEGAL_RISK Section에는 전용 Cross Evidence가 필요합니다."
            );
        }

        /*
         * 모든 Legal Evidence는
         * PRESSURE 또는 MIXED 상태여야 한다.
         */
        for (
                ConsultationGroundingEvidence evidence
                : section.natalEvidences()
        ) {

            validateLegalStatus(
                    evidence
            );
        }

        /*
         * 원국 자체로 Cross가 성립할 수도 있다.
         */
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