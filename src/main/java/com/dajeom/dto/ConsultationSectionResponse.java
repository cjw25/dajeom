package com.dajeom.dto;

import com.dajeom.type.AnalysisConfidence;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.InterpretationIntensity;
import com.dajeom.type.InterpretationPolarity;

import java.util.List;

/**
 * 하나의 상담 분야 API 출력값.
 *
 * 예:
 *
 * - 재물운
 * - 직업·사업운
 * - 현재와 올해의 흐름
 *
 * =================================================
 *
 * narrationText
 *
 * 기존 deterministic / legacy 상담 문장.
 *
 * =================================================
 *
 * storyText
 *
 * 정밀화 20차
 * ConsultationInterpretationPromptContext를
 * 외부 LLM이 문장화한 장문 스토리.
 *
 * 새로운 사주 계산값이 아니다.
 *
 * 외부 LLM이 비활성화되거나 실패하면
 * Service에서 안전한 fallback을 결정한다.
 */
public record ConsultationSectionResponse(

        int order,

        ConsultationCategory category,

        String title,

        InterpretationPolarity polarity,

        InterpretationIntensity intensity,

        AnalysisConfidence confidence,

        String openingMessage,

        String summaryMessage,

        String guidanceMessage,

        String narrationText,

        String storyText,

        List<String> basisLabels,

        int evidenceCount

) {

    public ConsultationSectionResponse {

        storyText =
                storyText == null
                        ? ""
                        : storyText.trim();

        basisLabels =
                basisLabels == null
                        ? List.of()
                        : List.copyOf(
                        basisLabels
                );
    }
}