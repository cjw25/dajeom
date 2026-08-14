package com.dajeom.adapter;

import com.dajeom.model.ConsultationInterpretationPromptContext;
import com.dajeom.model.ConsultationStoryGeneration;

import java.util.Optional;

/**
 * 정밀화 20차 Prompt Context를
 * 외부 LLM의 사용자용 장문 스토리로 변환하는 Adapter 경계.
 *
 * 새로운 사주 계산을 수행하지 않는다.
 */
public interface ConsultationStoryGenerator {

    Optional<ConsultationStoryGeneration> generate(
            ConsultationInterpretationPromptContext promptContext
    );
}