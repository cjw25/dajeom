package com.dajeom.analyzer;

import com.dajeom.model.ConsultationGroundingAnalysis;
import com.dajeom.model.ConsultationGroundingPolicy;
import com.dajeom.model.ConsultationGroundingPolicyAnalysis;
import com.dajeom.model.ConsultationGroundingSection;
import com.dajeom.model.ConsultationInterpretationPromptContext;
import com.dajeom.model.ConsultationInterpretationPromptSection;
import com.dajeom.type.ConsultationGroundingConstraint;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 20차.
 *
 * ConsultationGroundingPolicyAnalysis를
 * 실제 LLM 입력용 Prompt Context로 조립한다.
 *
 * =================================================
 *
 * 여기서는 프롬프트 문자열 하나로
 * 모든 데이터를 평탄화하지 않는다.
 *
 * 구조화된:
 *
 * - 전역 Interpretation 계약
 * - 사용자 질문
 * - Category
 * - Category별 Constraint
 * - 원국 Evidence
 * - Timeline Scenario
 *
 * 를 그대로 유지한다.
 *
 * =================================================
 *
 * 실제 OpenAI API 호출 형식:
 *
 * - system message
 * - developer message
 * - structured output
 * - JSON schema
 *
 * 등은 외부 Client 구현에 따라 달라질 수 있으므로
 * 이 Analyzer에서 결정하지 않는다.
 *
 * =================================================
 *
 * 이 클래스의 결과가
 * 외부 LLM Adapter의 공식 입력 Anchor가 된다.
 */
@Component
public class ConsultationInterpretationPromptContextAnalyzer {

    public static final String GROUNDING_VERSION =
            "DAJEOM_AI_GROUNDING_20";

    private static final List<String>
            GLOBAL_INSTRUCTIONS =
            List.of(
                    "당신의 역할은 제공된 사주 분석 결과를 설명하는 것이며, 새로운 사주 계산을 수행하는 것이 아니다.",

                    "제공되지 않은 명리 근거, 운의 상태, 출생시간 정보 또는 사건을 임의로 추정하거나 생성하지 않는다.",

                    "SUPPORTIVE, PRESSURE, MIXED, CONTEXTUAL은 성공·실패 확률이나 절대적인 길흉 점수가 아니다.",

                    "서로 다른 방향의 Evidence가 동시에 존재하면 하나를 제거하거나 평균내지 말고 각각의 의미와 조건을 함께 설명한다.",

                    "Timeline Scenario가 여러 개이면 하나를 실제 Scenario로 임의 선택하지 않고 가능한 흐름이 복수임을 명확히 설명한다.",

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
                        .toList();

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
                        .timelineScenarios()
        );
    }
}