package com.dajeom.analyzer;

import com.dajeom.model.ConsultationGroundingAnalysis;
import com.dajeom.model.ConsultationGroundingPolicy;
import com.dajeom.model.ConsultationGroundingPolicyAnalysis;
import com.dajeom.model.ConsultationGroundingSection;
import com.dajeom.model.ConsultationInterpretationPromptContext;
import com.dajeom.model.ConsultationInterpretationPromptSection;
import com.dajeom.model.NatalPersonalityStatementContract;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationGroundingConstraint;
import com.dajeom.type.PersonalityStatementContractGuardrail;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Grounding Policy를
 * 외부 LLM 공식 Prompt Context로 조립한다.
 *
 * =================================================
 * 일반 Category
 * =================================================
 *
 * 기존 LifeDomain Grounding 구조
 *
 * +
 *
 * Category Deterministic Meaning Contract
 *
 * 를 함께 전달한다.
 *
 * =================================================
 * NATAL
 * =================================================
 *
 * 일반 Category의:
 *
 * natalDeterministicConclusions
 *
 * 가 존재하면
 * 해당 Category의 공식 NATAL Meaning Boundary다.
 *
 * natalEvidences는:
 *
 * - 계산 근거
 * - provenance
 * - 세부 설명 자료
 *
 * 로 사용한다.
 *
 * raw natalEvidences를 다시 합산하여
 * 새로운 Category 결론을 만들지 않는다.
 *
 * =================================================
 * CURRENT_TIMELINE
 * =================================================
 *
 * ConsultationGroundingTimelineScenario 내부의:
 *
 * deterministicConclusions
 *
 * 가 존재하면
 * 해당 Scenario에서 Java가 확정한
 * 공식 CURRENT_TIMELINE Meaning Boundary다.
 *
 * Scenario의 raw evidences는
 * 그 결과를 설명하는 세부 근거다.
 *
 * =================================================
 * CURRENT_TIMELINE 독립성
 * =================================================
 *
 * 같은 Scenario 안에:
 *
 * - 대운 Context
 * - 세운 Context
 * - 세운 Functional
 * - 월운 Context
 * - 월운 Functional
 *
 * 결과가 여러 개 존재하더라도
 * 단순 공존만으로:
 *
 * - 하나의 종합 Status
 * - 새로운 인과
 * - 새로운 순서
 * - 새로운 강화/약화 관계
 *
 * 를 만들지 않는다.
 *
 * 관계·인과·결합 의미는
 * Java Deterministic Conclusion 자체가
 * 명시적으로 계산한 경우에만
 * 그 범위에서 설명한다.
 *
 * =================================================
 * PERSONALITY
 * =================================================
 *
 * NatalPersonalityStatementContract가
 * 공식 직접 원국 Meaning Contract.
 *
 * 일반 Category의
 * natalDeterministicConclusions 구조와
 * PERSONALITY Contract를 합치지 않는다.
 *
 * =================================================
 * PAST_LUCK / FUTURE_LUCK
 * =================================================
 *
 * Timeline Year 안의:
 *
 * deterministicContract
 *
 * 를 공식 연도 해석 Meaning Contract로 사용한다.
 *
 * =================================================
 * Timeline Year deterministicContract
 * =================================================
 *
 * OVERALL_NARRATIVE
 * BIG_FLOW
 * PRIMARY_ACTIVE_DOMAIN
 * PRIMARY_PRESSURE_DOMAIN
 * CROSS_DOMAIN_PATTERN
 * TURNING_POINT
 *
 * =================================================
 * raw Timeline Year Evidence
 * =================================================
 *
 * 위 결과를 설명하기 위한
 * 세부 Grounding Source다.
 *
 * =================================================
 * 가장 중요한 계약
 * =================================================
 *
 * 외부 LLM은 raw Evidence를 다시 합산하여:
 *
 * - 새로운 NATAL Category Status
 * - 새로운 CURRENT_TIMELINE 종합 Status
 * - 새로운 BIG_FLOW
 * - 새로운 Primary Domain
 * - 새로운 Cross-Domain Pattern
 * - 새로운 Turning Point
 *
 * 를 만들 수 없다.
 *
 * =================================================
 * 관계형 풀이 금지
 * =================================================
 *
 * 서로 다른 독립 Conclusion이
 * 같은 Prompt Section에 있다는 이유만으로:
 *
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 함께 있으므로 C다
 * - 먼저 A, 이후 B
 * - A가 커질수록 B
 *
 * 같은 관계·인과·순서를 만들지 않는다.
 *
 * 그런 의미는 별도의 Java 계산 결과가
 * 실제로 존재할 때만 허용한다.
 */
@Component
public class ConsultationInterpretationPromptContextAnalyzer {

    public static final String GROUNDING_VERSION =
            "DAJEOM_AI_GROUNDING_26_OFFICIAL_MEANING_ONLY";


    private static final List<String>
            GLOBAL_INSTRUCTIONS =
            List.of(

                    "당신의 역할은 제공된 사주 분석 결과를 설명하는 것이며, 새로운 사주 계산을 수행하는 것이 아니다.",

                    "제공되지 않은 명리 근거, 운의 상태, 출생시간 정보 또는 사건을 임의로 추정하거나 생성하지 않는다.",

                    "SUPPORTIVE, PRESSURE, MIXED, CONTEXTUAL은 성공·실패 확률이나 절대적인 길흉 점수가 아니다.",

                    "Evidence의 narrativePriority와 Source 개수 정보는 실제 deterministic provenance를 이용한 내부 서술 우선순위이며 적중 확률, 신뢰 확률, 길흉 점수로 해석하지 않는다.",

                    "narrativePriority 숫자는 사용자에게 설명하지 않고 상담문에서 어떤 근거를 먼저 깊게 설명할지 결정하는 용도로만 사용한다.",

                    "비보조 Source Type이 다양하거나 독립 SourceKey가 여러 개 사용된 Evidence는 상담문의 핵심 특징 후보로 먼저 검토하되 그 사실 자체를 사용자에게 숫자로 노출하지 않는다.",

                    "같은 Domain과 같은 Status가 여러 Rule에서 반복되더라도 이를 성공 확률이나 사건 발생 확률로 변환하지 않는다.",

                    "같은 방향의 Evidence가 반복되어도 반대 방향 Evidence가 존재하면 반대 방향을 삭제하거나 약화시키지 않는다.",

                    "서로 다른 방향의 Evidence가 동시에 존재하면 하나를 제거하거나 평균내지 말고 각각의 의미와 조건을 보존한다.",

                    "낮은 narrativePriority는 Evidence가 틀리거나 불필요하다는 뜻이 아니며 높은 우선순위 Evidence를 설명한 뒤 필요한 경우 보충적으로 사용한다.",

                    "일반 ConsultationCategory의 natalDeterministicConclusions가 존재하면 그것이 해당 Category의 공식 NATAL 의미 계약이며 natalEvidences는 그 결론을 설명하는 세부 근거로 사용한다.",

                    "natalDeterministicConclusions가 존재하는 Category에서 raw natalEvidences를 다시 합산하거나 개수를 비교하여 새로운 NATAL Status, 새로운 결론 또는 새로운 관계를 만들지 않는다.",

                    "서로 다른 natalDeterministicConclusions는 각각 Java에서 계산된 독립 결과로 유지하며 단순히 함께 존재한다는 이유로 서로의 원인, 결과, 전후 단계 또는 강화 조건으로 연결하지 않는다.",

                    "하나의 natalDeterministicConclusion 자체가 Java에서 명시적으로 계산한 관계 또는 조건을 포함하는 경우에는 그 Conclusion이 허용한 의미 범위만 자연어로 설명하고 추가 관계를 확장하지 않는다.",

                    "현재 Timeline Scenario의 deterministicConclusions가 존재하면 그것이 해당 Scenario의 공식 CURRENT_TIMELINE 의미 계약이며 Scenario의 raw evidences는 그 결과를 설명하는 세부 근거로만 사용한다.",

                    "같은 Timeline Scenario 안에 대운, 세운, 세운 Functional, 월운, 월운 Functional 결과가 함께 존재해도 별도 Java 계산 없이 서로를 강화·약화시키거나 원인과 결과로 연결하지 않는다.",

                    "같은 Timeline Scenario 안의 서로 다른 deterministicConclusions를 하나의 SUPPORTIVE, PRESSURE 또는 MIXED 종합 상태로 다시 계산하지 않는다.",

                    "CURRENT_YEAR_LUCK에 여러 LifeDomain의 deterministicConclusions가 존재하더라도 이를 하나의 전체 현재연도 Status로 합성하지 않고 각 Domain 및 시간층의 독립 계산 결과로 유지한다.",

                    "서로 다른 LifeDomain의 deterministicConclusions가 같은 Scenario에 존재한다는 이유만으로 A 때문에 B, A가 B로 이어진다, A와 B가 결합되어 C다 같은 새로운 Cross-Domain 의미를 만들지 않는다.",

                    "현재 Timeline Scenario가 여러 개이면 하나를 실제 Scenario로 임의 선택하지 않고 가능한 흐름이 복수임을 보존한다.",

                    "서로 다른 Timeline Scenario의 deterministicConclusions 또는 evidences를 하나의 Scenario처럼 합치거나 다수결하지 않는다.",

                    "Timeline Year Range는 실제 여러 시점의 deterministic 계산을 연도별로 정리한 결과이며 특정 사건 기록이나 사건 예측이 아니다.",

                    "PAST_LUCK 또는 FUTURE_LUCK의 각 Timeline Year의 deterministicContract는 해당 명리연도 해석의 공식 상위 계약이며 raw evidences는 그 계약을 설명하는 세부 근거로만 사용한다.",

                    "Timeline Year deterministicContract의 OVERALL_NARRATIVE와 BIG_FLOW가 정한 연도 전체 방향을 raw LifeDomain Evidence를 다시 합산하거나 개수를 비교하여 변경하지 않는다.",

                    "PRIMARY_ACTIVE_DOMAIN은 가장 좋은 분야나 가장 나쁜 분야가 아니라 해당 연도에서 시간적 작동 자격이 상대적으로 분명한 생활영역이라는 의미로만 설명한다.",

                    "PRIMARY_PRESSURE_DOMAIN은 전체 연도를 PRESSURE로 만드는 값이 아니라 명시적인 부담 방향 가운데 하나의 주된 부담축을 구조적으로 식별할 수 있을 때만 존재하는 제한된 판단이다.",

                    "CROSS_DOMAIN_PATTERN은 여러 생활영역의 deterministic 방향이 어떻게 공존하는지를 설명하는 구조이며 BIG_FLOW 또는 OVERALL_NARRATIVE의 전체 방향을 다시 계산하는 근거로 사용하지 않는다.",

                    "TURNING_POINT는 전후 명리연도 deterministic 구조의 변화가 확인되었다는 의미이며 이직, 결혼, 이별, 손실, 질병, 사고 등 구체적 사건이 발생하거나 발생할 것이라는 의미가 아니다.",

                    "Timeline Year의 raw evidences에서 PRESSURE 또는 SUPPORTIVE가 반복되더라도 그 반복 횟수로 deterministicContract의 결론을 강화하거나 약화시키지 않는다.",

                    "Timeline Year에 여러 Evidence가 존재한다고 해서 가장 강해 보이는 하나만 선택하지 않고 deterministicContract가 허용한 전체 구조 안에서 필요한 세부 근거를 설명한다.",

                    "Timeline Year의 monthIndices는 계산에 참여한 명리 월 범위를 나타내는 내부 시간 정보이며 이를 사용해 입력에 없는 특정 월 사건을 만들어내지 않는다.",

                    "PAST_LUCK의 Timeline Year는 과거에 실제 특정 사건이 발생했다는 뜻이 아니며 당시의 deterministic 생활 흐름 수준에서 설명한다.",

                    "FUTURE_LUCK의 Timeline Year는 미래 사건 확정이 아니며 해당 연도의 deterministicContract와 공식 판단메모가 실제로 명시한 의미 범위에서만 설명한다.",

                    "출생시간이 알려지지 않은 경우 시주나 시주 기반 의미를 추측해서 보완하지 않는다.",

                    "신살과 같은 AUXILIARY 근거는 핵심 구조를 뒤집거나 단독 결론의 근거로 사용하지 않는다.",

                    "합의 존재를 실제 합화 활성으로 간주하지 않고, 충이나 형의 존재를 특정 사건 발생으로 확정하지 않는다.",

                    "재물 획득, 취업, 합격, 승진, 결혼, 이별, 소송, 질병 발생 등 구체적인 미래 사건을 확정적으로 예언하지 않는다.",

                    "건강 관련 판단은 특정 장기, 질병, 진단 또는 치료 판단으로 변환하지 않는다. 생활 리듬, 관리, 활동, 휴식, 회복 같은 구체적인 생활 의미도 공식 deterministic 판단메모 또는 허용된 Evidence에 실제로 존재할 때만 사용한다.",

                    "법률 관련 내용은 실제 법적 결과나 승패를 예측하지 않는다. 계약, 확인, 기록, 조율, 책임 범위 같은 구체적인 생활 의미도 공식 deterministic 판단메모 또는 전용 Java 분석 결과에 실제로 존재할 때만 사용한다.",

                    "내부 명리 용어를 그대로 나열하지 말고 각 공식 판단메모가 실제로 허용한 의미만 사용자가 이해할 수 있는 일상 한국어로 번역한다. ConsultationCategory, LifeDomain, Status 이름 자체는 의미 Evidence가 아니므로 카테고리명·도메인명·상태명만 보고 행동, 심리, 관계 방식, 돈의 운용, 역할, 학습 방식, 생활 습관 같은 구체 의미를 추가하지 않는다.",

                    "Evidence 또는 deterministicConclusion에 없는 관계, 인과, 심리적 시간순서, 사건 연결을 자연스러운 문장을 만들기 위해 임의로 추가하지 않는다.",

                    "Evidence에 없는 내용을 전문적인 명리 지식으로 보충하지 않는다.",

                    "unavailable로 표시된 상담 분야는 내용을 만들어 채우지 않고 현재 계산 범위에서 충분한 근거가 준비되지 않았음을 따른다."
            );


    public ConsultationInterpretationPromptContext analyze(

            ConsultationGroundingPolicyAnalysis
                    policyAnalysis

    ) {

        Objects.requireNonNull(
                policyAnalysis,
                "ConsultationInterpretationPromptContextAnalyzer에는 ConsultationGroundingPolicyAnalysis가 필요합니다."
        );

        ConsultationGroundingAnalysis groundingAnalysis =
                policyAnalysis
                        .groundingAnalysis();

        List<ConsultationInterpretationPromptSection>
                sections =
                new ArrayList<>();

        for (
                ConsultationGroundingSection groundingSection
                : groundingAnalysis.sections()
        ) {

            ConsultationGroundingPolicy policy =
                    policyAnalysis
                            .get(
                                    groundingSection
                                            .category()
                            );

            sections.add(
                    toPromptSection(
                            groundingSection,
                            policy
                    )
            );
        }

        return new ConsultationInterpretationPromptContext(
                GROUNDING_VERSION,

                groundingAnalysis
                        .questionRoutingStatus(),

                groundingAnalysis
                        .consultationQuestion(),

                groundingAnalysis
                        .completeChart(),

                groundingAnalysis
                        .uncertaintyExists(),

                GLOBAL_INSTRUCTIONS,

                List.copyOf(
                        sections
                )
        );
    }


    /*
     * =================================================
     * Section
     * =================================================
     */

    private ConsultationInterpretationPromptSection
    toPromptSection(

            ConsultationGroundingSection groundingSection,

            ConsultationGroundingPolicy policy

    ) {

        Objects.requireNonNull(
                groundingSection,
                "ConsultationGroundingSection은 필수입니다."
        );

        Objects.requireNonNull(
                policy,
                "ConsultationGroundingPolicy는 필수입니다."
        );

        if (
                groundingSection.category()
                        != policy.category()
        ) {

            throw new IllegalStateException(
                    "Grounding Section과 Policy의 ConsultationCategory가 일치하지 않습니다."
            );
        }

        List<String> constraints =
                createConstraints(
                        groundingSection,
                        policy
                );

        /*
         * =================================================
         * Deterministic Grounding 전달
         * =================================================
         *
         * NATAL:
         *
         * natalDeterministicConclusions를
         * PromptSection의 독립 필드로 그대로 전달한다.
         *
         * -------------------------------------------------
         *
         * CURRENT_TIMELINE:
         *
         * ConsultationGroundingTimelineScenario 안에
         * deterministicConclusions가 이미 있으므로
         * timelineScenarios 자체를 그대로 전달한다.
         *
         * -------------------------------------------------
         *
         * PAST / FUTURE:
         *
         * ConsultationGroundingTimelineYear 안에
         * deterministicContract가 이미 있으므로
         * timelineYears 자체를 그대로 전달한다.
         *
         * =================================================
         * 중요
         * =================================================
         *
         * 여기서는:
         *
         * - 복사 과정에서 재계산하지 않는다.
         * - Evidence를 Conclusion으로 승격하지 않는다.
         * - Conclusion을 병합하지 않는다.
         * - Status를 합성하지 않는다.
         */

        return new ConsultationInterpretationPromptSection(

                groundingSection
                        .category()
                        .getOrder(),

                groundingSection
                        .category(),

                groundingSection
                        .title(),

                groundingSection
                        .available(),

                groundingSection
                        .unavailableReason(),

                constraints,

                groundingSection
                        .natalEvidences(),

                groundingSection
                        .natalDeterministicConclusions(),

                groundingSection
                        .timelineScenarios(),

                groundingSection
                        .timelineYears(),

                groundingSection
                        .personalityStatementContract()
        );
    }


    /*
     * =================================================
     * Constraint
     * =================================================
     */

    private List<String> createConstraints(

            ConsultationGroundingSection groundingSection,

            ConsultationGroundingPolicy policy

    ) {

        List<String> result =
                new ArrayList<>();

        /*
         * =================================================
         * 기존 Category Policy
         * =================================================
         */

        result.addAll(
                policy
                        .constraints()
                        .stream()
                        .sorted(
                                Comparator.comparingInt(
                                        ConsultationGroundingConstraint
                                                ::ordinal
                                )
                        )
                        .map(
                                ConsultationGroundingConstraint
                                        ::getInstruction
                        )
                        .toList()
        );

        /*
         * =================================================
         * 일반 Category Deterministic Contract
         * =================================================
         */

        appendCategoryDeterministicConstraints(
                groundingSection,
                result
        );

        /*
         * =================================================
         * PAST / FUTURE Timeline Year Contract
         * =================================================
         */

        appendTimelineYearDeterministicConstraints(
                groundingSection,
                result
        );

        /*
         * =================================================
         * PERSONALITY가 아니면 종료
         * =================================================
         */

        if (
                groundingSection.category()
                        != ConsultationCategory.PERSONALITY
        ) {

            return result
                    .stream()
                    .distinct()
                    .toList();
        }

        if (
                !groundingSection
                        .hasPersonalityStatementContract()
        ) {

            return result
                    .stream()
                    .distinct()
                    .toList();
        }

        NatalPersonalityStatementContract contract =
                groundingSection
                        .requirePersonalityStatementContract();

        /*
         * =================================================
         * Personality Source Priority
         * =================================================
         */

        result.add(
                "PERSONALITY에서는 personalityStatementContract를 "
                        + "기본성향 의미의 최우선 deterministic source로 사용한다. "
                        + "natalEvidences는 세부 계산 근거와 provenance 설명 자료이며 "
                        + "Contract가 허용하지 않은 성향을 새로 만들어내거나 "
                        + "Contract의 표현 강도를 끌어올리는 데 사용하지 않는다."
        );

        result.add(
                "Personality Contract의 REQUIRED Bundle에 포함된 "
                        + "mandatorySelections는 최종 기본성향 설명에서 반드시 다룬다."
        );

        result.add(
                "supportingSelections는 중심 설명을 구체화하는 보조 근거로 사용하며, "
                        + "PRIMARY나 STRONG 판정으로 임의 승격하지 않는다."
        );

        result.add(
                "강한 단정 표현은 Contract의 strongSelections에 포함된 의미 범위에서만 허용한다. "
                        + "같은 Bundle 안의 다른 Selection까지 동일한 강도로 확대하지 않는다."
        );

        result.add(
                "internalReferenceSelections는 내부 참고와 문맥 확인에만 사용하며 "
                        + "독립적인 사용자 성향문장이나 강한 단정으로 만들지 않는다."
        );

        result.add(
                "globalBackgroundContexts는 직접 성격판정이 아니라 "
                        + "다른 Personality Statement를 이해하기 위한 배경으로만 사용한다."
        );

        for (
                PersonalityStatementContractGuardrail guardrail
                : contract.guardrails()
        ) {

            result.add(
                    toGuardrailInstruction(
                            guardrail
                    )
            );
        }

        return result
                .stream()
                .distinct()
                .toList();
    }


    /*
     * =================================================
     * 일반 Category Deterministic Constraint
     * =================================================
     */

    private void appendCategoryDeterministicConstraints(

            ConsultationGroundingSection groundingSection,

            List<String> result

    ) {

        Objects.requireNonNull(
                groundingSection,
                "Category Deterministic Constraint 생성에는 Grounding Section이 필요합니다."
        );

        Objects.requireNonNull(
                result,
                "Category Deterministic Constraint 대상 List가 필요합니다."
        );

        /*
         * =================================================
         * PERSONALITY
         * =================================================
         *
         * PERSONALITY의 공식 의미 Contract는
         * NatalPersonalityStatementContract다.
         *
         * Grounding Policy에서 일반 Category
         * deterministic conclusion의 존재를
         * 이미 금지한다.
         */

        if (
                groundingSection.category()
                        == ConsultationCategory.PERSONALITY
        ) {

            return;
        }

        /*
         * =================================================
         * NATAL
         * =================================================
         */

        if (
                groundingSection
                        .hasNatalDeterministicConclusions()
        ) {

            result.add(
                    "이 상담 분야의 natalDeterministicConclusions는 Java가 확정한 공식 NATAL 의미 계약이다. natalEvidences는 그 결론의 세부 근거로만 사용하고 raw Evidence를 다시 합산하여 새로운 NATAL 결론을 만들지 않는다."
            );

            result.add(
                    "여러 natalDeterministicConclusions가 존재하면 각각을 독립 계산 결과로 유지한다. 한 Conclusion이 다른 Conclusion의 원인, 결과, 전후 단계 또는 강화 조건이라고 별도 Java 계산 없이 연결하지 않는다."
            );

            result.add(
                    "natalDeterministicConclusions 사이에 SUPPORTIVE와 PRESSURE가 함께 존재하더라도 이를 자동으로 MIXED로 합성하지 않는다. MIXED는 Java가 해당 Deterministic Conclusion에서 직접 계산한 경우에만 사용한다."
            );

            result.add(
                    "natalDeterministicConclusion에 명시되지 않은 관계·인과·순서를 natalEvidences의 공존만으로 만들어내지 않는다."
            );
        }

        /*
         * =================================================
         * CURRENT_TIMELINE
         * =================================================
         */

        if (
                groundingSection
                        .hasTimelineDeterministicConclusions()
        ) {

            result.add(
                    "Timeline Scenario의 deterministicConclusions는 해당 Scenario에서 Java가 확정한 공식 CURRENT_TIMELINE 의미 계약이다. Scenario의 raw evidences는 그 결과를 설명하는 세부 근거로만 사용한다."
            );

            result.add(
                    "같은 Scenario의 대운, 세운, 세운 Functional, 월운, 월운 Functional 결과는 각각 독립 계산 결과이며 별도 Java 교차 계산이 없으면 서로 강화·약화·원인·결과·전후 단계로 연결하지 않는다."
            );

            result.add(
                    "같은 Scenario에 서로 다른 Status의 deterministicConclusions가 존재해도 하나의 SUPPORTIVE, PRESSURE 또는 MIXED 전체 상태로 다시 계산하지 않는다."
            );

            result.add(
                    "서로 다른 Timeline Scenario의 deterministicConclusions를 합치거나 다수결하지 않으며 하나의 Scenario를 실제 정답 Scenario로 임의 선택하지 않는다."
            );

            if (
                    groundingSection.category()
                            == ConsultationCategory.CURRENT_YEAR_LUCK
            ) {

                result.add(
                        "CURRENT_YEAR_LUCK의 deterministicConclusions는 여러 생활영역과 시간층의 독립 결과 집합이다. 여러 Domain의 결과를 하나의 현재연도 전체 Status나 하나의 종합 서사로 새로 합성하지 않는다."
                );
            }
        }
    }


    /*
     * =================================================
     * PAST / FUTURE Contract Constraint
     * =================================================
     */

    private void appendTimelineYearDeterministicConstraints(

            ConsultationGroundingSection groundingSection,

            List<String> result

    ) {

        ConsultationCategory category =
                groundingSection
                        .category();

        if (
                category
                        != ConsultationCategory.PAST_LUCK

                        && category
                        != ConsultationCategory.FUTURE_LUCK
        ) {

            return;
        }

        /*
         * ConsultationGroundingTimelineYear는
         * 생성 시점부터 deterministicContract를
         * 반드시 가지는 공식 PAST/FUTURE 연도 모델이다.
         *
         * 따라서 Contract 존재 여부를 다시 추정하거나
         * 없는 경우 조용히 건너뛰는 분기를 두지 않는다.
         */

        result.add(
                "이 상담 분야의 Timeline Year에서는 deterministicContract를 각 명리연도의 공식 최상위 의미 계약으로 사용한다."
        );

        result.add(
                "deterministicContract.overallNarrative.status와 bigFlow.status가 해당 명리연도의 전체 방향이며 raw evidences를 다시 세거나 합산해서 다른 전체 방향을 만들지 않는다."
        );

        result.add(
                "primaryActiveDomain이 존재하면 해당 명리연도의 가장 좋은 분야가 아니라 시간적 작동 자격이 상대적으로 가장 분명한 생활영역으로만 설명한다."
        );

        result.add(
                "primaryPressureDomain이 존재하면 해당 연도의 전체 결론을 PRESSURE로 변경하지 말고 이미 정해진 전체 방향 안에서 주된 부담축으로만 설명한다."
        );

        result.add(
                "crossDomainPattern이 존재하면 참여 LifeDomain의 공존 구조를 설명하되 그 status로 overallNarrative 또는 bigFlow의 status를 덮어쓰지 않는다."
        );

        result.add(
                "turningPoint가 존재하면 전후 명리연도 구조 변화가 deterministic하게 확인되었다는 뜻으로만 설명하며 구체적인 사건 발생으로 번역하지 않는다."
        );

        result.add(
                "deterministicContract의 component가 비어 있으면 해당 판단을 확정할 구조적 자격이 없었던 것이므로 raw evidences를 이용해 누락된 PRIMARY_ACTIVE_DOMAIN, PRIMARY_PRESSURE_DOMAIN, CROSS_DOMAIN_PATTERN 또는 TURNING_POINT를 새로 만들어 채우지 않는다."
        );

        result.add(
                "Timeline Year evidences는 deterministicContract의 세부 설명과 근거 확인에 사용하며 최종 방향, Primary Domain 또는 Turning Point를 다시 판정하는 입력으로 사용하지 않는다."
        );
    }


    /*
     * =================================================
     * Personality Guardrail
     * =================================================
     */

    private String toGuardrailInstruction(

            PersonalityStatementContractGuardrail guardrail

    ) {

        Objects.requireNonNull(
                guardrail,
                "Personality Statement Contract Guardrail은 필수입니다."
        );

        return switch (
                guardrail
                ) {

            case DO_NOT_RECALCULATE_SAJU ->
                    "Personality Statement Contract에 전달된 결과를 다시 사주 계산하거나 "
                            + "십성·격국·신강신약·용신 등을 재판정하지 않는다.";

            case DO_NOT_PROMOTE_INTERNAL_REFERENCE ->
                    "INTERNAL_REFERENCE 또는 직접 단정이 허용되지 않은 Personality 근거를 "
                            + "사용자에게 확정적인 성향으로 승격시키지 않는다.";

            case DO_NOT_INVENT_EVENT_OR_HISTORY ->
                    "Personality 근거만으로 사용자의 실제 과거 사건, 특정 경험, "
                            + "가족사, 연애사, 직장사 또는 미래 사건을 만들어내지 않는다.";

            case DO_NOT_CONVERT_CORE_SELF_DIRECTLY_TO_PERSONALITY ->
                    "CoreSelf의 신강·신약이나 자기기반을 "
                            + "'고집이 세다', '소심하다' 같은 단순 성격문장으로 직접 치환하지 않는다.";

            case DO_NOT_CONVERT_INTERPERSONAL_CONTEXT_DIRECTLY_TO_PSYCHOLOGY ->
                    "원국 합·충·형·파·해 등의 관계 Context만으로 "
                            + "'사람을 못 믿는다', '사교적이다', '관계에 집착한다' 같은 "
                            + "심리판정을 직접 만들지 않는다.";

            case PRESERVE_QUALIFICATION_AND_COUNTER_STRUCTURE ->
                    "qualificationRequired가 있는 Personality 구조에서는 "
                            + "충돌·약화·조절·중재 가운데 하나를 임의로 삭제하지 않고 "
                            + "함께 성립하는 조건을 같은 설명 안에서 보존한다.";

            case DO_NOT_FLATTEN_STRUCTURE_TO_GOOD_OR_BAD ->
                    "생·조절 구조를 무조건 장점으로, 충돌·통제·경쟁 구조를 "
                            + "무조건 단점으로 단순화하지 않는다.";

            case USE_BACKGROUND_CONTEXT_AS_CONTEXT_ONLY ->
                    "BACKGROUND_CONTEXT는 다른 성향 근거의 작동 배경으로만 사용하고 "
                            + "독립적인 성격 단정의 근거로 사용하지 않는다.";

            case DO_NOT_INFER_PSYCHOLOGICAL_TIME_ORDER_FROM_FUNCTIONAL_SEQUENCE ->
                    "명리 기능의 A→B 작용방향을 "
                            + "'먼저 A를 하고 나중에 B를 한다'는 실제 심리적 시간순서로 바꾸지 않는다.";

            case DO_NOT_EXPOSE_INTERNAL_STRUCTURE_AS_FINAL_EXPLANATION ->
                    "내부 enum명이나 전통 Pattern 명칭을 나열하는 것으로 설명을 대신하지 말고, "
                            + "Contract가 허용한 의미 범위 안에서 사용자가 이해할 수 있는 일상 한국어로 표현한다.";
        };
    }
}