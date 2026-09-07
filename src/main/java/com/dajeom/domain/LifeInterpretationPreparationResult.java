package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * AI 호출 직전의 최종 해석 준비 결과.
 *
 * =================================================
 *
 * natalClaims
 *
 * 원국에서 생성한 최종 LifeClaim과
 * 7개 카테고리 Coverage 결과.
 *
 * =================================================
 *
 * pastFlow
 *
 * 대운 / 세운 / 월운 기반
 * 지나온 흐름 내부 분석 결과.
 *
 * =================================================
 *
 * prompts
 *
 * 최종 AI 호출용 7개 Prompt.
 *
 * 1. 성향·기질
 * 2. 지나온 흐름
 * 3. 가족·인간관계
 * 4. 연애·결혼
 * 5. 재물
 * 6. 일·성취
 * 7. 건강·생활
 *
 * =================================================
 *
 * 중요:
 *
 * PAST_FLOW Prompt는 원국 Claim으로 만든 Prompt가 아니라
 * 반드시 pastFlow 기반 Prompt로 교체된 최종 결과다.
 */
public record LifeInterpretationPreparationResult(

        NatalLifeClaimPipelineResult natalClaims,

        PastFlowAnalysis pastFlow,

        LifeInterpretationPromptBundle prompts

) {

    public LifeInterpretationPreparationResult {

        Objects.requireNonNull(
                natalClaims,
                "NatalLifeClaimPipelineResult는 필수입니다."
        );

        Objects.requireNonNull(
                pastFlow,
                "PastFlowAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                prompts,
                "LifeInterpretationPromptBundle은 필수입니다."
        );
    }

    /**
     * 원국 Claim 기준으로
     * 7개 카테고리 Coverage가 모두 확보됐는가.
     *
     * 단, 지나온 흐름의 실제 생성 가능 여부는
     * periodFacts 존재 여부에 따라 별도로 결정된다.
     */
    public boolean natalCoverageComplete() {

        return natalClaims.complete();
    }

    /**
     * 지나온 흐름에서 실제 기간 Claim이 존재하는가.
     */
    public boolean pastFlowReady() {

        return pastFlow.hasClaims();
    }

    /**
     * 특정 카테고리 Prompt가
     * 실제 AI 생성 가능한 상태인가.
     */
    public boolean generationAllowed(
            LifeInterpretationCategory category
    ) {

        Objects.requireNonNull(
                category,
                "LifeInterpretationCategory는 필수입니다."
        );

        return prompts
                .promptFor(category)
                .generationAllowed();
    }

    /**
     * 현재 AI 생성 가능한 카테고리 목록.
     */
    public List<LifeInterpretationCategory>
    generationReadyCategories() {

        return List
                .of(
                        LifeInterpretationCategory.values()
                )
                .stream()
                .filter(
                        this::generationAllowed
                )
                .toList();
    }

    /**
     * 아직 AI 생성하면 안 되는 카테고리 목록.
     */
    public List<LifeInterpretationCategory>
    generationBlockedCategories() {

        return List
                .of(
                        LifeInterpretationCategory.values()
                )
                .stream()
                .filter(
                        category ->
                                !generationAllowed(category)
                )
                .toList();
    }

    /**
     * 최종 7개 카테고리가 모두
     * AI 생성 가능한 상태인가.
     */
    public boolean allGenerationReady() {

        return generationBlockedCategories()
                .isEmpty();
    }
}