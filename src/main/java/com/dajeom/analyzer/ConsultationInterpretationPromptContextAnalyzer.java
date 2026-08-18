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
 * 정밀화 20차 / 21차 Range / 22차 Prominence 확장.
 *
 * ConsultationGroundingPolicyAnalysis를
 * 외부 LLM 공식 Prompt Context로 조립한다.
 *
 * =================================================
 *
 * 유지:
 *
 * - 전역 Interpretation 계약
 * - 사용자 질문
 * - Category
 * - Constraint
 * - 원국 Evidence
 * - 현재 Timeline Scenario
 * - PAST/FUTURE Timeline Year Range
 *
 * =================================================
 *
 * 22차 추가:
 *
 * ConsultationGroundingEvidence 내부에
 * deterministic prominence가 포함된다.
 *
 * prominence는:
 *
 * - 정확도 확률
 * - 길흉 점수
 * - 성공 확률
 *
 * 이 아니다.
 *
 * 실제 Source provenance를 근거로
 * 상담문 서술 순서를 정하는 내부 정보다.
 *
 * =================================================
 *
 * 이 클래스는:
 *
 * - 새로운 명리 계산을 하지 않는다.
 * - 연도 흐름을 재판정하지 않는다.
 * - Prompt Section 구조만 조립한다.
 */
@Component
public class ConsultationInterpretationPromptContextAnalyzer {

    public static final String GROUNDING_VERSION =
            "DAJEOM_AI_GROUNDING_22";

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

                    "Timeline Year에 여러 Evidence가 존재한다고 해서 가장 강해 보이는 하나만 선택하지 않고 서로 다른 방향의 Evidence를 함께 보존한다.",

                    "Timeline Year의 monthIndices는 계산에 참여한 명리 월 범위를 나타내는 내부 시간 정보이며 이를 사용해 입력에 없는 특정 월 사건을 만들어내지 않는다.",

                    "PAST_LUCK의 Timeline Year는 과거에 실제 특정 사건이 발생했다는 뜻이 아니며 당시 나타날 수 있었던 생활 흐름 수준에서 설명한다.",

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
                        !=
                        policy.category()
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
                        .timelineScenarios(),

                groundingSection
                        .timelineYears()
        );
    }
}