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

        List<String> basisLabels,

        int evidenceCount

) {

    public ConsultationSectionResponse {

        basisLabels =
                List.copyOf(
                        basisLabels
                );
    }
}