package com.dajeom.analyzer;

import com.dajeom.constant.ConsultationGroundingPolicyRegistry;
import com.dajeom.model.ConsultationCategoryDeterministicDependencyReference;
import com.dajeom.model.ConsultationGroundingAnalysis;
import com.dajeom.model.ConsultationGroundingDeterministicConclusion;
import com.dajeom.model.ConsultationGroundingEvidence;
import com.dajeom.model.ConsultationGroundingPolicy;
import com.dajeom.model.ConsultationGroundingPolicyAnalysis;
import com.dajeom.model.ConsultationGroundingSection;
import com.dajeom.model.ConsultationGroundingSource;
import com.dajeom.model.ConsultationGroundingTimelineScenario;
import com.dajeom.model.ConsultationGroundingTimelineYear;
import com.dajeom.model.ConsultationGroundingTimelineYearDeterministicContract;
import com.dajeom.model.NatalPersonalityStatementContract;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationCategoryDeterministicLayer;
import com.dajeom.type.ConsultationCategoryDeterministicStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Consultation Grounding Policy Analyzer.
 *
 * =================================================
 * 역할
 * =================================================
 *
 * ConsultationGroundingAnalysis가
 * 각 ConsultationCategory의 공식 Policy 경계를
 * 위반하지 않는지 deterministic하게 검증한다.
 *
 * =================================================
 * 검증 범위
 * =================================================
 *
 * - Category / Policy 일치
 * - Personality Direct-Natal Contract
 * - PAST/FUTURE Timeline Range 구조
 * - LEGAL_RISK 공식 Deterministic Cross Contract
 * - Full Domain 허용
 * - Selected RuleCode 허용
 * - AUXILIARY-only Grounding 차단
 * - Category Deterministic Category / Layer / 좌표 검증
 *
 * =================================================
 * Full Domain / Selected Rule
 * =================================================
 *
 * Grounding Evidence 허용 여부는:
 *
 * policy.allowsRule(
 *     evidence.domain(),
 *     evidence.ruleCode()
 * )
 *
 * 로 검증한다.
 *
 * 따라서:
 *
 * 1.
 * allowedDomains에 포함된 Domain
 *
 * ->
 *
 * 해당 Domain의 모든 RuleCode 허용.
 *
 * 2.
 * allowedDomains에는 없지만
 * selectedRuleCodes에 등록된 Domain
 *
 * ->
 *
 * 정확하게 등록된 RuleCode만 허용.
 *
 * =================================================
 * CAREER
 * =================================================
 *
 * CAREER Domain은 전체 허용.
 *
 * LEARNING_GROWTH는
 * CAREER에 필요한 정확한 선별 RuleCode만 허용.
 *
 * LEARNING_GROWTH 전체를
 * CAREER Grounding으로 확장하지 않는다.
 *
 * =================================================
 * CURRENT_YEAR_LUCK
 * =================================================
 *
 * CURRENT_YEAR_LUCK은
 * LifeDomain 전체 허용 방식이 아니다.
 *
 * 현재연도 분석에 실제 사용되는:
 *
 * - 대운 Context
 * - 세운 Context
 * - 세운 Functional
 * - 월운 Context
 * - 월운 Functional
 *
 * 의 정확한 RuleCode만 허용한다.
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * 이 Analyzer는:
 *
 * - 새로운 명리 계산
 * - Category Status 재판정
 * - Evidence 합성
 * - Conclusion 합성
 * - Domain 간 관계 생성
 * - 시간층 간 관계 생성
 *
 * 을 하지 않는다.
 *
 * 이미 계산된 결과가
 * 공식 Grounding 경계에 맞는지만 검증한다.
 *
 * =================================================
 * NATAL
 * =================================================
 *
 * Section.natalDeterministicConclusions에는
 * 반드시:
 *
 * layer = NATAL
 *
 * 인 Conclusion만 존재할 수 있다.
 *
 * NATAL은:
 *
 * sajuYear = null
 * monthIndex = null
 *
 * 이어야 한다.
 *
 * =================================================
 * CURRENT_TIMELINE
 * =================================================
 *
 * TimelineScenario.deterministicConclusions에는
 * 반드시:
 *
 * layer = CURRENT_TIMELINE
 *
 * 인 Conclusion만 존재할 수 있다.
 *
 * structuralReference의:
 *
 * sajuYear
 * monthIndex
 *
 * 가 실제 Scenario 좌표와
 * 정확히 일치해야 한다.
 *
 * =================================================
 * TIMELINE_YEAR
 * =================================================
 *
 * PAST_LUCK / FUTURE_LUCK의
 * TimelineYear Deterministic Contract에 들어가는
 * 모든 Conclusion은:
 *
 * layer = TIMELINE_YEAR
 *
 * 이어야 한다.
 *
 * sajuYear는 해당 TimelineYear와
 * 정확히 일치해야 한다.
 *
 * monthIndex는 null이어야 한다.
 *
 * =================================================
 * structuralReference
 * =================================================
 *
 * ConsultationGroundingDeterministicConclusion의
 * structuralReference는
 * 생성 시점부터 필수다.
 *
 * 따라서 Policy 단계에서는
 * 존재 여부를 추측하거나
 * Optional 상태를 처리하지 않는다.
 *
 * conclusion.structuralReference()
 *
 * 를 직접 사용해서:
 *
 * - Category
 * - Layer
 * - Timeline 좌표
 *
 * 를 검증한다.
 *
 * =================================================
 * RuleCode 문자열 추측 금지
 * =================================================
 *
 * 다음처럼 검증하지 않는다.
 *
 * - WEALTH_ prefix니까 WEALTH다
 * - MONTHLY라는 단어가 있으니 월운이다
 * - TIMELINE_YEAR 문자열이 있으니 연도 결과다
 *
 * Category Deterministic 결과는
 * structuralReference를 사용한다.
 *
 * raw Evidence 허용 여부는
 * Policy의 정확한 allowsRule()을 사용한다.
 *
 * =================================================
 * PERSONALITY
 * =================================================
 *
 * PERSONALITY는:
 *
 * NatalPersonalityStatementContract
 *
 * 를 공식 Direct-Natal Meaning Contract로 사용한다.
 *
 * 따라서 일반:
 *
 * natalDeterministicConclusions
 *
 * 또는:
 *
 * TimelineScenario.deterministicConclusions
 *
 * 를 PERSONALITY에 넣지 않는다.
 *
 * =================================================
 * LEGAL_RISK
 * =================================================
 *
 * LEGAL_RISK Cross 성립 여부는
 * 이 Policy Analyzer에서 다시 계산하지 않는다.
 *
 * 공식 계산 순서:
 *
 * ConsultationLegalRiskAnalyzer
 *
 * ->
 *
 * LegalRiskCrossEstablishedDeterministicRule
 *
 * ->
 *
 * LEGAL_RISK_CROSS_ESTABLISHED
 *
 * ->
 *
 * ConsultationGroundingDeterministicConclusion
 *
 * =================================================
 * LEGAL_RISK Policy 역할
 * =================================================
 *
 * Policy 단계에서는:
 *
 * - Cross Domain 개수 재계산 금지
 * - provenance 개수 재계산 금지
 * - Annual/Monthly provenance family 재계산 금지
 * - raw Evidence를 이용한 Cross 재판정 금지
 *
 * 대신:
 *
 * LEGAL_RISK_CROSS_ESTABLISHED
 *
 * 라는 공식 Java Deterministic Conclusion이
 * 올바른 Layer와 좌표에 존재하는지만 검증한다.
 *
 * =================================================
 * LEGAL_RISK NATAL
 * =================================================
 *
 * 원국 자체에서 Legal Cross가 성립한 경우:
 *
 * Section.natalDeterministicConclusions
 *
 * 에 정확한:
 *
 * LEGAL_RISK_CROSS_ESTABLISHED
 *
 * 가 존재한다.
 *
 * 원국 Evidence가 존재한다고 해서
 * 반드시 NATAL Cross가 성립하는 것은 아니다.
 *
 * 원국 +
 * 현재 Segment
 *
 * 조합에서만 Cross가 성립할 수도 있으므로
 * NATAL Conclusion 부재 자체는 오류가 아니다.
 *
 * =================================================
 * LEGAL_RISK CURRENT_TIMELINE
 * =================================================
 *
 * LEGAL_RISK Timeline Scenario에서
 * 실제 scenario.evidences가 존재하면
 * 해당 Scenario에는:
 *
 * LEGAL_RISK_CROSS_ESTABLISHED
 *
 * CURRENT_TIMELINE Conclusion이
 * 반드시 정확히 하나 존재해야 한다.
 *
 * scenario.evidences가 비어 있으면
 * 해당 Scenario에는
 * LEGAL_RISK_CROSS_ESTABLISHED Conclusion이
 * 존재해서는 안 된다.
 *
 * =================================================
 * LEGAL_RISK Status
 * =================================================
 *
 * raw LEGAL_RISK Evidence는:
 *
 * PRESSURE
 * MIXED
 *
 * 만 허용한다.
 *
 * 공식:
 *
 * LEGAL_RISK_CROSS_ESTABLISHED
 *
 * Deterministic Conclusion은
 * Rule 자체의 의미에 따라:
 *
 * PRESSURE
 *
 * 여야 한다.
 *
 * 이것은 raw Evidence 다수결이 아니다.
 *
 * =================================================
 * 관계형 풀이 금지
 * =================================================
 *
 * 여러 Evidence 또는 Conclusion이
 * 같이 존재한다는 이유만으로:
 *
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 결합되어 C다
 * - 먼저 A 이후 B
 * - 여러 결과가 같으므로 더 강하다
 *
 * 같은 의미를 생성하지 않는다.
 */
@Component
public class ConsultationGroundingPolicyAnalyzer {

    private static final String
            LEGAL_RISK_CROSS_ESTABLISHED_RULE_CODE =
            "LEGAL_RISK_CROSS_ESTABLISHED";

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


    /*
     * =================================================
     * Analyze
     * =================================================
     */

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

            ConsultationGroundingSection normalizedSection =
                    Objects.requireNonNull(
                            section,
                            "ConsultationGroundingSection은 null일 수 없습니다."
                    );

            ConsultationGroundingPolicy policy =
                    policyRegistry
                            .policyOf(
                                    normalizedSection.category()
                            );

            validateSection(
                    normalizedSection,
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

        validateCategoryDeterministicGrounding(
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
     * Personality
     * =================================================
     */

    private void validatePersonalityContract(

            ConsultationGroundingSection section,

            boolean completeChart

    ) {

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

        if (
                section.hasAnyCategoryDeterministicConclusion()
        ) {

            throw new IllegalStateException(
                    "PERSONALITY Grounding에는 일반 Category Deterministic Conclusion을 함께 넣지 않습니다."
            );
        }

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

        if (
                section.available()
                        && !hasOutput
        ) {

            throw new IllegalStateException(
                    "사용 가능한 PERSONALITY Section에는 "
                            + "직접 출력 가능한 Personality Contract가 필요합니다."
            );
        }

        if (
                section.unavailable()
                        && hasOutput
        ) {

            throw new IllegalStateException(
                    "출력 가능한 Personality Contract가 존재하는데 "
                            + "PERSONALITY Section이 unavailable입니다."
            );
        }

        if (
                section.hasAnyTimeline()
        ) {

            throw new IllegalStateException(
                    "PERSONALITY Direct-Natal Grounding에는 "
                            + "Timeline Grounding을 함께 넣지 않습니다."
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

        if (
                section.hasNatalDeterministicConclusions()
        ) {

            throw new IllegalStateException(
                    "PAST_LUCK/FUTURE_LUCK에는 별도 NATAL Category Deterministic Conclusion을 함께 넣지 않습니다: "
                            + section.category()
            );
        }
    }


    /*
     * =================================================
     * Category Deterministic Grounding
     * =================================================
     */

    private void validateCategoryDeterministicGrounding(

            ConsultationGroundingSection section

    ) {

        Objects.requireNonNull(
                section,
                "Category Deterministic Grounding 검증에는 Section이 필요합니다."
        );

        for (
                ConsultationGroundingDeterministicConclusion conclusion
                : section.natalDeterministicConclusions()
        ) {

            validateNatalDeterministicConclusion(
                    section,
                    conclusion
            );
        }

        for (
                ConsultationGroundingTimelineScenario scenario
                : section.timelineScenarios()
        ) {

            ConsultationGroundingTimelineScenario normalizedScenario =
                    Objects.requireNonNull(
                            scenario,
                            "Grounding Timeline Scenario는 null일 수 없습니다."
                    );

            for (
                    ConsultationGroundingDeterministicConclusion conclusion
                    : normalizedScenario.deterministicConclusions()
            ) {

                validateCurrentTimelineDeterministicConclusion(
                        section,
                        normalizedScenario,
                        conclusion
                );
            }
        }

        for (
                ConsultationGroundingTimelineYear timelineYear
                : section.timelineYears()
        ) {

            ConsultationGroundingTimelineYear normalizedYear =
                    Objects.requireNonNull(
                            timelineYear,
                            "Grounding Timeline Year는 null일 수 없습니다."
                    );

            ConsultationGroundingTimelineYearDeterministicContract contract =
                    Objects.requireNonNull(
                            normalizedYear.deterministicContract(),
                            "Grounding Timeline Year에는 Deterministic Contract가 필요합니다. sajuYear="
                                    + normalizedYear.sajuYear()
                    );

            for (
                    ConsultationGroundingDeterministicConclusion conclusion
                    : contract.allConclusions()
            ) {

                validateTimelineYearDeterministicConclusion(
                        section,
                        normalizedYear,
                        conclusion
                );
            }
        }
    }


    /*
     * =================================================
     * NATAL Deterministic
     * =================================================
     */

    private void validateNatalDeterministicConclusion(

            ConsultationGroundingSection section,

            ConsultationGroundingDeterministicConclusion conclusion

    ) {

        Objects.requireNonNull(
                conclusion,
                "NATAL Grounding Deterministic Conclusion은 null일 수 없습니다."
        );

        ConsultationCategoryDeterministicDependencyReference reference =
                conclusion.structuralReference();

        validateStructuralCategory(
                section,
                conclusion,
                reference
        );

        if (
                reference.layer()
                        != ConsultationCategoryDeterministicLayer.NATAL
        ) {

            throw new IllegalStateException(
                    "Section.natalDeterministicConclusions에는 NATAL Layer만 허용됩니다. category="
                            + section.category()
                            + ", rule="
                            + conclusion.ruleCode()
                            + ", actualLayer="
                            + reference.layer()
            );
        }

        if (
                reference.sajuYear() != null
                        || reference.monthIndex() != null
        ) {

            throw new IllegalStateException(
                    "NATAL Deterministic Conclusion에는 Timeline 좌표가 존재할 수 없습니다. category="
                            + section.category()
                            + ", rule="
                            + conclusion.ruleCode()
                            + ", sajuYear="
                            + reference.sajuYear()
                            + ", monthIndex="
                            + reference.monthIndex()
            );
        }
    }


    /*
     * =================================================
     * CURRENT_TIMELINE Deterministic
     * =================================================
     */

    private void validateCurrentTimelineDeterministicConclusion(

            ConsultationGroundingSection section,

            ConsultationGroundingTimelineScenario scenario,

            ConsultationGroundingDeterministicConclusion conclusion

    ) {

        Objects.requireNonNull(
                conclusion,
                "CURRENT_TIMELINE Grounding Deterministic Conclusion은 null일 수 없습니다."
        );

        ConsultationCategoryDeterministicDependencyReference reference =
                conclusion.structuralReference();

        validateStructuralCategory(
                section,
                conclusion,
                reference
        );

        if (
                reference.layer()
                        != ConsultationCategoryDeterministicLayer
                        .CURRENT_TIMELINE
        ) {

            throw new IllegalStateException(
                    "TimelineScenario.deterministicConclusions에는 CURRENT_TIMELINE Layer만 허용됩니다. category="
                            + section.category()
                            + ", rule="
                            + conclusion.ruleCode()
                            + ", scenarioOrder="
                            + scenario.scenarioOrder()
                            + ", actualLayer="
                            + reference.layer()
            );
        }

        if (
                reference.sajuYear() == null
                        || reference.monthIndex() == null
        ) {

            throw new IllegalStateException(
                    "CURRENT_TIMELINE Deterministic Conclusion에는 sajuYear와 monthIndex가 필요합니다. category="
                            + section.category()
                            + ", rule="
                            + conclusion.ruleCode()
                            + ", scenarioOrder="
                            + scenario.scenarioOrder()
            );
        }

        if (
                reference.sajuYear().intValue()
                        != scenario.sajuYear()
        ) {

            throw new IllegalStateException(
                    "CURRENT_TIMELINE Deterministic Conclusion의 sajuYear가 Scenario와 일치하지 않습니다. category="
                            + section.category()
                            + ", rule="
                            + conclusion.ruleCode()
                            + ", scenarioOrder="
                            + scenario.scenarioOrder()
                            + ", expectedYear="
                            + scenario.sajuYear()
                            + ", actualYear="
                            + reference.sajuYear()
            );
        }

        if (
                reference.monthIndex().intValue()
                        != scenario.monthIndex()
        ) {

            throw new IllegalStateException(
                    "CURRENT_TIMELINE Deterministic Conclusion의 monthIndex가 Scenario와 일치하지 않습니다. category="
                            + section.category()
                            + ", rule="
                            + conclusion.ruleCode()
                            + ", scenarioOrder="
                            + scenario.scenarioOrder()
                            + ", expectedMonthIndex="
                            + scenario.monthIndex()
                            + ", actualMonthIndex="
                            + reference.monthIndex()
            );
        }
    }


    /*
     * =================================================
     * TIMELINE_YEAR Deterministic
     * =================================================
     */

    private void validateTimelineYearDeterministicConclusion(

            ConsultationGroundingSection section,

            ConsultationGroundingTimelineYear timelineYear,

            ConsultationGroundingDeterministicConclusion conclusion

    ) {

        Objects.requireNonNull(
                conclusion,
                "TIMELINE_YEAR Grounding Deterministic Conclusion은 null일 수 없습니다."
        );

        ConsultationCategoryDeterministicDependencyReference reference =
                conclusion.structuralReference();

        validateStructuralCategory(
                section,
                conclusion,
                reference
        );

        if (
                reference.layer()
                        != ConsultationCategoryDeterministicLayer
                        .TIMELINE_YEAR
        ) {

            throw new IllegalStateException(
                    "TimelineYear deterministicContract에는 TIMELINE_YEAR Layer만 허용됩니다. category="
                            + section.category()
                            + ", rule="
                            + conclusion.ruleCode()
                            + ", sajuYear="
                            + timelineYear.sajuYear()
                            + ", actualLayer="
                            + reference.layer()
            );
        }

        if (
                reference.sajuYear() == null
        ) {

            throw new IllegalStateException(
                    "TIMELINE_YEAR Deterministic Conclusion에는 sajuYear가 필요합니다. category="
                            + section.category()
                            + ", rule="
                            + conclusion.ruleCode()
            );
        }

        if (
                reference.sajuYear().intValue()
                        != timelineYear.sajuYear()
        ) {

            throw new IllegalStateException(
                    "TIMELINE_YEAR Deterministic Conclusion의 sajuYear가 TimelineYear와 일치하지 않습니다. category="
                            + section.category()
                            + ", rule="
                            + conclusion.ruleCode()
                            + ", expectedYear="
                            + timelineYear.sajuYear()
                            + ", actualYear="
                            + reference.sajuYear()
            );
        }

        if (
                reference.monthIndex() != null
        ) {

            throw new IllegalStateException(
                    "TIMELINE_YEAR Deterministic Conclusion에는 monthIndex가 존재할 수 없습니다. category="
                            + section.category()
                            + ", rule="
                            + conclusion.ruleCode()
                            + ", sajuYear="
                            + timelineYear.sajuYear()
                            + ", monthIndex="
                            + reference.monthIndex()
            );
        }
    }


    /*
     * =================================================
     * Structural Category
     * =================================================
     */

    private void validateStructuralCategory(

            ConsultationGroundingSection section,

            ConsultationGroundingDeterministicConclusion conclusion,

            ConsultationCategoryDeterministicDependencyReference reference

    ) {

        Objects.requireNonNull(
                reference,
                "Grounding Deterministic Conclusion의 structuralReference는 null일 수 없습니다. category="
                        + section.category()
                        + ", rule="
                        + conclusion.ruleCode()
        );

        if (
                reference.category()
                        != section.category()
        ) {

            throw new IllegalStateException(
                    "Grounding Deterministic Conclusion의 structural category가 Section category와 일치하지 않습니다. "
                            + "sectionCategory="
                            + section.category()
                            + ", structuralCategory="
                            + reference.category()
                            + ", rule="
                            + conclusion.ruleCode()
            );
        }
    }


    /*
     * =================================================
     * LEGAL_RISK
     * =================================================
     *
     * 중요:
     *
     * 여기서는 Cross를 다시 계산하지 않는다.
     *
     * LEGAL_RISK_CROSS_ESTABLISHED
     *
     * 공식 Deterministic Conclusion의
     * 존재와 배치만 검증한다.
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
                    "사용 가능한 LEGAL_RISK Section에는 전용 Legal Evidence가 필요합니다."
            );
        }


        /*
         * =================================================
         * Raw NATAL Evidence Status
         * =================================================
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
         * =================================================
         * NATAL Deterministic Contract
         * =================================================
         */

        int natalLegalCrossCount =
                countLegalCrossConclusions(
                        section.natalDeterministicConclusions()
                );

        if (
                natalLegalCrossCount > 1
        ) {

            throw new IllegalStateException(
                    "LEGAL_RISK NATAL에 LEGAL_RISK_CROSS_ESTABLISHED Conclusion이 중복되었습니다. count="
                            + natalLegalCrossCount
            );
        }

        if (
                natalLegalCrossCount == 1
        ) {

            ConsultationGroundingDeterministicConclusion conclusion =
                    findSingleLegalCrossConclusion(
                            section.natalDeterministicConclusions()
                    );

            validateLegalCrossConclusion(
                    conclusion,
                    ConsultationCategoryDeterministicLayer.NATAL,
                    null,
                    null
            );
        }


        /*
         * =================================================
         * CURRENT_TIMELINE Deterministic Contract
         * =================================================
         */

        int currentTimelineLegalCrossCount =
                0;

        for (
                ConsultationGroundingTimelineScenario scenario
                : section.timelineScenarios()
        ) {

            ConsultationGroundingTimelineScenario normalizedScenario =
                    Objects.requireNonNull(
                            scenario,
                            "LEGAL_RISK Timeline Scenario는 null일 수 없습니다."
                    );

            for (
                    ConsultationGroundingEvidence evidence
                    : normalizedScenario.evidences()
            ) {

                validateLegalStatus(
                        evidence
                );
            }

            int scenarioLegalCrossCount =
                    countLegalCrossConclusions(
                            normalizedScenario
                                    .deterministicConclusions()
                    );


            /*
             * =================================================
             * 빈 Scenario
             * =================================================
             *
             * Cross를 성립시킨 현재 Segment Evidence가
             * 없으므로 CURRENT_TIMELINE Legal Cross
             * Conclusion도 없어야 한다.
             */

            if (
                    normalizedScenario
                            .evidences()
                            .isEmpty()
            ) {

                if (
                        scenarioLegalCrossCount != 0
                ) {

                    throw new IllegalStateException(
                            "LEGAL_RISK 빈 Timeline Scenario에 "
                                    + "LEGAL_RISK_CROSS_ESTABLISHED Conclusion이 존재합니다. "
                                    + "scenarioOrder="
                                    + normalizedScenario.scenarioOrder()
                                    + ", sajuYear="
                                    + normalizedScenario.sajuYear()
                                    + ", monthIndex="
                                    + normalizedScenario.monthIndex()
                    );
                }

                continue;
            }


            /*
             * =================================================
             * non-empty Scenario
             * =================================================
             *
             * ConsultationLegalRiskAnalyzer가
             * Legal Segment Evidence를 보존했고,
             *
             * LegalRiskCrossEstablishedDeterministicRule이
             * 공식 Cross Conclusion을 생성한 상태여야 한다.
             */

            if (
                    scenarioLegalCrossCount != 1
            ) {

                throw new IllegalStateException(
                        "LEGAL_RISK non-empty Timeline Scenario에는 "
                                + "정확히 하나의 LEGAL_RISK_CROSS_ESTABLISHED "
                                + "CURRENT_TIMELINE Conclusion이 필요합니다. "
                                + "scenarioOrder="
                                + normalizedScenario.scenarioOrder()
                                + ", sajuYear="
                                + normalizedScenario.sajuYear()
                                + ", monthIndex="
                                + normalizedScenario.monthIndex()
                                + ", actualCount="
                                + scenarioLegalCrossCount
                );
            }

            ConsultationGroundingDeterministicConclusion conclusion =
                    findSingleLegalCrossConclusion(
                            normalizedScenario
                                    .deterministicConclusions()
                    );

            validateLegalCrossConclusion(
                    conclusion,
                    ConsultationCategoryDeterministicLayer
                            .CURRENT_TIMELINE,
                    normalizedScenario.sajuYear(),
                    normalizedScenario.monthIndex()
            );

            currentTimelineLegalCrossCount++;
        }


        /*
         * =================================================
         * Available LEGAL_RISK Contract
         * =================================================
         *
         * 사용 가능한 LEGAL_RISK Section이라면
         *
         * NATAL
         * 또는
         * CURRENT_TIMELINE
         *
         * 가운데 하나 이상에서
         * 공식 Legal Cross Conclusion이
         * 존재해야 한다.
         */

        if (
                natalLegalCrossCount == 0
                        && currentTimelineLegalCrossCount == 0
        ) {

            throw new IllegalStateException(
                    "사용 가능한 LEGAL_RISK Section에 "
                            + "LEGAL_RISK_CROSS_ESTABLISHED "
                            + "Deterministic Conclusion이 없습니다."
            );
        }
    }


    /*
     * =================================================
     * LEGAL_RISK Raw Evidence Status
     * =================================================
     */

    private void validateLegalStatus(

            ConsultationGroundingEvidence evidence

    ) {

        Objects.requireNonNull(
                evidence,
                "LEGAL_RISK Grounding Evidence는 null일 수 없습니다."
        );

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


    /*
     * =================================================
     * Legal Cross Conclusion Count
     * =================================================
     *
     * Cross 성립 여부를 계산하지 않는다.
     *
     * 이미 Java Rule이 생성한
     * 정확한 RuleCode를 찾는 역할만 한다.
     */

    private int countLegalCrossConclusions(

            List<ConsultationGroundingDeterministicConclusion>
                    conclusions

    ) {

        if (
                conclusions == null
                        || conclusions.isEmpty()
        ) {

            return 0;
        }

        int count =
                0;

        for (
                ConsultationGroundingDeterministicConclusion conclusion
                : conclusions
        ) {

            ConsultationGroundingDeterministicConclusion normalized =
                    Objects.requireNonNull(
                            conclusion,
                            "LEGAL_RISK Deterministic Conclusion은 null일 수 없습니다."
                    );

            if (
                    normalized
                            .ruleCode()
                            .equals(
                                    LEGAL_RISK_CROSS_ESTABLISHED_RULE_CODE
                            )
            ) {

                count++;
            }
        }

        return count;
    }


    /*
     * =================================================
     * Legal Cross Conclusion Find
     * =================================================
     */

    private ConsultationGroundingDeterministicConclusion
    findSingleLegalCrossConclusion(

            List<ConsultationGroundingDeterministicConclusion>
                    conclusions

    ) {

        ConsultationGroundingDeterministicConclusion found =
                null;

        for (
                ConsultationGroundingDeterministicConclusion conclusion
                : conclusions
        ) {

            ConsultationGroundingDeterministicConclusion normalized =
                    Objects.requireNonNull(
                            conclusion,
                            "LEGAL_RISK Deterministic Conclusion은 null일 수 없습니다."
                    );

            if (
                    !normalized
                            .ruleCode()
                            .equals(
                                    LEGAL_RISK_CROSS_ESTABLISHED_RULE_CODE
                            )
            ) {

                continue;
            }

            if (
                    found != null
            ) {

                throw new IllegalStateException(
                        "LEGAL_RISK_CROSS_ESTABLISHED Deterministic Conclusion이 중복되었습니다."
                );
            }

            found =
                    normalized;
        }

        if (
                found == null
        ) {

            throw new IllegalStateException(
                    "LEGAL_RISK_CROSS_ESTABLISHED Deterministic Conclusion을 찾을 수 없습니다."
            );
        }

        return found;
    }


    /*
     * =================================================
     * Legal Cross Conclusion Validation
     * =================================================
     *
     * 의미를 재계산하지 않는다.
     *
     * 공식 Java Rule 결과의:
     *
     * - Category
     * - RuleCode
     * - Status
     * - Layer
     * - Timeline 좌표
     *
     * 만 검증한다.
     */

    private void validateLegalCrossConclusion(

            ConsultationGroundingDeterministicConclusion conclusion,

            ConsultationCategoryDeterministicLayer expectedLayer,

            Integer expectedSajuYear,

            Integer expectedMonthIndex

    ) {

        Objects.requireNonNull(
                conclusion,
                "LEGAL_RISK Cross Conclusion은 필수입니다."
        );

        Objects.requireNonNull(
                expectedLayer,
                "LEGAL_RISK Cross Conclusion 검증에는 expectedLayer가 필요합니다."
        );

        if (
                !LEGAL_RISK_CROSS_ESTABLISHED_RULE_CODE
                        .equals(
                                conclusion.ruleCode()
                        )
        ) {

            throw new IllegalStateException(
                    "LEGAL_RISK Cross Conclusion의 RuleCode가 일치하지 않습니다. expected="
                            + LEGAL_RISK_CROSS_ESTABLISHED_RULE_CODE
                            + ", actual="
                            + conclusion.ruleCode()
            );
        }

        if (
                conclusion.status()
                        != ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            throw new IllegalStateException(
                    "LEGAL_RISK_CROSS_ESTABLISHED의 Status는 PRESSURE여야 합니다. actual="
                            + conclusion.status()
            );
        }

        ConsultationCategoryDeterministicDependencyReference reference =
                conclusion.structuralReference();

        if (
                reference.category()
                        != ConsultationCategory.LEGAL_RISK
        ) {

            throw new IllegalStateException(
                    "LEGAL_RISK_CROSS_ESTABLISHED의 structural category가 LEGAL_RISK가 아닙니다. actual="
                            + reference.category()
            );
        }

        if (
                reference.layer()
                        != expectedLayer
        ) {

            throw new IllegalStateException(
                    "LEGAL_RISK_CROSS_ESTABLISHED의 Layer가 예상값과 일치하지 않습니다. expected="
                            + expectedLayer
                            + ", actual="
                            + reference.layer()
            );
        }

        if (
                expectedLayer
                        == ConsultationCategoryDeterministicLayer.NATAL
        ) {

            if (
                    reference.sajuYear() != null
                            || reference.monthIndex() != null
            ) {

                throw new IllegalStateException(
                        "LEGAL_RISK NATAL Cross Conclusion에는 Timeline 좌표가 존재할 수 없습니다. "
                                + "sajuYear="
                                + reference.sajuYear()
                                + ", monthIndex="
                                + reference.monthIndex()
                );
            }

            return;
        }

        if (
                expectedLayer
                        == ConsultationCategoryDeterministicLayer
                        .CURRENT_TIMELINE
        ) {

            if (
                    expectedSajuYear == null
                            || expectedMonthIndex == null
            ) {

                throw new IllegalArgumentException(
                        "LEGAL_RISK CURRENT_TIMELINE Cross 검증에는 예상 sajuYear와 monthIndex가 필요합니다."
                );
            }

            if (
                    reference.sajuYear() == null
                            || reference.monthIndex() == null
            ) {

                throw new IllegalStateException(
                        "LEGAL_RISK CURRENT_TIMELINE Cross Conclusion에는 sajuYear와 monthIndex가 필요합니다."
                );
            }

            if (
                    reference.sajuYear()
                            .intValue()
                            != expectedSajuYear
            ) {

                throw new IllegalStateException(
                        "LEGAL_RISK CURRENT_TIMELINE Cross Conclusion의 sajuYear가 Scenario와 일치하지 않습니다. expected="
                                + expectedSajuYear
                                + ", actual="
                                + reference.sajuYear()
                );
            }

            if (
                    reference.monthIndex()
                            .intValue()
                            != expectedMonthIndex
            ) {

                throw new IllegalStateException(
                        "LEGAL_RISK CURRENT_TIMELINE Cross Conclusion의 monthIndex가 Scenario와 일치하지 않습니다. expected="
                                + expectedMonthIndex
                                + ", actual="
                                + reference.monthIndex()
                );
            }

            return;
        }

        throw new IllegalStateException(
                "LEGAL_RISK_CROSS_ESTABLISHED에는 NATAL 또는 CURRENT_TIMELINE Layer만 허용됩니다. actual="
                        + expectedLayer
        );
    }


    /*
     * =================================================
     * Raw Evidence Policy
     * =================================================
     */

    private void validateEvidence(

            ConsultationGroundingEvidence evidence,

            ConsultationGroundingPolicy policy

    ) {

        Objects.requireNonNull(
                evidence,
                "Grounding Evidence는 null일 수 없습니다."
        );

        Objects.requireNonNull(
                policy,
                "Grounding Policy는 null일 수 없습니다."
        );

        if (
                !policy.allowsRule(
                        evidence.domain(),
                        evidence.ruleCode()
                )
        ) {

            throw new IllegalStateException(
                    "ConsultationCategory에 허용되지 않은 LifeDomain/RuleCode Evidence가 포함되었습니다. "
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