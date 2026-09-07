package com.dajeom.service;

import com.dajeom.domain.LifeInterpretationPrompt;

/**
 * 실제 AI 모델에 Prompt를 전달하고
 * 최종 한국어 본문을 반환하는 Adapter 계약.
 *
 * =================================================
 *
 * 이 인터페이스는 특정 AI SDK에 의존하지 않는다.
 *
 * 이후 실제 구현체에서:
 *
 * - OpenAI API
 * - 사내 LLM
 * - 기타 생성 모델
 *
 * 중 하나를 연결하면 된다.
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 1. 입력 Prompt의 systemPrompt / userPrompt를 그대로 사용한다.
 *
 * 2. AI 구현체가 명리 계산을 새로 하지 않는다.
 *
 * 3. generationAllowed=false인 Prompt는
 *    이 인터페이스까지 전달되면 안 된다.
 *
 * 4. 반환값은 최종 사용자에게 보여줄
 *    본문 텍스트만 반환한다.
 *
 * 5. 제목이나 내부 JSON을 반환하도록 강제하지 않는다.
 */
public interface LifeInterpretationTextGenerator {

    /**
     * 하나의 카테고리 Prompt를 실제 AI 모델로 생성한다.
     *
     * @param prompt
     * Java Rule Engine과 Narrative 계층에서
     * 이미 작성이 허용된 최종 Prompt.
     *
     * @return
     * 해당 카테고리의 최종 한국어 장문 본문.
     */
    String generate(
            LifeInterpretationPrompt prompt
    );
}