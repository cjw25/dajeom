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
 * 기존 LifeDomain Grounding 구조 유지.
 *
 * =================================================
 * PERSONALITY
 * =================================================
 *
 * NatalPersonalityStatementContract가
 * 공식 직접 원국 Meaning Contract.
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
 *
 * deterministicContract:
 *
 * OVERALL_NARRATIVE
 * BIG_FLOW
 * PRIMARY_ACTIVE_DOMAIN
 * PRIMARY_PRESSURE_DOMAIN
 * CROSS_DOMAIN_PATTERN
 * TURNING_POINT
 *
 * =================================================
 *
 * raw Timeline Year evidences는
 * 위 결과를 설명하기 위한
 * 세부 Grounding Source다.
 *
 * =================================================
 * 가장 중요한 계약
 * =================================================
 *
 * 외부 LLM은 raw Evidence를 다시 합산하여:
 *
 * - 새로운 BIG_FLOW
 * - 새로운 Primary Domain
 * - 새로운 Cross-Domain Pattern
 * - 새로운 Turning Point
 *
 * 를 만들 수 없다.
 */
@Component
public class ConsultationInterpretationPromptContextAnalyzer {

    public static final String GROUNDING_VERSION =
            "DAJEOM_AI_GROUNDING_24_TIMELINE_YEAR_DETERMINISTIC_CONTRACT";


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

                    "서로 다른 방향의 Evidence가 동시에 존재하면 하나를 제거하거나 평균내지 말고 각각의 의미와 조건을 함께 설명한다.",

                    "낮은 narrativePriority는 Evidence가 틀리거나 불필요하다는 뜻이 아니며 높은 우선순위 Evidence를 설명한 뒤 필요한 경우 보충적으로 사용한다.",

                    "현재 Timeline Scenario가 여러 개이면 하나를 실제 Scenario로 임의 선택하지 않고 가능한 흐름이 복수임을 보존한다.",

                    "Timeline Year Range는 실제 여러 시점의 deterministic 계산을 연도별로 정리한 결과이며 특정 사건 기록이나 사건 예측이 아니다.",

                    "PAST_LUCK 또는 FUTURE_LUCK의 Timeline Year에 deterministicContract가 존재하면 그것이 해당 명리연도 해석의 공식 상위 계약이며 raw evidences는 그 계약을 설명하는 세부 근거로만 사용한다.",

                    "Timeline Year deterministicContract의 OVERALL_NARRATIVE와 BIG_FLOW가 정한 연도 전체 방향을 raw LifeDomain Evidence를 다시 합산하거나 개수를 비교하여 변경하지 않는다.",

                    "PRIMARY_ACTIVE_DOMAIN은 가장 좋은 분야나 가장 나쁜 분야가 아니라 해당 연도에서 시간적 작동 자격이 상대적으로 분명한 생활영역이라는 의미로만 설명한다.",

                    "PRIMARY_PRESSURE_DOMAIN은 전체 연도를 PRESSURE로 만드는 값이 아니라 명시적인 부담 방향 가운데 하나의 주된 부담축을 구조적으로 식별할 수 있을 때만 존재하는 제한된 판단이다.",

                    "CROSS_DOMAIN_PATTERN은 여러 생활영역의 deterministic 방향이 어떻게 공존하는지를 설명하는 구조이며 BIG_FLOW 또는 OVERALL_NARRATIVE의 전체 방향을 다시 계산하는 근거로 사용하지 않는다.",

                    "TURNING_POINT는 전후 명리연도 deterministic 구조의 변화가 확인되었다는 의미이며 이직, 결혼, 이별, 손실, 질병, 사고 등 구체적 사건이 발생하거나 발생할 것이라는 의미가 아니다.",

                    "Timeline Year의 raw evidences에서 PRESSURE 또는 SUPPORTIVE가 반복되더라도 그 반복 횟수로 deterministicContract의 결론을 강화하거나 약화시키지 않는다.",

                    "Timeline Year에 여러 Evidence가 존재한다고 해서 가장 강해 보이는 하나만 선택하지 않고 deterministicContract가 허용한 전체 구조 안에서 필요한 세부 근거를 설명한다.",

                    "Timeline Year의 monthIndices는 계산에 참여한 명리 월 범위를 나타내는 내부 시간 정보이며 이를 사용해 입력에 없는 특정 월 사건을 만들어내지 않는다.",

                    "PAST_LUCK의 Timeline Year는 과거에 실제 특정 사건이 발생했다는 뜻이 아니며 당시의 deterministic 생활 흐름 수준에서 설명한다.",

                    "FUTURE_LUCK의 Timeline Year는 미래 사건 확정이 아니며 해당 시기에 나타날 수 있는 선택, 부담, 활동 방향 수준에서 설명한다.",

                    "출생시간이 알려지지 않은 경우 시주나 시주 기반 의미를 추측해서 보완하지 않는다.",

                    "신살과 같은 AUXILIARY 근거는 핵심 구조를 뒤집거나 단독 결론의 근거로 사용하지 않는다.",

                    "합의 존재를 실제 합화 활성으로 간주하지 않고, 충이나 형의 존재를 특정 사건 발생으로 확정하지 않는다.",

                    "재물 획득, 취업, 합격, 승진, 결혼, 이별, 소송, 질병 발생 등 구체적인 미래 사건을 확정적으로 예언하지 않는다.",

                    "건강 관련 명리 구조는 생활 리듬과 관리 경향 수준에서 설명하며 특정 장기, 질병, 진단 또는 치료 판단으로 변환하지 않는다.",

                    "법률 관련 내용은 실제 법적 결과나 승패를 예측하지 않고 계약, 확인, 기록, 조율 같은 일반적인 주의 방향으로만 설명한다.",

                    "내부 명리 용어를 그대로 나열하기보다 사용자가 이해할 수 있는 생각 방식, 행동 패턴, 관계 방식, 돈의 운용, 일의 부담과 역할, 학습과 표현, 생활 리듬 같은 일상 언어로 번역한다.",

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
         * 중요
         * =================================================
         *
         * ConsultationGroundingTimelineYear 안에
         * deterministicContract가 포함되어 있으므로
         *
         * GroundingSection
         * ->
         * PromptSection
         *
         * 에서는 timelineYears를 그대로 넘긴다.
         *
         * 별도 복사/재구성하지 않는다.
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
                        + "기존 natalEvidences는 보조 교차검증 자료이며 "
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

        boolean contractExists =
                groundingSection
                        .timelineYears()
                        .stream()
                        .anyMatch(
                                timelineYear ->
                                        timelineYear
                                                .hasDeterministicContract()
                        );

        /*
         * 기존 직접 생성 호환 경로에서는
         * Contract가 없을 수 있다.
         *
         * 실제 Spring 공식 실행에서
         * Rule Engine이 연결되어 있다면
         * GroundingAnalyzer가 Contract 누락을
         * 먼저 검증한다.
         */

        if (
                !contractExists
        ) {

            return;
        }

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